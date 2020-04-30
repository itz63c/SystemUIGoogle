package com.google.android.systemui.assist.uihints;

import com.google.android.systemui.assist.uihints.NgaMessageHandler.EdgeLightsInfoListener;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController;
import com.google.android.systemui.assist.uihints.input.NgaInputHandler;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Set;
import javax.inject.Provider;

public final class AssistantUIHintsModule_BindEdgeLightsInfoListenersFactory implements Factory<Set<EdgeLightsInfoListener>> {
    private final Provider<EdgeLightsController> edgeLightsControllerProvider;
    private final Provider<NgaInputHandler> ngaInputHandlerProvider;

    public AssistantUIHintsModule_BindEdgeLightsInfoListenersFactory(Provider<EdgeLightsController> provider, Provider<NgaInputHandler> provider2) {
        this.edgeLightsControllerProvider = provider;
        this.ngaInputHandlerProvider = provider2;
    }

    public Set<EdgeLightsInfoListener> get() {
        return provideInstance(this.edgeLightsControllerProvider, this.ngaInputHandlerProvider);
    }

    public static Set<EdgeLightsInfoListener> provideInstance(Provider<EdgeLightsController> provider, Provider<NgaInputHandler> provider2) {
        return proxyBindEdgeLightsInfoListeners((EdgeLightsController) provider.get(), (NgaInputHandler) provider2.get());
    }

    public static AssistantUIHintsModule_BindEdgeLightsInfoListenersFactory create(Provider<EdgeLightsController> provider, Provider<NgaInputHandler> provider2) {
        return new AssistantUIHintsModule_BindEdgeLightsInfoListenersFactory(provider, provider2);
    }

    public static Set<EdgeLightsInfoListener> proxyBindEdgeLightsInfoListeners(EdgeLightsController edgeLightsController, NgaInputHandler ngaInputHandler) {
        Set<EdgeLightsInfoListener> bindEdgeLightsInfoListeners = AssistantUIHintsModule.bindEdgeLightsInfoListeners(edgeLightsController, ngaInputHandler);
        Preconditions.checkNotNull(bindEdgeLightsInfoListeners, "Cannot return null from a non-@Nullable @Provides method");
        return bindEdgeLightsInfoListeners;
    }
}
