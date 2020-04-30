package com.android.systemui.keyguard;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.hardware.biometrics.BiometricSourceType;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.Builder;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig.OnPropertiesChangedListener;
import android.provider.DeviceConfig.Properties;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseIntArray;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IKeyguardDrawnCallback;
import com.android.internal.policy.IKeyguardExitCallback;
import com.android.internal.policy.IKeyguardStateCallback;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardDisplayManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitor.StrongAuthTracker;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.KeyguardViewController;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.C2007R$bool;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dumpable;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.InjectionInflationController;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;

public class KeyguardViewMediator extends SystemUI implements Dumpable {
    private static final Intent USER_PRESENT_INTENT = new Intent("android.intent.action.USER_PRESENT").addFlags(606076928);
    private AlarmManager mAlarmManager;
    private boolean mAodShowing;
    private AudioManager mAudioManager;
    private boolean mBootCompleted;
    private boolean mBootSendUserPresent;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.ACTION_SHUTDOWN".equals(intent.getAction())) {
                synchronized (KeyguardViewMediator.this) {
                    KeyguardViewMediator.this.mShuttingDown = true;
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public CharSequence mCustomMessage;
    private final BroadcastReceiver mDelayedLockBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("seq", 0);
                StringBuilder sb = new StringBuilder();
                sb.append("received DELAYED_KEYGUARD_ACTION with seq = ");
                sb.append(intExtra);
                sb.append(", mDelayedShowingSequence = ");
                sb.append(KeyguardViewMediator.this.mDelayedShowingSequence);
                Log.d("KeyguardViewMediator", sb.toString());
                synchronized (KeyguardViewMediator.this) {
                    if (KeyguardViewMediator.this.mDelayedShowingSequence == intExtra) {
                        KeyguardViewMediator.this.doKeyguardLocked(null);
                    }
                }
                return;
            }
            if ("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK".equals(intent.getAction())) {
                int intExtra2 = intent.getIntExtra("seq", 0);
                int intExtra3 = intent.getIntExtra("android.intent.extra.USER_ID", 0);
                if (intExtra3 != 0) {
                    synchronized (KeyguardViewMediator.this) {
                        if (KeyguardViewMediator.this.mDelayedProfileShowingSequence == intExtra2) {
                            KeyguardViewMediator.this.lockProfile(intExtra3);
                        }
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public int mDelayedProfileShowingSequence;
    /* access modifiers changed from: private */
    public int mDelayedShowingSequence;
    private DeviceConfigProxy mDeviceConfig;
    /* access modifiers changed from: private */
    public boolean mDeviceInteractive;
    private final DismissCallbackRegistry mDismissCallbackRegistry;
    private boolean mDozing;
    private IKeyguardDrawnCallback mDrawnCallback;
    private IKeyguardExitCallback mExitSecureCallback;
    private boolean mExternallyEnabled = true;
    /* access modifiers changed from: private */
    public final FalsingManager mFalsingManager;
    private boolean mGoingToSleep;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler(Looper.myLooper(), null, true) {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    KeyguardViewMediator.this.handleShow((Bundle) message.obj);
                    return;
                case 2:
                    KeyguardViewMediator.this.handleHide();
                    return;
                case 3:
                    KeyguardViewMediator.this.handleReset();
                    return;
                case 4:
                    Trace.beginSection("KeyguardViewMediator#handleMessage VERIFY_UNLOCK");
                    KeyguardViewMediator.this.handleVerifyUnlock();
                    Trace.endSection();
                    return;
                case 5:
                    KeyguardViewMediator.this.handleNotifyFinishedGoingToSleep();
                    return;
                case 6:
                    Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_SCREEN_TURNING_ON");
                    KeyguardViewMediator.this.handleNotifyScreenTurningOn((IKeyguardDrawnCallback) message.obj);
                    Trace.endSection();
                    return;
                case 7:
                    Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE");
                    KeyguardViewMediator.this.handleKeyguardDone();
                    Trace.endSection();
                    return;
                case 8:
                    Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE_DRAWING");
                    KeyguardViewMediator.this.handleKeyguardDoneDrawing();
                    Trace.endSection();
                    return;
                case 9:
                    Trace.beginSection("KeyguardViewMediator#handleMessage SET_OCCLUDED");
                    KeyguardViewMediator keyguardViewMediator = KeyguardViewMediator.this;
                    boolean z = true;
                    boolean z2 = message.arg1 != 0;
                    if (message.arg2 == 0) {
                        z = false;
                    }
                    keyguardViewMediator.handleSetOccluded(z2, z);
                    Trace.endSection();
                    return;
                case 10:
                    synchronized (KeyguardViewMediator.this) {
                        KeyguardViewMediator.this.doKeyguardLocked((Bundle) message.obj);
                    }
                    return;
                case 11:
                    DismissMessage dismissMessage = (DismissMessage) message.obj;
                    KeyguardViewMediator.this.handleDismiss(dismissMessage.getCallback(), dismissMessage.getMessage());
                    return;
                case 12:
                    Trace.beginSection("KeyguardViewMediator#handleMessage START_KEYGUARD_EXIT_ANIM");
                    StartKeyguardExitAnimParams startKeyguardExitAnimParams = (StartKeyguardExitAnimParams) message.obj;
                    KeyguardViewMediator.this.handleStartKeyguardExitAnimation(startKeyguardExitAnimParams.startTime, startKeyguardExitAnimParams.fadeoutDuration);
                    KeyguardViewMediator.this.mFalsingManager.onSuccessfulUnlock();
                    Trace.endSection();
                    return;
                case 13:
                    Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE_PENDING_TIMEOUT");
                    Log.w("KeyguardViewMediator", "Timeout while waiting for activity drawn!");
                    Trace.endSection();
                    return;
                case 14:
                    Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_STARTED_WAKING_UP");
                    KeyguardViewMediator.this.handleNotifyStartedWakingUp();
                    Trace.endSection();
                    return;
                case 15:
                    Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_SCREEN_TURNED_ON");
                    KeyguardViewMediator.this.handleNotifyScreenTurnedOn();
                    Trace.endSection();
                    return;
                case 16:
                    KeyguardViewMediator.this.handleNotifyScreenTurnedOff();
                    return;
                case 17:
                    KeyguardViewMediator.this.handleNotifyStartedGoingToSleep();
                    return;
                case 18:
                    KeyguardViewMediator.this.handleSystemReady();
                    return;
                default:
                    return;
            }
        }
    };
    private Animation mHideAnimation;
    /* access modifiers changed from: private */
    public final Runnable mHideAnimationFinishedRunnable = new Runnable() {
        public final void run() {
            KeyguardViewMediator.this.lambda$new$5$KeyguardViewMediator();
        }
    };
    /* access modifiers changed from: private */
    public boolean mHideAnimationRun = false;
    /* access modifiers changed from: private */
    public boolean mHideAnimationRunning = false;
    private boolean mHiding;
    private boolean mInGestureNavigationMode;
    private boolean mInputRestricted;
    /* access modifiers changed from: private */
    public KeyguardDisplayManager mKeyguardDisplayManager;
    /* access modifiers changed from: private */
    public boolean mKeyguardDonePending = false;
    private final Runnable mKeyguardGoingAwayRunnable = new Runnable() {
        public void run() {
            Trace.beginSection("KeyguardViewMediator.mKeyGuardGoingAwayRunnable");
            Log.d("KeyguardViewMediator", "keyguardGoingAway");
            ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).keyguardGoingAway();
            int i = (((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).shouldDisableWindowAnimationsForUnlock() || (KeyguardViewMediator.this.mWakeAndUnlocking && !KeyguardViewMediator.this.mPulsing)) ? 2 : 0;
            if (((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).isGoingToNotificationShade() || (KeyguardViewMediator.this.mWakeAndUnlocking && KeyguardViewMediator.this.mPulsing)) {
                i |= 1;
            }
            if (((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).isUnlockWithWallpaper()) {
                i |= 4;
            }
            if (((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).shouldSubtleWindowAnimationsForUnlock()) {
                i |= 8;
            }
            KeyguardViewMediator.this.mUpdateMonitor.setKeyguardGoingAway(true);
            KeyguardViewMediator.this.mNotificationShadeWindowController.setKeyguardGoingAway(true);
            KeyguardViewMediator.this.mUiBgExecutor.execute(new Runnable(i) {
                public final /* synthetic */ int f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    C08837.lambda$run$0(this.f$0);
                }
            });
            Trace.endSection();
        }

        static /* synthetic */ void lambda$run$0(int i) {
            try {
                ActivityTaskManager.getService().keyguardGoingAway(i);
            } catch (RemoteException e) {
                Log.e("KeyguardViewMediator", "Error while calling WindowManager", e);
            }
        }
    };
    /* access modifiers changed from: private */
    public final ArrayList<IKeyguardStateCallback> mKeyguardStateCallbacks = new ArrayList<>();
    /* access modifiers changed from: private */
    public final Lazy<KeyguardViewController> mKeyguardViewControllerLazy;
    /* access modifiers changed from: private */
    public final SparseIntArray mLastSimStates = new SparseIntArray();
    private boolean mLockLater;
    /* access modifiers changed from: private */
    public final LockPatternUtils mLockPatternUtils;
    private int mLockSoundId;
    private int mLockSoundStreamId;
    private float mLockSoundVolume;
    private SoundPool mLockSounds;
    private boolean mNeedToReshowWhenReenabled = false;
    /* access modifiers changed from: private */
    public final NotificationShadeWindowController mNotificationShadeWindowController;
    private boolean mOccluded = false;
    private final OnPropertiesChangedListener mOnPropertiesChangedListener = new OnPropertiesChangedListener() {
        public void onPropertiesChanged(Properties properties) {
            String str = "nav_bar_handle_show_over_lockscreen";
            if (properties.getKeyset().contains(str)) {
                KeyguardViewMediator.this.mShowHomeOverLockscreen = properties.getBoolean(str, true);
            }
        }
    };
    private final PowerManager mPM;
    private boolean mPendingLock;
    private boolean mPendingReset;
    private String mPhoneState = TelephonyManager.EXTRA_STATE_IDLE;
    /* access modifiers changed from: private */
    public boolean mPulsing;
    /* access modifiers changed from: private */
    public boolean mShowHomeOverLockscreen;
    private WakeLock mShowKeyguardWakeLock;
    /* access modifiers changed from: private */
    public boolean mShowing;
    /* access modifiers changed from: private */
    public boolean mShuttingDown;
    private StatusBarManager mStatusBarManager;
    private boolean mSystemReady;
    private final TrustManager mTrustManager;
    private int mTrustedSoundId;
    /* access modifiers changed from: private */
    public final Executor mUiBgExecutor;
    private int mUiSoundsStreamType;
    private int mUnlockSoundId;
    KeyguardUpdateMonitorCallback mUpdateCallback = new KeyguardUpdateMonitorCallback() {
        public void onUserInfoChanged(int i) {
        }

        public void onUserSwitching(int i) {
            synchronized (KeyguardViewMediator.this) {
                KeyguardViewMediator.this.resetKeyguardDonePendingLocked();
                if (KeyguardViewMediator.this.mLockPatternUtils.isLockScreenDisabled(i)) {
                    KeyguardViewMediator.this.dismiss(null, null);
                } else {
                    KeyguardViewMediator.this.resetStateLocked();
                }
                KeyguardViewMediator.this.adjustStatusBarLocked();
            }
        }

        public void onUserSwitchComplete(int i) {
            if (i != 0) {
                UserInfo userInfo = UserManager.get(KeyguardViewMediator.this.mContext).getUserInfo(i);
                if (userInfo != null && !KeyguardViewMediator.this.mLockPatternUtils.isSecure(i)) {
                    if (userInfo.isGuest() || userInfo.isDemo()) {
                        KeyguardViewMediator.this.dismiss(null, null);
                    }
                }
            }
        }

        public void onClockVisibilityChanged() {
            KeyguardViewMediator.this.adjustStatusBarLocked();
        }

        public void onDeviceProvisioned() {
            KeyguardViewMediator.this.sendUserPresentBroadcast();
            synchronized (KeyguardViewMediator.this) {
                if (KeyguardViewMediator.this.mustNotUnlockCurrentUser()) {
                    KeyguardViewMediator.this.doKeyguardLocked(null);
                }
            }
        }

        public void onSimStateChanged(int i, int i2, int i3) {
            boolean z;
            StringBuilder sb = new StringBuilder();
            sb.append("onSimStateChanged(subId=");
            sb.append(i);
            sb.append(", slotId=");
            sb.append(i2);
            sb.append(",state=");
            sb.append(i3);
            sb.append(")");
            Log.d("KeyguardViewMediator", sb.toString());
            int size = KeyguardViewMediator.this.mKeyguardStateCallbacks.size();
            boolean isSimPinSecure = KeyguardViewMediator.this.mUpdateMonitor.isSimPinSecure();
            for (int i4 = size - 1; i4 >= 0; i4--) {
                try {
                    ((IKeyguardStateCallback) KeyguardViewMediator.this.mKeyguardStateCallbacks.get(i4)).onSimSecureStateChanged(isSimPinSecure);
                } catch (RemoteException e) {
                    Slog.w("KeyguardViewMediator", "Failed to call onSimSecureStateChanged", e);
                    if (e instanceof DeadObjectException) {
                        KeyguardViewMediator.this.mKeyguardStateCallbacks.remove(i4);
                    }
                }
            }
            synchronized (KeyguardViewMediator.this) {
                int i5 = KeyguardViewMediator.this.mLastSimStates.get(i2);
                if (i5 != 2) {
                    if (i5 != 3) {
                        z = false;
                        KeyguardViewMediator.this.mLastSimStates.append(i2, i3);
                    }
                }
                z = true;
                KeyguardViewMediator.this.mLastSimStates.append(i2, i3);
            }
            if (i3 != 1) {
                if (i3 == 2 || i3 == 3) {
                    synchronized (KeyguardViewMediator.this) {
                        if (!KeyguardViewMediator.this.mShowing) {
                            Log.d("KeyguardViewMediator", "INTENT_VALUE_ICC_LOCKED and keygaurd isn't showing; need to show keyguard so user can enter sim pin");
                            KeyguardViewMediator.this.doKeyguardLocked(null);
                        } else {
                            KeyguardViewMediator.this.resetStateLocked();
                        }
                    }
                    return;
                } else if (i3 == 5) {
                    synchronized (KeyguardViewMediator.this) {
                        String str = "KeyguardViewMediator";
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("READY, reset state? ");
                        sb2.append(KeyguardViewMediator.this.mShowing);
                        Log.d(str, sb2.toString());
                        if (KeyguardViewMediator.this.mShowing && z) {
                            Log.d("KeyguardViewMediator", "SIM moved to READY when the previous state was locked. Reset the state.");
                            KeyguardViewMediator.this.resetStateLocked();
                        }
                    }
                    return;
                } else if (i3 != 6) {
                    if (i3 != 7) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("Unspecific state: ");
                        sb3.append(i3);
                        Log.v("KeyguardViewMediator", sb3.toString());
                        return;
                    }
                    synchronized (KeyguardViewMediator.this) {
                        if (!KeyguardViewMediator.this.mShowing) {
                            Log.d("KeyguardViewMediator", "PERM_DISABLED and keygaurd isn't showing.");
                            KeyguardViewMediator.this.doKeyguardLocked(null);
                        } else {
                            Log.d("KeyguardViewMediator", "PERM_DISABLED, resetStateLocked toshow permanently disabled message in lockscreen.");
                            KeyguardViewMediator.this.resetStateLocked();
                        }
                    }
                    return;
                }
            }
            synchronized (KeyguardViewMediator.this) {
                if (KeyguardViewMediator.this.shouldWaitForProvisioning()) {
                    if (!KeyguardViewMediator.this.mShowing) {
                        Log.d("KeyguardViewMediator", "ICC_ABSENT isn't showing, we need to show the keyguard since the device isn't provisioned yet.");
                        KeyguardViewMediator.this.doKeyguardLocked(null);
                    } else {
                        KeyguardViewMediator.this.resetStateLocked();
                    }
                }
                if (i3 == 1 && z) {
                    Log.d("KeyguardViewMediator", "SIM moved to ABSENT when the previous state was locked. Reset the state.");
                    KeyguardViewMediator.this.resetStateLocked();
                }
            }
        }

        public void onBiometricAuthFailed(BiometricSourceType biometricSourceType) {
            int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            if (KeyguardViewMediator.this.mLockPatternUtils.isSecure(currentUser)) {
                KeyguardViewMediator.this.mLockPatternUtils.getDevicePolicyManager().reportFailedBiometricAttempt(currentUser);
            }
        }

        public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
            if (KeyguardViewMediator.this.mLockPatternUtils.isSecure(i)) {
                KeyguardViewMediator.this.mLockPatternUtils.getDevicePolicyManager().reportSuccessfulBiometricAttempt(i);
            }
        }

        public void onTrustChanged(int i) {
            if (i == KeyguardUpdateMonitor.getCurrentUser()) {
                synchronized (KeyguardViewMediator.this) {
                    KeyguardViewMediator.this.notifyTrustedChangedLocked(KeyguardViewMediator.this.mUpdateMonitor.getUserHasTrust(i));
                }
            }
        }

        public void onHasLockscreenWallpaperChanged(boolean z) {
            synchronized (KeyguardViewMediator.this) {
                KeyguardViewMediator.this.notifyHasLockscreenWallpaperChanged(z);
            }
        }
    };
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mUpdateMonitor;
    ViewMediatorCallback mViewMediatorCallback = new ViewMediatorCallback() {
        public void userActivity() {
            KeyguardViewMediator.this.userActivity();
        }

        public void keyguardDone(boolean z, int i) {
            if (i == ActivityManager.getCurrentUser()) {
                KeyguardViewMediator.this.tryKeyguardDone();
            }
        }

        public void keyguardDoneDrawing() {
            Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardDoneDrawing");
            KeyguardViewMediator.this.mHandler.sendEmptyMessage(8);
            Trace.endSection();
        }

        public void setNeedsInput(boolean z) {
            ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).setNeedsInput(z);
        }

        public void keyguardDonePending(boolean z, int i) {
            Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardDonePending");
            if (i != ActivityManager.getCurrentUser()) {
                Trace.endSection();
                return;
            }
            KeyguardViewMediator.this.mKeyguardDonePending = true;
            KeyguardViewMediator.this.mHideAnimationRun = true;
            KeyguardViewMediator.this.mHideAnimationRunning = true;
            ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).startPreHideAnimation(KeyguardViewMediator.this.mHideAnimationFinishedRunnable);
            KeyguardViewMediator.this.mHandler.sendEmptyMessageDelayed(13, 3000);
            Trace.endSection();
        }

