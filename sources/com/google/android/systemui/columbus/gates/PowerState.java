package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.os.PowerManager;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import dagger.Lazy;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PowerState.kt */
public class PowerState extends Gate {
    private final PowerManager powerManager;
    private final Lazy<WakefulnessLifecycle> wakefulnessLifecycle;
    private final PowerState$wakefulnessLifecycleObserver$1 wakefulnessLifecycleObserver = new PowerState$wakefulnessLifecycleObserver$1(this);

    public PowerState(Context context, Lazy<WakefulnessLifecycle> lazy) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(lazy, "wakefulnessLifecycle");
        super(context);
        this.wakefulnessLifecycle = lazy;
        this.powerManager = (PowerManager) context.getSystemService("power");
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        ((WakefulnessLifecycle) this.wakefulnessLifecycle.get()).addObserver(this.wakefulnessLifecycleObserver);
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        ((WakefulnessLifecycle) this.wakefulnessLifecycle.get()).removeObserver(this.wakefulnessLifecycleObserver);
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        PowerManager powerManager2 = this.powerManager;
        return powerManager2 != null && !powerManager2.isInteractive();
    }
}
