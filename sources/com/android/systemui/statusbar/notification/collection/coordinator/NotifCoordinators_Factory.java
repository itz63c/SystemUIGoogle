package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.FeatureFlags;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotifCoordinators_Factory implements Factory<NotifCoordinators> {
    private final Provider<BubbleCoordinator> bubbleCoordinatorProvider;
    private final Provider<DeviceProvisionedCoordinator> deviceProvisionedCoordinatorProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<ForegroundCoordinator> foregroundCoordinatorProvider;
    private final Provider<HeadsUpCoordinator> headsUpCoordinatorProvider;
    private final Provider<KeyguardCoordinator> keyguardCoordinatorProvider;
    private final Provider<PreparationCoordinator> preparationCoordinatorProvider;
    private final Provider<RankingCoordinator> rankingCoordinatorProvider;

    public NotifCoordinators_Factory(Provider<DumpManager> provider, Provider<FeatureFlags> provider2, Provider<HeadsUpCoordinator> provider3, Provider<KeyguardCoordinator> provider4, Provider<RankingCoordinator> provider5, Provider<ForegroundCoordinator> provider6, Provider<DeviceProvisionedCoordinator> provider7, Provider<BubbleCoordinator> provider8, Provider<PreparationCoordinator> provider9) {
        this.dumpManagerProvider = provider;
        this.featureFlagsProvider = provider2;
        this.headsUpCoordinatorProvider = provider3;
        this.keyguardCoordinatorProvider = provider4;
        this.rankingCoordinatorProvider = provider5;
        this.foregroundCoordinatorProvider = provider6;
        this.deviceProvisionedCoordinatorProvider = provider7;
        this.bubbleCoordinatorProvider = provider8;
        this.preparationCoordinatorProvider = provider9;
    }

    public NotifCoordinators get() {
        return provideInstance(this.dumpManagerProvider, this.featureFlagsProvider, this.headsUpCoordinatorProvider, this.keyguardCoordinatorProvider, this.rankingCoordinatorProvider, this.foregroundCoordinatorProvider, this.deviceProvisionedCoordinatorProvider, this.bubbleCoordinatorProvider, this.preparationCoordinatorProvider);
    }

    public static NotifCoordinators provideInstance(Provider<DumpManager> provider, Provider<FeatureFlags> provider2, Provider<HeadsUpCoordinator> provider3, Provider<KeyguardCoordinator> provider4, Provider<RankingCoordinator> provider5, Provider<ForegroundCoordinator> provider6, Provider<DeviceProvisionedCoordinator> provider7, Provider<BubbleCoordinator> provider8, Provider<PreparationCoordinator> provider9) {
        NotifCoordinators notifCoordinators = new NotifCoordinators((DumpManager) provider.get(), (FeatureFlags) provider2.get(), (HeadsUpCoordinator) provider3.get(), (KeyguardCoordinator) provider4.get(), (RankingCoordinator) provider5.get(), (ForegroundCoordinator) provider6.get(), (DeviceProvisionedCoordinator) provider7.get(), (BubbleCoordinator) provider8.get(), (PreparationCoordinator) provider9.get());
        return notifCoordinators;
    }

    public static NotifCoordinators_Factory create(Provider<DumpManager> provider, Provider<FeatureFlags> provider2, Provider<HeadsUpCoordinator> provider3, Provider<KeyguardCoordinator> provider4, Provider<RankingCoordinator> provider5, Provider<ForegroundCoordinator> provider6, Provider<DeviceProvisionedCoordinator> provider7, Provider<BubbleCoordinator> provider8, Provider<PreparationCoordinator> provider9) {
        NotifCoordinators_Factory notifCoordinators_Factory = new NotifCoordinators_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
        return notifCoordinators_Factory;
    }
}
