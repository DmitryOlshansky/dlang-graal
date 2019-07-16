package org.dlang.dmd.root;

import kotlin.jvm.functions.*;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.utils.*;

public class filename {



    public static class FileName
    {
        public ByteSlice str;

        public  FileName() {}

        public  FileName(ByteSlice str) {
            this.str = xarraydup(str);
        }

        public static boolean equals(BytePtr name1, BytePtr name2) {
            return FileName.equals(toDString(name1), toDString(name2));
        }

        public static boolean equals(ByteSlice name1, ByteSlice name2) {
            if (name1.getLength() != name2.getLength())
                return false;
            return __equals(name1, name2);
        }

        public static boolean absolute(BytePtr name) {
            return FileName.absolute(toDString(name));
        }

        public static boolean absolute(ByteSlice name) {
            if ((name.getLength()) == 0)
                return false;
            return name.get(0) == (byte)47;
        }

        public static void test_0() {
            assert((FileName.absolute( new ByteSlice("/")) ? 1 : 0) == 1);
            assert((FileName.absolute( new ByteSlice("")) ? 1 : 0) == 0);
        }
        public static BytePtr toAbsolute(BytePtr name, BytePtr base) {
            ByteSlice name_ = toDString(name);
            ByteSlice base_ = base != null ? toDString(base) : toDString(getcwd(null, 0));
            return FileName.absolute(name_) ? name : FileName.combine(base_, name_).ptr();
        }

        public static BytePtr ext(BytePtr str) {
            return FileName.ext(toDString(str)).ptr();
        }

        public static ByteSlice ext(ByteSlice str) {
            {
                ByteSlice __r46 = str;
                int __key45 = __r46.getLength();
                for (; (__key45--) != 0;) {
                    byte e = __r46.get(__key45);
                    int idx = __key45;
                    switch ((int)e)
                    {
                        case (byte)46:
                            return str.slice(idx + 1,str.getLength());
                            case (byte)47:
                                return new ByteSlice();
                        default:
                        {
                            continue;
                        }
                    }
                }
            }
            return new ByteSlice();
        }


        public  BytePtr ext() {
            return FileName.ext(this.str).ptr();
        }

        public static BytePtr removeExt(BytePtr str) {
            return FileName.removeExt(toDString(str)).ptr();
        }

        public static ByteSlice removeExt(ByteSlice str) {
            ByteSlice e = FileName.ext(str);
            if ((e.getLength()) != 0)
            {
                int len = str.getLength() - e.getLength() - 1;
                BytePtr n = Mem.xmalloc(len + 1);
                memcpy(n, str.ptr(), len);
                n.set(len, (byte)0);
                return n.slice(0,len);
            }
            return Mem.xstrdup(str.ptr()).slice(0,str.getLength());
        }

        public static BytePtr name(BytePtr str) {
            return FileName.name(toDString(str)).ptr();
        }

        public static ByteSlice name(ByteSlice str) {
            {
                ByteSlice __r48 = str;
                int __key47 = __r48.getLength();
                for (; (__key47--) != 0;) {
                    byte e = __r48.get(__key47);
                    int idx = __key47;
                    switch ((int)e)
                    {
                        case (byte)47:
                            return str.slice(idx + 1,str.getLength());
                        default:
                        {
                            break;
                        }
                    }
                }
            }
            return str;
        }

        public  BytePtr name() {
            return FileName.name(this.str).ptr();
        }

        public static BytePtr path(BytePtr str) {
            return FileName.path(toDString(str)).ptr();
        }

        public static ByteSlice path(ByteSlice str) {
            ByteSlice n = FileName.name(str);
            boolean hasTrailingSlash = false;
            if (n.getLength() < str.getLength())
            {
                if (str.get(str.getLength() - n.getLength() - (byte)1) == (byte)47)
                    hasTrailingSlash = true;
            }
            int pathlen = str.getLength() - n.getLength() - (hasTrailingSlash ? 1 : 0);
            BytePtr path = Mem.xmalloc(pathlen + 1);
            memcpy(path, str.ptr(), pathlen);
            path.set(pathlen, (byte)0);
            return path.slice(0,pathlen);
        }

        public static ByteSlice replaceName(ByteSlice path, ByteSlice name) {
            if (FileName.absolute(name))
                return name;
            ByteSlice n = FileName.name(path);
            if (__equals(n, path))
                return name;
            return FileName.combine(path.slice(0,path.getLength() - n.getLength()), name);
        }

        public static BytePtr combine(BytePtr path, BytePtr name) {
            if (path == null)
                return name;
            return FileName.combine(toDString(path), toDString(name)).ptr();
        }

