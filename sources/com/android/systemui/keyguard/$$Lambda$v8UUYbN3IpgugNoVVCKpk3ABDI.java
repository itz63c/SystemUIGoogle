package com.android.systemui.keyguard;

import com.android.systemui.keyguard.WakefulnessLifecycle.Observer;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.keyguard.-$$Lambda$v8UUYbN3IpgugNoVVCKp-k3ABDI reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$v8UUYbN3IpgugNoVVCKpk3ABDI implements Consumer {
    public static final /* synthetic */ $$Lambda$v8UUYbN3IpgugNoVVCKpk3ABDI INSTANCE = new $$Lambda$v8UUYbN3IpgugNoVVCKpk3ABDI();

    private /* synthetic */ $$Lambda$v8UUYbN3IpgugNoVVCKpk3ABDI() {
    }

    public final void accept(Object obj) {
        ((Observer) obj).onFinishedWakingUp();
    }
}
