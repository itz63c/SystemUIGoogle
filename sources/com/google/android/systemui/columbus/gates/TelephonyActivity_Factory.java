package com.google.android.systemui.columbus.gates;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class TelephonyActivity_Factory implements Factory<TelephonyActivity> {
    private final Provider<Context> contextProvider;

    public TelephonyActivity_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public TelephonyActivity get() {
        return provideInstance(this.contextProvider);
    }

    public static TelephonyActivity provideInstance(Provider<Context> provider) {
        return new TelephonyActivity((Context) provider.get());
    }

    public static TelephonyActivity_Factory create(Provider<Context> provider) {
        return new TelephonyActivity_Factory(provider);
    }
}
