package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;

import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;

public class filecache {

    public static class FileAndLines extends Object
    {
        public FileName file;
        public FileBuffer buffer;
        public Slice<ByteSlice> lines;
        public  FileAndLines(ByteSlice filename) {
            this.file = new FileName(filename);
            this.readAndSplit();
        }

        public  void readAndSplit() {
            ReadResult readResult = File.read((this.file).toChars());
            this.buffer = new FileBuffer(readResult.extractData());
            BytePtr buf = (this.buffer).data.toBytePtr();
            for (; (buf.get(0)) != 0;){
                BytePtr prevBuf = buf;
                for (; buf.get(0) != (byte)10 && buf.get(0) != (byte)13;buf.postInc()){
                    if (!((buf.get(0)) != 0))
                        break;
                }
                if (buf.get(0) == (byte)13 && (buf.plus((byte)1)).get(0) == (byte)10)
                    buf.postInc();
                this.lines.append(prevBuf.slice(0,((buf.minus(prevBuf)) / 1)).toByteSlice());
                buf.postInc();
            }
        }

    }
    public static class FileCache
    {
        public StringTable files;
        public  FileAndLines addOrGetFile(ByteSlice file) {
            StringValue payload = this.files.lookup(file);
            if (payload != null)
            {
                if (payload != null)
                    return (FileAndLines)(payload).ptrvalue;
            }
            FileAndLines lines = new FileAndLines(file);
            this.files.insert(file, lines);
            return lines;
        }

        public static FileCache fileCache = new FileCache(new StringTable(null, 0, null, 0, 0, 0, 0));
        public static void _init() {
            FileCache.fileCache.initialize();
        }

        public  void initialize() {
            this.files._init(0);
        }

        public  void deinitialize() {
            Function1<StringValue,Integer> __foreachbody1 = new Function1<StringValue,Integer>(){
                public Integer invoke(StringValue sv){
                    destroy(sv);
                    return 0;
                }
            };
            this.files.opApply(__foreachbody1);
            this.files.reset(0);
        }

        public FileCache(){}
        public FileCache(StringTable files) {
            this.files = files;
        }

        public FileCache opAssign(FileCache that) {
            this.files = that.files;
            return this;
        }
    }
}
