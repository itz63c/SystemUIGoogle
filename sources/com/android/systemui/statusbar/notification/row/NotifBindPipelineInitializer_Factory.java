package com.android.systemui.statusbar.notification.row;

import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotifBindPipelineInitializer_Factory implements Factory<NotifBindPipelineInitializer> {
    private final Provider<NotifBindPipeline> pipelineProvider;
    private final Provider<RowContentBindStage> stageProvider;

    public NotifBindPipelineInitializer_Factory(Provider<NotifBindPipeline> provider, Provider<RowContentBindStage> provider2) {
        this.pipelineProvider = provider;
        this.stageProvider = provider2;
    }

    public NotifBindPipelineInitializer get() {
        return provideInstance(this.pipelineProvider, this.stageProvider);
    }

    public static NotifBindPipelineInitializer provideInstance(Provider<NotifBindPipeline> provider, Provider<RowContentBindStage> provider2) {
        return new NotifBindPipelineInitializer((NotifBindPipeline) provider.get(), (RowContentBindStage) provider2.get());
    }

    public static NotifBindPipelineInitializer_Factory create(Provider<NotifBindPipeline> provider, Provider<RowContentBindStage> provider2) {
        return new NotifBindPipelineInitializer_Factory(provider, provider2);
    }
}
