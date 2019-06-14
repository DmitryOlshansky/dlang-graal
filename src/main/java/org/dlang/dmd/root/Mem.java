package org.dlang.dmd.root;

public class Mem {
    public static BytePtr xmalloc(int size) {
        return new BytePtr(size);
    }

    public static void xfree(BytePtr p) { }

    public static BytePtr xstrdup(BytePtr ptr) {
        return org.dlang.dmd.root.ShimsKt.strdup(ptr);
    }
}

