package org.dlang.dmd.root;

public class Mem {
    public static BytePtr xmalloc(int size) {
        return new BytePtr(size);
    }
}