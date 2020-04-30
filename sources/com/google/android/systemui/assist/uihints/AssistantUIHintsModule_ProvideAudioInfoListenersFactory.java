package com.google.android.systemui.assist.uihints;

import com.google.android.systemui.assist.uihints.NgaMessageHandler.AudioInfoListener;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Set;
import javax.inject.Provider;

public final class AssistantUIHintsModule_ProvideAudioInfoListenersFactory implements Factory<Set<AudioInfoListener>> {
    private final Provider<EdgeLightsController> edgeLightsControllerProvider;
    private final Provider<GlowController> glowControllerProvider;

    public AssistantUIHintsModule_ProvideAudioInfoListenersFactory(Provider<EdgeLightsController> provider, Provider<GlowController> provider2) {
        this.edgeLightsControllerProvider = provider;
        this.glowControllerProvider = provider2;
    }

    public Set<AudioInfoListener> get() {
        return provideInstance(this.edgeLightsControllerProvider, this.glowControllerProvider);
    }

    public static Set<AudioInfoListener> provideInstance(Provider<EdgeLightsController> provider, Provider<GlowController> provider2) {
        return proxyProvideAudioInfoListeners((EdgeLightsController) provider.get(), (GlowController) provider2.get());
    }

    public static AssistantUIHintsModule_ProvideAudioInfoListenersFactory create(Provider<EdgeLightsController> provider, Provider<GlowController> provider2) {
        return new AssistantUIHintsModule_ProvideAudioInfoListenersFactory(provider, provider2);
    }

    public static Set<AudioInfoListener> proxyProvideAudioInfoListeners(EdgeLightsController edgeLightsController, GlowController glowController) {
        Set<AudioInfoListener> provideAudioInfoListeners = AssistantUIHintsModule.provideAudioInfoListeners(edgeLightsController, glowController);
        Preconditions.checkNotNull(provideAudioInfoListeners, "Cannot return null from a non-@Nullable @Provides method");
        return provideAudioInfoListeners;
    }
}
