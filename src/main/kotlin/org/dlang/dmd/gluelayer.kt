package org.dlang.dmd

import org.dlang.dmd.root.Ptr


object gluelayer {

    interface TYPE
    interface Symbol
    interface code

    fun asmSemantic(s: statement.AsmStatement, sc: Ptr<dscope.Scope>): statement.Statement? {
        sc.get()!!.func.hasReturnExp = 8
        return null
    }

    abstract class ObjcGlue {

        abstract fun copy(): ObjcGlue

        companion object {
            fun initialize() {}
        }
    }
}