        public static ByteSlice combine(ByteSlice path, ByteSlice name) {
            if ((path.getLength()) == 0)
                return name;
            BytePtr f = Mem.xmalloc(path.getLength() + 1 + name.getLength() + 1);
            memcpy(f, path.ptr(), path.getLength());
            boolean trailingSlash = false;
            if (path.get(path.getLength() - (byte)1) != (byte)47)
            {
                f.set(path.getLength(), (byte)47);
                trailingSlash = true;
            }
            int len = path.getLength() + (trailingSlash ? 1 : 0);
            memcpy((f.plus(len)), name.ptr(), name.getLength());
            f.set((len + name.getLength()), (byte)0);
            return f.slice(0,len + name.getLength());
        }


        public static BytePtr buildPath(BytePtr path, Slice<BytePtr> names) {
            {
                Slice<BytePtr> __r49 = names;
                int __key50 = 0;
                for (; __key50 < __r49.getLength();__key50 += 1) {
                    BytePtr name = __r49.get(__key50);
                    path = FileName.combine(path, name);
                }
            }
            return path;
        }

        public static DArray<BytePtr> splitPath(BytePtr path) {
            DArray<BytePtr> array = new DArray<BytePtr>();
            Function1<BytePtr,Integer> sink = new Function1<BytePtr,Integer>(){
                public Integer invoke(BytePtr p){
                    (array).push(p);
                    return 0;
                }
            };
            FileName.splitPath(sink, path);
            return array;
        }

        public static void splitPath(Function1<BytePtr,Integer> sink, BytePtr path) {
            if (path != null)
            {
                BytePtr p = path;
                OutBuffer buf = new OutBuffer();
                byte c = (byte)255;
                do
                {
                    BytePtr home = null;
                    boolean instring = false;
                    for (; (isspace((int)p.get(0))) != 0;) {
                        p.plusAssign(1);
                    }
                    buf.reserve(8);
                    for (; ;p.plusAssign(1)){
                        c = p.get(0);
                        switch ((int)c)
                        {
                            case (byte)34:
                                instring  = !instring;
                                continue;
                                case (byte)58:
                                p.postInc();
                                break;
                            case (byte)26:
                            case (byte)0:
                                break;
                            case (byte)13:
                                continue;
                            case (byte)126:
                                if (home == null)
                                    home = getenv(new BytePtr("HOME"));
                                if (home != null)
                                    buf.writestring(home);
                                else
                                    buf.writeByte(126);
                                continue;
                            default:
                            {
                                buf.writeByte((int)c);
                                continue;
                            }
                        }
                        break;
                    }
                    if ((buf.offset) != 0)
                    {
                        if ((sink.invoke(buf.extractChars())) != 0)
                            break;
                    }
                }
                while ((c) != 0);
            }
        }

        public static ByteSlice addExt(ByteSlice name, ByteSlice ext) {
            int len = name.getLength() + ext.getLength() + 2;
            BytePtr s = Mem.xmalloc(len);
            name.copyTo(s.slice(0,name.getLength()));
            s.set(name.getLength(), (byte)46);
            ext.copyTo(s.slice(name.getLength() + 1,len - 1));
            s.set((len - 1), (byte)0);
            return s.slice(0,len - 1);
        }

        public static BytePtr defaultExt(BytePtr name, BytePtr ext) {
            return FileName.defaultExt(toDString(name), toDString(ext)).ptr();
        }

        public static ByteSlice defaultExt(ByteSlice name, ByteSlice ext) {
            ByteSlice e = FileName.ext(name);
            if ((e.getLength()) != 0)
                return xarraydup(name);
            return FileName.addExt(name, ext);
        }

        public static BytePtr forceExt(BytePtr name, BytePtr ext) {
            return FileName.forceExt(toDString(name), toDString(ext)).ptr();
        }

        public static ByteSlice forceExt(ByteSlice name, ByteSlice ext) {
            ByteSlice e = FileName.ext(name);
            if (e.getLength() != 0)
                return FileName.addExt(name.slice(0,name.getLength() - e.getLength() - 1), ext);
            return FileName.defaultExt(name, ext);
        }

        public static boolean equalsExt(BytePtr name, BytePtr ext) {
            return FileName.equalsExt(toDString(name), toDString(ext));
        }

        public static boolean equalsExt(ByteSlice name, ByteSlice ext) {
            ByteSlice e = FileName.ext(name);
            if ((e.getLength()) == 0 && (ext.getLength()) == 0)
                return true;
            if ((e.getLength()) == 0 || (ext.getLength()) == 0)
                return false;
            return FileName.equals(e, ext);
        }


        public  boolean equalsExt(BytePtr ext) {
            return FileName.equalsExt(this.str, toDString(ext));
        }

        public static BytePtr searchPath(DArray<BytePtr> path, BytePtr name, boolean cwd) {
            return FileName.searchPath(path, toDString(name), cwd).ptr();
        }

