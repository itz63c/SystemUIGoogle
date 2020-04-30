package com.google.android.systemui.assist.uihints.input;

import com.google.android.systemui.assist.uihints.GlowController;
import com.google.android.systemui.assist.uihints.ScrimController;
import com.google.android.systemui.assist.uihints.TranscriptionController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Set;
import javax.inject.Provider;

public final class InputModule_ProvideTouchInsideRegionsFactory implements Factory<Set<TouchInsideRegion>> {
    private final Provider<GlowController> glowControllerProvider;
    private final Provider<ScrimController> scrimControllerProvider;
    private final Provider<TranscriptionController> transcriptionControllerProvider;

    public InputModule_ProvideTouchInsideRegionsFactory(Provider<GlowController> provider, Provider<ScrimController> provider2, Provider<TranscriptionController> provider3) {
        this.glowControllerProvider = provider;
        this.scrimControllerProvider = provider2;
        this.transcriptionControllerProvider = provider3;
    }

    public Set<TouchInsideRegion> get() {
        return provideInstance(this.glowControllerProvider, this.scrimControllerProvider, this.transcriptionControllerProvider);
    }

    public static Set<TouchInsideRegion> provideInstance(Provider<GlowController> provider, Provider<ScrimController> provider2, Provider<TranscriptionController> provider3) {
        return proxyProvideTouchInsideRegions((GlowController) provider.get(), (ScrimController) provider2.get(), (TranscriptionController) provider3.get());
    }

    public static InputModule_ProvideTouchInsideRegionsFactory create(Provider<GlowController> provider, Provider<ScrimController> provider2, Provider<TranscriptionController> provider3) {
        return new InputModule_ProvideTouchInsideRegionsFactory(provider, provider2, provider3);
    }

    public static Set<TouchInsideRegion> proxyProvideTouchInsideRegions(GlowController glowController, ScrimController scrimController, TranscriptionController transcriptionController) {
        Set<TouchInsideRegion> provideTouchInsideRegions = InputModule.provideTouchInsideRegions(glowController, scrimController, transcriptionController);
        Preconditions.checkNotNull(provideTouchInsideRegions, "Cannot return null from a non-@Nullable @Provides method");
        return provideTouchInsideRegions;
    }
}
