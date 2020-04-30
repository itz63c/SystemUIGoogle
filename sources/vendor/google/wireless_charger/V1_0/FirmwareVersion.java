package vendor.google.wireless_charger.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;

public final class FirmwareVersion {
    public String extra = new String();
    public int major = 0;
    public int minor = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != FirmwareVersion.class) {
            return false;
        }
        FirmwareVersion firmwareVersion = (FirmwareVersion) obj;
        return this.major == firmwareVersion.major && this.minor == firmwareVersion.minor && HidlSupport.deepEquals(this.extra, firmwareVersion.extra);
    }

    public final int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.major))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.minor))), Integer.valueOf(HidlSupport.deepHashCode(this.extra))});
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".major = ");
        sb.append(this.major);
        sb.append(", .minor = ");
        sb.append(this.minor);
        sb.append(", .extra = ");
        sb.append(this.extra);
        sb.append("}");
        return sb.toString();
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.major = hwBlob.getInt32(j + 0);
        this.minor = hwBlob.getInt32(4 + j);
        long j2 = j + 8;
        String string = hwBlob.getString(j2);
        this.extra = string;
        hwParcel.readEmbeddedBuffer((long) (string.getBytes().length + 1), hwBlob.handle(), j2 + 0, false);
    }
}
