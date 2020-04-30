package com.google.android.systemui.elmyra.feedback;

import com.google.android.systemui.assist.AssistManagerGoogle;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class AssistInvocationEffect_Factory implements Factory<AssistInvocationEffect> {
    private final Provider<AssistManagerGoogle> assistManagerGoogleProvider;
    private final Provider<OpaHomeButton> opaHomeButtonProvider;
    private final Provider<OpaLockscreen> opaLockscreenProvider;

    public AssistInvocationEffect_Factory(Provider<AssistManagerGoogle> provider, Provider<OpaHomeButton> provider2, Provider<OpaLockscreen> provider3) {
        this.assistManagerGoogleProvider = provider;
        this.opaHomeButtonProvider = provider2;
        this.opaLockscreenProvider = provider3;
    }

    public AssistInvocationEffect get() {
        return provideInstance(this.assistManagerGoogleProvider, this.opaHomeButtonProvider, this.opaLockscreenProvider);
    }

    public static AssistInvocationEffect provideInstance(Provider<AssistManagerGoogle> provider, Provider<OpaHomeButton> provider2, Provider<OpaLockscreen> provider3) {
        return new AssistInvocationEffect((AssistManagerGoogle) provider.get(), (OpaHomeButton) provider2.get(), (OpaLockscreen) provider3.get());
    }

    public static AssistInvocationEffect_Factory create(Provider<AssistManagerGoogle> provider, Provider<OpaHomeButton> provider2, Provider<OpaLockscreen> provider3) {
        return new AssistInvocationEffect_Factory(provider, provider2, provider3);
    }
}