        public void keyguardGone() {
            Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardGone");
            KeyguardViewMediator.this.mNotificationShadeWindowController.setKeyguardGoingAway(false);
            KeyguardViewMediator.this.mKeyguardDisplayManager.hide();
            Trace.endSection();
        }

        public void readyForKeyguardDone() {
            Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#readyForKeyguardDone");
            if (KeyguardViewMediator.this.mKeyguardDonePending) {
                KeyguardViewMediator.this.mKeyguardDonePending = false;
                KeyguardViewMediator.this.tryKeyguardDone();
            }
            Trace.endSection();
        }

        public void resetKeyguard() {
            KeyguardViewMediator.this.resetStateLocked();
        }

        public void onCancelClicked() {
            ((KeyguardViewController) KeyguardViewMediator.this.mKeyguardViewControllerLazy.get()).onCancelClicked();
        }

        public void onBouncerVisiblityChanged(boolean z) {
            synchronized (KeyguardViewMediator.this) {
                KeyguardViewMediator.this.adjustStatusBarLocked(z);
            }
        }

        public void playTrustedSound() {
            KeyguardViewMediator.this.playTrustedSound();
        }

        public boolean isScreenOn() {
            return KeyguardViewMediator.this.mDeviceInteractive;
        }

        public int getBouncerPromptReason() {
            int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            boolean isTrustUsuallyManaged = KeyguardViewMediator.this.mUpdateMonitor.isTrustUsuallyManaged(currentUser);
            boolean z = isTrustUsuallyManaged || KeyguardViewMediator.this.mUpdateMonitor.isUnlockingWithBiometricsPossible(currentUser);
            StrongAuthTracker strongAuthTracker = KeyguardViewMediator.this.mUpdateMonitor.getStrongAuthTracker();
            int strongAuthForUser = strongAuthTracker.getStrongAuthForUser(currentUser);
            if (z && !strongAuthTracker.hasUserAuthenticatedSinceBoot()) {
                return 1;
            }
            if (z && (strongAuthForUser & 16) != 0) {
                return 2;
            }
            if (z && (strongAuthForUser & 2) != 0) {
                return 3;
            }
            if (isTrustUsuallyManaged && (strongAuthForUser & 4) != 0) {
                return 4;
            }
            if (z && (strongAuthForUser & 8) != 0) {
                return 5;
            }
            if (z && (strongAuthForUser & 64) != 0) {
                return 6;
            }
            if (!z || (strongAuthForUser & 128) == 0) {
                return 0;
            }
            return 7;
        }

