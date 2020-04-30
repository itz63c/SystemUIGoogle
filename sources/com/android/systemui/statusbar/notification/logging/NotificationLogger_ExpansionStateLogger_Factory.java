package com.android.systemui.statusbar.notification.logging;

import com.android.systemui.statusbar.notification.logging.NotificationLogger.ExpansionStateLogger;
import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class NotificationLogger_ExpansionStateLogger_Factory implements Factory<ExpansionStateLogger> {
    private final Provider<Executor> uiBgExecutorProvider;

    public NotificationLogger_ExpansionStateLogger_Factory(Provider<Executor> provider) {
        this.uiBgExecutorProvider = provider;
    }

    public ExpansionStateLogger get() {
        return provideInstance(this.uiBgExecutorProvider);
    }

    public static ExpansionStateLogger provideInstance(Provider<Executor> provider) {
        return new ExpansionStateLogger((Executor) provider.get());
    }

    public static NotificationLogger_ExpansionStateLogger_Factory create(Provider<Executor> provider) {
        return new NotificationLogger_ExpansionStateLogger_Factory(provider);
    }
}
