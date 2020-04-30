package com.android.systemui;

import android.content.Context;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ForegroundServiceNotificationListener_Factory implements Factory<ForegroundServiceNotificationListener> {
    private final Provider<Context> contextProvider;
    private final Provider<ForegroundServiceController> foregroundServiceControllerProvider;
    private final Provider<NotifPipeline> notifPipelineProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;

    public ForegroundServiceNotificationListener_Factory(Provider<Context> provider, Provider<ForegroundServiceController> provider2, Provider<NotificationEntryManager> provider3, Provider<NotifPipeline> provider4) {
        this.contextProvider = provider;
        this.foregroundServiceControllerProvider = provider2;
        this.notificationEntryManagerProvider = provider3;
        this.notifPipelineProvider = provider4;
    }

    public ForegroundServiceNotificationListener get() {
        return provideInstance(this.contextProvider, this.foregroundServiceControllerProvider, this.notificationEntryManagerProvider, this.notifPipelineProvider);
    }

    public static ForegroundServiceNotificationListener provideInstance(Provider<Context> provider, Provider<ForegroundServiceController> provider2, Provider<NotificationEntryManager> provider3, Provider<NotifPipeline> provider4) {
        return new ForegroundServiceNotificationListener((Context) provider.get(), (ForegroundServiceController) provider2.get(), (NotificationEntryManager) provider3.get(), (NotifPipeline) provider4.get());
    }

    public static ForegroundServiceNotificationListener_Factory create(Provider<Context> provider, Provider<ForegroundServiceController> provider2, Provider<NotificationEntryManager> provider3, Provider<NotifPipeline> provider4) {
        return new ForegroundServiceNotificationListener_Factory(provider, provider2, provider3, provider4);
    }
}
