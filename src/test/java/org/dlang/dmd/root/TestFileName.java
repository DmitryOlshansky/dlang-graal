package org.dlang.dmd.root;

import junit.framework.TestCase;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.filename.FileName.*;

public class TestFileName extends TestCase {

    public void test_1() {
        assertEquals(ext( new ByteSlice("/foo/bar/dmd.conf")),  new ByteSlice("conf"));
        assertEquals(ext( new ByteSlice("object.o")),  new ByteSlice("o"));
        assertEquals(ext(new ByteSlice("/foo/bar/dmd")), new ByteSlice());
        assertEquals(ext(new ByteSlice(".objdir.o/object")), new ByteSlice());
        assertEquals(ext(new ByteSlice()), new ByteSlice());
    }


    public void test_2() {
        assertEquals(removeExt( new ByteSlice("/foo/bar/object.d")),  new ByteSlice("/foo/bar/object"));
        assertEquals(removeExt( new ByteSlice("/foo/bar/frontend.di")),  new ByteSlice("/foo/bar/frontend"));
    }


    public void test_3() {
        assertEquals(name( new ByteSlice("/foo/bar/object.d")),  new ByteSlice("object.d"));
        assertEquals(name( new ByteSlice("/foo/bar/frontend.di")),  new ByteSlice("frontend.di"));
    }


    public void test_4() {
        assertEquals(path( new ByteSlice("/foo/bar")),  new ByteSlice("/foo"));
        assertEquals(path( new ByteSlice("foo")),  new ByteSlice(""));
    }

    public void test_5() {
        assertEquals(FileName.combine( new ByteSlice("foo"),  new ByteSlice("bar")),  new ByteSlice("foo/bar"));
        assertEquals(FileName.combine( new ByteSlice("foo/"),  new ByteSlice("bar")),  new ByteSlice("foo/bar"));
    }


    public void test_6() {
        assertEquals(FileName.defaultExt( new ByteSlice("/foo/object.d"),  new ByteSlice("d")),  new ByteSlice("/foo/object.d"));
        assertEquals(FileName.defaultExt( new ByteSlice("/foo/object"),  new ByteSlice("d")),  new ByteSlice("/foo/object.d"));
        assertEquals(FileName.defaultExt( new ByteSlice("/foo/bar.d"),  new ByteSlice("o")),  new ByteSlice("/foo/bar.d"));
    }

    public void test_7() {
        assertEquals(FileName.forceExt( new ByteSlice("/foo/object.d"),  new ByteSlice("d")),  new ByteSlice("/foo/object.d"));
        assertEquals(FileName.forceExt( new ByteSlice("/foo/object"),  new ByteSlice("d")),  new ByteSlice("/foo/object.d"));
        assertEquals(FileName.forceExt( new ByteSlice("/foo/bar.d"),  new ByteSlice("o")),  new ByteSlice("/foo/bar.o"));
    }
    
    public void test_8() {
        assertTrue(!(FileName.equalsExt( new ByteSlice("foo.bar"),  new ByteSlice("d"))));
        assertTrue(FileName.equalsExt( new ByteSlice("foo.bar"),  new ByteSlice("bar")));
        assertTrue(FileName.equalsExt( new ByteSlice("object.d"),  new ByteSlice("d")));
        assertTrue(!(FileName.equalsExt( new ByteSlice("object"),  new ByteSlice("d"))));
    }
}
