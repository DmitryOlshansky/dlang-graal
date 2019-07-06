package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.aliasthis.*;
import static org.dlang.dmd.apply.*;
import static org.dlang.dmd.arrayop.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.ast_node.*;
import static org.dlang.dmd.canthrow.*;
import static org.dlang.dmd.complex.*;
import static org.dlang.dmd.constfold.*;
import static org.dlang.dmd.ctfeexpr.*;
import static org.dlang.dmd.ctorflow.*;
import static org.dlang.dmd.dcast.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.delegatize.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dinterpret.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.escape.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.inline.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.nspace.*;
import static org.dlang.dmd.objc.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.optimize.*;
import static org.dlang.dmd.safe.*;
import static org.dlang.dmd.sideeffect.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.utf.*;
import static org.dlang.dmd.visitor.*;

public class expression {
    static IntegerExp literaltheConstant;
    static IntegerExp literaltheConstant;
    static IntegerExp literaltheConstant;
    private static class DtorVisitor extends StoppableVisitor
    {
        private Scope sc;
        private CondExp ce;
        private VarDeclaration vcond;
        private boolean isThen;
        public  DtorVisitor(Scope sc, CondExp ce) {
            super();
            this.sc = sc;
            this.ce = ce;
        }

        public  void visit(Expression e) {
        }

        public  void visit(DeclarationExp e) {
            VarDeclaration v = e.declaration.isVarDeclaration();
            if ((v != null && !(v.isDataseg())))
            {
                if (v._init != null)
                {
                    {
                        ExpInitializer ei = v._init.isExpInitializer();
                        if (ei != null)
                            ei.exp.accept(this);
                    }
                }
                if (v.needsScopeDtor())
                {
                    if (!(this.vcond != null))
                    {
                        this.vcond = copyToTemp(8796093022208L, new BytePtr("__cond"), this.ce.econd);
                        dsymbolSemantic(this.vcond, this.sc);
                        Expression de = new DeclarationExp(this.ce.econd.loc, this.vcond);
                        de = expressionSemantic(de, this.sc);
                        Expression ve = new VarExp(this.ce.econd.loc, this.vcond, true);
                        this.ce.econd = Expression.combine(de, ve);
                    }
                    Expression ve = new VarExp(this.vcond.loc, this.vcond, true);
                    if (this.isThen)
                        v.edtor = new LogicalExp(v.edtor.loc, TOK.andAnd, ve, v.edtor);
                    else
                        v.edtor = new LogicalExp(v.edtor.loc, TOK.orOr, ve, v.edtor);
                    v.edtor = expressionSemantic(v.edtor, this.sc);
                }
            }
        }


        public DtorVisitor() {}
    }

    static boolean LOGSEMANTIC = false;
    // from template emplaceExp!(AddrExpLocExpression)
    public static void emplaceExpAddrExpLocExpression(Object p, Loc _param_1, Expression _param_2) {
        AddrExp tmp = new AddrExp(_param_1, _param_2);
        memcpy((BytePtr)p, (tmp), 32);
    }


    // from template emplaceExp!(AddrExpLocExpression)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "boolean isDittoBytePtr", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression

