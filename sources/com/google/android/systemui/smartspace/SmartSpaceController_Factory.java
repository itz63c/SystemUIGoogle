package com.google.android.systemui.smartspace;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Handler;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.dump.DumpManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class SmartSpaceController_Factory implements Factory<SmartSpaceController> {
    private final Provider<AlarmManager> alarmManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;

    public SmartSpaceController_Factory(Provider<Context> provider, Provider<KeyguardUpdateMonitor> provider2, Provider<Handler> provider3, Provider<AlarmManager> provider4, Provider<DumpManager> provider5) {
        this.contextProvider = provider;
        this.keyguardUpdateMonitorProvider = provider2;
        this.handlerProvider = provider3;
        this.alarmManagerProvider = provider4;
        this.dumpManagerProvider = provider5;
    }

    public SmartSpaceController get() {
        return provideInstance(this.contextProvider, this.keyguardUpdateMonitorProvider, this.handlerProvider, this.alarmManagerProvider, this.dumpManagerProvider);
    }

    public static SmartSpaceController provideInstance(Provider<Context> provider, Provider<KeyguardUpdateMonitor> provider2, Provider<Handler> provider3, Provider<AlarmManager> provider4, Provider<DumpManager> provider5) {
        SmartSpaceController smartSpaceController = new SmartSpaceController((Context) provider.get(), (KeyguardUpdateMonitor) provider2.get(), (Handler) provider3.get(), (AlarmManager) provider4.get(), (DumpManager) provider5.get());
        return smartSpaceController;
    }

    public static SmartSpaceController_Factory create(Provider<Context> provider, Provider<KeyguardUpdateMonitor> provider2, Provider<Handler> provider3, Provider<AlarmManager> provider4, Provider<DumpManager> provider5) {
        SmartSpaceController_Factory smartSpaceController_Factory = new SmartSpaceController_Factory(provider, provider2, provider3, provider4, provider5);
        return smartSpaceController_Factory;
    }
}
