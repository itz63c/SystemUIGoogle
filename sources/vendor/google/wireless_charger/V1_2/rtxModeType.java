package vendor.google.wireless_charger.V1_2;

public final class rtxModeType {
    public static final String toString(byte b) {
        if (b == 0) {
            return "DISABLED";
        }
        if (b == 1) {
            return "ACTIVE";
        }
        if (b == 2) {
            return "AVAILABLE";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("0x");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(b)));
        return sb.toString();
    }
}
