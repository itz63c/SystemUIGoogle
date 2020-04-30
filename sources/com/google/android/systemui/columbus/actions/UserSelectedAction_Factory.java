package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.assist.DeviceConfigHelper;
import dagger.internal.Factory;
import java.util.Map;
import javax.inject.Provider;

public final class UserSelectedAction_Factory implements Factory<UserSelectedAction> {
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<LaunchOpa> launchOpaProvider;
    private final Provider<Map<String, Action>> userSelectedActionsProvider;

    public UserSelectedAction_Factory(Provider<Context> provider, Provider<DeviceConfigHelper> provider2, Provider<Map<String, Action>> provider3, Provider<LaunchOpa> provider4, Provider<Handler> provider5) {
        this.contextProvider = provider;
        this.deviceConfigHelperProvider = provider2;
        this.userSelectedActionsProvider = provider3;
        this.launchOpaProvider = provider4;
        this.handlerProvider = provider5;
    }

    public UserSelectedAction get() {
        return provideInstance(this.contextProvider, this.deviceConfigHelperProvider, this.userSelectedActionsProvider, this.launchOpaProvider, this.handlerProvider);
    }

    public static UserSelectedAction provideInstance(Provider<Context> provider, Provider<DeviceConfigHelper> provider2, Provider<Map<String, Action>> provider3, Provider<LaunchOpa> provider4, Provider<Handler> provider5) {
        UserSelectedAction userSelectedAction = new UserSelectedAction((Context) provider.get(), (DeviceConfigHelper) provider2.get(), (Map) provider3.get(), (LaunchOpa) provider4.get(), (Handler) provider5.get());
        return userSelectedAction;
    }

    public static UserSelectedAction_Factory create(Provider<Context> provider, Provider<DeviceConfigHelper> provider2, Provider<Map<String, Action>> provider3, Provider<LaunchOpa> provider4, Provider<Handler> provider5) {
        UserSelectedAction_Factory userSelectedAction_Factory = new UserSelectedAction_Factory(provider, provider2, provider3, provider4, provider5);
        return userSelectedAction_Factory;
    }
}
