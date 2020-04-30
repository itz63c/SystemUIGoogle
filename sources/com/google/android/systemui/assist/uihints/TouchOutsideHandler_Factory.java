package com.google.android.systemui.assist.uihints;

import dagger.internal.Factory;

public final class TouchOutsideHandler_Factory implements Factory<TouchOutsideHandler> {
    private static final TouchOutsideHandler_Factory INSTANCE = new TouchOutsideHandler_Factory();

    public TouchOutsideHandler get() {
        return provideInstance();
    }

    public static TouchOutsideHandler provideInstance() {
        return new TouchOutsideHandler();
    }

    public static TouchOutsideHandler_Factory create() {
        return INSTANCE;
    }
}
