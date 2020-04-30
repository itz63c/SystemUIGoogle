package com.google.android.systemui.columbus.feedback;

import com.android.systemui.assist.AssistManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class AssistInvocationEffect_Factory implements Factory<AssistInvocationEffect> {
    private final Provider<AssistManager> assistManagerProvider;

    public AssistInvocationEffect_Factory(Provider<AssistManager> provider) {
        this.assistManagerProvider = provider;
    }

    public AssistInvocationEffect get() {
        return provideInstance(this.assistManagerProvider);
    }

    public static AssistInvocationEffect provideInstance(Provider<AssistManager> provider) {
        return new AssistInvocationEffect((AssistManager) provider.get());
    }

    public static AssistInvocationEffect_Factory create(Provider<AssistManager> provider) {
        return new AssistInvocationEffect_Factory(provider);
    }
}
