package com.android.settingslib.media;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import com.android.settingslib.R$drawable;
import com.android.settingslib.bluetooth.BluetoothUtils;

public class InfoMediaDevice extends MediaDevice {
    public boolean isConnected() {
        return true;
    }

    InfoMediaDevice(Context context, MediaRouter2Manager mediaRouter2Manager, MediaRoute2Info mediaRoute2Info, String str) {
        super(context, 2, mediaRouter2Manager, mediaRoute2Info, str);
        initDeviceRecord();
    }

    public String getName() {
        return this.mRouteInfo.getName().toString();
    }

    public Drawable getIcon() {
        Context context = this.mContext;
        return BluetoothUtils.buildBtRainbowDrawable(context, context.getDrawable(getDrawableResId()), getId().hashCode());
    }

    /* access modifiers changed from: 0000 */
    public int getDrawableResId() {
        if (this.mRouteInfo.getType() != 2000) {
            return R$drawable.ic_media_device;
        }
        return R$drawable.ic_media_group_device;
    }

    public String getId() {
        return MediaDeviceUtils.getId(this.mRouteInfo);
    }
}
