package com.android.systemui.statusbar.notification.interruption;

import android.content.ContentResolver;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Handler;
import android.os.PowerManager;
import android.service.dreams.IDreamManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotificationInterruptStateProviderImpl_Factory implements Factory<NotificationInterruptStateProviderImpl> {
    private final Provider<AmbientDisplayConfiguration> ambientDisplayConfigurationProvider;
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<ContentResolver> contentResolverProvider;
    private final Provider<IDreamManager> dreamManagerProvider;
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NotificationFilter> notificationFilterProvider;
    private final Provider<PowerManager> powerManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public NotificationInterruptStateProviderImpl_Factory(Provider<ContentResolver> provider, Provider<PowerManager> provider2, Provider<IDreamManager> provider3, Provider<AmbientDisplayConfiguration> provider4, Provider<NotificationFilter> provider5, Provider<BatteryController> provider6, Provider<StatusBarStateController> provider7, Provider<HeadsUpManager> provider8, Provider<Handler> provider9) {
        this.contentResolverProvider = provider;
        this.powerManagerProvider = provider2;
        this.dreamManagerProvider = provider3;
        this.ambientDisplayConfigurationProvider = provider4;
        this.notificationFilterProvider = provider5;
        this.batteryControllerProvider = provider6;
        this.statusBarStateControllerProvider = provider7;
        this.headsUpManagerProvider = provider8;
        this.mainHandlerProvider = provider9;
    }

    public NotificationInterruptStateProviderImpl get() {
        return provideInstance(this.contentResolverProvider, this.powerManagerProvider, this.dreamManagerProvider, this.ambientDisplayConfigurationProvider, this.notificationFilterProvider, this.batteryControllerProvider, this.statusBarStateControllerProvider, this.headsUpManagerProvider, this.mainHandlerProvider);
    }

    public static NotificationInterruptStateProviderImpl provideInstance(Provider<ContentResolver> provider, Provider<PowerManager> provider2, Provider<IDreamManager> provider3, Provider<AmbientDisplayConfiguration> provider4, Provider<NotificationFilter> provider5, Provider<BatteryController> provider6, Provider<StatusBarStateController> provider7, Provider<HeadsUpManager> provider8, Provider<Handler> provider9) {
        NotificationInterruptStateProviderImpl notificationInterruptStateProviderImpl = new NotificationInterruptStateProviderImpl((ContentResolver) provider.get(), (PowerManager) provider2.get(), (IDreamManager) provider3.get(), (AmbientDisplayConfiguration) provider4.get(), (NotificationFilter) provider5.get(), (BatteryController) provider6.get(), (StatusBarStateController) provider7.get(), (HeadsUpManager) provider8.get(), (Handler) provider9.get());
        return notificationInterruptStateProviderImpl;
    }

    public static NotificationInterruptStateProviderImpl_Factory create(Provider<ContentResolver> provider, Provider<PowerManager> provider2, Provider<IDreamManager> provider3, Provider<AmbientDisplayConfiguration> provider4, Provider<NotificationFilter> provider5, Provider<BatteryController> provider6, Provider<StatusBarStateController> provider7, Provider<HeadsUpManager> provider8, Provider<Handler> provider9) {
        NotificationInterruptStateProviderImpl_Factory notificationInterruptStateProviderImpl_Factory = new NotificationInterruptStateProviderImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
        return notificationInterruptStateProviderImpl_Factory;
    }
}
