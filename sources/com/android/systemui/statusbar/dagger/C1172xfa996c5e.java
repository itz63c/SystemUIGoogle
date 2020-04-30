package com.android.systemui.statusbar.dagger;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideNotificationRemoteInputManagerFactory */
public final class C1172xfa996c5e implements Factory<NotificationRemoteInputManager> {
    private final Provider<Context> contextProvider;
    private final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<RemoteInputUriController> remoteInputUriControllerProvider;
    private final Provider<SmartReplyController> smartReplyControllerProvider;
    private final Provider<StatusBar> statusBarLazyProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public C1172xfa996c5e(Provider<Context> provider, Provider<NotificationLockscreenUserManager> provider2, Provider<SmartReplyController> provider3, Provider<NotificationEntryManager> provider4, Provider<StatusBar> provider5, Provider<StatusBarStateController> provider6, Provider<Handler> provider7, Provider<RemoteInputUriController> provider8) {
        this.contextProvider = provider;
        this.lockscreenUserManagerProvider = provider2;
        this.smartReplyControllerProvider = provider3;
        this.notificationEntryManagerProvider = provider4;
        this.statusBarLazyProvider = provider5;
        this.statusBarStateControllerProvider = provider6;
        this.mainHandlerProvider = provider7;
        this.remoteInputUriControllerProvider = provider8;
    }

    public NotificationRemoteInputManager get() {
        return provideInstance(this.contextProvider, this.lockscreenUserManagerProvider, this.smartReplyControllerProvider, this.notificationEntryManagerProvider, this.statusBarLazyProvider, this.statusBarStateControllerProvider, this.mainHandlerProvider, this.remoteInputUriControllerProvider);
    }

    public static NotificationRemoteInputManager provideInstance(Provider<Context> provider, Provider<NotificationLockscreenUserManager> provider2, Provider<SmartReplyController> provider3, Provider<NotificationEntryManager> provider4, Provider<StatusBar> provider5, Provider<StatusBarStateController> provider6, Provider<Handler> provider7, Provider<RemoteInputUriController> provider8) {
        return proxyProvideNotificationRemoteInputManager((Context) provider.get(), (NotificationLockscreenUserManager) provider2.get(), (SmartReplyController) provider3.get(), (NotificationEntryManager) provider4.get(), DoubleCheck.lazy(provider5), (StatusBarStateController) provider6.get(), (Handler) provider7.get(), (RemoteInputUriController) provider8.get());
    }

    public static C1172xfa996c5e create(Provider<Context> provider, Provider<NotificationLockscreenUserManager> provider2, Provider<SmartReplyController> provider3, Provider<NotificationEntryManager> provider4, Provider<StatusBar> provider5, Provider<StatusBarStateController> provider6, Provider<Handler> provider7, Provider<RemoteInputUriController> provider8) {
        C1172xfa996c5e statusBarDependenciesModule_ProvideNotificationRemoteInputManagerFactory = new C1172xfa996c5e(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
        return statusBarDependenciesModule_ProvideNotificationRemoteInputManagerFactory;
    }

    public static NotificationRemoteInputManager proxyProvideNotificationRemoteInputManager(Context context, NotificationLockscreenUserManager notificationLockscreenUserManager, SmartReplyController smartReplyController, NotificationEntryManager notificationEntryManager, Lazy<StatusBar> lazy, StatusBarStateController statusBarStateController, Handler handler, RemoteInputUriController remoteInputUriController) {
        NotificationRemoteInputManager provideNotificationRemoteInputManager = StatusBarDependenciesModule.provideNotificationRemoteInputManager(context, notificationLockscreenUserManager, smartReplyController, notificationEntryManager, lazy, statusBarStateController, handler, remoteInputUriController);
        Preconditions.checkNotNull(provideNotificationRemoteInputManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationRemoteInputManager;
    }
}
