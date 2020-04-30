package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.android.systemui.statusbar.phone.NavigationModeController;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NonGesturalNavigation_Factory implements Factory<NonGesturalNavigation> {
    private final Provider<Context> contextProvider;
    private final Provider<NavigationModeController> modeControllerProvider;

    public NonGesturalNavigation_Factory(Provider<Context> provider, Provider<NavigationModeController> provider2) {
        this.contextProvider = provider;
        this.modeControllerProvider = provider2;
    }

    public NonGesturalNavigation get() {
        return provideInstance(this.contextProvider, this.modeControllerProvider);
    }

    public static NonGesturalNavigation provideInstance(Provider<Context> provider, Provider<NavigationModeController> provider2) {
        return new NonGesturalNavigation((Context) provider.get(), DoubleCheck.lazy(provider2));
    }

    public static NonGesturalNavigation_Factory create(Provider<Context> provider, Provider<NavigationModeController> provider2) {
        return new NonGesturalNavigation_Factory(provider, provider2);
    }
}
