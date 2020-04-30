package com.android.systemui.statusbar.notification.row;

import android.app.INotificationManager;
import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ChannelEditorDialogController_Factory implements Factory<ChannelEditorDialogController> {
    private final Provider<Context> cProvider;
    private final Provider<INotificationManager> noManProvider;

    public ChannelEditorDialogController_Factory(Provider<Context> provider, Provider<INotificationManager> provider2) {
        this.cProvider = provider;
        this.noManProvider = provider2;
    }

    public ChannelEditorDialogController get() {
        return provideInstance(this.cProvider, this.noManProvider);
    }

    public static ChannelEditorDialogController provideInstance(Provider<Context> provider, Provider<INotificationManager> provider2) {
        return new ChannelEditorDialogController((Context) provider.get(), (INotificationManager) provider2.get());
    }

    public static ChannelEditorDialogController_Factory create(Provider<Context> provider, Provider<INotificationManager> provider2) {
        return new ChannelEditorDialogController_Factory(provider, provider2);
    }
}
