package com.google.android.systemui.assist.uihints;

import android.view.ViewGroup;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class AssistantUIHintsModule_ProvideParentViewGroupFactory implements Factory<ViewGroup> {
    private final Provider<OverlayUiHost> overlayUiHostProvider;

    public AssistantUIHintsModule_ProvideParentViewGroupFactory(Provider<OverlayUiHost> provider) {
        this.overlayUiHostProvider = provider;
    }

    public ViewGroup get() {
        return provideInstance(this.overlayUiHostProvider);
    }

    public static ViewGroup provideInstance(Provider<OverlayUiHost> provider) {
        return proxyProvideParentViewGroup(provider.get());
    }

    public static AssistantUIHintsModule_ProvideParentViewGroupFactory create(Provider<OverlayUiHost> provider) {
        return new AssistantUIHintsModule_ProvideParentViewGroupFactory(provider);
    }

    public static ViewGroup proxyProvideParentViewGroup(Object obj) {
        ViewGroup provideParentViewGroup = AssistantUIHintsModule.provideParentViewGroup((OverlayUiHost) obj);
        Preconditions.checkNotNull(provideParentViewGroup, "Cannot return null from a non-@Nullable @Provides method");
        return provideParentViewGroup;
    }
}