    // from template emplaceExp!(AddrExpLocExpressionType)
    public static void emplaceExpAddrExpLocExpressionType(Object p, Loc _param_1, Expression _param_2, Type _param_3) {
        AddrExp tmp = new AddrExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 32);
    }


    // from template emplaceExp!(AddrExpLocIndexExpType)
    public static void emplaceExpAddrExpLocIndexExpType(Object p, Loc _param_1, IndexExp _param_2, Type _param_3) {
        AddrExp tmp = new AddrExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 32);
    }


    // from template emplaceExp!(AddrExpLocSliceExp)
    public static void emplaceExpAddrExpLocSliceExp(Object p, Loc _param_1, SliceExp _param_2) {
        AddrExp tmp = new AddrExp(_param_1, _param_2);
        memcpy((BytePtr)p, (tmp), 32);
    }


    // from template emplaceExp!(AddrExpLocSliceExpType)
    public static void emplaceExpAddrExpLocSliceExpType(Object p, Loc _param_1, SliceExp _param_2, Type _param_3) {
        AddrExp tmp = new AddrExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 32);
    }


    // from template emplaceExp!(AddrExpLocVarExpType)
    public static void emplaceExpAddrExpLocVarExpType(Object p, Loc _param_1, VarExp _param_2, Type _param_3) {
        AddrExp tmp = new AddrExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 32);
    }


    // from template emplaceExp!(ArrayLiteralExpLocObjectDArray<Expression>)
    public static void emplaceExpArrayLiteralExpLocObjectDArray<Expression>(Object p, Loc _param_1, Object _param_2, DArray<Expression> _param_3) {
        ArrayLiteralExp tmp = new ArrayLiteralExp(_param_1, (Type)_param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 33);
    }


    // from template emplaceExp!(ArrayLiteralExpLocTypeArrayDArray<Expression>)
    public static void emplaceExpArrayLiteralExpLocTypeArrayDArray<Expression>(Object p, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3) {
        ArrayLiteralExp tmp = new ArrayLiteralExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 33);
    }


    // from template emplaceExp!(ArrayLiteralExpLocTypeDArray<Expression>)
    public static void emplaceExpArrayLiteralExpLocTypeDArray<Expression>(Object p, Loc _param_1, Type _param_2, DArray<Expression> _param_3) {
        ArrayLiteralExp tmp = new ArrayLiteralExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 33);
    }


    // from template emplaceExp!(ArrayLiteralExpLocTypeDArray<Expression>)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>

    // from template emplaceExp!(ArrayLiteralExpLocTypeExpression)
    public static void emplaceExpArrayLiteralExpLocTypeExpression(Object p, Loc _param_1, Type _param_2, Expression _param_3) {
        ArrayLiteralExp tmp = new ArrayLiteralExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 33);
    }


    // from template emplaceExp!(ArrayLiteralExpLocTypeExpressionDArray<Expression>)
    public static void emplaceExpArrayLiteralExpLocTypeExpressionDArray<Expression>(Object p, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4) {
        ArrayLiteralExp tmp = new ArrayLiteralExp(_param_1, _param_2, _param_3, _param_4);
        memcpy((BytePtr)p, (tmp), 33);
    }


    // from template emplaceExp!(ArrayLiteralExpLocTypeSArrayDArray<Expression>)
    public static void emplaceExpArrayLiteralExpLocTypeSArrayDArray<Expression>(Object p, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3) {
        ArrayLiteralExp tmp = new ArrayLiteralExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 33);
    }


    // from template emplaceExp!(AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>)
    public static void emplaceExpAssocArrayLiteralExpLocDArray<Expression>DArray<Expression>(Object p, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3) {
        AssocArrayLiteralExp tmp = new AssocArrayLiteralExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 33);
    }


    // from template emplaceExp!(CTFEExpByte)
    public static void emplaceExpCTFEExpByte(Object p, byte _param_1) {
        CTFEExp tmp = new CTFEExp(_param_1);
        memcpy((BytePtr)p, (tmp), 24);
    }


    // from template emplaceExp!(ClassReferenceExpLocStructLiteralExpType)
    public static void emplaceExpClassReferenceExpLocStructLiteralExpType(Object p, Loc _param_1, StructLiteralExp _param_2, Type _param_3) {
        ClassReferenceExp tmp = new ClassReferenceExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 28);
    }


    // from template emplaceExp!(ComplexExpLoccomplex_tType)
    public static void emplaceExpComplexExpLoccomplex_tType(Object p, Loc _param_1, complex_t _param_2, Type _param_3) {
        ComplexExp tmp = new ComplexExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 56);
    }


    // from template emplaceExp!(ComplexExpLoccomplex_tType)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType

    // from template emplaceExp!(DelegateExpLocExpressionFuncDeclarationBoolean)
    public static void emplaceExpDelegateExpLocExpressionFuncDeclarationBoolean(Object p, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4) {
        DelegateExp tmp = new DelegateExp(_param_1, _param_2, _param_3, _param_4, null);
        memcpy((BytePtr)p, (tmp), 44);
    }


    // from template emplaceExp!(DotVarExpLocExpressionDeclarationBoolean)
    public static void emplaceExpDotVarExpLocExpressionDeclarationBoolean(Object p, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4) {
        DotVarExp tmp = new DotVarExp(_param_1, _param_2, _param_3, _param_4);
        memcpy((BytePtr)p, (tmp), 37);
    }


    // from template emplaceExp!(DotVarExpLocExpressionFuncDeclarationBoolean)
    public static void emplaceExpDotVarExpLocExpressionFuncDeclarationBoolean(Object p, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4) {
        DotVarExp tmp = new DotVarExp(_param_1, _param_2, _param_3, _param_4);
        memcpy((BytePtr)p, (tmp), 37);
    }


    // from template emplaceExp!(DotVarExpLocExpressionVarDeclaration)
    public static void emplaceExpDotVarExpLocExpressionVarDeclaration(Object p, Loc _param_1, Expression _param_2, VarDeclaration _param_3) {
        DotVarExp tmp = new DotVarExp(_param_1, _param_2, _param_3, true);
        memcpy((BytePtr)p, (tmp), 37);
    }


    // from template emplaceExp!(ErrorExp)
    public static void emplaceExpErrorExp(Object p) {
        ErrorExp tmp = new ErrorExp();
        memcpy((BytePtr)p, (tmp), 24);
    }


    // from template emplaceExp!(IndexExpLocExpressionExpression)
    public static void emplaceExpIndexExpLocExpressionExpression(Object p, Loc _param_1, Expression _param_2, Expression _param_3) {
        IndexExp tmp = new IndexExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 46);
    }


    // from template emplaceExp!(IndexExpLocExpressionIntegerExp)
    public static void emplaceExpIndexExpLocExpressionIntegerExp(Object p, Loc _param_1, Expression _param_2, IntegerExp _param_3) {
        IndexExp tmp = new IndexExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 46);
    }


    // from template emplaceExp!(IntegerExpInteger)
    public static void emplaceExpIntegerExpInteger(Object p, int _param_1) {
        IntegerExp tmp = new IntegerExp((long)_param_1);
        memcpy((BytePtr)p, (tmp), 32);
    }


    // from template emplaceExp!(IntegerExpLocBooleanType)
    public static void emplaceExpIntegerExpLocBooleanType(Object p, Loc _param_1, boolean _param_2, Type _param_3) {
        IntegerExp tmp = new IntegerExp(_param_1, (_param_2 ? 1 : 0), _param_3);
        memcpy((BytePtr)p, (tmp), 32);
    }


    // from template emplaceExp!(IntegerExpLocBooleanType)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType

    // from template emplaceExp!(IntegerExpLocIntegerType)
    public static void emplaceExpIntegerExpLocIntegerType(Object p, Loc _param_1, int _param_2, Type _param_3) {
        IntegerExp tmp = new IntegerExp(_param_1, (long)_param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 32);
    }


    // from template emplaceExp!(IntegerExpLocIntegerType)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType

    // from template emplaceExp!(IntegerExpLocIntegerType)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType

    // from template emplaceExp!(IntegerExpLocIntegerType)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType

    // from template emplaceExp!(IntegerExpLocLongType)
    public static void emplaceExpIntegerExpLocLongType(Object p, Loc _param_1, long _param_2, Type _param_3) {
        IntegerExp tmp = new IntegerExp(_param_1, (long)_param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 32);
    }


    // from template emplaceExp!(IntegerExpLocLongType)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType

    // from template emplaceExp!(IntegerExpLocLongType)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType

    // from template emplaceExp!(IntegerExpLocLongType)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType

    // from template emplaceExp!(NullExpLocType)
    public static void emplaceExpNullExpLocType(Object p, Loc _param_1, Type _param_2) {
        NullExp tmp = new NullExp(_param_1, _param_2);
        memcpy((BytePtr)p, (tmp), 25);
    }


    // from template emplaceExp!(NullExpLocType)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "void emplaceExpObject, Loc _param_1, Type _param_2NullExpLocType", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, Type _param_2NullExpLocType

    // from template emplaceExp!(RealExpLocDoubleType)
    public static void emplaceExpRealExpLocDoubleType(Object p, Loc _param_1, double _param_2, Type _param_3) {
        RealExp tmp = new RealExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 40);
    }


    // from template emplaceExp!(RealExpLocDoubleType)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "void emplaceExpObject, Loc _param_1, Type _param_2NullExpLocType", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "void emplaceExpObject, Loc _param_1, double _param_2, Type _param_3RealExpLocDoubleType", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, double _param_2, Type _param_3RealExpLocDoubleType

    // from template emplaceExp!(SliceExpLocExpressionExpressionExpression)
    public static void emplaceExpSliceExpLocExpressionExpressionExpression(Object p, Loc _param_1, Expression _param_2, Expression _param_3, Expression _param_4) {
        SliceExp tmp = new SliceExp(_param_1, _param_2, _param_3, _param_4);
        memcpy((BytePtr)p, (tmp), 47);
    }


    // from template emplaceExp!(SliceExpLocExpressionIntegerExpExpression)
    public static void emplaceExpSliceExpLocExpressionIntegerExpExpression(Object p, Loc _param_1, Expression _param_2, IntegerExp _param_3, Expression _param_4) {
        SliceExp tmp = new SliceExp(_param_1, _param_2, _param_3, _param_4);
        memcpy((BytePtr)p, (tmp), 47);
    }


    // from template emplaceExp!(SliceExpLocExpressionIntegerExpIntegerExp)
    public static void emplaceExpSliceExpLocExpressionIntegerExpIntegerExp(Object p, Loc _param_1, Expression _param_2, IntegerExp _param_3, IntegerExp _param_4) {
        SliceExp tmp = new SliceExp(_param_1, _param_2, _param_3, _param_4);
        memcpy((BytePtr)p, (tmp), 47);
    }


    // from template emplaceExp!(StringExpLocBytePtr)
    public static void emplaceExpStringExpLocBytePtr(Object p, Loc _param_1, BytePtr _param_2) {
        StringExp tmp = new StringExp(_param_1, _param_2);
        memcpy((BytePtr)p, (tmp), 36);
    }


    // from template emplaceExp!(StringExpLocBytePtrInteger)
    public static void emplaceExpStringExpLocBytePtrInteger(Object p, Loc _param_1, BytePtr _param_2, int _param_3) {
        StringExp tmp = new StringExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 36);
    }


    // from template emplaceExp!(StringExpLocBytePtrInteger)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "void emplaceExpObject, Loc _param_1, Type _param_2NullExpLocType", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void emplaceExpObject, Loc _param_1, BytePtr _param_2StringExpLocBytePtr", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3, Expression _param_4SliceExpLocExpressionIntegerExpExpression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3, IntegerExp _param_4SliceExpLocExpressionIntegerExpIntegerExp", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "void emplaceExpObject, Loc _param_1, double _param_2, Type _param_3RealExpLocDoubleType", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "void emplaceExpObject, Loc _param_1, BytePtr _param_2, int _param_3StringExpLocBytePtrInteger", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3, Expression _param_4SliceExpLocExpressionExpressionExpression", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, BytePtr _param_2, int _param_3StringExpLocBytePtrInteger

    // from template emplaceExp!(StringExpLocObjectInteger)
    public static void emplaceExpStringExpLocObjectInteger(Object p, Loc _param_1, Object _param_2, int _param_3) {
        StringExp tmp = new StringExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 36);
    }


    // from template emplaceExp!(StringExpLocObjectInteger)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "void emplaceExpObject, Loc _param_1, Type _param_2NullExpLocType", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void emplaceExpObject, Loc _param_1, BytePtr _param_2StringExpLocBytePtr", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Object _param_2, int _param_3StringExpLocObjectInteger", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3, Expression _param_4SliceExpLocExpressionIntegerExpExpression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3, IntegerExp _param_4SliceExpLocExpressionIntegerExpIntegerExp", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "void emplaceExpObject, Loc _param_1, double _param_2, Type _param_3RealExpLocDoubleType", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "void emplaceExpObject, Loc _param_1, BytePtr _param_2, int _param_3StringExpLocBytePtrInteger", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3, Expression _param_4SliceExpLocExpressionExpressionExpression", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, Object _param_2, int _param_3StringExpLocObjectInteger

    // from template emplaceExp!(StringExpLocObjectInteger)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "void emplaceExpObject, Loc _param_1, Type _param_2NullExpLocType", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void emplaceExpObject, Loc _param_1, BytePtr _param_2StringExpLocBytePtr", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Object _param_2, int _param_3StringExpLocObjectInteger", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3, Expression _param_4SliceExpLocExpressionIntegerExpExpression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3, IntegerExp _param_4SliceExpLocExpressionIntegerExpIntegerExp", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "void emplaceExpObject, Loc _param_1, double _param_2, Type _param_3RealExpLocDoubleType", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "void emplaceExpObject, Loc _param_1, BytePtr _param_2, int _param_3StringExpLocBytePtrInteger", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3, Expression _param_4SliceExpLocExpressionExpressionExpression", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, Object _param_2, int _param_3StringExpLocObjectInteger

    // from template emplaceExp!(StringExpLocObjectIntegerByte)
    public static void emplaceExpStringExpLocObjectIntegerByte(Object p, Loc _param_1, Object _param_2, int _param_3, byte _param_4) {
        StringExp tmp = new StringExp(_param_1, _param_2, _param_3, _param_4);
        memcpy((BytePtr)p, (tmp), 36);
    }


    // from template emplaceExp!(StructLiteralExpLocStructDeclarationDArray<Expression>)
    public static void emplaceExpStructLiteralExpLocStructDeclarationDArray<Expression>(Object p, Loc _param_1, StructDeclaration _param_2, DArray<Expression> _param_3) {
        StructLiteralExp tmp = new StructLiteralExp(_param_1, _param_2, _param_3, null);
        memcpy((BytePtr)p, (tmp), 54);
    }


    // from template emplaceExp!(StructLiteralExpLocStructDeclarationDArray<Expression>)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "void emplaceExpObject, Loc _param_1, Type _param_2NullExpLocType", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void emplaceExpObject, Loc _param_1, BytePtr _param_2StringExpLocBytePtr", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Object _param_2, int _param_3StringExpLocObjectInteger", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3, Expression _param_4SliceExpLocExpressionIntegerExpExpression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "void emplaceExpObject, Loc _param_1, Object _param_2, int _param_3, byte _param_4StringExpLocObjectIntegerByte", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3, IntegerExp _param_4SliceExpLocExpressionIntegerExpIntegerExp", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "void emplaceExpObject, Loc _param_1, double _param_2, Type _param_3RealExpLocDoubleType", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "void emplaceExpObject, Loc _param_1, BytePtr _param_2, int _param_3StringExpLocBytePtrInteger", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3, Expression _param_4SliceExpLocExpressionExpressionExpression", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void emplaceExpObject, Loc _param_1, StructDeclaration _param_2, DArray<Expression> _param_3StructLiteralExpLocStructDeclarationDArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, StructDeclaration _param_2, DArray<Expression> _param_3StructLiteralExpLocStructDeclarationDArray<Expression>

    // from template emplaceExp!(StructLiteralExpLocStructDeclarationDArray<Expression>Type)
    public static void emplaceExpStructLiteralExpLocStructDeclarationDArray<Expression>Type(Object p, Loc _param_1, StructDeclaration _param_2, DArray<Expression> _param_3, Type _param_4) {
        StructLiteralExp tmp = new StructLiteralExp(_param_1, _param_2, _param_3, _param_4);
        memcpy((BytePtr)p, (tmp), 54);
    }


    // from template emplaceExp!(SymOffExpLocDeclarationInteger)
    public static void emplaceExpSymOffExpLocDeclarationInteger(Object p, Loc _param_1, Declaration _param_2, int _param_3) {
        SymOffExp tmp = new SymOffExp(_param_1, _param_2, (long)_param_3, true);
        memcpy((BytePtr)p, (tmp), 44);
    }


    // from template emplaceExp!(SymOffExpLocDeclarationLong)
    public static void emplaceExpSymOffExpLocDeclarationLong(Object p, Loc _param_1, Declaration _param_2, long _param_3) {
        SymOffExp tmp = new SymOffExp(_param_1, _param_2, _param_3, true);
        memcpy((BytePtr)p, (tmp), 44);
    }


    // from template emplaceExp!(SymOffExpLocDeclarationLong)
    // removed duplicate function, [["TypeAArray toBuiltinAATypeType", "Expression eval_floorLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, Type _param_3AddrExpLocExpressionType", "UnionExp ArrayLengthType, Expression", "Expression resolveSliceExpression, UnionExp", "Expression getValueVarDeclaration", "int arrayObjectHashDArray<RootObject>", "void showCtfeExprExpression, int", "void removeBlankLineMacroOutBuffer, IntRef, IntRef", "void MODtoDecoBufferOutBuffer, byte", "Expression eval_copysignLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, Loc _param_1, Expression _param_2, VarDeclaration _param_3DotVarExpLocExpressionVarDeclaration", "int deduceTypeRootObject, Scope, Type, DArray<TemplateParameter>, DArray<RootObject>, IntPtr, int, boolean", "UnionExp DivLoc, Type, Expression, Expression", "boolean pointToSameMemoryBlockExpression, Expression", "ByteSlice initializerMsgtable", "Expression interpret_lengthUnionExp, InterState, Expression", "void emplaceExpObject, Loc _param_1, boolean _param_2, Type _param_3IntegerExpLocBooleanType", "boolean arrayObjectMatchDArray<RootObject>, DArray<RootObject>", "boolean isTrueBoolExpression", "boolean reliesOnTemplateParametersExpression, Slice<TemplateParameter>", "boolean utf_isValidDcharint", "void fatal", "int isConstExpression", "int utf_codeLengthWcharint", "boolean isBinArrayOpbyte", "Expression interpretStatement, InterState", "void emitProtectionOutBuffer, Prot", "int ctfeIdentityLoc, byte, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3IndexExpLocExpressionIntegerExp", "void emplaceExpObject, Loc _param_1, Declaration _param_2, int _param_3SymOffExpLocDeclarationInteger", "CtorDeclaration generateCopyCtorDeclarationStructDeclaration, long, long", "UnionExp PtrType, Expression", "boolean isIdTailBytePtr", "UnionExp ShrLoc, Type, Expression, Expression", "TemplateDeclaration getEponymousParentDsymbol", "boolean symbolIsVisibledmodule.Module, Dsymbol", "BytePtr getMessageDeprecatedDeclaration", "boolean issinglecharbyte", "void checkPossibleAddCatErrorAddAssignExpAddAssignExpCatAssignExp", "int getCodeIndentBytePtr", "void emplaceExpObject, Loc _param_1, StructDeclaration _param_2, DArray<Expression> _param_3, Type _param_4StructLiteralExpLocStructDeclarationDArray<Expression>Type", "ByteSlice lexBytePtr, ByteSlice", "Expression scrubReturnValueLoc, Expression", "void highlightCode3Scope, OutBuffer, BytePtr, BytePtr", "boolean modifyFieldVarLoc, Scope, VarDeclaration, Expression", "void deprecationSupplementalLoc, BytePtr", "UnionExp Identitybyte, Loc, Type, Expression, Expression", "boolean checkSymbolAccessScope, Dsymbol", "UnionExp PowLoc, Type, Expression, Expression", "int findFieldIndexByNameStructDeclaration, VarDeclaration", "int isattyint", "int ctfeEqualLoc, byte, Expression, Expression", "void sliceAssignStringFromArrayLiteralStringExp, ArrayLiteralExp, int", "boolean symbolIsVisibleDsymbol, Dsymbol", "void tyToDecoBufferOutBuffer, int", "boolean isNonAssignmentArrayOpExpression", "void colorHighlightCodeOutBuffer", "UnionExp BoolType, Expression", "void gendocfiledmodule.Module", "boolean checkArrayLiteralEscapeScope, ArrayLiteralExp, boolean", "boolean isoctalbyte", "Expression eval_fminLoc, FuncDeclaration, DArray<Expression>", "boolean checkAssocArrayLiteralEscapeScope, AssocArrayLiteralExp, boolean", "boolean checkAssignEscapeScope, Expression, boolean", "Expression eval_bsrLoc, FuncDeclaration, DArray<Expression>", "void semanticTypeInfoScope, Type", "void highlightCodeScope, Dsymbol, OutBuffer, int", "boolean allowsContractWithoutBodyFuncDeclaration", "UnionExp AndLoc, Type, Expression, Expression", "boolean _isZeroInitExpression", "boolean isCtfeReferenceValidExpression", "int replaceMarkdownEmphasisOutBuffer, Loc, Ref<Slice<MarkdownDelimiter>>, int", "boolean checkThrowEscapeScope, Expression, boolean", "Expression eval_popcntLoc, FuncDeclaration, DArray<Expression>", "void setValueWithoutCheckingVarDeclaration, Expression", "void mangleToBufferType, OutBuffer", "int isBuiltinFuncDeclaration", "Expression paintFloatIntUnionExp, Expression, Type", "void sliceAssignStringFromStringStringExp, StringExp, int", "void highlightCodeScope, DArray<Dsymbol>, OutBuffer, int", "void escapeByValueExpression, EscapeByResults", "void messageBytePtr", "void lambdaSetParentExpression, FuncDeclaration", "UnionExp pointerArithmeticLoc, byte, Type, Expression, Expression", "void emitProtectionOutBuffer, Declaration", "int templateIdentifierLookupIdentifier, DArray<TemplateParameter>", "Expression expTypeType, Expression", "void fix16997Scope, UnaExp", "Type isTypeRootObject", "Expression eval_builtinLoc, FuncDeclaration, DArray<Expression>", "BytePtr utf_decodeWcharCharPtr, int, IntRef, IntRef", "UnionExp copyLiteralExpression", "void emitCommentDsymbol, OutBuffer, Scope", "UnionExp paintTypeOntoLiteralCopyType, Expression", "void vdeprecationSupplementalLoc, BytePtr, Slice<Object>", "FuncDeclaration funcptrOfExpression", "void mangleToBufferExpression, OutBuffer", "void emplaceExpObject, Loc _param_1, Type _param_2NullExpLocType", "boolean isDittoBytePtr", "void emplaceExpObject, Loc _param_1, IndexExp _param_2, Type _param_3AddrExpLocIndexExpType", "boolean arrayTypeCompatibleLoc, Type, Type", "Expression eval_expm1Loc, FuncDeclaration, DArray<Expression>", "int implicitConvToExpression, Type", "BytePtr mangleExactFuncDeclaration", "void writeHighlightsConsole, OutBuffer", "boolean numCmpbyte, long, longLong", "void toDocBufferDsymbol, OutBuffer, Scope", "void emplaceExpObject, Loc _param_1, SliceExp _param_2, Type _param_3AddrExpLocSliceExpType", "void emplaceExpObject, byte _param_1CTFEExpByte", "boolean isValidManglingint", "boolean checkAccessAggregateDeclaration, Loc, Scope, Dsymbol", "boolean isFloatIntPaintType, Type", "void vmessageLoc, BytePtr, Slice<Object>", "int getAlignmentAlignDeclaration, Scope", "boolean numCmpbyte, int, intInteger", "boolean isUnaArrayOpbyte", "void escapeDdocStringOutBuffer, int", "void messageLoc, BytePtr", "UnionExp ModLoc, Type, Expression, Expression", "boolean checkAccessLoc, Scope, dmodule.Package", "int skippastURLOutBuffer, int", "void emplaceExpObject, Loc _param_1, complex_t _param_2, Type _param_3ComplexExpLoccomplex_tType", "Expression eval_log2Loc, FuncDeclaration, DArray<Expression>", "void warningSupplementalLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3IndexExpLocExpressionExpression", "Type toStaticArrayTypeSliceExp", "ErrorExp arrayOpInvalidErrorExpression", "Scope skipNonQualScopesScope", "boolean arrayTypeCompatibleWithoutCastingType, Type", "Parameter isParameterRootObject", "void emitProtectionOutBuffer, Import", "long resolveArrayLengthExpression", "FuncDeclaration buildInvAggregateDeclaration, Scope", "void emplaceExpObject, ErrorExp", "boolean definitelyValueParameterExpression", "Dsymbol mostVisibleOverloadDsymbol, dmodule.Module", "DtorDeclaration buildDtorAggregateDeclaration, Scope", "UnionExp NegType, Expression", "UnionExp pointerDifferenceLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, long _param_2, Type _param_3IntegerExpLocLongType", "Function3<Loc,FuncDeclaration,DArray<Expression>,Expression> builtin_lookupBytePtr", "Expression scaleFactorBinExp, Scope", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3ArrayLiteralExpLocTypeExpression", "int utf_codeLengthint, int", "UnionExp SliceType, Expression, Expression, Expression", "void functionResolveMatchAccumulator, Dsymbol, Loc, Scope, DArray<RootObject>, Type, DArray<Expression>, Ptr<BytePtr>", "int intSignedCmpbyte, long, long", "BytePtr skipwhitespaceBytePtr", "boolean isUniAlphaint", "void errorBytePtr, int, int, BytePtr", "FuncDeclaration buildXopCmpStructDeclaration, Scope", "boolean needOpEqualsStructDeclaration", "UnionExp ShlLoc, Type, Expression, Expression", "Dsymbol getDsymbolRootObject", "boolean mergeFieldInitRef<Integer>, int", "Type rawTypeMergeType, Type", "void assignInPlaceExpression, Expression", "int sliceCmpStringWithStringStringExp, StringExp, int, int, int", "boolean needToCopyLiteralExpression", "boolean isCtfeComparableExpression", "int endTableOutBuffer, int, Ref<IntSlice>", "void mangleToBufferTemplateInstance, OutBuffer", "boolean reliesOnTemplateParametersType, Slice<TemplateParameter>", "void emplaceExpObject, Loc _param_1, Object _param_2, DArray<Expression> _param_3ArrayLiteralExpLocObjectDArray<Expression>", "ByteSlice toLowercaseByteSlice", "Expression interpretFunctionUnionExp, FuncDeclaration, InterState, DArray<Expression>, Expression", "Expression tryAliasThisCastExpression, Scope, Type, Type, Type", "int templateParameterLookupType, DArray<TemplateParameter>", "boolean replaceMarkdownThematicBreakOutBuffer, IntRef, int, Loc", "boolean c_isalnumint", "FuncDeclaration buildOpAssignStructDeclaration, Scope", "void ObjectNotFoundIdentifier", "boolean isArrayOpValidExpression", "Expression paintTypeOntoLiteralType, Expression", "boolean needToHashStructDeclaration", "int skiptoidentOutBuffer, int", "Expression arrayOpBinAssignExp, Scope", "boolean checkNewEscapeScope, Expression, boolean", "UnionExp XorLoc, Type, Expression, Expression", "boolean isCVariadicArgByteSlice", "Expression eval_ceilLoc, FuncDeclaration, DArray<Expression>", "int parseModulePatternDepthBytePtr", "boolean hasPackageAccessdmodule.Module, Dsymbol", "int ctfeCmpArraysLoc, Expression, Expression, long", "Expression ctfeInterpretExpression", "boolean isIndentWSBytePtr", "FuncDeclaration search_toStringStructDeclaration", "Expression eval_sinLoc, FuncDeclaration, DArray<Expression>", "UnionExp Equalbyte, Loc, Type, Expression, Expression", "void processFileByteSlice, ByteSlice, BytePtr_lispy", "Expression eval_isnanLoc, FuncDeclaration, DArray<Expression>", "boolean needOpAssignStructDeclaration", "int intUnsignedCmpbyte, long, long", "void parseModulePatternBytePtr, MatcherNode, int", "FuncDeclaration hasIdentityOpAssignAggregateDeclaration, Scope", "Expression integralPromotionsExpression, Scope", "boolean c_isxdigitint", "boolean isKeywordBytePtr, int", "void semantic3OnDependenciesModule", "void emplaceExpObject, Loc _param_1, VarExp _param_2, Type _param_3AddrExpLocVarExpType", "FuncDeclaration buildXtoHashStructDeclaration, Scope", "void highlightCode2Scope, DArray<Dsymbol>, OutBuffer, int", "UnionExp CatType, Expression, Expression", "Expression eval_isfiniteLoc, FuncDeclaration, DArray<Expression>", "int comparePointersbyte, Expression, long, Expression, long", "boolean isAssocArrayType", "void mangleToBufferDsymbol, OutBuffer", "Expression assignAssocArrayElementLoc, AssocArrayLiteralExp, Expression, Expression", "void emplaceExpObject, Loc _param_1, BytePtr _param_2StringExpLocBytePtr", "void setValueVarDeclaration, Expression", "BytePtr stripLeadingNewlinesBytePtr", "boolean isReservedNameByteSlice", "Expression interpret_dupUnionExp, InterState, Expression", "Expression eval_powLoc, FuncDeclaration, DArray<Expression>", "boolean typeMergeScope, byte, Ptr<Type>, Ptr<Expression>, Ptr<Expression>", "void templateInstanceSemanticTemplateInstance, Scope, DArray<Expression>", "void sliceAssignArrayLiteralFromStringArrayLiteralExp, StringExp, int", "TemplateParameter isTemplateParameterDArray<Dsymbol>, BytePtr, int", "UnionExp changeArrayLiteralLengthLoc, TypeArray, Expression, int, int", "boolean hasPackageAccessScope, Dsymbol", "boolean exceptionOrCantInterpretExpression", "boolean isErrorRootObject", "void emitMemberCommentsScopeDsymbol, OutBuffer, Scope", "void deprecationLoc, BytePtr", "void emplaceExpObject, Loc _param_1, Object _param_2, int _param_3StringExpLocObjectInteger", "Expression eval_tanLoc, FuncDeclaration, DArray<Expression>", "Expression eval_truncLoc, FuncDeclaration, DArray<Expression>", "double creallcomplex_t", "void expandTemplateMixinCommentsTemplateMixin, OutBuffer, Scope", "Expression eval_sqrtLoc, FuncDeclaration, DArray<Expression>", "boolean lambdaCheckForNestedRefExpression, Scope", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DelegateExpLocExpressionFuncDeclarationBoolean", "void escapeByRefExpression, EscapeByResults", "void cantExpUnionExp", "long getStorageClassPrefixAttributesASTBaseASTBase", "int specificCmpbyte, int", "boolean isDigitSecondbyte", "int utfStrideBytePtr", "void builtin_init", "void errorSupplementalLoc, BytePtr", "Expression evaluateDtorInterState, Expression", "void checkPossibleAddCatErrorAddExpAddExpCatExp", "Parameter isFunctionParameterDsymbol, BytePtr, int", "boolean checkReturnEscapeScope, Expression, boolean", "boolean reliesOnTidentType, DArray<TemplateParameter>, int", "Expression getValueRef<Dsymbol>", "BytePtr linkToCharsint", "Expression getExpressionRootObject", "int blockExitStatement, FuncDeclaration, boolean", "UnionExp Cmpbyte, Loc, Type, Expression, Expression", "UnionExp OrLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, SliceExp _param_2AddrExpLocSliceExp", "int sliceCmpStringWithArrayStringExp, ArrayLiteralExp, int, int, int", "void utf_encodeWcharCharPtr, int", "boolean findConditionDArray<Identifier>, Identifier", "Expression getAggregateFromPointerExpression, Ptr<Long>", "boolean isArrayExpression", "UnionExp CastLoc, Type, Type, Expression", "void utf_encodeint, Object, int", "ByteSlice replaceCharByteSlice, byte, ByteSlice", "BytePtr utf_decodeCharBytePtr, int, IntRef, IntRef", "void vwarningLoc, BytePtr, Slice<Object>", "boolean isPointerType", "int utf_codeLengthCharint", "Expression typeCombineBinExp, Scope", "Expression eval_roundLoc, FuncDeclaration, DArray<Expression>", "UnionExp UshrLoc, Type, Expression, Expression", "Expression eval_ldexpLoc, FuncDeclaration, DArray<Expression>", "Expression interpret_keysUnionExp, InterState, Expression, Type", "void emplaceExpObject, Loc _param_1, Declaration _param_2, long _param_3SymOffExpLocDeclarationLong", "Expression toDelegateExpression, Type, Scope", "boolean isBinAssignArrayOpbyte", "FuncDeclaration buildPostBlitStructDeclaration, Scope", "int skipCharsOutBuffer, int, ByteSlice", "boolean ensureStaticLinkToDsymbol, Dsymbol", "Expression charPromotionsExpression, Scope", "boolean isCVariadicParameterDArray<Dsymbol>, ByteSlice", "boolean symbolIsVisibleScope, Dsymbol", "IntRange getIntRangeExpression", "void warningLoc, BytePtr", "void emplaceExpObject, Loc _param_1, TypeSArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeSArrayDArray<Expression>", "void colorSyntaxHighlightOutBuffer", "void endMarkdownHeadingOutBuffer, int, IntRef, Loc, IntRef", "int deduceTypeHelperType, Ptr<Type>, Type", "Expression interpretUnionExp, Expression, InterState, int", "UnionExp AddLoc, Type, Expression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3, Expression _param_4SliceExpLocExpressionIntegerExpExpression", "boolean isCtfeValueValidExpression", "UnionExp MinLoc, Type, Expression, Expression", "Expression eval_logLoc, FuncDeclaration, DArray<Expression>", "Expression eval_cosLoc, FuncDeclaration, DArray<Expression>", "int replaceTableRowOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, IntSlice, boolean", "DtorDeclaration buildExternDDtorAggregateDeclaration, Scope", "byte deduceWildHelperType, Ptr<Type>, Type", "Expression evaluatePostblitInterState, Expression", "void emplaceExpObject, Loc _param_1, Object _param_2, int _param_3, byte _param_4StringExpLocObjectIntegerByte", "void emplaceExpObject, Loc _param_1, Expression _param_2, IntegerExp _param_3, IntegerExp _param_4SliceExpLocExpressionIntegerExpIntegerExp", "Expression eval_bsfLoc, FuncDeclaration, DArray<Expression>", "ByteSlice getFilenameDArray<Identifier>, Identifier", "Expression paintTypeOntoLiteralUnionExp, Type, Expression", "void findAllOuterAccessedVariablesFuncDeclaration, DArray<VarDeclaration>", "Expression implicitCastToExpression, Scope, Type", "Expression interpretExpression, InterState, int", "Expression getValueExpression", "UnionExp NotType, Expression", "boolean isIdStartBytePtr", "ByteSlice lispyBytePtr, ByteSlice", "TypeFunction isTypeFunctionDsymbol", "void emplaceExpObject, Loc _param_1, Expression _param_2, Declaration _param_3, boolean _param_4DotVarExpLocExpressionDeclarationBoolean", "void printDepsConditionalScope, DVCondition, ByteSlice", "boolean Dsymbol_canThrowDsymbol, FuncDeclaration, boolean", "int detectAtxHeadingLevelOutBuffer, int", "Expression interpretUnionExp, Statement, InterState", "void aliasSemanticAliasDeclaration, Scope", "ByteSlice deinitializerMsgtable", "Expression eval_unimpLoc, FuncDeclaration, DArray<Expression>", "void utf_encodeCharBytePtr, int", "Expression interpret_aaApplyUnionExp, InterState, Expression, Expression", "boolean matchRootObject, RootObject", "boolean mergeCallSuperRef<Integer>, int", "Expression eval_exp2Loc, FuncDeclaration, DArray<Expression>", "boolean numCmpbyte, double, doubleDouble", "ByteSlice identifierMsgtable", "boolean ishexbyte", "void processFileByteSlice, ByteSlice, BytePtr_lex", "void vdeprecationLoc, BytePtr, Slice<Object>, BytePtr, BytePtr", "boolean hasValueVarDeclaration", "Type getTypeRootObject", "boolean includeImportedModuleCheckModuleComponentRange", "void emplaceExpObject, Loc _param_1, double _param_2, Type _param_3RealExpLocDoubleType", "Expression eval_yl2xp1Loc, FuncDeclaration, DArray<Expression>", "int skipPastIdentWithDotsOutBuffer, int", "boolean emitAnchorNameOutBuffer, Dsymbol, Scope, boolean", "Expression eval_isinfinityLoc, FuncDeclaration, DArray<Expression>", "ArrayLiteralExp createBlockDuplicatedArrayLiteralUnionExp, Loc, Type, Expression, int", "void createMatchNodes", "boolean checkParamArgumentEscapeScope, FuncDeclaration, Parameter, Expression, boolean", "boolean checkReturnEscapeRefScope, Expression, boolean", "FuncDeclaration hasIdentityOpEqualsAggregateDeclaration, Scope", "int mainSlice<ByteSlice>", "ByteSlice lookForSourceFileByteSlice", "void emplaceExpObject, Loc _param_1, Expression _param_2AddrExpLocExpression", "UnionExp ctfeCatLoc, Type, Expression, Expression", "Parameter isFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, StructLiteralExp _param_2, Type _param_3ClassReferenceExpLocStructLiteralExpType", "boolean isArrayOpOperandExpression", "boolean isZeroSecondbyte", "boolean canThrowExpression, FuncDeclaration, boolean", "Tuple isTupleRootObject", "void dsymbolSemanticDsymbol, Scope", "int endAllMarkdownQuotesOutBuffer, int, IntRef", "void emplaceExpObject, Loc _param_1, BytePtr _param_2, int _param_3StringExpLocBytePtrInteger", "Expression castToExpression, Scope, Type", "FuncDeclaration buildXopEqualsStructDeclaration, Scope", "UnionExp ComType, Expression", "void builtinDeinitialize", "UnionExp MulLoc, Type, Expression, Expression", "void verrorPrintLoc, int, BytePtr, BytePtr, Slice<Object>, BytePtr, BytePtr", "int ctfeCmpLoc, byte, Expression, Expression", "int foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Integer>", "void emplaceExpObject, Loc _param_1, Type _param_2, Expression _param_3, DArray<Expression> _param_4ArrayLiteralExpLocTypeExpressionDArray<Expression>", "TemplateParameter isTemplateParameterRootObject", "void errorLoc, BytePtr", "Expression evaluateIfBuiltinUnionExp, InterState, Loc, FuncDeclaration, DArray<Expression>, Expression", "Expression eval_yl2xLoc, FuncDeclaration, DArray<Expression>", "boolean isidcharbyte", "Expression ctfeIndexLoc, Type, Expression, long", "void highlightTextScope, DArray<Dsymbol>, Loc, OutBuffer, int", "DtorDeclaration buildWindowsCppDtorAggregateDeclaration, DtorDeclaration, Scope", "Expression ctfeInterpretForPragmaMsgExpression", "Expression arrayOpBinExp, Scope", "int setMangleOverrideDsymbol, ByteSlice", "void add_builtinBytePtr, Function3<Loc,FuncDeclaration,DArray<Expression>,Expression>", "int ctfeRawCmpLoc, Expression, Expression, boolean", "UnionExp voidInitLiteralType, VarDeclaration", "Expression eval_log10Loc, FuncDeclaration, DArray<Expression>", "Expression eval_expLoc, FuncDeclaration, DArray<Expression>", "Expression eval_fmaxLoc, FuncDeclaration, DArray<Expression>", "UnionExp IndexType, Expression, Expression", "void emitAnchorOutBuffer, Dsymbol, Scope, boolean", "void removeAnyAtxHeadingSuffixOutBuffer, int", "void inferReturnFuncDeclaration, VarDeclaration", "DArray<Expression> copyElementsExpression, Expression", "void emplaceExpObject, Loc _param_1, Expression _param_2, FuncDeclaration _param_3, boolean _param_4DotVarExpLocExpressionFuncDeclarationBoolean", "boolean isIdentifierDArray<Dsymbol>, BytePtr, int", "boolean buildCopyCtorStructDeclaration, Scope", "int endAllListsAndQuotesOutBuffer, IntRef, Ref<Slice<MarkdownList>>, IntRef, IntRef", "ByteSlice memdupByteSlice", "Expression findKeyInAALoc, AssocArrayLiteralExp, Expression", "boolean checkNonAssignmentArrayOpExpression, boolean", "long mergeFuncAttrslong, FuncDeclaration", "Expression interpret_valuesUnionExp, InterState, Expression, Type", "void emplaceExpObject, Loc _param_1, Expression _param_2, Expression _param_3, Expression _param_4SliceExpLocExpressionExpressionExpression", "RootObject objectSyntaxCopyRootObject", "int extractArgNByteSlice, Ref<ByteSlice>, int", "void vwarningSupplementalLoc, BytePtr, Slice<Object>", "BytePtr modToCharsint", "Expression scrubCacheValueExpression", "void eliminateMaybeScopesSlice<VarDeclaration>", "void emplaceExpObject, Loc _param_1, TypeArray _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeArrayDArray<Expression>", "Statement generateCopyCtorBodyStructDeclaration", "FuncDeclaration buildOpEqualsStructDeclaration, Scope", "int expressionHashExpression", "DArray<Expression> copyLiteralArrayDArray<Expression>, Expression", "void buildArrayOpScope, Expression, DArray<RootObject>, DArray<Expression>", "void emplaceExpObject, Loc _param_1, StructDeclaration _param_2, DArray<Expression> _param_3StructLiteralExpLocStructDeclarationDArray<Expression>", "void mangleToFuncSignatureOutBuffer, FuncDeclaration", "StringExp createBlockDuplicatedStringLiteralUnionExp, Loc, Type, int, int, byte", "boolean arrayObjectIsErrorDArray<RootObject>", "boolean isVoidArrayLiteralExpression, Type", "int endRowAndTableOutBuffer, int, int, Loc, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "void unSpeculativeScope, RootObject", "long getStorageClassPrefixAttributesASTCodegenASTCodegen", "boolean isSpecialEnumIdentIdentifier", "void foreachDsymbolDArray<Dsymbol>, Function1<Dsymbol,Void>", "Expression isExpressionRootObject", "int parseTableDelimiterRowOutBuffer, int, boolean, Ref<IntSlice>", "int skippastidentOutBuffer, int", "int realCmpbyte, double, double", "double cimaglcomplex_t", "void verrorLoc, BytePtr, Slice<Object>, BytePtr, BytePtr, BytePtr", "boolean checkFrameAccessLoc, Scope, AggregateDeclaration, int", "boolean isTypeInfo_ClassType", "void verrorSupplementalLoc, BytePtr, Slice<Object>", "boolean checkAccessLoc, Scope, Expression, Declaration", "ByteSlice generateSlice<Msgtable>, Function1<Msgtable,ByteSlice>", "Expression inferTypeExpression, Type, int", "void ctfeCompileFuncDeclaration", "Expression eval_fabsLoc, FuncDeclaration, DArray<Expression>", "void printCtfePerformanceStats", "void notMaybeScopeVarDeclaration", "boolean checkReturnEscapeImplScope, Expression, boolean, boolean", "Expression ctfeCastUnionExp, Loc, Type, Type, Expression", "void escapeStrayParenthesisLoc, OutBuffer, int, boolean", "void emplaceExpObject, Loc _param_1, int _param_2, Type _param_3IntegerExpLocIntegerType", "boolean walkPostorderExpression, StoppableVisitor", "boolean checkParamArgumentReturnScope, Expression, Expression, boolean", "Expression resolveAliasThisScope, Expression, boolean", "int getMarkdownIndentOutBuffer, int, int", "ByteSlice skipwhitespaceByteSlice", "void emplaceExpObject, Loc _param_1, DArray<Expression> _param_2, DArray<Expression> _param_3AssocArrayLiteralExpLocDArray<Expression>DArray<Expression>", "int startTableOutBuffer, int, int, Loc, boolean, Ref<Slice<MarkdownDelimiter>>, Ref<IntSlice>", "boolean isSafePointerCastType, Type", "Parameter isEponymousFunctionParameterDArray<Dsymbol>, BytePtr, int", "void emplaceExpObject, Loc _param_1, Type _param_2, DArray<Expression> _param_3ArrayLiteralExpLocTypeDArray<Expression>", "Expression eval_bswapLoc, FuncDeclaration, DArray<Expression>", "void emplaceExpObject, int _param_1IntegerExpInteger", "void halt", "Dsymbol isDsymbolRootObject", "boolean checkConstructorEscapeScope, CallExp, boolean", "boolean hasProtectedAccessScope, Dsymbol", "Dsymbol getEponymousMemberTemplateDeclaration", "Expression foreachApplyUtfUnionExp, InterState, Expression, Expression, boolean", "Expression eval_fmaLoc, FuncDeclaration, DArray<Expression>", "boolean writeMixinByteSlice, Loc"]] signature: void emplaceExpObject, Loc _param_1, Declaration _param_2, long _param_3SymOffExpLocDeclarationLong

    // from template emplaceExp!(TupleExpLocDArray<Expression>)
    public static void emplaceExpTupleExpLocDArray<Expression>(Object p, Loc _param_1, DArray<Expression> _param_2) {
        TupleExp tmp = new TupleExp(_param_1, _param_2);
        memcpy((BytePtr)p, (tmp), 32);
    }


    // from template emplaceExp!(TypeidExpLocType)
    public static void emplaceExpTypeidExpLocType(Object p, Loc _param_1, Type _param_2) {
        TypeidExp tmp = new TypeidExp(_param_1, _param_2);
        memcpy((BytePtr)p, (tmp), 28);
    }


    // from template emplaceExp!(VarExpLocDeclaration)
    public static void emplaceExpVarExpLocDeclaration(Object p, Loc _param_1, Declaration _param_2) {
        VarExp tmp = new VarExp(_param_1, _param_2, true);
        memcpy((BytePtr)p, (tmp), 36);
    }


    // from template emplaceExp!(VectorExpLocExpressionType)
    public static void emplaceExpVectorExpLocExpressionType(Object p, Loc _param_1, Expression _param_2, Type _param_3) {
        VectorExp tmp = new VectorExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 41);
    }


    // from template emplaceExp!(VectorExpLocExpressionTypeVector)
    public static void emplaceExpVectorExpLocExpressionTypeVector(Object p, Loc _param_1, Expression _param_2, TypeVector _param_3) {
        VectorExp tmp = new VectorExp(_param_1, _param_2, _param_3);
        memcpy((BytePtr)p, (tmp), 41);
    }


    // from template emplaceExp!(VoidInitExpVarDeclaration)
    public static void emplaceExpVoidInitExpVarDeclaration(Object p, VarDeclaration _param_1) {
        VoidInitExp tmp = new VoidInitExp(_param_1);
        memcpy((BytePtr)p, (tmp), 28);
    }


    // from template emplaceExp!(UnionExp)
    public static void emplaceExpUnionExp(UnionExp p, Expression e) {
        memcpy((BytePtr)(p), (e), ((e.size & 0xFF)));
    }



    public static class Modifiable 
    {
        public static final int no = 0;
        public static final int yes = 1;
        public static final int initialization = 2;
    }

    public static Expression firstComma(Expression e) {
        Expression ex = e;
        for (; (ex.op & 0xFF) == 99;) {
            ex = ((CommaExp)ex).e1;
        }
        return ex;
    }

    public static Expression lastComma(Expression e) {
        Expression ex = e;
        for (; (ex.op & 0xFF) == 99;) {
            ex = ((CommaExp)ex).e2;
        }
        return ex;
    }

    public static FuncDeclaration hasThis(Scope sc) {
        Dsymbol p = (sc).parent;
        for (; (p != null && p.isTemplateMixin() != null);) {
            p = p.parent;
        }
        FuncDeclaration fdthis = p != null ? p.isFuncDeclaration() : null;
        FuncDeclaration fd = fdthis;
        for (; (1) != 0;){
            if (!(fd != null))
            {
                return null;
            }
            if (((!(fd.isNested()) || fd.isThis() != null) || (fd.isThis2 && fd.isMember2() != null)))
                break;
            Dsymbol parent = fd.parent;
            for (; (1) != 0;){
                if (!(parent != null))
                    return null;
                TemplateInstance ti = parent.isTemplateInstance();
                if (ti != null)
                    parent = ti.parent;
                else
                    break;
            }
            fd = parent.isFuncDeclaration();
        }
        if ((!(fd.isThis() != null) && !((fd.isThis2 && fd.isMember2() != null))))
        {
            return null;
        }
        assert(fd.vthis != null);
        return fd;
    }

    public static boolean isNeedThisScope(Scope sc, Declaration d) {
        if ((sc).intypeof == 1)
            return false;
        AggregateDeclaration ad = d.isThis();
        if (!(ad != null))
            return false;
        {
            Dsymbol s = (sc).parent;
            for (; s != null;s = s.toParentLocal()){
                {
                    AggregateDeclaration ad2 = s.isAggregateDeclaration();
                    if (ad2 != null)
                    {
                        if (pequals(ad2, ad))
                            return false;
                        else if (ad2.isNested())
                            continue;
                        else
                            return true;
                    }
                }
                {
                    FuncDeclaration f = s.isFuncDeclaration();
                    if (f != null)
                    {
                        if (f.isMemberLocal() != null)
                            break;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isDotOpDispatch(Expression e) {
        {
            DotTemplateInstanceExp dtie = e.isDotTemplateInstanceExp();
            if (dtie != null)
                return pequals(dtie.ti.name, Id.opDispatch);
        }
        return false;
    }

    public static void expandTuples(DArray<Expression> exps) {
        if (exps == null)
            return ;
        {
            int i = 0;
            for (; i < (exps).length;i++){
                Expression arg = (exps).get(i);
                if (!(arg != null))
                    continue;
                {
                    TypeExp e = arg.isTypeExp();
                    if (e != null)
                    {
                        {
                            TypeTuple tt = e.type.toBasetype().isTypeTuple();
                            if (tt != null)
                            {
                                if ((tt.arguments == null || (tt.arguments).length == 0))
                                {
                                    (exps).remove(i);
                                    if (i == (exps).length)
                                        return ;
                                    i--;
                                    continue;
                                }
                            }
                        }
                    }
                }
                for (; (arg.op & 0xFF) == 126;){
                    TupleExp te = (TupleExp)arg;
                    (exps).remove(i);
                    (exps).insert(i, te.exps);
                    if (i == (exps).length)
                        return ;
                    exps.set(i, Expression.combine(te.e0, (exps).get(i)));
                    arg = (exps).get(i);
                }
            }
        }
    }

    public static TupleDeclaration isAliasThisTuple(Expression e) {
        if (!(e.type != null))
            return null;
        Type t = e.type.toBasetype();
        for (; true;){
            {
                Dsymbol s = t.toDsymbol(null);
                if (s != null)
                {
                    {
                        AggregateDeclaration ad = s.isAggregateDeclaration();
                        if (ad != null)
                        {
                            s = ad.aliasthis;
                            if ((s != null && s.isVarDeclaration() != null))
                            {
                                TupleDeclaration td = s.isVarDeclaration().toAlias().isTupleDeclaration();
                                if ((td != null && td.isexp))
                                    return td;
                            }
                            {
                                Type att = t.aliasthisOf();
                                if (att != null)
                                {
                                    t = att;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

    public static int expandAliasThisTuples(DArray<Expression> exps, int starti) {
        if ((exps == null || (exps).length == 0))
            return -1;
        {
            int u = starti;
            for (; u < (exps).length;u++){
                Expression exp = (exps).get(u);
                {
                    TupleDeclaration td = isAliasThisTuple(exp);
                    if (td != null)
                    {
                        (exps).remove(u);
                        {
                            Slice<RootObject> __r1319 = (td.objects).opSlice().copy();
                            int __key1318 = 0;
                            for (; __key1318 < __r1319.getLength();__key1318 += 1) {
                                RootObject o = __r1319.get(__key1318);
                                int i = __key1318;
                                Declaration d = isExpression(o).isDsymbolExp().s.isDeclaration();
                                DotVarExp e = new DotVarExp(exp.loc, exp, d, true);
                                assert(d.type != null);
                                e.type = d.type;
                                (exps).insert(u + i, e);
                            }
                        }
                        return u;
                    }
                }
            }
        }
        return -1;
    }

    public static TemplateDeclaration getFuncTemplateDecl(Dsymbol s) {
        FuncDeclaration f = s.isFuncDeclaration();
        if ((f != null && f.parent != null))
        {
            {
                TemplateInstance ti = f.parent.isTemplateInstance();
                if (ti != null)
                {
                    if ((!(ti.isTemplateMixin() != null) && ti.tempdecl != null))
                    {
                        TemplateDeclaration td = ti.tempdecl.isTemplateDeclaration();
                        if ((td.onemember != null && pequals(td.ident, f.ident)))
                        {
                            return td;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Expression valueNoDtor(Expression e) {
        Expression ex = lastComma(e);
        {
            CallExp ce = ex.isCallExp();
            if (ce != null)
            {
                {
                    DotVarExp dve = ce.e1.isDotVarExp();
                    if (dve != null)
                    {
                        if (dve.var.isCtorDeclaration() != null)
                        {
                            {
                                CommaExp comma = dve.e1.isCommaExp();
                                if (comma != null)
                                {
                                    {
                                        VarExp ve = comma.e2.isVarExp();
                                        if (ve != null)
                                        {
                                            VarDeclaration ctmp = ve.var.isVarDeclaration();
                                            if (ctmp != null)
                                            {
                                                ctmp.storage_class |= 16777216L;
                                                assert(!(ce.isLvalue()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                VarExp ve = ex.isVarExp();
                if (ve != null)
                {
                    VarDeclaration vtmp = ve.var.isVarDeclaration();
                    if ((vtmp != null && (vtmp.storage_class & 2199023255552L) != 0))
                    {
                        vtmp.storage_class |= 16777216L;
                    }
                }
            }
        }
        return e;
    }

    public static Expression callCpCtor(Scope sc, Expression e, Type destinationType) {
        {
            TypeStruct ts = e.type.baseElemOf().isTypeStruct();
            if (ts != null)
            {
                StructDeclaration sd = ts.sym;
                if ((sd.postblit != null || sd.hasCopyCtor))
                {
                    VarDeclaration tmp = copyToTemp(2199023255552L, new BytePtr("__copytmp"), e);
                    if ((sd.hasCopyCtor && destinationType != null))
                        tmp.type = destinationType;
                    tmp.storage_class |= 16777216L;
                    dsymbolSemantic(tmp, sc);
                    Expression de = new DeclarationExp(e.loc, tmp);
                    Expression ve = new VarExp(e.loc, tmp, true);
                    de.type = Type.tvoid;
                    ve.type = e.type;
                    return Expression.combine(de, ve);
                }
            }
        }
        return e;
    }

    public static Expression doCopyOrMove(Scope sc, Expression e, Type t) {
        {
            CondExp ce = e.isCondExp();
            if (ce != null)
            {
                ce.e1 = doCopyOrMove(sc, ce.e1, null);
                ce.e2 = doCopyOrMove(sc, ce.e2, null);
            }
            else
            {
                e = e.isLvalue() ? callCpCtor(sc, e, t) : valueNoDtor(e);
            }
        }
        return e;
    }

    public static class UnionExp
    {
        public  UnionExp(Expression e) {
            memcpy((BytePtr)(this), (e), ((e.size & 0xFF)));
        }

        public  Expression exp() {
            return (Expression)this.u;
        }

        public static class __AnonStruct__u
        {
            public ByteSlice exp = new ByteSlice(new byte[24]);
            public ByteSlice integerexp = new ByteSlice(new byte[32]);
            public ByteSlice errorexp = new ByteSlice(new byte[24]);
            public ByteSlice realexp = new ByteSlice(new byte[40]);
            public ByteSlice complexexp = new ByteSlice(new byte[56]);
            public ByteSlice symoffexp = new ByteSlice(new byte[44]);
            public ByteSlice stringexp = new ByteSlice(new byte[36]);
            public ByteSlice arrayliteralexp = new ByteSlice(new byte[33]);
            public ByteSlice assocarrayliteralexp = new ByteSlice(new byte[33]);
            public ByteSlice structliteralexp = new ByteSlice(new byte[54]);
            public ByteSlice nullexp = new ByteSlice(new byte[25]);
            public ByteSlice dotvarexp = new ByteSlice(new byte[37]);
            public ByteSlice addrexp = new ByteSlice(new byte[32]);
            public ByteSlice indexexp = new ByteSlice(new byte[46]);
            public ByteSlice sliceexp = new ByteSlice(new byte[47]);
            public ByteSlice vectorexp = new ByteSlice(new byte[41]);
            public __AnonStruct__u(){
            }
            public __AnonStruct__u copy(){
                __AnonStruct__u r = new __AnonStruct__u();
                r.exp = exp;
                r.integerexp = integerexp;
                r.errorexp = errorexp;
                r.realexp = realexp;
                r.complexexp = complexexp;
                r.symoffexp = symoffexp;
                r.stringexp = stringexp;
                r.arrayliteralexp = arrayliteralexp;
                r.assocarrayliteralexp = assocarrayliteralexp;
                r.structliteralexp = structliteralexp;
                r.nullexp = nullexp;
                r.dotvarexp = dotvarexp;
                r.addrexp = addrexp;
                r.indexexp = indexexp;
                r.sliceexp = sliceexp;
                r.vectorexp = vectorexp;
                return r;
            }
            public __AnonStruct__u opAssign(__AnonStruct__u that) {
                this.exp = that.exp;
                this.integerexp = that.integerexp;
                this.errorexp = that.errorexp;
                this.realexp = that.realexp;
                this.complexexp = that.complexexp;
                this.symoffexp = that.symoffexp;
                this.stringexp = that.stringexp;
                this.arrayliteralexp = that.arrayliteralexp;
                this.assocarrayliteralexp = that.assocarrayliteralexp;
                this.structliteralexp = that.structliteralexp;
                this.nullexp = that.nullexp;
                this.dotvarexp = that.dotvarexp;
                this.addrexp = that.addrexp;
                this.indexexp = that.indexexp;
                this.sliceexp = that.sliceexp;
                this.vectorexp = that.vectorexp;
                return this;
            }
        }
        public __AnonStruct__u u = new __AnonStruct__u();
        public UnionExp(){
            u = new __AnonStruct__u();
        }
        public UnionExp copy(){
            UnionExp r = new UnionExp();
            r.u = u.copy();
            return r;
        }
        public UnionExp opAssign(UnionExp that) {
            this.u = that.u;
            return this;
        }
    }
    public static int RealIdentical(double x1, double x2) {
        return ((((CTFloat.isNaN(x1) && CTFloat.isNaN(x2)) || CTFloat.isIdentical(x1, x2))) ? 1 : 0);
    }

    public static DotIdExp typeDotIdExp(Loc loc, Type type, Identifier ident) {
        return new DotIdExp(loc, new TypeExp(loc, type), ident);
    }

    public static VarDeclaration expToVariable(Expression e) {
        for (; (1) != 0;){
            switch ((e.op & 0xFF))
            {
                case 26:
                    return ((VarExp)e).var.isVarDeclaration();
                case 27:
                    e = ((DotVarExp)e).e1;
                    continue;
                case 62:
                    IndexExp ei = (IndexExp)e;
                    e = ei.e1;
                    Type ti = e.type.toBasetype();
                    if ((ti.ty & 0xFF) == ENUMTY.Tsarray)
                        continue;
                    return null;
                case 31:
                    SliceExp ei_1 = (SliceExp)e;
                    e = ei_1.e1;
                    Type ti_1 = e.type.toBasetype();
                    if ((ti_1.ty & 0xFF) == ENUMTY.Tsarray)
                        continue;
                    return null;
                case 123:
                case 124:
                    return ((ThisExp)e).var.isVarDeclaration();
                default:
                return null;
            }
        }
    }


    public static class OwnedBy 
    {
        public static final byte code = (byte)0;
        public static final byte ctfe = (byte)1;
        public static final byte cache = (byte)2;
    }

    static int WANTvalue = 0;
    static int WANTexpand = 1;
    public static abstract class Expression extends ASTNode
    {
        public byte op;
        public byte size;
        public byte parens;
        public Type type;
        public Loc loc = new Loc();
        public  Expression(Loc loc, byte op, int size) {
            super();
            this.loc = loc.copy();
            this.op = op;
            this.size = (byte)size;
        }

        public static void _init() {
            CTFEExp.cantexp = new CTFEExp(TOK.cantExpression);
            CTFEExp.voidexp = new CTFEExp(TOK.voidExpression);
            CTFEExp.breakexp = new CTFEExp(TOK.break_);
            CTFEExp.continueexp = new CTFEExp(TOK.continue_);
            CTFEExp.gotoexp = new CTFEExp(TOK.goto_);
            CTFEExp.showcontext = new CTFEExp(TOK.showCtfeContext);
        }

        public static void deinitialize() {
            CTFEExp.cantexp = null;
            CTFEExp.voidexp = null;
            CTFEExp.breakexp = null;
            CTFEExp.continueexp = null;
            CTFEExp.gotoexp = null;
            CTFEExp.showcontext = null;
        }

        public  Expression syntaxCopy() {
            return this.copy();
        }

        public  int dyncast() {
            return DYNCAST.expression;
        }

        public  BytePtr toChars() {
            OutBuffer buf = new OutBuffer();
            try {
                HdrGenState hgs = new HdrGenState();
                toCBuffer(this, buf, hgs);
                return buf.extractChars();
            }
            finally {
            }
        }

        public  void error(BytePtr format, Object... ap) {
            if (!pequals(this.type, Type.terror))
            {
                verror(this.loc, format, new Slice<>(ap), null, null, new BytePtr("Error: "));
            }
        }

        public  void errorSupplemental(BytePtr format, Object... ap) {
            if (pequals(this.type, Type.terror))
                return ;
            verrorSupplemental(this.loc, format, new Slice<>(ap));
        }

        public  void warning(BytePtr format, Object... ap) {
            if (!pequals(this.type, Type.terror))
            {
                vwarning(this.loc, format, new Slice<>(ap));
            }
        }

        public  void deprecation(BytePtr format, Object... ap) {
            if (!pequals(this.type, Type.terror))
            {
                vdeprecation(this.loc, format, new Slice<>(ap), null, null);
            }
        }

        public static Expression combine(Expression e1, Expression e2) {
            if (e1 != null)
            {
                if (e2 != null)
                {
                    e1 = new CommaExp(e1.loc, e1, e2, true);
                    e1.type = e2.type;
                }
            }
            else
                e1 = e2;
            return e1;
        }

        public static Expression combine(Expression e1, Expression e2, Expression e3) {
            return combine(combine(e1, e2), e3);
        }

        public static Expression combine(Expression e1, Expression e2, Expression e3, Expression e4) {
            return combine(combine(e1, e2), combine(e3, e4));
        }

        public static Expression extractLast(Expression e, Ref<Expression> e0) {
            e0.value = null;
            if ((e.op & 0xFF) != 99)
            {
                return e;
            }
            CommaExp ce = (CommaExp)e;
            if ((ce.e2.op & 0xFF) != 99)
            {
                e0.value = ce.e1;
                return ce.e2;
            }
            else
            {
                e0.value = e;
                Ptr<Expression> pce = pcopy(ce.e2);
                for (; (((CommaExp)pce.get()).e2.op & 0xFF) == 99;){
                    pce = pcopy((((CommaExp)pce.get()).e2));
                }
                assert(((pce.get()).op & 0xFF) == 99);
                ce = (CommaExp)pce.get();
                pce.set(0, ce.e1);
                return ce.e2;
            }
        }

        public static DArray<Expression> arraySyntaxCopy(DArray<Expression> exps) {
            DArray<Expression> a = null;
            if (exps != null)
            {
                a = new DArray<Expression>((exps).length);
                {
                    Slice<Expression> __r1321 = (exps).opSlice().copy();
                    int __key1320 = 0;
                    for (; __key1320 < __r1321.getLength();__key1320 += 1) {
                        Expression e = __r1321.get(__key1320);
                        int i = __key1320;
                        a.set(i, e != null ? e.syntaxCopy() : null);
                    }
                }
            }
            return a;
        }

        public  long toInteger() {
            this.error(new BytePtr("integer constant expression expected instead of `%s`"), this.toChars());
            return 0L;
        }

        public  long toUInteger() {
            return this.toInteger();
        }

        public  double toReal() {
            this.error(new BytePtr("floating point constant expression expected instead of `%s`"), this.toChars());
            return CTFloat.zero;
        }

        public  double toImaginary() {
            this.error(new BytePtr("floating point constant expression expected instead of `%s`"), this.toChars());
            return CTFloat.zero;
        }

        public  complex_t toComplex() {
            this.error(new BytePtr("floating point constant expression expected instead of `%s`"), this.toChars());
            return new complex_t(CTFloat.zero);
        }

        public  StringExp toStringExp() {
            return null;
        }

        public  TupleExp toTupleExp() {
            return null;
        }

        public  boolean isLvalue() {
            return false;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            if (!(e != null))
                e = this;
            else if (!(this.loc.isValid()))
                this.loc = e.loc.copy();
            if ((e.op & 0xFF) == 20)
                this.error(new BytePtr("`%s` is a `%s` definition and cannot be modified"), e.type.toChars(), e.type.kind());
            else
                this.error(new BytePtr("`%s` is not an lvalue and cannot be modified"), e.toChars());
            return new ErrorExp();
        }

        public  Expression modifiableLvalue(Scope sc, Expression e) {
            if (this.checkModifiable(sc, 0) == Modifiable.yes)
            {
                assert(this.type != null);
                if (!(this.type.isMutable()))
                {
                    {
                        DotVarExp dve = this.isDotVarExp();
                        if (dve != null)
                        {
                            if (isNeedThisScope(sc, dve.var))
                            {
                                Dsymbol s = (sc).func;
                                for (; s != null;s = s.toParentLocal()){
                                    FuncDeclaration ff = s.isFuncDeclaration();
                                    if (!(ff != null))
                                        break;
                                    if (!(ff.type.isMutable()))
                                    {
                                        this.error(new BytePtr("cannot modify `%s` in `%s` function"), this.toChars(), MODtoChars(this.type.mod));
                                        return new ErrorExp();
                                    }
                                }
                            }
                        }
                    }
                    this.error(new BytePtr("cannot modify `%s` expression `%s`"), MODtoChars(this.type.mod), this.toChars());
                    return new ErrorExp();
                }
                else if (!(this.type.isAssignable()))
                {
                    this.error(new BytePtr("cannot modify struct instance `%s` of type `%s` because it contains `const` or `immutable` members"), this.toChars(), this.type.toChars());
                    return new ErrorExp();
                }
            }
            return this.toLvalue(sc, e);
        }

        public  Expression implicitCastTo(Scope sc, Type t) {
            return implicitCastTo(this, sc, t);
        }

        public  int implicitConvTo(Type t) {
            return implicitConvTo(this, t);
        }

        public  Expression castTo(Scope sc, Type t) {
            return castTo(this, sc, t);
        }

        public  Expression resolveLoc(Loc loc, Scope sc) {
            this.loc = loc.copy();
            return this;
        }

        public  boolean checkType() {
            return false;
        }

        public  boolean checkValue() {
            if ((this.type != null && (this.type.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
            {
                this.error(new BytePtr("expression `%s` is `void` and has no value"), this.toChars());
                if (!((global.gag) != 0))
                    this.type = Type.terror;
                return true;
            }
            return false;
        }

        public  boolean checkScalar() {
            if ((this.op & 0xFF) == 127)
                return true;
            if ((this.type.toBasetype().ty & 0xFF) == ENUMTY.Terror)
                return true;
            if (!(this.type.isscalar()))
            {
                this.error(new BytePtr("`%s` is not a scalar, it is a `%s`"), this.toChars(), this.type.toChars());
                return true;
            }
            return this.checkValue();
        }

        public  boolean checkNoBool() {
            if ((this.op & 0xFF) == 127)
                return true;
            if ((this.type.toBasetype().ty & 0xFF) == ENUMTY.Terror)
                return true;
            if ((this.type.toBasetype().ty & 0xFF) == ENUMTY.Tbool)
            {
                this.error(new BytePtr("operation not allowed on `bool` `%s`"), this.toChars());
                return true;
            }
            return false;
        }

        public  boolean checkIntegral() {
            if ((this.op & 0xFF) == 127)
                return true;
            if ((this.type.toBasetype().ty & 0xFF) == ENUMTY.Terror)
                return true;
            if (!(this.type.isintegral()))
            {
                this.error(new BytePtr("`%s` is not of integral type, it is a `%s`"), this.toChars(), this.type.toChars());
                return true;
            }
            return this.checkValue();
        }

        public  boolean checkArithmetic() {
            if ((this.op & 0xFF) == 127)
                return true;
            if ((this.type.toBasetype().ty & 0xFF) == ENUMTY.Terror)
                return true;
            if ((!(this.type.isintegral()) && !(this.type.isfloating())))
            {
                this.error(new BytePtr("`%s` is not of arithmetic type, it is a `%s`"), this.toChars(), this.type.toChars());
                return true;
            }
            return this.checkValue();
        }

        public  boolean checkDeprecated(Scope sc, Dsymbol s) {
            return s.checkDeprecated(this.loc, sc);
        }

        public  boolean checkDisabled(Scope sc, Dsymbol s) {
            {
                Declaration d = s.isDeclaration();
                if (d != null)
                {
                    return d.checkDisabled(this.loc, sc, false);
                }
            }
            return false;
        }

        public  boolean checkPurity(Scope sc, FuncDeclaration f) {
            if (!((sc).func != null))
                return false;
            if (pequals((sc).func, f))
                return false;
            if ((sc).intypeof == 1)
                return false;
            if (((sc).flags & 136) != 0)
                return false;
            FuncDeclaration outerfunc = (sc).func;
            FuncDeclaration calledparent = f;
            if (outerfunc.isInstantiated() != null)
            {
            }
            else if (f.isInstantiated() != null)
            {
            }
            else if (f.isFuncLiteralDeclaration() != null)
            {
            }
            else
            {
                for (; ((outerfunc.toParent2() != null && outerfunc.isPureBypassingInference() == PURE.impure) && outerfunc.toParent2().isFuncDeclaration() != null);){
                    outerfunc = outerfunc.toParent2().isFuncDeclaration();
                    if ((outerfunc.type.ty & 0xFF) == ENUMTY.Terror)
                        return true;
                }
                for (; ((calledparent.toParent2() != null && calledparent.isPureBypassingInference() == PURE.impure) && calledparent.toParent2().isFuncDeclaration() != null);){
                    calledparent = calledparent.toParent2().isFuncDeclaration();
                    if ((calledparent.type.ty & 0xFF) == ENUMTY.Terror)
                        return true;
                }
            }
            if ((!((f.isPure()) != 0) && !pequals(calledparent, outerfunc)))
            {
                FuncDeclaration ff = outerfunc;
                if (((sc).flags & 256) != 0 ? ff.isPureBypassingInference() >= PURE.weak : ff.setImpure())
                {
                    this.error(new BytePtr("`pure` %s `%s` cannot call impure %s `%s`"), ff.kind(), ff.toPrettyChars(false), f.kind(), f.toPrettyChars(false));
                    return true;
                }
            }
            return false;
        }

        public  boolean checkPurity(Scope sc, VarDeclaration v) {
            if (!((sc).func != null))
                return false;
            if ((sc).intypeof == 1)
                return false;
            if (((sc).flags & 136) != 0)
                return false;
            if (pequals(v.ident, Id.ctfe))
                return false;
            if (v.isImmutable())
                return false;
            if ((((v.isConst() && !(v.isRef())) && (v.isDataseg() || v.isParameter())) && (v.type.implicitConvTo(v.type.immutableOf())) != 0))
                return false;
            if ((v.storage_class & 8388608L) != 0)
                return false;
            if ((v.type.ty & 0xFF) == ENUMTY.Tstruct)
            {
                StructDeclaration sd = ((TypeStruct)v.type).sym;
                if (sd.hasNoFields)
                    return false;
            }
            boolean err = false;
            if (v.isDataseg())
            {
                if (pequals(v.ident, Id.gate))
                    return false;
                {
                    Dsymbol s = (sc).func;
                    for (; s != null;s = s.toParent2()){
                        FuncDeclaration ff = s.isFuncDeclaration();
                        if (!(ff != null))
                            break;
                        if (((sc).flags & 256) != 0 ? ff.isPureBypassingInference() >= PURE.weak : ff.setImpure())
                        {
                            this.error(new BytePtr("`pure` %s `%s` cannot access mutable static data `%s`"), ff.kind(), ff.toPrettyChars(false), v.toChars());
                            err = true;
                            break;
                        }
                        if (ff.isInstantiated() != null)
                            break;
                        if (ff.isFuncLiteralDeclaration() != null)
                            break;
                    }
                }
            }
            else
            {
                Dsymbol vparent = v.toParent2();
                {
                    Dsymbol s = (sc).func;
                    for (; (!(err) && s != null);s = toParentPDsymbol(s, vparent)){
                        if (pequals(s, vparent))
                            break;
                        {
                            AggregateDeclaration ad = s.isAggregateDeclaration();
                            if (ad != null)
                            {
                                if (ad.isNested())
                                    continue;
                                break;
                            }
                        }
                        FuncDeclaration ff = s.isFuncDeclaration();
                        if (!(ff != null))
                            break;
                        if ((ff.isNested() || ff.isThis() != null))
                        {
                            if ((ff.type.isImmutable() || (ff.type.isShared() && !(MODimplicitConv(ff.type.mod, v.type.mod)))))
                            {
                                OutBuffer ffbuf = new OutBuffer();
                                try {
                                    OutBuffer vbuf = new OutBuffer();
                                    try {
                                        MODMatchToBuffer(ffbuf, ff.type.mod, v.type.mod);
                                        MODMatchToBuffer(vbuf, v.type.mod, ff.type.mod);
                                        this.error(new BytePtr("%s%s `%s` cannot access %sdata `%s`"), ffbuf.peekChars(), ff.kind(), ff.toPrettyChars(false), vbuf.peekChars(), v.toChars());
                                        err = true;
                                        break;
                                    }
                                    finally {
                                    }
                                }
                                finally {
                                }
                            }
                            continue;
                        }
                        break;
                    }
                }
            }
            if ((v.storage_class & 1073741824L) != 0)
            {
                if ((sc).func.setUnsafe())
                {
                    this.error(new BytePtr("`@safe` %s `%s` cannot access `__gshared` data `%s`"), (sc).func.kind(), (sc).func.toChars(), v.toChars());
                    err = true;
                }
            }
            return err;
        }

        public  boolean checkSafety(Scope sc, FuncDeclaration f) {
            if (!((sc).func != null))
                return false;
            if (pequals((sc).func, f))
                return false;
            if ((sc).intypeof == 1)
                return false;
            if (((sc).flags & 128) != 0)
                return false;
            if ((!(f.isSafe()) && !(f.isTrusted())))
            {
                if (((sc).flags & 256) != 0 ? (sc).func.isSafeBypassingInference() : ((sc).func.setUnsafe() && !(((sc).flags & 8) != 0)))
                {
                    if (!(this.loc.isValid()))
                        this.loc = (sc).func.loc.copy();
                    BytePtr prettyChars = pcopy(f.toPrettyChars(false));
                    this.error(new BytePtr("`@safe` %s `%s` cannot call `@system` %s `%s`"), (sc).func.kind(), (sc).func.toPrettyChars(false), f.kind(), prettyChars);
                    errorSupplemental(f.loc, new BytePtr("`%s` is declared here"), prettyChars);
                    return true;
                }
            }
            return false;
        }

        public  boolean checkNogc(Scope sc, FuncDeclaration f) {
            if (!((sc).func != null))
                return false;
            if (pequals((sc).func, f))
                return false;
            if ((sc).intypeof == 1)
                return false;
            if (((sc).flags & 128) != 0)
                return false;
            if (!(f.isNogc()))
            {
                if (((sc).flags & 256) != 0 ? (sc).func.isNogcBypassingInference() : ((sc).func.setGC() && !(((sc).flags & 8) != 0)))
                {
                    if (this.loc.linnum == 0)
                        this.loc = (sc).func.loc.copy();
                    this.error(new BytePtr("`@nogc` %s `%s` cannot call non-@nogc %s `%s`"), (sc).func.kind(), (sc).func.toPrettyChars(false), f.kind(), f.toPrettyChars(false));
                    return true;
                }
            }
            return false;
        }

        public  boolean checkPostblit(Scope sc, Type t) {
            {
                TypeStruct ts = t.baseElemOf().isTypeStruct();
                if (ts != null)
                {
                    if (global.params.useTypeInfo)
                    {
                        semanticTypeInfo(sc, t);
                    }
                    StructDeclaration sd = ts.sym;
                    if (sd.postblit != null)
                    {
                        if (sd.postblit.checkDisabled(this.loc, sc, false))
                            return true;
                        this.checkPurity(sc, sd.postblit);
                        this.checkSafety(sc, sd.postblit);
                        this.checkNogc(sc, sd.postblit);
                        return false;
                    }
                }
            }
            return false;
        }

        public  boolean checkRightThis(Scope sc) {
            if ((this.op & 0xFF) == 127)
                return true;
            if (((this.op & 0xFF) == 26 && (this.type.ty & 0xFF) != ENUMTY.Terror))
            {
                VarExp ve = (VarExp)this;
                if (isNeedThisScope(sc, ve.var))
                {
                    this.error(new BytePtr("need `this` for `%s` of type `%s`"), ve.var.toChars(), ve.var.type.toChars());
                    return true;
                }
            }
            return false;
        }

        public  boolean checkReadModifyWrite(byte rmwOp, Expression ex) {
            if ((!(this.type != null) || !(this.type.isShared())))
                return false;
            switch ((rmwOp & 0xFF))
            {
                case 93:
                case 103:
                    rmwOp = TOK.addAssign;
                    break;
                case 94:
                case 104:
                    rmwOp = TOK.minAssign;
                    break;
                default:
                break;
            }
            this.error(new BytePtr("read-modify-write operations are not allowed for `shared` variables. Use `core.atomic.atomicOp!\"%s\"(%s, %s)` instead."), Token.toChars(rmwOp), this.toChars(), ex != null ? ex.toChars() : new BytePtr("1"));
            return true;
        }

        public  int checkModifiable(Scope sc, int flag) {
            return this.type != null ? Modifiable.yes : Modifiable.no;
        }

        public  Expression toBoolean(Scope sc) {
            Expression e = this;
            Type t = this.type;
            Type tb = this.type.toBasetype();
            Type att = null;
            for (; (1) != 0;){
                {
                    TypeStruct ts = tb.isTypeStruct();
                    if (ts != null)
                    {
                        AggregateDeclaration ad = ts.sym;
                        {
                            Dsymbol fd = search_function(ad, Id._cast);
                            if (fd != null)
                            {
                                e = new CastExp(this.loc, e, Type.tbool);
                                e = expressionSemantic(e, sc);
                                return e;
                            }
                        }
                        if ((ad.aliasthis != null && !pequals(tb, att)))
                        {
                            if ((!(att != null) && tb.checkAliasThisRec()))
                                att = tb;
                            e = resolveAliasThis(sc, e, false);
                            t = e.type;
                            tb = e.type.toBasetype();
                            continue;
                        }
                    }
                }
                break;
            }
            if (!(t.isBoolean()))
            {
                if (!pequals(tb, Type.terror))
                    this.error(new BytePtr("expression `%s` of type `%s` does not have a boolean value"), this.toChars(), t.toChars());
                return new ErrorExp();
            }
            return e;
        }

        public  Expression addDtorHook(Scope sc) {
            return this;
        }

        public  Expression addressOf() {
            Expression e = new AddrExp(this.loc, this, this.type.pointerTo());
            return e;
        }

        public  Expression deref() {
            if (this.type != null)
                {
                    TypeReference tr = this.type.isTypeReference();
                    if (tr != null)
                    {
                        Expression e = new PtrExp(this.loc, this, tr.next);
                        return e;
                    }
                }
            return this;
        }

        public  Expression optimize(int result, boolean keepLvalue) {
            return Expression_optimize(this, result, keepLvalue);
        }

        public  Expression ctfeInterpret() {
            return ctfeInterpret(this);
        }

        public  int isConst() {
            return isConst(this);
        }

        public  boolean isBool(boolean result) {
            return false;
        }

        public  boolean hasCode() {
            return true;
        }

        public  IntegerExp isIntegerExp() {
            return (this.op & 0xFF) == 135 ? (IntegerExp)this : null;
        }

        public  ErrorExp isErrorExp() {
            return (this.op & 0xFF) == 127 ? (ErrorExp)this : null;
        }

        public  VoidInitExp isVoidInitExp() {
            return (this.op & 0xFF) == 128 ? (VoidInitExp)this : null;
        }

        public  RealExp isRealExp() {
            return (this.op & 0xFF) == 140 ? (RealExp)this : null;
        }

        public  ComplexExp isComplexExp() {
            return (this.op & 0xFF) == 147 ? (ComplexExp)this : null;
        }

        public  IdentifierExp isIdentifierExp() {
            return (this.op & 0xFF) == 120 ? (IdentifierExp)this : null;
        }

        public  DollarExp isDollarExp() {
            return (this.op & 0xFF) == 35 ? (DollarExp)this : null;
        }

        public  DsymbolExp isDsymbolExp() {
            return (this.op & 0xFF) == 41 ? (DsymbolExp)this : null;
        }

        public  ThisExp isThisExp() {
            return (this.op & 0xFF) == 123 ? (ThisExp)this : null;
        }

        public  SuperExp isSuperExp() {
            return (this.op & 0xFF) == 124 ? (SuperExp)this : null;
        }

        public  NullExp isNullExp() {
            return (this.op & 0xFF) == 13 ? (NullExp)this : null;
        }

        public  StringExp isStringExp() {
            return (this.op & 0xFF) == 121 ? (StringExp)this : null;
        }

        public  TupleExp isTupleExp() {
            return (this.op & 0xFF) == 126 ? (TupleExp)this : null;
        }

        public  ArrayLiteralExp isArrayLiteralExp() {
            return (this.op & 0xFF) == 47 ? (ArrayLiteralExp)this : null;
        }

        public  AssocArrayLiteralExp isAssocArrayLiteralExp() {
            return (this.op & 0xFF) == 48 ? (AssocArrayLiteralExp)this : null;
        }

        public  StructLiteralExp isStructLiteralExp() {
            return (this.op & 0xFF) == 49 ? (StructLiteralExp)this : null;
        }

        public  TypeExp isTypeExp() {
            return (this.op & 0xFF) == 20 ? (TypeExp)this : null;
        }

        public  ScopeExp isScopeExp() {
            return (this.op & 0xFF) == 203 ? (ScopeExp)this : null;
        }

        public  TemplateExp isTemplateExp() {
            return (this.op & 0xFF) == 36 ? (TemplateExp)this : null;
        }

        public  NewExp isNewExp() {
            return (this.op & 0xFF) == 22 ? (NewExp)this : null;
        }

        public  NewAnonClassExp isNewAnonClassExp() {
            return (this.op & 0xFF) == 45 ? (NewAnonClassExp)this : null;
        }

        public  SymOffExp isSymOffExp() {
            return (this.op & 0xFF) == 25 ? (SymOffExp)this : null;
        }

        public  VarExp isVarExp() {
            return (this.op & 0xFF) == 26 ? (VarExp)this : null;
        }

        public  OverExp isOverExp() {
            return (this.op & 0xFF) == 214 ? (OverExp)this : null;
        }

        public  FuncExp isFuncExp() {
            return (this.op & 0xFF) == 161 ? (FuncExp)this : null;
        }

        public  DeclarationExp isDeclarationExp() {
            return (this.op & 0xFF) == 38 ? (DeclarationExp)this : null;
        }

        public  TypeidExp isTypeidExp() {
            return (this.op & 0xFF) == 42 ? (TypeidExp)this : null;
        }

        public  TraitsExp isTraitsExp() {
            return (this.op & 0xFF) == 213 ? (TraitsExp)this : null;
        }

        public  HaltExp isHaltExp() {
            return (this.op & 0xFF) == 125 ? (HaltExp)this : null;
        }

        public  IsExp isExp() {
            return (this.op & 0xFF) == 63 ? (IsExp)this : null;
        }

        public  CompileExp isCompileExp() {
            return (this.op & 0xFF) == 162 ? (CompileExp)this : null;
        }

        public  ImportExp isImportExp() {
            return (this.op & 0xFF) == 157 ? (ImportExp)this : null;
        }

        public  AssertExp isAssertExp() {
            return (this.op & 0xFF) == 14 ? (AssertExp)this : null;
        }

        public  DotIdExp isDotIdExp() {
            return (this.op & 0xFF) == 28 ? (DotIdExp)this : null;
        }

        public  DotTemplateExp isDotTemplateExp() {
            return (this.op & 0xFF) == 37 ? (DotTemplateExp)this : null;
        }

        public  DotVarExp isDotVarExp() {
            return (this.op & 0xFF) == 27 ? (DotVarExp)this : null;
        }

        public  DotTemplateInstanceExp isDotTemplateInstanceExp() {
            return (this.op & 0xFF) == 29 ? (DotTemplateInstanceExp)this : null;
        }

        public  DelegateExp isDelegateExp() {
            return (this.op & 0xFF) == 160 ? (DelegateExp)this : null;
        }

        public  DotTypeExp isDotTypeExp() {
            return (this.op & 0xFF) == 30 ? (DotTypeExp)this : null;
        }

        public  CallExp isCallExp() {
            return (this.op & 0xFF) == 18 ? (CallExp)this : null;
        }

        public  AddrExp isAddrExp() {
            return (this.op & 0xFF) == 19 ? (AddrExp)this : null;
        }

        public  PtrExp isPtrExp() {
            return (this.op & 0xFF) == 24 ? (PtrExp)this : null;
        }

        public  NegExp isNegExp() {
            return (this.op & 0xFF) == 8 ? (NegExp)this : null;
        }

        public  UAddExp isUAddExp() {
            return (this.op & 0xFF) == 43 ? (UAddExp)this : null;
        }

        public  ComExp isComExp() {
            return (this.op & 0xFF) == 92 ? (ComExp)this : null;
        }

        public  NotExp isNotExp() {
            return (this.op & 0xFF) == 91 ? (NotExp)this : null;
        }

        public  DeleteExp isDeleteExp() {
            return (this.op & 0xFF) == 23 ? (DeleteExp)this : null;
        }

        public  CastExp isCastExp() {
            return (this.op & 0xFF) == 12 ? (CastExp)this : null;
        }

        public  VectorExp isVectorExp() {
            return (this.op & 0xFF) == 229 ? (VectorExp)this : null;
        }

        public  VectorArrayExp isVectorArrayExp() {
            return (this.op & 0xFF) == 236 ? (VectorArrayExp)this : null;
        }

        public  SliceExp isSliceExp() {
            return (this.op & 0xFF) == 31 ? (SliceExp)this : null;
        }

        public  ArrayLengthExp isArrayLengthExp() {
            return (this.op & 0xFF) == 32 ? (ArrayLengthExp)this : null;
        }

        public  ArrayExp isArrayExp() {
            return (this.op & 0xFF) == 17 ? (ArrayExp)this : null;
        }

        public  DotExp isDotExp() {
            return (this.op & 0xFF) == 97 ? (DotExp)this : null;
        }

        public  CommaExp isCommaExp() {
            return (this.op & 0xFF) == 99 ? (CommaExp)this : null;
        }

        public  IntervalExp isIntervalExp() {
            return (this.op & 0xFF) == 231 ? (IntervalExp)this : null;
        }

        public  DelegatePtrExp isDelegatePtrExp() {
            return (this.op & 0xFF) == 52 ? (DelegatePtrExp)this : null;
        }

        public  DelegateFuncptrExp isDelegateFuncptrExp() {
            return (this.op & 0xFF) == 53 ? (DelegateFuncptrExp)this : null;
        }

        public  IndexExp isIndexExp() {
            return (this.op & 0xFF) == 62 ? (IndexExp)this : null;
        }

        public  PostExp isPostExp() {
            return ((this.op & 0xFF) == 93 || (this.op & 0xFF) == 94) ? (PostExp)this : null;
        }

        public  PreExp isPreExp() {
            return ((this.op & 0xFF) == 103 || (this.op & 0xFF) == 104) ? (PreExp)this : null;
        }

        public  AssignExp isAssignExp() {
            return (this.op & 0xFF) == 90 ? (AssignExp)this : null;
        }

        public  ConstructExp isConstructExp() {
            return (this.op & 0xFF) == 95 ? (ConstructExp)this : null;
        }

        public  BlitExp isBlitExp() {
            return (this.op & 0xFF) == 96 ? (BlitExp)this : null;
        }

        public  AddAssignExp isAddAssignExp() {
            return (this.op & 0xFF) == 76 ? (AddAssignExp)this : null;
        }

        public  MinAssignExp isMinAssignExp() {
            return (this.op & 0xFF) == 77 ? (MinAssignExp)this : null;
        }

        public  MulAssignExp isMulAssignExp() {
            return (this.op & 0xFF) == 81 ? (MulAssignExp)this : null;
        }

        public  DivAssignExp isDivAssignExp() {
            return (this.op & 0xFF) == 82 ? (DivAssignExp)this : null;
        }

        public  ModAssignExp isModAssignExp() {
            return (this.op & 0xFF) == 83 ? (ModAssignExp)this : null;
        }

        public  AndAssignExp isAndAssignExp() {
            return (this.op & 0xFF) == 87 ? (AndAssignExp)this : null;
        }

        public  OrAssignExp isOrAssignExp() {
            return (this.op & 0xFF) == 88 ? (OrAssignExp)this : null;
        }

        public  XorAssignExp isXorAssignExp() {
            return (this.op & 0xFF) == 89 ? (XorAssignExp)this : null;
        }

        public  PowAssignExp isPowAssignExp() {
            return (this.op & 0xFF) == 227 ? (PowAssignExp)this : null;
        }

        public  ShlAssignExp isShlAssignExp() {
            return (this.op & 0xFF) == 66 ? (ShlAssignExp)this : null;
        }

        public  ShrAssignExp isShrAssignExp() {
            return (this.op & 0xFF) == 67 ? (ShrAssignExp)this : null;
        }

        public  UshrAssignExp isUshrAssignExp() {
            return (this.op & 0xFF) == 69 ? (UshrAssignExp)this : null;
        }

        public  CatAssignExp isCatAssignExp() {
            return (this.op & 0xFF) == 71 ? (CatAssignExp)this : null;
        }

        public  CatElemAssignExp isCatElemAssignExp() {
            return (this.op & 0xFF) == 72 ? (CatElemAssignExp)this : null;
        }

        public  CatDcharAssignExp isCatDcharAssignExp() {
            return (this.op & 0xFF) == 73 ? (CatDcharAssignExp)this : null;
        }

        public  AddExp isAddExp() {
            return (this.op & 0xFF) == 74 ? (AddExp)this : null;
        }

        public  MinExp isMinExp() {
            return (this.op & 0xFF) == 75 ? (MinExp)this : null;
        }

        public  CatExp isCatExp() {
            return (this.op & 0xFF) == 70 ? (CatExp)this : null;
        }

        public  MulExp isMulExp() {
            return (this.op & 0xFF) == 78 ? (MulExp)this : null;
        }

        public  DivExp isDivExp() {
            return (this.op & 0xFF) == 79 ? (DivExp)this : null;
        }

        public  ModExp isModExp() {
            return (this.op & 0xFF) == 80 ? (ModExp)this : null;
        }

        public  PowExp isPowExp() {
            return (this.op & 0xFF) == 226 ? (PowExp)this : null;
        }

        public  ShlExp isShlExp() {
            return (this.op & 0xFF) == 64 ? (ShlExp)this : null;
        }

        public  ShrExp isShrExp() {
            return (this.op & 0xFF) == 65 ? (ShrExp)this : null;
        }

        public  UshrExp isUshrExp() {
            return (this.op & 0xFF) == 68 ? (UshrExp)this : null;
        }

        public  AndExp isAndExp() {
            return (this.op & 0xFF) == 84 ? (AndExp)this : null;
        }

        public  OrExp isOrExp() {
            return (this.op & 0xFF) == 85 ? (OrExp)this : null;
        }

        public  XorExp isXorExp() {
            return (this.op & 0xFF) == 86 ? (XorExp)this : null;
        }

        public  LogicalExp isLogicalExp() {
            return ((this.op & 0xFF) == 101 || (this.op & 0xFF) == 102) ? (LogicalExp)this : null;
        }

        public  InExp isInExp() {
            return (this.op & 0xFF) == 175 ? (InExp)this : null;
        }

        public  RemoveExp isRemoveExp() {
            return (this.op & 0xFF) == 44 ? (RemoveExp)this : null;
        }

        public  EqualExp isEqualExp() {
            return ((this.op & 0xFF) == 58 || (this.op & 0xFF) == 59) ? (EqualExp)this : null;
        }

        public  IdentityExp isIdentityExp() {
            return ((this.op & 0xFF) == 60 || (this.op & 0xFF) == 61) ? (IdentityExp)this : null;
        }

        public  CondExp isCondExp() {
            return (this.op & 0xFF) == 100 ? (CondExp)this : null;
        }

        public  DefaultInitExp isDefaultInitExp() {
            return (this.op & 0xFF) == 190 ? (DefaultInitExp)this : null;
        }

        public  FileInitExp isFileInitExp() {
            return ((this.op & 0xFF) == 219 || (this.op & 0xFF) == 220) ? (FileInitExp)this : null;
        }

        public  LineInitExp isLineInitExp() {
            return (this.op & 0xFF) == 218 ? (LineInitExp)this : null;
        }

        public  ModuleInitExp isModuleInitExp() {
            return (this.op & 0xFF) == 221 ? (ModuleInitExp)this : null;
        }

        public  FuncInitExp isFuncInitExp() {
            return (this.op & 0xFF) == 222 ? (FuncInitExp)this : null;
        }

        public  PrettyFuncInitExp isPrettyFuncInitExp() {
            return (this.op & 0xFF) == 223 ? (PrettyFuncInitExp)this : null;
        }

        public  ClassReferenceExp isClassReferenceExp() {
            return (this.op & 0xFF) == 50 ? (ClassReferenceExp)this : null;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public Expression() {}

        public abstract Expression copy();
    }
    public static class IntegerExp extends Expression
    {
        public long value;
        public  IntegerExp(Loc loc, long value, Type type) {
            super(loc, TOK.int64, 32);
            assert(type != null);
            if (!(type.isscalar()))
            {
                if ((type.ty & 0xFF) != ENUMTY.Terror)
                    this.error(new BytePtr("integral constant must be scalar type, not `%s`"), type.toChars());
                type = Type.terror;
            }
            this.type = type;
            this.value = normalize(type.toBasetype().ty, value);
        }

        public  IntegerExp(long value) {
            super(Loc.initial, TOK.int64, 32);
            this.type = Type.tint32;
            this.value = (long)(int)value;
        }

        public static IntegerExp create(Loc loc, long value, Type type) {
            return new IntegerExp(loc, value, type);
        }

        public static void emplace(UnionExp pue, Loc loc, long value, Type type) {
            emplaceExpIntegerExpLocLongType(pue, loc, value, type);
        }

        public  boolean equals(RootObject o) {
            if (pequals(this, o))
                return true;
            {
                IntegerExp ne = ((Expression)o).isIntegerExp();
                if (ne != null)
                {
                    if ((this.type.toHeadMutable().equals(ne.type.toHeadMutable()) && this.value == ne.value))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public  long toInteger() {
            return this.value = normalize(this.type.toBasetype().ty, this.value);
        }

        public  double toReal() {
            byte ty = this.type.toBasetype().ty;
            long val = normalize(ty, this.value);
            this.value = val;
            return (ty & 0xFF) == ENUMTY.Tuns64 ? (double)val : (double)(long)val;
        }

        public  double toImaginary() {
            return CTFloat.zero;
        }

        public  complex_t toComplex() {
            return new complex_t(this.toReal());
        }

        public  boolean isBool(boolean result) {
            boolean r = this.toInteger() != 0L;
            return result ? r : !(r);
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            if (!(e != null))
                e = this;
            else if (!(this.loc.isValid()))
                this.loc = e.loc.copy();
            e.error(new BytePtr("cannot modify constant `%s`"), e.toChars());
            return new ErrorExp();
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public  long getInteger() {
            return this.value;
        }

        public  void setInteger(long value) {
            this.value = normalize(this.type.toBasetype().ty, value);
        }

        public static long normalize(byte ty, long value) {
            long result = 0L;
            {
                int __dispatch2 = 0;
                dispatched_2:
                do {
                    switch (__dispatch2 != 0 ? __dispatch2 : (ty & 0xFF))
                    {
                        case 30:
                            result = ((value != 0L) ? 1 : 0);
                            break;
                        case 13:
                            result = (long)(byte)value;
                            break;
                        case 31:
                        case 14:
                            result = (long)(byte)value;
                            break;
                        case 15:
                            result = (long)(int)value;
                            break;
                        case 32:
                        case 16:
                            __dispatch2 = 0;
                            result = (long)(int)value;
                            break;
                        case 17:
                            result = (long)(int)value;
                            break;
                        case 33:
                        case 18:
                            __dispatch2 = 0;
                            result = (long)(int)value;
                            break;
                        case 19:
                            result = (long)(long)value;
                            break;
                        case 20:
                            __dispatch2 = 0;
                            result = value;
                            break;
                        case 3:
                            if (target.ptrsize == 8)
                                /*goto case*/{ __dispatch2 = 20; continue dispatched_2; }
                            if (target.ptrsize == 4)
                                /*goto case*/{ __dispatch2 = 18; continue dispatched_2; }
                            if (target.ptrsize == 2)
                                /*goto case*/{ __dispatch2 = 16; continue dispatched_2; }
                            throw new AssertionError("Unreachable code!");
                        default:
                        break;
                    }
                } while(__dispatch2 != 0);
            }
            return result;
        }

        public  Expression syntaxCopy() {
            return this;
        }

        // from template literal!(-1)
        public static IntegerExp literal-1() {
            if (!(expression.literaltheConstant != null))
                expression.literaltheConstant = new IntegerExp(-1L);
            return expression.literaltheConstant;
        }


        // from template literal!(0)
        public static IntegerExp literal0() {
            if (!(expression.literaltheConstant != null))
                expression.literaltheConstant = new IntegerExp(0L);
            return expression.literaltheConstant;
        }


        // from template literal!(1)
        public static IntegerExp literal1() {
            if (!(expression.literaltheConstant != null))
                expression.literaltheConstant = new IntegerExp(1L);
            return expression.literaltheConstant;
        }



        public IntegerExp() {}

        public IntegerExp copy() {
            IntegerExp that = new IntegerExp();
            that.value = this.value;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ErrorExp extends Expression
    {
        public  ErrorExp() {
            if ((global.errors == 0 && global.gaggedErrors == 0))
            {
                this.error(new BytePtr("unknown, please file report on issues.dlang.org"));
            }
            super(Loc.initial, TOK.error, 24);
            this.type = Type.terror;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public static ErrorExp errorexp;

        public ErrorExp copy() {
            ErrorExp that = new ErrorExp();
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class VoidInitExp extends Expression
    {
        public VarDeclaration var;
        public  VoidInitExp(VarDeclaration var) {
            super(var.loc, TOK.void_, 28);
            this.var = var;
            this.type = var.type;
        }

        public  BytePtr toChars() {
            return new BytePtr("void");
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public VoidInitExp() {}

        public VoidInitExp copy() {
            VoidInitExp that = new VoidInitExp();
            that.var = this.var;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class RealExp extends Expression
    {
        public double value;
        public  RealExp(Loc loc, double value, Type type) {
            super(loc, TOK.float64, 40);
            this.value = value;
            this.type = type;
        }

        public static RealExp create(Loc loc, double value, Type type) {
            return new RealExp(loc, value, type);
        }

        public static void emplace(UnionExp pue, Loc loc, double value, Type type) {
            emplaceExpRealExpLocDoubleType(pue, loc, value, type);
        }

        public  boolean equals(RootObject o) {
            if (pequals(this, o))
                return true;
            {
                RealExp ne = ((Expression)o).isRealExp();
                if (ne != null)
                {
                    if ((this.type.toHeadMutable().equals(ne.type.toHeadMutable()) && (RealIdentical(this.value, ne.value)) != 0))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public  long toInteger() {
            return (long)(long)this.toReal();
        }

        public  long toUInteger() {
            return (long)this.toReal();
        }

        public  double toReal() {
            return this.type.isreal() ? this.value : CTFloat.zero;
        }

        public  double toImaginary() {
            return this.type.isreal() ? CTFloat.zero : this.value;
        }

        public  complex_t toComplex() {
            return new complex_t(this.toReal(), this.toImaginary());
        }

        public  boolean isBool(boolean result) {
            return result ? ((this.value) != 0) : !(((this.value) != 0));
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public RealExp() {}

        public RealExp copy() {
            RealExp that = new RealExp();
            that.value = this.value;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ComplexExp extends Expression
    {
        public complex_t value = new complex_t();
        public  ComplexExp(Loc loc, complex_t value, Type type) {
            super(loc, TOK.complex80, 56);
            this.value = value.copy();
            this.type = type;
        }

        public static ComplexExp create(Loc loc, complex_t value, Type type) {
            return new ComplexExp(loc, value, type);
        }

        public static void emplace(UnionExp pue, Loc loc, complex_t value, Type type) {
            emplaceExpComplexExpLoccomplex_tType(pue, loc, value, type);
        }

        public  boolean equals(RootObject o) {
            if (pequals(this, o))
                return true;
            {
                ComplexExp ne = ((Expression)o).isComplexExp();
                if (ne != null)
                {
                    if (((this.type.toHeadMutable().equals(ne.type.toHeadMutable()) && (RealIdentical(creall(this.value), creall(ne.value))) != 0) && (RealIdentical(cimagl(this.value), cimagl(ne.value))) != 0))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public  long toInteger() {
            return (long)(long)this.toReal();
        }

        public  long toUInteger() {
            return (long)this.toReal();
        }

        public  double toReal() {
            return creall(this.value);
        }

        public  double toImaginary() {
            return cimagl(this.value);
        }

        public  complex_t toComplex() {
            return this.value;
        }

        public  boolean isBool(boolean result) {
            if (result)
                return this.value.opCastBoolean();
            else
                return !(this.value.opCastBoolean());
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ComplexExp() {}

        public ComplexExp copy() {
            ComplexExp that = new ComplexExp();
            that.value = this.value;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class IdentifierExp extends Expression
    {
        public Identifier ident;
        public  IdentifierExp(Loc loc, Identifier ident) {
            super(loc, TOK.identifier, 28);
            this.ident = ident;
        }

        public static IdentifierExp create(Loc loc, Identifier ident) {
            return new IdentifierExp(loc, ident);
        }

        public  boolean isLvalue() {
            return true;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public IdentifierExp() {}

        public IdentifierExp copy() {
            IdentifierExp that = new IdentifierExp();
            that.ident = this.ident;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DollarExp extends IdentifierExp
    {
        public  DollarExp(Loc loc) {
            super(loc, Id.dollar);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DollarExp() {}

        public DollarExp copy() {
            DollarExp that = new DollarExp();
            that.ident = this.ident;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DsymbolExp extends Expression
    {
        public Dsymbol s;
        public boolean hasOverloads;
        public  DsymbolExp(Loc loc, Dsymbol s, boolean hasOverloads) {
            super(loc, TOK.dSymbol, 29);
            this.s = s;
            this.hasOverloads = hasOverloads;
        }

        public  boolean isLvalue() {
            return true;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DsymbolExp() {}

        public DsymbolExp copy() {
            DsymbolExp that = new DsymbolExp();
            that.s = this.s;
            that.hasOverloads = this.hasOverloads;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ThisExp extends Expression
    {
        public VarDeclaration var;
        public  ThisExp(Loc loc) {
            super(loc, TOK.this_, 28);
        }

        public  ThisExp(Loc loc, byte tok) {
            super(loc, tok, 28);
        }

        public  Expression syntaxCopy() {
            ThisExp r = (ThisExp)super.syntaxCopy();
            r.type = null;
            r.var = null;
            return r;
        }

        public  boolean isBool(boolean result) {
            return result;
        }

        public  boolean isLvalue() {
            return (this.type.toBasetype().ty & 0xFF) != ENUMTY.Tclass;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            if ((this.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass)
            {
                return this.toLvalue(sc, e);
            }
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ThisExp() {}

        public ThisExp copy() {
            ThisExp that = new ThisExp();
            that.var = this.var;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SuperExp extends ThisExp
    {
        public  SuperExp(Loc loc) {
            super(loc, TOK.super_);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SuperExp() {}

        public SuperExp copy() {
            SuperExp that = new SuperExp();
            that.var = this.var;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class NullExp extends Expression
    {
        public byte committed;
        public  NullExp(Loc loc, Type type) {
            super(loc, TOK.null_, 25);
            this.type = type;
        }

        public  boolean equals(RootObject o) {
            {
                Expression e = isExpression(o);
                if (e != null)
                {
                    if (((e.op & 0xFF) == 13 && this.type.equals(e.type)))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public  boolean isBool(boolean result) {
            return result ? false : true;
        }

        public  StringExp toStringExp() {
            if ((this.implicitConvTo(Type.tstring)) != 0)
            {
                StringExp se = new StringExp(this.loc, ptr(new byte[1u]), 0);
                se.type = Type.tstring;
                return se;
            }
            return null;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public NullExp() {}

        public NullExp copy() {
            NullExp that = new NullExp();
            that.committed = this.committed;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class StringExp extends Expression
    {
        public BytePtr string;
        public CharPtr wstring;
        public IntPtr dstring;
        public int len;
        public byte sz = (byte)1;
        public byte committed;
        public byte postfix = (byte)0;
        public byte ownedByCtfe = OwnedBy.code;
        public  StringExp(Loc loc, BytePtr string) {
            super(loc, TOK.string_, 36);
            this.string = pcopy(string);
            this.len = strlen(string);
            this.sz = (byte)1;
        }

        public  StringExp(Loc loc, Object string, int len) {
            super(loc, TOK.string_, 36);
            this.string = pcopy(((BytePtr)string));
            this.len = len;
            this.sz = (byte)1;
        }

        public  StringExp(Loc loc, Object string, int len, byte postfix) {
            super(loc, TOK.string_, 36);
            this.string = pcopy(((BytePtr)string));
            this.len = len;
            this.postfix = postfix;
            this.sz = (byte)1;
        }

        public static StringExp create(Loc loc, BytePtr s) {
            return new StringExp(loc, s);
        }

        public static StringExp create(Loc loc, Object string, int len) {
            return new StringExp(loc, string, len);
        }

        public static void emplace(UnionExp pue, Loc loc, BytePtr s) {
            emplaceExpStringExpLocBytePtr(pue, loc, s);
        }

        public static void emplace(UnionExp pue, Loc loc, Object string, int len) {
            emplaceExpStringExpLocObjectInteger(pue, loc, string, len);
        }

        public  boolean equals(RootObject o) {
            {
                Expression e = isExpression(o);
                if (e != null)
                {
                    {
                        StringExp se = e.isStringExp();
                        if (se != null)
                        {
                            return this.comparex(se) == 0;
                        }
                    }
                }
            }
            return false;
        }

        public  int numberOfCodeUnits(int tynto) {
            int encSize = 0;
            switch (tynto)
            {
                case 0:
                    return this.len;
                case 31:
                    encSize = 1;
                    break;
                case 32:
                    encSize = 2;
                    break;
                case 33:
                    encSize = 4;
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            if ((this.sz & 0xFF) == encSize)
                return this.len;
            int result = 0;
            IntRef c = ref(0x0ffff);
            switch ((this.sz & 0xFF))
            {
                case 1:
                    {
                        IntRef u = ref(0);
                        for (; u.value < this.len;){
                            {
                                BytePtr p = pcopy(utf_decodeChar(this.string, this.len, u, c));
                                if (p != null)
                                {
                                    this.error(new BytePtr("%s"), p);
                                    return 0;
                                }
                            }
                            result += utf_codeLength(encSize, c.value);
                        }
                    }
                    break;
                case 2:
                    {
                        IntRef u_1 = ref(0);
                        for (; u_1.value < this.len;){
                            {
                                BytePtr p = pcopy(utf_decodeWchar(this.wstring, this.len, u_1, c));
                                if (p != null)
                                {
                                    this.error(new BytePtr("%s"), p);
                                    return 0;
                                }
                            }
                            result += utf_codeLength(encSize, c.value);
                        }
                    }
                    break;
                case 4:
                    {
                        int __key1322 = 0;
                        int __limit1323 = this.len;
                        for (; __key1322 < __limit1323;__key1322 += 1) {
                            int u_2 = __key1322;
                            result += utf_codeLength(encSize, this.dstring.get(u_2));
                        }
                    }
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            return result;
        }

        public  void writeTo(Object dest, boolean zero, int tyto) {
            int encSize = 0;
            switch (tyto)
            {
                case 0:
                    encSize = (this.sz & 0xFF);
                    break;
                case 31:
                    encSize = 1;
                    break;
                case 32:
                    encSize = 2;
                    break;
                case 33:
                    encSize = 4;
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            if ((this.sz & 0xFF) == encSize)
            {
                memcpy((BytePtr)dest, (this.string), (this.len * (this.sz & 0xFF)));
                if (zero)
                    memset(((BytePtr)dest).plus((this.len * (this.sz & 0xFF))), 0, (this.sz & 0xFF));
            }
            else
                throw new AssertionError("Unreachable code!");
        }

        public  int getCodeUnit(int i) {
            assert(i < this.len);
            switch ((this.sz & 0xFF))
            {
                case 1:
                    return (this.string.get(i) & 0xFF);
                case 2:
                    return (int)this.wstring.get(i);
                case 4:
                    return this.dstring.get(i);
                default:
                throw SwitchError.INSTANCE;
            }
        }

        public  void setCodeUnit(int i, int c) {
            assert(i < this.len);
            switch ((this.sz & 0xFF))
            {
                case 1:
                    this.string.set(i, (byte)c);
                    break;
                case 2:
                    this.wstring.set(i, (char)c);
                    break;
                case 4:
                    this.dstring.set(i, c);
                    break;
                default:
                throw SwitchError.INSTANCE;
            }
        }

        public  BytePtr toPtr() {
            return (this.sz & 0xFF) == 1 ? this.string : null;
        }

        public  StringExp toStringExp() {
            return this;
        }

        public  StringExp toUTF8(Scope sc) {
            if ((this.sz & 0xFF) != 1)
            {
                this.committed = (byte)0;
                Expression e = this.castTo(sc, Type.tchar.arrayOf());
                e = e.optimize(0, false);
                StringExp se = e.isStringExp();
                assert((se.sz & 0xFF) == 1);
                return se;
            }
            return this;
        }

        public  int comparex(StringExp se2) {
            int len1 = this.len;
            int len2 = se2.len;
            if (len1 == len2)
            {
                switch ((this.sz & 0xFF))
                {
                    case 1:
                        return memcmp(this.string, se2.string, len1);
                    case 2:
                        {
                            CharPtr s1 = pcopy(toCharPtr(this.string));
                            CharPtr s2_1 = pcopy(toCharPtr(se2.string));
                            {
                                int __key1324 = 0;
                                int __limit1325 = this.len;
                                for (; __key1324 < __limit1325;__key1324 += 1) {
                                    int u = __key1324;
                                    if ((int)s1.get(u) != (int)s2_1.get(u))
                                        return (int)s1.get(u) - (int)s2_1.get(u);
                                }
                            }
                        }
                        break;
                    case 4:
                        {
                            IntPtr s1_1 = pcopy(toIntPtr(this.string));
                            IntPtr s2 = pcopy(toIntPtr(se2.string));
                            {
                                int __key1326 = 0;
                                int __limit1327 = this.len;
                                for (; __key1326 < __limit1327;__key1326 += 1) {
                                    int u_1 = __key1326;
                                    if (s1_1.get(u_1) != s2.get(u_1))
                                        return (s1_1.get(u_1) - s2.get(u_1));
                                }
                            }
                        }
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
            }
            return (len1 - len2);
        }

        public  boolean isBool(boolean result) {
            return result;
        }

        public  boolean isLvalue() {
            return (this.type != null && (this.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray);
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            return (this.type != null && (this.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray) ? this : this.toLvalue(sc, e);
        }

        public  Expression modifiableLvalue(Scope sc, Expression e) {
            this.error(new BytePtr("cannot modify string literal `%s`"), this.toChars());
            return new ErrorExp();
        }

        public  int charAt(long i) {
            int value = 0;
            switch ((this.sz & 0xFF))
            {
                case 1:
                    value = (this.string.get((int)i) & 0xFF);
                    break;
                case 2:
                    value = (int)(toPtr<Integer>(this.string)).get((int)i);
                    break;
                case 4:
                    value = (toIntPtr(this.string)).get((int)i);
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            return value;
        }

        public  ByteSlice toStringz() {
            int nbytes = this.len * (this.sz & 0xFF);
            BytePtr s = pcopy((BytePtr)Mem.xmalloc(nbytes + (this.sz & 0xFF)));
            this.writeTo(s, true, 0);
            return s.slice(0,nbytes);
        }

        public  ByteSlice peekSlice() {
            assert((this.sz & 0xFF) == 1);
            return this.string.slice(0,this.len);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StringExp() {}

        public StringExp copy() {
            StringExp that = new StringExp();
            that.string = this.string;
            that.wstring = this.wstring;
            that.dstring = this.dstring;
            that.len = this.len;
            that.sz = this.sz;
            that.committed = this.committed;
            that.postfix = this.postfix;
            that.ownedByCtfe = this.ownedByCtfe;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class TupleExp extends Expression
    {
        public Expression e0;
        public DArray<Expression> exps;
        public  TupleExp(Loc loc, Expression e0, DArray<Expression> exps) {
            super(loc, TOK.tuple, 32);
            this.e0 = e0;
            this.exps = exps;
        }

        public  TupleExp(Loc loc, DArray<Expression> exps) {
            super(loc, TOK.tuple, 32);
            this.exps = exps;
        }

        public  TupleExp(Loc loc, TupleDeclaration tup) {
            super(loc, TOK.tuple, 32);
            this.exps = new DArray<Expression>();
            (this.exps).reserve((tup.objects).length);
            {
                Slice<RootObject> __r1328 = (tup.objects).opSlice().copy();
                int __key1329 = 0;
                for (; __key1329 < __r1328.getLength();__key1329 += 1) {
                    RootObject o = __r1328.get(__key1329);
                    {
                        Dsymbol s = getDsymbol(o);
                        if (s != null)
                        {
                            Expression e = new DsymbolExp(loc, s, true);
                            (this.exps).push(e);
                        }
                        else {
                            Expression eo = isExpression(o);
                            if (eo != null)
                            {
                                Expression e = eo.copy();
                                e.loc = loc.copy();
                                (this.exps).push(e);
                            }
                            else {
                                Type t = isType(o);
                                if (t != null)
                                {
                                    Expression e = new TypeExp(loc, t);
                                    (this.exps).push(e);
                                }
                                else
                                {
                                    this.error(new BytePtr("`%s` is not an expression"), o.toChars());
                                }
                            }
                        }
                    }
                }
            }
        }

        public static TupleExp create(Loc loc, DArray<Expression> exps) {
            return new TupleExp(loc, exps);
        }

        public  TupleExp toTupleExp() {
            return this;
        }

        public  Expression syntaxCopy() {
            return new TupleExp(this.loc, this.e0 != null ? this.e0.syntaxCopy() : null, Expression.arraySyntaxCopy(this.exps));
        }

        public  boolean equals(RootObject o) {
            if (pequals(this, o))
                return true;
            {
                Expression e = isExpression(o);
                if (e != null)
                    {
                        TupleExp te = e.isTupleExp();
                        if (te != null)
                        {
                            if ((this.exps).length != (te.exps).length)
                                return false;
                            if (((this.e0 != null && !(this.e0.equals(te.e0))) || (!(this.e0 != null) && te.e0 != null)))
                                return false;
                            {
                                Slice<Expression> __r1331 = (this.exps).opSlice().copy();
                                int __key1330 = 0;
                                for (; __key1330 < __r1331.getLength();__key1330 += 1) {
                                    Expression e1 = __r1331.get(__key1330);
                                    int i = __key1330;
                                    Expression e2 = (te.exps).get(i);
                                    if (!(e1.equals(e2)))
                                        return false;
                                }
                            }
                            return true;
                        }
                    }
            }
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TupleExp() {}

        public TupleExp copy() {
            TupleExp that = new TupleExp();
            that.e0 = this.e0;
            that.exps = this.exps;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ArrayLiteralExp extends Expression
    {
        public Expression basis;
        public DArray<Expression> elements;
        public byte ownedByCtfe = OwnedBy.code;
        public  ArrayLiteralExp(Loc loc, Type type, DArray<Expression> elements) {
            super(loc, TOK.arrayLiteral, 33);
            this.type = type;
            this.elements = elements;
        }

        public  ArrayLiteralExp(Loc loc, Type type, Expression e) {
            super(loc, TOK.arrayLiteral, 33);
            this.type = type;
            this.elements = new DArray<Expression>();
            (this.elements).push(e);
        }

        public  ArrayLiteralExp(Loc loc, Type type, Expression basis, DArray<Expression> elements) {
            super(loc, TOK.arrayLiteral, 33);
            this.type = type;
            this.basis = basis;
            this.elements = elements;
        }

        public static ArrayLiteralExp create(Loc loc, DArray<Expression> elements) {
            return new ArrayLiteralExp(loc, null, elements);
        }

        public static void emplace(UnionExp pue, Loc loc, DArray<Expression> elements) {
            emplaceExpArrayLiteralExpLocObjectDArray<Expression>(pue, loc, null, elements);
        }

        public  Expression syntaxCopy() {
            return new ArrayLiteralExp(this.loc, null, this.basis != null ? this.basis.syntaxCopy() : null, Expression.arraySyntaxCopy(this.elements));
        }

        public  boolean equals(RootObject o) {
            if (pequals(this, o))
                return true;
            Expression e = isExpression(o);
            if (!(e != null))
                return false;
            {
                ArrayLiteralExp ae = e.isArrayLiteralExp();
                if (ae != null)
                {
                    if ((this.elements).length != (ae.elements).length)
                        return false;
                    if (((this.elements).length == 0 && !(this.type.equals(ae.type))))
                    {
                        return false;
                    }
                    {
                        Slice<Expression> __r1333 = (this.elements).opSlice().copy();
                        int __key1332 = 0;
                        for (; __key1332 < __r1333.getLength();__key1332 += 1) {
                            Expression e1 = __r1333.get(__key1332);
                            int i = __key1332;
                            Expression e2 = (ae.elements).get(i);
                            if (!(e1 != null))
                                e1 = this.basis;
                            if (!(e2 != null))
                                e2 = ae.basis;
                            if ((!pequals(e1, e2) && ((!(e1 != null) || !(e2 != null)) || !(e1.equals(e2)))))
                                return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        public  Expression getElement(int i) {
            Expression el = (this.elements).get(i);
            return el != null ? el : this.basis;
        }

        public  boolean isBool(boolean result) {
            int dim = this.elements != null ? (this.elements).length : 0;
            return result ? dim != 0 : dim == 0;
        }

        public  StringExp toStringExp() {
            byte telem = this.type.nextOf().toBasetype().ty;
            if (((((telem & 0xFF) == ENUMTY.Tchar || (telem & 0xFF) == ENUMTY.Twchar) || (telem & 0xFF) == ENUMTY.Tdchar) || ((telem & 0xFF) == ENUMTY.Tvoid && (this.elements == null || (this.elements).length == 0))))
            {
                byte sz = (byte)1;
                if ((telem & 0xFF) == ENUMTY.Twchar)
                    sz = (byte)2;
                else if ((telem & 0xFF) == ENUMTY.Tdchar)
                    sz = (byte)4;
                OutBuffer buf = new OutBuffer();
                try {
                    if (this.elements != null)
                    {
                        {
                            int __key1334 = 0;
                            int __limit1335 = (this.elements).length;
                            for (; __key1334 < __limit1335;__key1334 += 1) {
                                int i = __key1334;
                                Expression ch = this.getElement(i);
                                if ((ch.op & 0xFF) != 135)
                                    return null;
                                if ((sz & 0xFF) == 1)
                                    buf.writeByte((int)ch.toInteger());
                                else if ((sz & 0xFF) == 2)
                                    buf.writeword((int)ch.toInteger());
                                else
                                    buf.write4((int)ch.toInteger());
                            }
                        }
                    }
                    byte prefix = (byte)255;
                    if ((sz & 0xFF) == 1)
                    {
                        prefix = (byte)99;
                        buf.writeByte(0);
                    }
                    else if ((sz & 0xFF) == 2)
                    {
                        prefix = (byte)119;
                        buf.writeword(0);
                    }
                    else
                    {
                        prefix = (byte)100;
                        buf.write4(0);
                    }
                    int len = buf.offset / (sz & 0xFF) - 1;
                    StringExp se = new StringExp(this.loc, buf.extractData(), len, prefix);
                    se.sz = sz;
                    se.type = this.type;
                    return se;
                }
                finally {
                }
            }
            return null;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ArrayLiteralExp() {}

        public ArrayLiteralExp copy() {
            ArrayLiteralExp that = new ArrayLiteralExp();
            that.basis = this.basis;
            that.elements = this.elements;
            that.ownedByCtfe = this.ownedByCtfe;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class AssocArrayLiteralExp extends Expression
    {
        public DArray<Expression> keys;
        public DArray<Expression> values;
        public byte ownedByCtfe = OwnedBy.code;
        public  AssocArrayLiteralExp(Loc loc, DArray<Expression> keys, DArray<Expression> values) {
            super(loc, TOK.assocArrayLiteral, 33);
            assert((keys).length == (values).length);
            this.keys = keys;
            this.values = values;
        }

        public  boolean equals(RootObject o) {
            if (pequals(this, o))
                return true;
            Expression e = isExpression(o);
            if (!(e != null))
                return false;
            {
                AssocArrayLiteralExp ae = e.isAssocArrayLiteralExp();
                if (ae != null)
                {
                    if ((this.keys).length != (ae.keys).length)
                        return false;
                    int count = 0;
                    {
                        Slice<Expression> __r1337 = (this.keys).opSlice().copy();
                        int __key1336 = 0;
                        for (; __key1336 < __r1337.getLength();__key1336 += 1) {
                            Expression key = __r1337.get(__key1336);
                            int i = __key1336;
                            {
                                Slice<Expression> __r1339 = (ae.keys).opSlice().copy();
                                int __key1338 = 0;
                                for (; __key1338 < __r1339.getLength();__key1338 += 1) {
                                    Expression akey = __r1339.get(__key1338);
                                    int j = __key1338;
                                    if (key.equals(akey))
                                    {
                                        if (!((this.values).get(i).equals((ae.values).get(j))))
                                            return false;
                                        count += 1;
                                    }
                                }
                            }
                        }
                    }
                    return count == (this.keys).length;
                }
            }
            return false;
        }

        public  Expression syntaxCopy() {
            return new AssocArrayLiteralExp(this.loc, Expression.arraySyntaxCopy(this.keys), Expression.arraySyntaxCopy(this.values));
        }

        public  boolean isBool(boolean result) {
            int dim = (this.keys).length;
            return result ? dim != 0 : dim == 0;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AssocArrayLiteralExp() {}

        public AssocArrayLiteralExp copy() {
            AssocArrayLiteralExp that = new AssocArrayLiteralExp();
            that.keys = this.keys;
            that.values = this.values;
            that.ownedByCtfe = this.ownedByCtfe;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    static int stageScrub = 1;
    static int stageSearchPointers = 2;
    static int stageOptimize = 4;
    static int stageApply = 8;
    static int stageInlineScan = 16;
    static int stageToCBuffer = 32;
    public static class StructLiteralExp extends Expression
    {
        public StructDeclaration sd;
        public DArray<Expression> elements;
        public Type stype;
        public Symbol sym;
        public StructLiteralExp origin;
        public StructLiteralExp inlinecopy;
        public int stageflags;
        public boolean useStaticInit;
        public byte ownedByCtfe = OwnedBy.code;
        public  StructLiteralExp(Loc loc, StructDeclaration sd, DArray<Expression> elements, Type stype) {
            super(loc, TOK.structLiteral, 54);
            this.sd = sd;
            if (elements == null)
                elements = new DArray<Expression>();
            this.elements = elements;
            this.stype = stype;
            this.origin = this;
        }

        public static StructLiteralExp create(Loc loc, StructDeclaration sd, Object elements, Type stype) {
            return new StructLiteralExp(loc, sd, (DArray<Expression>)elements, stype);
        }

        public  boolean equals(RootObject o) {
            if (pequals(this, o))
                return true;
            Expression e = isExpression(o);
            if (!(e != null))
                return false;
            {
                StructLiteralExp se = e.isStructLiteralExp();
                if (se != null)
                {
                    if (!(this.type.equals(se.type)))
                        return false;
                    if ((this.elements).length != (se.elements).length)
                        return false;
                    {
                        Slice<Expression> __r1341 = (this.elements).opSlice().copy();
                        int __key1340 = 0;
                        for (; __key1340 < __r1341.getLength();__key1340 += 1) {
                            Expression e1 = __r1341.get(__key1340);
                            int i = __key1340;
                            Expression e2 = (se.elements).get(i);
                            if ((!pequals(e1, e2) && ((!(e1 != null) || !(e2 != null)) || !(e1.equals(e2)))))
                                return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        public  Expression syntaxCopy() {
            StructLiteralExp exp = new StructLiteralExp(this.loc, this.sd, Expression.arraySyntaxCopy(this.elements), this.type != null ? this.type : this.stype);
            exp.origin = this;
            return exp;
        }

        public  Expression getField(Type type, int offset) {
            Expression e = null;
            int i = this.getFieldIndex(type, offset);
            if (i != -1)
            {
                if (i >= this.sd.nonHiddenFields())
                    return null;
                assert(i < (this.elements).length);
                e = (this.elements).get(i);
                if (e != null)
                {
                    TypeSArray tsa = type.isTypeSArray();
                    if ((tsa != null && !pequals(e.type.castMod((byte)0), type.castMod((byte)0))))
                    {
                        int length = (int)tsa.dim.toInteger();
                        DArray<Expression> z = new DArray<Expression>(length);
                        {
                            Slice<Expression> __r1342 = (z).opSlice().copy();
                            int __key1343 = 0;
                            for (; __key1343 < __r1342.getLength();__key1343 += 1) {
                                Expression q = __r1342.get(__key1343);
                                q = e.copy();
                            }
                        }
                        e = new ArrayLiteralExp(this.loc, type, z);
                    }
                    else
                    {
                        e = e.copy();
                        e.type = type;
                    }
                    if ((this.useStaticInit && e.type.needsNested()))
                        {
                            StructLiteralExp se = e.isStructLiteralExp();
                            if (se != null)
                            {
                                se.useStaticInit = true;
                            }
                        }
                }
            }
            return e;
        }

        public  int getFieldIndex(Type type, int offset) {
            if (((this.elements).length) != 0)
            {
                {
                    Slice<VarDeclaration> __r1345 = this.sd.fields.opSlice().copy();
                    int __key1344 = 0;
                    for (; __key1344 < __r1345.getLength();__key1344 += 1) {
                        VarDeclaration v = __r1345.get(__key1344);
                        int i = __key1344;
                        if ((offset == v.offset && type.size() == v.type.size()))
                        {
                            if (i >= this.sd.nonHiddenFields())
                                return i;
                            {
                                Expression e = (this.elements).get(i);
                                if (e != null)
                                {
                                    return i;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            return -1;
        }

        public  Expression addDtorHook(Scope sc) {
            if ((this.sd.dtor != null && (sc).func != null))
            {
                int len = 10;
                ByteSlice buf = new ByteSlice(new byte[11]);
                buf.set(10, (byte)0);
                strcpy(ptr(buf), new BytePtr("__sl"));
                strncat(ptr(buf), this.sd.ident.toChars(), 5);
                assert((buf.get(10) & 0xFF) == 0);
                VarDeclaration tmp = copyToTemp(0L, ptr(buf), this);
                Expression ae = new DeclarationExp(this.loc, tmp);
                Expression e = new CommaExp(this.loc, ae, new VarExp(this.loc, tmp, true), true);
                e = expressionSemantic(e, sc);
                return e;
            }
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StructLiteralExp() {}

        public StructLiteralExp copy() {
            StructLiteralExp that = new StructLiteralExp();
            that.sd = this.sd;
            that.elements = this.elements;
            that.stype = this.stype;
            that.sym = this.sym;
            that.origin = this.origin;
            that.inlinecopy = this.inlinecopy;
            that.stageflags = this.stageflags;
            that.useStaticInit = this.useStaticInit;
            that.ownedByCtfe = this.ownedByCtfe;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class TypeExp extends Expression
    {
        public  TypeExp(Loc loc, Type type) {
            super(loc, TOK.type, 24);
            this.type = type;
        }

        public  Expression syntaxCopy() {
            return new TypeExp(this.loc, this.type.syntaxCopy());
        }

        public  boolean checkType() {
            this.error(new BytePtr("type `%s` is not an expression"), this.toChars());
            return true;
        }

        public  boolean checkValue() {
            this.error(new BytePtr("type `%s` has no value"), this.toChars());
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeExp() {}

        public TypeExp copy() {
            TypeExp that = new TypeExp();
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ScopeExp extends Expression
    {
        public ScopeDsymbol sds;
        public  ScopeExp(Loc loc, ScopeDsymbol sds) {
            super(loc, TOK.scope_, 28);
            this.sds = sds;
            assert(!(sds.isTemplateDeclaration() != null));
        }

        public  Expression syntaxCopy() {
            return new ScopeExp(this.loc, (ScopeDsymbol)this.sds.syntaxCopy(null));
        }

        public  boolean checkType() {
            if (this.sds.isPackage() != null)
            {
                this.error(new BytePtr("%s `%s` has no type"), this.sds.kind(), this.sds.toChars());
                return true;
            }
            {
                TemplateInstance ti = this.sds.isTemplateInstance();
                if (ti != null)
                {
                    if (((ti.tempdecl != null && ti.semantictiargsdone) && ti.semanticRun == PASS.init))
                    {
                        this.error(new BytePtr("partial %s `%s` has no type"), this.sds.kind(), this.toChars());
                        return true;
                    }
                }
            }
            return false;
        }

        public  boolean checkValue() {
            this.error(new BytePtr("%s `%s` has no value"), this.sds.kind(), this.sds.toChars());
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ScopeExp() {}

        public ScopeExp copy() {
            ScopeExp that = new ScopeExp();
            that.sds = this.sds;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class TemplateExp extends Expression
    {
        public TemplateDeclaration td;
        public FuncDeclaration fd;
        public  TemplateExp(Loc loc, TemplateDeclaration td, FuncDeclaration fd) {
            super(loc, TOK.template_, 32);
            this.td = td;
            this.fd = fd;
        }

        public  boolean isLvalue() {
            return this.fd != null;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            if (!(this.fd != null))
                return this.toLvalue(sc, e);
            assert(sc != null);
            return symbolToExp(this.fd, this.loc, sc, true);
        }

        public  boolean checkType() {
            this.error(new BytePtr("%s `%s` has no type"), this.td.kind(), this.toChars());
            return true;
        }

        public  boolean checkValue() {
            this.error(new BytePtr("%s `%s` has no value"), this.td.kind(), this.toChars());
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateExp() {}

        public TemplateExp copy() {
            TemplateExp that = new TemplateExp();
            that.td = this.td;
            that.fd = this.fd;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class NewExp extends Expression
    {
        public Expression thisexp;
        public DArray<Expression> newargs;
        public Type newtype;
        public DArray<Expression> arguments;
        public Expression argprefix;
        public CtorDeclaration member;
        public NewDeclaration allocator;
        public boolean onstack;
        public boolean thrownew;
        public  NewExp(Loc loc, Expression thisexp, DArray<Expression> newargs, Type newtype, DArray<Expression> arguments) {
            super(loc, TOK.new_, 54);
            this.thisexp = thisexp;
            this.newargs = newargs;
            this.newtype = newtype;
            this.arguments = arguments;
        }

        public static NewExp create(Loc loc, Expression thisexp, DArray<Expression> newargs, Type newtype, DArray<Expression> arguments) {
            return new NewExp(loc, thisexp, newargs, newtype, arguments);
        }

        public  Expression syntaxCopy() {
            return new NewExp(this.loc, this.thisexp != null ? this.thisexp.syntaxCopy() : null, Expression.arraySyntaxCopy(this.newargs), this.newtype.syntaxCopy(), Expression.arraySyntaxCopy(this.arguments));
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public NewExp() {}

        public NewExp copy() {
            NewExp that = new NewExp();
            that.thisexp = this.thisexp;
            that.newargs = this.newargs;
            that.newtype = this.newtype;
            that.arguments = this.arguments;
            that.argprefix = this.argprefix;
            that.member = this.member;
            that.allocator = this.allocator;
            that.onstack = this.onstack;
            that.thrownew = this.thrownew;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class NewAnonClassExp extends Expression
    {
        public Expression thisexp;
        public DArray<Expression> newargs;
        public ClassDeclaration cd;
        public DArray<Expression> arguments;
        public  NewAnonClassExp(Loc loc, Expression thisexp, DArray<Expression> newargs, ClassDeclaration cd, DArray<Expression> arguments) {
            super(loc, TOK.newAnonymousClass, 40);
            this.thisexp = thisexp;
            this.newargs = newargs;
            this.cd = cd;
            this.arguments = arguments;
        }

        public  Expression syntaxCopy() {
            return new NewAnonClassExp(this.loc, this.thisexp != null ? this.thisexp.syntaxCopy() : null, Expression.arraySyntaxCopy(this.newargs), (ClassDeclaration)this.cd.syntaxCopy(null), Expression.arraySyntaxCopy(this.arguments));
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public NewAnonClassExp() {}

        public NewAnonClassExp copy() {
            NewAnonClassExp that = new NewAnonClassExp();
            that.thisexp = this.thisexp;
            that.newargs = this.newargs;
            that.cd = this.cd;
            that.arguments = this.arguments;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SymbolExp extends Expression
    {
        public Declaration var;
        public boolean hasOverloads;
        public Dsymbol originalScope;
        public  SymbolExp(Loc loc, byte op, int size, Declaration var, boolean hasOverloads) {
            super(loc, op, size);
            assert(var != null);
            this.var = var;
            this.hasOverloads = hasOverloads;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SymbolExp() {}

        public SymbolExp copy() {
            SymbolExp that = new SymbolExp();
            that.var = this.var;
            that.hasOverloads = this.hasOverloads;
            that.originalScope = this.originalScope;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SymOffExp extends SymbolExp
    {
        public long offset;
        public  SymOffExp(Loc loc, Declaration var, long offset, boolean hasOverloads) {
            {
                VarDeclaration v = var.isVarDeclaration();
                if (v != null)
                {
                    if (v.needThis())
                        error(loc, new BytePtr("need `this` for address of `%s`"), v.toChars());
                    hasOverloads = false;
                }
            }
            super(loc, TOK.symbolOffset, 44, var, hasOverloads);
            this.offset = offset;
        }

        public  boolean isBool(boolean result) {
            return result ? true : false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SymOffExp() {}

        public SymOffExp copy() {
            SymOffExp that = new SymOffExp();
            that.offset = this.offset;
            that.var = this.var;
            that.hasOverloads = this.hasOverloads;
            that.originalScope = this.originalScope;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class VarExp extends SymbolExp
    {
        public  VarExp(Loc loc, Declaration var, boolean hasOverloads) {
            if (var.isVarDeclaration() != null)
                hasOverloads = false;
            super(loc, TOK.variable, 36, var, hasOverloads);
            this.type = var.type;
        }

        public static VarExp create(Loc loc, Declaration var, boolean hasOverloads) {
            return new VarExp(loc, var, hasOverloads);
        }

        public  boolean equals(RootObject o) {
            if (pequals(this, o))
                return true;
            {
                VarExp ne = isExpression(o).isVarExp();
                if (ne != null)
                {
                    if ((this.type.toHeadMutable().equals(ne.type.toHeadMutable()) && pequals(this.var, ne.var)))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public  int checkModifiable(Scope sc, int flag) {
            assert(this.type != null);
            return this.var.checkModify(this.loc, sc, null, flag);
        }

        public  boolean isLvalue() {
            if ((this.var.storage_class & 2199031652352L) != 0)
                return false;
            return true;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            if ((this.var.storage_class & 8388608L) != 0)
            {
                this.error(new BytePtr("manifest constant `%s` cannot be modified"), this.var.toChars());
                return new ErrorExp();
            }
            if ((this.var.storage_class & 8192L) != 0)
            {
                this.error(new BytePtr("lazy variable `%s` cannot be modified"), this.var.toChars());
                return new ErrorExp();
            }
            if (pequals(this.var.ident, Id.ctfe))
            {
                this.error(new BytePtr("cannot modify compiler-generated variable `__ctfe`"));
                return new ErrorExp();
            }
            if (pequals(this.var.ident, Id.dollar))
            {
                this.error(new BytePtr("cannot modify operator `$`"));
                return new ErrorExp();
            }
            return this;
        }

        public  Expression modifiableLvalue(Scope sc, Expression e) {
            if ((this.var.storage_class & 8388608L) != 0)
            {
                this.error(new BytePtr("cannot modify manifest constant `%s`"), this.toChars());
                return new ErrorExp();
            }
            return this.modifiableLvalue(sc, e);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public  Expression syntaxCopy() {
            Expression ret = super.syntaxCopy();
            return ret;
        }


        public VarExp() {}

        public VarExp copy() {
            VarExp that = new VarExp();
            that.var = this.var;
            that.hasOverloads = this.hasOverloads;
            that.originalScope = this.originalScope;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class OverExp extends Expression
    {
        public OverloadSet vars;
        public  OverExp(Loc loc, OverloadSet s) {
            super(loc, TOK.overloadSet, 28);
            this.vars = s;
            this.type = Type.tvoid;
        }

        public  boolean isLvalue() {
            return true;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public OverExp() {}

        public OverExp copy() {
            OverExp that = new OverExp();
            that.vars = this.vars;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class FuncExp extends Expression
    {
        public FuncLiteralDeclaration fd;
        public TemplateDeclaration td;
        public byte tok;
        public  FuncExp(Loc loc, Dsymbol s) {
            super(loc, TOK.function_, 33);
            this.td = s.isTemplateDeclaration();
            this.fd = s.isFuncLiteralDeclaration();
            if (this.td != null)
            {
                assert(this.td.literal);
                assert((this.td.members != null && (this.td.members).length == 1));
                this.fd = (this.td.members).get(0).isFuncLiteralDeclaration();
            }
            this.tok = this.fd.tok;
            assert(this.fd.fbody != null);
        }

        public  boolean equals(RootObject o) {
            if (pequals(this, o))
                return true;
            Expression e = isExpression(o);
            if (!(e != null))
                return false;
            {
                FuncExp fe = e.isFuncExp();
                if (fe != null)
                {
                    return pequals(this.fd, fe.fd);
                }
            }
            return false;
        }

        public  void genIdent(Scope sc) {
            if (pequals(this.fd.ident, Id.empty))
            {
                BytePtr s = null;
                if (this.fd.fes != null)
                    s = pcopy(new BytePtr("__foreachbody"));
                else if ((this.fd.tok & 0xFF) == 0)
                    s = pcopy(new BytePtr("__lambda"));
                else if ((this.fd.tok & 0xFF) == 160)
                    s = pcopy(new BytePtr("__dgliteral"));
                else
                    s = pcopy(new BytePtr("__funcliteral"));
                DsymbolTable symtab = null;
                {
                    FuncDeclaration func = (sc).parent.isFuncDeclaration();
                    if (func != null)
                    {
                        if (func.localsymtab == null)
                        {
                            func.localsymtab = new DsymbolTable();
                        }
                        symtab = func.localsymtab;
                    }
                    else
                    {
                        ScopeDsymbol sds = (sc).parent.isScopeDsymbol();
                        if (!(sds.symtab != null))
                        {
                            assert(sds.isTemplateInstance() != null);
                            sds.symtab = new DsymbolTable();
                        }
                        symtab = sds.symtab;
                    }
                }
                assert(symtab != null);
                Identifier id = Identifier.generateId(s, symtab.len() + 1);
                this.fd.ident = id;
                if (this.td != null)
                    this.td.ident = id;
                symtab.insert(this.td != null ? this.td : this.fd);
            }
        }

        public  Expression syntaxCopy() {
            if (this.td != null)
                return new FuncExp(this.loc, this.td.syntaxCopy(null));
            else if (this.fd.semanticRun == PASS.init)
                return new FuncExp(this.loc, this.fd.syntaxCopy(null));
            else
                return new FuncExp(this.loc, this.fd);
        }

        public  int matchType(Type to, Scope sc, Ptr<FuncExp> presult, int flag) {
            Function3<Expression,Type,Integer,Integer> cannotInfer = new Function3<Expression,Type,Integer,Integer>(){
                public Integer invoke(Expression e, Type to, Integer flag){
                    if (!((flag) != 0))
                        e.error(new BytePtr("cannot infer parameter types from `%s`"), to.toChars());
                    return MATCH.nomatch;
                }
            };
            if (presult != null)
                presult.set(0, null);
            TypeFunction tof = null;
            if ((to.ty & 0xFF) == ENUMTY.Tdelegate)
            {
                if ((this.tok & 0xFF) == 161)
                {
                    if (!((flag) != 0))
                        this.error(new BytePtr("cannot match function literal to delegate type `%s`"), to.toChars());
                    return MATCH.nomatch;
                }
                tof = (TypeFunction)to.nextOf();
            }
            else if (((to.ty & 0xFF) == ENUMTY.Tpointer && (tof = to.nextOf().isTypeFunction()) != null))
            {
                if ((this.tok & 0xFF) == 160)
                {
                    if (!((flag) != 0))
                        this.error(new BytePtr("cannot match delegate literal to function pointer type `%s`"), to.toChars());
                    return MATCH.nomatch;
                }
            }
            if (this.td != null)
            {
                if (!(tof != null))
                {
                    return cannotInfer.invoke(this, to, flag);
                }
                assert(this.td._scope != null);
                TypeFunction tf = this.fd.type.isTypeFunction();
                int dim = tf.parameterList.length();
                if ((tof.parameterList.length() != dim || tof.parameterList.varargs != tf.parameterList.varargs))
                    return cannotInfer.invoke(this, to, flag);
                DArray<RootObject> tiargs = new DArray<RootObject>();
                (tiargs).reserve((this.td.parameters).length);
                {
                    Slice<TemplateParameter> __r1346 = (this.td.parameters).opSlice().copy();
                    int __key1347 = 0;
                    for (; __key1347 < __r1346.getLength();__key1347 += 1) {
                        TemplateParameter tp = __r1346.get(__key1347);
                        int u = 0;
                        for (; u < dim;u++){
                            Parameter p = tf.parameterList.get(u);
                            {
                                TypeIdentifier ti = p.type.isTypeIdentifier();
                                if (ti != null)
                                    if ((ti != null && pequals(ti.ident, tp.ident)))
                                    {
                                        break;
                                    }
                            }
                        }
                        assert(u < dim);
                        Parameter pto = tof.parameterList.get(u);
                        Type t = pto.type;
                        if ((t.ty & 0xFF) == ENUMTY.Terror)
                            return cannotInfer.invoke(this, to, flag);
                        (tiargs).push(t);
                    }
                }
                if ((!(tf.next != null) && tof.next != null))
                    this.fd.treq = to;
                TemplateInstance ti = new TemplateInstance(this.loc, this.td, tiargs);
                Expression ex = expressionSemantic(new ScopeExp(this.loc, ti), this.td._scope);
                this.fd.treq = null;
                if ((ex.op & 0xFF) == 127)
                    return MATCH.nomatch;
                {
                    FuncExp ef = ex.isFuncExp();
                    if (ef != null)
                        return ef.matchType(to, sc, presult, flag);
                    else
                        return cannotInfer.invoke(this, to, flag);
                }
            }
            if ((!(tof != null) || !(tof.next != null)))
                return MATCH.nomatch;
            assert((this.type != null && !pequals(this.type, Type.tvoid)));
            if ((this.fd.type.ty & 0xFF) == ENUMTY.Terror)
                return MATCH.nomatch;
            TypeFunction tfx = this.fd.type.isTypeFunction();
            boolean convertMatch = (this.type.ty & 0xFF) != (to.ty & 0xFF);
            if ((this.fd.inferRetType && tfx.next.implicitConvTo(tof.next) == MATCH.convert))
            {
                convertMatch = true;
                TypeFunction tfy = new TypeFunction(tfx.parameterList, tof.next, tfx.linkage, 0L);
                tfy.mod = tfx.mod;
                tfy.isnothrow = tfx.isnothrow;
                tfy.isnogc = tfx.isnogc;
                tfy.purity = tfx.purity;
                tfy.isproperty = tfx.isproperty;
                tfy.isref = tfx.isref;
                tfy.iswild = tfx.iswild;
                tfy.deco = pcopy(merge(tfy).deco);
                tfx = tfy;
            }
            Type tx = null;
            if (((this.tok & 0xFF) == 160 || ((this.tok & 0xFF) == 0 && ((this.type.ty & 0xFF) == ENUMTY.Tdelegate || ((this.type.ty & 0xFF) == ENUMTY.Tpointer && (to.ty & 0xFF) == ENUMTY.Tdelegate)))))
            {
                tx = new TypeDelegate(tfx);
                tx.deco = pcopy(merge(tx).deco);
            }
            else
            {
                assert(((this.tok & 0xFF) == 161 || ((this.tok & 0xFF) == 0 && (this.type.ty & 0xFF) == ENUMTY.Tpointer)));
                tx = tfx.pointerTo();
            }
            int m = tx.implicitConvTo(to);
            if (m > MATCH.nomatch)
            {
                m = convertMatch ? MATCH.convert : tx.equals(to) ? MATCH.exact : MATCH.constant;
                if (presult != null)
                {
                    presult.set(0, ((FuncExp)this.copy()));
                    (presult.get()).type = to;
                    (presult.get()).fd.modifyReturns(sc, tof.next);
                }
            }
            else if (!((flag) != 0))
            {
                Slice<BytePtr> ts = toAutoQualChars(tx, to);
                this.error(new BytePtr("cannot implicitly convert expression `%s` of type `%s` to `%s`"), this.toChars(), ts.get(0), ts.get(1));
            }
            return m;
        }

        public  BytePtr toChars() {
            return this.fd.toChars();
        }

        public  boolean checkType() {
            if (this.td != null)
            {
                this.error(new BytePtr("template lambda has no type"));
                return true;
            }
            return false;
        }

        public  boolean checkValue() {
            if (this.td != null)
            {
                this.error(new BytePtr("template lambda has no value"));
                return true;
            }
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public FuncExp() {}

        public FuncExp copy() {
            FuncExp that = new FuncExp();
            that.fd = this.fd;
            that.td = this.td;
            that.tok = this.tok;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DeclarationExp extends Expression
    {
        public Dsymbol declaration;
        public  DeclarationExp(Loc loc, Dsymbol declaration) {
            super(loc, TOK.declaration, 28);
            this.declaration = declaration;
        }

        public  Expression syntaxCopy() {
            return new DeclarationExp(this.loc, this.declaration.syntaxCopy(null));
        }

        public  boolean hasCode() {
            {
                VarDeclaration vd = this.declaration.isVarDeclaration();
                if (vd != null)
                {
                    return !((vd.storage_class & 8388609L) != 0);
                }
            }
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DeclarationExp() {}

        public DeclarationExp copy() {
            DeclarationExp that = new DeclarationExp();
            that.declaration = this.declaration;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class TypeidExp extends Expression
    {
        public RootObject obj;
        public  TypeidExp(Loc loc, RootObject o) {
            super(loc, TOK.typeid_, 28);
            this.obj = o;
        }

        public  Expression syntaxCopy() {
            return new TypeidExp(this.loc, objectSyntaxCopy(this.obj));
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeidExp() {}

        public TypeidExp copy() {
            TypeidExp that = new TypeidExp();
            that.obj = this.obj;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class TraitsExp extends Expression
    {
        public Identifier ident;
        public DArray<RootObject> args;
        public  TraitsExp(Loc loc, Identifier ident, DArray<RootObject> args) {
            super(loc, TOK.traits, 32);
            this.ident = ident;
            this.args = args;
        }

        public  Expression syntaxCopy() {
            return new TraitsExp(this.loc, this.ident, TemplateInstance.arraySyntaxCopy(this.args));
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TraitsExp() {}

        public TraitsExp copy() {
            TraitsExp that = new TraitsExp();
            that.ident = this.ident;
            that.args = this.args;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class HaltExp extends Expression
    {
        public  HaltExp(Loc loc) {
            super(loc, TOK.halt, 24);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public HaltExp() {}

        public HaltExp copy() {
            HaltExp that = new HaltExp();
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class IsExp extends Expression
    {
        public Type targ;
        public Identifier id;
        public Type tspec;
        public DArray<TemplateParameter> parameters;
        public byte tok;
        public byte tok2;
        public  IsExp(Loc loc, Type targ, Identifier id, byte tok, Type tspec, byte tok2, DArray<TemplateParameter> parameters) {
            super(loc, TOK.is_, 42);
            this.targ = targ;
            this.id = id;
            this.tok = tok;
            this.tspec = tspec;
            this.tok2 = tok2;
            this.parameters = parameters;
        }

        public  Expression syntaxCopy() {
            DArray<TemplateParameter> p = null;
            if (this.parameters != null)
            {
                p = new DArray<TemplateParameter>((this.parameters).length);
                {
                    Slice<TemplateParameter> __r1349 = (this.parameters).opSlice().copy();
                    int __key1348 = 0;
                    for (; __key1348 < __r1349.getLength();__key1348 += 1) {
                        TemplateParameter el = __r1349.get(__key1348);
                        int i = __key1348;
                        p.set(i, el.syntaxCopy());
                    }
                }
            }
            return new IsExp(this.loc, this.targ.syntaxCopy(), this.id, this.tok, this.tspec != null ? this.tspec.syntaxCopy() : null, this.tok2, p);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public IsExp() {}

        public IsExp copy() {
            IsExp that = new IsExp();
            that.targ = this.targ;
            that.id = this.id;
            that.tspec = this.tspec;
            that.parameters = this.parameters;
            that.tok = this.tok;
            that.tok2 = this.tok2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static abstract class UnaExp extends Expression
    {
        public Expression e1;
        public Type att1;
        public  UnaExp(Loc loc, byte op, int size, Expression e1) {
            super(loc, op, size);
            this.e1 = e1;
        }

        public  Expression syntaxCopy() {
            UnaExp e = (UnaExp)this.copy();
            e.type = null;
            e.e1 = e.e1.syntaxCopy();
            return e;
        }

        public  Expression incompatibleTypes() {
            if (pequals(this.e1.type.toBasetype(), Type.terror))
                return this.e1;
            if ((this.e1.op & 0xFF) == 20)
            {
                this.error(new BytePtr("incompatible type for `%s(%s)`: cannot use `%s` with types"), Token.toChars(this.op), this.e1.toChars(), Token.toChars(this.op));
            }
            else
            {
                this.error(new BytePtr("incompatible type for `%s(%s)`: `%s`"), Token.toChars(this.op), this.e1.toChars(), this.e1.type.toChars());
            }
            return new ErrorExp();
        }

        public  void setNoderefOperand() {
            {
                DotIdExp edi = this.e1.isDotIdExp();
                if (edi != null)
                    edi.noderef = true;
            }
        }

        public  Expression resolveLoc(Loc loc, Scope sc) {
            this.e1 = this.e1.resolveLoc(loc, sc);
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UnaExp() {}

        public abstract UnaExp copy();
    }
    public static abstract class BinExp extends Expression
    {
        public Expression e1;
        public Expression e2;
        public Type att1;
        public Type att2;
        public  BinExp(Loc loc, byte op, int size, Expression e1, Expression e2) {
            super(loc, op, size);
            this.e1 = e1;
            this.e2 = e2;
        }

        public  Expression syntaxCopy() {
            BinExp e = (BinExp)this.copy();
            e.type = null;
            e.e1 = e.e1.syntaxCopy();
            e.e2 = e.e2.syntaxCopy();
            return e;
        }

        public  Expression incompatibleTypes() {
            if (pequals(this.e1.type.toBasetype(), Type.terror))
                return this.e1;
            if (pequals(this.e2.type.toBasetype(), Type.terror))
                return this.e2;
            byte thisOp = (this.op & 0xFF) == 100 ? TOK.colon : (byte)(this.op & 0xFF);
            if (((this.e1.op & 0xFF) == 20 || (this.e2.op & 0xFF) == 20))
            {
                this.error(new BytePtr("incompatible types for `(%s) %s (%s)`: cannot use `%s` with types"), this.e1.toChars(), Token.toChars(thisOp), this.e2.toChars(), Token.toChars(this.op));
            }
            else if (this.e1.type.equals(this.e2.type))
            {
                this.error(new BytePtr("incompatible types for `(%s) %s (%s)`: both operands are of type `%s`"), this.e1.toChars(), Token.toChars(thisOp), this.e2.toChars(), this.e1.type.toChars());
            }
            else
            {
                Slice<BytePtr> ts = toAutoQualChars(this.e1.type, this.e2.type);
                this.error(new BytePtr("incompatible types for `(%s) %s (%s)`: `%s` and `%s`"), this.e1.toChars(), Token.toChars(thisOp), this.e2.toChars(), ts.get(0), ts.get(1));
            }
            return new ErrorExp();
        }

        public  Expression checkOpAssignTypes(Scope sc) {
            Type t1 = this.e1.type;
            Type t2 = this.e2.type;
            if (((((((this.op & 0xFF) == 76 || (this.op & 0xFF) == 77) || (this.op & 0xFF) == 81) || (this.op & 0xFF) == 82) || (this.op & 0xFF) == 83) || (this.op & 0xFF) == 227))
            {
                if ((this.type.isintegral() && t2.isfloating()))
                {
                    this.warning(new BytePtr("`%s %s %s` is performing truncating conversion"), this.type.toChars(), Token.toChars(this.op), t2.toChars());
                }
            }
            if ((((this.op & 0xFF) == 81 || (this.op & 0xFF) == 82) || (this.op & 0xFF) == 83))
            {
                BytePtr opstr = pcopy(Token.toChars(this.op));
                if ((t1.isreal() && t2.iscomplex()))
                {
                    this.error(new BytePtr("`%s %s %s` is undefined. Did you mean `%s %s %s.re`?"), t1.toChars(), opstr, t2.toChars(), t1.toChars(), opstr, t2.toChars());
                    return new ErrorExp();
                }
                else if ((t1.isimaginary() && t2.iscomplex()))
                {
                    this.error(new BytePtr("`%s %s %s` is undefined. Did you mean `%s %s %s.im`?"), t1.toChars(), opstr, t2.toChars(), t1.toChars(), opstr, t2.toChars());
                    return new ErrorExp();
                }
                else if (((t1.isreal() || t1.isimaginary()) && t2.isimaginary()))
                {
                    this.error(new BytePtr("`%s %s %s` is an undefined operation"), t1.toChars(), opstr, t2.toChars());
                    return new ErrorExp();
                }
            }
            if (((this.op & 0xFF) == 76 || (this.op & 0xFF) == 77))
            {
                if (((t1.isreal() && (t2.isimaginary() || t2.iscomplex())) || (t1.isimaginary() && (t2.isreal() || t2.iscomplex()))))
                {
                    this.error(new BytePtr("`%s %s %s` is undefined (result is complex)"), t1.toChars(), Token.toChars(this.op), t2.toChars());
                    return new ErrorExp();
                }
                if ((this.type.isreal() || this.type.isimaginary()))
                {
                    assert(((global.errors) != 0 || t2.isfloating()));
                    this.e2 = this.e2.castTo(sc, t1);
                }
            }
            if ((this.op & 0xFF) == 81)
            {
                if (t2.isfloating())
                {
                    if (t1.isreal())
                    {
                        if ((t2.isimaginary() || t2.iscomplex()))
                        {
                            this.e2 = this.e2.castTo(sc, t1);
                        }
                    }
                    else if (t1.isimaginary())
                    {
                        if ((t2.isimaginary() || t2.iscomplex()))
                        {
                            switch ((t1.ty & 0xFF))
                            {
                                case 24:
                                    t2 = Type.tfloat32;
                                    break;
                                case 25:
                                    t2 = Type.tfloat64;
                                    break;
                                case 26:
                                    t2 = Type.tfloat80;
                                    break;
                                default:
                                throw new AssertionError("Unreachable code!");
                            }
                            this.e2 = this.e2.castTo(sc, t2);
                        }
                    }
                }
            }
            else if ((this.op & 0xFF) == 82)
            {
                if (t2.isimaginary())
                {
                    if (t1.isreal())
                    {
                        this.e2 = new CommaExp(this.loc, this.e2, new RealExp(this.loc, CTFloat.zero, t1), true);
                        this.e2.type = t1;
                        Expression e = new AssignExp(this.loc, this.e1, this.e2);
                        e.type = t1;
                        return e;
                    }
                    else if (t1.isimaginary())
                    {
                        Type t3 = null;
                        switch ((t1.ty & 0xFF))
                        {
                            case 24:
                                t3 = Type.tfloat32;
                                break;
                            case 25:
                                t3 = Type.tfloat64;
                                break;
                            case 26:
                                t3 = Type.tfloat80;
                                break;
                            default:
                            throw new AssertionError("Unreachable code!");
                        }
                        this.e2 = this.e2.castTo(sc, t3);
                        Expression e = new AssignExp(this.loc, this.e1, this.e2);
                        e.type = t1;
                        return e;
                    }
                }
            }
            else if ((this.op & 0xFF) == 83)
            {
                if (t2.iscomplex())
                {
                    this.error(new BytePtr("cannot perform modulo complex arithmetic"));
                    return new ErrorExp();
                }
            }
            return this;
        }

        public  boolean checkIntegralBin() {
            boolean r1 = this.e1.checkIntegral();
            boolean r2 = this.e2.checkIntegral();
            return (r1 || r2);
        }

        public  boolean checkArithmeticBin() {
            boolean r1 = this.e1.checkArithmetic();
            boolean r2 = this.e2.checkArithmetic();
            return (r1 || r2);
        }

        public  void setNoderefOperands() {
            {
                DotIdExp edi = this.e1.isDotIdExp();
                if (edi != null)
                    edi.noderef = true;
            }
            {
                DotIdExp edi = this.e2.isDotIdExp();
                if (edi != null)
                    edi.noderef = true;
            }
        }

        public  Expression reorderSettingAAElem(Scope sc) {
            BinExp be = this;
            IndexExp ie = be.e1.isIndexExp();
            if (!(ie != null))
                return be;
            if ((ie.e1.type.toBasetype().ty & 0xFF) != ENUMTY.Taarray)
                return be;
            Ref<Expression> e0 = ref(null);
            for (; (1) != 0;){
                Ref<Expression> de = ref(null);
                ie.e2 = extractSideEffect(sc, new BytePtr("__aakey"), de, ie.e2, false);
                e0.value = Expression.combine(de.value, e0.value);
                IndexExp ie1 = ie.e1.isIndexExp();
                if ((!(ie1 != null) || (ie1.e1.type.toBasetype().ty & 0xFF) != ENUMTY.Taarray))
                {
                    break;
                }
                ie = ie1;
            }
            assert((ie.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Taarray);
            Ref<Expression> de = ref(null);
            ie.e1 = extractSideEffect(sc, new BytePtr("__aatmp"), de, ie.e1, false);
            e0.value = Expression.combine(de.value, e0.value);
            be.e2 = extractSideEffect(sc, new BytePtr("__aaval"), e0, be.e2, true);
            return Expression.combine(e0.value, (Expression)be);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public BinExp() {}

        public abstract BinExp copy();
    }
    public static class BinAssignExp extends BinExp
    {
        public  BinAssignExp(Loc loc, byte op, int size, Expression e1, Expression e2) {
            super(loc, op, size, e1, e2);
        }

        public  boolean isLvalue() {
            return true;
        }

        public  Expression toLvalue(Scope sc, Expression ex) {
            return this;
        }

        public  Expression modifiableLvalue(Scope sc, Expression e) {
            return this.toLvalue(sc, this);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public BinAssignExp() {}

        public BinAssignExp copy() {
            BinAssignExp that = new BinAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CompileExp extends Expression
    {
        public DArray<Expression> exps;
        public  CompileExp(Loc loc, DArray<Expression> exps) {
            super(loc, TOK.mixin_, 28);
            this.exps = exps;
        }

        public  Expression syntaxCopy() {
            return new CompileExp(this.loc, Expression.arraySyntaxCopy(this.exps));
        }

        public  boolean equals(RootObject o) {
            if (pequals(this, o))
                return true;
            Expression e = isExpression(o);
            if (!(e != null))
                return false;
            {
                CompileExp ce = e.isCompileExp();
                if (ce != null)
                {
                    if ((this.exps).length != (ce.exps).length)
                        return false;
                    {
                        Slice<Expression> __r1351 = (this.exps).opSlice().copy();
                        int __key1350 = 0;
                        for (; __key1350 < __r1351.getLength();__key1350 += 1) {
                            Expression e1 = __r1351.get(__key1350);
                            int i = __key1350;
                            Expression e2 = (ce.exps).get(i);
                            if ((!pequals(e1, e2) && ((!(e1 != null) || !(e2 != null)) || !(e1.equals(e2)))))
                                return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CompileExp() {}

        public CompileExp copy() {
            CompileExp that = new CompileExp();
            that.exps = this.exps;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ImportExp extends UnaExp
    {
        public  ImportExp(Loc loc, Expression e) {
            super(loc, TOK.import_, 32, e);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ImportExp() {}

        public ImportExp copy() {
            ImportExp that = new ImportExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class AssertExp extends UnaExp
    {
        public Expression msg;
        public  AssertExp(Loc loc, Expression e, Expression msg) {
            super(loc, TOK.assert_, 36, e);
            this.msg = msg;
        }

        public  Expression syntaxCopy() {
            return new AssertExp(this.loc, this.e1.syntaxCopy(), this.msg != null ? this.msg.syntaxCopy() : null);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AssertExp() {}

        public AssertExp copy() {
            AssertExp that = new AssertExp();
            that.msg = this.msg;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DotIdExp extends UnaExp
    {
        public Identifier ident;
        public boolean noderef;
        public boolean wantsym;
        public  DotIdExp(Loc loc, Expression e, Identifier ident) {
            super(loc, TOK.dotIdentifier, 38, e);
            this.ident = ident;
        }

        public static DotIdExp create(Loc loc, Expression e, Identifier ident) {
            return new DotIdExp(loc, e, ident);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DotIdExp() {}

        public DotIdExp copy() {
            DotIdExp that = new DotIdExp();
            that.ident = this.ident;
            that.noderef = this.noderef;
            that.wantsym = this.wantsym;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DotTemplateExp extends UnaExp
    {
        public TemplateDeclaration td;
        public  DotTemplateExp(Loc loc, Expression e, TemplateDeclaration td) {
            super(loc, TOK.dotTemplateDeclaration, 36, e);
            this.td = td;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DotTemplateExp() {}

        public DotTemplateExp copy() {
            DotTemplateExp that = new DotTemplateExp();
            that.td = this.td;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DotVarExp extends UnaExp
    {
        public Declaration var;
        public boolean hasOverloads;
        public  DotVarExp(Loc loc, Expression e, Declaration var, boolean hasOverloads) {
            if (var.isVarDeclaration() != null)
                hasOverloads = false;
            super(loc, TOK.dotVariable, 37, e);
            this.var = var;
            this.hasOverloads = hasOverloads;
        }

        public  int checkModifiable(Scope sc, int flag) {
            if (checkUnsafeAccess(sc, this, false, !((flag) != 0)))
                return Modifiable.initialization;
            if ((this.e1.op & 0xFF) == 123)
                return this.var.checkModify(this.loc, sc, this.e1, flag);
            if (((sc).func != null && (sc).func.isCtorDeclaration() != null))
            {
                {
                    DotVarExp dve = this.e1.isDotVarExp();
                    if (dve != null)
                    {
                        if ((dve.e1.op & 0xFF) == 123)
                        {
                            VarDeclaration v = dve.var.isVarDeclaration();
                            if ((((v != null && v.isField()) && !(v._init != null)) && !(v.ctorinit)))
                            {
                                {
                                    TypeStruct ts = v.type.isTypeStruct();
                                    if (ts != null)
                                    {
                                        if (ts.sym.noDefaultCtor)
                                        {
                                            int modifyLevel = v.checkModify(this.loc, sc, dve.e1, flag);
                                            v.ctorinit = false;
                                            if (modifyLevel == Modifiable.initialization)
                                                return Modifiable.yes;
                                            return modifyLevel;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return this.e1.checkModifiable(sc, flag);
        }

        public  boolean isLvalue() {
            return true;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            if ((((this.e1.op & 0xFF) == 123 && ((sc).ctorflow.fieldinit.getLength()) != 0) && !(((sc).ctorflow.callSuper & 16) != 0)))
            {
                {
                    VarDeclaration vd = this.var.isVarDeclaration();
                    if (vd != null)
                    {
                        AggregateDeclaration ad = vd.isMember2();
                        if ((ad != null && ad.fields.length == (sc).ctorflow.fieldinit.getLength()))
                        {
                            {
                                Slice<VarDeclaration> __r1353 = ad.fields.opSlice().copy();
                                int __key1352 = 0;
                                for (; __key1352 < __r1353.getLength();__key1352 += 1) {
                                    VarDeclaration f = __r1353.get(__key1352);
                                    int i = __key1352;
                                    if (pequals(f, vd))
                                    {
                                        if (!(((sc).ctorflow.fieldinit.get(i).csx & 1) != 0))
                                        {
                                            modifyFieldVar(this.loc, sc, vd, this.e1);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return this;
        }

        public  Expression modifiableLvalue(Scope sc, Expression e) {
            return this.modifiableLvalue(sc, e);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DotVarExp() {}

        public DotVarExp copy() {
            DotVarExp that = new DotVarExp();
            that.var = this.var;
            that.hasOverloads = this.hasOverloads;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DotTemplateInstanceExp extends UnaExp
    {
        public TemplateInstance ti;
        public  DotTemplateInstanceExp(Loc loc, Expression e, Identifier name, DArray<RootObject> tiargs) {
            super(loc, TOK.dotTemplateInstance, 36, e);
            this.ti = new TemplateInstance(loc, name, tiargs);
        }

        public  DotTemplateInstanceExp(Loc loc, Expression e, TemplateInstance ti) {
            super(loc, TOK.dotTemplateInstance, 36, e);
            this.ti = ti;
        }

        public  Expression syntaxCopy() {
            return new DotTemplateInstanceExp(this.loc, this.e1.syntaxCopy(), this.ti.name, TemplateInstance.arraySyntaxCopy(this.ti.tiargs));
        }

        public  boolean findTempDecl(Scope sc) {
            if (this.ti.tempdecl != null)
                return true;
            Expression e = new DotIdExp(this.loc, this.e1, this.ti.name);
            e = expressionSemantic(e, sc);
            if ((e.op & 0xFF) == 97)
                e = ((DotExp)e).e2;
            Dsymbol s = null;
            switch ((e.op & 0xFF))
            {
                case 214:
                    s = ((OverExp)e).vars;
                    break;
                case 37:
                    s = ((DotTemplateExp)e).td;
                    break;
                case 203:
                    s = ((ScopeExp)e).sds;
                    break;
                case 27:
                    s = ((DotVarExp)e).var;
                    break;
                case 26:
                    s = ((VarExp)e).var;
                    break;
                default:
                return false;
            }
            return this.ti.updateTempDecl(sc, s);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DotTemplateInstanceExp() {}

        public DotTemplateInstanceExp copy() {
            DotTemplateInstanceExp that = new DotTemplateInstanceExp();
            that.ti = this.ti;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DelegateExp extends UnaExp
    {
        public FuncDeclaration func;
        public boolean hasOverloads;
        public VarDeclaration vthis2;
        public  DelegateExp(Loc loc, Expression e, FuncDeclaration f, boolean hasOverloads, VarDeclaration vthis2) {
            super(loc, TOK.delegate_, 44, e);
            this.func = f;
            this.hasOverloads = hasOverloads;
            this.vthis2 = vthis2;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DelegateExp() {}

        public DelegateExp copy() {
            DelegateExp that = new DelegateExp();
            that.func = this.func;
            that.hasOverloads = this.hasOverloads;
            that.vthis2 = this.vthis2;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DotTypeExp extends UnaExp
    {
        public Dsymbol sym;
        public  DotTypeExp(Loc loc, Expression e, Dsymbol s) {
            super(loc, TOK.dotType, 36, e);
            this.sym = s;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DotTypeExp() {}

        public DotTypeExp copy() {
            DotTypeExp that = new DotTypeExp();
            that.sym = this.sym;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CallExp extends UnaExp
    {
        public DArray<Expression> arguments;
        public FuncDeclaration f;
        public boolean directcall;
        public VarDeclaration vthis2;
        public  CallExp(Loc loc, Expression e, DArray<Expression> exps) {
            super(loc, TOK.call, 48, e);
            this.arguments = exps;
        }

        public  CallExp(Loc loc, Expression e) {
            super(loc, TOK.call, 48, e);
        }

        public  CallExp(Loc loc, Expression e, Expression earg1) {
            super(loc, TOK.call, 48, e);
            this.arguments = new DArray<Expression>();
            if (earg1 != null)
                (this.arguments).push(earg1);
        }

        public  CallExp(Loc loc, Expression e, Expression earg1, Expression earg2) {
            super(loc, TOK.call, 48, e);
            DArray<Expression> arguments = new DArray<Expression>(2);
            arguments.set(0, earg1);
            arguments.set(1, earg2);
            this.arguments = arguments;
        }

        public  CallExp(Loc loc, FuncDeclaration fd, Expression earg1) {
            this(loc, new VarExp(loc, fd, false), earg1);
            this.f = fd;
        }

        public static CallExp create(Loc loc, Expression e, DArray<Expression> exps) {
            return new CallExp(loc, e, exps);
        }

        public static CallExp create(Loc loc, Expression e) {
            return new CallExp(loc, e);
        }

        public static CallExp create(Loc loc, Expression e, Expression earg1) {
            return new CallExp(loc, e, earg1);
        }

        public static CallExp create(Loc loc, FuncDeclaration fd, Expression earg1) {
            return new CallExp(loc, fd, earg1);
        }

        public  Expression syntaxCopy() {
            return new CallExp(this.loc, this.e1.syntaxCopy(), Expression.arraySyntaxCopy(this.arguments));
        }

        public  boolean isLvalue() {
            Type tb = this.e1.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tdelegate || (tb.ty & 0xFF) == ENUMTY.Tpointer))
                tb = tb.nextOf();
            TypeFunction tf = tb.isTypeFunction();
            if ((tf != null && tf.isref))
            {
                {
                    DotVarExp dve = this.e1.isDotVarExp();
                    if (dve != null)
                        if (dve.var.isCtorDeclaration() != null)
                            return false;
                }
                return true;
            }
            return false;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            if (this.isLvalue())
                return this;
            return this.toLvalue(sc, e);
        }

        public  Expression addDtorHook(Scope sc) {
            {
                TypeFunction tf = this.e1.type.isTypeFunction();
                if (tf != null)
                {
                    if (tf.isref)
                        return this;
                }
            }
            Type tv = this.type.baseElemOf();
            {
                TypeStruct ts = tv.isTypeStruct();
                if (ts != null)
                {
                    StructDeclaration sd = ts.sym;
                    if (sd.dtor != null)
                    {
                        VarDeclaration tmp = copyToTemp(0L, new BytePtr("__tmpfordtor"), this);
                        DeclarationExp de = new DeclarationExp(this.loc, tmp);
                        VarExp ve = new VarExp(this.loc, tmp, true);
                        Expression e = new CommaExp(this.loc, de, ve, true);
                        e = expressionSemantic(e, sc);
                        return e;
                    }
                }
            }
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CallExp() {}

        public CallExp copy() {
            CallExp that = new CallExp();
            that.arguments = this.arguments;
            that.f = this.f;
            that.directcall = this.directcall;
            that.vthis2 = this.vthis2;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static FuncDeclaration isFuncAddress(Expression e, Ptr<Boolean> hasOverloads) {
        {
            AddrExp ae = e.isAddrExp();
            if (ae != null)
            {
                Expression ae1 = ae.e1;
                {
                    VarExp ve = ae1.isVarExp();
                    if (ve != null)
                    {
                        if (hasOverloads != null)
                            hasOverloads.set(0, ve.hasOverloads);
                        return ve.var.isFuncDeclaration();
                    }
                }
                {
                    DotVarExp dve = ae1.isDotVarExp();
                    if (dve != null)
                    {
                        if (hasOverloads != null)
                            hasOverloads.set(0, dve.hasOverloads);
                        return dve.var.isFuncDeclaration();
                    }
                }
            }
            else
            {
                {
                    SymOffExp soe = e.isSymOffExp();
                    if (soe != null)
                    {
                        if (hasOverloads != null)
                            hasOverloads.set(0, soe.hasOverloads);
                        return soe.var.isFuncDeclaration();
                    }
                }
                {
                    DelegateExp dge = e.isDelegateExp();
                    if (dge != null)
                    {
                        if (hasOverloads != null)
                            hasOverloads.set(0, dge.hasOverloads);
                        return dge.func.isFuncDeclaration();
                    }
                }
            }
        }
        return null;
    }

    public static class AddrExp extends UnaExp
    {
        public  AddrExp(Loc loc, Expression e) {
            super(loc, TOK.address, 32, e);
        }

        public  AddrExp(Loc loc, Expression e, Type t) {
            this(loc, e);
            this.type = t;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AddrExp() {}

        public AddrExp copy() {
            AddrExp that = new AddrExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PtrExp extends UnaExp
    {
        public  PtrExp(Loc loc, Expression e) {
            super(loc, TOK.star, 32, e);
        }

        public  PtrExp(Loc loc, Expression e, Type t) {
            super(loc, TOK.star, 32, e);
            this.type = t;
        }

        public  int checkModifiable(Scope sc, int flag) {
            {
                SymOffExp se = this.e1.isSymOffExp();
                if (se != null)
                {
                    return se.var.checkModify(this.loc, sc, null, flag);
                }
                else {
                    AddrExp ae = this.e1.isAddrExp();
                    if (ae != null)
                    {
                        return ae.e1.checkModifiable(sc, flag);
                    }
                }
            }
            return Modifiable.yes;
        }

        public  boolean isLvalue() {
            return true;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            return this;
        }

        public  Expression modifiableLvalue(Scope sc, Expression e) {
            return this.modifiableLvalue(sc, e);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PtrExp() {}

        public PtrExp copy() {
            PtrExp that = new PtrExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class NegExp extends UnaExp
    {
        public  NegExp(Loc loc, Expression e) {
            super(loc, TOK.negate, 32, e);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public NegExp() {}

        public NegExp copy() {
            NegExp that = new NegExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class UAddExp extends UnaExp
    {
        public  UAddExp(Loc loc, Expression e) {
            super(loc, TOK.uadd, 32, e);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UAddExp() {}

        public UAddExp copy() {
            UAddExp that = new UAddExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ComExp extends UnaExp
    {
        public  ComExp(Loc loc, Expression e) {
            super(loc, TOK.tilde, 32, e);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ComExp() {}

        public ComExp copy() {
            ComExp that = new ComExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class NotExp extends UnaExp
    {
        public  NotExp(Loc loc, Expression e) {
            super(loc, TOK.not, 32, e);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public NotExp() {}

        public NotExp copy() {
            NotExp that = new NotExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DeleteExp extends UnaExp
    {
        public boolean isRAII;
        public  DeleteExp(Loc loc, Expression e, boolean isRAII) {
            super(loc, TOK.delete_, 33, e);
            this.isRAII = isRAII;
        }

        public  Expression toBoolean(Scope sc) {
            this.error(new BytePtr("`delete` does not give a boolean result"));
            return new ErrorExp();
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DeleteExp() {}

        public DeleteExp copy() {
            DeleteExp that = new DeleteExp();
            that.isRAII = this.isRAII;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CastExp extends UnaExp
    {
        public Type to;
        public byte mod = (byte)255;
        public  CastExp(Loc loc, Expression e, Type t) {
            super(loc, TOK.cast_, 37, e);
            this.to = t;
        }

        public  CastExp(Loc loc, Expression e, byte mod) {
            super(loc, TOK.cast_, 37, e);
            this.mod = mod;
        }

        public  Expression syntaxCopy() {
            return this.to != null ? new CastExp(this.loc, this.e1.syntaxCopy(), this.to.syntaxCopy()) : new CastExp(this.loc, this.e1.syntaxCopy(), this.mod);
        }

        public  boolean isLvalue() {
            return (this.e1.isLvalue() && this.e1.type.mutableOf().unSharedOf().equals(this.to.mutableOf().unSharedOf()));
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            if (this.isLvalue())
                return this;
            return this.toLvalue(sc, e);
        }

        public  Expression addDtorHook(Scope sc) {
            if ((this.to.toBasetype().ty & 0xFF) == ENUMTY.Tvoid)
                this.e1 = this.e1.addDtorHook(sc);
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CastExp() {}

        public CastExp copy() {
            CastExp that = new CastExp();
            that.to = this.to;
            that.mod = this.mod;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class VectorExp extends UnaExp
    {
        public TypeVector to;
        public int dim = -1;
        public byte ownedByCtfe = OwnedBy.code;
        public  VectorExp(Loc loc, Expression e, Type t) {
            super(loc, TOK.vector, 41, e);
            assert((t.ty & 0xFF) == ENUMTY.Tvector);
            this.to = (TypeVector)t;
        }

        public static VectorExp create(Loc loc, Expression e, Type t) {
            return new VectorExp(loc, e, t);
        }

        public static void emplace(UnionExp pue, Loc loc, Expression e, Type type) {
            emplaceExpVectorExpLocExpressionType(pue, loc, e, type);
        }

        public  Expression syntaxCopy() {
            return new VectorExp(this.loc, this.e1.syntaxCopy(), this.to.syntaxCopy());
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public VectorExp() {}

        public VectorExp copy() {
            VectorExp that = new VectorExp();
            that.to = this.to;
            that.dim = this.dim;
            that.ownedByCtfe = this.ownedByCtfe;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class VectorArrayExp extends UnaExp
    {
        public  VectorArrayExp(Loc loc, Expression e1) {
            super(loc, TOK.vectorArray, 32, e1);
        }

        public  boolean isLvalue() {
            return this.e1.isLvalue();
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            this.e1 = this.e1.toLvalue(sc, e);
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public VectorArrayExp() {}

        public VectorArrayExp copy() {
            VectorArrayExp that = new VectorArrayExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SliceExp extends UnaExp
    {
        public Expression upr;
        public Expression lwr;
        public VarDeclaration lengthVar;
        public boolean upperIsInBounds;
        public boolean lowerIsLessThanUpper;
        public boolean arrayop;
        public  SliceExp(Loc loc, Expression e1, IntervalExp ie) {
            super(loc, TOK.slice, 47, e1);
            this.upr = ie != null ? ie.upr : null;
            this.lwr = ie != null ? ie.lwr : null;
        }

        public  SliceExp(Loc loc, Expression e1, Expression lwr, Expression upr) {
            super(loc, TOK.slice, 47, e1);
            this.upr = upr;
            this.lwr = lwr;
        }

        public  Expression syntaxCopy() {
            SliceExp se = new SliceExp(this.loc, this.e1.syntaxCopy(), this.lwr != null ? this.lwr.syntaxCopy() : null, this.upr != null ? this.upr.syntaxCopy() : null);
            se.lengthVar = this.lengthVar;
            return se;
        }

        public  int checkModifiable(Scope sc, int flag) {
            if ((((this.e1.type.ty & 0xFF) == ENUMTY.Tsarray || ((this.e1.op & 0xFF) == 62 && (this.e1.type.ty & 0xFF) != ENUMTY.Tarray)) || (this.e1.op & 0xFF) == 31))
            {
                return this.e1.checkModifiable(sc, flag);
            }
            return Modifiable.yes;
        }

        public  boolean isLvalue() {
            return (this.type != null && (this.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray);
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            return (this.type != null && (this.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray) ? this : this.toLvalue(sc, e);
        }

        public  Expression modifiableLvalue(Scope sc, Expression e) {
            this.error(new BytePtr("slice expression `%s` is not a modifiable lvalue"), this.toChars());
            return this;
        }

        public  boolean isBool(boolean result) {
            return this.e1.isBool(result);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SliceExp() {}

        public SliceExp copy() {
            SliceExp that = new SliceExp();
            that.upr = this.upr;
            that.lwr = this.lwr;
            that.lengthVar = this.lengthVar;
            that.upperIsInBounds = this.upperIsInBounds;
            that.lowerIsLessThanUpper = this.lowerIsLessThanUpper;
            that.arrayop = this.arrayop;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ArrayLengthExp extends UnaExp
    {
        public  ArrayLengthExp(Loc loc, Expression e1) {
            super(loc, TOK.arrayLength, 32, e1);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ArrayLengthExp() {}

        public ArrayLengthExp copy() {
            ArrayLengthExp that = new ArrayLengthExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ArrayExp extends UnaExp
    {
        public DArray<Expression> arguments;
        public int currentDimension;
        public VarDeclaration lengthVar;
        public  ArrayExp(Loc loc, Expression e1, Expression index) {
            super(loc, TOK.array, 44, e1);
            this.arguments = new DArray<Expression>();
            if (index != null)
                (this.arguments).push(index);
        }

        public  ArrayExp(Loc loc, Expression e1, DArray<Expression> args) {
            super(loc, TOK.array, 44, e1);
            this.arguments = args;
        }

        public  Expression syntaxCopy() {
            ArrayExp ae = new ArrayExp(this.loc, this.e1.syntaxCopy(), Expression.arraySyntaxCopy(this.arguments));
            ae.lengthVar = this.lengthVar;
            return ae;
        }

        public  boolean isLvalue() {
            if ((this.type != null && (this.type.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                return false;
            return true;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            if ((this.type != null && (this.type.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                this.error(new BytePtr("`void`s have no value"));
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ArrayExp() {}

        public ArrayExp copy() {
            ArrayExp that = new ArrayExp();
            that.arguments = this.arguments;
            that.currentDimension = this.currentDimension;
            that.lengthVar = this.lengthVar;
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DotExp extends BinExp
    {
        public  DotExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.dot, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DotExp() {}

        public DotExp copy() {
            DotExp that = new DotExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CommaExp extends BinExp
    {
        public boolean isGenerated;
        public boolean allowCommaExp;
        public  CommaExp(Loc loc, Expression e1, Expression e2, boolean generated) {
            super(loc, TOK.comma, 42, e1, e2);
            this.allowCommaExp = (this.isGenerated = generated);
        }

        public  int checkModifiable(Scope sc, int flag) {
            return this.e2.checkModifiable(sc, flag);
        }

        public  boolean isLvalue() {
            return this.e2.isLvalue();
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            this.e2 = this.e2.toLvalue(sc, null);
            return this;
        }

        public  Expression modifiableLvalue(Scope sc, Expression e) {
            this.e2 = this.e2.modifiableLvalue(sc, e);
            return this;
        }

        public  boolean isBool(boolean result) {
            return this.e2.isBool(result);
        }

        public  Expression toBoolean(Scope sc) {
            Expression ex2 = this.e2.toBoolean(sc);
            if ((ex2.op & 0xFF) == 127)
                return ex2;
            this.e2 = ex2;
            this.type = this.e2.type;
            return this;
        }

        public  Expression addDtorHook(Scope sc) {
            this.e2 = this.e2.addDtorHook(sc);
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public static void allow(Expression exp) {
            if (exp != null)
                {
                    CommaExp ce = exp.isCommaExp();
                    if (ce != null)
                        ce.allowCommaExp = true;
                }
        }


        public CommaExp() {}

        public CommaExp copy() {
            CommaExp that = new CommaExp();
            that.isGenerated = this.isGenerated;
            that.allowCommaExp = this.allowCommaExp;
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class IntervalExp extends Expression
    {
        public Expression lwr;
        public Expression upr;
        public  IntervalExp(Loc loc, Expression lwr, Expression upr) {
            super(loc, TOK.interval, 32);
            this.lwr = lwr;
            this.upr = upr;
        }

        public  Expression syntaxCopy() {
            return new IntervalExp(this.loc, this.lwr.syntaxCopy(), this.upr.syntaxCopy());
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public IntervalExp() {}

        public IntervalExp copy() {
            IntervalExp that = new IntervalExp();
            that.lwr = this.lwr;
            that.upr = this.upr;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DelegatePtrExp extends UnaExp
    {
        public  DelegatePtrExp(Loc loc, Expression e1) {
            super(loc, TOK.delegatePointer, 32, e1);
        }

        public  boolean isLvalue() {
            return this.e1.isLvalue();
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            this.e1 = this.e1.toLvalue(sc, e);
            return this;
        }

        public  Expression modifiableLvalue(Scope sc, Expression e) {
            if ((sc).func.setUnsafe())
            {
                this.error(new BytePtr("cannot modify delegate pointer in `@safe` code `%s`"), this.toChars());
                return new ErrorExp();
            }
            return this.modifiableLvalue(sc, e);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DelegatePtrExp() {}

        public DelegatePtrExp copy() {
            DelegatePtrExp that = new DelegatePtrExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DelegateFuncptrExp extends UnaExp
    {
        public  DelegateFuncptrExp(Loc loc, Expression e1) {
            super(loc, TOK.delegateFunctionPointer, 32, e1);
        }

        public  boolean isLvalue() {
            return this.e1.isLvalue();
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            this.e1 = this.e1.toLvalue(sc, e);
            return this;
        }

        public  Expression modifiableLvalue(Scope sc, Expression e) {
            if ((sc).func.setUnsafe())
            {
                this.error(new BytePtr("cannot modify delegate function pointer in `@safe` code `%s`"), this.toChars());
                return new ErrorExp();
            }
            return this.modifiableLvalue(sc, e);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DelegateFuncptrExp() {}

        public DelegateFuncptrExp copy() {
            DelegateFuncptrExp that = new DelegateFuncptrExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class IndexExp extends BinExp
    {
        public VarDeclaration lengthVar;
        public boolean modifiable = false;
        public boolean indexIsInBounds;
        public  IndexExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.index, 46, e1, e2);
        }

        public  Expression syntaxCopy() {
            IndexExp ie = new IndexExp(this.loc, this.e1.syntaxCopy(), this.e2.syntaxCopy());
            ie.lengthVar = this.lengthVar;
            return ie;
        }

        public  int checkModifiable(Scope sc, int flag) {
            if (((((this.e1.type.ty & 0xFF) == ENUMTY.Tsarray || (this.e1.type.ty & 0xFF) == ENUMTY.Taarray) || ((this.e1.op & 0xFF) == 62 && (this.e1.type.ty & 0xFF) != ENUMTY.Tarray)) || (this.e1.op & 0xFF) == 31))
            {
                return this.e1.checkModifiable(sc, flag);
            }
            return Modifiable.yes;
        }

        public  boolean isLvalue() {
            return true;
        }

        public  Expression toLvalue(Scope sc, Expression e) {
            return this;
        }

        public  Expression modifiableLvalue(Scope sc, Expression e) {
            Expression ex = this.markSettingAAElem();
            if ((ex.op & 0xFF) == 127)
                return ex;
            return this.modifiableLvalue(sc, e);
        }

        public  Expression markSettingAAElem() {
            if ((this.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Taarray)
            {
                Type t2b = this.e2.type.toBasetype();
                if (((t2b.ty & 0xFF) == ENUMTY.Tarray && t2b.nextOf().isMutable()))
                {
                    this.error(new BytePtr("associative arrays can only be assigned values with immutable keys, not `%s`"), this.e2.type.toChars());
                    return new ErrorExp();
                }
                this.modifiable = true;
                {
                    IndexExp ie = this.e1.isIndexExp();
                    if (ie != null)
                    {
                        Expression ex = ie.markSettingAAElem();
                        if ((ex.op & 0xFF) == 127)
                            return ex;
                        assert(pequals(ex, this.e1));
                    }
                }
            }
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public IndexExp() {}

        public IndexExp copy() {
            IndexExp that = new IndexExp();
            that.lengthVar = this.lengthVar;
            that.modifiable = this.modifiable;
            that.indexIsInBounds = this.indexIsInBounds;
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PostExp extends BinExp
    {
        public  PostExp(byte op, Loc loc, Expression e) {
            super(loc, op, 40, e, new IntegerExp(loc, 1L, Type.tint32));
            assert(((op & 0xFF) == 94 || (op & 0xFF) == 93));
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PostExp() {}

        public PostExp copy() {
            PostExp that = new PostExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PreExp extends UnaExp
    {
        public  PreExp(byte op, Loc loc, Expression e) {
            super(loc, op, 32, e);
            assert(((op & 0xFF) == 104 || (op & 0xFF) == 103));
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PreExp() {}

        public PreExp copy() {
            PreExp that = new PreExp();
            that.e1 = this.e1;
            that.att1 = this.att1;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }

    public static class MemorySet 
    {
        public static final int blockAssign = 1;
        public static final int referenceInit = 2;
    }

    public static class AssignExp extends BinExp
    {
        public int memset;
        public  AssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.assign, 44, e1, e2);
        }

        public  AssignExp(Loc loc, byte tok, Expression e1, Expression e2) {
            super(loc, tok, 44, e1, e2);
        }

        public  boolean isLvalue() {
            if (((this.e1.op & 0xFF) == 31 || (this.e1.op & 0xFF) == 32))
            {
                return false;
            }
            return true;
        }

        public  Expression toLvalue(Scope sc, Expression ex) {
            if (((this.e1.op & 0xFF) == 31 || (this.e1.op & 0xFF) == 32))
            {
                return this.toLvalue(sc, ex);
            }
            return this;
        }

        public  Expression toBoolean(Scope sc) {
            this.error(new BytePtr("assignment cannot be used as a condition, perhaps `==` was meant?"));
            return new ErrorExp();
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AssignExp() {}

        public AssignExp copy() {
            AssignExp that = new AssignExp();
            that.memset = this.memset;
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ConstructExp extends AssignExp
    {
        public  ConstructExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.construct, e1, e2);
        }

        public  ConstructExp(Loc loc, VarDeclaration v, Expression e2) {
            VarExp ve = new VarExp(loc, v, true);
            assert((v.type != null && ve.type != null));
            super(loc, TOK.construct, ve, e2);
            if ((v.storage_class & 2101248L) != 0)
                this.memset |= MemorySet.referenceInit;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ConstructExp() {}

        public ConstructExp copy() {
            ConstructExp that = new ConstructExp();
            that.memset = this.memset;
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class BlitExp extends AssignExp
    {
        public  BlitExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.blit, e1, e2);
        }

        public  BlitExp(Loc loc, VarDeclaration v, Expression e2) {
            VarExp ve = new VarExp(loc, v, true);
            assert((v.type != null && ve.type != null));
            super(loc, TOK.blit, ve, e2);
            if ((v.storage_class & 2101248L) != 0)
                this.memset |= MemorySet.referenceInit;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public BlitExp() {}

        public BlitExp copy() {
            BlitExp that = new BlitExp();
            that.memset = this.memset;
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class AddAssignExp extends BinAssignExp
    {
        public  AddAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.addAssign, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AddAssignExp() {}

        public AddAssignExp copy() {
            AddAssignExp that = new AddAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class MinAssignExp extends BinAssignExp
    {
        public  MinAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.minAssign, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public MinAssignExp() {}

        public MinAssignExp copy() {
            MinAssignExp that = new MinAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class MulAssignExp extends BinAssignExp
    {
        public  MulAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.mulAssign, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public MulAssignExp() {}

        public MulAssignExp copy() {
            MulAssignExp that = new MulAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DivAssignExp extends BinAssignExp
    {
        public  DivAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.divAssign, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DivAssignExp() {}

        public DivAssignExp copy() {
            DivAssignExp that = new DivAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ModAssignExp extends BinAssignExp
    {
        public  ModAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.modAssign, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ModAssignExp() {}

        public ModAssignExp copy() {
            ModAssignExp that = new ModAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class AndAssignExp extends BinAssignExp
    {
        public  AndAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.andAssign, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AndAssignExp() {}

        public AndAssignExp copy() {
            AndAssignExp that = new AndAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class OrAssignExp extends BinAssignExp
    {
        public  OrAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.orAssign, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public OrAssignExp() {}

        public OrAssignExp copy() {
            OrAssignExp that = new OrAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class XorAssignExp extends BinAssignExp
    {
        public  XorAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.xorAssign, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public XorAssignExp() {}

        public XorAssignExp copy() {
            XorAssignExp that = new XorAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PowAssignExp extends BinAssignExp
    {
        public  PowAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.powAssign, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PowAssignExp() {}

        public PowAssignExp copy() {
            PowAssignExp that = new PowAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ShlAssignExp extends BinAssignExp
    {
        public  ShlAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.leftShiftAssign, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ShlAssignExp() {}

        public ShlAssignExp copy() {
            ShlAssignExp that = new ShlAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ShrAssignExp extends BinAssignExp
    {
        public  ShrAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.rightShiftAssign, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ShrAssignExp() {}

        public ShrAssignExp copy() {
            ShrAssignExp that = new ShrAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class UshrAssignExp extends BinAssignExp
    {
        public  UshrAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.unsignedRightShiftAssign, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UshrAssignExp() {}

        public UshrAssignExp copy() {
            UshrAssignExp that = new UshrAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CatAssignExp extends BinAssignExp
    {
        public  CatAssignExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.concatenateAssign, 40, e1, e2);
        }

        public  CatAssignExp(Loc loc, byte tok, Expression e1, Expression e2) {
            super(loc, tok, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CatAssignExp() {}

        public CatAssignExp copy() {
            CatAssignExp that = new CatAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CatElemAssignExp extends CatAssignExp
    {
        public  CatElemAssignExp(Loc loc, Type type, Expression e1, Expression e2) {
            super(loc, TOK.concatenateElemAssign, e1, e2);
            this.type = type;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CatElemAssignExp() {}

        public CatElemAssignExp copy() {
            CatElemAssignExp that = new CatElemAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CatDcharAssignExp extends CatAssignExp
    {
        public  CatDcharAssignExp(Loc loc, Type type, Expression e1, Expression e2) {
            super(loc, TOK.concatenateDcharAssign, e1, e2);
            this.type = type;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CatDcharAssignExp() {}

        public CatDcharAssignExp copy() {
            CatDcharAssignExp that = new CatDcharAssignExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class AddExp extends BinExp
    {
        public  AddExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.add, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AddExp() {}

        public AddExp copy() {
            AddExp that = new AddExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class MinExp extends BinExp
    {
        public  MinExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.min, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public MinExp() {}

        public MinExp copy() {
            MinExp that = new MinExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CatExp extends BinExp
    {
        public  CatExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.concatenate, 40, e1, e2);
        }

        public  Expression resolveLoc(Loc loc, Scope sc) {
            this.e1 = this.e1.resolveLoc(loc, sc);
            this.e2 = this.e2.resolveLoc(loc, sc);
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CatExp() {}

        public CatExp copy() {
            CatExp that = new CatExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class MulExp extends BinExp
    {
        public  MulExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.mul, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public MulExp() {}

        public MulExp copy() {
            MulExp that = new MulExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DivExp extends BinExp
    {
        public  DivExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.div, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DivExp() {}

        public DivExp copy() {
            DivExp that = new DivExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ModExp extends BinExp
    {
        public  ModExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.mod, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ModExp() {}

        public ModExp copy() {
            ModExp that = new ModExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PowExp extends BinExp
    {
        public  PowExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.pow, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PowExp() {}

        public PowExp copy() {
            PowExp that = new PowExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ShlExp extends BinExp
    {
        public  ShlExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.leftShift, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ShlExp() {}

        public ShlExp copy() {
            ShlExp that = new ShlExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ShrExp extends BinExp
    {
        public  ShrExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.rightShift, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ShrExp() {}

        public ShrExp copy() {
            ShrExp that = new ShrExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class UshrExp extends BinExp
    {
        public  UshrExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.unsignedRightShift, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UshrExp() {}

        public UshrExp copy() {
            UshrExp that = new UshrExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class AndExp extends BinExp
    {
        public  AndExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.and, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AndExp() {}

        public AndExp copy() {
            AndExp that = new AndExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class OrExp extends BinExp
    {
        public  OrExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.or, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public OrExp() {}

        public OrExp copy() {
            OrExp that = new OrExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class XorExp extends BinExp
    {
        public  XorExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.xor, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public XorExp() {}

        public XorExp copy() {
            XorExp that = new XorExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class LogicalExp extends BinExp
    {
        public  LogicalExp(Loc loc, byte op, Expression e1, Expression e2) {
            super(loc, op, 40, e1, e2);
            assert(((op & 0xFF) == 101 || (op & 0xFF) == 102));
        }

        public  Expression toBoolean(Scope sc) {
            Expression ex2 = this.e2.toBoolean(sc);
            if ((ex2.op & 0xFF) == 127)
                return ex2;
            this.e2 = ex2;
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public LogicalExp() {}

        public LogicalExp copy() {
            LogicalExp that = new LogicalExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CmpExp extends BinExp
    {
        public  CmpExp(byte op, Loc loc, Expression e1, Expression e2) {
            super(loc, op, 40, e1, e2);
            assert(((((op & 0xFF) == 54 || (op & 0xFF) == 56) || (op & 0xFF) == 55) || (op & 0xFF) == 57));
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CmpExp() {}

        public CmpExp copy() {
            CmpExp that = new CmpExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class InExp extends BinExp
    {
        public  InExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.in_, 40, e1, e2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public InExp() {}

        public InExp copy() {
            InExp that = new InExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class RemoveExp extends BinExp
    {
        public  RemoveExp(Loc loc, Expression e1, Expression e2) {
            super(loc, TOK.remove, 40, e1, e2);
            this.type = Type.tbool;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public RemoveExp() {}

        public RemoveExp copy() {
            RemoveExp that = new RemoveExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class EqualExp extends BinExp
    {
        public  EqualExp(byte op, Loc loc, Expression e1, Expression e2) {
            super(loc, op, 40, e1, e2);
            assert(((op & 0xFF) == 58 || (op & 0xFF) == 59));
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public EqualExp() {}

        public EqualExp copy() {
            EqualExp that = new EqualExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class IdentityExp extends BinExp
    {
        public  IdentityExp(byte op, Loc loc, Expression e1, Expression e2) {
            super(loc, op, 40, e1, e2);
            assert(((op & 0xFF) == 60 || (op & 0xFF) == 61));
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public IdentityExp() {}

        public IdentityExp copy() {
            IdentityExp that = new IdentityExp();
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CondExp extends BinExp
    {
        public Expression econd;
        public  CondExp(Loc loc, Expression econd, Expression e1, Expression e2) {
            super(loc, TOK.question, 44, e1, e2);
            this.econd = econd;
        }

        public  Expression syntaxCopy() {
            return new CondExp(this.loc, this.econd.syntaxCopy(), this.e1.syntaxCopy(), this.e2.syntaxCopy());
        }

        public  int checkModifiable(Scope sc, int flag) {
            if ((this.e1.checkModifiable(sc, flag) != Modifiable.no && this.e2.checkModifiable(sc, flag) != Modifiable.no))
                return Modifiable.yes;
            return Modifiable.no;
        }

        public  boolean isLvalue() {
            return (this.e1.isLvalue() && this.e2.isLvalue());
        }

        public  Expression toLvalue(Scope sc, Expression ex) {
            CondExp e = (CondExp)this.copy();
            e.e1 = this.e1.toLvalue(sc, null).addressOf();
            e.e2 = this.e2.toLvalue(sc, null).addressOf();
            e.type = this.type.pointerTo();
            return new PtrExp(this.loc, e, this.type);
        }

        public  Expression modifiableLvalue(Scope sc, Expression e) {
            this.e1 = this.e1.modifiableLvalue(sc, this.e1);
            this.e2 = this.e2.modifiableLvalue(sc, this.e2);
            return this.toLvalue(sc, this);
        }

        public  Expression toBoolean(Scope sc) {
            Expression ex1 = this.e1.toBoolean(sc);
            Expression ex2 = this.e2.toBoolean(sc);
            if ((ex1.op & 0xFF) == 127)
                return ex1;
            if ((ex2.op & 0xFF) == 127)
                return ex2;
            this.e1 = ex1;
            this.e2 = ex2;
            return this;
        }

        public  void hookDtors(Scope sc) {
            DtorVisitor v = new DtorVisitor(sc, this);
            v.isThen = true;
            walkPostorder(this.e1, v);
            v.isThen = false;
            walkPostorder(this.e2, v);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CondExp() {}

        public CondExp copy() {
            CondExp that = new CondExp();
            that.econd = this.econd;
            that.e1 = this.e1;
            that.e2 = this.e2;
            that.att1 = this.att1;
            that.att2 = this.att2;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DefaultInitExp extends Expression
    {
        public byte subop;
        public  DefaultInitExp(Loc loc, byte subop, int size) {
            super(loc, TOK.default_, size);
            this.subop = subop;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DefaultInitExp() {}

        public DefaultInitExp copy() {
            DefaultInitExp that = new DefaultInitExp();
            that.subop = this.subop;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class FileInitExp extends DefaultInitExp
    {
        public  FileInitExp(Loc loc, byte tok) {
            super(loc, tok, 25);
        }

        public  Expression resolveLoc(Loc loc, Scope sc) {
            BytePtr s = null;
            if ((this.subop & 0xFF) == 220)
                s = pcopy(FileName.toAbsolute(loc.isValid() ? loc.filename : (sc)._module.srcfile.toChars(), null));
            else
                s = pcopy((loc.isValid() ? loc.filename : (sc)._module.ident.toChars()));
            Expression e = new StringExp(loc, s);
            e = expressionSemantic(e, sc);
            e = e.castTo(sc, this.type);
            return e;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public FileInitExp() {}

        public FileInitExp copy() {
            FileInitExp that = new FileInitExp();
            that.subop = this.subop;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class LineInitExp extends DefaultInitExp
    {
        public  LineInitExp(Loc loc) {
            super(loc, TOK.line, 25);
        }

        public  Expression resolveLoc(Loc loc, Scope sc) {
            Expression e = new IntegerExp(loc, (long)loc.linnum, Type.tint32);
            e = e.castTo(sc, this.type);
            return e;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public LineInitExp() {}

        public LineInitExp copy() {
            LineInitExp that = new LineInitExp();
            that.subop = this.subop;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ModuleInitExp extends DefaultInitExp
    {
        public  ModuleInitExp(Loc loc) {
            super(loc, TOK.moduleString, 25);
        }

        public  Expression resolveLoc(Loc loc, Scope sc) {
            BytePtr s = pcopy((((sc).callsc != null ? (sc).callsc : sc))._module.toPrettyChars(false));
            Expression e = new StringExp(loc, s);
            e = expressionSemantic(e, sc);
            e = e.castTo(sc, this.type);
            return e;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ModuleInitExp() {}

        public ModuleInitExp copy() {
            ModuleInitExp that = new ModuleInitExp();
            that.subop = this.subop;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class FuncInitExp extends DefaultInitExp
    {
        public  FuncInitExp(Loc loc) {
            super(loc, TOK.functionString, 25);
        }

        public  Expression resolveLoc(Loc loc, Scope sc) {
            BytePtr s = null;
            if (((sc).callsc != null && ((sc).callsc).func != null))
                s = pcopy(((sc).callsc).func.toPrettyChars(false));
            else if ((sc).func != null)
                s = pcopy((sc).func.toPrettyChars(false));
            else
                s = pcopy(new BytePtr(""));
            Expression e = new StringExp(loc, s);
            e = expressionSemantic(e, sc);
            e.type = Type.tstring;
            return e;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public FuncInitExp() {}

        public FuncInitExp copy() {
            FuncInitExp that = new FuncInitExp();
            that.subop = this.subop;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PrettyFuncInitExp extends DefaultInitExp
    {
        public  PrettyFuncInitExp(Loc loc) {
            super(loc, TOK.prettyFunction, 25);
        }

        public  Expression resolveLoc(Loc loc, Scope sc) {
            FuncDeclaration fd = ((sc).callsc != null && ((sc).callsc).func != null) ? ((sc).callsc).func : (sc).func;
            BytePtr s = null;
            if (fd != null)
            {
                BytePtr funcStr = pcopy(fd.toPrettyChars(false));
                OutBuffer buf = new OutBuffer();
                try {
                    functionToBufferWithIdent(fd.type.isTypeFunction(), buf, funcStr);
                    s = pcopy(buf.extractChars());
                }
                finally {
                }
            }
            else
            {
                s = pcopy(new BytePtr(""));
            }
            Expression e = new StringExp(loc, s);
            e = expressionSemantic(e, sc);
            e.type = Type.tstring;
            return e;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PrettyFuncInitExp() {}

        public PrettyFuncInitExp copy() {
            PrettyFuncInitExp that = new PrettyFuncInitExp();
            that.subop = this.subop;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ObjcClassReferenceExp extends Expression
    {
        public ClassDeclaration classDeclaration;
        public  ObjcClassReferenceExp(Loc loc, ClassDeclaration classDeclaration) {
            super(loc, TOK.objcClassReference, 28);
            this.classDeclaration = classDeclaration;
            this.type = objc().getRuntimeMetaclass(classDeclaration).getType();
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ObjcClassReferenceExp() {}

        public ObjcClassReferenceExp copy() {
            ObjcClassReferenceExp that = new ObjcClassReferenceExp();
            that.classDeclaration = this.classDeclaration;
            that.op = this.op;
            that.size = this.size;
            that.parens = this.parens;
            that.type = this.type;
            that.loc = this.loc;
            return that;
        }
    }
}
