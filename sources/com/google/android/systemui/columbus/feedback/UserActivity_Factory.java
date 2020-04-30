package com.google.android.systemui.columbus.feedback;

import android.content.Context;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class UserActivity_Factory implements Factory<UserActivity> {
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;

    public UserActivity_Factory(Provider<Context> provider, Provider<KeyguardStateController> provider2) {
        this.contextProvider = provider;
        this.keyguardStateControllerProvider = provider2;
    }

    public UserActivity get() {
        return provideInstance(this.contextProvider, this.keyguardStateControllerProvider);
    }

    public static UserActivity provideInstance(Provider<Context> provider, Provider<KeyguardStateController> provider2) {
        return new UserActivity((Context) provider.get(), (KeyguardStateController) provider2.get());
    }

    public static UserActivity_Factory create(Provider<Context> provider, Provider<KeyguardStateController> provider2) {
        return new UserActivity_Factory(provider, provider2);
    }
}
