package org.dlang.dmd.root;

public class CTFloat {

    static public void sprint(BytePtr s, byte k, double value) {
        String str = Double.toString(value);
        int i = 0;
        for (char c : str.toCharArray()) {
            s.set(i++, (byte)c); // assume ASCII-only
        }
        s.set(i, (byte)0);
    }
}
