package com.google.android.systemui.assist.uihints;

import dagger.internal.Factory;

public final class LightnessProvider_Factory implements Factory<LightnessProvider> {
    private static final LightnessProvider_Factory INSTANCE = new LightnessProvider_Factory();

    public LightnessProvider get() {
        return provideInstance();
    }

    public static LightnessProvider provideInstance() {
        return new LightnessProvider();
    }

    public static LightnessProvider_Factory create() {
        return INSTANCE;
    }
}
