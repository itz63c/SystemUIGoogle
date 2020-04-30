package com.android.systemui.util.wakelock;

import android.content.Context;
import com.android.systemui.util.wakelock.WakeLock.Builder;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class WakeLock_Builder_Factory implements Factory<Builder> {
    private final Provider<Context> contextProvider;

    public WakeLock_Builder_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public Builder get() {
        return provideInstance(this.contextProvider);
    }

    public static Builder provideInstance(Provider<Context> provider) {
        return new Builder((Context) provider.get());
    }

    public static WakeLock_Builder_Factory create(Provider<Context> provider) {
        return new WakeLock_Builder_Factory(provider);
    }
}
