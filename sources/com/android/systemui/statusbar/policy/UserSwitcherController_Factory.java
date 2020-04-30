package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.ActivityStarter;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class UserSwitcherController_Factory implements Factory<UserSwitcherController> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;

    public UserSwitcherController_Factory(Provider<Context> provider, Provider<KeyguardStateController> provider2, Provider<Handler> provider3, Provider<ActivityStarter> provider4, Provider<BroadcastDispatcher> provider5) {
        this.contextProvider = provider;
        this.keyguardStateControllerProvider = provider2;
        this.handlerProvider = provider3;
        this.activityStarterProvider = provider4;
        this.broadcastDispatcherProvider = provider5;
    }

    public UserSwitcherController get() {
        return provideInstance(this.contextProvider, this.keyguardStateControllerProvider, this.handlerProvider, this.activityStarterProvider, this.broadcastDispatcherProvider);
    }

    public static UserSwitcherController provideInstance(Provider<Context> provider, Provider<KeyguardStateController> provider2, Provider<Handler> provider3, Provider<ActivityStarter> provider4, Provider<BroadcastDispatcher> provider5) {
        UserSwitcherController userSwitcherController = new UserSwitcherController((Context) provider.get(), (KeyguardStateController) provider2.get(), (Handler) provider3.get(), (ActivityStarter) provider4.get(), (BroadcastDispatcher) provider5.get());
        return userSwitcherController;
    }

    public static UserSwitcherController_Factory create(Provider<Context> provider, Provider<KeyguardStateController> provider2, Provider<Handler> provider3, Provider<ActivityStarter> provider4, Provider<BroadcastDispatcher> provider5) {
        UserSwitcherController_Factory userSwitcherController_Factory = new UserSwitcherController_Factory(provider, provider2, provider3, provider4, provider5);
        return userSwitcherController_Factory;
    }
}
