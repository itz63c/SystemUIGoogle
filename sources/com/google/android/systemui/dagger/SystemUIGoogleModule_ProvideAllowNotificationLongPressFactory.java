package com.google.android.systemui.dagger;

import dagger.internal.Factory;

public final class SystemUIGoogleModule_ProvideAllowNotificationLongPressFactory implements Factory<Boolean> {
    private static final SystemUIGoogleModule_ProvideAllowNotificationLongPressFactory INSTANCE = new SystemUIGoogleModule_ProvideAllowNotificationLongPressFactory();

    public Boolean get() {
        return provideInstance();
    }

    public static Boolean provideInstance() {
        proxyProvideAllowNotificationLongPress();
        return Boolean.TRUE;
    }

    public static SystemUIGoogleModule_ProvideAllowNotificationLongPressFactory create() {
        return INSTANCE;
    }

    public static boolean proxyProvideAllowNotificationLongPress() {
        return SystemUIGoogleModule.provideAllowNotificationLongPress();
    }
}
