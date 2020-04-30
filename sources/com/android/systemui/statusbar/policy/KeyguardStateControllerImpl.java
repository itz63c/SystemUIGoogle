package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Build;
import android.os.Trace;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.policy.KeyguardStateController.Callback;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;

public class KeyguardStateControllerImpl implements KeyguardStateController, Dumpable {
    private boolean mBypassFadingAnimation;
    private final ArrayList<Callback> mCallbacks = new ArrayList<>();
    private boolean mCanDismissLockScreen;
    private boolean mDebugUnlocked;
    private boolean mFaceAuthEnabled;
    private boolean mKeyguardFadingAway;
    private long mKeyguardFadingAwayDelay;
    private long mKeyguardFadingAwayDuration;
    private boolean mKeyguardGoingAway;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback;
    private boolean mLaunchTransitionFadingAway;
    private final LockPatternUtils mLockPatternUtils;
    private boolean mOccluded;
    private boolean mSecure;
    private boolean mShowing;
    private boolean mTrustManaged;
    private boolean mTrusted;

    /* renamed from: com.android.systemui.statusbar.policy.KeyguardStateControllerImpl$1 */
    class C16561 extends BroadcastReceiver {
    }

    private class UpdateMonitorCallback extends KeyguardUpdateMonitorCallback {
        private UpdateMonitorCallback() {
        }

        /* synthetic */ UpdateMonitorCallback(KeyguardStateControllerImpl keyguardStateControllerImpl, C16561 r2) {
            this();
        }

        public void onUserSwitchComplete(int i) {
            KeyguardStateControllerImpl.this.update(false);
        }

        public void onTrustChanged(int i) {
            KeyguardStateControllerImpl.this.update(false);
            KeyguardStateControllerImpl.this.notifyKeyguardChanged();
        }

        public void onTrustManagedChanged(int i) {
            KeyguardStateControllerImpl.this.update(false);
        }

        public void onStartedWakingUp() {
            KeyguardStateControllerImpl.this.update(false);
        }

        public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
            Trace.beginSection("KeyguardUpdateMonitorCallback#onBiometricAuthenticated");
            if (KeyguardStateControllerImpl.this.mKeyguardUpdateMonitor.isUnlockingWithBiometricAllowed(z)) {
                KeyguardStateControllerImpl.this.update(false);
            }
            Trace.endSection();
        }

        public void onFaceUnlockStateChanged(boolean z, int i) {
            KeyguardStateControllerImpl.this.update(false);
        }

        public void onStrongAuthStateChanged(int i) {
            KeyguardStateControllerImpl.this.update(false);
        }

        public void onKeyguardVisibilityChanged(boolean z) {
            KeyguardStateControllerImpl.this.update(false);
        }

