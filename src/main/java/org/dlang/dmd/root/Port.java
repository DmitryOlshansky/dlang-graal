package org.dlang.dmd.root;

public class Port {
    public static boolean isFloat32LiteralOutOfRange(BytePtr sbufptr){
        try {
            Float.parseFloat(sbufptr.toString());
        }
        catch(Exception e) { return true; }
        return false;
    }

    public static boolean isFloat64LiteralOutOfRange(BytePtr sbufptr){
        try {
            Double.parseDouble(sbufptr.toString());
        }
        catch(Exception e) { return true; }
        return false;
    }
}
