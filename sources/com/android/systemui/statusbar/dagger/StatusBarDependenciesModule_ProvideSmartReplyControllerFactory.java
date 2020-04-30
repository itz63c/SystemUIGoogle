package com.android.systemui.statusbar.dagger;

import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class StatusBarDependenciesModule_ProvideSmartReplyControllerFactory implements Factory<SmartReplyController> {
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<IStatusBarService> statusBarServiceProvider;

    public StatusBarDependenciesModule_ProvideSmartReplyControllerFactory(Provider<NotificationEntryManager> provider, Provider<IStatusBarService> provider2) {
        this.entryManagerProvider = provider;
        this.statusBarServiceProvider = provider2;
    }

    public SmartReplyController get() {
        return provideInstance(this.entryManagerProvider, this.statusBarServiceProvider);
    }

    public static SmartReplyController provideInstance(Provider<NotificationEntryManager> provider, Provider<IStatusBarService> provider2) {
        return proxyProvideSmartReplyController((NotificationEntryManager) provider.get(), (IStatusBarService) provider2.get());
    }

    public static StatusBarDependenciesModule_ProvideSmartReplyControllerFactory create(Provider<NotificationEntryManager> provider, Provider<IStatusBarService> provider2) {
        return new StatusBarDependenciesModule_ProvideSmartReplyControllerFactory(provider, provider2);
    }

    public static SmartReplyController proxyProvideSmartReplyController(NotificationEntryManager notificationEntryManager, IStatusBarService iStatusBarService) {
        SmartReplyController provideSmartReplyController = StatusBarDependenciesModule.provideSmartReplyController(notificationEntryManager, iStatusBarService);
        Preconditions.checkNotNull(provideSmartReplyController, "Cannot return null from a non-@Nullable @Provides method");
        return provideSmartReplyController;
    }
}
