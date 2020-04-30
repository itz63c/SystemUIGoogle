package com.android.systemui.assist;

import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class AssistHandleService_Factory implements Factory<AssistHandleService> {
    private final Provider<AssistManager> assistManagerProvider;

    public AssistHandleService_Factory(Provider<AssistManager> provider) {
        this.assistManagerProvider = provider;
    }

    public AssistHandleService get() {
        return provideInstance(this.assistManagerProvider);
    }

    public static AssistHandleService provideInstance(Provider<AssistManager> provider) {
        return new AssistHandleService(DoubleCheck.lazy(provider));
    }

    public static AssistHandleService_Factory create(Provider<AssistManager> provider) {
        return new AssistHandleService_Factory(provider);
    }
}
