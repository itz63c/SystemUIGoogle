package com.android.systemui.statusbar.notification.collection.provider;

import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class HighPriorityProvider_Factory implements Factory<HighPriorityProvider> {
    private final Provider<PeopleNotificationIdentifier> peopleNotificationIdentifierProvider;

    public HighPriorityProvider_Factory(Provider<PeopleNotificationIdentifier> provider) {
        this.peopleNotificationIdentifierProvider = provider;
    }

    public HighPriorityProvider get() {
        return provideInstance(this.peopleNotificationIdentifierProvider);
    }

    public static HighPriorityProvider provideInstance(Provider<PeopleNotificationIdentifier> provider) {
        return new HighPriorityProvider((PeopleNotificationIdentifier) provider.get());
    }

    public static HighPriorityProvider_Factory create(Provider<PeopleNotificationIdentifier> provider) {
        return new HighPriorityProvider_Factory(provider);
    }
}
