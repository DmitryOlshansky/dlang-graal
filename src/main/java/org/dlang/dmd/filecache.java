package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
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
            File.ReadResult readResult = File.read((this.file).toChars()).copy();
            this.buffer = new FileBuffer(readResult.extractData());
            BytePtr buf = pcopy(toBytePtr((this.buffer).data));
            for (; (buf.get()) != 0;){
                BytePtr prevBuf = pcopy(buf);
                for (; (buf.get() & 0xFF) != 10 && (buf.get() & 0xFF) != 13;buf.postInc()){
                    if (!((buf.get()) != 0))
                        break;
                }
                if ((buf.get() & 0xFF) == 13 && ((buf.plus(1)).get() & 0xFF) == 10)
                    buf.postInc();
                this.lines.append(toByteSlice(prevBuf.slice(0,((buf.minus(prevBuf))))));
                buf.postInc();
            }
        }


        protected FileAndLines() {}

        public FileAndLines copy() {
            FileAndLines that = new FileAndLines();
            that.file = this.file;
            that.buffer = this.buffer;
            that.lines = this.lines;
            return that;
        }
    }
    public static class FileCache
    {
        public StringTable files = new StringTable();
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
            fileCache.initialize();
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

        public FileCache(){
            files = new StringTable();
        }
        public FileCache copy(){
            FileCache r = new FileCache();
            r.files = files.copy();
            return r;
        }
        public FileCache(StringTable files) {
            this.files = files;
        }

        public FileCache opAssign(FileCache that) {
            this.files = that.files;
            return this;
        }
    }
}
