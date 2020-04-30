package com.google.android.systemui.assist.uihints;

import com.android.systemui.assist.AssistManager;
import com.android.systemui.statusbar.phone.NavigationModeController;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class TouchInsideHandler_Factory implements Factory<TouchInsideHandler> {
    private final Provider<AssistManager> assistManagerProvider;
    private final Provider<NavigationModeController> navigationModeControllerProvider;

    public TouchInsideHandler_Factory(Provider<AssistManager> provider, Provider<NavigationModeController> provider2) {
        this.assistManagerProvider = provider;
        this.navigationModeControllerProvider = provider2;
    }

    public TouchInsideHandler get() {
        return provideInstance(this.assistManagerProvider, this.navigationModeControllerProvider);
    }

    public static TouchInsideHandler provideInstance(Provider<AssistManager> provider, Provider<NavigationModeController> provider2) {
        return new TouchInsideHandler(DoubleCheck.lazy(provider), (NavigationModeController) provider2.get());
    }

    public static TouchInsideHandler_Factory create(Provider<AssistManager> provider, Provider<NavigationModeController> provider2) {
        return new TouchInsideHandler_Factory(provider, provider2);
    }
}
