package org.dlang.dmd.root;

public class FileBuffer {

    /// Owns a file buffer
    public ByteSlice data;

    /// Transfers ownership of the buffer to the caller.
    public ByteSlice extractData()
    {
        ByteSlice result = data;
        data = null;
        return result;
    }

    public FileBuffer()
    {
        this.data = new ByteSlice();
    }

    public FileBuffer(ByteSlice data) {
        this.data = data;
    }

    public static FileBuffer create()
    {
        return new FileBuffer(null);
    }
}

