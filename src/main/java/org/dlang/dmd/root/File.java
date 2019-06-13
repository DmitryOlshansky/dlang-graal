package org.dlang.dmd.root;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

///
public class  File {
    ///
    public static class ReadResult {
        public boolean success;
        public FileBuffer buffer;

        /// Transfers ownership of the buffer to the caller.
        public ByteSlice extractData() {
            return buffer.extractData();
        }
    }

    /// Read the full content of a file.
    public static ReadResult read(BytePtr name) {
        ReadResult result = new ReadResult();
        java.io.File f = new java.io.File(name.toString());
        int size = (int)f.length();
        byte[] buffer = new byte[size + 2];
        try {
            try (FileInputStream r = new FileInputStream(f)) {
                if (r.read(buffer, 0, size) != size) throw new IOException("Short read?");
                // Always store a wchar ^Z past end of buffer so scanner has a sentinel
                buffer[size] = 0; // ^Z is obsolete, use 0
                buffer[size + 1] = 0;
                result.success = true;
                result.buffer.data = new ByteSlice(buffer);
                return result;
            }
        }
        catch (IOException e) {
            result.success = false;
            return result;
        }

    }

    /// Write a file, returning `true` on success.
    public static boolean write(BytePtr name, ByteSlice data) {
        java.io.File f = new java.io.File(name.toString());
        int size = (int)f.length();
        try {
            try (FileOutputStream w = new FileOutputStream(f)) {
                w.write(data.getData(), data.getBeg(), data.getLength());
            }
            return true;
        }
        catch (IOException e) {
            return false;
        }

    }

    public static boolean write(ByteSlice name, ByteSlice data) {
        return write(name.ptr(), data);
    }

    public static boolean write(BytePtr name, BytePtr data, int size) {
        return write(name, data.slice(0, size));
    }

    /// Delete a file.
    public static void remove(BytePtr name) {
        java.io.File f = new java.io.File(name.toString());
        f.delete();
    }
}
