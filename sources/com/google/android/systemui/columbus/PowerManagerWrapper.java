package com.google.android.systemui.columbus;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PowerManagerWrapper.kt */
public class PowerManagerWrapper {
    private final PowerManager powerManager;

    /* compiled from: PowerManagerWrapper.kt */
    public static class WakeLockWrapper {
        private final WakeLock wakeLock;

        public WakeLockWrapper(WakeLock wakeLock2) {
            this.wakeLock = wakeLock2;
        }

        public void acquire(long j) {
            WakeLock wakeLock2 = this.wakeLock;
            if (wakeLock2 != null) {
                wakeLock2.acquire(j);
            }
        }
    }

    public PowerManagerWrapper(Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.powerManager = (PowerManager) context.getSystemService("power");
    }

    public WakeLockWrapper newWakeLock(int i, String str) {
        Intrinsics.checkParameterIsNotNull(str, "tag");
        PowerManager powerManager2 = this.powerManager;
        return new WakeLockWrapper(powerManager2 != null ? powerManager2.newWakeLock(i, str) : null);
    }

    public Boolean isInteractive() {
        PowerManager powerManager2 = this.powerManager;
        if (powerManager2 != null) {
            return Boolean.valueOf(powerManager2.isInteractive());
        }
        return null;
    }
}
