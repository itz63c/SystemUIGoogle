package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings.Global;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.net.DataUsageController.Callback;
import com.android.settingslib.net.DataUsageController.NetworkNameProvider;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2017R$string;
import com.android.systemui.DemoMode;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener;
import com.android.systemui.statusbar.policy.NetworkController.AccessPointController;
import com.android.systemui.statusbar.policy.NetworkController.IconState;
import com.android.systemui.statusbar.policy.NetworkController.SignalCallback;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;

public class NetworkControllerImpl extends BroadcastReceiver implements NetworkController, DemoMode, NetworkNameProvider, Dumpable {
    static final boolean CHATTY = Log.isLoggable("NetworkControllerChat", 3);
    static final boolean DEBUG = Log.isLoggable("NetworkController", 3);
    private final AccessPointControllerImpl mAccessPoints;
    /* access modifiers changed from: private */
    public int mActiveMobileDataSubscription;
    private boolean mAirplaneMode;
    private final BroadcastDispatcher mBroadcastDispatcher;
    /* access modifiers changed from: private */
    public final CallbackHandler mCallbackHandler;
    /* access modifiers changed from: private */
    public final Runnable mClearForceValidated;
    private Config mConfig;
    private final BitSet mConnectedTransports;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private List<SubscriptionInfo> mCurrentSubscriptions;
    private int mCurrentUserId;
    private final DataSaverController mDataSaverController;
    private final DataUsageController mDataUsageController;
    private MobileSignalController mDefaultSignalController;
    private boolean mDemoInetCondition;
    private boolean mDemoMode;
    private WifiState mDemoWifiState;
    private int mEmergencySource;
    @VisibleForTesting
    final EthernetSignalController mEthernetSignalController;
    /* access modifiers changed from: private */
    public boolean mForceCellularValidated;
    private final boolean mHasMobileDataFeature;
    private boolean mHasNoSubs;
    private boolean mInetCondition;
    private boolean mIsEmergency;
    @VisibleForTesting
    ServiceState mLastServiceState;
    @VisibleForTesting
    boolean mListening;
    private Locale mLocale;
    private final Object mLock;
    @VisibleForTesting
    final SparseArray<MobileSignalController> mMobileSignalControllers;
    private final TelephonyManager mPhone;
    private PhoneStateListener mPhoneStateListener;
    /* access modifiers changed from: private */
    public final Handler mReceiverHandler;
    private final Runnable mRegisterListeners;
    private boolean mSimDetected;
    private final SubscriptionDefaults mSubDefaults;
    private OnSubscriptionsChangedListener mSubscriptionListener;
    private final SubscriptionManager mSubscriptionManager;
    private boolean mUserSetup;
    private final CurrentUserTracker mUserTracker;
    private final BitSet mValidatedTransports;
    /* access modifiers changed from: private */
    public final WifiManager mWifiManager;
    @VisibleForTesting
    final WifiSignalController mWifiSignalController;

    /* renamed from: com.android.systemui.statusbar.policy.NetworkControllerImpl$1 */
    class C16641 implements ConfigurationListener {
    }

    @VisibleForTesting
    static class Config {
        boolean alwaysShowCdmaRssi = false;
        boolean alwaysShowDataRatIcon = false;
        boolean hideLtePlus = false;
        boolean hspaDataDistinguishable;
        boolean show4gFor3g = false;
        boolean show4gForLte = false;
        boolean showAtLeast3G = false;

        Config() {
        }

        static Config readConfig(Context context) {
            Config config = new Config();
            Resources resources = context.getResources();
            config.showAtLeast3G = resources.getBoolean(C2007R$bool.config_showMin3G);
            config.alwaysShowCdmaRssi = resources.getBoolean(17891359);
            config.hspaDataDistinguishable = resources.getBoolean(C2007R$bool.config_hspa_data_distinguishable);
            resources.getBoolean(17891472);
            CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
            SubscriptionManager.from(context);
            PersistableBundle configForSubId = carrierConfigManager.getConfigForSubId(SubscriptionManager.getDefaultDataSubscriptionId());
            if (configForSubId != null) {
                config.alwaysShowDataRatIcon = configForSubId.getBoolean("always_show_data_rat_icon_bool");
                config.show4gForLte = configForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
                config.show4gFor3g = configForSubId.getBoolean("show_4g_for_3g_data_icon_bool");
                config.hideLtePlus = configForSubId.getBoolean("hide_lte_plus_data_icon_bool");
            }
            return config;
        }
    }

    private class SubListener extends OnSubscriptionsChangedListener {
        private SubListener() {
        }

        /* synthetic */ SubListener(NetworkControllerImpl networkControllerImpl, C16641 r2) {
            this();
        }

        public void onSubscriptionsChanged() {
            NetworkControllerImpl.this.updateMobileControllers();
        }
    }

    public static class SubscriptionDefaults {
        public int getDefaultVoiceSubId() {
            return SubscriptionManager.getDefaultVoiceSubscriptionId();
        }

        public int getDefaultDataSubId() {
            return SubscriptionManager.getDefaultDataSubscriptionId();
        }

        public int getActiveDataSubId() {
            return SubscriptionManager.getActiveDataSubscriptionId();
        }
    }

    public NetworkControllerImpl(Context context, Looper looper, DeviceProvisionedController deviceProvisionedController, BroadcastDispatcher broadcastDispatcher) {
        Context context2 = context;
        this(context2, (ConnectivityManager) context2.getSystemService("connectivity"), (TelephonyManager) context2.getSystemService("phone"), (WifiManager) context2.getSystemService("wifi"), SubscriptionManager.from(context), Config.readConfig(context), looper, new CallbackHandler(), new AccessPointControllerImpl(context2), new DataUsageController(context2), new SubscriptionDefaults(), deviceProvisionedController, broadcastDispatcher);
        this.mReceiverHandler.post(this.mRegisterListeners);
    }

