package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;

import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utf.*;
import static org.dlang.dmd.utils.*;

public class identifier {
    static Identifier anonymousanonymous;
    static int generateIdi;
    private static class Key
    {
        private Loc loc;
        private ByteSlice prefix;
        public Key(){}
        public Key(Loc loc, ByteSlice prefix) {
            this.loc = loc;
            this.prefix = prefix;
        }

        public Key opAssign(Key that) {
            this.loc = that.loc;
            this.prefix = that.prefix;
            return this;
        }
    }
    static AA<Key,Integer> generateIdWithLoccounters;

    public static class Identifier extends RootObject
    {
        public int value;
        public ByteSlice name;
        public  Identifier(BytePtr name, int length, int value) {
            super();
            this.name = name.slice(0,length);
            this.value = value;
        }

        public  Identifier(ByteSlice name, int value) {
            super();
            this.name = name;
            this.value = value;
        }

        public  Identifier(BytePtr name) {
            this(name.slice(0,strlen(name)), 120);
        }

        public static Identifier anonymous() {
            Identifier anonymous = identifier.anonymousanonymous;
            if (anonymous != null)
                return anonymous;
            return anonymous = new Identifier( new ByteSlice("__anonymous"), 120);
        }

        public static Identifier create(BytePtr name) {
            return new Identifier(name);
        }

        public  BytePtr toChars() {
            return this.name.toBytePtr();
        }

        public  ByteSlice asString() {
            return this.name;
        }

        public  int getValue() {
            return this.value;
        }

        public  BytePtr toHChars2() {
            BytePtr p = null;
            if (this.equals(Id.ctor))
                p = new BytePtr("this");
            else if (this.equals(Id.dtor))
                p = new BytePtr("~this");
            else if (this.equals(Id.unitTest))
                p = new BytePtr("unittest");
            else if (this.equals(Id.dollar))
                p = new BytePtr("$");
            else if (this.equals(Id.withSym))
                p = new BytePtr("with");
            else if (this.equals(Id.result))
                p = new BytePtr("result");
            else if (this.equals(Id.returnLabel))
                p = new BytePtr("return");
            else
            {
                p = this.toChars();
                if (p.get(0) == (byte)95)
                {
                    if (strncmp(p,  new ByteSlice("_staticCtor"), 11) == 0)
                        p = new BytePtr("static this");
                    else if (strncmp(p,  new ByteSlice("_staticDtor"), 11) == 0)
                        p = new BytePtr("static ~this");
                    else if (strncmp(p,  new ByteSlice("__invariant"), 11) == 0)
                        p = new BytePtr("invariant");
                }
            }
            return p;
        }

        public  int dyncast() {
            return DYNCAST.identifier;
        }

        public static StringTable stringtable;
        public static Identifier generateId(BytePtr prefix) {
            int i = identifier.generateIdi;
            return Identifier.generateId(prefix, i += 1);
        }

        public static Identifier generateId(BytePtr prefix, int i) {
            OutBuffer buf = new OutBuffer();
            buf.writestring(prefix);
            buf.print((long)i);
            return Identifier.idPool(buf.peekSlice());
        }

        public static Identifier generateIdWithLoc(ByteSlice prefix, Loc loc) {
            OutBuffer idBuf = new OutBuffer();
            Function0<Integer> __lambda7 = new Function0<Integer>(){
                public Integer invoke(){
                    return 1;
                }
            };
            Function1<Integer,Integer> __lambda8 = new Function1<Integer,Integer>(){
                public Integer invoke(Integer counter){
                    idBuf.writestring( new ByteSlice("_"));
                    idBuf.print((long)counter);
                    return counter + 1;
                }
            };
            idBuf.writestring(prefix);
            idBuf.writestring( new ByteSlice("_L"));
            idBuf.print((long)loc.linnum);
            idBuf.writestring( new ByteSlice("_C"));
            idBuf.print((long)loc.charnum);
            AA<Key,Integer> counters = identifier.generateIdWithLoccounters;
            update(counters, new Key(loc, prefix), __lambda7, __lambda8);
            return Identifier.idPool(idBuf.peekSlice());
        }

        public static Identifier idPool(BytePtr s, int len) {
            return Identifier.idPool(s.slice(0,len));
        }

        public static Identifier idPool(ByteSlice s) {
            StringValue sv = Identifier.stringtable.update(s);
            Identifier id = (Identifier)(sv).ptrvalue;
            if (!(id != null))
            {
                id = new Identifier((sv).asString(), 120);
                (sv).ptrvalue = id;
            }
            return id;
        }

        public static Identifier idPool(BytePtr s, int len, int value) {
            return Identifier.idPool(s.slice(0,len), value);
        }

        public static Identifier idPool(ByteSlice s, int value) {
            StringValue sv = Identifier.stringtable.insert(s, null);
            assert(sv != null);
            Identifier id = new Identifier((sv).asString(), value);
            (sv).ptrvalue = id;
            return id;
        }

        public static boolean isValidIdentifier(BytePtr str) {
            return str != null && Identifier.isValidIdentifier(toDString(str));
        }

        public static boolean isValidIdentifier(ByteSlice str) {
            if (str.getLength() == 0 || str.get(0) >= (byte)48 && str.get(0) <= (byte)57)
            {
                return false;
            }
            IntRef idx = ref(0);
            for (; idx.value < str.getLength();){
                IntRef dc = ref('\uffff');
                BytePtr q = utf_decodeChar(str.toBytePtr(), str.getLength(), idx, dc);
                if (q != null || !(dc.value >= 128 && isUniAlpha(dc.value) || (isalnum(dc.value)) != 0 || dc.value == 95))
                {
                    return false;
                }
            }
            return true;
        }

        public static Identifier lookup(BytePtr s, int len) {
            return Identifier.lookup(s.slice(0,len));
        }

        public static Identifier lookup(ByteSlice s) {
            StringValue sv = Identifier.stringtable.lookup(s);
            if (sv == null)
                return null;
            return (Identifier)(sv).ptrvalue;
        }

        public static void initTable() {
            Identifier.stringtable._init(28000);
        }

    }
}
