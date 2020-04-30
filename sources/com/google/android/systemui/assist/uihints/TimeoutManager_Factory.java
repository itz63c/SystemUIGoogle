package com.google.android.systemui.assist.uihints;

import com.android.systemui.assist.AssistManager;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class TimeoutManager_Factory implements Factory<TimeoutManager> {
    private final Provider<AssistManager> assistManagerProvider;

    public TimeoutManager_Factory(Provider<AssistManager> provider) {
        this.assistManagerProvider = provider;
    }

    public TimeoutManager get() {
        return provideInstance(this.assistManagerProvider);
    }

    public static TimeoutManager provideInstance(Provider<AssistManager> provider) {
        return new TimeoutManager(DoubleCheck.lazy(provider));
    }

    public static TimeoutManager_Factory create(Provider<AssistManager> provider) {
        return new TimeoutManager_Factory(provider);
    }
}
