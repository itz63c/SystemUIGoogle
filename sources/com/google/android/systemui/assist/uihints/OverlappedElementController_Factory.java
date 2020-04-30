package com.google.android.systemui.assist.uihints;

import com.android.systemui.statusbar.phone.StatusBar;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class OverlappedElementController_Factory implements Factory<OverlappedElementController> {
    private final Provider<StatusBar> statusBarLazyProvider;

    public OverlappedElementController_Factory(Provider<StatusBar> provider) {
        this.statusBarLazyProvider = provider;
    }

    public OverlappedElementController get() {
        return provideInstance(this.statusBarLazyProvider);
    }

    public static OverlappedElementController provideInstance(Provider<StatusBar> provider) {
        return new OverlappedElementController(DoubleCheck.lazy(provider));
    }

    public static OverlappedElementController_Factory create(Provider<StatusBar> provider) {
        return new OverlappedElementController_Factory(provider);
    }
}
