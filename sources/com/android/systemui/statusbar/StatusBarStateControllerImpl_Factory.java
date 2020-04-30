package com.android.systemui.statusbar;

import dagger.internal.Factory;

public final class StatusBarStateControllerImpl_Factory implements Factory<StatusBarStateControllerImpl> {
    private static final StatusBarStateControllerImpl_Factory INSTANCE = new StatusBarStateControllerImpl_Factory();

    public StatusBarStateControllerImpl get() {
        return provideInstance();
    }

    public static StatusBarStateControllerImpl provideInstance() {
        return new StatusBarStateControllerImpl();
    }

    public static StatusBarStateControllerImpl_Factory create() {
        return INSTANCE;
    }
}
