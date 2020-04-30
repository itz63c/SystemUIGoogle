package com.android.settingslib.media;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import android.text.TextUtils;

public abstract class MediaDevice implements Comparable<MediaDevice> {
    private int mConnectedRecord;
    protected final Context mContext;
    protected final MediaRoute2Info mRouteInfo;
    private int mState;
    int mType;

    public abstract Drawable getIcon();

    public abstract String getId();

    public abstract String getName();

    /* access modifiers changed from: protected */
    public boolean isCarKitDevice() {
        return false;
    }

    public abstract boolean isConnected();

    MediaDevice(Context context, int i, MediaRouter2Manager mediaRouter2Manager, MediaRoute2Info mediaRoute2Info, String str) {
        this.mType = i;
        this.mContext = context;
        this.mRouteInfo = mediaRoute2Info;
    }

    /* access modifiers changed from: 0000 */
    public void initDeviceRecord() {
        ConnectionRecordManager.getInstance().fetchLastSelectedDevice(this.mContext);
        this.mConnectedRecord = ConnectionRecordManager.getInstance().fetchConnectionRecord(this.mContext, getId());
    }

    public void setState(int i) {
        this.mState = i;
    }

    public int getState() {
        return this.mState;
    }

    public int compareTo(MediaDevice mediaDevice) {
        if (isConnected() ^ mediaDevice.isConnected()) {
            return isConnected() ? -1 : 1;
        }
        if (this.mType == 1) {
            return -1;
        }
        if (mediaDevice.mType == 1) {
            return 1;
        }
        if (isCarKitDevice()) {
            return -1;
        }
        if (mediaDevice.isCarKitDevice()) {
            return 1;
        }
        String lastSelectedDevice = ConnectionRecordManager.getInstance().getLastSelectedDevice();
        if (TextUtils.equals(lastSelectedDevice, getId())) {
            return -1;
        }
        if (TextUtils.equals(lastSelectedDevice, mediaDevice.getId())) {
            return 1;
        }
        int i = this.mConnectedRecord;
        int i2 = mediaDevice.mConnectedRecord;
        if (i != i2 && (i2 > 0 || i > 0)) {
            return mediaDevice.mConnectedRecord - this.mConnectedRecord;
        }
        int i3 = this.mType;
        int i4 = mediaDevice.mType;
        return i3 == i4 ? getName().compareToIgnoreCase(mediaDevice.getName()) : i3 - i4;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MediaDevice)) {
            return false;
        }
        return ((MediaDevice) obj).getId().equals(getId());
    }
}
