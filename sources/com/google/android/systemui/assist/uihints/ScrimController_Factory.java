package com.google.android.systemui.assist.uihints;

import android.view.ViewGroup;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ScrimController_Factory implements Factory<ScrimController> {
    private final Provider<LightnessProvider> lightnessProvider;
    private final Provider<OverlappedElementController> overlappedElementControllerProvider;
    private final Provider<ViewGroup> parentProvider;
    private final Provider<TouchInsideHandler> touchInsideHandlerProvider;

    public ScrimController_Factory(Provider<ViewGroup> provider, Provider<OverlappedElementController> provider2, Provider<LightnessProvider> provider3, Provider<TouchInsideHandler> provider4) {
        this.parentProvider = provider;
        this.overlappedElementControllerProvider = provider2;
        this.lightnessProvider = provider3;
        this.touchInsideHandlerProvider = provider4;
    }

    public ScrimController get() {
        return provideInstance(this.parentProvider, this.overlappedElementControllerProvider, this.lightnessProvider, this.touchInsideHandlerProvider);
    }

    public static ScrimController provideInstance(Provider<ViewGroup> provider, Provider<OverlappedElementController> provider2, Provider<LightnessProvider> provider3, Provider<TouchInsideHandler> provider4) {
        return new ScrimController((ViewGroup) provider.get(), (OverlappedElementController) provider2.get(), (LightnessProvider) provider3.get(), (TouchInsideHandler) provider4.get());
    }

    public static ScrimController_Factory create(Provider<ViewGroup> provider, Provider<OverlappedElementController> provider2, Provider<LightnessProvider> provider3, Provider<TouchInsideHandler> provider4) {
        return new ScrimController_Factory(provider, provider2, provider3, provider4);
    }
}
