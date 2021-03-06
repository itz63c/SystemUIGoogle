package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.biometrics.BiometricSourceType;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.LatencyTracker;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.ScreenLifecycle.Observer;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class BiometricUnlockController extends KeyguardUpdateMonitorCallback implements Dumpable {
    private final Context mContext;
    private final DozeParameters mDozeParameters;
    private DozeScrimController mDozeScrimController;
    private boolean mFadedAwayAfterWakeAndUnlock;
    private final Handler mHandler;
    /* access modifiers changed from: private */
    public boolean mHasScreenTurnedOnSinceAuthenticating;
    private final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardStateController mKeyguardStateController;
    private KeyguardViewMediator mKeyguardViewMediator;
    private final NotificationMediaManager mMediaManager;
    private final MetricsLogger mMetricsLogger;
    private int mMode;
    /* access modifiers changed from: private */
    public final NotificationShadeWindowController mNotificationShadeWindowController;
    private PendingAuthenticated mPendingAuthenticated = null;
    /* access modifiers changed from: private */
    public boolean mPendingShowBouncer;
    private final PowerManager mPowerManager;
    private final Runnable mReleaseBiometricWakeLockRunnable = new Runnable() {
        public void run() {
            Log.i("BiometricUnlockCtrl", "biometric wakelock: TIMEOUT!!");
            BiometricUnlockController.this.releaseBiometricWakeLock();
        }
    };
    private final Observer mScreenObserver = new Observer() {
        public void onScreenTurnedOn() {
            BiometricUnlockController.this.mHasScreenTurnedOnSinceAuthenticating = true;
        }
    };
    private ScrimController mScrimController;
    private final ShadeController mShadeController;
    private StatusBar mStatusBar;
    private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final KeyguardUpdateMonitor mUpdateMonitor;
    private WakeLock mWakeLock;
    private final int mWakeUpDelay;
    @VisibleForTesting
    final WakefulnessLifecycle.Observer mWakefulnessObserver = new WakefulnessLifecycle.Observer() {
        public void onFinishedWakingUp() {
            if (BiometricUnlockController.this.mPendingShowBouncer) {
                BiometricUnlockController.this.showBouncer();
            }
        }
    };

    /* renamed from: com.android.systemui.statusbar.phone.BiometricUnlockController$5 */
    static /* synthetic */ class C14465 {
        static final /* synthetic */ int[] $SwitchMap$android$hardware$biometrics$BiometricSourceType;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                android.hardware.biometrics.BiometricSourceType[] r0 = android.hardware.biometrics.BiometricSourceType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$android$hardware$biometrics$BiometricSourceType = r0
                android.hardware.biometrics.BiometricSourceType r1 = android.hardware.biometrics.BiometricSourceType.FINGERPRINT     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$android$hardware$biometrics$BiometricSourceType     // Catch:{ NoSuchFieldError -> 0x001d }
                android.hardware.biometrics.BiometricSourceType r1 = android.hardware.biometrics.BiometricSourceType.FACE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$android$hardware$biometrics$BiometricSourceType     // Catch:{ NoSuchFieldError -> 0x0028 }
                android.hardware.biometrics.BiometricSourceType r1 = android.hardware.biometrics.BiometricSourceType.IRIS     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.BiometricUnlockController.C14465.<clinit>():void");
        }
    }

    private static final class PendingAuthenticated {
        public final BiometricSourceType biometricSourceType;
        public final boolean isStrongBiometric;
        public final int userId;

        PendingAuthenticated(int i, BiometricSourceType biometricSourceType2, boolean z) {
            this.userId = i;
            this.biometricSourceType = biometricSourceType2;
            this.isStrongBiometric = z;
        }
    }

    public BiometricUnlockController(Context context, DozeScrimController dozeScrimController, KeyguardViewMediator keyguardViewMediator, ScrimController scrimController, StatusBar statusBar, ShadeController shadeController, NotificationShadeWindowController notificationShadeWindowController, KeyguardStateController keyguardStateController, Handler handler, KeyguardUpdateMonitor keyguardUpdateMonitor, Resources resources, KeyguardBypassController keyguardBypassController, DozeParameters dozeParameters, MetricsLogger metricsLogger, DumpManager dumpManager) {
        Context context2 = context;
        KeyguardUpdateMonitor keyguardUpdateMonitor2 = keyguardUpdateMonitor;
        KeyguardBypassController keyguardBypassController2 = keyguardBypassController;
        this.mContext = context2;
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mShadeController = shadeController;
        this.mUpdateMonitor = keyguardUpdateMonitor2;
        this.mDozeParameters = dozeParameters;
        keyguardUpdateMonitor.registerCallback(this);
        this.mMediaManager = (NotificationMediaManager) Dependency.get(NotificationMediaManager.class);
        ((WakefulnessLifecycle) Dependency.get(WakefulnessLifecycle.class)).addObserver(this.mWakefulnessObserver);
        ((ScreenLifecycle) Dependency.get(ScreenLifecycle.class)).addObserver(this.mScreenObserver);
        this.mNotificationShadeWindowController = notificationShadeWindowController;
        this.mDozeScrimController = dozeScrimController;
        this.mKeyguardViewMediator = keyguardViewMediator;
        this.mScrimController = scrimController;
        this.mStatusBar = statusBar;
        this.mKeyguardStateController = keyguardStateController;
        this.mHandler = handler;
        this.mWakeUpDelay = resources.getInteger(17694918);
        this.mKeyguardBypassController = keyguardBypassController2;
        keyguardBypassController2.setUnlockController(this);
        this.mMetricsLogger = metricsLogger;
        dumpManager.registerDumpable(BiometricUnlockController.class.getName(), this);
    }

    public void setStatusBarKeyguardViewManager(StatusBarKeyguardViewManager statusBarKeyguardViewManager) {
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
    }

    /* access modifiers changed from: private */
    public void releaseBiometricWakeLock() {
        if (this.mWakeLock != null) {
            this.mHandler.removeCallbacks(this.mReleaseBiometricWakeLockRunnable);
            Log.i("BiometricUnlockCtrl", "releasing biometric wakelock");
            this.mWakeLock.release();
            this.mWakeLock = null;
        }
    }

    public void onBiometricAcquired(BiometricSourceType biometricSourceType) {
        Trace.beginSection("BiometricUnlockController#onBiometricAcquired");
        releaseBiometricWakeLock();
        if (!this.mUpdateMonitor.isDeviceInteractive()) {
            if (LatencyTracker.isEnabled(this.mContext)) {
                int i = 2;
                if (biometricSourceType == BiometricSourceType.FACE) {
                    i = 6;
                }
                LatencyTracker.getInstance(this.mContext).onActionStart(i);
            }
            this.mWakeLock = this.mPowerManager.newWakeLock(1, "wake-and-unlock:wakelock");
            Trace.beginSection("acquiring wake-and-unlock");
            this.mWakeLock.acquire();
            Trace.endSection();
            Log.i("BiometricUnlockCtrl", "biometric acquired, grabbing biometric wakelock");
            this.mHandler.postDelayed(this.mReleaseBiometricWakeLockRunnable, 15000);
        }
        Trace.endSection();
    }

    private boolean pulsingOrAod() {
        ScrimState state = this.mScrimController.getState();
        return state == ScrimState.AOD || state == ScrimState.PULSING;
    }

    public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
        Trace.beginSection("BiometricUnlockController#onBiometricAuthenticated");
        if (this.mUpdateMonitor.isGoingToSleep()) {
            this.mPendingAuthenticated = new PendingAuthenticated(i, biometricSourceType, z);
            Trace.endSection();
            return;
        }
        this.mMetricsLogger.write(new LogMaker(1697).setType(10).setSubtype(toSubtype(biometricSourceType)));
        if (this.mKeyguardBypassController.onBiometricAuthenticated(biometricSourceType, z)) {
            this.mKeyguardViewMediator.userActivity();
            startWakeAndUnlock(biometricSourceType, z);
        } else {
            Log.d("BiometricUnlockCtrl", "onBiometricAuthenticated aborted by bypass controller");
        }
    }

    public void startWakeAndUnlock(BiometricSourceType biometricSourceType, boolean z) {
        startWakeAndUnlock(calculateMode(biometricSourceType, z));
    }

    public void startWakeAndUnlock(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("startWakeAndUnlock(");
        sb.append(i);
        sb.append(")");
        Log.v("BiometricUnlockCtrl", sb.toString());
        boolean isDeviceInteractive = this.mUpdateMonitor.isDeviceInteractive();
        this.mMode = i;
        if (i == 2 && pulsingOrAod()) {
            this.mNotificationShadeWindowController.setForceDozeBrightness(true);
        }
        boolean z = i == 1 && this.mDozeParameters.getAlwaysOn() && this.mWakeUpDelay > 0;
        $$Lambda$BiometricUnlockController$eARUOiIHQidy4dPvrf3UVu6gsv0 r3 = new Runnable(isDeviceInteractive, z) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                BiometricUnlockController.this.lambda$startWakeAndUnlock$0$BiometricUnlockController(this.f$1, this.f$2);
            }
        };
        if (!z && this.mMode != 0) {
            r3.run();
        }
        int i2 = this.mMode;
        switch (i2) {
            case 1:
            case 2:
            case 6:
                if (i2 == 2) {
                    Trace.beginSection("MODE_WAKE_AND_UNLOCK_PULSING");
                    this.mMediaManager.updateMediaMetaData(false, true);
                } else if (i2 == 1) {
                    Trace.beginSection("MODE_WAKE_AND_UNLOCK");
                } else {
                    Trace.beginSection("MODE_WAKE_AND_UNLOCK_FROM_DREAM");
                    this.mUpdateMonitor.awakenFromDream();
                }
                this.mNotificationShadeWindowController.setNotificationShadeFocusable(false);
                if (z) {
                    this.mHandler.postDelayed(r3, (long) this.mWakeUpDelay);
                } else {
                    this.mKeyguardViewMediator.onWakeAndUnlocking();
                }
                if (this.mStatusBar.getNavigationBarView() != null) {
                    this.mStatusBar.getNavigationBarView().setWakeAndUnlocking(true);
                }
                Trace.endSection();
                break;
            case 3:
            case 5:
                Trace.beginSection("MODE_UNLOCK_COLLAPSING or MODE_SHOW_BOUNCER");
                if (!isDeviceInteractive) {
                    this.mPendingShowBouncer = true;
                } else {
                    showBouncer();
                }
                Trace.endSection();
                break;
            case 7:
            case 8:
                Trace.beginSection("MODE_DISMISS_BOUNCER or MODE_UNLOCK_FADING");
                this.mStatusBarKeyguardViewManager.notifyKeyguardAuthenticated(false);
                Trace.endSection();
                break;
        }
        this.mStatusBar.notifyBiometricAuthModeChanged();
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startWakeAndUnlock$0 */
    public /* synthetic */ void lambda$startWakeAndUnlock$0$BiometricUnlockController(boolean z, boolean z2) {
        if (!z) {
            Log.i("BiometricUnlockCtrl", "bio wakelock: Authenticated, waking up...");
            this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 4, "android.policy:BIOMETRIC");
        }
        if (z2) {
            this.mKeyguardViewMediator.onWakeAndUnlocking();
        }
        Trace.beginSection("release wake-and-unlock");
        releaseBiometricWakeLock();
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void showBouncer() {
        if (this.mMode == 3) {
            this.mStatusBarKeyguardViewManager.showBouncer(false);
        }
        this.mShadeController.animateCollapsePanels(0, true, false, 1.1f);
        this.mPendingShowBouncer = false;
    }

    public void onStartedGoingToSleep(int i) {
        resetMode();
        this.mFadedAwayAfterWakeAndUnlock = false;
        this.mPendingAuthenticated = null;
    }

    public void onFinishedGoingToSleep(int i) {
        Trace.beginSection("BiometricUnlockController#onFinishedGoingToSleep");
        PendingAuthenticated pendingAuthenticated = this.mPendingAuthenticated;
        if (pendingAuthenticated != null) {
            this.mHandler.post(new Runnable(pendingAuthenticated) {
                public final /* synthetic */ PendingAuthenticated f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    BiometricUnlockController.this.lambda$onFinishedGoingToSleep$1$BiometricUnlockController(this.f$1);
                }
            });
            this.mPendingAuthenticated = null;
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishedGoingToSleep$1 */
    public /* synthetic */ void lambda$onFinishedGoingToSleep$1$BiometricUnlockController(PendingAuthenticated pendingAuthenticated) {
        onBiometricAuthenticated(pendingAuthenticated.userId, pendingAuthenticated.biometricSourceType, pendingAuthenticated.isStrongBiometric);
    }

    public boolean hasPendingAuthentication() {
        PendingAuthenticated pendingAuthenticated = this.mPendingAuthenticated;
        return pendingAuthenticated != null && this.mUpdateMonitor.isUnlockingWithBiometricAllowed(pendingAuthenticated.isStrongBiometric) && this.mPendingAuthenticated.userId == KeyguardUpdateMonitor.getCurrentUser();
    }

    public int getMode() {
        return this.mMode;
    }

    private int calculateMode(BiometricSourceType biometricSourceType, boolean z) {
        if (biometricSourceType == BiometricSourceType.FACE || biometricSourceType == BiometricSourceType.IRIS) {
            return calculateModeForPassiveAuth(z);
        }
        return calculateModeForFingerprint(z);
    }

    private int calculateModeForFingerprint(boolean z) {
        boolean isUnlockingWithBiometricAllowed = this.mUpdateMonitor.isUnlockingWithBiometricAllowed(z);
        boolean isDreaming = this.mUpdateMonitor.isDreaming();
        if (!this.mUpdateMonitor.isDeviceInteractive()) {
            if (!this.mStatusBarKeyguardViewManager.isShowing()) {
                return 4;
            }
            if (this.mDozeScrimController.isPulsing() && isUnlockingWithBiometricAllowed) {
                return 2;
            }
            if (isUnlockingWithBiometricAllowed || !this.mKeyguardStateController.isMethodSecure()) {
                return 1;
            }
            return 3;
        } else if (isUnlockingWithBiometricAllowed && isDreaming) {
            return 6;
        } else {
            if (this.mStatusBarKeyguardViewManager.isShowing()) {
                if (this.mStatusBarKeyguardViewManager.bouncerIsOrWillBeShowing() && isUnlockingWithBiometricAllowed) {
                    return 8;
                }
                if (isUnlockingWithBiometricAllowed) {
                    return 5;
                }
                if (!this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                    return 3;
                }
            }
            return 0;
        }
    }

    private int calculateModeForPassiveAuth(boolean z) {
        boolean isUnlockingWithBiometricAllowed = this.mUpdateMonitor.isUnlockingWithBiometricAllowed(z);
        boolean isDreaming = this.mUpdateMonitor.isDreaming();
        boolean bypassEnabled = this.mKeyguardBypassController.getBypassEnabled();
        int i = 3;
        int i2 = 4;
        int i3 = 0;
        if (!this.mUpdateMonitor.isDeviceInteractive()) {
            if (!this.mStatusBarKeyguardViewManager.isShowing()) {
                if (bypassEnabled) {
                    i2 = 1;
                }
                return i2;
            } else if (!isUnlockingWithBiometricAllowed) {
                if (!bypassEnabled) {
                    i = 0;
                }
                return i;
            } else if (this.mDozeScrimController.isPulsing()) {
                if (bypassEnabled) {
                    i2 = 2;
                }
                return i2;
            } else if (bypassEnabled) {
                return 2;
            } else {
                return 4;
            }
        } else if (isUnlockingWithBiometricAllowed && isDreaming) {
            if (bypassEnabled) {
                i2 = 6;
            }
            return i2;
        } else if (!this.mStatusBarKeyguardViewManager.isShowing()) {
            return 0;
        } else {
            if (!this.mStatusBarKeyguardViewManager.bouncerIsOrWillBeShowing() || !isUnlockingWithBiometricAllowed) {
                if (isUnlockingWithBiometricAllowed) {
                    if (bypassEnabled) {
                        i3 = 7;
                    }
                    return i3;
                }
                if (!bypassEnabled) {
                    i = 0;
                }
                return i;
            } else if (!bypassEnabled || !this.mKeyguardBypassController.canPlaySubtleWindowAnimations()) {
                return 8;
            } else {
                return 7;
            }
        }
    }

    public void onBiometricAuthFailed(BiometricSourceType biometricSourceType) {
        this.mMetricsLogger.write(new LogMaker(1697).setType(11).setSubtype(toSubtype(biometricSourceType)));
        cleanup();
    }

    public void onBiometricError(int i, String str, BiometricSourceType biometricSourceType) {
        this.mMetricsLogger.write(new LogMaker(1697).setType(15).setSubtype(toSubtype(biometricSourceType)).addTaggedData(1741, Integer.valueOf(i)));
        cleanup();
    }

    private void cleanup() {
        releaseBiometricWakeLock();
    }

    public void startKeyguardFadingAway() {
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                BiometricUnlockController.this.mNotificationShadeWindowController.setForceDozeBrightness(false);
            }
        }, 96);
    }

    public void finishKeyguardFadingAway() {
        if (isWakeAndUnlock()) {
            this.mFadedAwayAfterWakeAndUnlock = true;
        }
        resetMode();
    }

    private void resetMode() {
        this.mMode = 0;
        this.mNotificationShadeWindowController.setForceDozeBrightness(false);
        if (this.mStatusBar.getNavigationBarView() != null) {
            this.mStatusBar.getNavigationBarView().setWakeAndUnlocking(false);
        }
        this.mStatusBar.notifyBiometricAuthModeChanged();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println(" BiometricUnlockController:");
        printWriter.print("   mMode=");
        printWriter.println(this.mMode);
        printWriter.print("   mWakeLock=");
        printWriter.println(this.mWakeLock);
    }

    public boolean isWakeAndUnlock() {
        int i = this.mMode;
        return i == 1 || i == 2 || i == 6;
    }

    public boolean unlockedByWakeAndUnlock() {
        return isWakeAndUnlock() || this.mFadedAwayAfterWakeAndUnlock;
    }

    public boolean isBiometricUnlock() {
        if (!isWakeAndUnlock()) {
            int i = this.mMode;
            if (!(i == 5 || i == 7)) {
                return false;
            }
        }
        return true;
    }

    private int toSubtype(BiometricSourceType biometricSourceType) {
        int i = C14465.$SwitchMap$android$hardware$biometrics$BiometricSourceType[biometricSourceType.ordinal()];
        if (i == 1) {
            return 0;
        }
        if (i != 2) {
            return i != 3 ? 3 : 2;
        }
        return 1;
    }
}
