package com.android.systemui.screenrecord;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class RecordingController_Factory implements Factory<RecordingController> {
    private final Provider<Context> contextProvider;

    public RecordingController_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public RecordingController get() {
        return provideInstance(this.contextProvider);
    }

    public static RecordingController provideInstance(Provider<Context> provider) {
        return new RecordingController((Context) provider.get());
    }

    public static RecordingController_Factory create(Provider<Context> provider) {
        return new RecordingController_Factory(provider);
    }
}
