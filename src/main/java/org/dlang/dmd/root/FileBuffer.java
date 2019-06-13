package org.dlang.dmd.root;

public class FileBuffer {

    /// Owns a file buffer
    ByteSlice data;

    /// Transfers ownership of the buffer to the caller.
    ByteSlice extractData()
    {
        ByteSlice result = data;
        data = null;
        return result;
    }

    public FileBuffer(ByteSlice data) {
        this.data = data;
    }

    static FileBuffer create()
    {
        return new FileBuffer(null);
    }
}

