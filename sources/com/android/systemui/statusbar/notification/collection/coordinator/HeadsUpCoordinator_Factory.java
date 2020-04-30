package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class HeadsUpCoordinator_Factory implements Factory<HeadsUpCoordinator> {
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;

    public HeadsUpCoordinator_Factory(Provider<HeadsUpManager> provider, Provider<NotificationRemoteInputManager> provider2) {
        this.headsUpManagerProvider = provider;
        this.remoteInputManagerProvider = provider2;
    }

    public HeadsUpCoordinator get() {
        return provideInstance(this.headsUpManagerProvider, this.remoteInputManagerProvider);
    }

    public static HeadsUpCoordinator provideInstance(Provider<HeadsUpManager> provider, Provider<NotificationRemoteInputManager> provider2) {
        return new HeadsUpCoordinator((HeadsUpManager) provider.get(), (NotificationRemoteInputManager) provider2.get());
    }

    public static HeadsUpCoordinator_Factory create(Provider<HeadsUpManager> provider, Provider<NotificationRemoteInputManager> provider2) {
        return new HeadsUpCoordinator_Factory(provider, provider2);
    }
}
