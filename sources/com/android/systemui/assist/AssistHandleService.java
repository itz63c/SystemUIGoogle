package com.android.systemui.assist;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import dagger.Lazy;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: AssistHandleService.kt */
public final class AssistHandleService extends Service {
    /* access modifiers changed from: private */
    public final Lazy<AssistManager> assistManager;
    private final AssistHandleService$binder$1 binder = new AssistHandleService$binder$1(this);

    public AssistHandleService(Lazy<AssistManager> lazy) {
        Intrinsics.checkParameterIsNotNull(lazy, "assistManager");
        this.assistManager = lazy;
    }

    public IBinder onBind(Intent intent) {
        return this.binder;
    }
}
