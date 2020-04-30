package com.android.keyguard;

import android.app.ActivityManager;
import android.app.ActivityManager.StackInfo;
import android.app.ActivityTaskManager;
import android.app.UserSwitchObserver;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.app.trust.TrustManager.TrustListener;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageManager;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricSourceType;
import android.hardware.biometrics.IBiometricEnabledOnKeyguardCallback;
import android.hardware.biometrics.IBiometricEnabledOnKeyguardCallback.Stub;
import android.hardware.face.FaceManager;
import android.hardware.face.FaceManager.AuthenticationCallback;
import android.hardware.face.FaceManager.AuthenticationResult;
import android.hardware.face.FaceManager.LockoutResetCallback;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.service.dreams.IDreamManager;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.fuelgauge.BatteryStatus;
import com.android.systemui.C2017R$string;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.util.Assert;
import com.google.android.collect.Lists;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class KeyguardUpdateMonitor implements TrustListener, Dumpable {
    public static final boolean CORE_APPS_ONLY;
    private static final ComponentName FALLBACK_HOME_COMPONENT = new ComponentName("com.android.settings", "com.android.settings.FallbackHome");
    private static int sCurrentUser;
    /* access modifiers changed from: private */
    public int mActiveMobileDataSubscription = -1;
    private boolean mAssistantVisible;
    private boolean mAuthInterruptActive;
    private final Executor mBackgroundExecutor;
    private BatteryStatus mBatteryStatus;
    private IBiometricEnabledOnKeyguardCallback mBiometricEnabledCallback = new Stub() {
        public void onChanged(BiometricSourceType biometricSourceType, boolean z, int i) throws RemoteException {
            if (biometricSourceType == BiometricSourceType.FACE) {
                KeyguardUpdateMonitor.this.mFaceSettingEnabledForUser.put(i, z);
                KeyguardUpdateMonitor.this.updateFaceListeningState();
            }
        }
    };
    private BiometricManager mBiometricManager;
    private boolean mBouncer;
    @VisibleForTesting
    protected final BroadcastReceiver mBroadcastAllReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.app.action.NEXT_ALARM_CLOCK_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(301);
                return;
            }
            String str = "android.intent.extra.user_handle";
            if ("android.intent.action.USER_INFO_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(317, intent.getIntExtra(str, getSendingUserId()), 0));
            } else if ("com.android.facelock.FACE_UNLOCK_STARTED".equals(action)) {
                Trace.beginSection("KeyguardUpdateMonitor.mBroadcastAllReceiver#onReceive ACTION_FACE_UNLOCK_STARTED");
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(327, 1, getSendingUserId()));
                Trace.endSection();
            } else if ("com.android.facelock.FACE_UNLOCK_STOPPED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(327, 0, getSendingUserId()));
            } else if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(309, Integer.valueOf(getSendingUserId())));
            } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(334, getSendingUserId(), 0));
            } else if ("android.intent.action.USER_STOPPED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(340, intent.getIntExtra(str, -1), 0));
            } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(341, intent.getIntExtra(str, -1), 0));
            }
        }
    };
    private final BroadcastDispatcher mBroadcastDispatcher;
    @VisibleForTesting
    protected final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.TIME_TICK".equals(action) || "android.intent.action.TIME_SET".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(301);
            } else if ("android.intent.action.TIMEZONE_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(339, intent.getStringExtra("time-zone")));
            } else if ("android.intent.action.BATTERY_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(302, new BatteryStatus(intent)));
            } else if ("android.intent.action.SIM_STATE_CHANGED".equals(action)) {
                SimData fromIntent = SimData.fromIntent(intent);
                if (intent.getBooleanExtra("rebroadcastOnUnlock", false)) {
                    if (fromIntent.simState == 1) {
                        KeyguardUpdateMonitor.this.mHandler.obtainMessage(338, Boolean.TRUE).sendToTarget();
                    }
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("action ");
                sb.append(action);
                sb.append(" state: ");
                sb.append(intent.getStringExtra("ss"));
                sb.append(" slotId: ");
                sb.append(fromIntent.slotId);
                sb.append(" subid: ");
                sb.append(fromIntent.subId);
                Log.v("KeyguardUpdateMonitor", sb.toString());
                KeyguardUpdateMonitor.this.mHandler.obtainMessage(304, fromIntent.subId, fromIntent.slotId, Integer.valueOf(fromIntent.simState)).sendToTarget();
            } else if ("android.media.RINGER_MODE_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(305, intent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1), 0));
            } else if ("android.intent.action.PHONE_STATE".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(306, intent.getStringExtra("state")));
            } else if ("android.intent.action.AIRPLANE_MODE".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(329);
            } else if ("android.intent.action.SERVICE_STATE".equals(action)) {
                ServiceState newFromBundle = ServiceState.newFromBundle(intent.getExtras());
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(330, intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1), 0, newFromBundle));
            } else if ("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(328);
            } else if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(337);
            }
        }
    };
    private final ArrayList<WeakReference<KeyguardUpdateMonitorCallback>> mCallbacks = Lists.newArrayList();
    private final Runnable mCancelNotReceived = new Runnable() {
        public void run() {
            Log.w("KeyguardUpdateMonitor", "Cancel not received, transitioning to STOPPED");
            KeyguardUpdateMonitor keyguardUpdateMonitor = KeyguardUpdateMonitor.this;
            keyguardUpdateMonitor.mFaceRunningState = 0;
            keyguardUpdateMonitor.mFingerprintRunningState = 0;
            KeyguardUpdateMonitor.this.updateBiometricListeningState();
        }
    };
    private final Context mContext;
    private boolean mDeviceInteractive;
    private final DevicePolicyManager mDevicePolicyManager;
    /* access modifiers changed from: private */
    public boolean mDeviceProvisioned;
    private ContentObserver mDeviceProvisionedObserver;
    private final IDreamManager mDreamManager;
    @VisibleForTesting
    AuthenticationCallback mFaceAuthenticationCallback = new AuthenticationCallback() {
        public void onAuthenticationFailed() {
            KeyguardUpdateMonitor.this.handleFaceAuthFailed();
        }

        public void onAuthenticationSucceeded(AuthenticationResult authenticationResult) {
            Trace.beginSection("KeyguardUpdateMonitor#onAuthenticationSucceeded");
            KeyguardUpdateMonitor.this.handleFaceAuthenticated(authenticationResult.getUserId(), authenticationResult.isStrongBiometric());
            Trace.endSection();
        }

        public void onAuthenticationHelp(int i, CharSequence charSequence) {
            KeyguardUpdateMonitor.this.handleFaceHelp(i, charSequence.toString());
        }

        public void onAuthenticationError(int i, CharSequence charSequence) {
            KeyguardUpdateMonitor.this.handleFaceError(i, charSequence.toString());
        }

        public void onAuthenticationAcquired(int i) {
            KeyguardUpdateMonitor.this.handleFaceAcquired(i);
        }
    };
    private CancellationSignal mFaceCancelSignal;
    private final LockoutResetCallback mFaceLockoutResetCallback = new LockoutResetCallback() {
        public void onLockoutReset() {
            KeyguardUpdateMonitor.this.handleFaceLockoutReset();
        }
    };
    private FaceManager mFaceManager;
    /* access modifiers changed from: private */
    public int mFaceRunningState = 0;
    /* access modifiers changed from: private */
    public SparseBooleanArray mFaceSettingEnabledForUser = new SparseBooleanArray();
    private FingerprintManager.AuthenticationCallback mFingerprintAuthenticationCallback = new FingerprintManager.AuthenticationCallback() {
        public void onAuthenticationFailed() {
            KeyguardUpdateMonitor.this.handleFingerprintAuthFailed();
        }

        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
            Trace.beginSection("KeyguardUpdateMonitor#onAuthenticationSucceeded");
            KeyguardUpdateMonitor.this.handleFingerprintAuthenticated(authenticationResult.getUserId(), authenticationResult.isStrongBiometric());
            Trace.endSection();
        }

        public void onAuthenticationHelp(int i, CharSequence charSequence) {
            KeyguardUpdateMonitor.this.handleFingerprintHelp(i, charSequence.toString());
        }

        public void onAuthenticationError(int i, CharSequence charSequence) {
            KeyguardUpdateMonitor.this.handleFingerprintError(i, charSequence.toString());
        }

        public void onAuthenticationAcquired(int i) {
            KeyguardUpdateMonitor.this.handleFingerprintAcquired(i);
        }
    };
    private CancellationSignal mFingerprintCancelSignal;
    private final FingerprintManager.LockoutResetCallback mFingerprintLockoutResetCallback = new FingerprintManager.LockoutResetCallback() {
        public void onLockoutReset() {
            KeyguardUpdateMonitor.this.handleFingerprintLockoutReset();
        }
    };
    /* access modifiers changed from: private */
    public int mFingerprintRunningState = 0;
    private FingerprintManager mFpm;
    private boolean mGoingToSleep;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    /* access modifiers changed from: private */
    public int mHardwareFaceUnavailableRetryCount = 0;
    /* access modifiers changed from: private */
    public int mHardwareFingerprintUnavailableRetryCount = 0;
    private boolean mHasLockscreenWallpaper;
    private boolean mIsDreaming;
    private final boolean mIsPrimaryUser;
    private KeyguardBypassController mKeyguardBypassController;
    private boolean mKeyguardGoingAway;
    private boolean mKeyguardIsVisible;
    private boolean mKeyguardOccluded;
    private boolean mLockIconPressed;
    /* access modifiers changed from: private */
    public LockPatternUtils mLockPatternUtils;
    private boolean mLogoutEnabled;
    private boolean mNeedsSlowUnlockTransition;
    private int mPhoneState;
    @VisibleForTesting
    public PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onActiveDataSubscriptionIdChanged(int i) {
            KeyguardUpdateMonitor.this.mActiveMobileDataSubscription = i;
            KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(328);
        }
    };
    private Runnable mRetryFaceAuthentication = new Runnable() {
        public void run() {
            StringBuilder sb = new StringBuilder();
            sb.append("Retrying face after HW unavailable, attempt ");
            sb.append(KeyguardUpdateMonitor.this.mHardwareFaceUnavailableRetryCount);
            Log.w("KeyguardUpdateMonitor", sb.toString());
            KeyguardUpdateMonitor.this.updateFaceListeningState();
        }
    };
    private Runnable mRetryFingerprintAuthentication = new Runnable() {
        public void run() {
            StringBuilder sb = new StringBuilder();
            sb.append("Retrying fingerprint after HW unavailable, attempt ");
            sb.append(KeyguardUpdateMonitor.this.mHardwareFingerprintUnavailableRetryCount);
            Log.w("KeyguardUpdateMonitor", sb.toString());
            KeyguardUpdateMonitor.this.updateFingerprintListeningState();
        }
    };
    private int mRingMode;
    private boolean mScreenOn;
    private Map<Integer, Intent> mSecondaryLockscreenRequirement = new HashMap();
    private boolean mSecureCameraLaunched;
    HashMap<Integer, ServiceState> mServiceStates = new HashMap<>();
    HashMap<Integer, SimData> mSimDatas = new HashMap<>();
    @VisibleForTesting
    protected StrongAuthTracker mStrongAuthTracker;
    private List<SubscriptionInfo> mSubscriptionInfo;
    private OnSubscriptionsChangedListener mSubscriptionListener = new OnSubscriptionsChangedListener() {
        public void onSubscriptionsChanged() {
            KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(328);
        }
    };
    private SubscriptionManager mSubscriptionManager;
    private boolean mSwitchingUser;
    private final TaskStackChangeListener mTaskStackListener = new TaskStackChangeListener() {
        public void onTaskStackChangedBackground() {
            try {
                StackInfo stackInfo = ActivityTaskManager.getService().getStackInfo(0, 4);
                if (stackInfo != null) {
                    KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(335, Boolean.valueOf(stackInfo.visible)));
                }
            } catch (RemoteException e) {
                Log.e("KeyguardUpdateMonitor", "unable to check task stack", e);
            }
        }
    };
    @VisibleForTesting
    protected boolean mTelephonyCapable;
    private TrustManager mTrustManager;
    private Runnable mUpdateBiometricListeningState = new Runnable() {
        public final void run() {
            KeyguardUpdateMonitor.this.updateBiometricListeningState();
        }
    };
    private SparseArray<BiometricAuthenticated> mUserFaceAuthenticated = new SparseArray<>();
    private SparseBooleanArray mUserFaceUnlockRunning = new SparseBooleanArray();
    private SparseArray<BiometricAuthenticated> mUserFingerprintAuthenticated = new SparseArray<>();
    private SparseBooleanArray mUserHasTrust = new SparseBooleanArray();
    private SparseBooleanArray mUserIsUnlocked = new SparseBooleanArray();
    private UserManager mUserManager;
    private final UserSwitchObserver mUserSwitchObserver = new UserSwitchObserver() {
        public void onUserSwitching(int i, IRemoteCallback iRemoteCallback) {
            KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(310, i, 0, iRemoteCallback));
        }

        public void onUserSwitchComplete(int i) throws RemoteException {
            KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(314, i, 0));
        }
    };
    private SparseBooleanArray mUserTrustIsManaged = new SparseBooleanArray();
    private SparseBooleanArray mUserTrustIsUsuallyManaged = new SparseBooleanArray();

    private class BiometricAuthenticated {
        /* access modifiers changed from: private */
        public final boolean mAuthenticated;
        /* access modifiers changed from: private */
        public final boolean mIsStrongBiometric;

        BiometricAuthenticated(KeyguardUpdateMonitor keyguardUpdateMonitor, boolean z, boolean z2) {
            this.mAuthenticated = z;
            this.mIsStrongBiometric = z2;
        }
    }

    private static class SimData {
        public int simState;
        public int slotId;
        public int subId;

        SimData(int i, int i2, int i3) {
            this.simState = i;
            this.slotId = i2;
            this.subId = i3;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0089, code lost:
            if ("IMSI".equals(r0) == false) goto L_0x008c;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static com.android.keyguard.KeyguardUpdateMonitor.SimData fromIntent(android.content.Intent r7) {
            /*
                java.lang.String r0 = r7.getAction()
                java.lang.String r1 = "android.intent.action.SIM_STATE_CHANGED"
                boolean r0 = r1.equals(r0)
                if (r0 == 0) goto L_0x0092
                java.lang.String r0 = "ss"
                java.lang.String r0 = r7.getStringExtra(r0)
                java.lang.String r1 = "android.telephony.extra.SLOT_INDEX"
                r2 = 0
                int r1 = r7.getIntExtra(r1, r2)
                r3 = -1
                java.lang.String r4 = "android.telephony.extra.SUBSCRIPTION_INDEX"
                int r3 = r7.getIntExtra(r4, r3)
                java.lang.String r4 = "ABSENT"
                boolean r4 = r4.equals(r0)
                r5 = 5
                java.lang.String r6 = "reason"
                if (r4 == 0) goto L_0x003c
                java.lang.String r7 = r7.getStringExtra(r6)
                java.lang.String r0 = "PERM_DISABLED"
                boolean r7 = r0.equals(r7)
                if (r7 == 0) goto L_0x0039
                r7 = 7
                goto L_0x003a
            L_0x0039:
                r7 = 1
            L_0x003a:
                r2 = r7
                goto L_0x008c
            L_0x003c:
                java.lang.String r4 = "READY"
                boolean r4 = r4.equals(r0)
                if (r4 == 0) goto L_0x0046
            L_0x0044:
                r2 = r5
                goto L_0x008c
            L_0x0046:
                java.lang.String r4 = "LOCKED"
                boolean r4 = r4.equals(r0)
                if (r4 == 0) goto L_0x0066
                java.lang.String r7 = r7.getStringExtra(r6)
                java.lang.String r0 = "PIN"
                boolean r0 = r0.equals(r7)
                if (r0 == 0) goto L_0x005c
                r2 = 2
                goto L_0x008c
            L_0x005c:
                java.lang.String r0 = "PUK"
                boolean r7 = r0.equals(r7)
                if (r7 == 0) goto L_0x008c
                r2 = 3
                goto L_0x008c
            L_0x0066:
                java.lang.String r7 = "NETWORK"
                boolean r7 = r7.equals(r0)
                if (r7 == 0) goto L_0x0070
                r2 = 4
                goto L_0x008c
            L_0x0070:
                java.lang.String r7 = "CARD_IO_ERROR"
                boolean r7 = r7.equals(r0)
                if (r7 == 0) goto L_0x007b
                r2 = 8
                goto L_0x008c
            L_0x007b:
                java.lang.String r7 = "LOADED"
                boolean r7 = r7.equals(r0)
                if (r7 != 0) goto L_0x0044
                java.lang.String r7 = "IMSI"
                boolean r7 = r7.equals(r0)
                if (r7 == 0) goto L_0x008c
                goto L_0x0044
            L_0x008c:
                com.android.keyguard.KeyguardUpdateMonitor$SimData r7 = new com.android.keyguard.KeyguardUpdateMonitor$SimData
                r7.<init>(r2, r1, r3)
                return r7
            L_0x0092:
                java.lang.IllegalArgumentException r7 = new java.lang.IllegalArgumentException
                java.lang.String r0 = "only handles intent ACTION_SIM_STATE_CHANGED"
                r7.<init>(r0)
                throw r7
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardUpdateMonitor.SimData.fromIntent(android.content.Intent):com.android.keyguard.KeyguardUpdateMonitor$SimData");
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("SimData{state=");
            sb.append(this.simState);
            sb.append(",slotId=");
            sb.append(this.slotId);
            sb.append(",subId=");
            sb.append(this.subId);
            sb.append("}");
            return sb.toString();
        }
    }

    public static class StrongAuthTracker extends com.android.internal.widget.LockPatternUtils.StrongAuthTracker {
        private final Consumer<Integer> mStrongAuthRequiredChangedCallback;

        public StrongAuthTracker(Context context, Consumer<Integer> consumer) {
            super(context);
            this.mStrongAuthRequiredChangedCallback = consumer;
        }

        public boolean isUnlockingWithBiometricAllowed(boolean z) {
            return isBiometricAllowedForUser(z, KeyguardUpdateMonitor.getCurrentUser());
        }

        public boolean hasUserAuthenticatedSinceBoot() {
            return (getStrongAuthForUser(KeyguardUpdateMonitor.getCurrentUser()) & 1) == 0;
        }

        public void onStrongAuthRequiredChanged(int i) {
            this.mStrongAuthRequiredChangedCallback.accept(Integer.valueOf(i));
        }
    }

    private boolean containsFlag(int i, int i2) {
        return (i & i2) != 0;
    }

    public static boolean isSimPinSecure(int i) {
        return i == 2 || i == 3 || i == 7;
    }

    static {
        try {
            CORE_APPS_ONLY = IPackageManager.Stub.asInterface(ServiceManager.getService("package")).isOnlyCoreApps();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static synchronized void setCurrentUser(int i) {
        synchronized (KeyguardUpdateMonitor.class) {
            sCurrentUser = i;
        }
    }

    public static synchronized int getCurrentUser() {
        int i;
        synchronized (KeyguardUpdateMonitor.class) {
            i = sCurrentUser;
        }
        return i;
    }

    public void onTrustChanged(boolean z, int i, int i2) {
        Assert.isMainThread();
        this.mUserHasTrust.put(i, z);
        for (int i3 = 0; i3 < this.mCallbacks.size(); i3++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i3)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTrustChanged(i);
                if (z && i2 != 0) {
                    keyguardUpdateMonitorCallback.onTrustGrantedWithFlags(i2, i);
                }
            }
        }
    }

    public void onTrustError(CharSequence charSequence) {
        dispatchErrorMessage(charSequence);
    }

    /* access modifiers changed from: private */
    public void handleSimSubscriptionInfoChanged() {
        Assert.isMainThread();
        String str = "KeyguardUpdateMonitor";
        Log.v(str, "onSubscriptionInfoChanged()");
        List<SubscriptionInfo> completeActiveSubscriptionInfoList = this.mSubscriptionManager.getCompleteActiveSubscriptionInfoList();
        if (completeActiveSubscriptionInfoList != null) {
            for (SubscriptionInfo subscriptionInfo : completeActiveSubscriptionInfoList) {
                StringBuilder sb = new StringBuilder();
                sb.append("SubInfo:");
                sb.append(subscriptionInfo);
                Log.v(str, sb.toString());
            }
        } else {
            Log.v(str, "onSubscriptionInfoChanged: list is null");
        }
        List subscriptionInfo2 = getSubscriptionInfo(true);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < subscriptionInfo2.size(); i++) {
            SubscriptionInfo subscriptionInfo3 = (SubscriptionInfo) subscriptionInfo2.get(i);
            if (refreshSimState(subscriptionInfo3.getSubscriptionId(), subscriptionInfo3.getSimSlotIndex())) {
                arrayList.add(subscriptionInfo3);
            }
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            SimData simData = (SimData) this.mSimDatas.get(Integer.valueOf(((SubscriptionInfo) arrayList.get(i2)).getSubscriptionId()));
            for (int i3 = 0; i3 < this.mCallbacks.size(); i3++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i3)).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onSimStateChanged(simData.subId, simData.slotId, simData.simState);
                }
            }
        }
        callbacksRefreshCarrierInfo();
    }

    /* access modifiers changed from: private */
    public void handleAirplaneModeChanged() {
        callbacksRefreshCarrierInfo();
    }

    private void callbacksRefreshCarrierInfo() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onRefreshCarrierInfo();
            }
        }
    }

    public List<SubscriptionInfo> getSubscriptionInfo(boolean z) {
        List<SubscriptionInfo> list = this.mSubscriptionInfo;
        if (list == null || z) {
            list = this.mSubscriptionManager.getCompleteActiveSubscriptionInfoList();
        }
        if (list == null) {
            this.mSubscriptionInfo = new ArrayList();
        } else {
            this.mSubscriptionInfo = list;
        }
        return new ArrayList(this.mSubscriptionInfo);
    }

    public List<SubscriptionInfo> getFilteredSubscriptionInfo(boolean z) {
        List<SubscriptionInfo> subscriptionInfo = getSubscriptionInfo(false);
        if (subscriptionInfo.size() == 2) {
            SubscriptionInfo subscriptionInfo2 = (SubscriptionInfo) subscriptionInfo.get(0);
            SubscriptionInfo subscriptionInfo3 = (SubscriptionInfo) subscriptionInfo.get(1);
            if (subscriptionInfo2.getGroupUuid() == null || !subscriptionInfo2.getGroupUuid().equals(subscriptionInfo3.getGroupUuid()) || (!subscriptionInfo2.isOpportunistic() && !subscriptionInfo3.isOpportunistic())) {
                return subscriptionInfo;
            }
            if (CarrierConfigManager.getDefaultConfig().getBoolean("always_show_primary_signal_bar_in_opportunistic_network_boolean")) {
                if (!subscriptionInfo2.isOpportunistic()) {
                    subscriptionInfo2 = subscriptionInfo3;
                }
                subscriptionInfo.remove(subscriptionInfo2);
            } else {
                if (subscriptionInfo2.getSubscriptionId() == this.mActiveMobileDataSubscription) {
                    subscriptionInfo2 = subscriptionInfo3;
                }
                subscriptionInfo.remove(subscriptionInfo2);
            }
        }
        return subscriptionInfo;
    }

    public void onTrustManagedChanged(boolean z, int i) {
        Assert.isMainThread();
        this.mUserTrustIsManaged.put(i, z);
        this.mUserTrustIsUsuallyManaged.put(i, this.mTrustManager.isTrustUsuallyManaged(i));
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTrustManagedChanged(i);
            }
        }
    }

    public void setKeyguardGoingAway(boolean z) {
        this.mKeyguardGoingAway = z;
        updateFingerprintListeningState();
    }

    public void setKeyguardOccluded(boolean z) {
        this.mKeyguardOccluded = z;
        updateBiometricListeningState();
    }

    public void onCameraLaunched() {
        this.mSecureCameraLaunched = true;
        updateBiometricListeningState();
    }

    public boolean isDreaming() {
        return this.mIsDreaming;
    }

    public void awakenFromDream() {
        if (this.mIsDreaming) {
            IDreamManager iDreamManager = this.mDreamManager;
            if (iDreamManager != null) {
                try {
                    iDreamManager.awaken();
                } catch (RemoteException unused) {
                    Log.e("KeyguardUpdateMonitor", "Unable to awaken from dream");
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void onFingerprintAuthenticated(int i, boolean z) {
        Assert.isMainThread();
        Trace.beginSection("KeyGuardUpdateMonitor#onFingerPrintAuthenticated");
        this.mUserFingerprintAuthenticated.put(i, new BiometricAuthenticated(this, true, z));
        if (getUserCanSkipBouncer(i)) {
            this.mTrustManager.unlockedByBiometricForUser(i, BiometricSourceType.FINGERPRINT);
        }
        this.mFingerprintCancelSignal = null;
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAuthenticated(i, BiometricSourceType.FINGERPRINT, z);
            }
        }
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(336), 500);
        this.mAssistantVisible = false;
        reportSuccessfulBiometricUnlock(z, i);
        Trace.endSection();
    }

    private void reportSuccessfulBiometricUnlock(final boolean z, final int i) {
        this.mBackgroundExecutor.execute(new Runnable() {
            public void run() {
                KeyguardUpdateMonitor.this.mLockPatternUtils.reportSuccessfulBiometricUnlock(z, i);
            }
        });
    }

    /* access modifiers changed from: private */
    public void handleFingerprintAuthFailed() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAuthFailed(BiometricSourceType.FINGERPRINT);
            }
        }
        handleFingerprintHelp(-1, this.mContext.getString(C2017R$string.kg_fingerprint_not_recognized));
    }

    /* access modifiers changed from: private */
    public void handleFingerprintAcquired(int i) {
        Assert.isMainThread();
        if (i == 0) {
            for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onBiometricAcquired(BiometricSourceType.FINGERPRINT);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFingerprintAuthenticated(int i, boolean z) {
        String str = "KeyguardUpdateMonitor";
        Trace.beginSection("KeyGuardUpdateMonitor#handlerFingerPrintAuthenticated");
        try {
            int i2 = ActivityManager.getService().getCurrentUser().id;
            if (i2 != i) {
                StringBuilder sb = new StringBuilder();
                sb.append("Fingerprint authenticated for wrong user: ");
                sb.append(i);
                Log.d(str, sb.toString());
            } else if (isFingerprintDisabled(i2)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Fingerprint disabled by DPM for userId: ");
                sb2.append(i2);
                Log.d(str, sb2.toString());
            } else {
                onFingerprintAuthenticated(i2, z);
                setFingerprintRunningState(0);
                Trace.endSection();
            }
        } catch (RemoteException e) {
            Log.e(str, "Failed to get current user id: ", e);
        } finally {
            setFingerprintRunningState(0);
        }
    }

    /* access modifiers changed from: private */
    public void handleFingerprintHelp(int i, String str) {
        Assert.isMainThread();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricHelp(i, str, BiometricSourceType.FINGERPRINT);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFingerprintError(int i, String str) {
        Assert.isMainThread();
        if (i == 5 && this.mHandler.hasCallbacks(this.mCancelNotReceived)) {
            this.mHandler.removeCallbacks(this.mCancelNotReceived);
        }
        if (i == 5 && this.mFingerprintRunningState == 3) {
            setFingerprintRunningState(0);
            updateFingerprintListeningState();
        } else {
            setFingerprintRunningState(0);
        }
        if (i == 1) {
            int i2 = this.mHardwareFingerprintUnavailableRetryCount;
            if (i2 < 10) {
                this.mHardwareFingerprintUnavailableRetryCount = i2 + 1;
                this.mHandler.removeCallbacks(this.mRetryFingerprintAuthentication);
                this.mHandler.postDelayed(this.mRetryFingerprintAuthentication, 500);
            }
        }
        if (i == 9) {
            this.mLockPatternUtils.requireStrongAuth(8, getCurrentUser());
        }
        for (int i3 = 0; i3 < this.mCallbacks.size(); i3++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i3)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricError(i, str, BiometricSourceType.FINGERPRINT);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFingerprintLockoutReset() {
        updateFingerprintListeningState();
    }

    private void setFingerprintRunningState(int i) {
        boolean z = false;
        boolean z2 = this.mFingerprintRunningState == 1;
        if (i == 1) {
            z = true;
        }
        this.mFingerprintRunningState = i;
        StringBuilder sb = new StringBuilder();
        sb.append("fingerprintRunningState: ");
        sb.append(this.mFingerprintRunningState);
        Log.d("KeyguardUpdateMonitor", sb.toString());
        if (z2 != z) {
            notifyFingerprintRunningStateChanged();
        }
    }

    private void notifyFingerprintRunningStateChanged() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricRunningStateChanged(isFingerprintDetectionRunning(), BiometricSourceType.FINGERPRINT);
            }
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void onFaceAuthenticated(int i, boolean z) {
        Trace.beginSection("KeyGuardUpdateMonitor#onFaceAuthenticated");
        Assert.isMainThread();
        this.mUserFaceAuthenticated.put(i, new BiometricAuthenticated(this, true, z));
        if (getUserCanSkipBouncer(i)) {
            this.mTrustManager.unlockedByBiometricForUser(i, BiometricSourceType.FACE);
        }
        this.mFaceCancelSignal = null;
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAuthenticated(i, BiometricSourceType.FACE, z);
            }
        }
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(336), 500);
        this.mAssistantVisible = false;
        reportSuccessfulBiometricUnlock(z, i);
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void handleFaceAuthFailed() {
        Assert.isMainThread();
        setFaceRunningState(0);
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAuthFailed(BiometricSourceType.FACE);
            }
        }
        handleFaceHelp(-2, this.mContext.getString(C2017R$string.kg_face_not_recognized));
    }

    /* access modifiers changed from: private */
    public void handleFaceAcquired(int i) {
        Assert.isMainThread();
        if (i == 0) {
            Log.d("KeyguardUpdateMonitor", "Face acquired");
            for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onBiometricAcquired(BiometricSourceType.FACE);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFaceAuthenticated(int i, boolean z) {
        String str;
        Trace.beginSection("KeyGuardUpdateMonitor#handlerFaceAuthenticated");
        try {
            str = "KeyguardUpdateMonitor";
            if (this.mGoingToSleep) {
                Log.d(str, "Aborted successful auth because device is going to sleep.");
                return;
            }
            int i2 = ActivityManager.getService().getCurrentUser().id;
            if (i2 != i) {
                StringBuilder sb = new StringBuilder();
                sb.append("Face authenticated for wrong user: ");
                sb.append(i);
                Log.d(str, sb.toString());
            } else if (isFaceDisabled(i2)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Face authentication disabled by DPM for userId: ");
                sb2.append(i2);
                Log.d(str, sb2.toString());
                setFaceRunningState(0);
            } else {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Face auth succeeded for user ");
                sb3.append(i2);
                Log.d(str, sb3.toString());
                onFaceAuthenticated(i2, z);
                setFaceRunningState(0);
                Trace.endSection();
            }
        } catch (RemoteException e) {
            Log.e(str, "Failed to get current user id: ", e);
        } finally {
            setFaceRunningState(0);
        }
    }

    /* access modifiers changed from: private */
    public void handleFaceHelp(int i, String str) {
        Assert.isMainThread();
        StringBuilder sb = new StringBuilder();
        sb.append("Face help received: ");
        sb.append(str);
        Log.d("KeyguardUpdateMonitor", sb.toString());
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricHelp(i, str, BiometricSourceType.FACE);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFaceError(int i, String str) {
        Assert.isMainThread();
        StringBuilder sb = new StringBuilder();
        sb.append("Face error received: ");
        sb.append(str);
        Log.d("KeyguardUpdateMonitor", sb.toString());
        if (i == 5 && this.mHandler.hasCallbacks(this.mCancelNotReceived)) {
            this.mHandler.removeCallbacks(this.mCancelNotReceived);
        }
        if (i == 5 && this.mFaceRunningState == 3) {
            setFaceRunningState(0);
            updateFaceListeningState();
        } else {
            setFaceRunningState(0);
        }
        if (i == 1 || i == 2) {
            int i2 = this.mHardwareFaceUnavailableRetryCount;
            if (i2 < 10) {
                this.mHardwareFaceUnavailableRetryCount = i2 + 1;
                this.mHandler.removeCallbacks(this.mRetryFaceAuthentication);
                this.mHandler.postDelayed(this.mRetryFaceAuthentication, 500);
            }
        }
        if (i == 9) {
            this.mLockPatternUtils.requireStrongAuth(8, getCurrentUser());
        }
        for (int i3 = 0; i3 < this.mCallbacks.size(); i3++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i3)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricError(i, str, BiometricSourceType.FACE);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFaceLockoutReset() {
        updateFaceListeningState();
    }

    private void setFaceRunningState(int i) {
        boolean z = false;
        boolean z2 = this.mFaceRunningState == 1;
        if (i == 1) {
            z = true;
        }
        this.mFaceRunningState = i;
        StringBuilder sb = new StringBuilder();
        sb.append("faceRunningState: ");
        sb.append(this.mFaceRunningState);
        Log.d("KeyguardUpdateMonitor", sb.toString());
        if (z2 != z) {
            notifyFaceRunningStateChanged();
        }
    }

    private void notifyFaceRunningStateChanged() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricRunningStateChanged(isFaceDetectionRunning(), BiometricSourceType.FACE);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFaceUnlockStateChanged(boolean z, int i) {
        Assert.isMainThread();
        this.mUserFaceUnlockRunning.put(i, z);
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onFaceUnlockStateChanged(z, i);
            }
        }
    }

    public boolean isFingerprintDetectionRunning() {
        return this.mFingerprintRunningState == 1;
    }

    public boolean isFaceDetectionRunning() {
        return this.mFaceRunningState == 1;
    }

    private boolean isTrustDisabled(int i) {
        return isSimPinSecure();
    }

    private boolean isFingerprintDisabled(int i) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        return !(devicePolicyManager == null || (devicePolicyManager.getKeyguardDisabledFeatures(null, i) & 32) == 0) || isSimPinSecure();
    }

    private boolean isFaceDisabled(int i) {
        return ((Boolean) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier((DevicePolicyManager) this.mContext.getSystemService("device_policy"), i) {
            public final /* synthetic */ DevicePolicyManager f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final Object get() {
                return KeyguardUpdateMonitor.this.lambda$isFaceDisabled$0$KeyguardUpdateMonitor(this.f$1, this.f$2);
            }
        })).booleanValue();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$isFaceDisabled$0 */
    public /* synthetic */ Boolean lambda$isFaceDisabled$0$KeyguardUpdateMonitor(DevicePolicyManager devicePolicyManager, int i) {
        return Boolean.valueOf(!(devicePolicyManager == null || (devicePolicyManager.getKeyguardDisabledFeatures(null, i) & 128) == 0) || isSimPinSecure());
    }

    public boolean getUserCanSkipBouncer(int i) {
        return getUserHasTrust(i) || getUserUnlockedWithBiometric(i);
    }

    public boolean getUserHasTrust(int i) {
        return !isTrustDisabled(i) && this.mUserHasTrust.get(i);
    }

    public boolean getUserUnlockedWithBiometric(int i) {
        BiometricAuthenticated biometricAuthenticated = (BiometricAuthenticated) this.mUserFingerprintAuthenticated.get(i);
        BiometricAuthenticated biometricAuthenticated2 = (BiometricAuthenticated) this.mUserFaceAuthenticated.get(i);
        boolean z = biometricAuthenticated != null && biometricAuthenticated.mAuthenticated && isUnlockingWithBiometricAllowed(biometricAuthenticated.mIsStrongBiometric);
        boolean z2 = biometricAuthenticated2 != null && biometricAuthenticated2.mAuthenticated && isUnlockingWithBiometricAllowed(biometricAuthenticated2.mIsStrongBiometric);
        if (z || z2) {
            return true;
        }
        return false;
    }

    public boolean getUserTrustIsManaged(int i) {
        return this.mUserTrustIsManaged.get(i) && !isTrustDisabled(i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0055 A[LOOP:0: B:11:0x0055->B:16:0x0070, LOOP_START, PHI: r3 
      PHI: (r3v1 int) = (r3v0 int), (r3v2 int) binds: [B:10:0x0053, B:16:0x0070] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:20:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateSecondaryLockscreenRequirement(int r6) {
        /*
            r5 = this;
            java.util.Map<java.lang.Integer, android.content.Intent> r0 = r5.mSecondaryLockscreenRequirement
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            java.lang.Object r0 = r0.get(r1)
            android.content.Intent r0 = (android.content.Intent) r0
            android.app.admin.DevicePolicyManager r1 = r5.mDevicePolicyManager
            boolean r1 = r1.isSecondaryLockscreenEnabled(r6)
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0043
            if (r0 != 0) goto L_0x0043
            android.content.Context r0 = r5.mContext
            android.content.pm.PackageManager r0 = r0.getPackageManager()
            android.content.Intent r1 = new android.content.Intent
            java.lang.String r4 = "android.app.action.BIND_SECONDARY_LOCKSCREEN_SERVICE"
            r1.<init>(r4)
            android.content.pm.ResolveInfo r0 = r0.resolveService(r1, r3)
            if (r0 == 0) goto L_0x0052
            android.content.Intent r1 = new android.content.Intent
            r1.<init>()
            android.content.pm.ServiceInfo r0 = r0.serviceInfo
            android.content.ComponentName r0 = r0.getComponentName()
            r1.setComponent(r0)
            java.util.Map<java.lang.Integer, android.content.Intent> r0 = r5.mSecondaryLockscreenRequirement
            java.lang.Integer r4 = java.lang.Integer.valueOf(r6)
            r0.put(r4, r1)
            goto L_0x0053
        L_0x0043:
            if (r1 != 0) goto L_0x0052
            if (r0 == 0) goto L_0x0052
            java.util.Map<java.lang.Integer, android.content.Intent> r0 = r5.mSecondaryLockscreenRequirement
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            r4 = 0
            r0.put(r1, r4)
            goto L_0x0053
        L_0x0052:
            r2 = r3
        L_0x0053:
            if (r2 == 0) goto L_0x0073
        L_0x0055:
            java.util.ArrayList<java.lang.ref.WeakReference<com.android.keyguard.KeyguardUpdateMonitorCallback>> r0 = r5.mCallbacks
            int r0 = r0.size()
            if (r3 >= r0) goto L_0x0073
            java.util.ArrayList<java.lang.ref.WeakReference<com.android.keyguard.KeyguardUpdateMonitorCallback>> r0 = r5.mCallbacks
            java.lang.Object r0 = r0.get(r3)
            java.lang.ref.WeakReference r0 = (java.lang.ref.WeakReference) r0
            java.lang.Object r0 = r0.get()
            com.android.keyguard.KeyguardUpdateMonitorCallback r0 = (com.android.keyguard.KeyguardUpdateMonitorCallback) r0
            if (r0 == 0) goto L_0x0070
            r0.onSecondaryLockscreenRequirementChanged(r6)
        L_0x0070:
            int r3 = r3 + 1
            goto L_0x0055
        L_0x0073:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardUpdateMonitor.updateSecondaryLockscreenRequirement(int):void");
    }

    public Intent getSecondaryLockscreenRequirement(int i) {
        return (Intent) this.mSecondaryLockscreenRequirement.get(Integer.valueOf(i));
    }

    public boolean isTrustUsuallyManaged(int i) {
        Assert.isMainThread();
        return this.mUserTrustIsUsuallyManaged.get(i);
    }

    public boolean isUnlockingWithBiometricAllowed(boolean z) {
        return this.mStrongAuthTracker.isUnlockingWithBiometricAllowed(z);
    }

    public boolean isUserInLockdown(int i) {
        return containsFlag(this.mStrongAuthTracker.getStrongAuthForUser(i), 32);
    }

    public boolean userNeedsStrongAuth() {
        return this.mStrongAuthTracker.getStrongAuthForUser(getCurrentUser()) != 0;
    }

    public boolean needsSlowUnlockTransition() {
        return this.mNeedsSlowUnlockTransition;
    }

    public StrongAuthTracker getStrongAuthTracker() {
        return this.mStrongAuthTracker;
    }

    /* access modifiers changed from: private */
    public void notifyStrongAuthStateChanged(int i) {
        Assert.isMainThread();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onStrongAuthStateChanged(i);
            }
        }
    }

    public boolean isScreenOn() {
        return this.mScreenOn;
    }

    private void dispatchErrorMessage(CharSequence charSequence) {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTrustAgentErrorMessage(charSequence);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setAssistantVisible(boolean z) {
        this.mAssistantVisible = z;
        updateBiometricListeningState();
    }

    /* access modifiers changed from: protected */
    public void handleStartedWakingUp() {
        Trace.beginSection("KeyguardUpdateMonitor#handleStartedWakingUp");
        Assert.isMainThread();
        updateBiometricListeningState();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onStartedWakingUp();
            }
        }
        Trace.endSection();
    }

    /* access modifiers changed from: protected */
    public void handleStartedGoingToSleep(int i) {
        Assert.isMainThread();
        this.mLockIconPressed = false;
        clearBiometricRecognized();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onStartedGoingToSleep(i);
            }
        }
        this.mGoingToSleep = true;
        updateBiometricListeningState();
    }

    /* access modifiers changed from: protected */
    public void handleFinishedGoingToSleep(int i) {
        Assert.isMainThread();
        this.mGoingToSleep = false;
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onFinishedGoingToSleep(i);
            }
        }
        updateBiometricListeningState();
    }

    /* access modifiers changed from: private */
    public void handleScreenTurnedOn() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onScreenTurnedOn();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleScreenTurnedOff() {
        String str = "KeyguardUpdateMonitor#handleScreenTurnedOff";
        DejankUtils.startDetectingBlockingIpcs(str);
        Assert.isMainThread();
        this.mHardwareFingerprintUnavailableRetryCount = 0;
        this.mHardwareFaceUnavailableRetryCount = 0;
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onScreenTurnedOff();
            }
        }
        DejankUtils.stopDetectingBlockingIpcs(str);
    }

    /* access modifiers changed from: private */
    public void handleDreamingStateChanged(int i) {
        Assert.isMainThread();
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        this.mIsDreaming = z;
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onDreamingStateChanged(this.mIsDreaming);
            }
        }
        updateBiometricListeningState();
    }

    /* access modifiers changed from: private */
    public void handleUserInfoChanged(int i) {
        Assert.isMainThread();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onUserInfoChanged(i);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleUserUnlocked(int i) {
        Assert.isMainThread();
        this.mUserIsUnlocked.put(i, true);
        this.mNeedsSlowUnlockTransition = resolveNeedsSlowUnlockTransition();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onUserUnlocked();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleUserStopped(int i) {
        Assert.isMainThread();
        this.mUserIsUnlocked.put(i, this.mUserManager.isUserUnlocked(i));
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void handleUserRemoved(int i) {
        Assert.isMainThread();
        this.mUserIsUnlocked.delete(i);
        this.mUserTrustIsUsuallyManaged.delete(i);
    }

    @VisibleForTesting
    protected KeyguardUpdateMonitor(Context context, Looper looper, BroadcastDispatcher broadcastDispatcher, DumpManager dumpManager, Executor executor) {
        this.mContext = context;
        this.mSubscriptionManager = SubscriptionManager.from(context);
        this.mDeviceProvisioned = isDeviceProvisionedInSettingsDb();
        this.mStrongAuthTracker = new StrongAuthTracker(context, new Consumer() {
            public final void accept(Object obj) {
                KeyguardUpdateMonitor.this.notifyStrongAuthStateChanged(((Integer) obj).intValue());
            }
        });
        this.mBackgroundExecutor = executor;
        this.mBroadcastDispatcher = broadcastDispatcher;
        dumpManager.registerDumpable(KeyguardUpdateMonitor.class.getName(), this);
        this.mHandler = new Handler(looper) {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 301:
                        KeyguardUpdateMonitor.this.handleTimeUpdate();
                        return;
                    case 302:
                        KeyguardUpdateMonitor.this.handleBatteryUpdate((BatteryStatus) message.obj);
                        return;
                    case 304:
                        KeyguardUpdateMonitor.this.handleSimStateChange(message.arg1, message.arg2, ((Integer) message.obj).intValue());
                        return;
                    case 305:
                        KeyguardUpdateMonitor.this.handleRingerModeChange(message.arg1);
                        return;
                    case 306:
                        KeyguardUpdateMonitor.this.handlePhoneStateChanged((String) message.obj);
                        return;
                    case 308:
                        KeyguardUpdateMonitor.this.handleDeviceProvisioned();
                        return;
                    case 309:
                        KeyguardUpdateMonitor.this.handleDevicePolicyManagerStateChanged(message.arg1);
                        return;
                    case 310:
                        KeyguardUpdateMonitor.this.handleUserSwitching(message.arg1, (IRemoteCallback) message.obj);
                        return;
                    case 312:
                        KeyguardUpdateMonitor.this.handleKeyguardReset();
                        return;
                    case 314:
                        KeyguardUpdateMonitor.this.handleUserSwitchComplete(message.arg1);
                        return;
                    case 317:
                        KeyguardUpdateMonitor.this.handleUserInfoChanged(message.arg1);
                        return;
                    case 318:
                        KeyguardUpdateMonitor.this.handleReportEmergencyCallAction();
                        return;
                    case 319:
                        Trace.beginSection("KeyguardUpdateMonitor#handler MSG_STARTED_WAKING_UP");
                        KeyguardUpdateMonitor.this.handleStartedWakingUp();
                        Trace.endSection();
                        return;
                    case 320:
                        KeyguardUpdateMonitor.this.handleFinishedGoingToSleep(message.arg1);
                        return;
                    case 321:
                        KeyguardUpdateMonitor.this.handleStartedGoingToSleep(message.arg1);
                        return;
                    case 322:
                        KeyguardUpdateMonitor.this.handleKeyguardBouncerChanged(message.arg1);
                        return;
                    case 327:
                        Trace.beginSection("KeyguardUpdateMonitor#handler MSG_FACE_UNLOCK_STATE_CHANGED");
                        KeyguardUpdateMonitor.this.handleFaceUnlockStateChanged(message.arg1 != 0, message.arg2);
                        Trace.endSection();
                        return;
                    case 328:
                        KeyguardUpdateMonitor.this.handleSimSubscriptionInfoChanged();
                        return;
                    case 329:
                        KeyguardUpdateMonitor.this.handleAirplaneModeChanged();
                        return;
                    case 330:
                        KeyguardUpdateMonitor.this.handleServiceStateChange(message.arg1, (ServiceState) message.obj);
                        return;
                    case 331:
                        KeyguardUpdateMonitor.this.handleScreenTurnedOn();
                        return;
                    case 332:
                        Trace.beginSection("KeyguardUpdateMonitor#handler MSG_SCREEN_TURNED_ON");
                        KeyguardUpdateMonitor.this.handleScreenTurnedOff();
                        Trace.endSection();
                        return;
                    case 333:
                        KeyguardUpdateMonitor.this.handleDreamingStateChanged(message.arg1);
                        return;
                    case 334:
                        KeyguardUpdateMonitor.this.handleUserUnlocked(message.arg1);
                        return;
                    case 335:
                        KeyguardUpdateMonitor.this.setAssistantVisible(((Boolean) message.obj).booleanValue());
                        return;
                    case 336:
                        KeyguardUpdateMonitor.this.updateBiometricListeningState();
                        return;
                    case 337:
                        KeyguardUpdateMonitor.this.updateLogoutEnabled();
                        return;
                    case 338:
                        KeyguardUpdateMonitor.this.updateTelephonyCapable(((Boolean) message.obj).booleanValue());
                        return;
                    case 339:
                        KeyguardUpdateMonitor.this.handleTimeZoneUpdate((String) message.obj);
                        return;
                    case 340:
                        KeyguardUpdateMonitor.this.handleUserStopped(message.arg1);
                        return;
                    case 341:
                        KeyguardUpdateMonitor.this.handleUserRemoved(message.arg1);
                        return;
                    default:
                        super.handleMessage(message);
                        return;
                }
            }
        };
        if (!this.mDeviceProvisioned) {
            watchForDeviceProvisioning();
        }
        BatteryStatus batteryStatus = new BatteryStatus(1, 100, 0, 0, 0);
        this.mBatteryStatus = batteryStatus;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_TICK");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.SERVICE_STATE");
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.media.RINGER_MODE_CHANGED");
        String str = "android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED";
        intentFilter.addAction(str);
        this.mBroadcastDispatcher.registerReceiverWithHandler(this.mBroadcastReceiver, intentFilter, this.mHandler);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.USER_INFO_CHANGED");
        intentFilter2.addAction("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
        intentFilter2.addAction("com.android.facelock.FACE_UNLOCK_STARTED");
        intentFilter2.addAction("com.android.facelock.FACE_UNLOCK_STOPPED");
        intentFilter2.addAction(str);
        intentFilter2.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter2.addAction("android.intent.action.USER_STOPPED");
        intentFilter2.addAction("android.intent.action.USER_REMOVED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this.mBroadcastAllReceiver, intentFilter2, this.mHandler, UserHandle.ALL);
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mSubscriptionListener);
        try {
            ActivityManager.getService().registerUserSwitchObserver(this.mUserSwitchObserver, "KeyguardUpdateMonitor");
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        }
        TrustManager trustManager = (TrustManager) context.getSystemService(TrustManager.class);
        this.mTrustManager = trustManager;
        trustManager.registerTrustListener(this);
        LockPatternUtils lockPatternUtils = new LockPatternUtils(context);
        this.mLockPatternUtils = lockPatternUtils;
        lockPatternUtils.registerStrongAuthTracker(this.mStrongAuthTracker);
        this.mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.fingerprint")) {
            this.mFpm = (FingerprintManager) context.getSystemService("fingerprint");
        }
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            this.mFaceManager = (FaceManager) context.getSystemService("face");
        }
        if (!(this.mFpm == null && this.mFaceManager == null)) {
            BiometricManager biometricManager = (BiometricManager) context.getSystemService(BiometricManager.class);
            this.mBiometricManager = biometricManager;
            biometricManager.registerEnabledOnKeyguardCallback(this.mBiometricEnabledCallback);
        }
        updateBiometricListeningState();
        FingerprintManager fingerprintManager = this.mFpm;
        if (fingerprintManager != null) {
            fingerprintManager.addLockoutResetCallback(this.mFingerprintLockoutResetCallback);
        }
        FaceManager faceManager = this.mFaceManager;
        if (faceManager != null) {
            faceManager.addLockoutResetCallback(this.mFaceLockoutResetCallback);
        }
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        this.mUserManager = userManager;
        this.mIsPrimaryUser = userManager.isPrimaryUser();
        int currentUser = ActivityManager.getCurrentUser();
        this.mUserIsUnlocked.put(currentUser, this.mUserManager.isUserUnlocked(currentUser));
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        this.mDevicePolicyManager = devicePolicyManager;
        this.mLogoutEnabled = devicePolicyManager.isLogoutEnabled();
        updateSecondaryLockscreenRequirement(currentUser);
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            SparseBooleanArray sparseBooleanArray = this.mUserTrustIsUsuallyManaged;
            int i = userInfo.id;
            sparseBooleanArray.put(i, this.mTrustManager.isTrustUsuallyManaged(i));
        }
        updateAirplaneModeState();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager != null) {
            telephonyManager.listen(this.mPhoneStateListener, 4194304);
        }
    }

    private void updateAirplaneModeState() {
        if (WirelessUtils.isAirplaneModeOn(this.mContext) && !this.mHandler.hasMessages(329)) {
            this.mHandler.sendEmptyMessage(329);
        }
    }

    /* access modifiers changed from: private */
    public void updateBiometricListeningState() {
        updateFingerprintListeningState();
        updateFaceListeningState();
    }

    /* access modifiers changed from: private */
    public void updateFingerprintListeningState() {
        if (!this.mHandler.hasMessages(336)) {
            this.mHandler.removeCallbacks(this.mRetryFingerprintAuthentication);
            boolean shouldListenForFingerprint = shouldListenForFingerprint();
            int i = this.mFingerprintRunningState;
            boolean z = true;
            if (!(i == 1 || i == 3)) {
                z = false;
            }
            if (z && !shouldListenForFingerprint) {
                stopListeningForFingerprint();
            } else if (!z && shouldListenForFingerprint) {
                startListeningForFingerprint();
            }
        }
    }

    public boolean isUserUnlocked(int i) {
        return this.mUserIsUnlocked.get(i);
    }

    public void onAuthInterruptDetected(boolean z) {
        if (this.mAuthInterruptActive != z) {
            this.mAuthInterruptActive = z;
            updateFaceListeningState();
        }
    }

    public void requestFaceAuth() {
        updateFaceListeningState();
    }

    public void cancelFaceAuth() {
        stopListeningForFace();
    }

    /* access modifiers changed from: private */
    public void updateFaceListeningState() {
        if (!this.mHandler.hasMessages(336)) {
            this.mHandler.removeCallbacks(this.mRetryFaceAuthentication);
            boolean shouldListenForFace = shouldListenForFace();
            if (this.mFaceRunningState == 1 && !shouldListenForFace) {
                stopListeningForFace();
            } else if (this.mFaceRunningState != 1 && shouldListenForFace) {
                startListeningForFace();
            }
        }
    }

    private boolean shouldListenForFingerprintAssistant() {
        BiometricAuthenticated biometricAuthenticated = (BiometricAuthenticated) this.mUserFingerprintAuthenticated.get(getCurrentUser());
        if (!this.mAssistantVisible || !this.mKeyguardOccluded) {
            return false;
        }
        if ((biometricAuthenticated == null || !biometricAuthenticated.mAuthenticated) && !this.mUserHasTrust.get(getCurrentUser(), false)) {
            return true;
        }
        return false;
    }

    private boolean shouldListenForFaceAssistant() {
        BiometricAuthenticated biometricAuthenticated = (BiometricAuthenticated) this.mUserFaceAuthenticated.get(getCurrentUser());
        if (!this.mAssistantVisible || !this.mKeyguardOccluded) {
            return false;
        }
        if ((biometricAuthenticated == null || !biometricAuthenticated.mAuthenticated) && !this.mUserHasTrust.get(getCurrentUser(), false)) {
            return true;
        }
        return false;
    }

    private boolean shouldListenForFingerprint() {
        return (this.mKeyguardIsVisible || !this.mDeviceInteractive || ((this.mBouncer && !this.mKeyguardGoingAway) || this.mGoingToSleep || shouldListenForFingerprintAssistant() || (this.mKeyguardOccluded && this.mIsDreaming))) && !this.mSwitchingUser && !isFingerprintDisabled(getCurrentUser()) && (!this.mKeyguardGoingAway || !this.mDeviceInteractive) && this.mIsPrimaryUser;
    }

    public boolean shouldListenForFace() {
        boolean z = this.mKeyguardIsVisible && this.mDeviceInteractive && !this.mGoingToSleep;
        int currentUser = getCurrentUser();
        int strongAuthForUser = this.mStrongAuthTracker.getStrongAuthForUser(currentUser);
        boolean z2 = containsFlag(strongAuthForUser, 2) || containsFlag(strongAuthForUser, 32);
        boolean z3 = containsFlag(strongAuthForUser, 1) || containsFlag(strongAuthForUser, 16);
        KeyguardBypassController keyguardBypassController = this.mKeyguardBypassController;
        boolean z4 = keyguardBypassController != null && keyguardBypassController.canBypass();
        boolean z5 = !getUserCanSkipBouncer(currentUser) || z4;
        boolean z6 = (!z3 || (z4 && !this.mBouncer)) && !z2;
        if ((this.mBouncer || this.mAuthInterruptActive || z || shouldListenForFaceAssistant()) && !this.mSwitchingUser && !isFaceDisabled(currentUser) && z5 && !this.mKeyguardGoingAway && this.mFaceSettingEnabledForUser.get(currentUser) && !this.mLockIconPressed && z6 && this.mIsPrimaryUser && !this.mSecureCameraLaunched) {
            return true;
        }
        return false;
    }

    public void onLockIconPressed() {
        this.mLockIconPressed = true;
        int currentUser = getCurrentUser();
        this.mUserFaceAuthenticated.put(currentUser, null);
        updateFaceListeningState();
        this.mStrongAuthTracker.onStrongAuthRequiredChanged(currentUser);
    }

    private void startListeningForFingerprint() {
        int i = this.mFingerprintRunningState;
        if (i == 2) {
            setFingerprintRunningState(3);
        } else if (i != 3) {
            int currentUser = getCurrentUser();
            if (isUnlockWithFingerprintPossible(currentUser)) {
                CancellationSignal cancellationSignal = this.mFingerprintCancelSignal;
                if (cancellationSignal != null) {
                    cancellationSignal.cancel();
                }
                CancellationSignal cancellationSignal2 = new CancellationSignal();
                this.mFingerprintCancelSignal = cancellationSignal2;
                this.mFpm.authenticate(null, cancellationSignal2, 0, this.mFingerprintAuthenticationCallback, null, currentUser);
                setFingerprintRunningState(1);
            }
        }
    }

    private void startListeningForFace() {
        if (this.mFaceRunningState == 2) {
            setFaceRunningState(3);
            return;
        }
        int currentUser = getCurrentUser();
        if (isUnlockWithFacePossible(currentUser)) {
            CancellationSignal cancellationSignal = this.mFaceCancelSignal;
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
            }
            CancellationSignal cancellationSignal2 = new CancellationSignal();
            this.mFaceCancelSignal = cancellationSignal2;
            this.mFaceManager.authenticate(null, cancellationSignal2, 0, this.mFaceAuthenticationCallback, null, currentUser);
            setFaceRunningState(1);
        }
    }

    public boolean isUnlockingWithBiometricsPossible(int i) {
        return isUnlockWithFacePossible(i) || isUnlockWithFingerprintPossible(i);
    }

    private boolean isUnlockWithFingerprintPossible(int i) {
        FingerprintManager fingerprintManager = this.mFpm;
        return fingerprintManager != null && fingerprintManager.isHardwareDetected() && !isFingerprintDisabled(i) && this.mFpm.getEnrolledFingerprints(i).size() > 0;
    }

    private boolean isUnlockWithFacePossible(int i) {
        return isFaceAuthEnabledForUser(i) && !isFaceDisabled(i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$isFaceAuthEnabledForUser$1 */
    public /* synthetic */ Boolean lambda$isFaceAuthEnabledForUser$1$KeyguardUpdateMonitor(int i) {
        FaceManager faceManager = this.mFaceManager;
        return Boolean.valueOf(faceManager != null && faceManager.isHardwareDetected() && this.mFaceManager.hasEnrolledTemplates(i) && this.mFaceSettingEnabledForUser.get(i));
    }

    public boolean isFaceAuthEnabledForUser(int i) {
        return ((Boolean) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final Object get() {
                return KeyguardUpdateMonitor.this.lambda$isFaceAuthEnabledForUser$1$KeyguardUpdateMonitor(this.f$1);
            }
        })).booleanValue();
    }

    private void stopListeningForFingerprint() {
        if (this.mFingerprintRunningState == 1) {
            CancellationSignal cancellationSignal = this.mFingerprintCancelSignal;
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
                this.mFingerprintCancelSignal = null;
                if (!this.mHandler.hasCallbacks(this.mCancelNotReceived)) {
                    this.mHandler.postDelayed(this.mCancelNotReceived, 3000);
                }
            }
            setFingerprintRunningState(2);
        }
        if (this.mFingerprintRunningState == 3) {
            setFingerprintRunningState(2);
        }
    }

    private void stopListeningForFace() {
        if (this.mFaceRunningState == 1) {
            CancellationSignal cancellationSignal = this.mFaceCancelSignal;
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
                this.mFaceCancelSignal = null;
                if (!this.mHandler.hasCallbacks(this.mCancelNotReceived)) {
                    this.mHandler.postDelayed(this.mCancelNotReceived, 3000);
                }
            }
            setFaceRunningState(2);
        }
        if (this.mFaceRunningState == 3) {
            setFaceRunningState(2);
        }
    }

    /* access modifiers changed from: private */
    public boolean isDeviceProvisionedInSettingsDb() {
        return Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
    }

    private void watchForDeviceProvisioning() {
        this.mDeviceProvisionedObserver = new ContentObserver(this.mHandler) {
            public void onChange(boolean z) {
                super.onChange(z);
                KeyguardUpdateMonitor keyguardUpdateMonitor = KeyguardUpdateMonitor.this;
                keyguardUpdateMonitor.mDeviceProvisioned = keyguardUpdateMonitor.isDeviceProvisionedInSettingsDb();
                if (KeyguardUpdateMonitor.this.mDeviceProvisioned) {
                    KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(308);
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Global.getUriFor("device_provisioned"), false, this.mDeviceProvisionedObserver);
        boolean isDeviceProvisionedInSettingsDb = isDeviceProvisionedInSettingsDb();
        if (isDeviceProvisionedInSettingsDb != this.mDeviceProvisioned) {
            this.mDeviceProvisioned = isDeviceProvisionedInSettingsDb;
            if (isDeviceProvisionedInSettingsDb) {
                this.mHandler.sendEmptyMessage(308);
            }
        }
    }

    public void setHasLockscreenWallpaper(boolean z) {
        Assert.isMainThread();
        if (z != this.mHasLockscreenWallpaper) {
            this.mHasLockscreenWallpaper = z;
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onHasLockscreenWallpaperChanged(z);
                }
            }
        }
    }

    public boolean hasLockscreenWallpaper() {
        return this.mHasLockscreenWallpaper;
    }

    /* access modifiers changed from: private */
    public void handleDevicePolicyManagerStateChanged(int i) {
        Assert.isMainThread();
        updateFingerprintListeningState();
        updateSecondaryLockscreenRequirement(i);
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onDevicePolicyManagerStateChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleUserSwitching(int i, IRemoteCallback iRemoteCallback) {
        Assert.isMainThread();
        this.mUserTrustIsUsuallyManaged.put(i, this.mTrustManager.isTrustUsuallyManaged(i));
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onUserSwitching(i);
            }
        }
        try {
            iRemoteCallback.sendResult(null);
        } catch (RemoteException unused) {
        }
    }

    /* access modifiers changed from: private */
    public void handleUserSwitchComplete(int i) {
        Assert.isMainThread();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onUserSwitchComplete(i);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleDeviceProvisioned() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onDeviceProvisioned();
            }
        }
        if (this.mDeviceProvisionedObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mDeviceProvisionedObserver);
            this.mDeviceProvisionedObserver = null;
        }
    }

    /* access modifiers changed from: private */
    public void handlePhoneStateChanged(String str) {
        Assert.isMainThread();
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(str)) {
            this.mPhoneState = 0;
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(str)) {
            this.mPhoneState = 2;
        } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(str)) {
            this.mPhoneState = 1;
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onPhoneStateChanged(this.mPhoneState);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleRingerModeChange(int i) {
        Assert.isMainThread();
        this.mRingMode = i;
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onRingerModeChanged(i);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleTimeUpdate() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTimeChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleTimeZoneUpdate(String str) {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTimeZoneChanged(TimeZone.getTimeZone(str));
                keyguardUpdateMonitorCallback.onTimeChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleBatteryUpdate(BatteryStatus batteryStatus) {
        Assert.isMainThread();
        boolean isBatteryUpdateInteresting = isBatteryUpdateInteresting(this.mBatteryStatus, batteryStatus);
        this.mBatteryStatus = batteryStatus;
        if (isBatteryUpdateInteresting) {
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onRefreshBatteryInfo(batteryStatus);
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void updateTelephonyCapable(boolean z) {
        Assert.isMainThread();
        if (z != this.mTelephonyCapable) {
            this.mTelephonyCapable = z;
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onTelephonyCapable(this.mTelephonyCapable);
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00a8  */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleSimStateChange(int r7, int r8, int r9) {
        /*
            r6 = this;
            com.android.systemui.util.Assert.isMainThread()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "handleSimStateChange(subId="
            r0.append(r1)
            r0.append(r7)
            java.lang.String r1 = ", slotId="
            r0.append(r1)
            r0.append(r8)
            java.lang.String r1 = ", state="
            r0.append(r1)
            r0.append(r9)
            java.lang.String r1 = ")"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "KeyguardUpdateMonitor"
            android.util.Log.d(r1, r0)
            boolean r0 = android.telephony.SubscriptionManager.isValidSubscriptionId(r7)
            r2 = 0
            r3 = 1
            if (r0 != 0) goto L_0x0068
            java.lang.String r0 = "invalid subId in handleSimStateChange()"
            android.util.Log.w(r1, r0)
            if (r9 != r3) goto L_0x005f
            r6.updateTelephonyCapable(r3)
            java.util.HashMap<java.lang.Integer, com.android.keyguard.KeyguardUpdateMonitor$SimData> r0 = r6.mSimDatas
            java.util.Collection r0 = r0.values()
            java.util.Iterator r0 = r0.iterator()
        L_0x004a:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x005d
            java.lang.Object r1 = r0.next()
            com.android.keyguard.KeyguardUpdateMonitor$SimData r1 = (com.android.keyguard.KeyguardUpdateMonitor.SimData) r1
            int r4 = r1.slotId
            if (r4 != r8) goto L_0x004a
            r1.simState = r3
            goto L_0x004a
        L_0x005d:
            r0 = r3
            goto L_0x0069
        L_0x005f:
            r0 = 8
            if (r9 != r0) goto L_0x0067
            r6.updateTelephonyCapable(r3)
            goto L_0x0068
        L_0x0067:
            return
        L_0x0068:
            r0 = r2
        L_0x0069:
            java.util.HashMap<java.lang.Integer, com.android.keyguard.KeyguardUpdateMonitor$SimData> r1 = r6.mSimDatas
            java.lang.Integer r4 = java.lang.Integer.valueOf(r7)
            java.lang.Object r1 = r1.get(r4)
            com.android.keyguard.KeyguardUpdateMonitor$SimData r1 = (com.android.keyguard.KeyguardUpdateMonitor.SimData) r1
            if (r1 != 0) goto L_0x0086
            com.android.keyguard.KeyguardUpdateMonitor$SimData r1 = new com.android.keyguard.KeyguardUpdateMonitor$SimData
            r1.<init>(r9, r8, r7)
            java.util.HashMap<java.lang.Integer, com.android.keyguard.KeyguardUpdateMonitor$SimData> r4 = r6.mSimDatas
            java.lang.Integer r5 = java.lang.Integer.valueOf(r7)
            r4.put(r5, r1)
            goto L_0x009a
        L_0x0086:
            int r4 = r1.simState
            if (r4 != r9) goto L_0x0094
            int r4 = r1.subId
            if (r4 != r7) goto L_0x0094
            int r4 = r1.slotId
            if (r4 == r8) goto L_0x0093
            goto L_0x0094
        L_0x0093:
            r3 = r2
        L_0x0094:
            r1.simState = r9
            r1.subId = r7
            r1.slotId = r8
        L_0x009a:
            if (r3 != 0) goto L_0x009e
            if (r0 == 0) goto L_0x00be
        L_0x009e:
            if (r9 == 0) goto L_0x00be
        L_0x00a0:
            java.util.ArrayList<java.lang.ref.WeakReference<com.android.keyguard.KeyguardUpdateMonitorCallback>> r0 = r6.mCallbacks
            int r0 = r0.size()
            if (r2 >= r0) goto L_0x00be
            java.util.ArrayList<java.lang.ref.WeakReference<com.android.keyguard.KeyguardUpdateMonitorCallback>> r0 = r6.mCallbacks
            java.lang.Object r0 = r0.get(r2)
            java.lang.ref.WeakReference r0 = (java.lang.ref.WeakReference) r0
            java.lang.Object r0 = r0.get()
            com.android.keyguard.KeyguardUpdateMonitorCallback r0 = (com.android.keyguard.KeyguardUpdateMonitorCallback) r0
            if (r0 == 0) goto L_0x00bb
            r0.onSimStateChanged(r7, r8, r9)
        L_0x00bb:
            int r2 = r2 + 1
            goto L_0x00a0
        L_0x00be:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardUpdateMonitor.handleSimStateChange(int, int, int):void");
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void handleServiceStateChange(int i, ServiceState serviceState) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            Log.w("KeyguardUpdateMonitor", "invalid subId in handleServiceStateChange()");
            return;
        }
        updateTelephonyCapable(true);
        this.mServiceStates.put(Integer.valueOf(i), serviceState);
        callbacksRefreshCarrierInfo();
    }

    public boolean isKeyguardVisible() {
        return this.mKeyguardIsVisible;
    }

    public void onKeyguardVisibilityChanged(boolean z) {
        Assert.isMainThread();
        StringBuilder sb = new StringBuilder();
        sb.append("onKeyguardVisibilityChanged(");
        sb.append(z);
        sb.append(")");
        Log.d("KeyguardUpdateMonitor", sb.toString());
        this.mKeyguardIsVisible = z;
        if (z) {
            this.mSecureCameraLaunched = false;
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onKeyguardVisibilityChangedRaw(z);
            }
        }
        updateBiometricListeningState();
    }

    /* access modifiers changed from: private */
    public void handleKeyguardReset() {
        updateBiometricListeningState();
        this.mNeedsSlowUnlockTransition = resolveNeedsSlowUnlockTransition();
    }

    private boolean resolveNeedsSlowUnlockTransition() {
        if (isUserUnlocked(getCurrentUser())) {
            return false;
        }
        return FALLBACK_HOME_COMPONENT.equals(this.mContext.getPackageManager().resolveActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 0).getComponentInfo().getComponentName());
    }

    /* access modifiers changed from: private */
    public void handleKeyguardBouncerChanged(int i) {
        Assert.isMainThread();
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        this.mBouncer = z;
        if (z) {
            this.mSecureCameraLaunched = false;
        }
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i2)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onKeyguardBouncerChanged(z);
            }
        }
        updateBiometricListeningState();
    }

    /* access modifiers changed from: private */
    public void handleReportEmergencyCallAction() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onEmergencyCallAction();
            }
        }
    }

    private boolean isBatteryUpdateInteresting(BatteryStatus batteryStatus, BatteryStatus batteryStatus2) {
        boolean isPluggedIn = batteryStatus2.isPluggedIn();
        boolean isPluggedIn2 = batteryStatus.isPluggedIn();
        boolean z = isPluggedIn2 && isPluggedIn && batteryStatus.status != batteryStatus2.status;
        if (isPluggedIn2 == isPluggedIn && !z && batteryStatus.level == batteryStatus2.level) {
            return isPluggedIn && batteryStatus2.maxChargingWattage != batteryStatus.maxChargingWattage;
        }
        return true;
    }

    public void removeCallback(KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback) {
        Assert.isMainThread();
        this.mCallbacks.removeIf(new Predicate() {
            public final boolean test(Object obj) {
                return KeyguardUpdateMonitor.lambda$removeCallback$2(KeyguardUpdateMonitorCallback.this, (WeakReference) obj);
            }
        });
    }

    static /* synthetic */ boolean lambda$removeCallback$2(KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback, WeakReference weakReference) {
        return weakReference.get() == keyguardUpdateMonitorCallback;
    }

    public void registerCallback(KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback) {
        Assert.isMainThread();
        int i = 0;
        while (i < this.mCallbacks.size()) {
            if (((WeakReference) this.mCallbacks.get(i)).get() != keyguardUpdateMonitorCallback) {
                i++;
            } else {
                return;
            }
        }
        this.mCallbacks.add(new WeakReference(keyguardUpdateMonitorCallback));
        removeCallback(null);
        sendUpdates(keyguardUpdateMonitorCallback);
    }

    public void setKeyguardBypassController(KeyguardBypassController keyguardBypassController) {
        this.mKeyguardBypassController = keyguardBypassController;
    }

    public boolean isSwitchingUser() {
        return this.mSwitchingUser;
    }

    public void setSwitchingUser(boolean z) {
        this.mSwitchingUser = z;
        this.mHandler.post(this.mUpdateBiometricListeningState);
    }

    private void sendUpdates(KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback) {
        keyguardUpdateMonitorCallback.onRefreshBatteryInfo(this.mBatteryStatus);
        keyguardUpdateMonitorCallback.onTimeChanged();
        keyguardUpdateMonitorCallback.onRingerModeChanged(this.mRingMode);
        keyguardUpdateMonitorCallback.onPhoneStateChanged(this.mPhoneState);
        keyguardUpdateMonitorCallback.onRefreshCarrierInfo();
        keyguardUpdateMonitorCallback.onClockVisibilityChanged();
        keyguardUpdateMonitorCallback.onKeyguardVisibilityChangedRaw(this.mKeyguardIsVisible);
        keyguardUpdateMonitorCallback.onTelephonyCapable(this.mTelephonyCapable);
        for (Entry value : this.mSimDatas.entrySet()) {
            SimData simData = (SimData) value.getValue();
            keyguardUpdateMonitorCallback.onSimStateChanged(simData.subId, simData.slotId, simData.simState);
        }
    }

    public void sendKeyguardReset() {
        this.mHandler.obtainMessage(312).sendToTarget();
    }

    public void sendKeyguardBouncerChanged(boolean z) {
        Message obtainMessage = this.mHandler.obtainMessage(322);
        obtainMessage.arg1 = z ? 1 : 0;
        obtainMessage.sendToTarget();
    }

    public void reportSimUnlocked(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("reportSimUnlocked(subId=");
        sb.append(i);
        sb.append(")");
        Log.v("KeyguardUpdateMonitor", sb.toString());
        handleSimStateChange(i, getSlotId(i), 5);
    }

    public void reportEmergencyCallAction(boolean z) {
        if (!z) {
            this.mHandler.obtainMessage(318).sendToTarget();
            return;
        }
        Assert.isMainThread();
        handleReportEmergencyCallAction();
    }

    public boolean isDeviceProvisioned() {
        return this.mDeviceProvisioned;
    }

    public ServiceState getServiceState(int i) {
        return (ServiceState) this.mServiceStates.get(Integer.valueOf(i));
    }

    public void clearBiometricRecognized() {
        Assert.isMainThread();
        this.mUserFingerprintAuthenticated.clear();
        this.mUserFaceAuthenticated.clear();
        this.mTrustManager.clearAllBiometricRecognized(BiometricSourceType.FINGERPRINT);
        this.mTrustManager.clearAllBiometricRecognized(BiometricSourceType.FACE);
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricsCleared();
            }
        }
    }

    public boolean isSimPinVoiceSecure() {
        return isSimPinSecure();
    }

    public boolean isSimPinSecure() {
        for (SubscriptionInfo subscriptionId : getSubscriptionInfo(false)) {
            if (isSimPinSecure(getSimState(subscriptionId.getSubscriptionId()))) {
                return true;
            }
        }
        return false;
    }

    public int getSimState(int i) {
        if (this.mSimDatas.containsKey(Integer.valueOf(i))) {
            return ((SimData) this.mSimDatas.get(Integer.valueOf(i))).simState;
        }
        return 0;
    }

    private int getSlotId(int i) {
        if (!this.mSimDatas.containsKey(Integer.valueOf(i))) {
            refreshSimState(i, SubscriptionManager.getSlotIndex(i));
        }
        return ((SimData) this.mSimDatas.get(Integer.valueOf(i))).slotId;
    }

    private boolean refreshSimState(int i, int i2) {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        boolean z = false;
        int simState = telephonyManager != null ? telephonyManager.getSimState(i2) : 0;
        SimData simData = (SimData) this.mSimDatas.get(Integer.valueOf(i));
        if (simData == null) {
            this.mSimDatas.put(Integer.valueOf(i), new SimData(simState, i2, i));
            return true;
        }
        if (simData.simState != simState) {
            z = true;
        }
        simData.simState = simState;
        return z;
    }

    public void dispatchStartedWakingUp() {
        synchronized (this) {
            this.mDeviceInteractive = true;
        }
        this.mHandler.sendEmptyMessage(319);
    }

    public void dispatchStartedGoingToSleep(int i) {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(321, i, 0));
    }

    public void dispatchFinishedGoingToSleep(int i) {
        synchronized (this) {
            this.mDeviceInteractive = false;
        }
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(320, i, 0));
    }

    public void dispatchScreenTurnedOn() {
        synchronized (this) {
            this.mScreenOn = true;
        }
        this.mHandler.sendEmptyMessage(331);
    }

    public void dispatchScreenTurnedOff() {
        synchronized (this) {
            this.mScreenOn = false;
        }
        this.mHandler.sendEmptyMessage(332);
    }

    public void dispatchDreamingStarted() {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(333, 1, 0));
    }

    public void dispatchDreamingStopped() {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(333, 0, 0));
    }

    public boolean isDeviceInteractive() {
        return this.mDeviceInteractive;
    }

    public boolean isGoingToSleep() {
        return this.mGoingToSleep;
    }

    public int getNextSubIdForState(int i) {
        List subscriptionInfo = getSubscriptionInfo(false);
        int i2 = -1;
        int i3 = Integer.MAX_VALUE;
        for (int i4 = 0; i4 < subscriptionInfo.size(); i4++) {
            int subscriptionId = ((SubscriptionInfo) subscriptionInfo.get(i4)).getSubscriptionId();
            int slotId = getSlotId(subscriptionId);
            if (i == getSimState(subscriptionId) && i3 > slotId) {
                i2 = subscriptionId;
                i3 = slotId;
            }
        }
        return i2;
    }

    public SubscriptionInfo getSubscriptionInfoForSubId(int i) {
        List subscriptionInfo = getSubscriptionInfo(false);
        for (int i2 = 0; i2 < subscriptionInfo.size(); i2++) {
            SubscriptionInfo subscriptionInfo2 = (SubscriptionInfo) subscriptionInfo.get(i2);
            if (i == subscriptionInfo2.getSubscriptionId()) {
                return subscriptionInfo2;
            }
        }
        return null;
    }

    public boolean isLogoutEnabled() {
        return this.mLogoutEnabled;
    }

    /* access modifiers changed from: private */
    public void updateLogoutEnabled() {
        Assert.isMainThread();
        boolean isLogoutEnabled = this.mDevicePolicyManager.isLogoutEnabled();
        if (this.mLogoutEnabled != isLogoutEnabled) {
            this.mLogoutEnabled = isLogoutEnabled;
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) this.mCallbacks.get(i)).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onLogoutEnabledChanged();
                }
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        PrintWriter printWriter2 = printWriter;
        printWriter2.println("KeyguardUpdateMonitor state:");
        printWriter2.println("  SIM States:");
        Iterator it = this.mSimDatas.values().iterator();
        while (true) {
            str = "    ";
            if (!it.hasNext()) {
                break;
            }
            SimData simData = (SimData) it.next();
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(simData.toString());
            printWriter2.println(sb.toString());
        }
        printWriter2.println("  Subs:");
        if (this.mSubscriptionInfo != null) {
            for (int i = 0; i < this.mSubscriptionInfo.size(); i++) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append(this.mSubscriptionInfo.get(i));
                printWriter2.println(sb2.toString());
            }
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  Current active data subId=");
        sb3.append(this.mActiveMobileDataSubscription);
        printWriter2.println(sb3.toString());
        printWriter2.println("  Service states:");
        for (Integer intValue : this.mServiceStates.keySet()) {
            int intValue2 = intValue.intValue();
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str);
            sb4.append(intValue2);
            sb4.append("=");
            sb4.append(this.mServiceStates.get(Integer.valueOf(intValue2)));
            printWriter2.println(sb4.toString());
        }
        FingerprintManager fingerprintManager = this.mFpm;
        String str2 = "    trustManaged=";
        String str3 = "    strongAuthFlags=";
        String str4 = "    possible=";
        String str5 = "    disabled(DPM)=";
        String str6 = "    authSinceBoot=";
        String str7 = "    auth'd=";
        String str8 = "    allowed=";
        String str9 = ")";
        boolean z = true;
        if (fingerprintManager != null && fingerprintManager.isHardwareDetected()) {
            int currentUser = ActivityManager.getCurrentUser();
            int strongAuthForUser = this.mStrongAuthTracker.getStrongAuthForUser(currentUser);
            BiometricAuthenticated biometricAuthenticated = (BiometricAuthenticated) this.mUserFingerprintAuthenticated.get(currentUser);
            StringBuilder sb5 = new StringBuilder();
            sb5.append("  Fingerprint state (user=");
            sb5.append(currentUser);
            sb5.append(str9);
            printWriter2.println(sb5.toString());
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str8);
            sb6.append(biometricAuthenticated != null && isUnlockingWithBiometricAllowed(biometricAuthenticated.mIsStrongBiometric));
            printWriter2.println(sb6.toString());
            StringBuilder sb7 = new StringBuilder();
            sb7.append(str7);
            sb7.append(biometricAuthenticated != null && biometricAuthenticated.mAuthenticated);
            printWriter2.println(sb7.toString());
            StringBuilder sb8 = new StringBuilder();
            sb8.append(str6);
            sb8.append(getStrongAuthTracker().hasUserAuthenticatedSinceBoot());
            printWriter2.println(sb8.toString());
            StringBuilder sb9 = new StringBuilder();
            sb9.append(str5);
            sb9.append(isFingerprintDisabled(currentUser));
            printWriter2.println(sb9.toString());
            StringBuilder sb10 = new StringBuilder();
            sb10.append(str4);
            sb10.append(isUnlockWithFingerprintPossible(currentUser));
            printWriter2.println(sb10.toString());
            StringBuilder sb11 = new StringBuilder();
            sb11.append("    listening: actual=");
            sb11.append(this.mFingerprintRunningState);
            sb11.append(" expected=");
            sb11.append(shouldListenForFingerprint() ? 1 : 0);
            printWriter2.println(sb11.toString());
            StringBuilder sb12 = new StringBuilder();
            sb12.append(str3);
            sb12.append(Integer.toHexString(strongAuthForUser));
            printWriter2.println(sb12.toString());
            StringBuilder sb13 = new StringBuilder();
            sb13.append(str2);
            sb13.append(getUserTrustIsManaged(currentUser));
            printWriter2.println(sb13.toString());
        }
        FaceManager faceManager = this.mFaceManager;
        if (faceManager != null && faceManager.isHardwareDetected()) {
            int currentUser2 = ActivityManager.getCurrentUser();
            int strongAuthForUser2 = this.mStrongAuthTracker.getStrongAuthForUser(currentUser2);
            BiometricAuthenticated biometricAuthenticated2 = (BiometricAuthenticated) this.mUserFaceAuthenticated.get(currentUser2);
            StringBuilder sb14 = new StringBuilder();
            sb14.append("  Face authentication state (user=");
            sb14.append(currentUser2);
            sb14.append(str9);
            printWriter2.println(sb14.toString());
            StringBuilder sb15 = new StringBuilder();
            sb15.append(str8);
            sb15.append(biometricAuthenticated2 != null && isUnlockingWithBiometricAllowed(biometricAuthenticated2.mIsStrongBiometric));
            printWriter2.println(sb15.toString());
            StringBuilder sb16 = new StringBuilder();
            sb16.append(str7);
            if (biometricAuthenticated2 == null || !biometricAuthenticated2.mAuthenticated) {
                z = false;
            }
            sb16.append(z);
            printWriter2.println(sb16.toString());
            StringBuilder sb17 = new StringBuilder();
            sb17.append(str6);
            sb17.append(getStrongAuthTracker().hasUserAuthenticatedSinceBoot());
            printWriter2.println(sb17.toString());
            StringBuilder sb18 = new StringBuilder();
            sb18.append(str5);
            sb18.append(isFaceDisabled(currentUser2));
            printWriter2.println(sb18.toString());
            StringBuilder sb19 = new StringBuilder();
            sb19.append(str4);
            sb19.append(isUnlockWithFacePossible(currentUser2));
            printWriter2.println(sb19.toString());
            StringBuilder sb20 = new StringBuilder();
            sb20.append(str3);
            sb20.append(Integer.toHexString(strongAuthForUser2));
            printWriter2.println(sb20.toString());
            StringBuilder sb21 = new StringBuilder();
            sb21.append(str2);
            sb21.append(getUserTrustIsManaged(currentUser2));
            printWriter2.println(sb21.toString());
            StringBuilder sb22 = new StringBuilder();
            sb22.append("    enabledByUser=");
            sb22.append(this.mFaceSettingEnabledForUser.get(currentUser2));
            printWriter2.println(sb22.toString());
            StringBuilder sb23 = new StringBuilder();
            sb23.append("    mSecureCameraLaunched=");
            sb23.append(this.mSecureCameraLaunched);
            printWriter2.println(sb23.toString());
        }
    }
}
