package com.android.systemui.p007qs;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.AutoAddTracker_Factory */
public final class AutoAddTracker_Factory implements Factory<AutoAddTracker> {
    private final Provider<Context> contextProvider;

    public AutoAddTracker_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public AutoAddTracker get() {
        return provideInstance(this.contextProvider);
    }

    public static AutoAddTracker provideInstance(Provider<Context> provider) {
        return new AutoAddTracker((Context) provider.get());
    }

    public static AutoAddTracker_Factory create(Provider<Context> provider) {
        return new AutoAddTracker_Factory(provider);
    }
}
