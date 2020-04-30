package com.android.systemui.p007qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.wifi.AccessPoint;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.AlphaControlledSignalTileView;
import com.android.systemui.p007qs.QSDetailItems;
import com.android.systemui.p007qs.QSDetailItems.Callback;
import com.android.systemui.p007qs.QSDetailItems.Item;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.p006qs.DetailAdapter;
import com.android.systemui.plugins.p006qs.QSIconView;
import com.android.systemui.plugins.p006qs.QSTile.SignalState;
import com.android.systemui.plugins.p006qs.QSTile.SlashState;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkController.AccessPointController;
import com.android.systemui.statusbar.policy.NetworkController.AccessPointController.AccessPointCallback;
import com.android.systemui.statusbar.policy.NetworkController.IconState;
import com.android.systemui.statusbar.policy.NetworkController.SignalCallback;
import java.util.List;

/* renamed from: com.android.systemui.qs.tiles.WifiTile */
public class WifiTile extends QSTileImpl<SignalState> {
    private static final Intent WIFI_PANEL = new Intent("android.settings.panel.action.WIFI");
    /* access modifiers changed from: private */
    public static final Intent WIFI_SETTINGS = new Intent("android.settings.WIFI_SETTINGS");
    /* access modifiers changed from: private */
    public final ActivityStarter mActivityStarter;
    protected final NetworkController mController;
    /* access modifiers changed from: private */
    public final WifiDetailAdapter mDetailAdapter;
    private boolean mExpectDisabled;
    protected final WifiSignalCallback mSignalCallback = new WifiSignalCallback();
    private final SignalState mStateBeforeClick = newTileState();
    /* access modifiers changed from: private */
    public final AccessPointController mWifiController;

    /* renamed from: com.android.systemui.qs.tiles.WifiTile$CallbackInfo */
    protected static final class CallbackInfo {
        boolean activityIn;
        boolean activityOut;
        boolean connected;
        boolean enabled;
        boolean isTransient;
        String ssid;
        public String statusLabel;
        String wifiSignalContentDescription;
        int wifiSignalIconId;

