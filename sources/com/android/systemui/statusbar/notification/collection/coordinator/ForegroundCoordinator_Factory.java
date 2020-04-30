package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.ForegroundServiceController;
import com.android.systemui.appops.AppOpsController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ForegroundCoordinator_Factory implements Factory<ForegroundCoordinator> {
    private final Provider<AppOpsController> appOpsControllerProvider;
    private final Provider<ForegroundServiceController> foregroundServiceControllerProvider;
    private final Provider<DelayableExecutor> mainExecutorProvider;

    public ForegroundCoordinator_Factory(Provider<ForegroundServiceController> provider, Provider<AppOpsController> provider2, Provider<DelayableExecutor> provider3) {
        this.foregroundServiceControllerProvider = provider;
        this.appOpsControllerProvider = provider2;
        this.mainExecutorProvider = provider3;
    }

    public ForegroundCoordinator get() {
        return provideInstance(this.foregroundServiceControllerProvider, this.appOpsControllerProvider, this.mainExecutorProvider);
    }

    public static ForegroundCoordinator provideInstance(Provider<ForegroundServiceController> provider, Provider<AppOpsController> provider2, Provider<DelayableExecutor> provider3) {
        return new ForegroundCoordinator((ForegroundServiceController) provider.get(), (AppOpsController) provider2.get(), (DelayableExecutor) provider3.get());
    }

    public static ForegroundCoordinator_Factory create(Provider<ForegroundServiceController> provider, Provider<AppOpsController> provider2, Provider<DelayableExecutor> provider3) {
        return new ForegroundCoordinator_Factory(provider, provider2, provider3);
    }
}