        public CharSequence consumeCustomMessage() {
            CharSequence access$2700 = KeyguardViewMediator.this.mCustomMessage;
            KeyguardViewMediator.this.mCustomMessage = null;
            return access$2700;
        }
    };
    private boolean mWaitingUntilKeyguardVisible = false;
    /* access modifiers changed from: private */
    public boolean mWakeAndUnlocking;

    private static class DismissMessage {
        private final IKeyguardDismissCallback mCallback;
        private final CharSequence mMessage;

        DismissMessage(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) {
            this.mCallback = iKeyguardDismissCallback;
            this.mMessage = charSequence;
        }

        public IKeyguardDismissCallback getCallback() {
            return this.mCallback;
        }

        public CharSequence getMessage() {
            return this.mMessage;
        }
    }

    private static class StartKeyguardExitAnimParams {
        long fadeoutDuration;
        long startTime;

        private StartKeyguardExitAnimParams(long j, long j2) {
            this.startTime = j;
            this.fadeoutDuration = j2;
        }
    }

    public void onShortPowerPressedGoHome() {
    }

    public KeyguardViewMediator(Context context, FalsingManager falsingManager, LockPatternUtils lockPatternUtils, BroadcastDispatcher broadcastDispatcher, NotificationShadeWindowController notificationShadeWindowController, Lazy<KeyguardViewController> lazy, DismissCallbackRegistry dismissCallbackRegistry, KeyguardUpdateMonitor keyguardUpdateMonitor, DumpManager dumpManager, Executor executor, PowerManager powerManager, TrustManager trustManager, DeviceConfigProxy deviceConfigProxy, NavigationModeController navigationModeController) {
        DeviceConfigProxy deviceConfigProxy2 = deviceConfigProxy;
        super(context);
        this.mFalsingManager = falsingManager;
        this.mLockPatternUtils = lockPatternUtils;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mNotificationShadeWindowController = notificationShadeWindowController;
        this.mKeyguardViewControllerLazy = lazy;
        this.mDismissCallbackRegistry = dismissCallbackRegistry;
        this.mUiBgExecutor = executor;
        this.mUpdateMonitor = keyguardUpdateMonitor;
        this.mPM = powerManager;
        this.mTrustManager = trustManager;
        DumpManager dumpManager2 = dumpManager;
        dumpManager.registerDumpable(KeyguardViewMediator.class.getName(), this);
        this.mDeviceConfig = deviceConfigProxy2;
        String str = "systemui";
        this.mShowHomeOverLockscreen = deviceConfigProxy2.getBoolean(str, "nav_bar_handle_show_over_lockscreen", true);
        DeviceConfigProxy deviceConfigProxy3 = this.mDeviceConfig;
        Handler handler = this.mHandler;
        Objects.requireNonNull(handler);
        deviceConfigProxy3.addOnPropertiesChangedListener(str, new Executor(handler) {
            public final /* synthetic */ Handler f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(Runnable runnable) {
                this.f$0.post(runnable);
            }
        }, this.mOnPropertiesChangedListener);
        this.mInGestureNavigationMode = QuickStepContract.isGesturalMode(navigationModeController.addListener(new ModeChangedListener() {
            public final void onNavigationModeChanged(int i) {
                KeyguardViewMediator.this.lambda$new$0$KeyguardViewMediator(i);
            }
        }));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$KeyguardViewMediator(int i) {
        this.mInGestureNavigationMode = QuickStepContract.isGesturalMode(i);
    }

    public void userActivity() {
        this.mPM.userActivity(SystemClock.uptimeMillis(), false);
    }

    /* access modifiers changed from: 0000 */
    public boolean mustNotUnlockCurrentUser() {
        return UserManager.isSplitSystemUser() && KeyguardUpdateMonitor.getCurrentUser() == 0;
    }

    private void setupLocked() {
        WakeLock newWakeLock = this.mPM.newWakeLock(1, "show keyguard");
        this.mShowKeyguardWakeLock = newWakeLock;
        boolean z = false;
        newWakeLock.setReferenceCounted(false);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
        this.mBroadcastDispatcher.registerReceiver(this.mBroadcastReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD");
        intentFilter2.addAction("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK");
        this.mContext.registerReceiver(this.mDelayedLockBroadcastReceiver, intentFilter2, "com.android.systemui.permission.SELF", null);
        this.mKeyguardDisplayManager = new KeyguardDisplayManager(this.mContext, new InjectionInflationController(SystemUIFactory.getInstance().getRootComponent()));
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        KeyguardUpdateMonitor.setCurrentUser(ActivityManager.getCurrentUser());
        if (this.mContext.getResources().getBoolean(C2007R$bool.config_enableKeyguardService)) {
            if (!shouldWaitForProvisioning() && !this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())) {
                z = true;
            }
            setShowingLocked(z, true);
        } else {
            setShowingLocked(false, true);
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        this.mDeviceInteractive = this.mPM.isInteractive();
        this.mLockSounds = new Builder().setMaxStreams(1).setAudioAttributes(new AudioAttributes.Builder().setUsage(13).setContentType(4).build()).build();
        String string = Global.getString(contentResolver, "lock_sound");
        if (string != null) {
            this.mLockSoundId = this.mLockSounds.load(string, 1);
        }
        String str = "KeyguardViewMediator";
        if (string == null || this.mLockSoundId == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("failed to load lock sound from ");
            sb.append(string);
            Log.w(str, sb.toString());
        }
        String string2 = Global.getString(contentResolver, "unlock_sound");
        if (string2 != null) {
            this.mUnlockSoundId = this.mLockSounds.load(string2, 1);
        }
        if (string2 == null || this.mUnlockSoundId == 0) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("failed to load unlock sound from ");
            sb2.append(string2);
            Log.w(str, sb2.toString());
        }
        String string3 = Global.getString(contentResolver, "trusted_sound");
        if (string3 != null) {
            this.mTrustedSoundId = this.mLockSounds.load(string3, 1);
        }
        if (string3 == null || this.mTrustedSoundId == 0) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("failed to load trusted sound from ");
            sb3.append(string3);
            Log.w(str, sb3.toString());
        }
        this.mLockSoundVolume = (float) Math.pow(10.0d, (double) (((float) this.mContext.getResources().getInteger(17694823)) / 20.0f));
        this.mHideAnimation = AnimationUtils.loadAnimation(this.mContext, 17432680);
        new WorkLockActivityController(this.mContext);
    }

    public void start() {
        synchronized (this) {
            setupLocked();
        }
    }

    public void onSystemReady() {
        this.mHandler.obtainMessage(18).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void handleSystemReady() {
        synchronized (this) {
            Log.d("KeyguardViewMediator", "onSystemReady");
            this.mSystemReady = true;
            doKeyguardLocked(null);
            this.mUpdateMonitor.registerCallback(this.mUpdateCallback);
        }
        maybeSendUserPresentBroadcast();
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x004d A[Catch:{ RemoteException -> 0x005a }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x006d A[Catch:{ RemoteException -> 0x005a }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0096 A[Catch:{ RemoteException -> 0x005a }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onStartedGoingToSleep(int r9) {
        /*
            r8 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onStartedGoingToSleep("
            r0.append(r1)
            r0.append(r9)
            java.lang.String r1 = ")"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "KeyguardViewMediator"
            android.util.Log.d(r1, r0)
            monitor-enter(r8)
            r0 = 0
            r8.mDeviceInteractive = r0     // Catch:{ all -> 0x00a3 }
            r1 = 1
            r8.mGoingToSleep = r1     // Catch:{ all -> 0x00a3 }
            com.android.keyguard.KeyguardUpdateMonitor r2 = r8.mUpdateMonitor     // Catch:{ all -> 0x00a3 }
            r2.setKeyguardGoingAway(r0)     // Catch:{ all -> 0x00a3 }
            int r2 = com.android.keyguard.KeyguardUpdateMonitor.getCurrentUser()     // Catch:{ all -> 0x00a3 }
            com.android.internal.widget.LockPatternUtils r3 = r8.mLockPatternUtils     // Catch:{ all -> 0x00a3 }
            boolean r3 = r3.getPowerButtonInstantlyLocks(r2)     // Catch:{ all -> 0x00a3 }
            if (r3 != 0) goto L_0x003e
            com.android.internal.widget.LockPatternUtils r3 = r8.mLockPatternUtils     // Catch:{ all -> 0x00a3 }
            boolean r3 = r3.isSecure(r2)     // Catch:{ all -> 0x00a3 }
            if (r3 != 0) goto L_0x003c
            goto L_0x003e
        L_0x003c:
            r3 = r0
            goto L_0x003f
        L_0x003e:
            r3 = r1
        L_0x003f:
            int r4 = com.android.keyguard.KeyguardUpdateMonitor.getCurrentUser()     // Catch:{ all -> 0x00a3 }
            long r4 = r8.getLockTimeout(r4)     // Catch:{ all -> 0x00a3 }
            r8.mLockLater = r0     // Catch:{ all -> 0x00a3 }
            com.android.internal.policy.IKeyguardExitCallback r6 = r8.mExitSecureCallback     // Catch:{ all -> 0x00a3 }
            if (r6 == 0) goto L_0x006d
            java.lang.String r2 = "KeyguardViewMediator"
            java.lang.String r3 = "pending exit secure callback cancelled"
            android.util.Log.d(r2, r3)     // Catch:{ all -> 0x00a3 }
            com.android.internal.policy.IKeyguardExitCallback r2 = r8.mExitSecureCallback     // Catch:{ RemoteException -> 0x005a }
            r2.onKeyguardExitResult(r0)     // Catch:{ RemoteException -> 0x005a }
            goto L_0x0062
        L_0x005a:
            r0 = move-exception
            java.lang.String r2 = "KeyguardViewMediator"
            java.lang.String r3 = "Failed to call onKeyguardExitResult(false)"
            android.util.Slog.w(r2, r3, r0)     // Catch:{ all -> 0x00a3 }
        L_0x0062:
            r0 = 0
            r8.mExitSecureCallback = r0     // Catch:{ all -> 0x00a3 }
            boolean r0 = r8.mExternallyEnabled     // Catch:{ all -> 0x00a3 }
            if (r0 != 0) goto L_0x0092
            r8.hideLocked()     // Catch:{ all -> 0x00a3 }
            goto L_0x0092
        L_0x006d:
            boolean r0 = r8.mShowing     // Catch:{ all -> 0x00a3 }
            if (r0 == 0) goto L_0x0074
            r8.mPendingReset = r1     // Catch:{ all -> 0x00a3 }
            goto L_0x0092
        L_0x0074:
            r0 = 3
            if (r9 != r0) goto L_0x007d
            r6 = 0
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 > 0) goto L_0x0082
        L_0x007d:
            r0 = 2
            if (r9 != r0) goto L_0x0088
            if (r3 != 0) goto L_0x0088
        L_0x0082:
            r8.doKeyguardLaterLocked(r4)     // Catch:{ all -> 0x00a3 }
            r8.mLockLater = r1     // Catch:{ all -> 0x00a3 }
            goto L_0x0092
        L_0x0088:
            com.android.internal.widget.LockPatternUtils r0 = r8.mLockPatternUtils     // Catch:{ all -> 0x00a3 }
            boolean r0 = r0.isLockScreenDisabled(r2)     // Catch:{ all -> 0x00a3 }
            if (r0 != 0) goto L_0x0092
            r8.mPendingLock = r1     // Catch:{ all -> 0x00a3 }
        L_0x0092:
            boolean r0 = r8.mPendingLock     // Catch:{ all -> 0x00a3 }
            if (r0 == 0) goto L_0x0099
            r8.playSounds(r1)     // Catch:{ all -> 0x00a3 }
        L_0x0099:
            monitor-exit(r8)     // Catch:{ all -> 0x00a3 }
            com.android.keyguard.KeyguardUpdateMonitor r0 = r8.mUpdateMonitor
            r0.dispatchStartedGoingToSleep(r9)
            r8.notifyStartedGoingToSleep()
            return
        L_0x00a3:
            r9 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x00a3 }
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.keyguard.KeyguardViewMediator.onStartedGoingToSleep(int):void");
    }

    public void onFinishedGoingToSleep(int i, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("onFinishedGoingToSleep(");
        sb.append(i);
        sb.append(")");
        Log.d("KeyguardViewMediator", sb.toString());
        synchronized (this) {
            this.mDeviceInteractive = false;
            this.mGoingToSleep = false;
            this.mWakeAndUnlocking = false;
            resetKeyguardDonePendingLocked();
            this.mHideAnimationRun = false;
            notifyFinishedGoingToSleep();
            if (z) {
                Log.i("KeyguardViewMediator", "Camera gesture was triggered, preventing Keyguard locking.");
                ((PowerManager) this.mContext.getSystemService(PowerManager.class)).wakeUp(SystemClock.uptimeMillis(), 5, "com.android.systemui:CAMERA_GESTURE_PREVENT_LOCK");
                this.mPendingLock = false;
                this.mPendingReset = false;
            }
            if (this.mPendingReset) {
                resetStateLocked();
                this.mPendingReset = false;
            }
            if (this.mPendingLock) {
                doKeyguardLocked(null);
                this.mPendingLock = false;
            }
            if (!this.mLockLater && !z) {
                doKeyguardForChildProfilesLocked();
            }
        }
        this.mUpdateMonitor.dispatchFinishedGoingToSleep(i);
    }

    private long getLockTimeout(int i) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        long j = (long) Secure.getInt(contentResolver, "lock_screen_lock_after_timeout", 5000);
        long maximumTimeToLock = this.mLockPatternUtils.getDevicePolicyManager().getMaximumTimeToLock(null, i);
        return maximumTimeToLock <= 0 ? j : Math.max(Math.min(maximumTimeToLock - Math.max((long) System.getInt(contentResolver, "screen_off_timeout", 30000), 0), j), 0);
    }

    private void doKeyguardLaterLocked() {
        long lockTimeout = getLockTimeout(KeyguardUpdateMonitor.getCurrentUser());
        if (lockTimeout == 0) {
            doKeyguardLocked(null);
        } else {
            doKeyguardLaterLocked(lockTimeout);
        }
    }

    private void doKeyguardLaterLocked(long j) {
        long elapsedRealtime = SystemClock.elapsedRealtime() + j;
        Intent intent = new Intent("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD");
        intent.putExtra("seq", this.mDelayedShowingSequence);
        intent.addFlags(268435456);
        this.mAlarmManager.setExactAndAllowWhileIdle(2, elapsedRealtime, PendingIntent.getBroadcast(this.mContext, 0, intent, 268435456));
        StringBuilder sb = new StringBuilder();
        sb.append("setting alarm to turn off keyguard, seq = ");
        sb.append(this.mDelayedShowingSequence);
        Log.d("KeyguardViewMediator", sb.toString());
        doKeyguardLaterForChildProfilesLocked();
    }

    private void doKeyguardLaterForChildProfilesLocked() {
        int[] enabledProfileIds;
        for (int i : UserManager.get(this.mContext).getEnabledProfileIds(UserHandle.myUserId())) {
            if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i)) {
                long lockTimeout = getLockTimeout(i);
                if (lockTimeout == 0) {
                    doKeyguardForChildProfilesLocked();
                } else {
                    long elapsedRealtime = SystemClock.elapsedRealtime() + lockTimeout;
                    Intent intent = new Intent("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK");
                    intent.putExtra("seq", this.mDelayedProfileShowingSequence);
                    intent.putExtra("android.intent.extra.USER_ID", i);
                    intent.addFlags(268435456);
                    this.mAlarmManager.setExactAndAllowWhileIdle(2, elapsedRealtime, PendingIntent.getBroadcast(this.mContext, 0, intent, 268435456));
                }
            }
        }
    }

    private void doKeyguardForChildProfilesLocked() {
        int[] enabledProfileIds;
        for (int i : UserManager.get(this.mContext).getEnabledProfileIds(UserHandle.myUserId())) {
            if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i)) {
                lockProfile(i);
            }
        }
    }

    private void cancelDoKeyguardLaterLocked() {
        this.mDelayedShowingSequence++;
    }

    private void cancelDoKeyguardForChildProfilesLocked() {
        this.mDelayedProfileShowingSequence++;
    }

    public void onStartedWakingUp() {
        Trace.beginSection("KeyguardViewMediator#onStartedWakingUp");
        synchronized (this) {
            this.mDeviceInteractive = true;
            cancelDoKeyguardLaterLocked();
            cancelDoKeyguardForChildProfilesLocked();
            StringBuilder sb = new StringBuilder();
            sb.append("onStartedWakingUp, seq = ");
            sb.append(this.mDelayedShowingSequence);
            Log.d("KeyguardViewMediator", sb.toString());
            notifyStartedWakingUp();
        }
        this.mUpdateMonitor.dispatchStartedWakingUp();
        maybeSendUserPresentBroadcast();
        Trace.endSection();
    }

    public void onScreenTurningOn(IKeyguardDrawnCallback iKeyguardDrawnCallback) {
        Trace.beginSection("KeyguardViewMediator#onScreenTurningOn");
        notifyScreenOn(iKeyguardDrawnCallback);
        Trace.endSection();
    }

    public void onScreenTurnedOn() {
        Trace.beginSection("KeyguardViewMediator#onScreenTurnedOn");
        notifyScreenTurnedOn();
        this.mUpdateMonitor.dispatchScreenTurnedOn();
        Trace.endSection();
    }

    public void onScreenTurnedOff() {
        notifyScreenTurnedOff();
        this.mUpdateMonitor.dispatchScreenTurnedOff();
    }

    private void maybeSendUserPresentBroadcast() {
        if (this.mSystemReady && this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())) {
            sendUserPresentBroadcast();
        } else if (this.mSystemReady && shouldWaitForProvisioning()) {
            getLockPatternUtils().userPresent(KeyguardUpdateMonitor.getCurrentUser());
        }
    }

    public void onDreamingStarted() {
        this.mUpdateMonitor.dispatchDreamingStarted();
        synchronized (this) {
            if (this.mDeviceInteractive && this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser())) {
                doKeyguardLaterLocked();
            }
        }
    }

    public void onDreamingStopped() {
        this.mUpdateMonitor.dispatchDreamingStopped();
        synchronized (this) {
            if (this.mDeviceInteractive) {
                cancelDoKeyguardLaterLocked();
            }
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(7:29|30|31|32|42|39|27) */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00a2, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x008a, code lost:
        continue;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x0092 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setKeyguardEnabled(boolean r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            java.lang.String r0 = "KeyguardViewMediator"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a3 }
            r1.<init>()     // Catch:{ all -> 0x00a3 }
            java.lang.String r2 = "setKeyguardEnabled("
            r1.append(r2)     // Catch:{ all -> 0x00a3 }
            r1.append(r4)     // Catch:{ all -> 0x00a3 }
            java.lang.String r2 = ")"
            r1.append(r2)     // Catch:{ all -> 0x00a3 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00a3 }
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x00a3 }
            r3.mExternallyEnabled = r4     // Catch:{ all -> 0x00a3 }
            r0 = 1
            if (r4 != 0) goto L_0x0042
            boolean r1 = r3.mShowing     // Catch:{ all -> 0x00a3 }
            if (r1 == 0) goto L_0x0042
            com.android.internal.policy.IKeyguardExitCallback r4 = r3.mExitSecureCallback     // Catch:{ all -> 0x00a3 }
            if (r4 == 0) goto L_0x0032
            java.lang.String r4 = "KeyguardViewMediator"
            java.lang.String r0 = "in process of verifyUnlock request, ignoring"
            android.util.Log.d(r4, r0)     // Catch:{ all -> 0x00a3 }
            monitor-exit(r3)     // Catch:{ all -> 0x00a3 }
            return
        L_0x0032:
            java.lang.String r4 = "KeyguardViewMediator"
            java.lang.String r1 = "remembering to reshow, hiding keyguard, disabling status bar expansion"
            android.util.Log.d(r4, r1)     // Catch:{ all -> 0x00a3 }
            r3.mNeedToReshowWhenReenabled = r0     // Catch:{ all -> 0x00a3 }
            r3.updateInputRestrictedLocked()     // Catch:{ all -> 0x00a3 }
            r3.hideLocked()     // Catch:{ all -> 0x00a3 }
            goto L_0x00a1
        L_0x0042:
            if (r4 == 0) goto L_0x00a1
            boolean r4 = r3.mNeedToReshowWhenReenabled     // Catch:{ all -> 0x00a3 }
            if (r4 == 0) goto L_0x00a1
            java.lang.String r4 = "KeyguardViewMediator"
            java.lang.String r1 = "previously hidden, reshowing, reenabling status bar expansion"
            android.util.Log.d(r4, r1)     // Catch:{ all -> 0x00a3 }
            r4 = 0
            r3.mNeedToReshowWhenReenabled = r4     // Catch:{ all -> 0x00a3 }
            r3.updateInputRestrictedLocked()     // Catch:{ all -> 0x00a3 }
            com.android.internal.policy.IKeyguardExitCallback r1 = r3.mExitSecureCallback     // Catch:{ all -> 0x00a3 }
            r2 = 0
            if (r1 == 0) goto L_0x0075
            java.lang.String r0 = "KeyguardViewMediator"
            java.lang.String r1 = "onKeyguardExitResult(false), resetting"
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x00a3 }
            com.android.internal.policy.IKeyguardExitCallback r0 = r3.mExitSecureCallback     // Catch:{ RemoteException -> 0x0067 }
            r0.onKeyguardExitResult(r4)     // Catch:{ RemoteException -> 0x0067 }
            goto L_0x006f
        L_0x0067:
            r4 = move-exception
            java.lang.String r0 = "KeyguardViewMediator"
            java.lang.String r1 = "Failed to call onKeyguardExitResult(false)"
            android.util.Slog.w(r0, r1, r4)     // Catch:{ all -> 0x00a3 }
        L_0x006f:
            r3.mExitSecureCallback = r2     // Catch:{ all -> 0x00a3 }
            r3.resetStateLocked()     // Catch:{ all -> 0x00a3 }
            goto L_0x00a1
        L_0x0075:
            r3.showLocked(r2)     // Catch:{ all -> 0x00a3 }
            r3.mWaitingUntilKeyguardVisible = r0     // Catch:{ all -> 0x00a3 }
            android.os.Handler r4 = r3.mHandler     // Catch:{ all -> 0x00a3 }
            r0 = 8
            r1 = 2000(0x7d0, double:9.88E-321)
            r4.sendEmptyMessageDelayed(r0, r1)     // Catch:{ all -> 0x00a3 }
            java.lang.String r4 = "KeyguardViewMediator"
            java.lang.String r0 = "waiting until mWaitingUntilKeyguardVisible is false"
            android.util.Log.d(r4, r0)     // Catch:{ all -> 0x00a3 }
        L_0x008a:
            boolean r4 = r3.mWaitingUntilKeyguardVisible     // Catch:{ all -> 0x00a3 }
            if (r4 == 0) goto L_0x009a
            r3.wait()     // Catch:{ InterruptedException -> 0x0092 }
            goto L_0x008a
        L_0x0092:
            java.lang.Thread r4 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x00a3 }
            r4.interrupt()     // Catch:{ all -> 0x00a3 }
            goto L_0x008a
        L_0x009a:
            java.lang.String r4 = "KeyguardViewMediator"
            java.lang.String r0 = "done waiting for mWaitingUntilKeyguardVisible"
            android.util.Log.d(r4, r0)     // Catch:{ all -> 0x00a3 }
        L_0x00a1:
            monitor-exit(r3)     // Catch:{ all -> 0x00a3 }
            return
        L_0x00a3:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00a3 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.keyguard.KeyguardViewMediator.setKeyguardEnabled(boolean):void");
    }

    public void verifyUnlock(IKeyguardExitCallback iKeyguardExitCallback) {
        Trace.beginSection("KeyguardViewMediator#verifyUnlock");
        synchronized (this) {
            Log.d("KeyguardViewMediator", "verifyUnlock");
            if (shouldWaitForProvisioning()) {
                Log.d("KeyguardViewMediator", "ignoring because device isn't provisioned");
                try {
                    iKeyguardExitCallback.onKeyguardExitResult(false);
                } catch (RemoteException e) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", e);
                }
            } else if (this.mExternallyEnabled) {
                Log.w("KeyguardViewMediator", "verifyUnlock called when not externally disabled");
                try {
                    iKeyguardExitCallback.onKeyguardExitResult(false);
                } catch (RemoteException e2) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", e2);
                }
            } else if (this.mExitSecureCallback != null) {
                try {
                    iKeyguardExitCallback.onKeyguardExitResult(false);
                } catch (RemoteException e3) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", e3);
                }
            } else if (!isSecure()) {
                this.mExternallyEnabled = true;
                this.mNeedToReshowWhenReenabled = false;
                updateInputRestricted();
                try {
                    iKeyguardExitCallback.onKeyguardExitResult(true);
                } catch (RemoteException e4) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", e4);
                }
            } else {
                try {
                    iKeyguardExitCallback.onKeyguardExitResult(false);
                } catch (RemoteException e5) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", e5);
                }
            }
        }
        Trace.endSection();
    }

    public boolean isShowingAndNotOccluded() {
        return this.mShowing && !this.mOccluded;
    }

    public void setOccluded(boolean z, boolean z2) {
        Trace.beginSection("KeyguardViewMediator#setOccluded");
        StringBuilder sb = new StringBuilder();
        sb.append("setOccluded ");
        sb.append(z);
        Log.d("KeyguardViewMediator", sb.toString());
        this.mHandler.removeMessages(9);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(9, z ? 1 : 0, z2 ? 1 : 0));
        Trace.endSection();
    }

    public boolean isHiding() {
        return this.mHiding;
    }

    /* access modifiers changed from: private */
    public void handleSetOccluded(boolean z, boolean z2) {
        Trace.beginSection("KeyguardViewMediator#handleSetOccluded");
        synchronized (this) {
            if (this.mHiding && z) {
                startKeyguardExitAnimation(0, 0);
            }
            if (this.mOccluded != z) {
                this.mOccluded = z;
                this.mUpdateMonitor.setKeyguardOccluded(z);
                ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).setOccluded(z, z2 && this.mDeviceInteractive);
                adjustStatusBarLocked();
            }
        }
        Trace.endSection();
    }

    public void doKeyguardTimeout(Bundle bundle) {
        this.mHandler.removeMessages(10);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(10, bundle));
    }

    public boolean isInputRestricted() {
        return this.mShowing || this.mNeedToReshowWhenReenabled;
    }

    private void updateInputRestricted() {
        synchronized (this) {
            updateInputRestrictedLocked();
        }
    }

    private void updateInputRestrictedLocked() {
        boolean isInputRestricted = isInputRestricted();
        if (this.mInputRestricted != isInputRestricted) {
            this.mInputRestricted = isInputRestricted;
            for (int size = this.mKeyguardStateCallbacks.size() - 1; size >= 0; size--) {
                IKeyguardStateCallback iKeyguardStateCallback = (IKeyguardStateCallback) this.mKeyguardStateCallbacks.get(size);
                try {
                    iKeyguardStateCallback.onInputRestrictedStateChanged(isInputRestricted);
                } catch (RemoteException e) {
                    Slog.w("KeyguardViewMediator", "Failed to call onDeviceProvisioned", e);
                    if (e instanceof DeadObjectException) {
                        this.mKeyguardStateCallbacks.remove(iKeyguardStateCallback);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void doKeyguardLocked(Bundle bundle) {
        String str = "KeyguardViewMediator";
        if (KeyguardUpdateMonitor.CORE_APPS_ONLY) {
            Log.d(str, "doKeyguard: not showing because booting to cryptkeeper");
            return;
        }
        boolean z = true;
        if (!this.mExternallyEnabled) {
            Log.d(str, "doKeyguard: not showing because externally disabled");
            this.mNeedToReshowWhenReenabled = true;
        } else if (((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).isShowing()) {
            Log.d(str, "doKeyguard: not showing because it is already showing");
            resetStateLocked();
        } else {
            if (!mustNotUnlockCurrentUser() || !this.mUpdateMonitor.isDeviceProvisioned()) {
                boolean z2 = this.mUpdateMonitor.isSimPinSecure() || ((SubscriptionManager.isValidSubscriptionId(this.mUpdateMonitor.getNextSubIdForState(1)) || SubscriptionManager.isValidSubscriptionId(this.mUpdateMonitor.getNextSubIdForState(7))) && (SystemProperties.getBoolean("keyguard.no_require_sim", false) ^ true));
                if (z2 || !shouldWaitForProvisioning()) {
                    if (bundle == null || !bundle.getBoolean("force_show", false)) {
                        z = false;
                    }
                    if (this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser()) && !z2 && !z) {
                        Log.d(str, "doKeyguard: not showing because lockscreen is off");
                        return;
                    } else if (this.mLockPatternUtils.checkVoldPassword(KeyguardUpdateMonitor.getCurrentUser())) {
                        Log.d(str, "Not showing lock screen since just decrypted");
                        setShowingLocked(false);
                        hideLocked();
                        return;
                    }
                } else {
                    Log.d(str, "doKeyguard: not showing because device isn't provisioned and the sim is not locked or missing");
                    return;
                }
            }
            Log.d(str, "doKeyguard: showing the lock screen");
            showLocked(bundle);
        }
    }

    /* access modifiers changed from: private */
    public void lockProfile(int i) {
        this.mTrustManager.setDeviceLockedForUser(i, true);
    }

    /* access modifiers changed from: private */
    public boolean shouldWaitForProvisioning() {
        return !this.mUpdateMonitor.isDeviceProvisioned() && !isSecure();
    }

    /* access modifiers changed from: private */
    public void handleDismiss(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) {
        if (this.mShowing) {
            if (iKeyguardDismissCallback != null) {
                this.mDismissCallbackRegistry.addCallback(iKeyguardDismissCallback);
            }
            this.mCustomMessage = charSequence;
            ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).dismissAndCollapse();
        } else if (iKeyguardDismissCallback != null) {
            new DismissCallbackWrapper(iKeyguardDismissCallback).notifyDismissError();
        }
    }

    public void dismiss(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) {
        this.mHandler.obtainMessage(11, new DismissMessage(iKeyguardDismissCallback, charSequence)).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void resetStateLocked() {
        Log.e("KeyguardViewMediator", "resetStateLocked");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
    }

    private void notifyStartedGoingToSleep() {
        Log.d("KeyguardViewMediator", "notifyStartedGoingToSleep");
        this.mHandler.sendEmptyMessage(17);
    }

    private void notifyFinishedGoingToSleep() {
        Log.d("KeyguardViewMediator", "notifyFinishedGoingToSleep");
        this.mHandler.sendEmptyMessage(5);
    }

    private void notifyStartedWakingUp() {
        Log.d("KeyguardViewMediator", "notifyStartedWakingUp");
        this.mHandler.sendEmptyMessage(14);
    }

    private void notifyScreenOn(IKeyguardDrawnCallback iKeyguardDrawnCallback) {
        Log.d("KeyguardViewMediator", "notifyScreenOn");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, iKeyguardDrawnCallback));
    }

    private void notifyScreenTurnedOn() {
        Log.d("KeyguardViewMediator", "notifyScreenTurnedOn");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(15));
    }

    private void notifyScreenTurnedOff() {
        Log.d("KeyguardViewMediator", "notifyScreenTurnedOff");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(16));
    }

    private void showLocked(Bundle bundle) {
        Trace.beginSection("KeyguardViewMediator#showLocked aqcuiring mShowKeyguardWakeLock");
        Log.d("KeyguardViewMediator", "showLocked");
        this.mShowKeyguardWakeLock.acquire();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, bundle));
        Trace.endSection();
    }

    private void hideLocked() {
        Trace.beginSection("KeyguardViewMediator#hideLocked");
        Log.d("KeyguardViewMediator", "hideLocked");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(2));
        Trace.endSection();
    }

    public boolean isSecure() {
        return isSecure(KeyguardUpdateMonitor.getCurrentUser());
    }

    public boolean isSecure(int i) {
        return this.mLockPatternUtils.isSecure(i) || this.mUpdateMonitor.isSimPinSecure();
    }

    public void setSwitchingUser(boolean z) {
        this.mUpdateMonitor.setSwitchingUser(z);
    }

    public void setCurrentUser(int i) {
        KeyguardUpdateMonitor.setCurrentUser(i);
        synchronized (this) {
            notifyTrustedChangedLocked(this.mUpdateMonitor.getUserHasTrust(i));
        }
    }

    public void keyguardDone() {
        Trace.beginSection("KeyguardViewMediator#keyguardDone");
        Log.d("KeyguardViewMediator", "keyguardDone()");
        userActivity();
        EventLog.writeEvent(70000, 2);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(7));
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void tryKeyguardDone() {
        if (!this.mKeyguardDonePending && this.mHideAnimationRun && !this.mHideAnimationRunning) {
            handleKeyguardDone();
        } else if (!this.mHideAnimationRun) {
            this.mHideAnimationRun = true;
            this.mHideAnimationRunning = true;
            ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).startPreHideAnimation(this.mHideAnimationFinishedRunnable);
        }
    }

    /* access modifiers changed from: private */
    public void handleKeyguardDone() {
        Trace.beginSection("KeyguardViewMediator#handleKeyguardDone");
        this.mUiBgExecutor.execute(new Runnable(KeyguardUpdateMonitor.getCurrentUser()) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                KeyguardViewMediator.this.lambda$handleKeyguardDone$1$KeyguardViewMediator(this.f$1);
            }
        });
        Log.d("KeyguardViewMediator", "handleKeyguardDone");
        synchronized (this) {
            resetKeyguardDonePendingLocked();
        }
        this.mUpdateMonitor.clearBiometricRecognized();
        if (this.mGoingToSleep) {
            Log.i("KeyguardViewMediator", "Device is going to sleep, aborting keyguardDone");
            return;
        }
        IKeyguardExitCallback iKeyguardExitCallback = this.mExitSecureCallback;
        if (iKeyguardExitCallback != null) {
            try {
                iKeyguardExitCallback.onKeyguardExitResult(true);
            } catch (RemoteException e) {
                Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult()", e);
            }
            this.mExitSecureCallback = null;
            this.mExternallyEnabled = true;
            this.mNeedToReshowWhenReenabled = false;
            updateInputRestricted();
        }
        handleHide();
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleKeyguardDone$1 */
    public /* synthetic */ void lambda$handleKeyguardDone$1$KeyguardViewMediator(int i) {
        if (this.mLockPatternUtils.isSecure(i)) {
            this.mLockPatternUtils.getDevicePolicyManager().reportKeyguardDismissed(i);
        }
    }

    /* access modifiers changed from: private */
    public void sendUserPresentBroadcast() {
        synchronized (this) {
            if (this.mBootCompleted) {
                int currentUser = KeyguardUpdateMonitor.getCurrentUser();
                UserManager userManager = (UserManager) this.mContext.getSystemService("user");
                this.mUiBgExecutor.execute(new Runnable(userManager, new UserHandle(currentUser), currentUser) {
                    public final /* synthetic */ UserManager f$1;
                    public final /* synthetic */ UserHandle f$2;
                    public final /* synthetic */ int f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        KeyguardViewMediator.this.lambda$sendUserPresentBroadcast$2$KeyguardViewMediator(this.f$1, this.f$2, this.f$3);
                    }
                });
            } else {
                this.mBootSendUserPresent = true;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendUserPresentBroadcast$2 */
    public /* synthetic */ void lambda$sendUserPresentBroadcast$2$KeyguardViewMediator(UserManager userManager, UserHandle userHandle, int i) {
        for (int of : userManager.getProfileIdsWithDisabled(userHandle.getIdentifier())) {
            this.mContext.sendBroadcastAsUser(USER_PRESENT_INTENT, UserHandle.of(of));
        }
        getLockPatternUtils().userPresent(i);
    }

    /* access modifiers changed from: private */
    public void handleKeyguardDoneDrawing() {
        Trace.beginSection("KeyguardViewMediator#handleKeyguardDoneDrawing");
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleKeyguardDoneDrawing");
            if (this.mWaitingUntilKeyguardVisible) {
                Log.d("KeyguardViewMediator", "handleKeyguardDoneDrawing: notifying mWaitingUntilKeyguardVisible");
                this.mWaitingUntilKeyguardVisible = false;
                notifyAll();
                this.mHandler.removeMessages(8);
            }
        }
        Trace.endSection();
    }

    private void playSounds(boolean z) {
        playSound(z ? this.mLockSoundId : this.mUnlockSoundId);
    }

    private void playSound(int i) {
        if (i != 0 && System.getInt(this.mContext.getContentResolver(), "lockscreen_sounds_enabled", 1) == 1) {
            this.mLockSounds.stop(this.mLockSoundStreamId);
            if (this.mAudioManager == null) {
                AudioManager audioManager = (AudioManager) this.mContext.getSystemService("audio");
                this.mAudioManager = audioManager;
                if (audioManager != null) {
                    this.mUiSoundsStreamType = audioManager.getUiSoundsStreamType();
                } else {
                    return;
                }
            }
            this.mUiBgExecutor.execute(new Runnable(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    KeyguardViewMediator.this.lambda$playSound$3$KeyguardViewMediator(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$playSound$3 */
    public /* synthetic */ void lambda$playSound$3$KeyguardViewMediator(int i) {
        if (!this.mAudioManager.isStreamMute(this.mUiSoundsStreamType)) {
            SoundPool soundPool = this.mLockSounds;
            float f = this.mLockSoundVolume;
            int play = soundPool.play(i, f, f, 1, 0, 1.0f);
            synchronized (this) {
                this.mLockSoundStreamId = play;
            }
        }
    }

    /* access modifiers changed from: private */
    public void playTrustedSound() {
        playSound(this.mTrustedSoundId);
    }

    private void updateActivityLockScreenState(boolean z, boolean z2) {
        this.mUiBgExecutor.execute(new Runnable(z, z2) {
            public final /* synthetic */ boolean f$0;
            public final /* synthetic */ boolean f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                KeyguardViewMediator.lambda$updateActivityLockScreenState$4(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$updateActivityLockScreenState$4(boolean z, boolean z2) {
        StringBuilder sb = new StringBuilder();
        sb.append("updateActivityLockScreenState(");
        sb.append(z);
        sb.append(", ");
        sb.append(z2);
        sb.append(")");
        Log.d("KeyguardViewMediator", sb.toString());
        try {
            ActivityTaskManager.getService().setLockScreenShown(z, z2);
        } catch (RemoteException unused) {
        }
    }

    /* access modifiers changed from: private */
    public void handleShow(Bundle bundle) {
        Trace.beginSection("KeyguardViewMediator#handleShow");
        int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        if (this.mLockPatternUtils.isSecure(currentUser)) {
            this.mLockPatternUtils.getDevicePolicyManager().reportKeyguardSecured(currentUser);
        }
        synchronized (this) {
            if (!this.mSystemReady) {
                Log.d("KeyguardViewMediator", "ignoring handleShow because system is not ready.");
                return;
            }
            Log.d("KeyguardViewMediator", "handleShow");
            this.mHiding = false;
            this.mWakeAndUnlocking = false;
            setShowingLocked(true);
            ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).show(bundle);
            resetKeyguardDonePendingLocked();
            this.mHideAnimationRun = false;
            adjustStatusBarLocked();
            userActivity();
            this.mUpdateMonitor.setKeyguardGoingAway(false);
            this.mNotificationShadeWindowController.setKeyguardGoingAway(false);
            this.mShowKeyguardWakeLock.release();
            this.mKeyguardDisplayManager.show();
            this.mLockPatternUtils.scheduleNonStrongBiometricIdleTimeout(KeyguardUpdateMonitor.getCurrentUser());
            Trace.endSection();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$5 */
    public /* synthetic */ void lambda$new$5$KeyguardViewMediator() {
        this.mHideAnimationRunning = false;
        tryKeyguardDone();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x005a, code lost:
        android.os.Trace.endSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x005d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleHide() {
        /*
            r5 = this;
            java.lang.String r0 = "KeyguardViewMediator#handleHide"
            android.os.Trace.beginSection(r0)
            boolean r0 = r5.mAodShowing
            if (r0 == 0) goto L_0x001d
            android.content.Context r0 = r5.mContext
            java.lang.Class<android.os.PowerManager> r1 = android.os.PowerManager.class
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.PowerManager r0 = (android.os.PowerManager) r0
            long r1 = android.os.SystemClock.uptimeMillis()
            r3 = 4
            java.lang.String r4 = "com.android.systemui:BOUNCER_DOZING"
            r0.wakeUp(r1, r3, r4)
        L_0x001d:
            monitor-enter(r5)
            java.lang.String r0 = "KeyguardViewMediator"
            java.lang.String r1 = "handleHide"
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x005e }
            boolean r0 = r5.mustNotUnlockCurrentUser()     // Catch:{ all -> 0x005e }
            if (r0 == 0) goto L_0x0034
            java.lang.String r0 = "KeyguardViewMediator"
            java.lang.String r1 = "Split system user, quit unlocking."
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x005e }
            monitor-exit(r5)     // Catch:{ all -> 0x005e }
            return
        L_0x0034:
            r0 = 1
            r5.mHiding = r0     // Catch:{ all -> 0x005e }
            boolean r0 = r5.mShowing     // Catch:{ all -> 0x005e }
            if (r0 == 0) goto L_0x0045
            boolean r0 = r5.mOccluded     // Catch:{ all -> 0x005e }
            if (r0 != 0) goto L_0x0045
            java.lang.Runnable r0 = r5.mKeyguardGoingAwayRunnable     // Catch:{ all -> 0x005e }
            r0.run()     // Catch:{ all -> 0x005e }
            goto L_0x0059
        L_0x0045:
            long r0 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x005e }
            android.view.animation.Animation r2 = r5.mHideAnimation     // Catch:{ all -> 0x005e }
            long r2 = r2.getStartOffset()     // Catch:{ all -> 0x005e }
            long r0 = r0 + r2
            android.view.animation.Animation r2 = r5.mHideAnimation     // Catch:{ all -> 0x005e }
            long r2 = r2.getDuration()     // Catch:{ all -> 0x005e }
            r5.handleStartKeyguardExitAnimation(r0, r2)     // Catch:{ all -> 0x005e }
        L_0x0059:
            monitor-exit(r5)     // Catch:{ all -> 0x005e }
            android.os.Trace.endSection()
            return
        L_0x005e:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x005e }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.keyguard.KeyguardViewMediator.handleHide():void");
    }

    /* access modifiers changed from: private */
    public void handleStartKeyguardExitAnimation(long j, long j2) {
        Trace.beginSection("KeyguardViewMediator#handleStartKeyguardExitAnimation");
        StringBuilder sb = new StringBuilder();
        sb.append("handleStartKeyguardExitAnimation startTime=");
        sb.append(j);
        sb.append(" fadeoutDuration=");
        sb.append(j2);
        Log.d("KeyguardViewMediator", sb.toString());
        synchronized (this) {
            if (!this.mHiding) {
                setShowingLocked(this.mShowing, true);
                return;
            }
            this.mHiding = false;
            if (this.mWakeAndUnlocking && this.mDrawnCallback != null) {
                ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).getViewRootImpl().setReportNextDraw();
                notifyDrawn(this.mDrawnCallback);
                this.mDrawnCallback = null;
            }
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(this.mPhoneState)) {
                playSounds(false);
            }
            setShowingLocked(false);
            this.mWakeAndUnlocking = false;
            this.mDismissCallbackRegistry.notifyDismissSucceeded();
            ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).hide(j, j2);
            resetKeyguardDonePendingLocked();
            this.mHideAnimationRun = false;
            adjustStatusBarLocked();
            sendUserPresentBroadcast();
            Trace.endSection();
        }
    }

    /* access modifiers changed from: private */
    public void adjustStatusBarLocked() {
        adjustStatusBarLocked(false);
    }

    /* access modifiers changed from: private */
    public void adjustStatusBarLocked(boolean z) {
        if (this.mStatusBarManager == null) {
            this.mStatusBarManager = (StatusBarManager) this.mContext.getSystemService("statusbar");
        }
        String str = "KeyguardViewMediator";
        if (this.mStatusBarManager == null) {
            Log.w(str, "Could not get status bar manager");
            return;
        }
        int i = 0;
        if (z || isShowingAndNotOccluded()) {
            if (!this.mShowHomeOverLockscreen || !this.mInGestureNavigationMode) {
                i = 2097152;
            }
            i |= 16777216;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("adjustStatusBarLocked: mShowing=");
        sb.append(this.mShowing);
        sb.append(" mOccluded=");
        sb.append(this.mOccluded);
        sb.append(" isSecure=");
        sb.append(isSecure());
        sb.append(" force=");
        sb.append(z);
        sb.append(" --> flags=0x");
        sb.append(Integer.toHexString(i));
        Log.d(str, sb.toString());
        this.mStatusBarManager.disable(i);
    }

    /* access modifiers changed from: private */
    public void handleReset() {
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleReset");
            ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).reset(true);
        }
    }

    /* access modifiers changed from: private */
    public void handleVerifyUnlock() {
        Trace.beginSection("KeyguardViewMediator#handleVerifyUnlock");
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleVerifyUnlock");
            setShowingLocked(true);
            ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).dismissAndCollapse();
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void handleNotifyStartedGoingToSleep() {
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleNotifyStartedGoingToSleep");
            ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).onStartedGoingToSleep();
        }
    }

    /* access modifiers changed from: private */
    public void handleNotifyFinishedGoingToSleep() {
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleNotifyFinishedGoingToSleep");
            ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).onFinishedGoingToSleep();
        }
    }

    /* access modifiers changed from: private */
    public void handleNotifyStartedWakingUp() {
        Trace.beginSection("KeyguardViewMediator#handleMotifyStartedWakingUp");
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleNotifyWakingUp");
            ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).onStartedWakingUp();
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void handleNotifyScreenTurningOn(IKeyguardDrawnCallback iKeyguardDrawnCallback) {
        Trace.beginSection("KeyguardViewMediator#handleNotifyScreenTurningOn");
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleNotifyScreenTurningOn");
            ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).onScreenTurningOn();
            if (iKeyguardDrawnCallback != null) {
                if (this.mWakeAndUnlocking) {
                    this.mDrawnCallback = iKeyguardDrawnCallback;
                } else {
                    notifyDrawn(iKeyguardDrawnCallback);
                }
            }
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void handleNotifyScreenTurnedOn() {
        Trace.beginSection("KeyguardViewMediator#handleNotifyScreenTurnedOn");
        if (LatencyTracker.isEnabled(this.mContext)) {
            LatencyTracker.getInstance(this.mContext).onActionEnd(5);
        }
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleNotifyScreenTurnedOn");
            ((KeyguardViewController) this.mKeyguardViewControllerLazy.get()).onScreenTurnedOn();
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void handleNotifyScreenTurnedOff() {
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleNotifyScreenTurnedOff");
            this.mDrawnCallback = null;
        }
    }

    private void notifyDrawn(IKeyguardDrawnCallback iKeyguardDrawnCallback) {
        Trace.beginSection("KeyguardViewMediator#notifyDrawn");
        try {
            iKeyguardDrawnCallback.onDrawn();
        } catch (RemoteException e) {
            Slog.w("KeyguardViewMediator", "Exception calling onDrawn():", e);
        }
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void resetKeyguardDonePendingLocked() {
        this.mKeyguardDonePending = false;
        this.mHandler.removeMessages(13);
    }

    public void onBootCompleted() {
        synchronized (this) {
            this.mBootCompleted = true;
            if (this.mBootSendUserPresent) {
                sendUserPresentBroadcast();
            }
        }
    }

    public void onWakeAndUnlocking() {
        Trace.beginSection("KeyguardViewMediator#onWakeAndUnlocking");
        this.mWakeAndUnlocking = true;
        keyguardDone();
        Trace.endSection();
    }

    public void startKeyguardExitAnimation(long j, long j2) {
        Trace.beginSection("KeyguardViewMediator#startKeyguardExitAnimation");
        Handler handler = this.mHandler;
        StartKeyguardExitAnimParams startKeyguardExitAnimParams = new StartKeyguardExitAnimParams(j, j2);
        this.mHandler.sendMessage(handler.obtainMessage(12, startKeyguardExitAnimParams));
        Trace.endSection();
    }

    public ViewMediatorCallback getViewMediatorCallback() {
        return this.mViewMediatorCallback;
    }

    public LockPatternUtils getLockPatternUtils() {
        return this.mLockPatternUtils;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print("  mSystemReady: ");
        printWriter.println(this.mSystemReady);
        printWriter.print("  mBootCompleted: ");
        printWriter.println(this.mBootCompleted);
        printWriter.print("  mBootSendUserPresent: ");
        printWriter.println(this.mBootSendUserPresent);
        printWriter.print("  mExternallyEnabled: ");
        printWriter.println(this.mExternallyEnabled);
        printWriter.print("  mShuttingDown: ");
        printWriter.println(this.mShuttingDown);
        printWriter.print("  mNeedToReshowWhenReenabled: ");
        printWriter.println(this.mNeedToReshowWhenReenabled);
        printWriter.print("  mShowing: ");
        printWriter.println(this.mShowing);
        printWriter.print("  mInputRestricted: ");
        printWriter.println(this.mInputRestricted);
        printWriter.print("  mOccluded: ");
        printWriter.println(this.mOccluded);
        printWriter.print("  mDelayedShowingSequence: ");
        printWriter.println(this.mDelayedShowingSequence);
        printWriter.print("  mExitSecureCallback: ");
        printWriter.println(this.mExitSecureCallback);
        printWriter.print("  mDeviceInteractive: ");
        printWriter.println(this.mDeviceInteractive);
        printWriter.print("  mGoingToSleep: ");
        printWriter.println(this.mGoingToSleep);
        printWriter.print("  mHiding: ");
        printWriter.println(this.mHiding);
        printWriter.print("  mDozing: ");
        printWriter.println(this.mDozing);
        printWriter.print("  mAodShowing: ");
        printWriter.println(this.mAodShowing);
        printWriter.print("  mWaitingUntilKeyguardVisible: ");
        printWriter.println(this.mWaitingUntilKeyguardVisible);
        printWriter.print("  mKeyguardDonePending: ");
        printWriter.println(this.mKeyguardDonePending);
        printWriter.print("  mHideAnimationRun: ");
        printWriter.println(this.mHideAnimationRun);
        printWriter.print("  mPendingReset: ");
        printWriter.println(this.mPendingReset);
        printWriter.print("  mPendingLock: ");
        printWriter.println(this.mPendingLock);
        printWriter.print("  mWakeAndUnlocking: ");
        printWriter.println(this.mWakeAndUnlocking);
        printWriter.print("  mDrawnCallback: ");
        printWriter.println(this.mDrawnCallback);
    }

    public void setDozing(boolean z) {
        if (z != this.mDozing) {
            this.mDozing = z;
            setShowingLocked(this.mShowing);
        }
    }

    public void setPulsing(boolean z) {
        this.mPulsing = z;
    }

    private void setShowingLocked(boolean z) {
        setShowingLocked(z, false);
    }

    private void setShowingLocked(boolean z, boolean z2) {
        boolean z3 = true;
        boolean z4 = this.mDozing && !this.mWakeAndUnlocking;
        if (z == this.mShowing && z4 == this.mAodShowing && !z2) {
            z3 = false;
        }
        this.mShowing = z;
        this.mAodShowing = z4;
        if (z3) {
            notifyDefaultDisplayCallbacks(z);
            updateActivityLockScreenState(z, z4);
        }
    }

    private void notifyDefaultDisplayCallbacks(boolean z) {
        DejankUtils.whitelistIpcs((Runnable) new Runnable(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                KeyguardViewMediator.this.lambda$notifyDefaultDisplayCallbacks$6$KeyguardViewMediator(this.f$1);
            }
        });
        updateInputRestrictedLocked();
        this.mUiBgExecutor.execute(new Runnable() {
            public final void run() {
                KeyguardViewMediator.this.lambda$notifyDefaultDisplayCallbacks$7$KeyguardViewMediator();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$notifyDefaultDisplayCallbacks$6 */
    public /* synthetic */ void lambda$notifyDefaultDisplayCallbacks$6$KeyguardViewMediator(boolean z) {
        for (int size = this.mKeyguardStateCallbacks.size() - 1; size >= 0; size--) {
            IKeyguardStateCallback iKeyguardStateCallback = (IKeyguardStateCallback) this.mKeyguardStateCallbacks.get(size);
            try {
                iKeyguardStateCallback.onShowingStateChanged(z);
            } catch (RemoteException e) {
                Slog.w("KeyguardViewMediator", "Failed to call onShowingStateChanged", e);
                if (e instanceof DeadObjectException) {
                    this.mKeyguardStateCallbacks.remove(iKeyguardStateCallback);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$notifyDefaultDisplayCallbacks$7 */
    public /* synthetic */ void lambda$notifyDefaultDisplayCallbacks$7$KeyguardViewMediator() {
        this.mTrustManager.reportKeyguardShowingChanged();
    }

    /* access modifiers changed from: private */
    public void notifyTrustedChangedLocked(boolean z) {
        for (int size = this.mKeyguardStateCallbacks.size() - 1; size >= 0; size--) {
            try {
                ((IKeyguardStateCallback) this.mKeyguardStateCallbacks.get(size)).onTrustedChanged(z);
            } catch (RemoteException e) {
                Slog.w("KeyguardViewMediator", "Failed to call notifyTrustedChangedLocked", e);
                if (e instanceof DeadObjectException) {
                    this.mKeyguardStateCallbacks.remove(size);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyHasLockscreenWallpaperChanged(boolean z) {
        for (int size = this.mKeyguardStateCallbacks.size() - 1; size >= 0; size--) {
            try {
                ((IKeyguardStateCallback) this.mKeyguardStateCallbacks.get(size)).onHasLockscreenWallpaperChanged(z);
            } catch (RemoteException e) {
                Slog.w("KeyguardViewMediator", "Failed to call onHasLockscreenWallpaperChanged", e);
                if (e instanceof DeadObjectException) {
                    this.mKeyguardStateCallbacks.remove(size);
                }
            }
        }
    }

    public void addStateMonitorCallback(IKeyguardStateCallback iKeyguardStateCallback) {
        synchronized (this) {
            this.mKeyguardStateCallbacks.add(iKeyguardStateCallback);
            try {
                iKeyguardStateCallback.onSimSecureStateChanged(this.mUpdateMonitor.isSimPinSecure());
                iKeyguardStateCallback.onShowingStateChanged(this.mShowing);
                iKeyguardStateCallback.onInputRestrictedStateChanged(this.mInputRestricted);
                iKeyguardStateCallback.onTrustedChanged(this.mUpdateMonitor.getUserHasTrust(KeyguardUpdateMonitor.getCurrentUser()));
                iKeyguardStateCallback.onHasLockscreenWallpaperChanged(this.mUpdateMonitor.hasLockscreenWallpaper());
            } catch (RemoteException e) {
                Slog.w("KeyguardViewMediator", "Failed to call to IKeyguardStateCallback", e);
            }
        }
    }
}
