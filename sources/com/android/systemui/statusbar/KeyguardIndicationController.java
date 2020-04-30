package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.widget.ViewClippingUtil;
import com.android.internal.widget.ViewClippingUtil.ClippingParameters;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.settingslib.Utils;
import com.android.settingslib.fuelgauge.BatteryStatus;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2012R$integer;
import com.android.systemui.C2017R$string;
import com.android.systemui.Interpolators;
import com.android.systemui.dock.DockManager;
import com.android.systemui.dock.DockManager.AlignmentStateListener;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.phone.KeyguardIndicationTextView;
import com.android.systemui.statusbar.phone.LockscreenLockIconController;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.KeyguardStateController.Callback;
import com.android.systemui.util.wakelock.SettableWakeLock;
import com.android.systemui.util.wakelock.WakeLock.Builder;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.IllegalFormatConversionException;

public class KeyguardIndicationController implements StateListener, Callback {
    private String mAlignmentIndication;
    /* access modifiers changed from: private */
    public final IBatteryStats mBatteryInfo;
    /* access modifiers changed from: private */
    public int mBatteryLevel;
    /* access modifiers changed from: private */
    public int mChargingSpeed;
    /* access modifiers changed from: private */
    public long mChargingTimeRemaining;
    /* access modifiers changed from: private */
    public int mChargingWattage;
    /* access modifiers changed from: private */
    public final ClippingParameters mClippingParams = new ClippingParameters() {
        public boolean shouldFinish(View view) {
            return view == KeyguardIndicationController.this.mIndicationArea;
        }
    };
    /* access modifiers changed from: private */
    public final Context mContext;
    private final DockManager mDockManager;
    /* access modifiers changed from: private */
    public boolean mDozing;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                KeyguardIndicationController.this.hideTransientIndication();
            } else if (i == 2) {
                if (KeyguardIndicationController.this.mLockIconController != null) {
                    KeyguardIndicationController.this.mLockIconController.setTransientBiometricsError(false);
                }
            } else if (i == 3) {
                KeyguardIndicationController.this.showSwipeUpToUnlock();
            }
        }
    };
    private boolean mHideTransientMessageOnScreenOff;
    /* access modifiers changed from: private */
    public ViewGroup mIndicationArea;
    /* access modifiers changed from: private */
    public ColorStateList mInitialTextColorState;
    private final KeyguardStateController mKeyguardStateController;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    /* access modifiers changed from: private */
    public LockscreenLockIconController mLockIconController;
    /* access modifiers changed from: private */
    public String mMessageToShowOnScreenOn;
    /* access modifiers changed from: private */
    public boolean mPowerCharged;
    /* access modifiers changed from: private */
    public boolean mPowerPluggedIn;
    /* access modifiers changed from: private */
    public boolean mPowerPluggedInWired;
    private String mRestingIndication;
    /* access modifiers changed from: private */
    public StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final StatusBarStateController mStatusBarStateController;
    private KeyguardIndicationTextView mTextView;
    private final KeyguardUpdateMonitorCallback mTickReceiver = new KeyguardUpdateMonitorCallback() {
        public void onTimeChanged() {
            if (KeyguardIndicationController.this.mVisible) {
                KeyguardIndicationController.this.updateIndication(false);
            }
        }
    };
    private CharSequence mTransientIndication;
    private ColorStateList mTransientTextColorState;
    private KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    /* access modifiers changed from: private */
    public boolean mVisible;
    private final SettableWakeLock mWakeLock;

    protected class BaseKeyguardCallback extends KeyguardUpdateMonitorCallback {
        protected BaseKeyguardCallback() {
        }

        public void onRefreshBatteryInfo(BatteryStatus batteryStatus) {
            int i = batteryStatus.status;
            boolean z = false;
            boolean z2 = i == 2 || i == 5;
            boolean access$500 = KeyguardIndicationController.this.mPowerPluggedIn;
            KeyguardIndicationController.this.mPowerPluggedInWired = batteryStatus.isPluggedInWired() && z2;
            KeyguardIndicationController.this.mPowerPluggedIn = batteryStatus.isPluggedIn() && z2;
            KeyguardIndicationController.this.mPowerCharged = batteryStatus.isCharged();
            KeyguardIndicationController.this.mChargingWattage = batteryStatus.maxChargingWattage;
            KeyguardIndicationController keyguardIndicationController = KeyguardIndicationController.this;
            keyguardIndicationController.mChargingSpeed = batteryStatus.getChargingSpeed(keyguardIndicationController.mContext);
            KeyguardIndicationController.this.mBatteryLevel = batteryStatus.level;
            try {
                KeyguardIndicationController.this.mChargingTimeRemaining = KeyguardIndicationController.this.mPowerPluggedIn ? KeyguardIndicationController.this.mBatteryInfo.computeChargeTimeRemaining() : -1;
            } catch (RemoteException e) {
                Log.e("KeyguardIndication", "Error calling IBatteryStats: ", e);
                KeyguardIndicationController.this.mChargingTimeRemaining = -1;
            }
            KeyguardIndicationController keyguardIndicationController2 = KeyguardIndicationController.this;
            if (!access$500 && keyguardIndicationController2.mPowerPluggedInWired) {
                z = true;
            }
            keyguardIndicationController2.updateIndication(z);
            if (!KeyguardIndicationController.this.mDozing) {
                return;
            }
            if (!access$500 && KeyguardIndicationController.this.mPowerPluggedIn) {
                KeyguardIndicationController keyguardIndicationController3 = KeyguardIndicationController.this;
                keyguardIndicationController3.showTransientIndication((CharSequence) keyguardIndicationController3.computePowerIndication());
                KeyguardIndicationController.this.hideTransientIndicationDelayed(5000);
            } else if (access$500 && !KeyguardIndicationController.this.mPowerPluggedIn) {
                KeyguardIndicationController.this.hideTransientIndication();
            }
        }

        public void onBiometricHelp(int i, String str, BiometricSourceType biometricSourceType) {
            boolean z = true;
            if (KeyguardIndicationController.this.mKeyguardUpdateMonitor.isUnlockingWithBiometricAllowed(true)) {
                if (i != -2) {
                    z = false;
                }
                if (KeyguardIndicationController.this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                    KeyguardIndicationController.this.mStatusBarKeyguardViewManager.showBouncerMessage(str, KeyguardIndicationController.this.mInitialTextColorState);
                } else if (KeyguardIndicationController.this.mKeyguardUpdateMonitor.isScreenOn()) {
                    KeyguardIndicationController keyguardIndicationController = KeyguardIndicationController.this;
                    keyguardIndicationController.showTransientIndication(str, keyguardIndicationController.mInitialTextColorState, z);
                    if (!z) {
                        KeyguardIndicationController.this.hideTransientIndicationDelayed(1300);
                    }
                }
                if (z) {
                    KeyguardIndicationController.this.mHandler.sendMessageDelayed(KeyguardIndicationController.this.mHandler.obtainMessage(3), 1300);
                }
            }
        }

        public void onBiometricError(int i, String str, BiometricSourceType biometricSourceType) {
            if (!shouldSuppressBiometricError(i, biometricSourceType, KeyguardIndicationController.this.mKeyguardUpdateMonitor)) {
                animatePadlockError();
                if (i == 3) {
                    KeyguardIndicationController.this.showSwipeUpToUnlock();
                } else if (KeyguardIndicationController.this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                    KeyguardIndicationController.this.mStatusBarKeyguardViewManager.showBouncerMessage(str, KeyguardIndicationController.this.mInitialTextColorState);
                } else if (KeyguardIndicationController.this.mKeyguardUpdateMonitor.isScreenOn()) {
                    KeyguardIndicationController.this.showTransientIndication((CharSequence) str);
                    KeyguardIndicationController.this.hideTransientIndicationDelayed(5000);
                } else {
                    KeyguardIndicationController.this.mMessageToShowOnScreenOn = str;
                }
            }
        }

        private void animatePadlockError() {
            if (KeyguardIndicationController.this.mLockIconController != null) {
                KeyguardIndicationController.this.mLockIconController.setTransientBiometricsError(true);
            }
            KeyguardIndicationController.this.mHandler.removeMessages(2);
            KeyguardIndicationController.this.mHandler.sendMessageDelayed(KeyguardIndicationController.this.mHandler.obtainMessage(2), 1300);
        }

        private boolean shouldSuppressBiometricError(int i, BiometricSourceType biometricSourceType, KeyguardUpdateMonitor keyguardUpdateMonitor) {
            if (biometricSourceType == BiometricSourceType.FINGERPRINT) {
                return shouldSuppressFingerprintError(i, keyguardUpdateMonitor);
            }
            if (biometricSourceType == BiometricSourceType.FACE) {
                return shouldSuppressFaceError(i, keyguardUpdateMonitor);
            }
            return false;
        }

        private boolean shouldSuppressFingerprintError(int i, KeyguardUpdateMonitor keyguardUpdateMonitor) {
            return (!keyguardUpdateMonitor.isUnlockingWithBiometricAllowed(true) && i != 9) || i == 5;
        }

        private boolean shouldSuppressFaceError(int i, KeyguardUpdateMonitor keyguardUpdateMonitor) {
            return (!keyguardUpdateMonitor.isUnlockingWithBiometricAllowed(true) && i != 9) || i == 5;
        }

        public void onTrustAgentErrorMessage(CharSequence charSequence) {
            KeyguardIndicationController keyguardIndicationController = KeyguardIndicationController.this;
            keyguardIndicationController.showTransientIndication(charSequence, Utils.getColorError(keyguardIndicationController.mContext), false);
        }

        public void onScreenTurnedOn() {
            if (KeyguardIndicationController.this.mMessageToShowOnScreenOn != null) {
                KeyguardIndicationController keyguardIndicationController = KeyguardIndicationController.this;
                keyguardIndicationController.showTransientIndication(keyguardIndicationController.mMessageToShowOnScreenOn, Utils.getColorError(KeyguardIndicationController.this.mContext), false);
                KeyguardIndicationController.this.hideTransientIndicationDelayed(5000);
                KeyguardIndicationController.this.mMessageToShowOnScreenOn = null;
            }
        }

        public void onBiometricRunningStateChanged(boolean z, BiometricSourceType biometricSourceType) {
            if (z) {
                KeyguardIndicationController.this.hideTransientIndication();
                KeyguardIndicationController.this.mMessageToShowOnScreenOn = null;
            }
        }

        public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
            super.onBiometricAuthenticated(i, biometricSourceType, z);
            KeyguardIndicationController.this.mHandler.sendEmptyMessage(1);
        }

        public void onUserSwitchComplete(int i) {
            if (KeyguardIndicationController.this.mVisible) {
                KeyguardIndicationController.this.updateIndication(false);
            }
        }

        public void onUserUnlocked() {
            if (KeyguardIndicationController.this.mVisible) {
                KeyguardIndicationController.this.updateIndication(false);
            }
        }
    }

    private String getTrustManagedIndication() {
        return null;
    }

    public void onStateChanged(int i) {
    }

    KeyguardIndicationController(Context context, Builder builder, KeyguardStateController keyguardStateController, StatusBarStateController statusBarStateController, KeyguardUpdateMonitor keyguardUpdateMonitor, DockManager dockManager, IBatteryStats iBatteryStats) {
        this.mContext = context;
        this.mKeyguardStateController = keyguardStateController;
        this.mStatusBarStateController = statusBarStateController;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mDockManager = dockManager;
        dockManager.addAlignmentStateListener(new AlignmentStateListener() {
            public final void onAlignmentStateChanged(int i) {
                KeyguardIndicationController.this.lambda$new$1$KeyguardIndicationController(i);
            }
        });
        builder.setTag("Doze:KeyguardIndication");
        this.mWakeLock = new SettableWakeLock(builder.build(), "KeyguardIndication");
        this.mBatteryInfo = iBatteryStats;
        this.mKeyguardUpdateMonitor.registerCallback(getKeyguardCallback());
        this.mKeyguardUpdateMonitor.registerCallback(this.mTickReceiver);
        this.mStatusBarStateController.addCallback(this);
        this.mKeyguardStateController.addCallback(this);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$KeyguardIndicationController(int i) {
        this.mHandler.post(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                KeyguardIndicationController.this.lambda$new$0$KeyguardIndicationController(this.f$1);
            }
        });
    }

    public void setIndicationArea(ViewGroup viewGroup) {
        this.mIndicationArea = viewGroup;
        KeyguardIndicationTextView keyguardIndicationTextView = (KeyguardIndicationTextView) viewGroup.findViewById(C2011R$id.keyguard_indication_text);
        this.mTextView = keyguardIndicationTextView;
        this.mInitialTextColorState = keyguardIndicationTextView != null ? keyguardIndicationTextView.getTextColors() : ColorStateList.valueOf(-1);
        updateIndication(false);
    }

    public void setLockIconController(LockscreenLockIconController lockscreenLockIconController) {
        this.mLockIconController = lockscreenLockIconController;
    }

    /* access modifiers changed from: private */
    /* renamed from: handleAlignStateChanged */
    public void lambda$new$0(int i) {
        String str = i == 1 ? this.mContext.getResources().getString(C2017R$string.dock_alignment_slow_charging) : i == 2 ? this.mContext.getResources().getString(C2017R$string.dock_alignment_not_charging) : "";
        if (!str.equals(this.mAlignmentIndication)) {
            this.mAlignmentIndication = str;
            updateIndication(false);
        }
    }

    /* access modifiers changed from: protected */
    public KeyguardUpdateMonitorCallback getKeyguardCallback() {
        if (this.mUpdateMonitorCallback == null) {
            this.mUpdateMonitorCallback = new BaseKeyguardCallback();
        }
        return this.mUpdateMonitorCallback;
    }

    public void setVisible(boolean z) {
        this.mVisible = z;
        this.mIndicationArea.setVisibility(z ? 0 : 8);
        if (z) {
            if (!this.mHandler.hasMessages(1)) {
                hideTransientIndication();
            }
            updateIndication(false);
        } else if (!z) {
            hideTransientIndication();
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public String getTrustGrantedIndication() {
        return this.mContext.getString(C2017R$string.keyguard_indication_trust_unlocked);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setPowerPluggedIn(boolean z) {
        this.mPowerPluggedIn = z;
    }

    public void hideTransientIndicationDelayed(long j) {
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(1), j);
    }

    public void showTransientIndication(int i) {
        showTransientIndication((CharSequence) this.mContext.getResources().getString(i));
    }

    public void showTransientIndication(CharSequence charSequence) {
        showTransientIndication(charSequence, this.mInitialTextColorState, false);
    }

    /* access modifiers changed from: private */
    public void showTransientIndication(CharSequence charSequence, ColorStateList colorStateList, boolean z) {
        this.mTransientIndication = charSequence;
        this.mHideTransientMessageOnScreenOff = z && charSequence != null;
        this.mTransientTextColorState = colorStateList;
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(3);
        if (this.mDozing && !TextUtils.isEmpty(this.mTransientIndication)) {
            this.mWakeLock.setAcquired(true);
            hideTransientIndicationDelayed(5000);
        }
        updateIndication(false);
    }

    public void hideTransientIndication() {
        if (this.mTransientIndication != null) {
            this.mTransientIndication = null;
            this.mHideTransientMessageOnScreenOff = false;
            this.mHandler.removeMessages(1);
            updateIndication(false);
        }
    }

    /* access modifiers changed from: protected */
    public final void updateIndication(boolean z) {
        if (TextUtils.isEmpty(this.mTransientIndication)) {
            this.mWakeLock.setAcquired(false);
        }
        if (this.mVisible) {
            if (this.mDozing) {
                this.mTextView.setTextColor(-1);
                if (!TextUtils.isEmpty(this.mTransientIndication)) {
                    this.mTextView.switchIndication(this.mTransientIndication);
                } else if (!TextUtils.isEmpty(this.mAlignmentIndication)) {
                    this.mTextView.switchIndication((CharSequence) this.mAlignmentIndication);
                    this.mTextView.setTextColor(Utils.getColorError(this.mContext));
                } else if (this.mPowerPluggedIn) {
                    String computePowerIndication = computePowerIndication();
                    if (z) {
                        animateText(this.mTextView, computePowerIndication);
                    } else {
                        this.mTextView.switchIndication((CharSequence) computePowerIndication);
                    }
                } else {
                    this.mTextView.switchIndication((CharSequence) NumberFormat.getPercentInstance().format((double) (((float) this.mBatteryLevel) / 100.0f)));
                }
                return;
            }
            int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            String trustGrantedIndication = getTrustGrantedIndication();
            String trustManagedIndication = getTrustManagedIndication();
            String str = null;
            if (this.mPowerPluggedIn) {
                str = computePowerIndication();
            }
            if (!this.mKeyguardUpdateMonitor.isUserUnlocked(currentUser)) {
                this.mTextView.switchIndication(17040371);
                this.mTextView.setTextColor(this.mInitialTextColorState);
            } else if (!TextUtils.isEmpty(this.mTransientIndication)) {
                if (str != null) {
                    this.mTextView.switchIndication((CharSequence) this.mContext.getResources().getString(C2017R$string.keyguard_indication_trust_unlocked_plugged_in, new Object[]{this.mTransientIndication, str}));
                } else {
                    this.mTextView.switchIndication(this.mTransientIndication);
                }
                this.mTextView.setTextColor(this.mTransientTextColorState);
            } else if (!TextUtils.isEmpty(trustGrantedIndication) && this.mKeyguardUpdateMonitor.getUserHasTrust(currentUser)) {
                if (str != null) {
                    this.mTextView.switchIndication((CharSequence) this.mContext.getResources().getString(C2017R$string.keyguard_indication_trust_unlocked_plugged_in, new Object[]{trustGrantedIndication, str}));
                } else {
                    this.mTextView.switchIndication((CharSequence) trustGrantedIndication);
                }
                this.mTextView.setTextColor(this.mInitialTextColorState);
            } else if (!TextUtils.isEmpty(this.mAlignmentIndication)) {
                this.mTextView.switchIndication((CharSequence) this.mAlignmentIndication);
                this.mTextView.setTextColor(Utils.getColorError(this.mContext));
            } else if (this.mPowerPluggedIn) {
                this.mTextView.setTextColor(this.mInitialTextColorState);
                if (z) {
                    animateText(this.mTextView, str);
                } else {
                    this.mTextView.switchIndication((CharSequence) str);
                }
            } else if (TextUtils.isEmpty(trustManagedIndication) || !this.mKeyguardUpdateMonitor.getUserTrustIsManaged(currentUser) || this.mKeyguardUpdateMonitor.getUserHasTrust(currentUser)) {
                this.mTextView.switchIndication((CharSequence) this.mRestingIndication);
                this.mTextView.setTextColor(this.mInitialTextColorState);
            } else {
                this.mTextView.switchIndication((CharSequence) trustManagedIndication);
                this.mTextView.setTextColor(this.mInitialTextColorState);
            }
        }
    }

    private void animateText(final KeyguardIndicationTextView keyguardIndicationTextView, final String str) {
        int integer = this.mContext.getResources().getInteger(C2012R$integer.wired_charging_keyguard_text_animation_distance);
        int integer2 = this.mContext.getResources().getInteger(C2012R$integer.wired_charging_keyguard_text_animation_duration_up);
        final int integer3 = this.mContext.getResources().getInteger(C2012R$integer.wired_charging_keyguard_text_animation_duration_down);
        keyguardIndicationTextView.animate().cancel();
        ViewClippingUtil.setClippingDeactivated(keyguardIndicationTextView, true, this.mClippingParams);
        keyguardIndicationTextView.animate().translationYBy((float) integer).setInterpolator(Interpolators.LINEAR).setDuration((long) integer2).setListener(new AnimatorListenerAdapter() {
            private boolean mCancelled;

            public void onAnimationStart(Animator animator) {
                keyguardIndicationTextView.switchIndication((CharSequence) str);
            }

            public void onAnimationCancel(Animator animator) {
                keyguardIndicationTextView.setTranslationY(0.0f);
                this.mCancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (this.mCancelled) {
                    ViewClippingUtil.setClippingDeactivated(keyguardIndicationTextView, false, KeyguardIndicationController.this.mClippingParams);
                } else {
                    keyguardIndicationTextView.animate().setDuration((long) integer3).setInterpolator(Interpolators.BOUNCE).translationY(0.0f).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            keyguardIndicationTextView.setTranslationY(0.0f);
                            C11322 r1 = C11322.this;
                            ViewClippingUtil.setClippingDeactivated(keyguardIndicationTextView, false, KeyguardIndicationController.this.mClippingParams);
                        }
                    });
                }
            }
        });
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public String computePowerIndication() {
        int i;
        if (this.mPowerCharged) {
            return this.mContext.getResources().getString(C2017R$string.keyguard_charged);
        }
        boolean z = this.mChargingTimeRemaining > 0;
        if (this.mPowerPluggedInWired) {
            int i2 = this.mChargingSpeed;
            if (i2 != 0) {
                if (i2 != 2) {
                    if (z) {
                        i = C2017R$string.keyguard_indication_charging_time;
                    } else {
                        i = C2017R$string.keyguard_plugged_in;
                    }
                } else if (z) {
                    i = C2017R$string.keyguard_indication_charging_time_fast;
                } else {
                    i = C2017R$string.keyguard_plugged_in_charging_fast;
                }
            } else if (z) {
                i = C2017R$string.keyguard_indication_charging_time_slowly;
            } else {
                i = C2017R$string.keyguard_plugged_in_charging_slowly;
            }
        } else if (z) {
            i = C2017R$string.keyguard_indication_charging_time_wireless;
        } else {
            i = C2017R$string.keyguard_plugged_in_wireless;
        }
        String format = NumberFormat.getPercentInstance().format((double) (((float) this.mBatteryLevel) / 100.0f));
        if (z) {
            String formatShortElapsedTimeRoundingUpToMinutes = Formatter.formatShortElapsedTimeRoundingUpToMinutes(this.mContext, this.mChargingTimeRemaining);
            try {
                return this.mContext.getResources().getString(i, new Object[]{formatShortElapsedTimeRoundingUpToMinutes, format});
            } catch (IllegalFormatConversionException unused) {
                return this.mContext.getResources().getString(i, new Object[]{formatShortElapsedTimeRoundingUpToMinutes});
            }
        } else {
            try {
                return this.mContext.getResources().getString(i, new Object[]{format});
            } catch (IllegalFormatConversionException unused2) {
                return this.mContext.getResources().getString(i);
            }
        }
    }

    public void setStatusBarKeyguardViewManager(StatusBarKeyguardViewManager statusBarKeyguardViewManager) {
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
    }

    /* access modifiers changed from: private */
    public void showSwipeUpToUnlock() {
        if (!this.mDozing) {
            if (this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                this.mStatusBarKeyguardViewManager.showBouncerMessage(this.mContext.getString(C2017R$string.keyguard_retry), this.mInitialTextColorState);
            } else if (this.mKeyguardUpdateMonitor.isScreenOn()) {
                showTransientIndication(this.mContext.getString(C2017R$string.keyguard_unlock), this.mInitialTextColorState, true);
                hideTransientIndicationDelayed(5000);
            }
        }
    }

    public void setDozing(boolean z) {
        if (this.mDozing != z) {
            this.mDozing = z;
            if (!this.mHideTransientMessageOnScreenOff || !z) {
                updateIndication(false);
            } else {
                hideTransientIndication();
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("KeyguardIndicationController:");
        StringBuilder sb = new StringBuilder();
        sb.append("  mTransientTextColorState: ");
        sb.append(this.mTransientTextColorState);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mInitialTextColorState: ");
        sb2.append(this.mInitialTextColorState);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mPowerPluggedInWired: ");
        sb3.append(this.mPowerPluggedInWired);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("  mPowerPluggedIn: ");
        sb4.append(this.mPowerPluggedIn);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("  mPowerCharged: ");
        sb5.append(this.mPowerCharged);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("  mChargingSpeed: ");
        sb6.append(this.mChargingSpeed);
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append("  mChargingWattage: ");
        sb7.append(this.mChargingWattage);
        printWriter.println(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append("  mMessageToShowOnScreenOn: ");
        sb8.append(this.mMessageToShowOnScreenOn);
        printWriter.println(sb8.toString());
        StringBuilder sb9 = new StringBuilder();
        sb9.append("  mDozing: ");
        sb9.append(this.mDozing);
        printWriter.println(sb9.toString());
        StringBuilder sb10 = new StringBuilder();
        sb10.append("  mBatteryLevel: ");
        sb10.append(this.mBatteryLevel);
        printWriter.println(sb10.toString());
        StringBuilder sb11 = new StringBuilder();
        sb11.append("  mTextView.getText(): ");
        KeyguardIndicationTextView keyguardIndicationTextView = this.mTextView;
        sb11.append(keyguardIndicationTextView == null ? null : keyguardIndicationTextView.getText());
        printWriter.println(sb11.toString());
        StringBuilder sb12 = new StringBuilder();
        sb12.append("  computePowerIndication(): ");
        sb12.append(computePowerIndication());
        printWriter.println(sb12.toString());
    }

    public void onDozingChanged(boolean z) {
        setDozing(z);
    }

    public void onUnlockedChanged() {
        updateIndication(!this.mDozing);
    }
}
