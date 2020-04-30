package com.android.systemui.statusbar;

import android.content.Context;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class PulseExpansionHandler_Factory implements Factory<PulseExpansionHandler> {
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerProvider;
    private final Provider<NotificationRoundnessManager> roundnessManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<NotificationWakeUpCoordinator> wakeUpCoordinatorProvider;

    public PulseExpansionHandler_Factory(Provider<Context> provider, Provider<NotificationWakeUpCoordinator> provider2, Provider<KeyguardBypassController> provider3, Provider<HeadsUpManagerPhone> provider4, Provider<NotificationRoundnessManager> provider5, Provider<StatusBarStateController> provider6, Provider<FalsingManager> provider7) {
        this.contextProvider = provider;
        this.wakeUpCoordinatorProvider = provider2;
        this.bypassControllerProvider = provider3;
        this.headsUpManagerProvider = provider4;
        this.roundnessManagerProvider = provider5;
        this.statusBarStateControllerProvider = provider6;
        this.falsingManagerProvider = provider7;
    }

    public PulseExpansionHandler get() {
        return provideInstance(this.contextProvider, this.wakeUpCoordinatorProvider, this.bypassControllerProvider, this.headsUpManagerProvider, this.roundnessManagerProvider, this.statusBarStateControllerProvider, this.falsingManagerProvider);
    }

    public static PulseExpansionHandler provideInstance(Provider<Context> provider, Provider<NotificationWakeUpCoordinator> provider2, Provider<KeyguardBypassController> provider3, Provider<HeadsUpManagerPhone> provider4, Provider<NotificationRoundnessManager> provider5, Provider<StatusBarStateController> provider6, Provider<FalsingManager> provider7) {
        PulseExpansionHandler pulseExpansionHandler = new PulseExpansionHandler((Context) provider.get(), (NotificationWakeUpCoordinator) provider2.get(), (KeyguardBypassController) provider3.get(), (HeadsUpManagerPhone) provider4.get(), (NotificationRoundnessManager) provider5.get(), (StatusBarStateController) provider6.get(), (FalsingManager) provider7.get());
        return pulseExpansionHandler;
    }

    public static PulseExpansionHandler_Factory create(Provider<Context> provider, Provider<NotificationWakeUpCoordinator> provider2, Provider<KeyguardBypassController> provider3, Provider<HeadsUpManagerPhone> provider4, Provider<NotificationRoundnessManager> provider5, Provider<StatusBarStateController> provider6, Provider<FalsingManager> provider7) {
        PulseExpansionHandler_Factory pulseExpansionHandler_Factory = new PulseExpansionHandler_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7);
        return pulseExpansionHandler_Factory;
    }
}
