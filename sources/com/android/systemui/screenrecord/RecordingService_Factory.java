package com.android.systemui.screenrecord;

import dagger.internal.Factory;
import javax.inject.Provider;

public final class RecordingService_Factory implements Factory<RecordingService> {
    private final Provider<RecordingController> controllerProvider;

    public RecordingService_Factory(Provider<RecordingController> provider) {
        this.controllerProvider = provider;
    }

    public RecordingService get() {
        return provideInstance(this.controllerProvider);
    }

    public static RecordingService provideInstance(Provider<RecordingController> provider) {
        return new RecordingService((RecordingController) provider.get());
    }

    public static RecordingService_Factory create(Provider<RecordingController> provider) {
        return new RecordingService_Factory(provider);
    }
}
