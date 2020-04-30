package com.google.android.systemui.assist.uihints;

import dagger.internal.Factory;

public final class GoBackHandler_Factory implements Factory<GoBackHandler> {
    private static final GoBackHandler_Factory INSTANCE = new GoBackHandler_Factory();

    public GoBackHandler get() {
        return provideInstance();
    }

    public static GoBackHandler provideInstance() {
        return new GoBackHandler();
    }

    public static GoBackHandler_Factory create() {
        return INSTANCE;
    }
}
