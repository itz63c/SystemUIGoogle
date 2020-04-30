package vendor.google.wireless_charger.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;

public final class DockInfo {
    public boolean isGetInfoSupported = false;
    public String manufacturer = new String();
    public int maxFwSize = 0;
    public String model = new String();
    public byte orientation = 0;
    public String serial = new String();
    public byte type = 0;
    public FirmwareVersion version = new FirmwareVersion();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != DockInfo.class) {
            return false;
        }
        DockInfo dockInfo = (DockInfo) obj;
        return HidlSupport.deepEquals(this.manufacturer, dockInfo.manufacturer) && HidlSupport.deepEquals(this.model, dockInfo.model) && HidlSupport.deepEquals(this.serial, dockInfo.serial) && this.maxFwSize == dockInfo.maxFwSize && this.isGetInfoSupported == dockInfo.isGetInfoSupported && HidlSupport.deepEquals(this.version, dockInfo.version) && this.orientation == dockInfo.orientation && this.type == dockInfo.type;
    }

    public final int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(HidlSupport.deepHashCode(this.manufacturer)), Integer.valueOf(HidlSupport.deepHashCode(this.model)), Integer.valueOf(HidlSupport.deepHashCode(this.serial)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxFwSize))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isGetInfoSupported))), Integer.valueOf(HidlSupport.deepHashCode(this.version)), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.orientation))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.type)))});
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".manufacturer = ");
        sb.append(this.manufacturer);
        sb.append(", .model = ");
        sb.append(this.model);
        sb.append(", .serial = ");
        sb.append(this.serial);
        sb.append(", .maxFwSize = ");
        sb.append(this.maxFwSize);
        sb.append(", .isGetInfoSupported = ");
        sb.append(this.isGetInfoSupported);
        sb.append(", .version = ");
        sb.append(this.version);
        sb.append(", .orientation = ");
        sb.append(Orientation.toString(this.orientation));
        sb.append(", .type = ");
        sb.append(DockType.toString(this.type));
        sb.append("}");
        return sb.toString();
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(88), 0);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        HwBlob hwBlob2 = hwBlob;
        long j2 = j + 0;
        String string = hwBlob2.getString(j2);
        this.manufacturer = string;
        HwParcel hwParcel2 = hwParcel;
        hwParcel2.readEmbeddedBuffer((long) (string.getBytes().length + 1), hwBlob.handle(), j2 + 0, false);
        long j3 = j + 16;
        String string2 = hwBlob2.getString(j3);
        this.model = string2;
        hwParcel2.readEmbeddedBuffer((long) (string2.getBytes().length + 1), hwBlob.handle(), j3 + 0, false);
        long j4 = j + 32;
        String string3 = hwBlob2.getString(j4);
        this.serial = string3;
        hwParcel2.readEmbeddedBuffer((long) (string3.getBytes().length + 1), hwBlob.handle(), j4 + 0, false);
        this.maxFwSize = hwBlob2.getInt32(j + 48);
        this.isGetInfoSupported = hwBlob2.getBool(j + 52);
        this.version.readEmbeddedFromParcel(hwParcel, hwBlob2, j + 56);
        this.orientation = hwBlob2.getInt8(j + 80);
        this.type = hwBlob2.getInt8(j + 81);
    }
}
