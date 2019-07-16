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
        public Ref<Ptr<FileName>> file = ref(null);
        public Ref<Ptr<FileBuffer>> buffer = ref(null);
        public Ref<Slice<ByteSlice>> lines = ref(new RawSlice<ByteSlice>());
        // Erasure: __ctor<Array>
        public  FileAndLines(ByteSlice filename) {
            this.file.value = pcopy((refPtr(new FileName(filename))));
            this.readAndSplit();
        }

        // Erasure: readAndSplit<>
        public  void readAndSplit() {
            File.ReadResult readResult = File.read((this.file.value.get()).toChars()).copy();
            this.buffer.value = pcopy((refPtr(new FileBuffer(readResult.extractData()))));
            BytePtr buf = pcopy((this.buffer.value.get()).data.getPtr(0));
            for (; buf.get() != 0;){
                BytePtr prevBuf = pcopy(buf);
                for (; ((buf.get() & 0xFF) != 10) && ((buf.get() & 0xFF) != 13);buf.postInc()){
                    if (buf.get() == 0)
                    {
                        break;
                    }
                }
                if (((buf.get() & 0xFF) == 13) && (((buf.plus(1)).get() & 0xFF) == 10))
                {
                    buf.postInc();
                }
                this.lines.value.append(toByteSlice(prevBuf.slice(0,((buf.minus(prevBuf))))));
                buf.postInc();
            }
        }


        public FileAndLines() {}

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
        // Erasure: addOrGetFile<Array>
        public  FileAndLines addOrGetFile(ByteSlice file) {
            {
                Ptr<StringValue> payload = this.files.lookup(file);
                if ((payload) != null)
                {
                    if ((payload != null))
                    {
                        return ((FileAndLines)(payload.get()).ptrvalue);
                    }
                }
            }
            FileAndLines lines = new FileAndLines(file);
            this.files.insert(file, lines);
            return lines;
        }

        public static FileCache fileCache = new FileCache(new StringTable(null, 0, null, 0, 0, 0, 0));
        // Erasure: _init<>
        public static void _init() {
            fileCache.initialize();
        }

        // Erasure: initialize<>
        public  void initialize() {
            this.files._init(0);
        }

        // Erasure: deinitialize<>
        public  void deinitialize() {
            Function1<Ptr<StringValue>,Integer> __foreachbody1 = new Function1<Ptr<StringValue>,Integer>() {
                public Integer invoke(Ptr<StringValue> sv) {
                 {
                    Ref<Ptr<StringValue>> sv_ref = ref(sv);
                    destroy(sv_ref);
                    return 0;
                }}

            };
            this.files.opApply(__foreachbody1);
            this.files.reset(0);
        }

        public FileCache(){ }
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
