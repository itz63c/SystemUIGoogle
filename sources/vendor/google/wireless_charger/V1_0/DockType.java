package vendor.google.wireless_charger.V1_0;

public final class DockType {
    public static final String toString(byte b) {
        if (b == 0) {
            return "DESKTOP_DOCK";
        }
        if (b == 1) {
            return "DESKTOP_PAD";
        }
        if (b == 2) {
            return "AUTOMOBILE_DOCK";
        }
        if (b == 3) {
            return "AUTOMOBILE_PAD";
        }
        if (b == 15) {
            return "UNKNOWN";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("0x");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(b)));
        return sb.toString();
    }
}
