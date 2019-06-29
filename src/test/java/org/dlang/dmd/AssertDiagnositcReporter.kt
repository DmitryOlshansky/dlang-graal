package org.dlang.dmd

import org.dlang.dmd.root.BytePtr
import org.dlang.dmd.root.Slice

 class AssertDiagnosticReporter : errors.DiagnosticReporter() {
     override fun errorCount(): Int {
         throw AssertionError("Unreachable code!")
     }

     override fun warningCount(): Int {
         throw AssertionError("Unreachable code!")
     }

     override fun deprecationCount(): Int {
         throw AssertionError("Unreachable code!")
     }

     override fun error(_param_0: globals.Loc, _param_1: BytePtr, _param_2: Slice<Any>) {
         throw AssertionError("Unreachable code!")
     }

     override fun errorSupplemental(_param_0: globals.Loc, _param_1: BytePtr, _param_2: Slice<Any>) {
         throw AssertionError("Unreachable code!")
     }

     override fun warning(_param_0: globals.Loc, _param_1: BytePtr, _param_2: Slice<Any>) {
         throw AssertionError("Unreachable code!")
     }

     override fun warningSupplemental(_param_0: globals.Loc, _param_1: BytePtr, _param_2: Slice<Any>) {
         throw AssertionError("Unreachable code!")
     }

     override fun deprecation(_param_0: globals.Loc, _param_1: BytePtr, _param_2: Slice<Any>) {
         throw AssertionError("Unreachable code!")
     }

     override fun deprecationSupplemental(_param_0: globals.Loc, _param_1: BytePtr, _param_2: Slice<Any>) {
         throw AssertionError("Unreachable code!")
     }

     override fun copy(): errors.DiagnosticReporter {
         return this
     }
 }