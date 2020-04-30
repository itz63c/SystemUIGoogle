package com.android.settingslib.media;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import com.android.settingslib.R$drawable;
import com.android.settingslib.R$string;
import com.android.settingslib.bluetooth.BluetoothUtils;

public class PhoneMediaDevice extends MediaDevice {
    public String getId() {
        return "phone_media_device_id_1";
    }

    public boolean isConnected() {
        return true;
    }

    PhoneMediaDevice(Context context, MediaRouter2Manager mediaRouter2Manager, MediaRoute2Info mediaRoute2Info, String str) {
        super(context, 1, mediaRouter2Manager, mediaRoute2Info, str);
        initDeviceRecord();
    }

    public String getName() {
        CharSequence charSequence;
        int type = this.mRouteInfo.getType();
        if (type == 3 || type == 4) {
            charSequence = this.mRouteInfo.getName();
        } else {
            charSequence = this.mContext.getString(R$string.media_transfer_this_device_name);
        }
        return charSequence.toString();
    }

    public Drawable getIcon() {
        Context context = this.mContext;
        return BluetoothUtils.buildBtRainbowDrawable(context, context.getDrawable(getDrawableResId()), getId().hashCode());
    }

    /* access modifiers changed from: 0000 */
    public int getDrawableResId() {
        int type = this.mRouteInfo.getType();
        if (type == 3 || type == 4) {
            return 17302318;
        }
        return R$drawable.ic_smartphone;
    }
}
