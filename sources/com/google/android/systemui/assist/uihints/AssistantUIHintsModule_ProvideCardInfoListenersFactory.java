package com.google.android.systemui.assist.uihints;

import com.google.android.systemui.assist.uihints.NgaMessageHandler.CardInfoListener;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Set;
import javax.inject.Provider;

public final class AssistantUIHintsModule_ProvideCardInfoListenersFactory implements Factory<Set<CardInfoListener>> {
    private final Provider<GlowController> glowControllerProvider;
    private final Provider<LightnessProvider> lightnessProvider;
    private final Provider<ScrimController> scrimControllerProvider;
    private final Provider<TranscriptionController> transcriptionControllerProvider;

    public AssistantUIHintsModule_ProvideCardInfoListenersFactory(Provider<GlowController> provider, Provider<ScrimController> provider2, Provider<TranscriptionController> provider3, Provider<LightnessProvider> provider4) {
        this.glowControllerProvider = provider;
        this.scrimControllerProvider = provider2;
        this.transcriptionControllerProvider = provider3;
        this.lightnessProvider = provider4;
    }

    public Set<CardInfoListener> get() {
        return provideInstance(this.glowControllerProvider, this.scrimControllerProvider, this.transcriptionControllerProvider, this.lightnessProvider);
    }

    public static Set<CardInfoListener> provideInstance(Provider<GlowController> provider, Provider<ScrimController> provider2, Provider<TranscriptionController> provider3, Provider<LightnessProvider> provider4) {
        return proxyProvideCardInfoListeners((GlowController) provider.get(), (ScrimController) provider2.get(), (TranscriptionController) provider3.get(), provider4.get());
    }

    public static AssistantUIHintsModule_ProvideCardInfoListenersFactory create(Provider<GlowController> provider, Provider<ScrimController> provider2, Provider<TranscriptionController> provider3, Provider<LightnessProvider> provider4) {
        return new AssistantUIHintsModule_ProvideCardInfoListenersFactory(provider, provider2, provider3, provider4);
    }

    public static Set<CardInfoListener> proxyProvideCardInfoListeners(GlowController glowController, ScrimController scrimController, TranscriptionController transcriptionController, Object obj) {
        Set<CardInfoListener> provideCardInfoListeners = AssistantUIHintsModule.provideCardInfoListeners(glowController, scrimController, transcriptionController, (LightnessProvider) obj);
        Preconditions.checkNotNull(provideCardInfoListeners, "Cannot return null from a non-@Nullable @Provides method");
        return provideCardInfoListeners;
    }
}
