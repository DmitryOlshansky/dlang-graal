package org.dlang.dmd.root

import junit.framework.TestCase
import org.dlang.dmd.root.FileName

class TestFileName : TestCase() {
    fun testAbsolute() {
        assertTrue(FileName.absolute(ByteSlice("/")))
        assertFalse(FileName.absolute(ByteSlice("")))
    }

    fun testExt() {
        assert(ext("/foo/bar/dmd.conf"[]) == "conf");
        assert(ext("object.o"[]) == "o");
        assert(ext("/foo/bar/dmd"[]) == null);
        assert(ext(".objdir.o/object"[]) == null);
        assert(ext([]) == null);
    }

    fun testRemoveExt() {
        assert(removeExt("/foo/bar/object.d"[]) == "/foo/bar/object");
        assert(removeExt("/foo/bar/frontend.di"[]) == "/foo/bar/frontend");
    }
}