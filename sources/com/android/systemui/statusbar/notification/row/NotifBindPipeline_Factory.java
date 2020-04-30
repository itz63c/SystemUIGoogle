package com.android.systemui.statusbar.notification.row;

import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotifBindPipeline_Factory implements Factory<NotifBindPipeline> {
    private final Provider<CommonNotifCollection> collectionProvider;
    private final Provider<NotifBindPipelineLogger> loggerProvider;

    public NotifBindPipeline_Factory(Provider<CommonNotifCollection> provider, Provider<NotifBindPipelineLogger> provider2) {
        this.collectionProvider = provider;
        this.loggerProvider = provider2;
    }

    public NotifBindPipeline get() {
        return provideInstance(this.collectionProvider, this.loggerProvider);
    }

    public static NotifBindPipeline provideInstance(Provider<CommonNotifCollection> provider, Provider<NotifBindPipelineLogger> provider2) {
        return new NotifBindPipeline((CommonNotifCollection) provider.get(), (NotifBindPipelineLogger) provider2.get());
    }

    public static NotifBindPipeline_Factory create(Provider<CommonNotifCollection> provider, Provider<NotifBindPipelineLogger> provider2) {
        return new NotifBindPipeline_Factory(provider, provider2);
    }
}
