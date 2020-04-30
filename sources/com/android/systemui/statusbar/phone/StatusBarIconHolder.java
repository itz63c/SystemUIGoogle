package com.android.systemui.statusbar.phone;

import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy.MobileIconState;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy.WifiIconState;

public class StatusBarIconHolder {
    private StatusBarIcon mIcon;
    private MobileIconState mMobileState;
    private int mTag = 0;
    private int mType = 0;
    private WifiIconState mWifiState;

    public static StatusBarIconHolder fromIcon(StatusBarIcon statusBarIcon) {
        StatusBarIconHolder statusBarIconHolder = new StatusBarIconHolder();
        statusBarIconHolder.mIcon = statusBarIcon;
        return statusBarIconHolder;
    }

    public static StatusBarIconHolder fromWifiIconState(WifiIconState wifiIconState) {
        StatusBarIconHolder statusBarIconHolder = new StatusBarIconHolder();
        statusBarIconHolder.mWifiState = wifiIconState;
        statusBarIconHolder.mType = 1;
        return statusBarIconHolder;
    }

    public static StatusBarIconHolder fromMobileIconState(MobileIconState mobileIconState) {
        StatusBarIconHolder statusBarIconHolder = new StatusBarIconHolder();
        statusBarIconHolder.mMobileState = mobileIconState;
        statusBarIconHolder.mType = 2;
        statusBarIconHolder.mTag = mobileIconState.subId;
        return statusBarIconHolder;
    }

    public int getType() {
        return this.mType;
    }

    public StatusBarIcon getIcon() {
        return this.mIcon;
    }

    public WifiIconState getWifiState() {
        return this.mWifiState;
    }

    public void setWifiState(WifiIconState wifiIconState) {
        this.mWifiState = wifiIconState;
    }

    public MobileIconState getMobileState() {
        return this.mMobileState;
    }

    public void setMobileState(MobileIconState mobileIconState) {
        this.mMobileState = mobileIconState;
    }

    public boolean isVisible() {
        int i = this.mType;
        if (i == 0) {
            return this.mIcon.visible;
        }
        if (i == 1) {
            return this.mWifiState.visible;
        }
        if (i != 2) {
            return true;
        }
        return this.mMobileState.visible;
    }

    public void setVisible(boolean z) {
        if (isVisible() != z) {
            int i = this.mType;
            if (i == 0) {
                this.mIcon.visible = z;
            } else if (i == 1) {
                this.mWifiState.visible = z;
            } else if (i == 2) {
                this.mMobileState.visible = z;
            }
        }
    }

    public int getTag() {
        return this.mTag;
    }
}
