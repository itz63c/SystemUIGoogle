package com.android.systemui.dagger;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class SystemUIDefaultModule_ProvideHeadsUpManagerPhoneFactory implements Factory<HeadsUpManagerPhone> {
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public SystemUIDefaultModule_ProvideHeadsUpManagerPhoneFactory(Provider<Context> provider, Provider<StatusBarStateController> provider2, Provider<KeyguardBypassController> provider3, Provider<NotificationGroupManager> provider4, Provider<ConfigurationController> provider5) {
        this.contextProvider = provider;
        this.statusBarStateControllerProvider = provider2;
        this.bypassControllerProvider = provider3;
        this.groupManagerProvider = provider4;
        this.configurationControllerProvider = provider5;
    }

    public HeadsUpManagerPhone get() {
        return provideInstance(this.contextProvider, this.statusBarStateControllerProvider, this.bypassControllerProvider, this.groupManagerProvider, this.configurationControllerProvider);
    }

    public static HeadsUpManagerPhone provideInstance(Provider<Context> provider, Provider<StatusBarStateController> provider2, Provider<KeyguardBypassController> provider3, Provider<NotificationGroupManager> provider4, Provider<ConfigurationController> provider5) {
        return proxyProvideHeadsUpManagerPhone((Context) provider.get(), (StatusBarStateController) provider2.get(), (KeyguardBypassController) provider3.get(), (NotificationGroupManager) provider4.get(), (ConfigurationController) provider5.get());
    }

    public static SystemUIDefaultModule_ProvideHeadsUpManagerPhoneFactory create(Provider<Context> provider, Provider<StatusBarStateController> provider2, Provider<KeyguardBypassController> provider3, Provider<NotificationGroupManager> provider4, Provider<ConfigurationController> provider5) {
        SystemUIDefaultModule_ProvideHeadsUpManagerPhoneFactory systemUIDefaultModule_ProvideHeadsUpManagerPhoneFactory = new SystemUIDefaultModule_ProvideHeadsUpManagerPhoneFactory(provider, provider2, provider3, provider4, provider5);
        return systemUIDefaultModule_ProvideHeadsUpManagerPhoneFactory;
    }

    public static HeadsUpManagerPhone proxyProvideHeadsUpManagerPhone(Context context, StatusBarStateController statusBarStateController, KeyguardBypassController keyguardBypassController, NotificationGroupManager notificationGroupManager, ConfigurationController configurationController) {
        HeadsUpManagerPhone provideHeadsUpManagerPhone = SystemUIDefaultModule.provideHeadsUpManagerPhone(context, statusBarStateController, keyguardBypassController, notificationGroupManager, configurationController);
        Preconditions.checkNotNull(provideHeadsUpManagerPhone, "Cannot return null from a non-@Nullable @Provides method");
        return provideHeadsUpManagerPhone;
    }
}
