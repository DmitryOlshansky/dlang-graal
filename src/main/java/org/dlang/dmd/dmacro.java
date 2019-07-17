package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.doc.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.globals.*;

public class dmacro {
    static int expandnest = 0;
    static int expandnestLimit = 1000;

    public static class Macro
    {
        public Ptr<Macro> next = null;
        public ByteSlice name = new ByteSlice();
        public ByteSlice text = new ByteSlice();
        public int inuse = 0;
        // Erasure: __ctor<Array, Array>
        public  Macro(ByteSlice name, ByteSlice text) {
            this.name = name.copy();
            this.text = text.copy();
        }

        // Erasure: search<Array>
        public  Ptr<Macro> search(ByteSlice name) {
            Ptr<Macro> table = null;
            {
                table = pcopy(ptr(this));
                for (; table != null;table = pcopy((table.get()).next)){
                    if (__equals((table.get()).name, name))
                    {
                        break;
                    }
                }
            }
            return table;
        }

        // Erasure: define<Ptr, Array, Array>
        public static Ptr<Macro> define(Ptr<Ptr<Macro>> ptable, ByteSlice name, ByteSlice text) {
            Ptr<Macro> table = null;
            {
                table = pcopy(ptable.get());
                for (; table != null;table = pcopy((table.get()).next)){
                    if (__equals((table.get()).name, name))
                    {
                        (table.get()).text = text.copy();
                        return table;
                    }
                }
            }
            table = pcopy(refPtr(new Macro(name, text)));
            (table.get()).next = pcopy(ptable.get());
            ptable.set(0, table);
            return table;
        }