        public static ByteSlice searchPath(DArray<BytePtr> path, ByteSlice name, boolean cwd) {
            if (FileName.absolute(name))
            {
                return (FileName.exists(name)) != 0 ? name : new ByteSlice();
            }
            if (cwd)
            {
                if ((FileName.exists(name)) != 0)
                    return name;
            }
            if (path != null)
            {
                {
                    Slice<BytePtr> __r51 = (path).opSlice();
                    int __key52 = 0;
                    for (; __key52 < __r51.getLength();__key52 += 1) {
                        BytePtr p = __r51.get(__key52);
                        ByteSlice n = FileName.combine(toDString(p), name);
                        if ((FileName.exists(n)) != 0)
                            return n;
                        if (n.ptr() != name.ptr())
                        {
                            Mem.xfree(n.ptr());
                        }
                    }
                }
            }
            return new ByteSlice();
        }

        public static ByteSlice searchPath(BytePtr path, ByteSlice name, boolean cwd) {
            if (FileName.absolute(name))
            {
                return (FileName.exists(name)) != 0 ? name : new ByteSlice();
            }
            if (cwd)
            {
                if ((FileName.exists(name)) != 0)
                    return name;
            }
            if (path != null && (path.get(0)) != 0)
            {
                Ref<ByteSlice> result = ref(new ByteSlice());
                Function1<BytePtr,Integer> sink = new Function1<BytePtr,Integer>(){
                    public Integer invoke(BytePtr p){
                        ByteSlice n = FileName.combine(toDString(p), name);
                        Mem.xfree(p);
                        if ((FileName.exists(n)) != 0)
                        {
                            result.value = n;
                            return 1;
                        }
                        return 0;
                    }
                };
                FileName.splitPath(sink, path);
                return result.value;
            }
            return new ByteSlice();
        }

        public static BytePtr safeSearchPath(DArray<BytePtr> path, BytePtr name) {
            {
                BytePtr p = name;
                for (; (p.get(0)) != 0;p.postInc()){
                    byte c = p.get(0);
                    if (c == (byte)47 && p.get(1) == (byte)47)
                    {
                        return null;
                    }
                }
            }
            if (path != null)
            {
                {
                    int i = 0;
                    for (; i < (path).length;i++){
                        BytePtr cname = null;
                        BytePtr cpath = FileName.canonicalName((path).get(i));
                        if (cpath == null)
                        cname = FileName.canonicalName(FileName.combine(cpath, name));
                        if (cname == null)
                        if ((FileName.exists(cname)) != 0 && strncmp(cpath, cname, strlen(cpath)) == 0)
                        {
                            Mem.xfree(cpath);
                            BytePtr p = Mem.xstrdup(cname);
                            Mem.xfree(cname);
                            return p;
                        }
                        if (cpath != null)
                            Mem.xfree(cpath);
                        if (cname != null)
                            Mem.xfree(cname);
                    }
                }
            }
            return null;
        }

        public static int exists(BytePtr name) {
            return FileName.exists(toDString(name));
        }

        public static int exists(ByteSlice name) {
            if ((name.getLength()) == 0)
                return 0;
            java.io.File f = new java.io.File(name.toString());
            if (f.exists() && f.isDirectory()) return 2;
            else if (f.exists()) return 1;
            else return 0;
        }

        public static boolean ensurePathExists(ByteSlice path) {
            if ((path.getLength()) == 0)
                return true;
            if ((FileName.exists(path)) != 0)
                return true;
            ByteSlice p = FileName.path(path);
            if ((p.getLength()) != 0)
            {
                boolean r = FileName.ensurePathExists(p);
                Mem.xfree(p);
                if (!(r))
                    return r;
            }
            try {
                Files.createDirectory(Paths.get(p.toString()));
                return true;
            }
            catch(FileAlreadyExistsException exists) {
                return true;
            }
            catch (IOException io) {
                return false;
            }

        }

        public static boolean ensurePathExists(BytePtr path) {
            return FileName.ensurePathExists(toDString(path));
        }

        public static BytePtr canonicalName(BytePtr name) {
            return FileName.canonicalName(toDString(name)).ptr();
        }

        public static ByteSlice canonicalName(ByteSlice name) {
            try{
                ByteSlice path = new ByteSlice(Paths.get(name.toString()).toRealPath().toString());
                return path;
            }
            catch (IOException e) {
                return xstrdup(name);
            }
        }

        public static void free(BytePtr str) {
            if (str != null)
            {
                assert(str.get(0) != (byte)171);
                memset(str, 171, strlen(str) + 1);
            }
            Mem.xfree(str);
        }

        public  BytePtr toChars() {
            return this.str.ptr();
        }

        public  ByteSlice asString() {
            return this.str;
        }

    }
}
