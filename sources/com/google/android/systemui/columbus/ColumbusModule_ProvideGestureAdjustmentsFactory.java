package com.google.android.systemui.columbus;

import com.google.android.systemui.columbus.sensors.config.Adjustment;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Set;

public final class ColumbusModule_ProvideGestureAdjustmentsFactory implements Factory<Set<Adjustment>> {
    private static final ColumbusModule_ProvideGestureAdjustmentsFactory INSTANCE = new ColumbusModule_ProvideGestureAdjustmentsFactory();

    public Set<Adjustment> get() {
        return provideInstance();
    }

    public static Set<Adjustment> provideInstance() {
        return proxyProvideGestureAdjustments();
    }

    public static ColumbusModule_ProvideGestureAdjustmentsFactory create() {
        return INSTANCE;
    }

    public static Set<Adjustment> proxyProvideGestureAdjustments() {
        Set<Adjustment> provideGestureAdjustments = ColumbusModule.provideGestureAdjustments();
        Preconditions.checkNotNull(provideGestureAdjustments, "Cannot return null from a non-@Nullable @Provides method");
        return provideGestureAdjustments;
    }
}
