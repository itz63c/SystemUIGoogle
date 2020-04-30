package com.android.systemui.statusbar.dagger;

import android.content.Context;
import com.android.systemui.statusbar.MediaArtworkProcessor;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.util.DeviceConfigProxy;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.concurrent.Executor;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideNotificationMediaManagerFactory */
public final class C1171x30c882de implements Factory<NotificationMediaManager> {
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigProxy> deviceConfigProxyProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<Executor> mainExecutorProvider;
    private final Provider<MediaArtworkProcessor> mediaArtworkProcessorProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<StatusBar> statusBarLazyProvider;

    public C1171x30c882de(Provider<Context> provider, Provider<StatusBar> provider2, Provider<NotificationShadeWindowController> provider3, Provider<NotificationEntryManager> provider4, Provider<MediaArtworkProcessor> provider5, Provider<KeyguardBypassController> provider6, Provider<Executor> provider7, Provider<DeviceConfigProxy> provider8) {
        this.contextProvider = provider;
        this.statusBarLazyProvider = provider2;
        this.notificationShadeWindowControllerProvider = provider3;
        this.notificationEntryManagerProvider = provider4;
        this.mediaArtworkProcessorProvider = provider5;
        this.keyguardBypassControllerProvider = provider6;
        this.mainExecutorProvider = provider7;
        this.deviceConfigProxyProvider = provider8;
    }

    public NotificationMediaManager get() {
        return provideInstance(this.contextProvider, this.statusBarLazyProvider, this.notificationShadeWindowControllerProvider, this.notificationEntryManagerProvider, this.mediaArtworkProcessorProvider, this.keyguardBypassControllerProvider, this.mainExecutorProvider, this.deviceConfigProxyProvider);
    }

    public static NotificationMediaManager provideInstance(Provider<Context> provider, Provider<StatusBar> provider2, Provider<NotificationShadeWindowController> provider3, Provider<NotificationEntryManager> provider4, Provider<MediaArtworkProcessor> provider5, Provider<KeyguardBypassController> provider6, Provider<Executor> provider7, Provider<DeviceConfigProxy> provider8) {
        return proxyProvideNotificationMediaManager((Context) provider.get(), DoubleCheck.lazy(provider2), DoubleCheck.lazy(provider3), (NotificationEntryManager) provider4.get(), (MediaArtworkProcessor) provider5.get(), (KeyguardBypassController) provider6.get(), (Executor) provider7.get(), (DeviceConfigProxy) provider8.get());
    }

    public static C1171x30c882de create(Provider<Context> provider, Provider<StatusBar> provider2, Provider<NotificationShadeWindowController> provider3, Provider<NotificationEntryManager> provider4, Provider<MediaArtworkProcessor> provider5, Provider<KeyguardBypassController> provider6, Provider<Executor> provider7, Provider<DeviceConfigProxy> provider8) {
        C1171x30c882de statusBarDependenciesModule_ProvideNotificationMediaManagerFactory = new C1171x30c882de(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
        return statusBarDependenciesModule_ProvideNotificationMediaManagerFactory;
    }

    public static NotificationMediaManager proxyProvideNotificationMediaManager(Context context, Lazy<StatusBar> lazy, Lazy<NotificationShadeWindowController> lazy2, NotificationEntryManager notificationEntryManager, MediaArtworkProcessor mediaArtworkProcessor, KeyguardBypassController keyguardBypassController, Executor executor, DeviceConfigProxy deviceConfigProxy) {
        NotificationMediaManager provideNotificationMediaManager = StatusBarDependenciesModule.provideNotificationMediaManager(context, lazy, lazy2, notificationEntryManager, mediaArtworkProcessor, keyguardBypassController, executor, deviceConfigProxy);
        Preconditions.checkNotNull(provideNotificationMediaManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationMediaManager;
    }
}
