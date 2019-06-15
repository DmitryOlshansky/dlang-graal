package org.dlang.dmd.root;

import junit.framework.TestCase;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.filename.FileName.*;

public class TestFileName extends TestCase {

    public void test_1() {
        assertTrue(__equals(ext( new ByteSlice("/foo/bar/dmd.conf")),  new ByteSlice("conf")));
        assertTrue(__equals(ext( new ByteSlice("object.o")),  new ByteSlice("o")));
        assertTrue(ext( new ByteSlice("/foo/bar/dmd")).equals(new ByteSlice()));
        assertTrue(ext( new ByteSlice(".objdir.o/object")).equals(new ByteSlice()));
        assertTrue(ext(new ByteSlice()).equals(new ByteSlice()));
    }


    public void test_2() {
        assertTrue(__equals(removeExt( new ByteSlice("/foo/bar/object.d")),  new ByteSlice("/foo/bar/object")));
        assertTrue(__equals(removeExt( new ByteSlice("/foo/bar/frontend.di")),  new ByteSlice("/foo/bar/frontend")));
    }


    public void test_3() {
        assertTrue(__equals(name( new ByteSlice("/foo/bar/object.d")),  new ByteSlice("object.d")));
        assertTrue(__equals(name( new ByteSlice("/foo/bar/frontend.di")),  new ByteSlice("frontend.di")));
    }


    public void test_4() {
        assertTrue(__equals(path( new ByteSlice("/foo/bar")),  new ByteSlice("/foo")));
        assertTrue(__equals(path( new ByteSlice("foo")),  new ByteSlice("")));
    }

    public void test_5() {
        assertTrue(__equals(FileName.combine( new ByteSlice("foo"),  new ByteSlice("bar")),  new ByteSlice("foo/bar")));
        assertTrue(__equals(FileName.combine( new ByteSlice("foo/"),  new ByteSlice("bar")),  new ByteSlice("foo/bar")));
    }


    public void test_6() {
        assertTrue(__equals(FileName.defaultExt( new ByteSlice("/foo/object.d"),  new ByteSlice("d")),  new ByteSlice("/foo/object.d")));
        assertTrue(__equals(FileName.defaultExt( new ByteSlice("/foo/object"),  new ByteSlice("d")),  new ByteSlice("/foo/object.d")));
        assertTrue(__equals(FileName.defaultExt( new ByteSlice("/foo/bar.d"),  new ByteSlice("o")),  new ByteSlice("/foo/bar.d")));
    }

    public void test_7() {
        assertTrue(__equals(FileName.forceExt( new ByteSlice("/foo/object.d"),  new ByteSlice("d")),  new ByteSlice("/foo/object.d")));
        assertTrue(__equals(FileName.forceExt( new ByteSlice("/foo/object"),  new ByteSlice("d")),  new ByteSlice("/foo/object.d")));
        assertTrue(__equals(FileName.forceExt( new ByteSlice("/foo/bar.d"),  new ByteSlice("o")),  new ByteSlice("/foo/bar.o")));
    }
    
    public void test_8() {
        assertTrue(!(FileName.equalsExt( new ByteSlice("foo.bar"),  new ByteSlice("d"))));
        assertTrue(FileName.equalsExt( new ByteSlice("foo.bar"),  new ByteSlice("bar")));
        assertTrue(FileName.equalsExt( new ByteSlice("object.d"),  new ByteSlice("d")));
        assertTrue(!(FileName.equalsExt( new ByteSlice("object"),  new ByteSlice("d"))));
    }
}
