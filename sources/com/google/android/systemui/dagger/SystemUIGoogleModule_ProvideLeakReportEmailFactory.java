package com.google.android.systemui.dagger;

import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class SystemUIGoogleModule_ProvideLeakReportEmailFactory implements Factory<String> {
    private static final SystemUIGoogleModule_ProvideLeakReportEmailFactory INSTANCE = new SystemUIGoogleModule_ProvideLeakReportEmailFactory();

    public String get() {
        return provideInstance();
    }

    public static String provideInstance() {
        return proxyProvideLeakReportEmail();
    }

    public static SystemUIGoogleModule_ProvideLeakReportEmailFactory create() {
        return INSTANCE;
    }

    public static String proxyProvideLeakReportEmail() {
        String provideLeakReportEmail = SystemUIGoogleModule.provideLeakReportEmail();
        Preconditions.checkNotNull(provideLeakReportEmail, "Cannot return null from a non-@Nullable @Provides method");
        return provideLeakReportEmail;
    }
}
