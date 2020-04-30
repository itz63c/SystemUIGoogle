package com.google.android.systemui.columbus.gates;

import android.app.IActivityManager;
import android.content.Context;
import android.os.Handler;
import com.google.android.systemui.columbus.actions.Action;
import dagger.internal.Factory;
import java.util.List;
import javax.inject.Provider;

public final class CameraVisibility_Factory implements Factory<CameraVisibility> {
    private final Provider<IActivityManager> activityManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<List<Action>> exceptionsProvider;
    private final Provider<KeyguardVisibility> keyguardGateProvider;
    private final Provider<PowerState> powerStateProvider;
    private final Provider<Handler> updateHandlerProvider;

    public CameraVisibility_Factory(Provider<Context> provider, Provider<List<Action>> provider2, Provider<KeyguardVisibility> provider3, Provider<PowerState> provider4, Provider<IActivityManager> provider5, Provider<Handler> provider6) {
        this.contextProvider = provider;
        this.exceptionsProvider = provider2;
        this.keyguardGateProvider = provider3;
        this.powerStateProvider = provider4;
        this.activityManagerProvider = provider5;
        this.updateHandlerProvider = provider6;
    }

    public CameraVisibility get() {
        return provideInstance(this.contextProvider, this.exceptionsProvider, this.keyguardGateProvider, this.powerStateProvider, this.activityManagerProvider, this.updateHandlerProvider);
    }

    public static CameraVisibility provideInstance(Provider<Context> provider, Provider<List<Action>> provider2, Provider<KeyguardVisibility> provider3, Provider<PowerState> provider4, Provider<IActivityManager> provider5, Provider<Handler> provider6) {
        CameraVisibility cameraVisibility = new CameraVisibility((Context) provider.get(), (List) provider2.get(), (KeyguardVisibility) provider3.get(), (PowerState) provider4.get(), (IActivityManager) provider5.get(), (Handler) provider6.get());
        return cameraVisibility;
    }

    public static CameraVisibility_Factory create(Provider<Context> provider, Provider<List<Action>> provider2, Provider<KeyguardVisibility> provider3, Provider<PowerState> provider4, Provider<IActivityManager> provider5, Provider<Handler> provider6) {
        CameraVisibility_Factory cameraVisibility_Factory = new CameraVisibility_Factory(provider, provider2, provider3, provider4, provider5, provider6);
        return cameraVisibility_Factory;
    }
}
