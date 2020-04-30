package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.notification.collection.NotifInflaterImpl;
import com.android.systemui.statusbar.notification.collection.NotifViewBarn;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class PreparationCoordinator_Factory implements Factory<PreparationCoordinator> {
    private final Provider<NotifInflationErrorManager> errorManagerProvider;
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<PreparationCoordinatorLogger> loggerProvider;
    private final Provider<NotifInflaterImpl> notifInflaterProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider;
    private final Provider<IStatusBarService> serviceProvider;
    private final Provider<NotifViewBarn> viewBarnProvider;

    public PreparationCoordinator_Factory(Provider<PreparationCoordinatorLogger> provider, Provider<NotifInflaterImpl> provider2, Provider<NotifInflationErrorManager> provider3, Provider<NotifViewBarn> provider4, Provider<IStatusBarService> provider5, Provider<NotificationInterruptStateProvider> provider6, Provider<HeadsUpManager> provider7) {
        this.loggerProvider = provider;
        this.notifInflaterProvider = provider2;
        this.errorManagerProvider = provider3;
        this.viewBarnProvider = provider4;
        this.serviceProvider = provider5;
        this.notificationInterruptStateProvider = provider6;
        this.headsUpManagerProvider = provider7;
    }

    public PreparationCoordinator get() {
        return provideInstance(this.loggerProvider, this.notifInflaterProvider, this.errorManagerProvider, this.viewBarnProvider, this.serviceProvider, this.notificationInterruptStateProvider, this.headsUpManagerProvider);
    }

    public static PreparationCoordinator provideInstance(Provider<PreparationCoordinatorLogger> provider, Provider<NotifInflaterImpl> provider2, Provider<NotifInflationErrorManager> provider3, Provider<NotifViewBarn> provider4, Provider<IStatusBarService> provider5, Provider<NotificationInterruptStateProvider> provider6, Provider<HeadsUpManager> provider7) {
        PreparationCoordinator preparationCoordinator = new PreparationCoordinator((PreparationCoordinatorLogger) provider.get(), (NotifInflaterImpl) provider2.get(), (NotifInflationErrorManager) provider3.get(), (NotifViewBarn) provider4.get(), (IStatusBarService) provider5.get(), (NotificationInterruptStateProvider) provider6.get(), (HeadsUpManager) provider7.get());
        return preparationCoordinator;
    }

    public static PreparationCoordinator_Factory create(Provider<PreparationCoordinatorLogger> provider, Provider<NotifInflaterImpl> provider2, Provider<NotifInflationErrorManager> provider3, Provider<NotifViewBarn> provider4, Provider<IStatusBarService> provider5, Provider<NotificationInterruptStateProvider> provider6, Provider<HeadsUpManager> provider7) {
        PreparationCoordinator_Factory preparationCoordinator_Factory = new PreparationCoordinator_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7);
        return preparationCoordinator_Factory;
    }
}