        // Erasure: expand<Ptr, int, Ptr, Array>
        public  void expand(OutBuffer buf, int start, Ptr<Integer> pend, ByteSlice arg) {
            if ((dmacro.expandnest > 1000))
            {
                error(Loc.initial, new BytePtr("DDoc macro expansion limit exceeded; more than %d expansions."), 1000);
                return ;
            }
            dmacro.expandnest++;
            int end = pend.get();
            assert((start <= end));
            assert((end <= (buf).offset));
            arg = memdup(arg).copy();
            {
                int u = start;
                for (; (u + 1 < end);){
                    BytePtr p = pcopy(toBytePtr(buf.data));
                    if (((p.get(u) & 0xFF) == 36) && (isdigit((p.get(u + 1) & 0xFF)) != 0) || ((p.get(u + 1) & 0xFF) == 43))
                    {
                        if ((u > start) && ((p.get(u - 1) & 0xFF) == 36))
                        {
                            (buf).remove(u - 1, 1);
                            end--;
                            u += 1;
                            continue;
                        }
                        byte c = p.get(u + 1);
                        int n = ((c & 0xFF) == 43) ? -1 : (c & 0xFF) - 48;
                        Ref<ByteSlice> marg = ref(new ByteSlice().copy());
                        if ((n == 0))
                        {
                            marg.value = arg.copy();
                        }
                        else
                        {
                            extractArgN(arg, marg, n);
                        }
                        if ((marg.value.getLength() == 0))
                        {
                            (buf).remove(u, 2);
                            end -= 2;
                        }
                        else if (((c & 0xFF) == 43))
                        {
                            (buf).remove(u, 2);
                            (buf).insert(u, marg.value);
                            end += marg.value.getLength() - 2;
                            Ref<Integer> mend = ref(u + marg.value.getLength());
                            this.expand(buf, u, ptr(mend), new ByteSlice());
                            end += mend.value - (u + marg.value.getLength());
                            u = mend.value;
                        }
                        else
                        {
                            (buf).data.set(u, (byte)255);
                            (buf).data.set((u + 1), (byte)123);
                            (buf).insert(u + 2, marg.value);
                            (buf).insert(u + 2 + marg.value.getLength(), new ByteSlice("\u00ff}"));
                            end += 0 + marg.value.getLength() + 2;
                            Ref<Integer> mend = ref(u + 2 + marg.value.getLength());
                            this.expand(buf, u + 2, ptr(mend), new ByteSlice());
                            end += mend.value - (u + 2 + marg.value.getLength());
                            u = mend.value;
                        }
                        continue;
                    }
                    u++;
                }
            }
            {
                int u = start;
                for (; (u + 4 < end);){
                    BytePtr p = pcopy(toBytePtr(buf.data));
                    if (((p.get(u) & 0xFF) == 36) && ((p.get(u + 1) & 0xFF) == 40) && isIdStart(p.plus(u).plus(2)))
                    {
                        BytePtr name = pcopy(p.plus(u).plus(2));
                        int namelen = 0;
                        Ref<ByteSlice> marg = ref(new ByteSlice().copy());
                        int v = 0;
                        {
                            v = u + 2;
                            for (; (v < end);v += utfStride(p.plus(v))){
                                if (!isIdTail(p.plus(v)))
                                {
                                    namelen = v - (u + 2);
                                    break;
                                }
                            }
                        }
                        v += extractArgN(p.slice(v,end), marg, 0);
                        assert((v <= end));
                        if ((v < end))
                        {
                            if ((u > start) && ((p.get(u - 1) & 0xFF) == 36))
                            {
                                (buf).remove(u - 1, 1);
                                end--;
                                u = v;
                                continue;
                            }
                            Ptr<Macro> m = this.search(name.slice(0,namelen));
                            if (m == null)
                            {
                                ByteSlice undef = new ByteSlice("DDOC_UNDEFINED_MACRO").copy();
                                m = pcopy(this.search(toByteSlice(undef)));
                                if (m != null)
                                {
                                    if (marg.value.getLength() != 0)
                                    {
                                        BytePtr q = pcopy(((BytePtr)Mem.xmalloc(namelen + 1 + marg.value.getLength())));
                                        assert(q != null);
                                        memcpy((BytePtr)(q), (name), namelen);
                                        q.set(namelen, (byte)44);
                                        memcpy((BytePtr)((q.plus(namelen).plus(1))), (marg.value.getPtr(0)), marg.value.getLength());
                                        marg.value = q.slice(0,marg.value.getLength() + namelen + 1).copy();
                                    }
                                    else
                                    {
                                        marg.value = name.slice(0,namelen).copy();
                                    }
                                }
                            }
                            if (m != null)
                            {
                                if (((m.get()).inuse != 0) && (marg.value.getLength() == 0))
                                {
                                    (buf).remove(u, v + 1 - u);
                                    end -= v + 1 - u;
                                }
                                else if (((m.get()).inuse != 0) && (arg.getLength() == marg.value.getLength()) && (memcmp(arg.getPtr(0), marg.value.getPtr(0), arg.getLength()) == 0) || (arg.getLength() + 4 == marg.value.getLength()) && ((marg.value.get(0) & 0xFF) == 255) && ((marg.value.get(1) & 0xFF) == 123) && (memcmp(arg.getPtr(0), (marg.value.getPtr(0).plus(2)), arg.getLength()) == 0) && ((marg.value.get(marg.value.getLength() - 2) & 0xFF) == 255) && ((marg.value.get(marg.value.getLength() - 1) & 0xFF) == 125))
                                {
                                }
                                else
                                {
                                    marg.value = memdup(marg.value).copy();
                                    (buf).spread(v + 1, 2 + (m.get()).text.getLength() + 2);
                                    (buf).data.set((v + 1), (byte)255);
                                    (buf).data.set((v + 2), (byte)123);
                                    (buf).data.slice(v + 3,v + 3 + (m.get()).text.getLength()) = toByteSlice((m.get()).text).copy();
                                    (buf).data.set((v + 3 + (m.get()).text.getLength()), (byte)255);
                                    (buf).data.set((v + 3 + (m.get()).text.getLength() + 1), (byte)125);
                                    end += 2 + (m.get()).text.getLength() + 2;
                                    (m.get()).inuse++;
                                    Ref<Integer> mend = ref(v + 1 + 2 + (m.get()).text.getLength() + 2);
                                    this.expand(buf, v + 1, ptr(mend), marg.value);
                                    end += mend.value - (v + 1 + 2 + (m.get()).text.getLength() + 2);
                                    (m.get()).inuse--;
                                    (buf).remove(u, v + 1 - u);
                                    end -= v + 1 - u;
                                    u += mend.value - (v + 1);
                                    Mem.xfree(marg.value.getPtr(0));
                                    continue;
                                }
                            }
                            else
                            {
                                (buf).remove(u, v + 1 - u);
                                end -= v + 1 - u;
                                continue;
                            }
                        }
                    }
                    u++;
                }
            }
            Mem.xfree(arg.getPtr(0));
            pend.set(0, end);
            dmacro.expandnest--;
        }

