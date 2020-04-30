package com.google.android.systemui.columbus;

import android.os.Handler;
import android.provider.DeviceConfig.OnPropertiesChangedListener;
import com.android.systemui.Dumpable;
import com.android.systemui.assist.DeviceConfigHelper;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ColumbusServiceWrapper.kt */
public final class ColumbusServiceWrapper implements Dumpable {
    private final Lazy<ColumbusService> columbusService;
    private final DeviceConfigHelper deviceConfigHelper;
    /* access modifiers changed from: private */
    public final Handler handler;
    private final OnPropertiesChangedListener propertiesChangedListener = new ColumbusServiceWrapper$propertiesChangedListener$1(this);
    private boolean started;

    public ColumbusServiceWrapper(Lazy<ColumbusService> lazy, DeviceConfigHelper deviceConfigHelper2, Handler handler2) {
        Intrinsics.checkParameterIsNotNull(lazy, "columbusService");
        Intrinsics.checkParameterIsNotNull(deviceConfigHelper2, "deviceConfigHelper");
        Intrinsics.checkParameterIsNotNull(handler2, "handler");
        this.columbusService = lazy;
        this.deviceConfigHelper = deviceConfigHelper2;
        this.handler = handler2;
        if (this.deviceConfigHelper.getBoolean("systemui_google_columbus_enabled", false)) {
            startService();
        } else {
            this.deviceConfigHelper.addOnPropertiesChangedListener(new Executor(this) {
                final /* synthetic */ ColumbusServiceWrapper this$0;

                {
                    this.this$0 = r1;
                }

                public final void execute(Runnable runnable) {
                    this.this$0.handler.post(runnable);
                }
            }, this.propertiesChangedListener);
        }
    }

    /* access modifiers changed from: private */
    public final void startService() {
        this.deviceConfigHelper.removeOnPropertiesChangedListener(this.propertiesChangedListener);
        this.started = true;
        this.columbusService.get();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(strArr, "args");
        if (this.started) {
            ((ColumbusService) this.columbusService.get()).dump(fileDescriptor, printWriter, strArr);
        }
    }
}
