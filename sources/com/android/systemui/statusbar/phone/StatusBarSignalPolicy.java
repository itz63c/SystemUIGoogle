package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Handler;
import android.telephony.SubscriptionInfo;
import android.util.ArraySet;
import android.util.Log;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkController.IconState;
import com.android.systemui.statusbar.policy.NetworkController.SignalCallback;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.statusbar.policy.SecurityController.SecurityControllerCallback;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class StatusBarSignalPolicy implements SignalCallback, SecurityControllerCallback, Tunable {
    private boolean mActivityEnabled;
    private boolean mBlockAirplane;
    private boolean mBlockEthernet;
    private boolean mBlockMobile;
    private boolean mBlockWifi;
    private final Context mContext;
    private boolean mForceBlockWifi;
    private final Handler mHandler = Handler.getMain();
    private final StatusBarIconController mIconController;
    private boolean mIsAirplaneMode = false;
    private ArrayList<MobileIconState> mMobileStates = new ArrayList<>();
    private final NetworkController mNetworkController;
    private final SecurityController mSecurityController;
    private final String mSlotAirplane;
    private final String mSlotEthernet;
    private final String mSlotMobile;
    private final String mSlotVpn;
    private final String mSlotWifi;
    private WifiIconState mWifiIconState = new WifiIconState();

    public static class MobileIconState extends SignalIconState {
        public boolean needsLeadingPadding;
        public boolean roaming;
        public int strengthId;
        public int subId;
        public CharSequence typeContentDescription;
        public int typeId;

        private MobileIconState(int i) {
            super();
            this.subId = i;
        }

        public boolean equals(Object obj) {
            boolean z = false;
            if (obj != null && MobileIconState.class == obj.getClass()) {
                if (!super.equals(obj)) {
                    return false;
                }
                MobileIconState mobileIconState = (MobileIconState) obj;
                if (this.subId == mobileIconState.subId && this.strengthId == mobileIconState.strengthId && this.typeId == mobileIconState.typeId && this.roaming == mobileIconState.roaming && this.needsLeadingPadding == mobileIconState.needsLeadingPadding && Objects.equals(this.typeContentDescription, mobileIconState.typeContentDescription)) {
                    z = true;
                }
            }
            return z;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Integer.valueOf(super.hashCode()), Integer.valueOf(this.subId), Integer.valueOf(this.strengthId), Integer.valueOf(this.typeId), Boolean.valueOf(this.roaming), Boolean.valueOf(this.needsLeadingPadding), this.typeContentDescription});
        }

        public MobileIconState copy() {
            MobileIconState mobileIconState = new MobileIconState(this.subId);
            copyTo(mobileIconState);
            return mobileIconState;
        }

        public void copyTo(MobileIconState mobileIconState) {
            super.copyTo(mobileIconState);
            mobileIconState.subId = this.subId;
            mobileIconState.strengthId = this.strengthId;
            mobileIconState.typeId = this.typeId;
            mobileIconState.roaming = this.roaming;
            mobileIconState.needsLeadingPadding = this.needsLeadingPadding;
            mobileIconState.typeContentDescription = this.typeContentDescription;
        }

        /* access modifiers changed from: private */
        public static List<MobileIconState> copyStates(List<MobileIconState> list) {
            ArrayList arrayList = new ArrayList();
            for (MobileIconState mobileIconState : list) {
                MobileIconState mobileIconState2 = new MobileIconState(mobileIconState.subId);
                mobileIconState.copyTo(mobileIconState2);
                arrayList.add(mobileIconState2);
            }
            return arrayList;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("MobileIconState(subId=");
            sb.append(this.subId);
            sb.append(", strengthId=");
            sb.append(this.strengthId);
            sb.append(", roaming=");
            sb.append(this.roaming);
            sb.append(", typeId=");
            sb.append(this.typeId);
            sb.append(", visible=");
            sb.append(this.visible);
            sb.append(")");
            return sb.toString();
        }
    }

    private static abstract class SignalIconState {
        public boolean activityIn;
        public boolean activityOut;
        public String contentDescription;
        public String slot;
        public boolean visible;

        private SignalIconState() {
        }

        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            SignalIconState signalIconState = (SignalIconState) obj;
            if (this.visible == signalIconState.visible && this.activityOut == signalIconState.activityOut && this.activityIn == signalIconState.activityIn && Objects.equals(this.contentDescription, signalIconState.contentDescription) && Objects.equals(this.slot, signalIconState.slot)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Boolean.valueOf(this.visible), Boolean.valueOf(this.activityOut), this.slot});
        }

        /* access modifiers changed from: protected */
        public void copyTo(SignalIconState signalIconState) {
            signalIconState.visible = this.visible;
            signalIconState.activityIn = this.activityIn;
            signalIconState.activityOut = this.activityOut;
            signalIconState.slot = this.slot;
            signalIconState.contentDescription = this.contentDescription;
        }
    }

    public static class WifiIconState extends SignalIconState {
        public boolean airplaneSpacerVisible;
        public int resId;
        public boolean signalSpacerVisible;

        public WifiIconState() {
            super();
        }

        public boolean equals(Object obj) {
            boolean z = false;
            if (obj != null && WifiIconState.class == obj.getClass()) {
                if (!super.equals(obj)) {
                    return false;
                }
                WifiIconState wifiIconState = (WifiIconState) obj;
                if (this.resId == wifiIconState.resId && this.airplaneSpacerVisible == wifiIconState.airplaneSpacerVisible && this.signalSpacerVisible == wifiIconState.signalSpacerVisible) {
                    z = true;
                }
            }
            return z;
        }

        public void copyTo(WifiIconState wifiIconState) {
            super.copyTo(wifiIconState);
            wifiIconState.resId = this.resId;
            wifiIconState.airplaneSpacerVisible = this.airplaneSpacerVisible;
            wifiIconState.signalSpacerVisible = this.signalSpacerVisible;
        }

        public WifiIconState copy() {
            WifiIconState wifiIconState = new WifiIconState();
            copyTo(wifiIconState);
            return wifiIconState;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Integer.valueOf(super.hashCode()), Integer.valueOf(this.resId), Boolean.valueOf(this.airplaneSpacerVisible), Boolean.valueOf(this.signalSpacerVisible)});
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("WifiIconState(resId=");
            sb.append(this.resId);
            sb.append(", visible=");
            sb.append(this.visible);
            sb.append(")");
            return sb.toString();
        }
    }

    public void setMobileDataEnabled(boolean z) {
    }

    public void setNoSims(boolean z, boolean z2) {
    }

    public StatusBarSignalPolicy(Context context, StatusBarIconController statusBarIconController) {
        this.mContext = context;
        this.mSlotAirplane = context.getString(17041181);
        this.mSlotMobile = this.mContext.getString(17041198);
        this.mSlotWifi = this.mContext.getString(17041213);
        this.mSlotEthernet = this.mContext.getString(17041191);
        this.mSlotVpn = this.mContext.getString(17041212);
        this.mActivityEnabled = this.mContext.getResources().getBoolean(C2007R$bool.config_showActivity);
        this.mIconController = statusBarIconController;
        this.mNetworkController = (NetworkController) Dependency.get(NetworkController.class);
        this.mSecurityController = (SecurityController) Dependency.get(SecurityController.class);
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, "icon_blacklist");
        this.mNetworkController.addCallback(this);
        this.mSecurityController.addCallback(this);
    }

    /* access modifiers changed from: private */
    public void updateVpn() {
        boolean isVpnEnabled = this.mSecurityController.isVpnEnabled();
        this.mIconController.setIcon(this.mSlotVpn, currentVpnIconId(this.mSecurityController.isVpnBranded()), this.mContext.getResources().getString(C2017R$string.accessibility_vpn_on));
        this.mIconController.setIconVisibility(this.mSlotVpn, isVpnEnabled);
    }

    private int currentVpnIconId(boolean z) {
        return z ? C2010R$drawable.stat_sys_branded_vpn : C2010R$drawable.stat_sys_vpn_ic;
    }

    public void onStateChanged() {
        this.mHandler.post(new Runnable() {
            public final void run() {
                StatusBarSignalPolicy.this.updateVpn();
            }
        });
    }

    public void onTuningChanged(String str, String str2) {
        if ("icon_blacklist".equals(str)) {
            ArraySet iconBlacklist = StatusBarIconController.getIconBlacklist(this.mContext, str2);
            boolean contains = iconBlacklist.contains(this.mSlotAirplane);
            boolean contains2 = iconBlacklist.contains(this.mSlotMobile);
            boolean contains3 = iconBlacklist.contains(this.mSlotWifi);
            boolean contains4 = iconBlacklist.contains(this.mSlotEthernet);
            if (!(contains == this.mBlockAirplane && contains2 == this.mBlockMobile && contains4 == this.mBlockEthernet && contains3 == this.mBlockWifi)) {
                this.mBlockAirplane = contains;
                this.mBlockMobile = contains2;
                this.mBlockEthernet = contains4;
                this.mBlockWifi = contains3 || this.mForceBlockWifi;
                this.mNetworkController.removeCallback(this);
                this.mNetworkController.addCallback(this);
            }
        }
    }

    public void setWifiIndicators(boolean z, IconState iconState, IconState iconState2, boolean z2, boolean z3, String str, boolean z4, String str2) {
        boolean z5 = true;
        boolean z6 = iconState.visible && !this.mBlockWifi;
        boolean z7 = z2 && this.mActivityEnabled && z6;
        boolean z8 = z3 && this.mActivityEnabled && z6;
        WifiIconState copy = this.mWifiIconState.copy();
        copy.visible = z6;
        copy.resId = iconState.icon;
        copy.activityIn = z7;
        copy.activityOut = z8;
        copy.slot = this.mSlotWifi;
        copy.airplaneSpacerVisible = this.mIsAirplaneMode;
        copy.contentDescription = iconState.contentDescription;
        MobileIconState firstMobileState = getFirstMobileState();
        if (firstMobileState == null || firstMobileState.typeId == 0) {
            z5 = false;
        }
        copy.signalSpacerVisible = z5;
        updateWifiIconWithState(copy);
        this.mWifiIconState = copy;
    }

    private void updateShowWifiSignalSpacer(WifiIconState wifiIconState) {
        MobileIconState firstMobileState = getFirstMobileState();
        wifiIconState.signalSpacerVisible = (firstMobileState == null || firstMobileState.typeId == 0) ? false : true;
    }

    private void updateWifiIconWithState(WifiIconState wifiIconState) {
        if (!wifiIconState.visible || wifiIconState.resId <= 0) {
            this.mIconController.setIconVisibility(this.mSlotWifi, false);
            return;
        }
        this.mIconController.setSignalIcon(this.mSlotWifi, wifiIconState);
        this.mIconController.setIconVisibility(this.mSlotWifi, true);
    }

    public void setMobileDataIndicators(IconState iconState, IconState iconState2, int i, int i2, boolean z, boolean z2, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, boolean z3, int i3, boolean z4) {
        MobileIconState state = getState(i3);
        if (state != null) {
            int i4 = state.typeId;
            boolean z5 = true;
            boolean z6 = i != i4 && (i == 0 || i4 == 0);
            state.visible = iconState.visible && !this.mBlockMobile;
            state.strengthId = iconState.icon;
            state.typeId = i;
            state.contentDescription = iconState.contentDescription;
            state.typeContentDescription = charSequence;
            state.roaming = z4;
            state.activityIn = z && this.mActivityEnabled;
            if (!z2 || !this.mActivityEnabled) {
                z5 = false;
            }
            state.activityOut = z5;
            this.mIconController.setMobileIcons(this.mSlotMobile, MobileIconState.copyStates(this.mMobileStates));
            if (z6) {
                WifiIconState copy = this.mWifiIconState.copy();
                updateShowWifiSignalSpacer(copy);
                if (!Objects.equals(copy, this.mWifiIconState)) {
                    updateWifiIconWithState(copy);
                    this.mWifiIconState = copy;
                }
            }
        }
    }

    private MobileIconState getState(int i) {
        Iterator it = this.mMobileStates.iterator();
        while (it.hasNext()) {
            MobileIconState mobileIconState = (MobileIconState) it.next();
            if (mobileIconState.subId == i) {
                return mobileIconState;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unexpected subscription ");
        sb.append(i);
        Log.e("StatusBarSignalPolicy", sb.toString());
        return null;
    }

    private MobileIconState getFirstMobileState() {
        if (this.mMobileStates.size() > 0) {
            return (MobileIconState) this.mMobileStates.get(0);
        }
        return null;
    }

    public void setSubs(List<SubscriptionInfo> list) {
        if (!hasCorrectSubs(list)) {
            this.mIconController.removeAllIconsForSlot(this.mSlotMobile);
            this.mMobileStates.clear();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                this.mMobileStates.add(new MobileIconState(((SubscriptionInfo) list.get(i)).getSubscriptionId()));
            }
        }
    }

    private boolean hasCorrectSubs(List<SubscriptionInfo> list) {
        int size = list.size();
        if (size != this.mMobileStates.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (((MobileIconState) this.mMobileStates.get(i)).subId != ((SubscriptionInfo) list.get(i)).getSubscriptionId()) {
                return false;
            }
        }
        return true;
    }

    public void setEthernetIndicators(IconState iconState) {
        if (iconState.visible) {
            boolean z = this.mBlockEthernet;
        }
        int i = iconState.icon;
        String str = iconState.contentDescription;
        if (i > 0) {
            this.mIconController.setIcon(this.mSlotEthernet, i, str);
            this.mIconController.setIconVisibility(this.mSlotEthernet, true);
            return;
        }
        this.mIconController.setIconVisibility(this.mSlotEthernet, false);
    }

    public void setIsAirplaneMode(IconState iconState) {
        boolean z = iconState.visible && !this.mBlockAirplane;
        this.mIsAirplaneMode = z;
        int i = iconState.icon;
        String str = iconState.contentDescription;
        if (!z || i <= 0) {
            this.mIconController.setIconVisibility(this.mSlotAirplane, false);
            return;
        }
        this.mIconController.setIcon(this.mSlotAirplane, i, str);
        this.mIconController.setIconVisibility(this.mSlotAirplane, true);
    }
}
