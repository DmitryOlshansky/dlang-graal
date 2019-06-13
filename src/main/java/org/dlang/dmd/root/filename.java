package org.dlang.dmd.root;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.utils.*;

public class filename {

    public static class FileName
    {
        public ByteSlice str;
        public  FileName __ctor(ByteSlice str) {
            this.str = xarraydup(str);
            return this;
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
            if (!name.getLength())
                return false;
            return name.get(0) == (byte)47;
        }

        public static void test_0() {
            assert((int)FileName.absolute( new ByteSlice("/").slice()) == 1);
            assert((int)FileName.absolute( new ByteSlice("").slice()) == 0);
        }
        public static BytePtr toAbsolute(BytePtr name, BytePtr base) {
            ByteSlice name_ = toDString(name);
            ByteSlice base_ = base != null ? toDString(base) : toDString(getcwd(null, 0));
            return FileName.absolute(name_) ? name : FileName.combine(base_, name_).toBytePtr();
        }

        public static BytePtr ext(BytePtr str) {
            return FileName.ext(toDString(str)).toBytePtr();
        }

        public static ByteSlice ext(ByteSlice str) {
            {
                ByteSlice __r46 = str.slice();
                int __key45 = __r46.getLength();
                for (; __key45--;) {
                    byte e = __r46.get(__key45);
                    int idx = __key45;
                    switch ((int)e)
                    {
                        case (byte)46:
                        {
                            return str.slice(idx + 1,__dollar);
                            case (byte)47:
                            {
                                return null;
                            }
                        }
                        default:
                        {
                            continue;
                        }
                    }
                }
            }
            return null;
        }

        public static void test_1() {
            assert(__equals(FileName.ext( new ByteSlice("/foo/bar/dmd.conf").slice()),  new ByteSlice("conf")));
            assert(__equals(FileName.ext( new ByteSlice("object.o").slice()),  new ByteSlice("o")));
            assert(FileName.ext( new ByteSlice("/foo/bar/dmd").slice()).equals(null));
            assert(FileName.ext( new ByteSlice(".objdir.o/object").slice()).equals(null));
            assert(FileName.ext({}).equals(null));
        }
        public  BytePtr ext() {
            return FileName.ext(this.str).toBytePtr();
        }

        public static BytePtr removeExt(BytePtr str) {
            return FileName.removeExt(toDString(str)).toBytePtr();
        }

        public static ByteSlice removeExt(ByteSlice str) {
            ByteSlice e = FileName.ext(str);
            if ((e.getLength()) != 0)
            {
                int len = str.getLength() - e.getLength() - 1;
                BytePtr n = Mem.xmalloc(len + 1).toBytePtr();
                memcpy(n.toBytePtr(), str.toBytePtr().toBytePtr(), len);
                n.set(len, (byte)0);
                return n.slice(0,len);
            }
            return Mem.xstrdup(str.toBytePtr()).slice(0,str.getLength());
        }

        public static void test_2() {
            assert(__equals(FileName.removeExt( new ByteSlice("/foo/bar/object.d").slice()),  new ByteSlice("/foo/bar/object")));
            assert(__equals(FileName.removeExt( new ByteSlice("/foo/bar/frontend.di").slice()),  new ByteSlice("/foo/bar/frontend")));
        }
        public static BytePtr name(BytePtr str) {
            return FileName.name(toDString(str)).toBytePtr();
        }

