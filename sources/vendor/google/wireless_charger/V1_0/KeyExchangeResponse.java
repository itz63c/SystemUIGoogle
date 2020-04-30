package vendor.google.wireless_charger.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

public final class KeyExchangeResponse {
    public byte dockId = 0;
    public ArrayList<Byte> dockPublicKey = new ArrayList<>();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != KeyExchangeResponse.class) {
            return false;
        }
        KeyExchangeResponse keyExchangeResponse = (KeyExchangeResponse) obj;
        return this.dockId == keyExchangeResponse.dockId && HidlSupport.deepEquals(this.dockPublicKey, keyExchangeResponse.dockPublicKey);
    }

    public final int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.dockId))), Integer.valueOf(HidlSupport.deepHashCode(this.dockPublicKey))});
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".dockId = ");
        sb.append(this.dockId);
        sb.append(", .dockPublicKey = ");
        sb.append(this.dockPublicKey);
        sb.append("}");
        return sb.toString();
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(24), 0);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        HwBlob hwBlob2 = hwBlob;
        this.dockId = hwBlob2.getInt8(j + 0);
        long j2 = j + 8;
        int int32 = hwBlob2.getInt32(8 + j2);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer((long) (int32 * 1), hwBlob.handle(), j2 + 0, true);
        this.dockPublicKey.clear();
        for (int i = 0; i < int32; i++) {
            this.dockPublicKey.add(Byte.valueOf(readEmbeddedBuffer.getInt8((long) (i * 1))));
        }
    }
}
