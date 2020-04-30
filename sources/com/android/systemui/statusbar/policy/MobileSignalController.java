package com.android.systemui.statusbar.policy;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Global;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.Utils;
import com.android.settingslib.graph.SignalDrawable;
import com.android.settingslib.net.SignalStrengthUtil;
import com.android.systemui.C2017R$string;
import com.android.systemui.statusbar.policy.NetworkControllerImpl.SubscriptionDefaults;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public class MobileSignalController extends SignalController<MobileState, MobileIconGroup> {
    private Config mConfig;
    /* access modifiers changed from: private */
    public int mDataState = 0;
    private MobileIconGroup mDefaultIcons;
    private final SubscriptionDefaults mDefaults;
    @VisibleForTesting
    boolean mInflateSignalStrengths = false;
    private final String mNetworkNameDefault;
    private final String mNetworkNameSeparator;
    final Map<String, MobileIconGroup> mNetworkToIconLookup = new HashMap();
    private final ContentObserver mObserver;
    private final TelephonyManager mPhone;
    @VisibleForTesting
    final PhoneStateListener mPhoneStateListener;
    /* access modifiers changed from: private */
    public ServiceState mServiceState;
    /* access modifiers changed from: private */
    public SignalStrength mSignalStrength;
    final SubscriptionInfo mSubscriptionInfo;
    /* access modifiers changed from: private */
    public TelephonyDisplayInfo mTelephonyDisplayInfo = new TelephonyDisplayInfo(0, 0);

    static class MobileIconGroup extends IconGroup {
        final int mDataContentDescription;
        final int mDataType;
        final boolean mIsWide;
        final int mQsDataType;

        public MobileIconGroup(String str, int[][] iArr, int[][] iArr2, int[] iArr3, int i, int i2, int i3, int i4, int i5, int i6, int i7, boolean z) {
            super(str, iArr, iArr2, iArr3, i, i2, i3, i4, i5);
            this.mDataContentDescription = i6;
            this.mDataType = i7;
            this.mIsWide = z;
            this.mQsDataType = i7;
        }
    }

    class MobilePhoneStateListener extends PhoneStateListener {
        public MobilePhoneStateListener(Executor executor) {
            super(executor);
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            String str;
            if (SignalController.DEBUG) {
                String str2 = MobileSignalController.this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("onSignalStrengthsChanged signalStrength=");
                sb.append(signalStrength);
                if (signalStrength == null) {
                    str = "";
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(" level=");
                    sb2.append(signalStrength.getLevel());
                    str = sb2.toString();
                }
                sb.append(str);
                Log.d(str2, sb.toString());
            }
            MobileSignalController.this.mSignalStrength = signalStrength;
            MobileSignalController.this.updateTelephony();
        }

        public void onServiceStateChanged(ServiceState serviceState) {
            if (SignalController.DEBUG) {
                String str = MobileSignalController.this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("onServiceStateChanged voiceState=");
                sb.append(serviceState.getState());
                sb.append(" dataState=");
                sb.append(serviceState.getDataRegistrationState());
                Log.d(str, sb.toString());
            }
            MobileSignalController.this.mServiceState = serviceState;
        }

        public void onDataConnectionStateChanged(int i, int i2) {
            if (SignalController.DEBUG) {
                String str = MobileSignalController.this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("onDataConnectionStateChanged: state=");
                sb.append(i);
                sb.append(" type=");
                sb.append(i2);
                Log.d(str, sb.toString());
            }
            MobileSignalController.this.mDataState = i;
            if (i2 != MobileSignalController.this.mTelephonyDisplayInfo.getNetworkType()) {
                MobileSignalController.this.mTelephonyDisplayInfo = new TelephonyDisplayInfo(i2, 0);
            }
            MobileSignalController.this.updateTelephony();
        }

        public void onDataActivity(int i) {
            if (SignalController.DEBUG) {
                String str = MobileSignalController.this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("onDataActivity: direction=");
                sb.append(i);
                Log.d(str, sb.toString());
            }
            MobileSignalController.this.setActivity(i);
        }

        public void onCarrierNetworkChange(boolean z) {
            if (SignalController.DEBUG) {
                String str = MobileSignalController.this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("onCarrierNetworkChange: active=");
                sb.append(z);
                Log.d(str, sb.toString());
            }
            MobileSignalController mobileSignalController = MobileSignalController.this;
            ((MobileState) mobileSignalController.mCurrentState).carrierNetworkChangeMode = z;
            mobileSignalController.updateTelephony();
        }

        public void onActiveDataSubscriptionIdChanged(int i) {
            if (SignalController.DEBUG) {
                String str = MobileSignalController.this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("onActiveDataSubscriptionIdChanged: subId=");
                sb.append(i);
                Log.d(str, sb.toString());
            }
            MobileSignalController.this.updateDataSim();
            MobileSignalController.this.updateTelephony();
        }

        public void onDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) {
            if (SignalController.DEBUG) {
                String str = MobileSignalController.this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("onDisplayInfoChanged: telephonyDisplayInfo=");
                sb.append(telephonyDisplayInfo);
                Log.d(str, sb.toString());
            }
            MobileSignalController.this.mTelephonyDisplayInfo = telephonyDisplayInfo;
            MobileSignalController.this.updateTelephony();
        }
    }

    static class MobileState extends State {
        boolean airplaneMode;
        boolean carrierNetworkChangeMode;
        boolean dataConnected;
        boolean dataSim;
        boolean defaultDataOff;
        boolean isDefault;
        boolean isEmergency;
        String networkName;
        String networkNameData;
        boolean roaming;
        boolean userSetup;

        MobileState() {
        }

        public void copyFrom(State state) {
            super.copyFrom(state);
            MobileState mobileState = (MobileState) state;
            this.dataSim = mobileState.dataSim;
            this.networkName = mobileState.networkName;
            this.networkNameData = mobileState.networkNameData;
            this.dataConnected = mobileState.dataConnected;
            this.isDefault = mobileState.isDefault;
            this.isEmergency = mobileState.isEmergency;
            this.airplaneMode = mobileState.airplaneMode;
            this.carrierNetworkChangeMode = mobileState.carrierNetworkChangeMode;
            this.userSetup = mobileState.userSetup;
            this.roaming = mobileState.roaming;
            this.defaultDataOff = mobileState.defaultDataOff;
        }

        /* access modifiers changed from: protected */
        public void toString(StringBuilder sb) {
            super.toString(sb);
            sb.append(',');
            sb.append("dataSim=");
            sb.append(this.dataSim);
            sb.append(',');
            sb.append("networkName=");
            sb.append(this.networkName);
            sb.append(',');
            sb.append("networkNameData=");
            sb.append(this.networkNameData);
            sb.append(',');
            sb.append("dataConnected=");
            sb.append(this.dataConnected);
            sb.append(',');
            sb.append("roaming=");
            sb.append(this.roaming);
            sb.append(',');
            sb.append("isDefault=");
            sb.append(this.isDefault);
            sb.append(',');
            sb.append("isEmergency=");
            sb.append(this.isEmergency);
            sb.append(',');
            sb.append("airplaneMode=");
            sb.append(this.airplaneMode);
            sb.append(',');
            sb.append("carrierNetworkChangeMode=");
            sb.append(this.carrierNetworkChangeMode);
            sb.append(',');
            sb.append("userSetup=");
            sb.append(this.userSetup);
            sb.append(',');
            sb.append("defaultDataOff=");
            sb.append(this.defaultDataOff);
        }

        public boolean equals(Object obj) {
            if (super.equals(obj)) {
                MobileState mobileState = (MobileState) obj;
                if (Objects.equals(mobileState.networkName, this.networkName) && Objects.equals(mobileState.networkNameData, this.networkNameData) && mobileState.dataSim == this.dataSim && mobileState.dataConnected == this.dataConnected && mobileState.isEmergency == this.isEmergency && mobileState.airplaneMode == this.airplaneMode && mobileState.carrierNetworkChangeMode == this.carrierNetworkChangeMode && mobileState.userSetup == this.userSetup && mobileState.isDefault == this.isDefault && mobileState.roaming == this.roaming && mobileState.defaultDataOff == this.defaultDataOff) {
                    return true;
                }
            }
            return false;
        }
    }

    public MobileSignalController(Context context, Config config, boolean z, TelephonyManager telephonyManager, CallbackHandler callbackHandler, NetworkControllerImpl networkControllerImpl, SubscriptionInfo subscriptionInfo, SubscriptionDefaults subscriptionDefaults, Looper looper) {
        String str;
        boolean z2 = z;
        Looper looper2 = looper;
        StringBuilder sb = new StringBuilder();
        sb.append("MobileSignalController(");
        sb.append(subscriptionInfo.getSubscriptionId());
        sb.append(")");
        String sb2 = sb.toString();
        super(sb2, context, 0, callbackHandler, networkControllerImpl);
        this.mConfig = config;
        this.mPhone = telephonyManager;
        this.mDefaults = subscriptionDefaults;
        this.mSubscriptionInfo = subscriptionInfo;
        this.mPhoneStateListener = new MobilePhoneStateListener(new Executor(new Handler(looper2)) {
            public final /* synthetic */ Handler f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(Runnable runnable) {
                this.f$0.post(runnable);
            }
        });
        this.mNetworkNameSeparator = getTextIfExists(C2017R$string.status_bar_network_name_separator).toString();
        this.mNetworkNameDefault = getTextIfExists(17040335).toString();
        mapIconSets();
        if (subscriptionInfo.getCarrierName() != null) {
            str = subscriptionInfo.getCarrierName().toString();
        } else {
            str = this.mNetworkNameDefault;
        }
        T t = this.mLastState;
        MobileState mobileState = (MobileState) t;
        T t2 = this.mCurrentState;
        ((MobileState) t2).networkName = str;
        mobileState.networkName = str;
        MobileState mobileState2 = (MobileState) t;
        ((MobileState) t2).networkNameData = str;
        mobileState2.networkNameData = str;
        MobileState mobileState3 = (MobileState) t;
        ((MobileState) t2).enabled = z2;
        mobileState3.enabled = z2;
        MobileState mobileState4 = (MobileState) t;
        MobileState mobileState5 = (MobileState) t2;
        MobileIconGroup mobileIconGroup = this.mDefaultIcons;
        mobileState5.iconGroup = mobileIconGroup;
        mobileState4.iconGroup = mobileIconGroup;
        updateDataSim();
        this.mObserver = new ContentObserver(new Handler(looper2)) {
            public void onChange(boolean z) {
                MobileSignalController.this.updateTelephony();
            }
        };
    }

    public void setConfiguration(Config config) {
        this.mConfig = config;
        updateInflateSignalStrength();
        mapIconSets();
        updateTelephony();
    }

    public void setAirplaneMode(boolean z) {
        ((MobileState) this.mCurrentState).airplaneMode = z;
        notifyListenersIfNecessary();
    }

    public void setUserSetupComplete(boolean z) {
        ((MobileState) this.mCurrentState).userSetup = z;
        notifyListenersIfNecessary();
    }

    public void updateConnectivity(BitSet bitSet, BitSet bitSet2) {
        boolean z = bitSet2.get(this.mTransportType);
        ((MobileState) this.mCurrentState).isDefault = bitSet.get(this.mTransportType);
        T t = this.mCurrentState;
        ((MobileState) t).inetCondition = (z || !((MobileState) t).isDefault) ? 1 : 0;
        notifyListenersIfNecessary();
    }

    public void setCarrierNetworkChangeMode(boolean z) {
        ((MobileState) this.mCurrentState).carrierNetworkChangeMode = z;
        updateTelephony();
    }

    public void registerListener() {
        this.mPhone.listen(this.mPhoneStateListener, 5308897);
        String str = "mobile_data";
        this.mContext.getContentResolver().registerContentObserver(Global.getUriFor(str), true, this.mObserver);
        ContentResolver contentResolver = this.mContext.getContentResolver();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(this.mSubscriptionInfo.getSubscriptionId());
        contentResolver.registerContentObserver(Global.getUriFor(sb.toString()), true, this.mObserver);
    }

    public void unregisterListener() {
        this.mPhone.listen(this.mPhoneStateListener, 0);
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x012e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mapIconSets() {
        /*
            r7 = this;
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            r0.clear()
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            r1 = 5
            java.lang.String r1 = r7.toIconKey(r1)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r2 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            r0.put(r1, r2)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            r1 = 6
            java.lang.String r1 = r7.toIconKey(r1)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r2 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            r0.put(r1, r2)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            r1 = 12
            java.lang.String r1 = r7.toIconKey(r1)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r2 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            r0.put(r1, r2)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            r1 = 14
            java.lang.String r1 = r7.toIconKey(r1)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r2 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            r0.put(r1, r2)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            r1 = 3
            java.lang.String r2 = r7.toIconKey(r1)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r3 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            r0.put(r2, r3)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            r2 = 17
            java.lang.String r2 = r7.toIconKey(r2)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r3 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            r0.put(r2, r3)
            com.android.systemui.statusbar.policy.NetworkControllerImpl$Config r0 = r7.mConfig
            boolean r0 = r0.showAtLeast3G
            r2 = 7
            r3 = 0
            r4 = 4
            r5 = 2
            if (r0 != 0) goto L_0x008b
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r3 = r7.toIconKey(r3)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.UNKNOWN
            r0.put(r3, r6)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r3 = r7.toIconKey(r5)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.f80E
            r0.put(r3, r6)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r3 = r7.toIconKey(r4)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.ONE_X
            r0.put(r3, r6)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r2 = r7.toIconKey(r2)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r3 = com.android.systemui.statusbar.policy.TelephonyIcons.ONE_X
            r0.put(r2, r3)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r0 = com.android.systemui.statusbar.policy.TelephonyIcons.f81G
            r7.mDefaultIcons = r0
            goto L_0x00bb
        L_0x008b:
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r3 = r7.toIconKey(r3)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            r0.put(r3, r6)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r3 = r7.toIconKey(r5)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            r0.put(r3, r6)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r3 = r7.toIconKey(r4)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            r0.put(r3, r6)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r2 = r7.toIconKey(r2)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r3 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            r0.put(r2, r3)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r0 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            r7.mDefaultIcons = r0
        L_0x00bb:
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r0 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            com.android.systemui.statusbar.policy.NetworkControllerImpl$Config r2 = r7.mConfig
            boolean r3 = r2.show4gFor3g
            if (r3 == 0) goto L_0x00c6
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r0 = com.android.systemui.statusbar.policy.TelephonyIcons.FOUR_G
            goto L_0x00cf
        L_0x00c6:
            boolean r2 = r2.hspaDataDistinguishable
            if (r2 == 0) goto L_0x00cf
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r0 = com.android.systemui.statusbar.policy.TelephonyIcons.f82H
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r2 = com.android.systemui.statusbar.policy.TelephonyIcons.H_PLUS
            goto L_0x00d0
        L_0x00cf:
            r2 = r0
        L_0x00d0:
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r3 = r7.mNetworkToIconLookup
            r6 = 8
            java.lang.String r6 = r7.toIconKey(r6)
            r3.put(r6, r0)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r3 = r7.mNetworkToIconLookup
            r6 = 9
            java.lang.String r6 = r7.toIconKey(r6)
            r3.put(r6, r0)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r3 = r7.mNetworkToIconLookup
            r6 = 10
            java.lang.String r6 = r7.toIconKey(r6)
            r3.put(r6, r0)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            r3 = 15
            java.lang.String r3 = r7.toIconKey(r3)
            r0.put(r3, r2)
            com.android.systemui.statusbar.policy.NetworkControllerImpl$Config r0 = r7.mConfig
            boolean r0 = r0.show4gForLte
            r2 = 13
            r3 = 1
            if (r0 == 0) goto L_0x012e
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r2 = r7.toIconKey(r2)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.FOUR_G
            r0.put(r2, r6)
            com.android.systemui.statusbar.policy.NetworkControllerImpl$Config r0 = r7.mConfig
            boolean r0 = r0.hideLtePlus
            if (r0 == 0) goto L_0x0122
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r2 = r7.toDisplayIconKey(r3)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r3 = com.android.systemui.statusbar.policy.TelephonyIcons.FOUR_G
            r0.put(r2, r3)
            goto L_0x0156
        L_0x0122:
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r2 = r7.toDisplayIconKey(r3)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r3 = com.android.systemui.statusbar.policy.TelephonyIcons.FOUR_G_PLUS
            r0.put(r2, r3)
            goto L_0x0156
        L_0x012e:
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r2 = r7.toIconKey(r2)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.LTE
            r0.put(r2, r6)
            com.android.systemui.statusbar.policy.NetworkControllerImpl$Config r0 = r7.mConfig
            boolean r0 = r0.hideLtePlus
            if (r0 == 0) goto L_0x014b
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r2 = r7.toDisplayIconKey(r3)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r3 = com.android.systemui.statusbar.policy.TelephonyIcons.LTE
            r0.put(r2, r3)
            goto L_0x0156
        L_0x014b:
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r2 = r7.toDisplayIconKey(r3)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r3 = com.android.systemui.statusbar.policy.TelephonyIcons.LTE_PLUS
            r0.put(r2, r3)
        L_0x0156:
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            r2 = 18
            java.lang.String r2 = r7.toIconKey(r2)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r3 = com.android.systemui.statusbar.policy.TelephonyIcons.WFC
            r0.put(r2, r3)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r2 = r7.toDisplayIconKey(r5)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r3 = com.android.systemui.statusbar.policy.TelephonyIcons.LTE_CA_5G_E
            r0.put(r2, r3)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r1 = r7.toDisplayIconKey(r1)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r2 = com.android.systemui.statusbar.policy.TelephonyIcons.NR_5G
            r0.put(r1, r2)
            java.util.Map<java.lang.String, com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup> r0 = r7.mNetworkToIconLookup
            java.lang.String r7 = r7.toDisplayIconKey(r4)
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r1 = com.android.systemui.statusbar.policy.TelephonyIcons.NR_5G_PLUS
            r0.put(r7, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.MobileSignalController.mapIconSets():void");
    }

    private String getIconKey() {
        if (this.mTelephonyDisplayInfo.getOverrideNetworkType() == 0) {
            return toIconKey(this.mTelephonyDisplayInfo.getNetworkType());
        }
        return toDisplayIconKey(this.mTelephonyDisplayInfo.getOverrideNetworkType());
    }

    private String toIconKey(int i) {
        return Integer.toString(i);
    }

    private String toDisplayIconKey(int i) {
        if (i == 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(toIconKey(13));
            sb.append("_CA");
            return sb.toString();
        } else if (i == 2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(toIconKey(13));
            sb2.append("_CA_Plus");
            return sb2.toString();
        } else if (i != 3) {
            return i != 4 ? "unsupported" : "5G_Plus";
        } else {
            return "5G";
        }
    }

    private void updateInflateSignalStrength() {
        this.mInflateSignalStrengths = SignalStrengthUtil.shouldInflateSignalStrength(this.mContext, this.mSubscriptionInfo.getSubscriptionId());
    }

    private int getNumLevels() {
        if (this.mInflateSignalStrengths) {
            return CellSignalStrength.getNumSignalStrengthLevels() + 1;
        }
        return CellSignalStrength.getNumSignalStrengthLevels();
    }

    public int getCurrentIconId() {
        T t = this.mCurrentState;
        if (((MobileState) t).iconGroup == TelephonyIcons.CARRIER_NETWORK_CHANGE) {
            return SignalDrawable.getCarrierChangeState(getNumLevels());
        }
        boolean z = false;
        if (((MobileState) t).connected) {
            int i = ((MobileState) t).level;
            if (this.mInflateSignalStrengths) {
                i++;
            }
            T t2 = this.mCurrentState;
            boolean z2 = ((MobileState) t2).userSetup && (((MobileState) t2).iconGroup == TelephonyIcons.DATA_DISABLED || (((MobileState) t2).iconGroup == TelephonyIcons.NOT_DEFAULT_DATA && ((MobileState) t2).defaultDataOff));
            boolean z3 = ((MobileState) this.mCurrentState).inetCondition == 0;
            if (z2 || z3) {
                z = true;
            }
            return SignalDrawable.getState(i, getNumLevels(), z);
        } else if (((MobileState) t).enabled) {
            return SignalDrawable.getEmptyState(getNumLevels());
        } else {
            return 0;
        }
    }

    public int getQsCurrentIconId() {
        return getCurrentIconId();
    }

    /* JADX WARNING: type inference failed for: r9v4 */
    /* JADX WARNING: type inference failed for: r13v0, types: [java.lang.CharSequence] */
    /* JADX WARNING: type inference failed for: r9v5 */
    /* JADX WARNING: type inference failed for: r13v1 */
    /* JADX WARNING: type inference failed for: r10v12, types: [com.android.systemui.statusbar.policy.NetworkController$IconState] */
    /* JADX WARNING: type inference failed for: r9v7 */
    /* JADX WARNING: type inference failed for: r13v7 */
    /* JADX WARNING: type inference failed for: r9v8 */
    /* JADX WARNING: type inference failed for: r9v9, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r9v10 */
    /* JADX WARNING: type inference failed for: r9v11 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 5 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyListeners(com.android.systemui.statusbar.policy.NetworkController.SignalCallback r18) {
        /*
            r17 = this;
            r0 = r17
            com.android.systemui.statusbar.policy.SignalController$IconGroup r1 = r17.getIcons()
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r1 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileIconGroup) r1
            int r2 = r17.getContentDescription()
            java.lang.CharSequence r2 = r0.getTextIfExists(r2)
            java.lang.String r2 = r2.toString()
            int r3 = r1.mDataContentDescription
            java.lang.CharSequence r12 = r0.getTextIfExists(r3)
            java.lang.String r3 = r12.toString()
            r4 = 0
            android.text.Spanned r3 = android.text.Html.fromHtml(r3, r4)
            java.lang.String r3 = r3.toString()
            T r5 = r0.mCurrentState
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r5 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r5
            int r5 = r5.inetCondition
            if (r5 != 0) goto L_0x0037
            android.content.Context r3 = r0.mContext
            int r5 = com.android.systemui.C2017R$string.data_connection_no_internet
            java.lang.String r3 = r3.getString(r5)
        L_0x0037:
            r11 = r3
            T r3 = r0.mCurrentState
            r5 = r3
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r5 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r5
            com.android.systemui.statusbar.policy.SignalController$IconGroup r5 = r5.iconGroup
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.DATA_DISABLED
            r7 = 1
            if (r5 == r6) goto L_0x004c
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r3 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r3
            com.android.systemui.statusbar.policy.SignalController$IconGroup r3 = r3.iconGroup
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r5 = com.android.systemui.statusbar.policy.TelephonyIcons.NOT_DEFAULT_DATA
            if (r3 != r5) goto L_0x0056
        L_0x004c:
            T r3 = r0.mCurrentState
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r3 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r3
            boolean r3 = r3.userSetup
            if (r3 == 0) goto L_0x0056
            r3 = r7
            goto L_0x0057
        L_0x0056:
            r3 = r4
        L_0x0057:
            T r5 = r0.mCurrentState
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r5 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r5
            boolean r5 = r5.dataConnected
            if (r5 != 0) goto L_0x0064
            if (r3 == 0) goto L_0x0062
            goto L_0x0064
        L_0x0062:
            r5 = r4
            goto L_0x0065
        L_0x0064:
            r5 = r7
        L_0x0065:
            com.android.systemui.statusbar.policy.NetworkController$IconState r6 = new com.android.systemui.statusbar.policy.NetworkController$IconState
            T r8 = r0.mCurrentState
            r9 = r8
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r9 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r9
            boolean r9 = r9.enabled
            if (r9 == 0) goto L_0x0078
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r8 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r8
            boolean r8 = r8.airplaneMode
            if (r8 != 0) goto L_0x0078
            r8 = r7
            goto L_0x0079
        L_0x0078:
            r8 = r4
        L_0x0079:
            int r9 = r17.getCurrentIconId()
            r6.<init>(r8, r9, r2)
            T r8 = r0.mCurrentState
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r8 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r8
            boolean r8 = r8.dataSim
            r9 = 0
            if (r8 == 0) goto L_0x00c2
            if (r5 != 0) goto L_0x0094
            com.android.systemui.statusbar.policy.NetworkControllerImpl$Config r8 = r0.mConfig
            boolean r8 = r8.alwaysShowDataRatIcon
            if (r8 == 0) goto L_0x0092
            goto L_0x0094
        L_0x0092:
            r8 = r4
            goto L_0x0096
        L_0x0094:
            int r8 = r1.mQsDataType
        L_0x0096:
            com.android.systemui.statusbar.policy.NetworkController$IconState r10 = new com.android.systemui.statusbar.policy.NetworkController$IconState
            T r13 = r0.mCurrentState
            r14 = r13
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r14 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r14
            boolean r14 = r14.enabled
            if (r14 == 0) goto L_0x00a9
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r13 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r13
            boolean r13 = r13.isEmergency
            if (r13 != 0) goto L_0x00a9
            r13 = r7
            goto L_0x00aa
        L_0x00a9:
            r13 = r4
        L_0x00aa:
            int r14 = r17.getQsCurrentIconId()
            r10.<init>(r13, r14, r2)
            T r2 = r0.mCurrentState
            r13 = r2
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r13 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r13
            boolean r13 = r13.isEmergency
            if (r13 == 0) goto L_0x00bb
            goto L_0x00bf
        L_0x00bb:
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r2 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r2
            java.lang.String r9 = r2.networkName
        L_0x00bf:
            r13 = r9
            r9 = r10
            goto L_0x00c4
        L_0x00c2:
            r8 = r4
            r13 = r9
        L_0x00c4:
            T r2 = r0.mCurrentState
            r10 = r2
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r10 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r10
            boolean r10 = r10.dataConnected
            if (r10 == 0) goto L_0x00dc
            r10 = r2
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r10 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r10
            boolean r10 = r10.carrierNetworkChangeMode
            if (r10 != 0) goto L_0x00dc
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r2 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r2
            boolean r2 = r2.activityIn
            if (r2 == 0) goto L_0x00dc
            r2 = r7
            goto L_0x00dd
        L_0x00dc:
            r2 = r4
        L_0x00dd:
            T r10 = r0.mCurrentState
            r14 = r10
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r14 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r14
            boolean r14 = r14.dataConnected
            if (r14 == 0) goto L_0x00f5
            r14 = r10
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r14 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r14
            boolean r14 = r14.carrierNetworkChangeMode
            if (r14 != 0) goto L_0x00f5
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r10 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r10
            boolean r10 = r10.activityOut
            if (r10 == 0) goto L_0x00f5
            r10 = r7
            goto L_0x00f6
        L_0x00f5:
            r10 = r4
        L_0x00f6:
            T r14 = r0.mCurrentState
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r14 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r14
            boolean r14 = r14.isDefault
            if (r14 != 0) goto L_0x0102
            if (r3 == 0) goto L_0x0101
            goto L_0x0102
        L_0x0101:
            r7 = r4
        L_0x0102:
            r3 = r5 & r7
            if (r3 != 0) goto L_0x010f
            com.android.systemui.statusbar.policy.NetworkControllerImpl$Config r3 = r0.mConfig
            boolean r3 = r3.alwaysShowDataRatIcon
            if (r3 == 0) goto L_0x010d
            goto L_0x010f
        L_0x010d:
            r7 = r4
            goto L_0x0112
        L_0x010f:
            int r3 = r1.mDataType
            r7 = r3
        L_0x0112:
            boolean r14 = r1.mIsWide
            android.telephony.SubscriptionInfo r1 = r0.mSubscriptionInfo
            int r15 = r1.getSubscriptionId()
            T r0 = r0.mCurrentState
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r0 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r0
            boolean r0 = r0.roaming
            r4 = r18
            r5 = r6
            r6 = r9
            r9 = r2
            r16 = r0
            r4.setMobileDataIndicators(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.MobileSignalController.notifyListeners(com.android.systemui.statusbar.policy.NetworkController$SignalCallback):void");
    }

    /* access modifiers changed from: protected */
    public MobileState cleanState() {
        return new MobileState();
    }

    private boolean isCdma() {
        SignalStrength signalStrength = this.mSignalStrength;
        return signalStrength != null && !signalStrength.isGsm();
    }

    public boolean isEmergencyOnly() {
        ServiceState serviceState = this.mServiceState;
        return serviceState != null && serviceState.isEmergencyOnly();
    }

    private boolean isRoaming() {
        boolean z = false;
        if (isCarrierNetworkChangeActive()) {
            return false;
        }
        if (!isCdma() || this.mServiceState == null) {
            ServiceState serviceState = this.mServiceState;
            if (serviceState != null && serviceState.getRoaming()) {
                z = true;
            }
            return z;
        }
        int eriIconMode = this.mPhone.getCdmaEriInformation().getEriIconMode();
        if (this.mPhone.getCdmaEriInformation().getEriIconIndex() != 1 && (eriIconMode == 0 || eriIconMode == 1)) {
            z = true;
        }
        return z;
    }

    private boolean isCarrierNetworkChangeActive() {
        return ((MobileState) this.mCurrentState).carrierNetworkChangeMode;
    }

    public void handleBroadcast(Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.telephony.action.SERVICE_PROVIDERS_UPDATED")) {
            updateNetworkName(intent.getBooleanExtra("android.telephony.extra.SHOW_SPN", false), intent.getStringExtra("android.telephony.extra.SPN"), intent.getStringExtra("android.telephony.extra.DATA_SPN"), intent.getBooleanExtra("android.telephony.extra.SHOW_PLMN", false), intent.getStringExtra("android.telephony.extra.PLMN"));
            notifyListenersIfNecessary();
        } else if (action.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
            updateDataSim();
            notifyListenersIfNecessary();
        }
    }

    /* access modifiers changed from: private */
    public void updateDataSim() {
        int activeDataSubId = this.mDefaults.getActiveDataSubId();
        boolean z = true;
        if (SubscriptionManager.isValidSubscriptionId(activeDataSubId)) {
            MobileState mobileState = (MobileState) this.mCurrentState;
            if (activeDataSubId != this.mSubscriptionInfo.getSubscriptionId()) {
                z = false;
            }
            mobileState.dataSim = z;
            return;
        }
        ((MobileState) this.mCurrentState).dataSim = true;
    }

    /* access modifiers changed from: 0000 */
    public void updateNetworkName(boolean z, String str, String str2, boolean z2, String str3) {
        if (SignalController.CHATTY) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateNetworkName showSpn=");
            sb.append(z);
            sb.append(" spn=");
            sb.append(str);
            sb.append(" dataSpn=");
            sb.append(str2);
            sb.append(" showPlmn=");
            sb.append(z2);
            sb.append(" plmn=");
            sb.append(str3);
            Log.d("CarrierLabel", sb.toString());
        }
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        if (z2 && str3 != null) {
            sb2.append(str3);
            sb3.append(str3);
        }
        if (z && str != null) {
            if (sb2.length() != 0) {
                sb2.append(this.mNetworkNameSeparator);
            }
            sb2.append(str);
        }
        if (sb2.length() != 0) {
            ((MobileState) this.mCurrentState).networkName = sb2.toString();
        } else {
            ((MobileState) this.mCurrentState).networkName = this.mNetworkNameDefault;
        }
        if (z && str2 != null) {
            if (sb3.length() != 0) {
                sb3.append(this.mNetworkNameSeparator);
            }
            sb3.append(str2);
        }
        if (sb3.length() != 0) {
            ((MobileState) this.mCurrentState).networkNameData = sb3.toString();
            return;
        }
        ((MobileState) this.mCurrentState).networkNameData = this.mNetworkNameDefault;
    }

    private final int getCdmaLevel() {
        List cellSignalStrengths = this.mSignalStrength.getCellSignalStrengths(CellSignalStrengthCdma.class);
        if (!cellSignalStrengths.isEmpty()) {
            return ((CellSignalStrengthCdma) cellSignalStrengths.get(0)).getLevel();
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public final void updateTelephony() {
        if (SignalController.DEBUG) {
            String str = this.mTag;
            StringBuilder sb = new StringBuilder();
            sb.append("updateTelephonySignalStrength: hasService=");
            sb.append(Utils.isInService(this.mServiceState));
            sb.append(" ss=");
            sb.append(this.mSignalStrength);
            sb.append(" displayInfo=");
            sb.append(this.mTelephonyDisplayInfo);
            Log.d(str, sb.toString());
        }
        checkDefaultData();
        boolean z = true;
        ((MobileState) this.mCurrentState).connected = Utils.isInService(this.mServiceState) && this.mSignalStrength != null;
        if (((MobileState) this.mCurrentState).connected) {
            if (this.mSignalStrength.isGsm() || !this.mConfig.alwaysShowCdmaRssi) {
                ((MobileState) this.mCurrentState).level = this.mSignalStrength.getLevel();
            } else {
                ((MobileState) this.mCurrentState).level = getCdmaLevel();
            }
        }
        String iconKey = getIconKey();
        if (this.mNetworkToIconLookup.get(iconKey) != null) {
            ((MobileState) this.mCurrentState).iconGroup = (IconGroup) this.mNetworkToIconLookup.get(iconKey);
        } else {
            ((MobileState) this.mCurrentState).iconGroup = this.mDefaultIcons;
        }
        T t = this.mCurrentState;
        MobileState mobileState = (MobileState) t;
        if (!((MobileState) t).connected || this.mDataState != 2) {
            z = false;
        }
        mobileState.dataConnected = z;
        ((MobileState) this.mCurrentState).roaming = isRoaming();
        if (isCarrierNetworkChangeActive()) {
            ((MobileState) this.mCurrentState).iconGroup = TelephonyIcons.CARRIER_NETWORK_CHANGE;
        } else if (isDataDisabled() && !this.mConfig.alwaysShowDataRatIcon) {
            if (this.mSubscriptionInfo.getSubscriptionId() != this.mDefaults.getDefaultDataSubId()) {
                ((MobileState) this.mCurrentState).iconGroup = TelephonyIcons.NOT_DEFAULT_DATA;
            } else {
                ((MobileState) this.mCurrentState).iconGroup = TelephonyIcons.DATA_DISABLED;
            }
        }
        boolean isEmergencyOnly = isEmergencyOnly();
        T t2 = this.mCurrentState;
        if (isEmergencyOnly != ((MobileState) t2).isEmergency) {
            ((MobileState) t2).isEmergency = isEmergencyOnly();
            this.mNetworkController.recalculateEmergency();
        }
        if (((MobileState) this.mCurrentState).networkName.equals(this.mNetworkNameDefault)) {
            ServiceState serviceState = this.mServiceState;
            if (serviceState != null && !TextUtils.isEmpty(serviceState.getOperatorAlphaShort())) {
                ((MobileState) this.mCurrentState).networkName = this.mServiceState.getOperatorAlphaShort();
            }
        }
        if (((MobileState) this.mCurrentState).networkNameData.equals(this.mNetworkNameDefault)) {
            ServiceState serviceState2 = this.mServiceState;
            if (serviceState2 != null && ((MobileState) this.mCurrentState).dataSim && !TextUtils.isEmpty(serviceState2.getOperatorAlphaShort())) {
                ((MobileState) this.mCurrentState).networkNameData = this.mServiceState.getOperatorAlphaShort();
            }
        }
        notifyListenersIfNecessary();
    }

    private void checkDefaultData() {
        T t = this.mCurrentState;
        if (((MobileState) t).iconGroup != TelephonyIcons.NOT_DEFAULT_DATA) {
            ((MobileState) t).defaultDataOff = false;
            return;
        }
        ((MobileState) t).defaultDataOff = this.mNetworkController.isDataControllerDisabled();
    }

    /* access modifiers changed from: 0000 */
    public void onMobileDataChanged() {
        checkDefaultData();
        notifyListenersIfNecessary();
    }

    /* access modifiers changed from: 0000 */
    public boolean isDataDisabled() {
        return !this.mPhone.isDataConnectionAllowed();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setActivity(int i) {
        boolean z = false;
        ((MobileState) this.mCurrentState).activityIn = i == 3 || i == 1;
        MobileState mobileState = (MobileState) this.mCurrentState;
        if (i == 3 || i == 2) {
            z = true;
        }
        mobileState.activityOut = z;
        notifyListenersIfNecessary();
    }

    public void dump(PrintWriter printWriter) {
        super.dump(printWriter);
        StringBuilder sb = new StringBuilder();
        sb.append("  mSubscription=");
        sb.append(this.mSubscriptionInfo);
        String str = ",";
        sb.append(str);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mServiceState=");
        sb2.append(this.mServiceState);
        sb2.append(str);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mSignalStrength=");
        sb3.append(this.mSignalStrength);
        sb3.append(str);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("  mTelephonyDisplayInfo=");
        sb4.append(this.mTelephonyDisplayInfo);
        sb4.append(str);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("  mDataState=");
        sb5.append(this.mDataState);
        sb5.append(str);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("  mInflateSignalStrengths=");
        sb6.append(this.mInflateSignalStrengths);
        sb6.append(str);
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append("  isDataDisabled=");
        sb7.append(isDataDisabled());
        sb7.append(str);
        printWriter.println(sb7.toString());
    }
}
