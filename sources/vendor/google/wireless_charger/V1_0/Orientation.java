package vendor.google.wireless_charger.V1_0;

public final class Orientation {
    public static final String toString(byte b) {
        if (b == 0) {
            return "ARBITRARY";
        }
        if (b == 1) {
            return "LANDSCAPE";
        }
        if (b == 2) {
            return "PORTRAIT";
        }
        if (b == 3) {
            return "BOTH";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("0x");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(b)));
        return sb.toString();
    }
}
