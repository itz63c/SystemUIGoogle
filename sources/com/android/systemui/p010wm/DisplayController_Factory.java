package com.android.systemui.p010wm;

import android.content.Context;
import android.os.Handler;
import android.view.IWindowManager;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.wm.DisplayController_Factory */
public final class DisplayController_Factory implements Factory<DisplayController> {
    private final Provider<Context> contextProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<IWindowManager> wmServiceProvider;

    public DisplayController_Factory(Provider<Context> provider, Provider<Handler> provider2, Provider<IWindowManager> provider3) {
        this.contextProvider = provider;
        this.mainHandlerProvider = provider2;
        this.wmServiceProvider = provider3;
    }

    public DisplayController get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider, this.wmServiceProvider);
    }

    public static DisplayController provideInstance(Provider<Context> provider, Provider<Handler> provider2, Provider<IWindowManager> provider3) {
        return new DisplayController((Context) provider.get(), (Handler) provider2.get(), (IWindowManager) provider3.get());
    }

    public static DisplayController_Factory create(Provider<Context> provider, Provider<Handler> provider2, Provider<IWindowManager> provider3) {
        return new DisplayController_Factory(provider, provider2, provider3);
    }
}
