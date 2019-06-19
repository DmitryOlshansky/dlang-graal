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
            ReadResult readResult = File.read((this.file).toChars()).copy();
            this.buffer = new FileBuffer(readResult.extractData());
            BytePtr buf = pcopy(toBytePtr((this.buffer).data));
            for (; (buf.get(0)) != 0;){
                BytePtr prevBuf = pcopy(buf);
                for (; (buf.get(0) & 0xFF) != 10 && (buf.get(0) & 0xFF) != 13;buf.postInc()){
                    if (!((buf.get(0)) != 0))
                        break;
                }
                if ((buf.get(0) & 0xFF) == 13 && ((buf.plus(1)).get(0) & 0xFF) == 10)
                    buf.postInc();
                this.lines.append(toByteSlice(prevBuf.slice(0,((buf.minus(prevBuf)) / 1))));
                buf.postInc();
            }
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
        // from template DArray!(BytePtr)
        public static class Array
        {
            public int lengthBytePtr;
            public Ptr<BytePtr> dataBytePtr;
            public int allocdimBytePtr;
            public int SMALLARRAYCAPBytePtr = 1;
            public Slice<BytePtr> smallarrayBytePtr = new Slice<BytePtr>(new BytePtr[1]);
            public  DArray<BytePtr>BytePtr(int dim) {
                this.reserve(dim);
                this.length = dim;
            }

            public  ByteSlice asStringBytePtr() {
                throw new AssertionError("Unreachable code!");
            }

            public  BytePtr toCharsBytePtr() {
                return toBytePtr(this.asStringBytePtr());
            }

            public  DArray<BytePtr> pushBytePtr(BytePtr ptr) {
                this.reserve(1);
                this.data.set(this.length++, ptr);
                return this;
            }

            public  DArray<BytePtr> pushSliceBytePtr(Slice<BytePtr> a) {
                int oldLengthBytePtr = this.length;
                this.setDim(oldLength + a.getLength());
                memcpy((this.data.plus(oldLength * 4)), toPtr<BytePtr>(a), a.getLength() * 4);
                return this;
            }

            public  DArray<BytePtr> appendBytePtr(DArray<BytePtr> a) {
                this.insert(this.length, a);
                return this;
            }

            public  void reserveBytePtr(int nentries) {
                if (this.allocdim - this.length < nentries)
                {
                    if (this.allocdim == 0)
                    {
                        if (nentries <= 1)
                        {
                            this.allocdim = 1;
                            this.data = pcopy((this.smallarray));
                        }
                        else
                        {
                            this.allocdim = nentries;
                            this.data = pcopy((toPtr<BytePtr>(Mem.xmalloc(this.allocdim * 4))));
                        }
                    }
                    else if (this.allocdim == 1)
                    {
                        this.allocdim = this.length + nentries;
                        this.data = pcopy((toPtr<BytePtr>(Mem.xmalloc(this.allocdim * 4))));
                        memcpy(this.data, this.smallarray, this.length * 4);
                    }
                    else
                    {
                        int incrementBytePtr = this.length / 2;
                        if (nentries > increment)
                            increment = nentries;
                        this.allocdim = this.length + increment;
                        this.data = pcopy((toPtr<BytePtr>(Mem.xrealloc(this.data, this.allocdim * 4))));
                    }
                }
            }

            public  void removeBytePtr(int i) {
                if ((this.length - i - 1) != 0)
                    memmove((this.data.plus(i * 4)), (this.data.plus(i * 4).plus(4)), (this.length - i - 1) * 4);
                this.length--;
            }

            public  void insertBytePtr(int index, DArray<BytePtr> a) {
                if (a != null)
                {
                    int dBytePtr = (a).length;
                    this.reserveBytePtr(d);
                    if (this.length != index)
                        memmove((this.data.plus(index * 4).plus(d * 4)), (this.data.plus(index * 4)), (this.length - index) * 4);
                    memcpy((this.data.plus(index * 4)), (a).data, d * 4);
                    this.length += d;
                }
            }

            public  void insertBytePtr(int index, BytePtr ptr) {
                this.reserveBytePtr(1);
                memmove((this.data.plus(index * 4).plus(4)), (this.data.plus(index * 4)), (this.length - index) * 4);
                this.data.set(index, ptr);
                this.length++;
            }

            public  void setDimBytePtr(int newdim) {
                if (this.length < newdim)
                {
                    this.reserveBytePtr(newdim - this.length);
                }
                this.length = newdim;
            }

            public  BytePtr opIndexBytePtr(int i) {
                return this.data.get(i);
            }

            public  Ptr<BytePtr> tdataBytePtr() {
                return this.data;
            }

            public  DArray<BytePtr> copyBytePtr() {
                DArray<BytePtr> aBytePtr = new DArray<BytePtr>();
                (a).setDimBytePtr(this.length);
                memcpy((a).data, this.data, this.length * 4);
                return a;
            }

            public  void shiftBytePtr(BytePtr ptr) {
                this.reserveBytePtr(1);
                memmove((this.data.plus(4)), this.data, this.length * 4);
                this.data.set(0, ptr);
                this.length++;
            }

            public  void zeroBytePtr() {
                this.data.slice(0,this.length) = null;
            }

            public  BytePtr popBytePtr() {
                return this.data.get(this.length -= 1);
            }

            public  Slice<BytePtr> opSliceBytePtr() {
                return this.data.slice(0,this.length);
            }

            public  Slice<BytePtr> opSliceBytePtr(int a, int b) {
                assert(a <= b && b <= this.length);
                return this.data.slice(a,b);
            }

            public int lengthBytePtr;
            public int lengthBytePtr;
            public DArray(){
            }
            public DArray copy(){
                DArray r = new DArray();
                r.length = length;
                r.data = data;
                r.allocdim = allocdim;
                r.SMALLARRAYCAP = SMALLARRAYCAP;
                r.smallarray = smallarray;
                r.length = length;
                r.length = length;
                return r;
            }
        }

        // from template DArray!(Identifier)
        public static class Array
        {
            public int lengthIdentifier;
            public Identifier dataIdentifier;
            public int allocdimIdentifier;
            public int SMALLARRAYCAPIdentifier = 1;
            public Slice<Identifier> smallarrayIdentifier = new Slice<Identifier>(new Identifier[1]);
            public  DArray<Identifier>Identifier(int dim) {
                this.reserve(dim);
                this.length = dim;
            }

            public  ByteSlice asStringIdentifier() {
                Slice<ByteSlice> bufIdentifier = (toPtr<ByteSlice>(Mem.xcalloc(8, this.length))).slice(0,this.length).copy();
                int lenIdentifier = 2;
                {
                    int __key35Identifier = 0;
                    int __limit36Identifier = this.length;
                    for (; __key35 < __limit36;__key35 += 1) {
                        int uIdentifier = __key35;
                        buf.set(u, this.data.get(u).asString());
                        len += buf.get(u).getLength() + 1;
                    }
                }
                ByteSlice strIdentifier = (toBytePtr(Mem.xmalloc(len))).slice(0,len).copy();
                str.set(0, (byte)91);
                BytePtr pIdentifier = pcopy(toBytePtr(str).plus(1));
                {
                    int __key37Identifier = 0;
                    int __limit38Identifier = this.length;
                    for (; __key37 < __limit38;__key37 += 1) {
                        int uIdentifier = __key37;
                        if ((u) != 0)
                            p.postInc().set(0, (byte)44);
                        memcpy(p, toBytePtr(buf.get(u)), buf.get(u).getLength());
                        p.plusAssign(buf.get(u).getLength() * 1);
                    }
                }
                p.postInc().set(0, (byte)93);
                p.set(0, (byte)0);
                assert(((p.minus(toBytePtr(str))) / 1) == str.getLength() - 1);
                Mem.xfree(toPtr<ByteSlice>(buf));
                return str.slice(0,str.getLength() - 1);
            }

            public  BytePtr toCharsIdentifier() {
                return toBytePtr(this.asStringIdentifier());
            }

            public  DArray<Identifier> pushIdentifier(Identifier ptr) {
                this.reserve(1);
                this.data.set(this.length++, ptr);
                return this;
            }

            public  DArray<Identifier> pushSliceIdentifier(Slice<Identifier> a) {
                int oldLengthIdentifier = this.length;
                this.setDim(oldLength + a.getLength());
                memcpy((this.data.plus(oldLength * 4)), toIdentifier(a), a.getLength() * 4);
                return this;
            }

            public  DArray<Identifier> appendIdentifier(DArray<Identifier> a) {
                this.insert(this.length, a);
                return this;
            }

            public  void reserveIdentifier(int nentries) {
                if (this.allocdim - this.length < nentries)
                {
                    if (this.allocdim == 0)
                    {
                        if (nentries <= 1)
                        {
                            this.allocdim = 1;
                            this.data = pcopy((this.smallarray));
                        }
                        else
                        {
                            this.allocdim = nentries;
                            this.data = pcopy((toIdentifier(Mem.xmalloc(this.allocdim * 4))));
                        }
                    }
                    else if (this.allocdim == 1)
                    {
                        this.allocdim = this.length + nentries;
                        this.data = pcopy((toIdentifier(Mem.xmalloc(this.allocdim * 4))));
                        memcpy(this.data, this.smallarray, this.length * 4);
                    }
                    else
                    {
                        int incrementIdentifier = this.length / 2;
                        if (nentries > increment)
                            increment = nentries;
                        this.allocdim = this.length + increment;
                        this.data = pcopy((toIdentifier(Mem.xrealloc(this.data, this.allocdim * 4))));
                    }
                }
            }

            public  void removeIdentifier(int i) {
                if ((this.length - i - 1) != 0)
                    memmove((this.data.plus(i * 4)), (this.data.plus(i * 4).plus(4)), (this.length - i - 1) * 4);
                this.length--;
            }

            public  void insertIdentifier(int index, DArray<Identifier> a) {
                if (a != null)
                {
                    int dIdentifier = (a).length;
                    this.reserveIdentifier(d);
                    if (this.length != index)
                        memmove((this.data.plus(index * 4).plus(d * 4)), (this.data.plus(index * 4)), (this.length - index) * 4);
                    memcpy((this.data.plus(index * 4)), (a).data, d * 4);
                    this.length += d;
                }
            }

            public  void insertIdentifier(int index, Identifier ptr) {
                this.reserveIdentifier(1);
                memmove((this.data.plus(index * 4).plus(4)), (this.data.plus(index * 4)), (this.length - index) * 4);
                this.data.set(index, ptr);
                this.length++;
            }

            public  void setDimIdentifier(int newdim) {
                if (this.length < newdim)
                {
                    this.reserveIdentifier(newdim - this.length);
                }
                this.length = newdim;
            }

            public  Identifier opIndexIdentifier(int i) {
                return this.data.get(i);
            }

            public  Identifier tdataIdentifier() {
                return this.data;
            }

            public  DArray<Identifier> copyIdentifier() {
                DArray<Identifier> aIdentifier = new DArray<Identifier>();
                (a).setDimIdentifier(this.length);
                memcpy((a).data, this.data, this.length * 4);
                return a;
            }

            public  void shiftIdentifier(Identifier ptr) {
                this.reserveIdentifier(1);
                memmove((this.data.plus(4)), this.data, this.length * 4);
                this.data.set(0, ptr);
                this.length++;
            }

            public  void zeroIdentifier() {
                this.data.slice(0,this.length) = null;
            }

            public  Identifier popIdentifier() {
                return this.data.get(this.length -= 1);
            }

            public  Slice<Identifier> opSliceIdentifier() {
                return this.data.slice(0,this.length);
            }

            public  Slice<Identifier> opSliceIdentifier(int a, int b) {
                assert(a <= b && b <= this.length);
                return this.data.slice(a,b);
            }

            public int lengthIdentifier;
            public int lengthIdentifier;
            public DArray(){
            }
            public DArray copy(){
                DArray r = new DArray();
                r.length = length;
                r.data = data;
                r.allocdim = allocdim;
                r.SMALLARRAYCAP = SMALLARRAYCAP;
                r.smallarray = smallarray;
                r.length = length;
                r.length = length;
                return r;
            }
        }

        // from template xversion!("linux")
        static boolean xversion_linux = true;

        // from template xversion!("OSX")
        static boolean xversion_OSX = false;

        // from template xversion!("FreeBSD")
        static boolean xversion_FreeBSD = false;

        // from template xversion!("OpenBSD")
        static boolean xversion_OpenBSD = false;

        // from template xversion!("Solaris")
        static boolean xversion_Solaris = false;

        // from template xversion!("Windows")
        static boolean xversion_Windows = false;

        // from template xversion!("DragonFlyBSD")
        static boolean xversion_DragonFlyBSD = false;

}
