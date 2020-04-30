package com.android.keyguard;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.keyguard.CarrierTextController.CarrierTextCallback;
import com.android.keyguard.CarrierTextController.CarrierTextCallbackInfo;
import com.android.settingslib.WirelessUtils;
import com.android.systemui.C2017R$string;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle.Observer;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class CarrierTextController {
    /* access modifiers changed from: private */
    public int mActiveMobileDataSubscription;
    protected final KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback() {
        public void onRefreshCarrierInfo() {
            CarrierTextController.this.updateCarrierText();
        }

        public void onTelephonyCapable(boolean z) {
            CarrierTextController.this.mTelephonyCapable = z;
            CarrierTextController.this.updateCarrierText();
        }

        public void onSimStateChanged(int i, int i2, int i3) {
            if (i2 < 0 || i2 >= CarrierTextController.this.mSimSlotsNumber) {
                StringBuilder sb = new StringBuilder();
                sb.append("onSimStateChanged() - slotId invalid: ");
                sb.append(i2);
                sb.append(" mTelephonyCapable: ");
                sb.append(Boolean.toString(CarrierTextController.this.mTelephonyCapable));
                Log.d("CarrierTextController", sb.toString());
                return;
            }
            if (CarrierTextController.this.getStatusForIccState(i3) == StatusMode.SimIoError) {
                CarrierTextController.this.mSimErrorState[i2] = true;
                CarrierTextController.this.updateCarrierText();
            } else if (CarrierTextController.this.mSimErrorState[i2]) {
                CarrierTextController.this.mSimErrorState[i2] = false;
                CarrierTextController.this.updateCarrierText();
            }
        }
    };
    /* access modifiers changed from: private */
    public CarrierTextCallback mCarrierTextCallback;
    private Context mContext;
    private final boolean mIsEmergencyCallCapable;
    protected KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final Handler mMainHandler;
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onActiveDataSubscriptionIdChanged(int i) {
            CarrierTextController.this.mActiveMobileDataSubscription = i;
            CarrierTextController carrierTextController = CarrierTextController.this;
            if (carrierTextController.mKeyguardUpdateMonitor != null) {
                carrierTextController.updateCarrierText();
            }
        }
    };
    private CharSequence mSeparator;
    private boolean mShowAirplaneMode;
    private boolean mShowMissingSim;
    /* access modifiers changed from: private */
    public boolean[] mSimErrorState;
    /* access modifiers changed from: private */
    public final int mSimSlotsNumber;
    /* access modifiers changed from: private */
    public boolean mTelephonyCapable;
    private WakefulnessLifecycle mWakefulnessLifecycle;
    private final Observer mWakefulnessObserver = new Observer() {
        public void onFinishedWakingUp() {
            if (CarrierTextController.this.mCarrierTextCallback != null) {
                CarrierTextController.this.mCarrierTextCallback.finishedWakingUp();
            }
        }

        public void onStartedGoingToSleep() {
            if (CarrierTextController.this.mCarrierTextCallback != null) {
                CarrierTextController.this.mCarrierTextCallback.startedGoingToSleep();
            }
        }
    };
    private WifiManager mWifiManager;

    /* renamed from: com.android.keyguard.CarrierTextController$4 */
    static /* synthetic */ class C05204 {
        static final /* synthetic */ int[] $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode;

        /* JADX WARNING: Can't wrap try/catch for region: R(20:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|(3:19|20|22)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(22:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|22) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x006c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.keyguard.CarrierTextController$StatusMode[] r0 = com.android.keyguard.CarrierTextController.StatusMode.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode = r0
                com.android.keyguard.CarrierTextController$StatusMode r1 = com.android.keyguard.CarrierTextController.StatusMode.Normal     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.keyguard.CarrierTextController$StatusMode r1 = com.android.keyguard.CarrierTextController.StatusMode.SimNotReady     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.keyguard.CarrierTextController$StatusMode r1 = com.android.keyguard.CarrierTextController.StatusMode.NetworkLocked     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.keyguard.CarrierTextController$StatusMode r1 = com.android.keyguard.CarrierTextController.StatusMode.SimMissing     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.keyguard.CarrierTextController$StatusMode r1 = com.android.keyguard.CarrierTextController.StatusMode.SimPermDisabled     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.keyguard.CarrierTextController$StatusMode r1 = com.android.keyguard.CarrierTextController.StatusMode.SimMissingLocked     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.keyguard.CarrierTextController$StatusMode r1 = com.android.keyguard.CarrierTextController.StatusMode.SimLocked     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.android.keyguard.CarrierTextController$StatusMode r1 = com.android.keyguard.CarrierTextController.StatusMode.SimPukLocked     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode     // Catch:{ NoSuchFieldError -> 0x006c }
                com.android.keyguard.CarrierTextController$StatusMode r1 = com.android.keyguard.CarrierTextController.StatusMode.SimIoError     // Catch:{ NoSuchFieldError -> 0x006c }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x006c }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x006c }
            L_0x006c:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode     // Catch:{ NoSuchFieldError -> 0x0078 }
                com.android.keyguard.CarrierTextController$StatusMode r1 = com.android.keyguard.CarrierTextController.StatusMode.SimUnknown     // Catch:{ NoSuchFieldError -> 0x0078 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0078 }
                r2 = 10
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0078 }
            L_0x0078:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.CarrierTextController.C05204.<clinit>():void");
        }
    }

    public static class Builder {
        private final Context mContext;
        private final String mSeparator;
        private boolean mShowAirplaneMode;
        private boolean mShowMissingSim;

        public Builder(Context context, Resources resources) {
            this.mContext = context;
            this.mSeparator = resources.getString(17040302);
        }

        public Builder setShowAirplaneMode(boolean z) {
            this.mShowAirplaneMode = z;
            return this;
        }

        public Builder setShowMissingSim(boolean z) {
            this.mShowMissingSim = z;
            return this;
        }

        public CarrierTextController build() {
            return new CarrierTextController(this.mContext, this.mSeparator, this.mShowAirplaneMode, this.mShowMissingSim);
        }
    }

    public interface CarrierTextCallback {
        void finishedWakingUp() {
        }

        void startedGoingToSleep() {
        }

        void updateCarrierInfo(CarrierTextCallbackInfo carrierTextCallbackInfo) {
        }
    }

    public static final class CarrierTextCallbackInfo {
        public boolean airplaneMode;
        public final boolean anySimReady;
        public final CharSequence carrierText;
        public final CharSequence[] listOfCarriers;
        public final int[] subscriptionIds;

        public CarrierTextCallbackInfo(CharSequence charSequence, CharSequence[] charSequenceArr, boolean z, int[] iArr) {
            this(charSequence, charSequenceArr, z, iArr, false);
        }

        public CarrierTextCallbackInfo(CharSequence charSequence, CharSequence[] charSequenceArr, boolean z, int[] iArr, boolean z2) {
            this.carrierText = charSequence;
            this.listOfCarriers = charSequenceArr;
            this.anySimReady = z;
            this.subscriptionIds = iArr;
            this.airplaneMode = z2;
        }
    }

    private enum StatusMode {
        Normal,
        NetworkLocked,
        SimMissing,
        SimMissingLocked,
        SimPukLocked,
        SimLocked,
        SimPermDisabled,
        SimNotReady,
        SimIoError,
        SimUnknown
    }

    public CarrierTextController(Context context, CharSequence charSequence, boolean z, boolean z2) {
        this.mContext = context;
        this.mIsEmergencyCallCapable = getTelephonyManager().isVoiceCapable();
        this.mShowAirplaneMode = z;
        this.mShowMissingSim = z2;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mSeparator = charSequence;
        this.mWakefulnessLifecycle = (WakefulnessLifecycle) Dependency.get(WakefulnessLifecycle.class);
        int supportedModemCount = getTelephonyManager().getSupportedModemCount();
        this.mSimSlotsNumber = supportedModemCount;
        this.mSimErrorState = new boolean[supportedModemCount];
        this.mMainHandler = (Handler) Dependency.get(Dependency.MAIN_HANDLER);
    }

    private TelephonyManager getTelephonyManager() {
        return (TelephonyManager) this.mContext.getSystemService("phone");
    }

    private CharSequence updateCarrierTextWithSimIoError(CharSequence charSequence, CharSequence[] charSequenceArr, int[] iArr, boolean z) {
        CharSequence carrierTextForSimState = getCarrierTextForSimState(8, "");
        for (int i = 0; i < getTelephonyManager().getActiveModemCount(); i++) {
            if (this.mSimErrorState[i]) {
                if (z) {
                    return concatenate(carrierTextForSimState, getContext().getText(17039980), this.mSeparator);
                }
                if (iArr[i] != -1) {
                    int i2 = iArr[i];
                    charSequenceArr[i2] = concatenate(carrierTextForSimState, charSequenceArr[i2], this.mSeparator);
                } else {
                    charSequence = concatenate(charSequence, carrierTextForSimState, this.mSeparator);
                }
            }
        }
        return charSequence;
    }

    public void setListening(CarrierTextCallback carrierTextCallback) {
        TelephonyManager telephonyManager = getTelephonyManager();
        if (carrierTextCallback != null) {
            this.mCarrierTextCallback = carrierTextCallback;
            if (((Boolean) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier() {
                public final Object get() {
                    return CarrierTextController.this.lambda$setListening$0$CarrierTextController();
                }
            })).booleanValue()) {
                this.mKeyguardUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
                this.mMainHandler.post(new Runnable() {
                    public final void run() {
                        CarrierTextController.this.lambda$setListening$1$CarrierTextController();
                    }
                });
                this.mWakefulnessLifecycle.addObserver(this.mWakefulnessObserver);
                telephonyManager.listen(this.mPhoneStateListener, 4194304);
                return;
            }
            this.mKeyguardUpdateMonitor = null;
            carrierTextCallback.updateCarrierInfo(new CarrierTextCallbackInfo("", null, false, null));
            return;
        }
        this.mCarrierTextCallback = null;
        if (this.mKeyguardUpdateMonitor != null) {
            this.mMainHandler.post(new Runnable() {
                public final void run() {
                    CarrierTextController.this.lambda$setListening$2$CarrierTextController();
                }
            });
            this.mWakefulnessLifecycle.removeObserver(this.mWakefulnessObserver);
        }
        telephonyManager.listen(this.mPhoneStateListener, 0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setListening$0 */
    public /* synthetic */ Boolean lambda$setListening$0$CarrierTextController() {
        return Boolean.valueOf(ConnectivityManager.from(this.mContext).isNetworkSupported(0));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setListening$1 */
    public /* synthetic */ void lambda$setListening$1$CarrierTextController() {
        KeyguardUpdateMonitor keyguardUpdateMonitor = this.mKeyguardUpdateMonitor;
        if (keyguardUpdateMonitor != null) {
            keyguardUpdateMonitor.registerCallback(this.mCallback);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setListening$2 */
    public /* synthetic */ void lambda$setListening$2$CarrierTextController() {
        KeyguardUpdateMonitor keyguardUpdateMonitor = this.mKeyguardUpdateMonitor;
        if (keyguardUpdateMonitor != null) {
            keyguardUpdateMonitor.removeCallback(this.mCallback);
        }
    }

    /* access modifiers changed from: protected */
    public List<SubscriptionInfo> getSubscriptionInfo() {
        return this.mKeyguardUpdateMonitor.getFilteredSubscriptionInfo(false);
    }

    /* access modifiers changed from: protected */
    public void updateCarrierText() {
        CharSequence charSequence;
        boolean z;
        CharSequence charSequence2;
        List subscriptionInfo = getSubscriptionInfo();
        int size = subscriptionInfo.size();
        int[] iArr = new int[size];
        int[] iArr2 = new int[this.mSimSlotsNumber];
        for (int i = 0; i < this.mSimSlotsNumber; i++) {
            iArr2[i] = -1;
        }
        CharSequence[] charSequenceArr = new CharSequence[size];
        int i2 = 0;
        boolean z2 = false;
        boolean z3 = true;
        while (true) {
            charSequence = "";
            if (i2 >= size) {
                break;
            }
            int subscriptionId = ((SubscriptionInfo) subscriptionInfo.get(i2)).getSubscriptionId();
            charSequenceArr[i2] = charSequence;
            iArr[i2] = subscriptionId;
            iArr2[((SubscriptionInfo) subscriptionInfo.get(i2)).getSimSlotIndex()] = i2;
            int simState = this.mKeyguardUpdateMonitor.getSimState(subscriptionId);
            CharSequence carrierTextForSimState = getCarrierTextForSimState(simState, ((SubscriptionInfo) subscriptionInfo.get(i2)).getCarrierName());
            if (carrierTextForSimState != null) {
                charSequenceArr[i2] = carrierTextForSimState;
                z3 = false;
            }
            if (simState == 5) {
                ServiceState serviceState = (ServiceState) this.mKeyguardUpdateMonitor.mServiceStates.get(Integer.valueOf(subscriptionId));
                if (!(serviceState == null || serviceState.getDataRegistrationState() != 0 || (serviceState.getRilDataRadioTechnology() == 18 && (!this.mWifiManager.isWifiEnabled() || this.mWifiManager.getConnectionInfo() == null || this.mWifiManager.getConnectionInfo().getBSSID() == null)))) {
                    z2 = true;
                }
            }
            i2++;
        }
        CharSequence charSequence3 = null;
        if (z3 && !z2) {
            if (size != 0) {
                charSequence3 = makeCarrierStringOnEmergencyCapable(getMissingSimMessage(), ((SubscriptionInfo) subscriptionInfo.get(0)).getCarrierName());
            } else {
                CharSequence text = getContext().getText(17039980);
                Intent registerReceiver = getContext().registerReceiver(null, new IntentFilter("android.telephony.action.SERVICE_PROVIDERS_UPDATED"));
                if (registerReceiver != null) {
                    CharSequence stringExtra = registerReceiver.getBooleanExtra("android.telephony.extra.SHOW_SPN", false) ? registerReceiver.getStringExtra("android.telephony.extra.SPN") : charSequence;
                    if (registerReceiver.getBooleanExtra("android.telephony.extra.SHOW_PLMN", false)) {
                        charSequence = registerReceiver.getStringExtra("android.telephony.extra.PLMN");
                    }
                    if (Objects.equals(charSequence, stringExtra)) {
                        text = charSequence;
                    } else {
                        text = concatenate(charSequence, stringExtra, this.mSeparator);
                    }
                }
                charSequence3 = makeCarrierStringOnEmergencyCapable(getMissingSimMessage(), text);
            }
        }
        if (TextUtils.isEmpty(charSequence3)) {
            charSequence3 = joinNotEmpty(this.mSeparator, charSequenceArr);
        }
        CharSequence updateCarrierTextWithSimIoError = updateCarrierTextWithSimIoError(charSequence3, charSequenceArr, iArr2, z3);
        if (z2 || !WirelessUtils.isAirplaneModeOn(this.mContext)) {
            z = false;
            charSequence2 = updateCarrierTextWithSimIoError;
        } else {
            charSequence2 = getAirplaneModeMessage();
            z = true;
        }
        CarrierTextCallbackInfo carrierTextCallbackInfo = new CarrierTextCallbackInfo(charSequence2, charSequenceArr, true ^ z3, iArr, z);
        postToCallback(carrierTextCallbackInfo);
    }

    /* access modifiers changed from: protected */
    public void postToCallback(CarrierTextCallbackInfo carrierTextCallbackInfo) {
        CarrierTextCallback carrierTextCallback = this.mCarrierTextCallback;
        if (carrierTextCallback != null) {
            this.mMainHandler.post(new Runnable(carrierTextCallbackInfo) {
                public final /* synthetic */ CarrierTextCallbackInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    CarrierTextCallback.this.updateCarrierInfo(this.f$1);
                }
            });
        }
    }

    private Context getContext() {
        return this.mContext;
    }

    private String getMissingSimMessage() {
        return (!this.mShowMissingSim || !this.mTelephonyCapable) ? "" : getContext().getString(C2017R$string.keyguard_missing_sim_message_short);
    }

    private String getAirplaneModeMessage() {
        return this.mShowAirplaneMode ? getContext().getString(C2017R$string.airplane_mode) : "";
    }

    private CharSequence getCarrierTextForSimState(int i, CharSequence charSequence) {
        switch (C05204.$SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[getStatusForIccState(i).ordinal()]) {
            case 1:
                return charSequence;
            case 2:
                return "";
            case 3:
                return makeCarrierStringOnEmergencyCapable(this.mContext.getText(C2017R$string.keyguard_network_locked_message), charSequence);
            case 5:
                return makeCarrierStringOnEmergencyCapable(getContext().getText(C2017R$string.keyguard_permanent_disabled_sim_message_short), charSequence);
            case 7:
                return makeCarrierStringOnLocked(getContext().getText(C2017R$string.keyguard_sim_locked_message), charSequence);
            case 8:
                return makeCarrierStringOnLocked(getContext().getText(C2017R$string.keyguard_sim_puk_locked_message), charSequence);
            case 9:
                return makeCarrierStringOnEmergencyCapable(getContext().getText(C2017R$string.keyguard_sim_error_message_short), charSequence);
            default:
                return null;
        }
    }

    private CharSequence makeCarrierStringOnEmergencyCapable(CharSequence charSequence, CharSequence charSequence2) {
        return this.mIsEmergencyCallCapable ? concatenate(charSequence, charSequence2, this.mSeparator) : charSequence;
    }

    private CharSequence makeCarrierStringOnLocked(CharSequence charSequence, CharSequence charSequence2) {
        boolean z = !TextUtils.isEmpty(charSequence);
        boolean z2 = !TextUtils.isEmpty(charSequence2);
        if (z && z2) {
            return this.mContext.getString(C2017R$string.keyguard_carrier_name_with_sim_locked_template, new Object[]{charSequence2, charSequence});
        } else if (z) {
            return charSequence;
        } else {
            return z2 ? charSequence2 : "";
        }
    }

    /* access modifiers changed from: private */
    public StatusMode getStatusForIccState(int i) {
        boolean z = true;
        if (((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).isDeviceProvisioned() || !(i == 1 || i == 7)) {
            z = false;
        }
        if (z) {
            i = 4;
        }
        switch (i) {
            case 0:
                return StatusMode.SimUnknown;
            case 1:
                return StatusMode.SimMissing;
            case 2:
                return StatusMode.SimLocked;
            case 3:
                return StatusMode.SimPukLocked;
            case 4:
                return StatusMode.SimMissingLocked;
            case 5:
                return StatusMode.Normal;
            case 6:
                return StatusMode.SimNotReady;
            case 7:
                return StatusMode.SimPermDisabled;
            case 8:
                return StatusMode.SimIoError;
            default:
                return StatusMode.SimUnknown;
        }
    }

    private static CharSequence concatenate(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) {
        boolean z = !TextUtils.isEmpty(charSequence);
        boolean z2 = !TextUtils.isEmpty(charSequence2);
        if (z && z2) {
            StringBuilder sb = new StringBuilder();
            sb.append(charSequence);
            sb.append(charSequence3);
            sb.append(charSequence2);
            return sb.toString();
        } else if (z) {
            return charSequence;
        } else {
            return z2 ? charSequence2 : "";
        }
    }

    private static CharSequence joinNotEmpty(CharSequence charSequence, CharSequence[] charSequenceArr) {
        int length = charSequenceArr.length;
        if (length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (!TextUtils.isEmpty(charSequenceArr[i])) {
                if (!TextUtils.isEmpty(sb)) {
                    sb.append(charSequence);
                }
                sb.append(charSequenceArr[i]);
            }
        }
        return sb.toString();
    }
}
