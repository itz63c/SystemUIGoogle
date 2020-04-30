package com.google.android.systemui.columbus.feedback;

import com.android.systemui.statusbar.NavigationBarController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NavUndimEffect_Factory implements Factory<NavUndimEffect> {
    private final Provider<NavigationBarController> navBarControllerProvider;

    public NavUndimEffect_Factory(Provider<NavigationBarController> provider) {
        this.navBarControllerProvider = provider;
    }

    public NavUndimEffect get() {
        return provideInstance(this.navBarControllerProvider);
    }

    public static NavUndimEffect provideInstance(Provider<NavigationBarController> provider) {
        return new NavUndimEffect((NavigationBarController) provider.get());
    }

    public static NavUndimEffect_Factory create(Provider<NavigationBarController> provider) {
        return new NavUndimEffect_Factory(provider);
    }
}