        public Macro(){ }
        public Macro copy(){
            Macro r = new Macro();
            r.next = next;
            r.name = name.copy();
            r.text = text.copy();
            r.inuse = inuse;
            return r;
        }
        public Macro opAssign(Macro that) {
            this.next = that.next;
            this.name = that.name;
            this.text = that.text;
            this.inuse = that.inuse;
            return this;
        }
    }
    // Erasure: memdup<Array>
    public static ByteSlice memdup(ByteSlice p) {
        int len = p.getLength();
        return (((BytePtr)memcpy((BytePtr)Mem.xmalloc(len), (p.getPtr(0)), len))).slice(0,len);
    }

    // Erasure: extractArgN<Array, Array, int>
    public static int extractArgN(ByteSlice buf, ByteSlice marg, int n) {
        marg = new ByteSlice().copy();
        int parens = 1;
        byte instring = (byte)0;
        int incomment = 0;
        int intag = 0;
        int inexp = 0;
        int argn = 0;
        int v = 0;
        BytePtr p = pcopy(buf.getPtr(0));
        int end = buf.getLength();
        while(true) try {
        /*Largstart:*/
            if ((n != 1) && (v < end) && (isspace((p.get(v) & 0xFF)) != 0))
            {
                v++;
            }
            int vstart = v;
        L_outer1:
            for (; (v < end);v++){
                byte c = p.get(v);
                {
                    int __dispatch0 = 0;
                    dispatched_0:
                    do {
                        switch (__dispatch0 != 0 ? __dispatch0 : (c & 0xFF))
                        {
                            case 44:
                                if ((inexp == 0) && (instring == 0) && (incomment == 0) && (parens == 1))
                                {
                                    argn++;
                                    if ((argn == 1) && (n == -1))
                                    {
                                        v++;
                                        /*goto Largstart*/throw Dispatch0.INSTANCE;
                                    }
                                    if ((argn == n))
                                    {
                                        break;
                                    }
                                    if ((argn + 1 == n))
                                    {
                                        v++;
                                        /*goto Largstart*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                continue L_outer1;
                            case 40:
                                if ((inexp == 0) && (instring == 0) && (incomment == 0))
                                {
                                    parens++;
                                }
                                continue L_outer1;
                            case 41:
                                if ((inexp == 0) && (instring == 0) && (incomment == 0) && ((parens -= 1) == 0))
                                {
                                    break;
                                }
                                continue L_outer1;
                            case 34:
                            case 39:
                                if ((inexp == 0) && (incomment == 0) && (intag != 0))
                                {
                                    if (((c & 0xFF) == (instring & 0xFF)))
                                    {
                                        instring = (byte)0;
                                    }
                                    else if (instring == 0)
                                    {
                                        instring = (byte)c;
                                    }
                                }
                                continue L_outer1;
                            case 60:
                                if ((inexp == 0) && (instring == 0) && (incomment == 0))
                                {
                                    if ((v + 6 < end) && ((p.get(v + 1) & 0xFF) == 33) && ((p.get(v + 2) & 0xFF) == 45) && ((p.get(v + 3) & 0xFF) == 45))
                                    {
                                        incomment = 1;
                                        v += 3;
                                    }
                                    else if ((v + 2 < end) && (isalpha((p.get(v + 1) & 0xFF)) != 0))
                                    {
                                        intag = 1;
                                    }
                                }
                                continue L_outer1;
                            case 62:
                                if (inexp == 0)
                                {
                                    intag = 0;
                                }
                                continue L_outer1;
                            case 45:
                                if ((inexp == 0) && (instring == 0) && (incomment != 0) && (v + 2 < end) && ((p.get(v + 1) & 0xFF) == 45) && ((p.get(v + 2) & 0xFF) == 62))
                                {
                                    incomment = 0;
                                    v += 2;
                                }
                                continue L_outer1;
                            case 255:
                                if ((v + 1 < end))
                                {
                                    if (((p.get(v + 1) & 0xFF) == 123))
                                    {
                                        inexp++;
                                    }
                                    else if (((p.get(v + 1) & 0xFF) == 125))
                                    {
                                        inexp--;
                                    }
                                }
                                continue L_outer1;
                            default:
                            continue L_outer1;
                        }
                    } while(__dispatch0 != 0);
                }
                break;
            }
            if ((argn == 0) && (n == -1))
            {
                marg = p.slice(v,v).copy();
            }
            else
            {
                marg = p.slice(vstart,v).copy();
            }
            return v;
            break;
        } catch(Dispatch0 __d){}
    }

}