        protected CallbackInfo() {
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("CallbackInfo[");
            sb.append("enabled=");
            sb.append(this.enabled);
            sb.append(",connected=");
            sb.append(this.connected);
            sb.append(",wifiSignalIconId=");
            sb.append(this.wifiSignalIconId);
            sb.append(",ssid=");
            sb.append(this.ssid);
            sb.append(",activityIn=");
            sb.append(this.activityIn);
            sb.append(",activityOut=");
            sb.append(this.activityOut);
            sb.append(",wifiSignalContentDescription=");
            sb.append(this.wifiSignalContentDescription);
            sb.append(",isTransient=");
            sb.append(this.isTransient);
            sb.append(']');
            return sb.toString();
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.WifiTile$WifiDetailAdapter */
    protected class WifiDetailAdapter implements DetailAdapter, AccessPointCallback, Callback {
        private AccessPoint[] mAccessPoints;
        private QSDetailItems mItems;

        public int getMetricsCategory() {
            return 152;
        }

        public void onDetailItemDisconnect(Item item) {
        }

        protected WifiDetailAdapter() {
        }

        public CharSequence getTitle() {
            return WifiTile.this.mContext.getString(C2017R$string.quick_settings_wifi_label);
        }

        public Intent getSettingsIntent() {
            return WifiTile.WIFI_SETTINGS;
        }

        public Boolean getToggleState() {
            return Boolean.valueOf(((SignalState) WifiTile.this.mState).value);
        }

        public void setToggleState(boolean z) {
            if (QSTileImpl.DEBUG) {
                String access$900 = WifiTile.this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("setToggleState ");
                sb.append(z);
                Log.d(access$900, sb.toString());
            }
            MetricsLogger.action(WifiTile.this.mContext, 153, z);
            WifiTile.this.mController.setWifiEnabled(z);
        }

        public View createDetailView(Context context, View view, ViewGroup viewGroup) {
            if (QSTileImpl.DEBUG) {
                String access$1200 = WifiTile.this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("createDetailView convertView=");
                sb.append(view != null);
                Log.d(access$1200, sb.toString());
            }
            this.mAccessPoints = null;
            QSDetailItems convertOrInflate = QSDetailItems.convertOrInflate(context, view, viewGroup);
            this.mItems = convertOrInflate;
            convertOrInflate.setTagSuffix("Wifi");
            this.mItems.setCallback(this);
            WifiTile.this.mWifiController.scanForAccessPoints();
            setItemsVisible(((SignalState) WifiTile.this.mState).value);
            return this.mItems;
        }

        public void onAccessPointsChanged(List<AccessPoint> list) {
            this.mAccessPoints = (AccessPoint[]) list.toArray(new AccessPoint[list.size()]);
            filterUnreachableAPs();
            updateItems();
        }

        private void filterUnreachableAPs() {
            int i = 0;
            for (AccessPoint isReachable : this.mAccessPoints) {
                if (isReachable.isReachable()) {
                    i++;
                }
            }
            AccessPoint[] accessPointArr = this.mAccessPoints;
            if (i != accessPointArr.length) {
                this.mAccessPoints = new AccessPoint[i];
                int i2 = 0;
                for (AccessPoint accessPoint : accessPointArr) {
                    if (accessPoint.isReachable()) {
                        int i3 = i2 + 1;
                        this.mAccessPoints[i2] = accessPoint;
                        i2 = i3;
                    }
                }
            }
        }

        public void onSettingsActivityTriggered(Intent intent) {
            WifiTile.this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }

        public void onDetailItemClick(Item item) {
            if (item != null) {
                Object obj = item.tag;
                if (obj != null) {
                    AccessPoint accessPoint = (AccessPoint) obj;
                    if (!accessPoint.isActive() && WifiTile.this.mWifiController.connect(accessPoint)) {
                        WifiTile.this.mHost.collapsePanels();
                    }
                    WifiTile.this.showDetail(false);
                }
            }
        }

        public void setItemsVisible(boolean z) {
            QSDetailItems qSDetailItems = this.mItems;
            if (qSDetailItems != null) {
                qSDetailItems.setItemsVisible(z);
            }
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:13:0x002f  */
        /* JADX WARNING: Removed duplicated region for block: B:15:0x003c  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateItems() {
            /*
                r6 = this;
                com.android.systemui.qs.QSDetailItems r0 = r6.mItems
                if (r0 != 0) goto L_0x0005
                return
            L_0x0005:
                com.android.settingslib.wifi.AccessPoint[] r0 = r6.mAccessPoints
                r1 = 0
                if (r0 == 0) goto L_0x000d
                int r0 = r0.length
                if (r0 > 0) goto L_0x0017
            L_0x000d:
                com.android.systemui.qs.tiles.WifiTile r0 = com.android.systemui.p007qs.tiles.WifiTile.this
                com.android.systemui.qs.tiles.WifiTile$WifiSignalCallback r2 = r0.mSignalCallback
                com.android.systemui.qs.tiles.WifiTile$CallbackInfo r2 = r2.mInfo
                boolean r2 = r2.enabled
                if (r2 != 0) goto L_0x001d
            L_0x0017:
                com.android.systemui.qs.tiles.WifiTile r0 = com.android.systemui.p007qs.tiles.WifiTile.this
                r0.fireScanStateChanged(r1)
                goto L_0x0021
            L_0x001d:
                r2 = 1
                r0.fireScanStateChanged(r2)
            L_0x0021:
                com.android.systemui.qs.tiles.WifiTile r0 = com.android.systemui.p007qs.tiles.WifiTile.this
                com.android.systemui.qs.tiles.WifiTile$WifiSignalCallback r0 = r0.mSignalCallback
                com.android.systemui.qs.tiles.WifiTile$CallbackInfo r0 = r0.mInfo
                boolean r0 = r0.enabled
                r2 = 17302861(0x108054d, float:2.4983058E-38)
                r3 = 0
                if (r0 != 0) goto L_0x003c
                com.android.systemui.qs.QSDetailItems r0 = r6.mItems
                int r1 = com.android.systemui.C2017R$string.wifi_is_off
                r0.setEmptyState(r2, r1)
                com.android.systemui.qs.QSDetailItems r6 = r6.mItems
                r6.setItems(r3)
                return
            L_0x003c:
                com.android.systemui.qs.QSDetailItems r0 = r6.mItems
                int r4 = com.android.systemui.C2017R$string.quick_settings_wifi_detail_empty_text
                r0.setEmptyState(r2, r4)
                com.android.settingslib.wifi.AccessPoint[] r0 = r6.mAccessPoints
                if (r0 == 0) goto L_0x008a
                int r0 = r0.length
                com.android.systemui.qs.QSDetailItems$Item[] r0 = new com.android.systemui.p007qs.QSDetailItems.Item[r0]
            L_0x004a:
                com.android.settingslib.wifi.AccessPoint[] r2 = r6.mAccessPoints
                int r4 = r2.length
                if (r1 >= r4) goto L_0x0089
                r2 = r2[r1]
                com.android.systemui.qs.QSDetailItems$Item r4 = new com.android.systemui.qs.QSDetailItems$Item
                r4.<init>()
                r4.tag = r2
                com.android.systemui.qs.tiles.WifiTile r5 = com.android.systemui.p007qs.tiles.WifiTile.this
                com.android.systemui.statusbar.policy.NetworkController$AccessPointController r5 = r5.mWifiController
                int r5 = r5.getIcon(r2)
                r4.iconResId = r5
                java.lang.CharSequence r5 = r2.getSsid()
                r4.line1 = r5
                boolean r5 = r2.isActive()
                if (r5 == 0) goto L_0x0075
                java.lang.String r5 = r2.getSummary()
                goto L_0x0076
            L_0x0075:
                r5 = r3
            L_0x0076:
                r4.line2 = r5
                int r2 = r2.getSecurity()
                if (r2 == 0) goto L_0x0081
                int r2 = com.android.systemui.C2010R$drawable.qs_ic_wifi_lock
                goto L_0x0082
            L_0x0081:
                r2 = -1
            L_0x0082:
                r4.icon2 = r2
                r0[r1] = r4
                int r1 = r1 + 1
                goto L_0x004a
            L_0x0089:
                r3 = r0
            L_0x008a:
                com.android.systemui.qs.QSDetailItems r6 = r6.mItems
                r6.setItems(r3)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.tiles.WifiTile.WifiDetailAdapter.updateItems():void");
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.WifiTile$WifiSignalCallback */
    protected final class WifiSignalCallback implements SignalCallback {
        final CallbackInfo mInfo = new CallbackInfo();

        protected WifiSignalCallback() {
        }

        public void setWifiIndicators(boolean z, IconState iconState, IconState iconState2, boolean z2, boolean z3, String str, boolean z4, String str2) {
            if (QSTileImpl.DEBUG) {
                String access$100 = WifiTile.this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onWifiSignalChanged enabled=");
                sb.append(z);
                Log.d(access$100, sb.toString());
            }
            CallbackInfo callbackInfo = this.mInfo;
            callbackInfo.enabled = z;
            callbackInfo.connected = iconState2.visible;
            callbackInfo.wifiSignalIconId = iconState2.icon;
            callbackInfo.ssid = str;
            callbackInfo.activityIn = z2;
            callbackInfo.activityOut = z3;
            callbackInfo.wifiSignalContentDescription = iconState2.contentDescription;
            callbackInfo.isTransient = z4;
            callbackInfo.statusLabel = str2;
            if (WifiTile.this.isShowingDetail()) {
                WifiTile.this.mDetailAdapter.updateItems();
            }
            WifiTile.this.refreshState();
        }
    }

    public int getMetricsCategory() {
        return 126;
    }

    public WifiTile(QSHost qSHost, NetworkController networkController, ActivityStarter activityStarter) {
        super(qSHost);
        this.mController = networkController;
        this.mWifiController = networkController.getAccessPointController();
        this.mDetailAdapter = (WifiDetailAdapter) createDetailAdapter();
        this.mActivityStarter = activityStarter;
        this.mController.observe(getLifecycle(), this.mSignalCallback);
    }

    public SignalState newTileState() {
        return new SignalState();
    }

    public void setDetailListening(boolean z) {
        if (z) {
            this.mWifiController.addAccessPointCallback(this.mDetailAdapter);
        } else {
            this.mWifiController.removeAccessPointCallback(this.mDetailAdapter);
        }
    }

    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }

    /* access modifiers changed from: protected */
    public DetailAdapter createDetailAdapter() {
        return new WifiDetailAdapter();
    }

    public QSIconView createTileView(Context context) {
        return new AlphaControlledSignalTileView(context);
    }

    public Intent getLongClickIntent() {
        if (this.mQSSettingsPanelOption == QSSettingsPanel.OPEN_LONG_PRESS) {
            return WIFI_PANEL;
        }
        return WIFI_SETTINGS;
    }

    public boolean supportsDetailView() {
        return getDetailAdapter() != null && this.mQSSettingsPanelOption == QSSettingsPanel.OPEN_CLICK;
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        Object obj;
        if (this.mQSSettingsPanelOption == QSSettingsPanel.OPEN_CLICK) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(WIFI_PANEL, 0);
            return;
        }
        ((SignalState) this.mState).copyTo(this.mStateBeforeClick);
        boolean z = ((SignalState) this.mState).value;
        if (z) {
            obj = null;
        } else {
            obj = QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        }
        refreshState(obj);
        this.mController.setWifiEnabled(!z);
        this.mExpectDisabled = z;
        if (z) {
            this.mHandler.postDelayed(new Runnable() {
                public final void run() {
                    WifiTile.this.lambda$handleClick$0$WifiTile();
                }
            }, 350);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleClick$0 */
    public /* synthetic */ void lambda$handleClick$0$WifiTile() {
        if (this.mExpectDisabled) {
            this.mExpectDisabled = false;
            refreshState();
        }
    }

    /* access modifiers changed from: protected */
    public void handleSecondaryClick() {
        if (!this.mWifiController.canConfigWifi()) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.WIFI_SETTINGS"), 0);
            return;
        }
        showDetail(true);
        if (!((SignalState) this.mState).value) {
            this.mController.setWifiEnabled(true);
        }
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(C2017R$string.quick_settings_wifi_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(SignalState signalState, Object obj) {
        if (QSTileImpl.DEBUG) {
            String str = this.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("handleUpdateState arg=");
            sb.append(obj);
            Log.d(str, sb.toString());
        }
        CallbackInfo callbackInfo = this.mSignalCallback.mInfo;
        if (this.mExpectDisabled) {
            if (!callbackInfo.enabled) {
                this.mExpectDisabled = false;
            } else {
                return;
            }
        }
        boolean z = obj == QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        boolean z2 = callbackInfo.enabled && callbackInfo.wifiSignalIconId > 0 && callbackInfo.ssid != null;
        boolean z3 = callbackInfo.wifiSignalIconId > 0 && callbackInfo.ssid == null;
        if (signalState.value != callbackInfo.enabled) {
            this.mDetailAdapter.setItemsVisible(callbackInfo.enabled);
            fireToggleStateChanged(callbackInfo.enabled);
        }
        if (signalState.slash == null) {
            SlashState slashState = new SlashState();
            signalState.slash = slashState;
            slashState.rotation = 6.0f;
        }
        signalState.slash.isSlashed = false;
        boolean z4 = z || callbackInfo.isTransient;
        signalState.secondaryLabel = getSecondaryLabel(z4, callbackInfo.statusLabel);
        signalState.state = 2;
        signalState.dualTarget = true;
        signalState.value = z || callbackInfo.enabled;
        signalState.activityIn = callbackInfo.enabled && callbackInfo.activityIn;
        signalState.activityOut = callbackInfo.enabled && callbackInfo.activityOut;
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        Resources resources = this.mContext.getResources();
        if (z4) {
            signalState.icon = ResourceIcon.get(17302829);
            signalState.label = resources.getString(C2017R$string.quick_settings_wifi_label);
        } else if (!signalState.value) {
            signalState.slash.isSlashed = true;
            signalState.state = 1;
            signalState.icon = ResourceIcon.get(17302861);
            signalState.label = resources.getString(C2017R$string.quick_settings_wifi_label);
        } else if (z2) {
            signalState.icon = ResourceIcon.get(callbackInfo.wifiSignalIconId);
            signalState.label = removeDoubleQuotes(callbackInfo.ssid);
        } else if (z3) {
            signalState.icon = ResourceIcon.get(17302861);
            signalState.label = resources.getString(C2017R$string.quick_settings_wifi_label);
        } else {
            signalState.icon = ResourceIcon.get(17302861);
            signalState.label = resources.getString(C2017R$string.quick_settings_wifi_label);
        }
        stringBuffer.append(this.mContext.getString(C2017R$string.quick_settings_wifi_label));
        String str2 = ",";
        stringBuffer.append(str2);
        if (signalState.value && z2) {
            stringBuffer2.append(callbackInfo.wifiSignalContentDescription);
            stringBuffer.append(removeDoubleQuotes(callbackInfo.ssid));
            if (!TextUtils.isEmpty(signalState.secondaryLabel)) {
                stringBuffer.append(str2);
                stringBuffer.append(signalState.secondaryLabel);
            }
        }
        signalState.stateDescription = stringBuffer2.toString();
        signalState.contentDescription = stringBuffer.toString();
        signalState.dualLabelContentDescription = resources.getString(C2017R$string.accessibility_quick_settings_open_settings, new Object[]{getTileLabel()});
        signalState.expandedAccessibilityClassName = Switch.class.getName();
    }

    private CharSequence getSecondaryLabel(boolean z, String str) {
        return z ? this.mContext.getString(C2017R$string.quick_settings_wifi_secondary_label_transient) : str;
    }

    /* access modifiers changed from: protected */
    public boolean shouldAnnouncementBeDelayed() {
        return this.mStateBeforeClick.value == ((SignalState) this.mState).value;
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((SignalState) this.mState).value) {
            return this.mContext.getString(C2017R$string.accessibility_quick_settings_wifi_changed_on);
        }
        return this.mContext.getString(C2017R$string.accessibility_quick_settings_wifi_changed_off);
    }

    public boolean isAvailable() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi");
    }

    private static String removeDoubleQuotes(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length > 1 && str.charAt(0) == '\"') {
            int i = length - 1;
            if (str.charAt(i) == '\"') {
                str = str.substring(1, i);
            }
        }
        return str;
    }
}