        public void onBiometricsCleared() {
            KeyguardStateControllerImpl.this.update(false);
        }
    }

    public KeyguardStateControllerImpl(Context context, KeyguardUpdateMonitor keyguardUpdateMonitor, LockPatternUtils lockPatternUtils) {
        UpdateMonitorCallback updateMonitorCallback = new UpdateMonitorCallback(this, null);
        this.mKeyguardUpdateMonitorCallback = updateMonitorCallback;
        this.mDebugUnlocked = false;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mLockPatternUtils = lockPatternUtils;
        keyguardUpdateMonitor.registerCallback(updateMonitorCallback);
        update(true);
        boolean z = Build.IS_DEBUGGABLE;
    }

    public void addCallback(Callback callback) {
        Objects.requireNonNull(callback, "Callback must not be null. b/128895449");
        if (!this.mCallbacks.contains(callback)) {
            this.mCallbacks.add(callback);
        }
    }

    public void removeCallback(Callback callback) {
        Objects.requireNonNull(callback, "Callback must not be null. b/128895449");
        this.mCallbacks.remove(callback);
    }

    public boolean isShowing() {
        return this.mShowing;
    }

    public boolean isMethodSecure() {
        return this.mSecure;
    }

    public boolean isOccluded() {
        return this.mOccluded;
    }

    public void notifyKeyguardState(boolean z, boolean z2) {
        if (this.mShowing != z || this.mOccluded != z2) {
            this.mShowing = z;
            this.mOccluded = z2;
            notifyKeyguardChanged();
        }
    }

    /* access modifiers changed from: private */
    public void notifyKeyguardChanged() {
        Trace.beginSection("KeyguardStateController#notifyKeyguardChanged");
        new ArrayList(this.mCallbacks).forEach($$Lambda$AiBS48IhN8ohBVmcN3CRVHIBtQ8.INSTANCE);
        Trace.endSection();
    }

    private void notifyUnlockedChanged() {
        Trace.beginSection("KeyguardStateController#notifyUnlockedChanged");
        new ArrayList(this.mCallbacks).forEach($$Lambda$67ujqA_9Wm5PTpKC6v1UcUnDTY.INSTANCE);
        Trace.endSection();
    }

    public void notifyKeyguardFadingAway(long j, long j2, boolean z) {
        this.mKeyguardFadingAwayDelay = j;
        this.mKeyguardFadingAwayDuration = j2;
        this.mBypassFadingAnimation = z;
        setKeyguardFadingAway(true);
    }

    private void setKeyguardFadingAway(boolean z) {
        if (this.mKeyguardFadingAway != z) {
            this.mKeyguardFadingAway = z;
            ArrayList arrayList = new ArrayList(this.mCallbacks);
            for (int i = 0; i < arrayList.size(); i++) {
                ((Callback) arrayList.get(i)).onKeyguardFadingAwayChanged();
            }
        }
    }

    public void notifyKeyguardDoneFading() {
        this.mKeyguardGoingAway = false;
        setKeyguardFadingAway(false);
    }

    /* access modifiers changed from: 0000 */
    public void update(boolean z) {
        boolean z2;
        Trace.beginSection("KeyguardStateController#update");
        int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        boolean isSecure = this.mLockPatternUtils.isSecure(currentUser);
        boolean z3 = false;
        if (!isSecure || this.mKeyguardUpdateMonitor.getUserCanSkipBouncer(currentUser)) {
            z2 = true;
        } else {
            boolean z4 = Build.IS_DEBUGGABLE;
            z2 = false;
        }
        boolean userTrustIsManaged = this.mKeyguardUpdateMonitor.getUserTrustIsManaged(currentUser);
        boolean userHasTrust = this.mKeyguardUpdateMonitor.getUserHasTrust(currentUser);
        boolean isFaceAuthEnabledForUser = this.mKeyguardUpdateMonitor.isFaceAuthEnabledForUser(currentUser);
        if (!(isSecure == this.mSecure && z2 == this.mCanDismissLockScreen && userTrustIsManaged == this.mTrustManaged && this.mTrusted == userHasTrust && this.mFaceAuthEnabled == isFaceAuthEnabledForUser)) {
            z3 = true;
        }
        if (z3 || z) {
            this.mSecure = isSecure;
            this.mCanDismissLockScreen = z2;
            this.mTrusted = userHasTrust;
            this.mTrustManaged = userTrustIsManaged;
            this.mFaceAuthEnabled = isFaceAuthEnabledForUser;
            notifyUnlockedChanged();
        }
        Trace.endSection();
    }

    public boolean canDismissLockScreen() {
        return this.mCanDismissLockScreen;
    }

    public boolean isFaceAuthEnabled() {
        return this.mFaceAuthEnabled;
    }

    public boolean isKeyguardFadingAway() {
        return this.mKeyguardFadingAway;
    }

    public boolean isKeyguardGoingAway() {
        return this.mKeyguardGoingAway;
    }

    public boolean isBypassFadingAnimation() {
        return this.mBypassFadingAnimation;
    }

    public long getKeyguardFadingAwayDelay() {
        return this.mKeyguardFadingAwayDelay;
    }

    public long getKeyguardFadingAwayDuration() {
        return this.mKeyguardFadingAwayDuration;
    }

    public long calculateGoingToFullShadeDelay() {
        return this.mKeyguardFadingAwayDelay + this.mKeyguardFadingAwayDuration;
    }

    public void notifyKeyguardGoingAway(boolean z) {
        this.mKeyguardGoingAway = z;
    }

    public void setLaunchTransitionFadingAway(boolean z) {
        this.mLaunchTransitionFadingAway = z;
    }

    public boolean isLaunchTransitionFadingAway() {
        return this.mLaunchTransitionFadingAway;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("KeyguardStateController:");
        StringBuilder sb = new StringBuilder();
        sb.append("  mSecure: ");
        sb.append(this.mSecure);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mCanDismissLockScreen: ");
        sb2.append(this.mCanDismissLockScreen);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mTrustManaged: ");
        sb3.append(this.mTrustManaged);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("  mTrusted: ");
        sb4.append(this.mTrusted);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("  mDebugUnlocked: ");
        sb5.append(this.mDebugUnlocked);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("  mFaceAuthEnabled: ");
        sb6.append(this.mFaceAuthEnabled);
        printWriter.println(sb6.toString());
    }
}
