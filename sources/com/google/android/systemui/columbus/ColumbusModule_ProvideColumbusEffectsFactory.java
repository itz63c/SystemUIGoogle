package com.google.android.systemui.columbus;

import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import com.google.android.systemui.columbus.feedback.HapticClick;
import com.google.android.systemui.columbus.feedback.NavUndimEffect;
import com.google.android.systemui.columbus.feedback.UserActivity;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Set;
import javax.inject.Provider;

public final class ColumbusModule_ProvideColumbusEffectsFactory implements Factory<Set<FeedbackEffect>> {
    private final Provider<HapticClick> hapticClickProvider;
    private final Provider<NavUndimEffect> navUndimEffectProvider;
    private final Provider<UserActivity> userActivityProvider;

    public ColumbusModule_ProvideColumbusEffectsFactory(Provider<HapticClick> provider, Provider<NavUndimEffect> provider2, Provider<UserActivity> provider3) {
        this.hapticClickProvider = provider;
        this.navUndimEffectProvider = provider2;
        this.userActivityProvider = provider3;
    }

    public Set<FeedbackEffect> get() {
        return provideInstance(this.hapticClickProvider, this.navUndimEffectProvider, this.userActivityProvider);
    }

    public static Set<FeedbackEffect> provideInstance(Provider<HapticClick> provider, Provider<NavUndimEffect> provider2, Provider<UserActivity> provider3) {
        return proxyProvideColumbusEffects((HapticClick) provider.get(), (NavUndimEffect) provider2.get(), (UserActivity) provider3.get());
    }

    public static ColumbusModule_ProvideColumbusEffectsFactory create(Provider<HapticClick> provider, Provider<NavUndimEffect> provider2, Provider<UserActivity> provider3) {
        return new ColumbusModule_ProvideColumbusEffectsFactory(provider, provider2, provider3);
    }

    public static Set<FeedbackEffect> proxyProvideColumbusEffects(HapticClick hapticClick, NavUndimEffect navUndimEffect, UserActivity userActivity) {
        Set<FeedbackEffect> provideColumbusEffects = ColumbusModule.provideColumbusEffects(hapticClick, navUndimEffect, userActivity);
        Preconditions.checkNotNull(provideColumbusEffects, "Cannot return null from a non-@Nullable @Provides method");
        return provideColumbusEffects;
    }
}
