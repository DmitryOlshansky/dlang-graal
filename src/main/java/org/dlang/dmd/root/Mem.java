package org.dlang.dmd.root;

import static org.dlang.dmd.root.ShimsKt.*;
public class Mem {
    public static BytePtr xmalloc(int size) {
        return new BytePtr(size);
    }
    public static BytePtr xcalloc(int size, int item) {
        return new BytePtr(size*item);
    }

    public static void xfree(BytePtr p) { }

    public static void xfree(ByteSlice p) { }

    public static BytePtr xstrdup(BytePtr ptr) {
        return strdup(ptr);
    }
    public static ByteSlice xstrdup(ByteSlice s) {
        return strdup(s.ptr()).slice(0, s.getLength());
    }
}

