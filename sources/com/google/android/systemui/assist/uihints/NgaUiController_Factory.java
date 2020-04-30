package com.google.android.systemui.assist.uihints;

import android.content.Context;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NavigationBarController;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NgaUiController_Factory implements Factory<NgaUiController> {
    private final Provider<AssistManager> assistManagerProvider;
    private final Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider;
    private final Provider<AssistantWarmer> assistantWarmerProvider;
    private final Provider<ColorChangeHandler> colorChangeHandlerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<EdgeLightsController> edgeLightsControllerProvider;
    private final Provider<FlingVelocityWrapper> flingVelocityProvider;
    private final Provider<GlowController> glowControllerProvider;
    private final Provider<IconController> iconControllerProvider;
    private final Provider<LightnessProvider> lightnessProvider;
    private final Provider<NavigationBarController> navigationBarControllerProvider;
    private final Provider<ScrimController> scrimControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<TimeoutManager> timeoutManagerProvider;
    private final Provider<TouchInsideHandler> touchInsideHandlerProvider;
    private final Provider<TranscriptionController> transcriptionControllerProvider;
    private final Provider<OverlayUiHost> uiHostProvider;

    public NgaUiController_Factory(Provider<Context> provider, Provider<TimeoutManager> provider2, Provider<AssistantPresenceHandler> provider3, Provider<TouchInsideHandler> provider4, Provider<ColorChangeHandler> provider5, Provider<OverlayUiHost> provider6, Provider<EdgeLightsController> provider7, Provider<GlowController> provider8, Provider<ScrimController> provider9, Provider<TranscriptionController> provider10, Provider<IconController> provider11, Provider<LightnessProvider> provider12, Provider<StatusBarStateController> provider13, Provider<AssistManager> provider14, Provider<NavigationBarController> provider15, Provider<FlingVelocityWrapper> provider16, Provider<AssistantWarmer> provider17) {
        this.contextProvider = provider;
        this.timeoutManagerProvider = provider2;
        this.assistantPresenceHandlerProvider = provider3;
        this.touchInsideHandlerProvider = provider4;
        this.colorChangeHandlerProvider = provider5;
        this.uiHostProvider = provider6;
        this.edgeLightsControllerProvider = provider7;
        this.glowControllerProvider = provider8;
        this.scrimControllerProvider = provider9;
        this.transcriptionControllerProvider = provider10;
        this.iconControllerProvider = provider11;
        this.lightnessProvider = provider12;
        this.statusBarStateControllerProvider = provider13;
        this.assistManagerProvider = provider14;
        this.navigationBarControllerProvider = provider15;
        this.flingVelocityProvider = provider16;
        this.assistantWarmerProvider = provider17;
    }

    public NgaUiController get() {
        return provideInstance(this.contextProvider, this.timeoutManagerProvider, this.assistantPresenceHandlerProvider, this.touchInsideHandlerProvider, this.colorChangeHandlerProvider, this.uiHostProvider, this.edgeLightsControllerProvider, this.glowControllerProvider, this.scrimControllerProvider, this.transcriptionControllerProvider, this.iconControllerProvider, this.lightnessProvider, this.statusBarStateControllerProvider, this.assistManagerProvider, this.navigationBarControllerProvider, this.flingVelocityProvider, this.assistantWarmerProvider);
    }

    public static NgaUiController provideInstance(Provider<Context> provider, Provider<TimeoutManager> provider2, Provider<AssistantPresenceHandler> provider3, Provider<TouchInsideHandler> provider4, Provider<ColorChangeHandler> provider5, Provider<OverlayUiHost> provider6, Provider<EdgeLightsController> provider7, Provider<GlowController> provider8, Provider<ScrimController> provider9, Provider<TranscriptionController> provider10, Provider<IconController> provider11, Provider<LightnessProvider> provider12, Provider<StatusBarStateController> provider13, Provider<AssistManager> provider14, Provider<NavigationBarController> provider15, Provider<FlingVelocityWrapper> provider16, Provider<AssistantWarmer> provider17) {
        NgaUiController ngaUiController = new NgaUiController((Context) provider.get(), (TimeoutManager) provider2.get(), (AssistantPresenceHandler) provider3.get(), (TouchInsideHandler) provider4.get(), (ColorChangeHandler) provider5.get(), (OverlayUiHost) provider6.get(), (EdgeLightsController) provider7.get(), (GlowController) provider8.get(), (ScrimController) provider9.get(), (TranscriptionController) provider10.get(), (IconController) provider11.get(), (LightnessProvider) provider12.get(), (StatusBarStateController) provider13.get(), DoubleCheck.lazy(provider14), DoubleCheck.lazy(provider15), (FlingVelocityWrapper) provider16.get(), (AssistantWarmer) provider17.get());
        return ngaUiController;
    }

    public static NgaUiController_Factory create(Provider<Context> provider, Provider<TimeoutManager> provider2, Provider<AssistantPresenceHandler> provider3, Provider<TouchInsideHandler> provider4, Provider<ColorChangeHandler> provider5, Provider<OverlayUiHost> provider6, Provider<EdgeLightsController> provider7, Provider<GlowController> provider8, Provider<ScrimController> provider9, Provider<TranscriptionController> provider10, Provider<IconController> provider11, Provider<LightnessProvider> provider12, Provider<StatusBarStateController> provider13, Provider<AssistManager> provider14, Provider<NavigationBarController> provider15, Provider<FlingVelocityWrapper> provider16, Provider<AssistantWarmer> provider17) {
        NgaUiController_Factory ngaUiController_Factory = new NgaUiController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17);
        return ngaUiController_Factory;
    }
}
