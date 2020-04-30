package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.hardware.biometrics.BiometricSourceType;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager.UserChangedListener;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: KeyguardBypassController.kt */
public class KeyguardBypassController implements Dumpable {
    private boolean bouncerShowing;
    /* access modifiers changed from: private */
    public boolean bypassEnabled;
    private boolean hasFaceFeature;
    private boolean isPulseExpanding;
    private boolean launchingAffordance;
    private final KeyguardStateController mKeyguardStateController;
    /* access modifiers changed from: private */
    public PendingUnlock pendingUnlock;
    private boolean qSExpanded;
    private final StatusBarStateController statusBarStateController;
    public BiometricUnlockController unlockController;

    /* compiled from: KeyguardBypassController.kt */
    private static final class PendingUnlock {
        private final boolean isStrongBiometric;
        private final BiometricSourceType pendingUnlockType;

        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0016, code lost:
            if (r2.isStrongBiometric == r3.isStrongBiometric) goto L_0x001b;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean equals(java.lang.Object r3) {
            /*
                r2 = this;
                if (r2 == r3) goto L_0x001b
                boolean r0 = r3 instanceof com.android.systemui.statusbar.phone.KeyguardBypassController.PendingUnlock
                if (r0 == 0) goto L_0x0019
                com.android.systemui.statusbar.phone.KeyguardBypassController$PendingUnlock r3 = (com.android.systemui.statusbar.phone.KeyguardBypassController.PendingUnlock) r3
                android.hardware.biometrics.BiometricSourceType r0 = r2.pendingUnlockType
                android.hardware.biometrics.BiometricSourceType r1 = r3.pendingUnlockType
                boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
                if (r0 == 0) goto L_0x0019
                boolean r2 = r2.isStrongBiometric
                boolean r3 = r3.isStrongBiometric
                if (r2 != r3) goto L_0x0019
                goto L_0x001b
            L_0x0019:
                r2 = 0
                return r2
            L_0x001b:
                r2 = 1
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.KeyguardBypassController.PendingUnlock.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            BiometricSourceType biometricSourceType = this.pendingUnlockType;
            int hashCode = (biometricSourceType != null ? biometricSourceType.hashCode() : 0) * 31;
            boolean z = this.isStrongBiometric;
            if (z) {
                z = true;
            }
            return hashCode + (z ? 1 : 0);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("PendingUnlock(pendingUnlockType=");
            sb.append(this.pendingUnlockType);
            sb.append(", isStrongBiometric=");
            sb.append(this.isStrongBiometric);
            sb.append(")");
            return sb.toString();
        }

        public PendingUnlock(BiometricSourceType biometricSourceType, boolean z) {
            Intrinsics.checkParameterIsNotNull(biometricSourceType, "pendingUnlockType");
            this.pendingUnlockType = biometricSourceType;
            this.isStrongBiometric = z;
        }

        public final BiometricSourceType getPendingUnlockType() {
            return this.pendingUnlockType;
        }

        public final boolean isStrongBiometric() {
            return this.isStrongBiometric;
        }
    }

    public final void setUnlockController(BiometricUnlockController biometricUnlockController) {
        Intrinsics.checkParameterIsNotNull(biometricUnlockController, "<set-?>");
        this.unlockController = biometricUnlockController;
    }

    public final void setPulseExpanding(boolean z) {
        this.isPulseExpanding = z;
    }

    public final boolean getBypassEnabled() {
        return this.bypassEnabled && this.mKeyguardStateController.isFaceAuthEnabled();
    }

    public final void setBouncerShowing(boolean z) {
        this.bouncerShowing = z;
    }

    public final void setLaunchingAffordance(boolean z) {
        this.launchingAffordance = z;
    }

    public final void setQSExpanded(boolean z) {
        boolean z2 = this.qSExpanded != z;
        this.qSExpanded = z;
        if (z2 && !z) {
            maybePerformPendingUnlock();
        }
    }

    public KeyguardBypassController(Context context, final TunerService tunerService, StatusBarStateController statusBarStateController2, NotificationLockscreenUserManager notificationLockscreenUserManager, KeyguardStateController keyguardStateController, DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(tunerService, "tunerService");
        Intrinsics.checkParameterIsNotNull(statusBarStateController2, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(notificationLockscreenUserManager, "lockscreenUserManager");
        Intrinsics.checkParameterIsNotNull(keyguardStateController, "keyguardStateController");
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        this.mKeyguardStateController = keyguardStateController;
        this.statusBarStateController = statusBarStateController2;
        boolean hasSystemFeature = context.getPackageManager().hasSystemFeature("android.hardware.biometrics.face");
        this.hasFaceFeature = hasSystemFeature;
        if (hasSystemFeature) {
            dumpManager.registerDumpable("KeyguardBypassController", this);
            statusBarStateController2.addCallback(new StateListener(this) {
                final /* synthetic */ KeyguardBypassController this$0;

                {
                    this.this$0 = r1;
                }

                public void onStateChanged(int i) {
                    if (i != 1) {
                        this.this$0.pendingUnlock = null;
                    }
                }
            });
            final int i = context.getResources().getBoolean(17891457) ? 1 : 0;
            tunerService.addTunable(new Tunable(this) {
                final /* synthetic */ KeyguardBypassController this$0;

                {
                    this.this$0 = r1;
                }

                public void onTuningChanged(String str, String str2) {
                    this.this$0.bypassEnabled = tunerService.getValue(str, i) != 0;
                }
            }, "face_unlock_dismisses_keyguard");
            notificationLockscreenUserManager.addUserChangedListener(new UserChangedListener(this) {
                final /* synthetic */ KeyguardBypassController this$0;

                {
                    this.this$0 = r1;
                }

                public void onUserChanged(int i) {
                    this.this$0.pendingUnlock = null;
                }
            });
        }
    }

    public final boolean onBiometricAuthenticated(BiometricSourceType biometricSourceType, boolean z) {
        Intrinsics.checkParameterIsNotNull(biometricSourceType, "biometricSourceType");
        if (!getBypassEnabled()) {
            return true;
        }
        boolean canBypass = canBypass();
        if (!canBypass && (this.isPulseExpanding || this.qSExpanded)) {
            this.pendingUnlock = new PendingUnlock(biometricSourceType, z);
        }
        return canBypass;
    }

    public final void maybePerformPendingUnlock() {
        PendingUnlock pendingUnlock2 = this.pendingUnlock;
        if (pendingUnlock2 == null) {
            return;
        }
        if (pendingUnlock2 != null) {
            BiometricSourceType pendingUnlockType = pendingUnlock2.getPendingUnlockType();
            PendingUnlock pendingUnlock3 = this.pendingUnlock;
            if (pendingUnlock3 == null) {
                Intrinsics.throwNpe();
                throw null;
            } else if (onBiometricAuthenticated(pendingUnlockType, pendingUnlock3.isStrongBiometric())) {
                BiometricUnlockController biometricUnlockController = this.unlockController;
                if (biometricUnlockController != null) {
                    PendingUnlock pendingUnlock4 = this.pendingUnlock;
                    if (pendingUnlock4 != null) {
                        BiometricSourceType pendingUnlockType2 = pendingUnlock4.getPendingUnlockType();
                        PendingUnlock pendingUnlock5 = this.pendingUnlock;
                        if (pendingUnlock5 != null) {
                            biometricUnlockController.startWakeAndUnlock(pendingUnlockType2, pendingUnlock5.isStrongBiometric());
                            this.pendingUnlock = null;
                            return;
                        }
                        Intrinsics.throwNpe();
                        throw null;
                    }
                    Intrinsics.throwNpe();
                    throw null;
                }
                Intrinsics.throwUninitializedPropertyAccessException("unlockController");
                throw null;
            }
        } else {
            Intrinsics.throwNpe();
            throw null;
        }
    }

    public final boolean canBypass() {
        if (!getBypassEnabled()) {
            return false;
        }
        if (!this.bouncerShowing && (this.statusBarStateController.getState() != 1 || this.launchingAffordance || this.isPulseExpanding || this.qSExpanded)) {
            return false;
        }
        return true;
    }

    public final boolean canPlaySubtleWindowAnimations() {
        if (!getBypassEnabled() || this.statusBarStateController.getState() != 1 || this.qSExpanded) {
            return false;
        }
        return true;
    }

    public final void onStartedGoingToSleep() {
        this.pendingUnlock = null;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(strArr, "args");
        printWriter.println("KeyguardBypassController:");
        if (this.pendingUnlock != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("  mPendingUnlock.pendingUnlockType: ");
            PendingUnlock pendingUnlock2 = this.pendingUnlock;
            if (pendingUnlock2 != null) {
                sb.append(pendingUnlock2.getPendingUnlockType());
                printWriter.println(sb.toString());
                StringBuilder sb2 = new StringBuilder();
                sb2.append("  mPendingUnlock.isStrongBiometric: ");
                PendingUnlock pendingUnlock3 = this.pendingUnlock;
                if (pendingUnlock3 != null) {
                    sb2.append(pendingUnlock3.isStrongBiometric());
                    printWriter.println(sb2.toString());
                } else {
                    Intrinsics.throwNpe();
                    throw null;
                }
            } else {
                Intrinsics.throwNpe();
                throw null;
            }
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("  mPendingUnlock: ");
            sb3.append(this.pendingUnlock);
            printWriter.println(sb3.toString());
        }
        StringBuilder sb4 = new StringBuilder();
        sb4.append("  bypassEnabled: ");
        sb4.append(getBypassEnabled());
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("  canBypass: ");
        sb5.append(canBypass());
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("  bouncerShowing: ");
        sb6.append(this.bouncerShowing);
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append("  isPulseExpanding: ");
        sb7.append(this.isPulseExpanding);
        printWriter.println(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append("  launchingAffordance: ");
        sb8.append(this.launchingAffordance);
        printWriter.println(sb8.toString());
        StringBuilder sb9 = new StringBuilder();
        sb9.append("  qSExpanded: ");
        sb9.append(this.qSExpanded);
        printWriter.println(sb9.toString());
        StringBuilder sb10 = new StringBuilder();
        sb10.append("  hasFaceFeature: ");
        sb10.append(this.hasFaceFeature);
        printWriter.println(sb10.toString());
    }
}
