package org.dlang.dmd.root;

public class CTFloat {

    public static void sprint(BytePtr s, byte k, double value) {
        String str = Double.toString(value);
        int i = 0;
        for (char c : str.toCharArray()) {
            s.set(i++, (byte)c); // assume ASCII-only
        }
        s.set(i, (byte)0);
    }


    public static double parse(BytePtr sbufptr, Ptr<Boolean> isOutOfRange){
        try {
            isOutOfRange.set(0, false);
            return Double.parseDouble(sbufptr.toString());
        }
        catch(Exception e) {
            isOutOfRange.set(0, true);
            return 0.0;
        }

    }

    public static final double zero = 0.0;
}
