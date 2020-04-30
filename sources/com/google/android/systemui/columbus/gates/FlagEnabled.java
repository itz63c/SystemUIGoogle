package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.os.Handler;
import android.provider.DeviceConfig.OnPropertiesChangedListener;
import com.android.systemui.assist.DeviceConfigHelper;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: FlagEnabled.kt */
public final class FlagEnabled extends Gate {
    /* access modifiers changed from: private */
    public boolean columbusEnabled;
    private final DeviceConfigHelper deviceConfigHelper;
    private final Executor executor = new FlagEnabled$executor$1(this);
    /* access modifiers changed from: private */
    public final Handler handler;
    private final OnPropertiesChangedListener propertiesChangedListener = new FlagEnabled$propertiesChangedListener$1(this);

    public FlagEnabled(Context context, Handler handler2, DeviceConfigHelper deviceConfigHelper2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler2, "handler");
        Intrinsics.checkParameterIsNotNull(deviceConfigHelper2, "deviceConfigHelper");
        super(context);
        this.handler = handler2;
        this.deviceConfigHelper = deviceConfigHelper2;
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.columbusEnabled = this.deviceConfigHelper.getBoolean("systemui_google_columbus_enabled", false);
        this.deviceConfigHelper.addOnPropertiesChangedListener(this.executor, this.propertiesChangedListener);
        notifyListener();
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.deviceConfigHelper.removeOnPropertiesChangedListener(this.propertiesChangedListener);
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return !this.columbusEnabled;
    }
}
