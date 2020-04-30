package com.google.android.systemui.assist.uihints;

import dagger.internal.Factory;

public final class FlingVelocityWrapper_Factory implements Factory<FlingVelocityWrapper> {
    private static final FlingVelocityWrapper_Factory INSTANCE = new FlingVelocityWrapper_Factory();

    public FlingVelocityWrapper get() {
        return provideInstance();
    }

    public static FlingVelocityWrapper provideInstance() {
        return new FlingVelocityWrapper();
    }

    public static FlingVelocityWrapper_Factory create() {
        return INSTANCE;
    }
}