        public static ByteSlice name(ByteSlice str) {
            {
                ByteSlice __r48 = str.slice();
                int __key47 = __r48.getLength();
                for (; __key47--;) {
                    byte e = __r48.get(__key47);
                    int idx = __key47;
                    switch ((int)e)
                    {
                        case (byte)47:
                        {
                            return str.slice(idx + 1,__dollar);
                        }
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
            return FileName.name(this.str).toBytePtr();
        }

        public static void test_3() {
            assert(__equals(FileName.name( new ByteSlice("/foo/bar/object.d").slice()),  new ByteSlice("object.d")));
            assert(__equals(FileName.name( new ByteSlice("/foo/bar/frontend.di").slice()),  new ByteSlice("frontend.di")));
        }
        public static BytePtr path(BytePtr str) {
            return FileName.path(toDString(str)).toBytePtr();
        }

        public static ByteSlice path(ByteSlice str) {
            ByteSlice n = FileName.name(str);
            boolean hasTrailingSlash = false;
            if (n.getLength() < str.getLength())
            {
                if (str.get(__dollar - n.getLength() - (byte)1) == (byte)47)
                    hasTrailingSlash = true;
            }
            int pathlen = str.getLength() - n.getLength() - (hasTrailingSlash ? 1 : 0);
            BytePtr path = Mem.xmalloc(pathlen + 1).toBytePtr();
            memcpy(path.toBytePtr(), str.toBytePtr().toBytePtr(), pathlen);
            path.set(pathlen, (byte)0);
            return path.slice(0,pathlen);
        }

        public static void test_4() {
            assert(__equals(FileName.path( new ByteSlice("/foo/bar").slice()),  new ByteSlice("/foo")));
            assert(__equals(FileName.path( new ByteSlice("foo").slice()),  new ByteSlice("")));
        }
        public static ByteSlice replaceName(ByteSlice path, ByteSlice name) {
            if (FileName.absolute(name))
                return name;
            ByteSlice n = FileName.name(path);
            if (__equals(n, path))
                return name;
            return FileName.combine(path.slice(0,__dollar - n.getLength()), name);
        }

        public static BytePtr combine(BytePtr path, BytePtr name) {
            if (!path)
                return name;
            return FileName.combine(toDString(path), toDString(name)).toBytePtr();
        }

        public static ByteSlice combine(ByteSlice path, ByteSlice name) {
            if (!path.getLength())
                return name;
            BytePtr f = Mem.xmalloc(path.getLength() + 1 + name.getLength() + 1).toBytePtr();
            memcpy(f.toBytePtr(), path.toBytePtr().toBytePtr(), path.getLength());
            boolean trailingSlash = false;
            if (path.get(__dollar - (byte)1) != (byte)47)
            {
                f.set(path.getLength(), (byte)47);
                trailingSlash = true;
            }
            int len = path.getLength() + (int)trailingSlash;
            memcpy((f + len * 1).toBytePtr(), name.toBytePtr().toBytePtr(), name.getLength());
            f.set((len + name.getLength()), (byte)0);
            return f.slice(0,len + name.getLength());
        }

        public static void test_5() {
            assert(__equals(FileName.combine( new ByteSlice("foo").slice(),  new ByteSlice("bar").slice()),  new ByteSlice("foo/bar")));
            assert(__equals(FileName.combine( new ByteSlice("foo/").slice(),  new ByteSlice("bar").slice()),  new ByteSlice("foo/bar")));
        }
        public static BytePtr buildPath(BytePtr path, Slice<BytePtr> names) {
            {
                Slice<BytePtr> __r49 = names.slice();
                int __key50 = 0;
                for (; __key50 < __r49.getLength();__key50 += 1) {
                    BytePtr name = __r49.get(__key50);
                    path = FileName.combine(path, name);
                }
            }
            return path;
        }

        public static DArray<BytePtr> splitPath(BytePtr path) {
            DArray<BytePtr> array = Array<BytePtr>(0, null, 0, null);
            public  int sink(BytePtr p) {
                (array).push(p);
                return 0;
            }

            FileName.splitPath(sink, path);
            return array;
        }

        public static void splitPath(Function1<BytePtr,int> sink, BytePtr path) {
            if (path != null)
            {
                BytePtr p = path;
                OutBuffer buf = new OutBuffer();
                byte c = (byte)255;
                do
                {
                    BytePtr home = null;
                    boolean instring = false;
                    for (; isspace((int)p.get(0));) {
                        p += 1;
                    }
                    buf.reserve(8);
                    for (; ;p += 1){
                        c = p.get(0);
                        switch ((int)c)
                        {
                            case (byte)34:
                            {
                                (int)instring ^= 0;
                                continue;
                                case (byte)58:
                                {
                                }
                                p++;
                                break;
                            }
                            case (byte)26:
                            {
                            }
                            case (byte)0:
                            {
                                break;
                            }
                            case (byte)13:
                            {
                                continue;
                                case (byte)126:
                                {
                                    if (!home)
                                        home = getenv(new BytePtr("HOME"));
                                    if (home != null)
                                        buf.writestring(home);
                                    else
                                        buf.writeByte(126);
                                    continue;
                                }
                            }
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
                        if ((sink(buf.extractChars())) != 0)
                            break;
                    }
                }
                while (c);
            }
        }

        public static ByteSlice addExt(ByteSlice name, ByteSlice ext) {
            int len = name.getLength() + ext.getLength() + 2;
            BytePtr s = Mem.xmalloc(len).toBytePtr();
            s.slice(0,name.getLength()) = name.slice();
            s.set(name.getLength(), (byte)46);
            s.slice(name.getLength() + 1,len - 1) = ext.slice();
            s.set((len - 1), (byte)0);
            return s.slice(0,len - 1);
        }

        public static BytePtr defaultExt(BytePtr name, BytePtr ext) {
            return FileName.defaultExt(toDString(name), toDString(ext)).toBytePtr();
        }

        public static ByteSlice defaultExt(ByteSlice name, ByteSlice ext) {
            ByteSlice e = FileName.ext(name);
            if ((e.getLength()) != 0)
                return xarraydup(name);
            return FileName.addExt(name, ext);
        }

        public static void test_6() {
            assert(__equals(FileName.defaultExt( new ByteSlice("/foo/object.d").slice(),  new ByteSlice("d")),  new ByteSlice("/foo/object.d")));
            assert(__equals(FileName.defaultExt( new ByteSlice("/foo/object").slice(),  new ByteSlice("d")),  new ByteSlice("/foo/object.d")));
            assert(__equals(FileName.defaultExt( new ByteSlice("/foo/bar.d").slice(),  new ByteSlice("o")),  new ByteSlice("/foo/bar.d")));
        }
        public static BytePtr forceExt(BytePtr name, BytePtr ext) {
            return FileName.forceExt(toDString(name), toDString(ext)).toBytePtr();
        }

        public static ByteSlice forceExt(ByteSlice name, ByteSlice ext) {
            if (e = .getLength() != 0)
                return FileName.addExt(name.slice(0,__dollar - e.getLength() - 1), ext);
            return FileName.defaultExt(name, ext);
        }

        public static void test_7() {
            assert(__equals(FileName.forceExt( new ByteSlice("/foo/object.d").slice(),  new ByteSlice("d")),  new ByteSlice("/foo/object.d")));
            assert(__equals(FileName.forceExt( new ByteSlice("/foo/object").slice(),  new ByteSlice("d")),  new ByteSlice("/foo/object.d")));
            assert(__equals(FileName.forceExt( new ByteSlice("/foo/bar.d").slice(),  new ByteSlice("o")),  new ByteSlice("/foo/bar.o")));
        }
        public static boolean equalsExt(BytePtr name, BytePtr ext) {
            return FileName.equalsExt(toDString(name), toDString(ext));
        }

        public static boolean equalsExt(ByteSlice name, ByteSlice ext) {
            ByteSlice e = FileName.ext(name);
            if (!e.getLength() && !ext.getLength())
                return true;
            if (!e.getLength() || !ext.getLength())
                return false;
            return FileName.equals(e, ext);
        }

        public static void test_8() {
            assert(!FileName.equalsExt( new ByteSlice("foo.bar").slice(),  new ByteSlice("d")));
            assert(FileName.equalsExt( new ByteSlice("foo.bar").slice(),  new ByteSlice("bar")));
            assert(FileName.equalsExt( new ByteSlice("object.d").slice(),  new ByteSlice("d")));
            assert(!FileName.equalsExt( new ByteSlice("object").slice(),  new ByteSlice("d")));
        }
        public  boolean equalsExt(BytePtr ext) {
            return FileName.equalsExt(this.str, toDString(ext));
        }

        public static BytePtr searchPath(DArray<BytePtr> path, BytePtr name, boolean cwd) {
            return FileName.searchPath(path, toDString(name), cwd).toBytePtr();
        }

        public static ByteSlice searchPath(DArray<BytePtr> path, ByteSlice name, boolean cwd) {
            if (FileName.absolute(name))
            {
                return (FileName.exists(name)) != 0 ? name : null;
            }
            if (cwd)
            {
                if ((FileName.exists(name)) != 0)
                    return name;
            }
            if (path != null)
            {
                {
                    Slice<BytePtr> __r51 = (path).opSlice().slice();
                    int __key52 = 0;
                    for (; __key52 < __r51.getLength();__key52 += 1) {
                        BytePtr p = __r51.get(__key52);
                        ByteSlice n = FileName.combine(toDString(p), name);
                        if ((FileName.exists(n)) != 0)
                            return n;
                        if (n.toBytePtr() != name.toBytePtr())
                        {
                            Mem.xfree(n.toBytePtr().toBytePtr());
                        }
                    }
                }
            }
            return null;
        }

        public static ByteSlice searchPath(BytePtr path, ByteSlice name, boolean cwd) {
            if (FileName.absolute(name))
            {
                return (FileName.exists(name)) != 0 ? name : null;
            }
            if (cwd)
            {
                if ((FileName.exists(name)) != 0)
                    return name;
            }
            if (path != null && (path.get(0)) != 0)
            {
                ByteSlice result = new ByteSlice();
                public  int sink(BytePtr p) {
                    ByteSlice n = FileName.combine(toDString(p), name);
                    Mem.xfree(p.toBytePtr());
                    if ((FileName.exists(n)) != 0)
                    {
                        result = n;
                        return 1;
                    }
                    return 0;
                }

                FileName.splitPath(sink, path);
                return result;
            }
            return null;
        }

        public static BytePtr safeSearchPath(DArray<BytePtr> path, BytePtr name) {
            {
                BytePtr p = name;
                for (; p.get(0);p++){
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
                        BytePtr cpath = FileName.canonicalName((path).opIndex(i));
                        if (cpath == null)
                        cname = FileName.canonicalName(FileName.combine(cpath, name));
                        if (cname == null)
                        if ((FileName.exists(cname)) != 0 && strncmp(cpath, cname, strlen(cpath)) == 0)
                        {
                            Mem.xfree(cpath.toBytePtr());
                            BytePtr p = Mem.xstrdup(cname);
                            Mem.xfree(cname.toBytePtr());
                            return p;
                        }
                        if (cpath != null)
                            Mem.xfree(cpath.toBytePtr());
                        if (cname != null)
                            Mem.xfree(cname.toBytePtr());
                    }
                }
            }
            return null;
        }

        public static int exists(BytePtr name) {
            return FileName.exists(toDString(name));
        }

        public static int exists(ByteSlice name) {
            if (!name.getLength())
                return 0;
            stat_t st = new stat_t();
            if (toCStringThen(name) < 0)
                return 0;
            if (S_ISDIR(st.st_mode))
                return 2;
            return 1;
        }

        public static boolean ensurePathExists(ByteSlice path) {
            if (!path.getLength())
                return true;
            if ((FileName.exists(path)) != 0)
                return true;
            ByteSlice p = FileName.path(path);
            if ((p.getLength()) != 0)
            {
                boolean r = FileName.ensurePathExists(p);
                Mem.xfree(p.toBytePtr());
                if (!r)
                    return r;
            }
            __errno_location() = 0;
            int r = toCStringThen(path);
            if (r == 0)
                return true;
            if (__errno_location() == 17)
                return true;
            return false;
        }

        public static boolean ensurePathExists(BytePtr path) {
            return FileName.ensurePathExists(toDString(path));
        }

        public static BytePtr canonicalName(BytePtr name) {
            return FileName.canonicalName(toDString(name)).toBytePtr();
        }

        public static ByteSlice canonicalName(ByteSlice name) {
            ByteSlice buf = void;
            BytePtr path = toCStringThen(name);
            if (path != null)
                return toDString(Mem.xstrdup(path));
            if (!name.getLength())
                return null;
            return Mem.xstrdup(name.toBytePtr()).slice(0,name.getLength());
        }

        public static void free(BytePtr str) {
            if (str != null)
            {
                assert(str.get(0) != (byte)171);
                memset(str.toBytePtr(), 171, strlen(str) + 1);
            }
            Mem.xfree(str.toBytePtr());
        }

        public  BytePtr toChars() {
            return this.str.toBytePtr();
        }

        public  ByteSlice toString() {
            return this.str;
        }

        public FileName(){}
        public FileName(ByteSlice str) {
            this.str = str;
        }
    }
}
