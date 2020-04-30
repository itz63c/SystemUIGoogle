package com.android.systemui.util.wakelock;

import android.content.Context;
import com.android.systemui.util.wakelock.DelayedWakeLock.Builder;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class DelayedWakeLock_Builder_Factory implements Factory<Builder> {
    private final Provider<Context> contextProvider;

    public DelayedWakeLock_Builder_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public Builder get() {
        return provideInstance(this.contextProvider);
    }

    public static Builder provideInstance(Provider<Context> provider) {
        return new Builder((Context) provider.get());
    }

    public static DelayedWakeLock_Builder_Factory create(Provider<Context> provider) {
        return new DelayedWakeLock_Builder_Factory(provider);
    }
}