    @VisibleForTesting
    NetworkControllerImpl(Context context, ConnectivityManager connectivityManager, TelephonyManager telephonyManager, WifiManager wifiManager, SubscriptionManager subscriptionManager, Config config, Looper looper, CallbackHandler callbackHandler, AccessPointControllerImpl accessPointControllerImpl, DataUsageController dataUsageController, SubscriptionDefaults subscriptionDefaults, final DeviceProvisionedController deviceProvisionedController, BroadcastDispatcher broadcastDispatcher) {
        this.mLock = new Object();
        this.mActiveMobileDataSubscription = -1;
        this.mMobileSignalControllers = new SparseArray<>();
        this.mConnectedTransports = new BitSet();
        this.mValidatedTransports = new BitSet();
        this.mAirplaneMode = false;
        this.mLocale = null;
        this.mCurrentSubscriptions = new ArrayList();
        this.mClearForceValidated = new Runnable() {
            public final void run() {
                NetworkControllerImpl.this.lambda$new$0$NetworkControllerImpl();
            }
        };
        this.mRegisterListeners = new Runnable() {
            public void run() {
                NetworkControllerImpl.this.registerListeners();
            }
        };
        this.mContext = context;
        this.mConfig = config;
        this.mReceiverHandler = new Handler(looper);
        this.mCallbackHandler = callbackHandler;
        this.mDataSaverController = new DataSaverControllerImpl(context);
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mSubscriptionManager = subscriptionManager;
        this.mSubDefaults = subscriptionDefaults;
        this.mConnectivityManager = connectivityManager;
        this.mHasMobileDataFeature = connectivityManager.isNetworkSupported(0);
        this.mPhone = telephonyManager;
        this.mWifiManager = wifiManager;
        this.mLocale = this.mContext.getResources().getConfiguration().locale;
        this.mAccessPoints = accessPointControllerImpl;
        this.mDataUsageController = dataUsageController;
        dataUsageController.setNetworkController(this);
        this.mDataUsageController.setCallback(new Callback() {
            public void onMobileDataEnabled(boolean z) {
                NetworkControllerImpl.this.mCallbackHandler.setMobileDataEnabled(z);
                NetworkControllerImpl.this.notifyControllersMobileDataChanged();
            }
        });
        WifiSignalController wifiSignalController = new WifiSignalController(this.mContext, this.mHasMobileDataFeature, this.mCallbackHandler, this, this.mWifiManager);
        this.mWifiSignalController = wifiSignalController;
        this.mEthernetSignalController = new EthernetSignalController(this.mContext, this.mCallbackHandler, this);
        updateAirplaneMode(true);
        C16663 r3 = new CurrentUserTracker(broadcastDispatcher) {
            public void onUserSwitched(int i) {
                NetworkControllerImpl.this.onUserSwitched(i);
            }
        };
        this.mUserTracker = r3;
        r3.startTracking();
        deviceProvisionedController.addCallback(new DeviceProvisionedListener() {
            public void onUserSetupChanged() {
                NetworkControllerImpl networkControllerImpl = NetworkControllerImpl.this;
                DeviceProvisionedController deviceProvisionedController = deviceProvisionedController;
                networkControllerImpl.setUserSetupComplete(deviceProvisionedController.isUserSetup(deviceProvisionedController.getCurrentUser()));
            }
        });
        this.mConnectivityManager.registerDefaultNetworkCallback(new NetworkCallback() {
            private Network mLastNetwork;
            private NetworkCapabilities mLastNetworkCapabilities;

            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                NetworkCapabilities networkCapabilities2 = this.mLastNetworkCapabilities;
                boolean z = networkCapabilities2 != null && networkCapabilities2.hasCapability(16);
                boolean hasCapability = networkCapabilities.hasCapability(16);
                if (!network.equals(this.mLastNetwork) || !networkCapabilities.equalsTransportTypes(this.mLastNetworkCapabilities) || hasCapability != z) {
                    this.mLastNetwork = network;
                    this.mLastNetworkCapabilities = networkCapabilities;
                    NetworkControllerImpl.this.updateConnectivity();
                }
            }
        }, this.mReceiverHandler);
        Handler handler = this.mReceiverHandler;
        Objects.requireNonNull(handler);
        this.mPhoneStateListener = new PhoneStateListener(new Executor(handler) {
            public final /* synthetic */ Handler f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(Runnable runnable) {
                this.f$0.post(runnable);
            }
        }) {
            public void onActiveDataSubscriptionIdChanged(int i) {
                NetworkControllerImpl networkControllerImpl = NetworkControllerImpl.this;
                if (networkControllerImpl.keepCellularValidationBitInSwitch(networkControllerImpl.mActiveMobileDataSubscription, i)) {
                    if (NetworkControllerImpl.DEBUG) {
                        Log.d("NetworkController", ": mForceCellularValidated to true.");
                    }
                    NetworkControllerImpl.this.mForceCellularValidated = true;
                    NetworkControllerImpl.this.mReceiverHandler.removeCallbacks(NetworkControllerImpl.this.mClearForceValidated);
                    NetworkControllerImpl.this.mReceiverHandler.postDelayed(NetworkControllerImpl.this.mClearForceValidated, 2000);
                }
                NetworkControllerImpl.this.mActiveMobileDataSubscription = i;
                NetworkControllerImpl.this.doUpdateMobileControllers();
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$NetworkControllerImpl() {
        if (DEBUG) {
            Log.d("NetworkController", ": mClearForceValidated");
        }
        this.mForceCellularValidated = false;
        updateConnectivity();
    }

    /* access modifiers changed from: 0000 */
    public boolean isInGroupDataSwitch(int i, int i2) {
        SubscriptionInfo activeSubscriptionInfo = this.mSubscriptionManager.getActiveSubscriptionInfo(i);
        SubscriptionInfo activeSubscriptionInfo2 = this.mSubscriptionManager.getActiveSubscriptionInfo(i2);
        return (activeSubscriptionInfo == null || activeSubscriptionInfo2 == null || activeSubscriptionInfo.getGroupUuid() == null || !activeSubscriptionInfo.getGroupUuid().equals(activeSubscriptionInfo2.getGroupUuid())) ? false : true;
    }

    /* access modifiers changed from: 0000 */
    public boolean keepCellularValidationBitInSwitch(int i, int i2) {
        if (!this.mValidatedTransports.get(0) || !isInGroupDataSwitch(i, i2)) {
            return false;
        }
        return true;
    }

    public DataSaverController getDataSaverController() {
        return this.mDataSaverController;
    }

    /* access modifiers changed from: private */
    public void registerListeners() {
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            ((MobileSignalController) this.mMobileSignalControllers.valueAt(i)).registerListener();
        }
        if (this.mSubscriptionListener == null) {
            this.mSubscriptionListener = new SubListener(this, null);
        }
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mSubscriptionListener);
        this.mPhone.listen(this.mPhoneStateListener, 4194304);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED");
        intentFilter.addAction("android.intent.action.SERVICE_STATE");
        intentFilter.addAction("android.telephony.action.SERVICE_PROVIDERS_UPDATED");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.conn.INET_CONDITION_ACTION");
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter, this.mReceiverHandler);
        this.mListening = true;
        updateMobileControllers();
    }

    private void unregisterListeners() {
        this.mListening = false;
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            ((MobileSignalController) this.mMobileSignalControllers.valueAt(i)).unregisterListener();
        }
        this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mSubscriptionListener);
        this.mContext.unregisterReceiver(this);
    }

    public AccessPointController getAccessPointController() {
        return this.mAccessPoints;
    }

    public DataUsageController getMobileDataController() {
        return this.mDataUsageController;
    }

    public boolean hasMobileDataFeature() {
        return this.mHasMobileDataFeature;
    }

    public boolean hasVoiceCallingFeature() {
        return this.mPhone.getPhoneType() != 0;
    }

    private MobileSignalController getDataController() {
        int activeDataSubId = this.mSubDefaults.getActiveDataSubId();
        String str = "NetworkController";
        if (!SubscriptionManager.isValidSubscriptionId(activeDataSubId)) {
            if (DEBUG) {
                Log.e(str, "No data sim selected");
            }
            return this.mDefaultSignalController;
        } else if (this.mMobileSignalControllers.indexOfKey(activeDataSubId) >= 0) {
            return (MobileSignalController) this.mMobileSignalControllers.get(activeDataSubId);
        } else {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Cannot find controller for data sub: ");
                sb.append(activeDataSubId);
                Log.e(str, sb.toString());
            }
            return this.mDefaultSignalController;
        }
    }

    public String getMobileDataNetworkName() {
        MobileSignalController dataController = getDataController();
        return dataController != null ? ((MobileState) dataController.getState()).networkNameData : "";
    }

    public int getNumberSubscriptions() {
        return this.mMobileSignalControllers.size();
    }

    /* access modifiers changed from: 0000 */
    public boolean isDataControllerDisabled() {
        MobileSignalController dataController = getDataController();
        if (dataController == null) {
            return false;
        }
        return dataController.isDataDisabled();
    }

    /* access modifiers changed from: private */
    public void notifyControllersMobileDataChanged() {
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            ((MobileSignalController) this.mMobileSignalControllers.valueAt(i)).onMobileDataChanged();
        }
    }

    public boolean isEmergencyOnly() {
        boolean z = true;
        if (this.mMobileSignalControllers.size() == 0) {
            this.mEmergencySource = 0;
            ServiceState serviceState = this.mLastServiceState;
            if (serviceState == null || !serviceState.isEmergencyOnly()) {
                z = false;
            }
            return z;
        }
        int defaultVoiceSubId = this.mSubDefaults.getDefaultVoiceSubId();
        String str = "NetworkController";
        if (!SubscriptionManager.isValidSubscriptionId(defaultVoiceSubId)) {
            for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
                MobileSignalController mobileSignalController = (MobileSignalController) this.mMobileSignalControllers.valueAt(i);
                if (!((MobileState) mobileSignalController.getState()).isEmergency) {
                    this.mEmergencySource = mobileSignalController.mSubscriptionInfo.getSubscriptionId() + 100;
                    if (DEBUG) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Found emergency ");
                        sb.append(mobileSignalController.mTag);
                        Log.d(str, sb.toString());
                    }
                    return false;
                }
            }
        }
        if (this.mMobileSignalControllers.indexOfKey(defaultVoiceSubId) >= 0) {
            this.mEmergencySource = defaultVoiceSubId + 200;
            if (DEBUG) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Getting emergency from ");
                sb2.append(defaultVoiceSubId);
                Log.d(str, sb2.toString());
            }
            return ((MobileState) ((MobileSignalController) this.mMobileSignalControllers.get(defaultVoiceSubId)).getState()).isEmergency;
        } else if (this.mMobileSignalControllers.size() == 1) {
            this.mEmergencySource = this.mMobileSignalControllers.keyAt(0) + 400;
            if (DEBUG) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Getting assumed emergency from ");
                sb3.append(this.mMobileSignalControllers.keyAt(0));
                Log.d(str, sb3.toString());
            }
            return ((MobileState) ((MobileSignalController) this.mMobileSignalControllers.valueAt(0)).getState()).isEmergency;
        } else {
            if (DEBUG) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Cannot find controller for voice sub: ");
                sb4.append(defaultVoiceSubId);
                Log.e(str, sb4.toString());
            }
            this.mEmergencySource = defaultVoiceSubId + 300;
            return true;
        }
    }

    /* access modifiers changed from: 0000 */
    public void recalculateEmergency() {
        boolean isEmergencyOnly = isEmergencyOnly();
        this.mIsEmergency = isEmergencyOnly;
        this.mCallbackHandler.setEmergencyCallsOnly(isEmergencyOnly);
    }

    public void addCallback(SignalCallback signalCallback) {
        signalCallback.setSubs(this.mCurrentSubscriptions);
        signalCallback.setIsAirplaneMode(new IconState(this.mAirplaneMode, TelephonyIcons.FLIGHT_MODE_ICON, C2017R$string.accessibility_airplane_mode, this.mContext));
        signalCallback.setNoSims(this.mHasNoSubs, this.mSimDetected);
        this.mWifiSignalController.notifyListeners(signalCallback);
        this.mEthernetSignalController.notifyListeners(signalCallback);
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            ((MobileSignalController) this.mMobileSignalControllers.valueAt(i)).notifyListeners(signalCallback);
        }
        this.mCallbackHandler.setListening(signalCallback, true);
    }

    public void removeCallback(SignalCallback signalCallback) {
        this.mCallbackHandler.setListening(signalCallback, false);
    }

    public void setWifiEnabled(final boolean z) {
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                NetworkControllerImpl.this.mWifiManager.setWifiEnabled(z);
                return null;
            }
        }.execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    public void onUserSwitched(int i) {
        this.mCurrentUserId = i;
        this.mAccessPoints.onUserSwitched(i);
        updateConnectivity();
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onReceive(android.content.Context r4, android.content.Intent r5) {
        /*
            r3 = this;
            boolean r4 = CHATTY
            if (r4 == 0) goto L_0x001a
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r0 = "onReceive: intent="
            r4.append(r0)
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            java.lang.String r0 = "NetworkController"
            android.util.Log.d(r0, r4)
        L_0x001a:
            java.lang.String r4 = r5.getAction()
            int r0 = r4.hashCode()
            r1 = -1
            r2 = 0
            switch(r0) {
                case -2104353374: goto L_0x006e;
                case -1465084191: goto L_0x0064;
                case -1172645946: goto L_0x005a;
                case -1138588223: goto L_0x0050;
                case -1076576821: goto L_0x0046;
                case -229777127: goto L_0x003c;
                case -25388475: goto L_0x0032;
                case 623179603: goto L_0x0028;
                default: goto L_0x0027;
            }
        L_0x0027:
            goto L_0x0078
        L_0x0028:
            java.lang.String r0 = "android.net.conn.INET_CONDITION_ACTION"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0078
            r4 = 1
            goto L_0x0079
        L_0x0032:
            java.lang.String r0 = "android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0078
            r4 = 4
            goto L_0x0079
        L_0x003c:
            java.lang.String r0 = "android.intent.action.SIM_STATE_CHANGED"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0078
            r4 = 5
            goto L_0x0079
        L_0x0046:
            java.lang.String r0 = "android.intent.action.AIRPLANE_MODE"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0078
            r4 = 2
            goto L_0x0079
        L_0x0050:
            java.lang.String r0 = "android.telephony.action.CARRIER_CONFIG_CHANGED"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0078
            r4 = 7
            goto L_0x0079
        L_0x005a:
            java.lang.String r0 = "android.net.conn.CONNECTIVITY_CHANGE"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0078
            r4 = r2
            goto L_0x0079
        L_0x0064:
            java.lang.String r0 = "android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0078
            r4 = 3
            goto L_0x0079
        L_0x006e:
            java.lang.String r0 = "android.intent.action.SERVICE_STATE"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0078
            r4 = 6
            goto L_0x0079
        L_0x0078:
            r4 = r1
        L_0x0079:
            switch(r4) {
                case 0: goto L_0x0107;
                case 1: goto L_0x0107;
                case 2: goto L_0x0100;
                case 3: goto L_0x00fc;
                case 4: goto L_0x00d3;
                case 5: goto L_0x00c6;
                case 6: goto L_0x00b0;
                case 7: goto L_0x009d;
                default: goto L_0x007c;
            }
        L_0x007c:
            java.lang.String r4 = "android.telephony.extra.SUBSCRIPTION_INDEX"
            int r4 = r5.getIntExtra(r4, r1)
            boolean r0 = android.telephony.SubscriptionManager.isValidSubscriptionId(r4)
            if (r0 == 0) goto L_0x010f
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r0 = r3.mMobileSignalControllers
            int r0 = r0.indexOfKey(r4)
            if (r0 < 0) goto L_0x010b
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r3 = r3.mMobileSignalControllers
            java.lang.Object r3 = r3.get(r4)
            com.android.systemui.statusbar.policy.MobileSignalController r3 = (com.android.systemui.statusbar.policy.MobileSignalController) r3
            r3.handleBroadcast(r5)
            goto L_0x0114
        L_0x009d:
            android.content.Context r4 = r3.mContext
            com.android.systemui.statusbar.policy.NetworkControllerImpl$Config r4 = com.android.systemui.statusbar.policy.NetworkControllerImpl.Config.readConfig(r4)
            r3.mConfig = r4
            android.os.Handler r4 = r3.mReceiverHandler
            com.android.systemui.statusbar.policy.-$$Lambda$ybM43k5QVX_SxWbQACu1XwL3Knk r5 = new com.android.systemui.statusbar.policy.-$$Lambda$ybM43k5QVX_SxWbQACu1XwL3Knk
            r5.<init>()
            r4.post(r5)
            goto L_0x0114
        L_0x00b0:
            android.os.Bundle r4 = r5.getExtras()
            android.telephony.ServiceState r4 = android.telephony.ServiceState.newFromBundle(r4)
            r3.mLastServiceState = r4
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r4 = r3.mMobileSignalControllers
            int r4 = r4.size()
            if (r4 != 0) goto L_0x0114
            r3.recalculateEmergency()
            goto L_0x0114
        L_0x00c6:
            java.lang.String r4 = "rebroadcastOnUnlock"
            boolean r4 = r5.getBooleanExtra(r4, r2)
            if (r4 == 0) goto L_0x00cf
            goto L_0x0114
        L_0x00cf:
            r3.updateMobileControllers()
            goto L_0x0114
        L_0x00d3:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r4 = r3.mMobileSignalControllers
            int r4 = r4.size()
            if (r2 >= r4) goto L_0x00e9
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r4 = r3.mMobileSignalControllers
            java.lang.Object r4 = r4.valueAt(r2)
            com.android.systemui.statusbar.policy.MobileSignalController r4 = (com.android.systemui.statusbar.policy.MobileSignalController) r4
            r4.handleBroadcast(r5)
            int r2 = r2 + 1
            goto L_0x00d3
        L_0x00e9:
            android.content.Context r4 = r3.mContext
            com.android.systemui.statusbar.policy.NetworkControllerImpl$Config r4 = com.android.systemui.statusbar.policy.NetworkControllerImpl.Config.readConfig(r4)
            r3.mConfig = r4
            android.os.Handler r4 = r3.mReceiverHandler
            com.android.systemui.statusbar.policy.-$$Lambda$ybM43k5QVX_SxWbQACu1XwL3Knk r5 = new com.android.systemui.statusbar.policy.-$$Lambda$ybM43k5QVX_SxWbQACu1XwL3Knk
            r5.<init>()
            r4.post(r5)
            goto L_0x0114
        L_0x00fc:
            r3.recalculateEmergency()
            goto L_0x0114
        L_0x0100:
            r3.refreshLocale()
            r3.updateAirplaneMode(r2)
            goto L_0x0114
        L_0x0107:
            r3.updateConnectivity()
            goto L_0x0114
        L_0x010b:
            r3.updateMobileControllers()
            goto L_0x0114
        L_0x010f:
            com.android.systemui.statusbar.policy.WifiSignalController r3 = r3.mWifiSignalController
            r3.handleBroadcast(r5)
        L_0x0114:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.NetworkControllerImpl.onReceive(android.content.Context, android.content.Intent):void");
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void handleConfigurationChanged() {
        updateMobileControllers();
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            ((MobileSignalController) this.mMobileSignalControllers.valueAt(i)).setConfiguration(this.mConfig);
        }
        refreshLocale();
    }

    /* access modifiers changed from: private */
    public void updateMobileControllers() {
        if (this.mListening) {
            doUpdateMobileControllers();
        }
    }

    private void filterMobileSubscriptionInSameGroup(List<SubscriptionInfo> list) {
        if (list.size() == 2) {
            SubscriptionInfo subscriptionInfo = (SubscriptionInfo) list.get(0);
            SubscriptionInfo subscriptionInfo2 = (SubscriptionInfo) list.get(1);
            if (subscriptionInfo.getGroupUuid() != null && subscriptionInfo.getGroupUuid().equals(subscriptionInfo2.getGroupUuid()) && (subscriptionInfo.isOpportunistic() || subscriptionInfo2.isOpportunistic())) {
                if (CarrierConfigManager.getDefaultConfig().getBoolean("always_show_primary_signal_bar_in_opportunistic_network_boolean")) {
                    if (!subscriptionInfo.isOpportunistic()) {
                        subscriptionInfo = subscriptionInfo2;
                    }
                    list.remove(subscriptionInfo);
                } else {
                    if (subscriptionInfo.getSubscriptionId() == this.mActiveMobileDataSubscription) {
                        subscriptionInfo = subscriptionInfo2;
                    }
                    list.remove(subscriptionInfo);
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void doUpdateMobileControllers() {
        List completeActiveSubscriptionInfoList = this.mSubscriptionManager.getCompleteActiveSubscriptionInfoList();
        if (completeActiveSubscriptionInfoList == null) {
            completeActiveSubscriptionInfoList = Collections.emptyList();
        }
        filterMobileSubscriptionInSameGroup(completeActiveSubscriptionInfoList);
        if (hasCorrectMobileControllers(completeActiveSubscriptionInfoList)) {
            updateNoSims();
            return;
        }
        synchronized (this.mLock) {
            setCurrentSubscriptionsLocked(completeActiveSubscriptionInfoList);
        }
        updateNoSims();
        recalculateEmergency();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void updateNoSims() {
        boolean z = this.mHasMobileDataFeature && this.mMobileSignalControllers.size() == 0;
        boolean hasAnySim = hasAnySim();
        if (z != this.mHasNoSubs || hasAnySim != this.mSimDetected) {
            this.mHasNoSubs = z;
            this.mSimDetected = hasAnySim;
            this.mCallbackHandler.setNoSims(z, hasAnySim);
        }
    }

    private boolean hasAnySim() {
        int activeModemCount = this.mPhone.getActiveModemCount();
        for (int i = 0; i < activeModemCount; i++) {
            int simState = this.mPhone.getSimState(i);
            if (simState != 1 && simState != 0) {
                return true;
            }
        }
        return false;
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void setCurrentSubscriptionsLocked(List<SubscriptionInfo> list) {
        int i;
        List<SubscriptionInfo> list2 = list;
        Collections.sort(list2, new Comparator<SubscriptionInfo>(this) {
            public int compare(SubscriptionInfo subscriptionInfo, SubscriptionInfo subscriptionInfo2) {
                int i;
                int i2;
                if (subscriptionInfo.getSimSlotIndex() == subscriptionInfo2.getSimSlotIndex()) {
                    i2 = subscriptionInfo.getSubscriptionId();
                    i = subscriptionInfo2.getSubscriptionId();
                } else {
                    i2 = subscriptionInfo.getSimSlotIndex();
                    i = subscriptionInfo2.getSimSlotIndex();
                }
                return i2 - i;
            }
        });
        this.mCurrentSubscriptions = list2;
        SparseArray sparseArray = new SparseArray();
        for (int i2 = 0; i2 < this.mMobileSignalControllers.size(); i2++) {
            sparseArray.put(this.mMobileSignalControllers.keyAt(i2), (MobileSignalController) this.mMobileSignalControllers.valueAt(i2));
        }
        this.mMobileSignalControllers.clear();
        int size = list.size();
        int i3 = 0;
        while (i3 < size) {
            int subscriptionId = ((SubscriptionInfo) list2.get(i3)).getSubscriptionId();
            if (sparseArray.indexOfKey(subscriptionId) >= 0) {
                this.mMobileSignalControllers.put(subscriptionId, (MobileSignalController) sparseArray.get(subscriptionId));
                sparseArray.remove(subscriptionId);
                i = size;
            } else {
                Context context = this.mContext;
                Config config = this.mConfig;
                boolean z = this.mHasMobileDataFeature;
                TelephonyManager createForSubscriptionId = this.mPhone.createForSubscriptionId(subscriptionId);
                CallbackHandler callbackHandler = this.mCallbackHandler;
                SubscriptionInfo subscriptionInfo = (SubscriptionInfo) list2.get(i3);
                MobileSignalController mobileSignalController = r0;
                SubscriptionDefaults subscriptionDefaults = this.mSubDefaults;
                i = size;
                int i4 = subscriptionId;
                MobileSignalController mobileSignalController2 = new MobileSignalController(context, config, z, createForSubscriptionId, callbackHandler, this, subscriptionInfo, subscriptionDefaults, this.mReceiverHandler.getLooper());
                mobileSignalController.setUserSetupComplete(this.mUserSetup);
                this.mMobileSignalControllers.put(i4, mobileSignalController);
                if (((SubscriptionInfo) list2.get(i3)).getSimSlotIndex() == 0) {
                    this.mDefaultSignalController = mobileSignalController;
                }
                if (this.mListening) {
                    mobileSignalController.registerListener();
                }
            }
            i3++;
            size = i;
        }
        if (this.mListening) {
            for (int i5 = 0; i5 < sparseArray.size(); i5++) {
                int keyAt = sparseArray.keyAt(i5);
                if (sparseArray.get(keyAt) == this.mDefaultSignalController) {
                    this.mDefaultSignalController = null;
                }
                ((MobileSignalController) sparseArray.get(keyAt)).unregisterListener();
            }
        }
        this.mCallbackHandler.setSubs(list2);
        notifyAllListeners();
        pushConnectivityToSignals();
        updateAirplaneMode(true);
    }

    /* access modifiers changed from: private */
    public void setUserSetupComplete(boolean z) {
        this.mReceiverHandler.post(new Runnable(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NetworkControllerImpl.this.lambda$setUserSetupComplete$1$NetworkControllerImpl(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: handleSetUserSetupComplete */
    public void lambda$setUserSetupComplete$1(boolean z) {
        this.mUserSetup = z;
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            ((MobileSignalController) this.mMobileSignalControllers.valueAt(i)).setUserSetupComplete(this.mUserSetup);
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public boolean hasCorrectMobileControllers(List<SubscriptionInfo> list) {
        if (list.size() != this.mMobileSignalControllers.size()) {
            return false;
        }
        for (SubscriptionInfo subscriptionId : list) {
            if (this.mMobileSignalControllers.indexOfKey(subscriptionId.getSubscriptionId()) < 0) {
                return false;
            }
        }
        return true;
    }

    private void updateAirplaneMode(boolean z) {
        boolean z2 = true;
        if (Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 1) {
            z2 = false;
        }
        if (z2 != this.mAirplaneMode || z) {
            this.mAirplaneMode = z2;
            for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
                ((MobileSignalController) this.mMobileSignalControllers.valueAt(i)).setAirplaneMode(this.mAirplaneMode);
            }
            notifyListeners();
        }
    }

    private void refreshLocale() {
        Locale locale = this.mContext.getResources().getConfiguration().locale;
        if (!locale.equals(this.mLocale)) {
            this.mLocale = locale;
            this.mWifiSignalController.refreshLocale();
            notifyAllListeners();
        }
    }

    private void notifyAllListeners() {
        notifyListeners();
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            ((MobileSignalController) this.mMobileSignalControllers.valueAt(i)).notifyListeners();
        }
        this.mWifiSignalController.notifyListeners();
        this.mEthernetSignalController.notifyListeners();
    }

    private void notifyListeners() {
        this.mCallbackHandler.setIsAirplaneMode(new IconState(this.mAirplaneMode, TelephonyIcons.FLIGHT_MODE_ICON, C2017R$string.accessibility_airplane_mode, this.mContext));
        this.mCallbackHandler.setNoSims(this.mHasNoSubs, this.mSimDetected);
    }

    /* access modifiers changed from: private */
    public void updateConnectivity() {
        NetworkCapabilities[] defaultNetworkCapabilitiesForUser;
        int[] transportTypes;
        this.mConnectedTransports.clear();
        this.mValidatedTransports.clear();
        for (NetworkCapabilities networkCapabilities : this.mConnectivityManager.getDefaultNetworkCapabilitiesForUser(this.mCurrentUserId)) {
            for (int i : networkCapabilities.getTransportTypes()) {
                this.mConnectedTransports.set(i);
                if (networkCapabilities.hasCapability(16)) {
                    this.mValidatedTransports.set(i);
                }
            }
        }
        if (this.mForceCellularValidated) {
            this.mValidatedTransports.set(0);
        }
        if (CHATTY) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateConnectivity: mConnectedTransports=");
            sb.append(this.mConnectedTransports);
            String str = "NetworkController";
            Log.d(str, sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("updateConnectivity: mValidatedTransports=");
            sb2.append(this.mValidatedTransports);
            Log.d(str, sb2.toString());
        }
        this.mInetCondition = !this.mValidatedTransports.isEmpty();
        pushConnectivityToSignals();
    }

    private void pushConnectivityToSignals() {
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            ((MobileSignalController) this.mMobileSignalControllers.valueAt(i)).updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
        }
        this.mWifiSignalController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
        this.mEthernetSignalController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NetworkController state:");
        printWriter.println("  - telephony ------");
        printWriter.print("  hasVoiceCallingFeature()=");
        printWriter.println(hasVoiceCallingFeature());
        printWriter.println("  - connectivity ------");
        printWriter.print("  mConnectedTransports=");
        printWriter.println(this.mConnectedTransports);
        printWriter.print("  mValidatedTransports=");
        printWriter.println(this.mValidatedTransports);
        printWriter.print("  mInetCondition=");
        printWriter.println(this.mInetCondition);
        printWriter.print("  mAirplaneMode=");
        printWriter.println(this.mAirplaneMode);
        printWriter.print("  mLocale=");
        printWriter.println(this.mLocale);
        printWriter.print("  mLastServiceState=");
        printWriter.println(this.mLastServiceState);
        printWriter.print("  mIsEmergency=");
        printWriter.println(this.mIsEmergency);
        printWriter.print("  mEmergencySource=");
        printWriter.println(emergencyToString(this.mEmergencySource));
        printWriter.println("  - config ------");
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            ((MobileSignalController) this.mMobileSignalControllers.valueAt(i)).dump(printWriter);
        }
        this.mWifiSignalController.dump(printWriter);
        this.mEthernetSignalController.dump(printWriter);
        this.mAccessPoints.dump(printWriter);
    }

    private static final String emergencyToString(int i) {
        String str = ")";
        if (i > 300) {
            StringBuilder sb = new StringBuilder();
            sb.append("ASSUMED_VOICE_CONTROLLER(");
            sb.append(i - 200);
            sb.append(str);
            return sb.toString();
        } else if (i > 300) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("NO_SUB(");
            sb2.append(i - 300);
            sb2.append(str);
            return sb2.toString();
        } else if (i > 200) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("VOICE_CONTROLLER(");
            sb3.append(i - 200);
            sb3.append(str);
            return sb3.toString();
        } else if (i <= 100) {
            return i == 0 ? "NO_CONTROLLERS" : "UNKNOWN_SOURCE";
        } else {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("FIRST_CONTROLLER(");
            sb4.append(i - 100);
            sb4.append(str);
            return sb4.toString();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:210:0x03f3  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0407  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x015f  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0177  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchDemoCommand(java.lang.String r19, android.os.Bundle r20) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            boolean r3 = r0.mDemoMode
            java.lang.String r4 = "NetworkController"
            r5 = 1
            if (r3 != 0) goto L_0x0037
            java.lang.String r3 = "enter"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0037
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x001e
            java.lang.String r1 = "Entering demo mode"
            android.util.Log.d(r4, r1)
        L_0x001e:
            r18.unregisterListeners()
            r0.mDemoMode = r5
            boolean r1 = r0.mInetCondition
            r0.mDemoInetCondition = r1
            com.android.systemui.statusbar.policy.WifiSignalController r1 = r0.mWifiSignalController
            com.android.systemui.statusbar.policy.SignalController$State r1 = r1.getState()
            com.android.systemui.statusbar.policy.WifiSignalController$WifiState r1 = (com.android.systemui.statusbar.policy.WifiSignalController.WifiState) r1
            r0.mDemoWifiState = r1
            java.lang.String r0 = "DemoMode"
            r1.ssid = r0
            goto L_0x0440
        L_0x0037:
            boolean r3 = r0.mDemoMode
            r6 = 0
            if (r3 == 0) goto L_0x0079
            java.lang.String r3 = "exit"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0079
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x004d
            java.lang.String r1 = "Exiting demo mode"
            android.util.Log.d(r4, r1)
        L_0x004d:
            r0.mDemoMode = r6
            r18.updateMobileControllers()
        L_0x0052:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r1 = r0.mMobileSignalControllers
            int r1 = r1.size()
            if (r6 >= r1) goto L_0x0068
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r1 = r0.mMobileSignalControllers
            java.lang.Object r1 = r1.valueAt(r6)
            com.android.systemui.statusbar.policy.MobileSignalController r1 = (com.android.systemui.statusbar.policy.MobileSignalController) r1
            r1.resetLastState()
            int r6 = r6 + 1
            goto L_0x0052
        L_0x0068:
            com.android.systemui.statusbar.policy.WifiSignalController r1 = r0.mWifiSignalController
            r1.resetLastState()
            android.os.Handler r1 = r0.mReceiverHandler
            java.lang.Runnable r2 = r0.mRegisterListeners
            r1.post(r2)
            r18.notifyAllListeners()
            goto L_0x0440
        L_0x0079:
            boolean r3 = r0.mDemoMode
            if (r3 == 0) goto L_0x0440
            java.lang.String r3 = "network"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x0440
            java.lang.String r1 = "airplane"
            java.lang.String r1 = r2.getString(r1)
            java.lang.String r3 = "show"
            if (r1 == 0) goto L_0x00a3
            boolean r1 = r1.equals(r3)
            com.android.systemui.statusbar.policy.CallbackHandler r4 = r0.mCallbackHandler
            com.android.systemui.statusbar.policy.NetworkController$IconState r7 = new com.android.systemui.statusbar.policy.NetworkController$IconState
            int r8 = com.android.systemui.statusbar.policy.TelephonyIcons.FLIGHT_MODE_ICON
            int r9 = com.android.systemui.C2017R$string.accessibility_airplane_mode
            android.content.Context r10 = r0.mContext
            r7.<init>(r1, r8, r9, r10)
            r4.setIsAirplaneMode(r7)
        L_0x00a3:
            java.lang.String r1 = "fully"
            java.lang.String r1 = r2.getString(r1)
            if (r1 == 0) goto L_0x00e6
            boolean r1 = java.lang.Boolean.parseBoolean(r1)
            r0.mDemoInetCondition = r1
            java.util.BitSet r1 = new java.util.BitSet
            r1.<init>()
            boolean r4 = r0.mDemoInetCondition
            if (r4 == 0) goto L_0x00c1
            com.android.systemui.statusbar.policy.WifiSignalController r4 = r0.mWifiSignalController
            int r4 = r4.mTransportType
            r1.set(r4)
        L_0x00c1:
            com.android.systemui.statusbar.policy.WifiSignalController r4 = r0.mWifiSignalController
            r4.updateConnectivity(r1, r1)
            r4 = r6
        L_0x00c7:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r7 = r0.mMobileSignalControllers
            int r7 = r7.size()
            if (r4 >= r7) goto L_0x00e6
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r7 = r0.mMobileSignalControllers
            java.lang.Object r7 = r7.valueAt(r4)
            com.android.systemui.statusbar.policy.MobileSignalController r7 = (com.android.systemui.statusbar.policy.MobileSignalController) r7
            boolean r8 = r0.mDemoInetCondition
            if (r8 == 0) goto L_0x00e0
            int r8 = r7.mTransportType
            r1.set(r8)
        L_0x00e0:
            r7.updateConnectivity(r1, r1)
            int r4 = r4 + 1
            goto L_0x00c7
        L_0x00e6:
            java.lang.String r1 = "wifi"
            java.lang.String r1 = r2.getString(r1)
            java.lang.String r7 = "inout"
            java.lang.String r8 = "out"
            java.lang.String r9 = "in"
            r11 = 110414(0x1af4e, float:1.54723E-40)
            r12 = 3365(0xd25, float:4.715E-42)
            java.lang.String r13 = "null"
            java.lang.String r14 = "activity"
            java.lang.String r15 = "level"
            r16 = -1
            if (r1 == 0) goto L_0x0199
            boolean r1 = r1.equals(r3)
            java.lang.String r6 = r2.getString(r15)
            if (r6 == 0) goto L_0x012f
            com.android.systemui.statusbar.policy.WifiSignalController$WifiState r4 = r0.mDemoWifiState
            boolean r17 = r6.equals(r13)
            if (r17 == 0) goto L_0x0116
            r6 = r16
            goto L_0x0122
        L_0x0116:
            int r6 = java.lang.Integer.parseInt(r6)
            int r17 = com.android.systemui.statusbar.policy.WifiIcons.WIFI_LEVEL_COUNT
            int r10 = r17 + -1
            int r6 = java.lang.Math.min(r6, r10)
        L_0x0122:
            r4.level = r6
            com.android.systemui.statusbar.policy.WifiSignalController$WifiState r4 = r0.mDemoWifiState
            int r6 = r4.level
            if (r6 < 0) goto L_0x012c
            r6 = r5
            goto L_0x012d
        L_0x012c:
            r6 = 0
        L_0x012d:
            r4.connected = r6
        L_0x012f:
            java.lang.String r4 = r2.getString(r14)
            if (r4 == 0) goto L_0x017e
            int r6 = r4.hashCode()
            if (r6 == r12) goto L_0x0153
            if (r6 == r11) goto L_0x014b
            r10 = 100357129(0x5fb5409, float:2.3634796E-35)
            if (r6 == r10) goto L_0x0143
            goto L_0x015b
        L_0x0143:
            boolean r4 = r4.equals(r7)
            if (r4 == 0) goto L_0x015b
            r4 = 0
            goto L_0x015d
        L_0x014b:
            boolean r4 = r4.equals(r8)
            if (r4 == 0) goto L_0x015b
            r4 = 2
            goto L_0x015d
        L_0x0153:
            boolean r4 = r4.equals(r9)
            if (r4 == 0) goto L_0x015b
            r4 = r5
            goto L_0x015d
        L_0x015b:
            r4 = r16
        L_0x015d:
            if (r4 == 0) goto L_0x0177
            if (r4 == r5) goto L_0x0171
            r6 = 2
            if (r4 == r6) goto L_0x016b
            com.android.systemui.statusbar.policy.WifiSignalController r4 = r0.mWifiSignalController
            r10 = 0
            r4.setActivity(r10)
            goto L_0x0184
        L_0x016b:
            com.android.systemui.statusbar.policy.WifiSignalController r4 = r0.mWifiSignalController
            r4.setActivity(r6)
            goto L_0x0184
        L_0x0171:
            com.android.systemui.statusbar.policy.WifiSignalController r4 = r0.mWifiSignalController
            r4.setActivity(r5)
            goto L_0x0184
        L_0x0177:
            com.android.systemui.statusbar.policy.WifiSignalController r4 = r0.mWifiSignalController
            r6 = 3
            r4.setActivity(r6)
            goto L_0x0184
        L_0x017e:
            com.android.systemui.statusbar.policy.WifiSignalController r4 = r0.mWifiSignalController
            r6 = 0
            r4.setActivity(r6)
        L_0x0184:
            java.lang.String r4 = "ssid"
            java.lang.String r4 = r2.getString(r4)
            if (r4 == 0) goto L_0x0190
            com.android.systemui.statusbar.policy.WifiSignalController$WifiState r6 = r0.mDemoWifiState
            r6.ssid = r4
        L_0x0190:
            com.android.systemui.statusbar.policy.WifiSignalController$WifiState r4 = r0.mDemoWifiState
            r4.enabled = r1
            com.android.systemui.statusbar.policy.WifiSignalController r1 = r0.mWifiSignalController
            r1.notifyListeners()
        L_0x0199:
            java.lang.String r1 = "sims"
            java.lang.String r1 = r2.getString(r1)
            r4 = 8
            if (r1 == 0) goto L_0x01f4
            int r1 = java.lang.Integer.parseInt(r1)
            int r1 = android.util.MathUtils.constrain(r1, r5, r4)
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r10 = r0.mMobileSignalControllers
            int r10 = r10.size()
            if (r1 == r10) goto L_0x01f4
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r10 = r0.mMobileSignalControllers
            r10.clear()
            android.telephony.SubscriptionManager r10 = r0.mSubscriptionManager
            int r10 = r10.getActiveSubscriptionInfoCountMax()
            r11 = r10
        L_0x01c4:
            int r12 = r10 + r1
            if (r11 >= r12) goto L_0x01d2
            android.telephony.SubscriptionInfo r12 = r0.addSignalController(r11, r11)
            r6.add(r12)
            int r11 = r11 + 1
            goto L_0x01c4
        L_0x01d2:
            com.android.systemui.statusbar.policy.CallbackHandler r1 = r0.mCallbackHandler
            r1.setSubs(r6)
            r1 = 0
        L_0x01d8:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r6 = r0.mMobileSignalControllers
            int r6 = r6.size()
            if (r1 >= r6) goto L_0x01f4
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r6 = r0.mMobileSignalControllers
            int r6 = r6.keyAt(r1)
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r10 = r0.mMobileSignalControllers
            java.lang.Object r6 = r10.get(r6)
            com.android.systemui.statusbar.policy.MobileSignalController r6 = (com.android.systemui.statusbar.policy.MobileSignalController) r6
            r6.notifyListeners()
            int r1 = r1 + 1
            goto L_0x01d8
        L_0x01f4:
            java.lang.String r1 = "nosim"
            java.lang.String r1 = r2.getString(r1)
            if (r1 == 0) goto L_0x0209
            boolean r1 = r1.equals(r3)
            r0.mHasNoSubs = r1
            com.android.systemui.statusbar.policy.CallbackHandler r6 = r0.mCallbackHandler
            boolean r10 = r0.mSimDetected
            r6.setNoSims(r1, r10)
        L_0x0209:
            java.lang.String r1 = "mobile"
            java.lang.String r1 = r2.getString(r1)
            if (r1 == 0) goto L_0x041d
            boolean r1 = r1.equals(r3)
            java.lang.String r6 = "datatype"
            java.lang.String r6 = r2.getString(r6)
            java.lang.String r10 = "slot"
            java.lang.String r10 = r2.getString(r10)
            boolean r11 = android.text.TextUtils.isEmpty(r10)
            if (r11 == 0) goto L_0x0229
            r10 = 0
            goto L_0x022d
        L_0x0229:
            int r10 = java.lang.Integer.parseInt(r10)
        L_0x022d:
            r11 = 0
            int r4 = android.util.MathUtils.constrain(r10, r11, r4)
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
        L_0x0237:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r11 = r0.mMobileSignalControllers
            int r11 = r11.size()
            if (r11 > r4) goto L_0x024d
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r11 = r0.mMobileSignalControllers
            int r11 = r11.size()
            android.telephony.SubscriptionInfo r11 = r0.addSignalController(r11, r11)
            r10.add(r11)
            goto L_0x0237
        L_0x024d:
            boolean r11 = r10.isEmpty()
            if (r11 != 0) goto L_0x0258
            com.android.systemui.statusbar.policy.CallbackHandler r11 = r0.mCallbackHandler
            r11.setSubs(r10)
        L_0x0258:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r10 = r0.mMobileSignalControllers
            java.lang.Object r4 = r10.valueAt(r4)
            com.android.systemui.statusbar.policy.MobileSignalController r4 = (com.android.systemui.statusbar.policy.MobileSignalController) r4
            com.android.systemui.statusbar.policy.SignalController$State r10 = r4.getState()
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r10 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r10
            if (r6 == 0) goto L_0x026a
            r11 = r5
            goto L_0x026b
        L_0x026a:
            r11 = 0
        L_0x026b:
            r10.dataSim = r11
            com.android.systemui.statusbar.policy.SignalController$State r10 = r4.getState()
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r10 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r10
            if (r6 == 0) goto L_0x0277
            r11 = r5
            goto L_0x0278
        L_0x0277:
            r11 = 0
        L_0x0278:
            r10.isDefault = r11
            com.android.systemui.statusbar.policy.SignalController$State r10 = r4.getState()
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r10 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r10
            if (r6 == 0) goto L_0x0284
            r11 = r5
            goto L_0x0285
        L_0x0284:
            r11 = 0
        L_0x0285:
            r10.dataConnected = r11
            if (r6 == 0) goto L_0x033e
            com.android.systemui.statusbar.policy.SignalController$State r10 = r4.getState()
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r10 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r10
            java.lang.String r11 = "1x"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x029b
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.ONE_X
            goto L_0x033c
        L_0x029b:
            java.lang.String r11 = "3g"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x02a7
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.THREE_G
            goto L_0x033c
        L_0x02a7:
            java.lang.String r11 = "4g"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x02b3
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.FOUR_G
            goto L_0x033c
        L_0x02b3:
            java.lang.String r11 = "4g+"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x02bf
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.FOUR_G_PLUS
            goto L_0x033c
        L_0x02bf:
            java.lang.String r11 = "5g"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x02cb
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.NR_5G
            goto L_0x033c
        L_0x02cb:
            java.lang.String r11 = "5ge"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x02d7
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.LTE_CA_5G_E
            goto L_0x033c
        L_0x02d7:
            java.lang.String r11 = "5g+"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x02e2
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.NR_5G_PLUS
            goto L_0x033c
        L_0x02e2:
            java.lang.String r11 = "e"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x02ed
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.f80E
            goto L_0x033c
        L_0x02ed:
            java.lang.String r11 = "g"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x02f8
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.f81G
            goto L_0x033c
        L_0x02f8:
            java.lang.String r11 = "h"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x0303
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.f82H
            goto L_0x033c
        L_0x0303:
            java.lang.String r11 = "h+"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x030e
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.H_PLUS
            goto L_0x033c
        L_0x030e:
            java.lang.String r11 = "lte"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x0319
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.LTE
            goto L_0x033c
        L_0x0319:
            java.lang.String r11 = "lte+"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x0324
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.LTE_PLUS
            goto L_0x033c
        L_0x0324:
            java.lang.String r11 = "dis"
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x032f
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.DATA_DISABLED
            goto L_0x033c
        L_0x032f:
            java.lang.String r11 = "not"
            boolean r6 = r6.equals(r11)
            if (r6 == 0) goto L_0x033a
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.NOT_DEFAULT_DATA
            goto L_0x033c
        L_0x033a:
            com.android.systemui.statusbar.policy.MobileSignalController$MobileIconGroup r6 = com.android.systemui.statusbar.policy.TelephonyIcons.UNKNOWN
        L_0x033c:
            r10.iconGroup = r6
        L_0x033e:
            java.lang.String r6 = "roam"
            boolean r10 = r2.containsKey(r6)
            if (r10 == 0) goto L_0x0356
            com.android.systemui.statusbar.policy.SignalController$State r10 = r4.getState()
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r10 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r10
            java.lang.String r6 = r2.getString(r6)
            boolean r6 = r3.equals(r6)
            r10.roaming = r6
        L_0x0356:
            java.lang.String r6 = r2.getString(r15)
            if (r6 == 0) goto L_0x038e
            com.android.systemui.statusbar.policy.SignalController$State r10 = r4.getState()
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r10 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r10
            boolean r11 = r6.equals(r13)
            if (r11 == 0) goto L_0x036b
            r6 = r16
            goto L_0x0377
        L_0x036b:
            int r6 = java.lang.Integer.parseInt(r6)
            int r11 = android.telephony.CellSignalStrength.getNumSignalStrengthLevels()
            int r6 = java.lang.Math.min(r6, r11)
        L_0x0377:
            r10.level = r6
            com.android.systemui.statusbar.policy.SignalController$State r6 = r4.getState()
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r6 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r6
            com.android.systemui.statusbar.policy.SignalController$State r10 = r4.getState()
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r10 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r10
            int r10 = r10.level
            if (r10 < 0) goto L_0x038b
            r10 = r5
            goto L_0x038c
        L_0x038b:
            r10 = 0
        L_0x038c:
            r6.connected = r10
        L_0x038e:
            java.lang.String r6 = "inflate"
            boolean r10 = r2.containsKey(r6)
            if (r10 == 0) goto L_0x03b6
            r10 = 0
        L_0x0397:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r11 = r0.mMobileSignalControllers
            int r11 = r11.size()
            if (r10 >= r11) goto L_0x03b6
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r11 = r0.mMobileSignalControllers
            java.lang.Object r11 = r11.valueAt(r10)
            com.android.systemui.statusbar.policy.MobileSignalController r11 = (com.android.systemui.statusbar.policy.MobileSignalController) r11
            java.lang.String r12 = r2.getString(r6)
            java.lang.String r13 = "true"
            boolean r12 = r13.equals(r12)
            r11.mInflateSignalStrengths = r12
            int r10 = r10 + 1
            goto L_0x0397
        L_0x03b6:
            java.lang.String r6 = r2.getString(r14)
            if (r6 == 0) goto L_0x040d
            com.android.systemui.statusbar.policy.SignalController$State r10 = r4.getState()
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r10 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r10
            r10.dataConnected = r5
            int r10 = r6.hashCode()
            r11 = 3365(0xd25, float:4.715E-42)
            if (r10 == r11) goto L_0x03e7
            r11 = 110414(0x1af4e, float:1.54723E-40)
            if (r10 == r11) goto L_0x03df
            r9 = 100357129(0x5fb5409, float:2.3634796E-35)
            if (r10 == r9) goto L_0x03d7
            goto L_0x03ef
        L_0x03d7:
            boolean r6 = r6.equals(r7)
            if (r6 == 0) goto L_0x03ef
            r6 = 0
            goto L_0x03f1
        L_0x03df:
            boolean r6 = r6.equals(r8)
            if (r6 == 0) goto L_0x03ef
            r6 = 2
            goto L_0x03f1
        L_0x03e7:
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x03ef
            r6 = r5
            goto L_0x03f1
        L_0x03ef:
            r6 = r16
        L_0x03f1:
            if (r6 == 0) goto L_0x0407
            if (r6 == r5) goto L_0x0402
            r7 = 2
            if (r6 == r7) goto L_0x03fd
            r6 = 0
            r4.setActivity(r6)
            goto L_0x0411
        L_0x03fd:
            r6 = 0
            r4.setActivity(r7)
            goto L_0x0411
        L_0x0402:
            r6 = 0
            r4.setActivity(r5)
            goto L_0x0411
        L_0x0407:
            r5 = 3
            r6 = 0
            r4.setActivity(r5)
            goto L_0x0411
        L_0x040d:
            r6 = 0
            r4.setActivity(r6)
        L_0x0411:
            com.android.systemui.statusbar.policy.SignalController$State r5 = r4.getState()
            com.android.systemui.statusbar.policy.MobileSignalController$MobileState r5 = (com.android.systemui.statusbar.policy.MobileSignalController.MobileState) r5
            r5.enabled = r1
            r4.notifyListeners()
            goto L_0x041e
        L_0x041d:
            r6 = 0
        L_0x041e:
            java.lang.String r1 = "carriernetworkchange"
            java.lang.String r1 = r2.getString(r1)
            if (r1 == 0) goto L_0x0440
            boolean r1 = r1.equals(r3)
        L_0x042a:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r2 = r0.mMobileSignalControllers
            int r2 = r2.size()
            if (r6 >= r2) goto L_0x0440
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r2 = r0.mMobileSignalControllers
            java.lang.Object r2 = r2.valueAt(r6)
            com.android.systemui.statusbar.policy.MobileSignalController r2 = (com.android.systemui.statusbar.policy.MobileSignalController) r2
            r2.setCarrierNetworkChangeMode(r1)
            int r6 = r6 + 1
            goto L_0x042a
        L_0x0440:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.NetworkControllerImpl.dispatchDemoCommand(java.lang.String, android.os.Bundle):void");
    }

    private SubscriptionInfo addSignalController(int i, int i2) {
        SubscriptionInfo subscriptionInfo = new SubscriptionInfo(i, "", i2, "", "", 0, 0, "", 0, null, null, null, "", false, null, null);
        MobileSignalController mobileSignalController = new MobileSignalController(this.mContext, this.mConfig, this.mHasMobileDataFeature, this.mPhone.createForSubscriptionId(subscriptionInfo.getSubscriptionId()), this.mCallbackHandler, this, subscriptionInfo, this.mSubDefaults, this.mReceiverHandler.getLooper());
        this.mMobileSignalControllers.put(i, mobileSignalController);
        ((MobileState) mobileSignalController.getState()).userSetup = true;
        return subscriptionInfo;
    }

    public boolean hasEmergencyCryptKeeperText() {
        return EncryptionHelper.IS_DATA_ENCRYPTED;
    }

    public boolean isRadioOn() {
        return !this.mAirplaneMode;
    }
}
