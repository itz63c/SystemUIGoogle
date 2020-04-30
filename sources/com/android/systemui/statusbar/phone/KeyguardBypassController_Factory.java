package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.tuner.TunerService;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class KeyguardBypassController_Factory implements Factory<KeyguardBypassController> {
    private final Provider<Context> contextProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<TunerService> tunerServiceProvider;

    public KeyguardBypassController_Factory(Provider<Context> provider, Provider<TunerService> provider2, Provider<StatusBarStateController> provider3, Provider<NotificationLockscreenUserManager> provider4, Provider<KeyguardStateController> provider5, Provider<DumpManager> provider6) {
        this.contextProvider = provider;
        this.tunerServiceProvider = provider2;
        this.statusBarStateControllerProvider = provider3;
        this.lockscreenUserManagerProvider = provider4;
        this.keyguardStateControllerProvider = provider5;
        this.dumpManagerProvider = provider6;
    }

    public KeyguardBypassController get() {
        return provideInstance(this.contextProvider, this.tunerServiceProvider, this.statusBarStateControllerProvider, this.lockscreenUserManagerProvider, this.keyguardStateControllerProvider, this.dumpManagerProvider);
    }

    public static KeyguardBypassController provideInstance(Provider<Context> provider, Provider<TunerService> provider2, Provider<StatusBarStateController> provider3, Provider<NotificationLockscreenUserManager> provider4, Provider<KeyguardStateController> provider5, Provider<DumpManager> provider6) {
        KeyguardBypassController keyguardBypassController = new KeyguardBypassController((Context) provider.get(), (TunerService) provider2.get(), (StatusBarStateController) provider3.get(), (NotificationLockscreenUserManager) provider4.get(), (KeyguardStateController) provider5.get(), (DumpManager) provider6.get());
        return keyguardBypassController;
    }

    public static KeyguardBypassController_Factory create(Provider<Context> provider, Provider<TunerService> provider2, Provider<StatusBarStateController> provider3, Provider<NotificationLockscreenUserManager> provider4, Provider<KeyguardStateController> provider5, Provider<DumpManager> provider6) {
        KeyguardBypassController_Factory keyguardBypassController_Factory = new KeyguardBypassController_Factory(provider, provider2, provider3, provider4, provider5, provider6);
        return keyguardBypassController_Factory;
    }
}
