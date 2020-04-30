package com.android.systemui.statusbar.notification.collection;

import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.notification.NotificationEntryManagerLogger;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotificationRankingManager_Factory implements Factory<NotificationRankingManager> {
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<HighPriorityProvider> highPriorityProvider;
    private final Provider<NotificationEntryManagerLogger> loggerProvider;
    private final Provider<NotificationMediaManager> mediaManagerLazyProvider;
    private final Provider<NotificationFilter> notifFilterProvider;
    private final Provider<PeopleNotificationIdentifier> peopleNotificationIdentifierProvider;
    private final Provider<NotificationSectionsFeatureManager> sectionsFeatureManagerProvider;

    public NotificationRankingManager_Factory(Provider<NotificationMediaManager> provider, Provider<NotificationGroupManager> provider2, Provider<HeadsUpManager> provider3, Provider<NotificationFilter> provider4, Provider<NotificationEntryManagerLogger> provider5, Provider<NotificationSectionsFeatureManager> provider6, Provider<PeopleNotificationIdentifier> provider7, Provider<HighPriorityProvider> provider8) {
        this.mediaManagerLazyProvider = provider;
        this.groupManagerProvider = provider2;
        this.headsUpManagerProvider = provider3;
        this.notifFilterProvider = provider4;
        this.loggerProvider = provider5;
        this.sectionsFeatureManagerProvider = provider6;
        this.peopleNotificationIdentifierProvider = provider7;
        this.highPriorityProvider = provider8;
    }

    public NotificationRankingManager get() {
        return provideInstance(this.mediaManagerLazyProvider, this.groupManagerProvider, this.headsUpManagerProvider, this.notifFilterProvider, this.loggerProvider, this.sectionsFeatureManagerProvider, this.peopleNotificationIdentifierProvider, this.highPriorityProvider);
    }

    public static NotificationRankingManager provideInstance(Provider<NotificationMediaManager> provider, Provider<NotificationGroupManager> provider2, Provider<HeadsUpManager> provider3, Provider<NotificationFilter> provider4, Provider<NotificationEntryManagerLogger> provider5, Provider<NotificationSectionsFeatureManager> provider6, Provider<PeopleNotificationIdentifier> provider7, Provider<HighPriorityProvider> provider8) {
        NotificationRankingManager notificationRankingManager = new NotificationRankingManager(DoubleCheck.lazy(provider), (NotificationGroupManager) provider2.get(), (HeadsUpManager) provider3.get(), (NotificationFilter) provider4.get(), (NotificationEntryManagerLogger) provider5.get(), (NotificationSectionsFeatureManager) provider6.get(), (PeopleNotificationIdentifier) provider7.get(), (HighPriorityProvider) provider8.get());
        return notificationRankingManager;
    }

    public static NotificationRankingManager_Factory create(Provider<NotificationMediaManager> provider, Provider<NotificationGroupManager> provider2, Provider<HeadsUpManager> provider3, Provider<NotificationFilter> provider4, Provider<NotificationEntryManagerLogger> provider5, Provider<NotificationSectionsFeatureManager> provider6, Provider<PeopleNotificationIdentifier> provider7, Provider<HighPriorityProvider> provider8) {
        NotificationRankingManager_Factory notificationRankingManager_Factory = new NotificationRankingManager_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
        return notificationRankingManager_Factory;
    }
}
