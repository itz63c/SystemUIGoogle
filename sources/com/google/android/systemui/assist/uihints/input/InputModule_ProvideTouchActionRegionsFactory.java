package com.google.android.systemui.assist.uihints.input;

import com.google.android.systemui.assist.uihints.IconController;
import com.google.android.systemui.assist.uihints.TranscriptionController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Set;
import javax.inject.Provider;

public final class InputModule_ProvideTouchActionRegionsFactory implements Factory<Set<TouchActionRegion>> {
    private final Provider<IconController> iconControllerProvider;
    private final Provider<TranscriptionController> transcriptionControllerProvider;

    public InputModule_ProvideTouchActionRegionsFactory(Provider<IconController> provider, Provider<TranscriptionController> provider2) {
        this.iconControllerProvider = provider;
        this.transcriptionControllerProvider = provider2;
    }

    public Set<TouchActionRegion> get() {
        return provideInstance(this.iconControllerProvider, this.transcriptionControllerProvider);
    }

    public static Set<TouchActionRegion> provideInstance(Provider<IconController> provider, Provider<TranscriptionController> provider2) {
        return proxyProvideTouchActionRegions((IconController) provider.get(), (TranscriptionController) provider2.get());
    }

    public static InputModule_ProvideTouchActionRegionsFactory create(Provider<IconController> provider, Provider<TranscriptionController> provider2) {
        return new InputModule_ProvideTouchActionRegionsFactory(provider, provider2);
    }

    public static Set<TouchActionRegion> proxyProvideTouchActionRegions(IconController iconController, TranscriptionController transcriptionController) {
        Set<TouchActionRegion> provideTouchActionRegions = InputModule.provideTouchActionRegions(iconController, transcriptionController);
        Preconditions.checkNotNull(provideTouchActionRegions, "Cannot return null from a non-@Nullable @Provides method");
        return provideTouchActionRegions;
    }
}
