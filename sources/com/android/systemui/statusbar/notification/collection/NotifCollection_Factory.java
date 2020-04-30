package com.android.systemui.statusbar.notification.collection;

import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionLogger;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotifCollection_Factory implements Factory<NotifCollection> {
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<NotifCollectionLogger> loggerProvider;
    private final Provider<IStatusBarService> statusBarServiceProvider;

    public NotifCollection_Factory(Provider<IStatusBarService> provider, Provider<DumpManager> provider2, Provider<FeatureFlags> provider3, Provider<NotifCollectionLogger> provider4) {
        this.statusBarServiceProvider = provider;
        this.dumpManagerProvider = provider2;
        this.featureFlagsProvider = provider3;
        this.loggerProvider = provider4;
    }

    public NotifCollection get() {
        return provideInstance(this.statusBarServiceProvider, this.dumpManagerProvider, this.featureFlagsProvider, this.loggerProvider);
    }

    public static NotifCollection provideInstance(Provider<IStatusBarService> provider, Provider<DumpManager> provider2, Provider<FeatureFlags> provider3, Provider<NotifCollectionLogger> provider4) {
        return new NotifCollection((IStatusBarService) provider.get(), (DumpManager) provider2.get(), (FeatureFlags) provider3.get(), (NotifCollectionLogger) provider4.get());
    }

    public static NotifCollection_Factory create(Provider<IStatusBarService> provider, Provider<DumpManager> provider2, Provider<FeatureFlags> provider3, Provider<NotifCollectionLogger> provider4) {
        return new NotifCollection_Factory(provider, provider2, provider3, provider4);
    }
}
