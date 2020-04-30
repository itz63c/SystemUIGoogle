package com.android.systemui.statusbar.phone;

import android.app.IActivityManager;
import android.content.Context;
import android.view.WindowManager;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotificationShadeWindowController_Factory implements Factory<NotificationShadeWindowController> {
    private final Provider<IActivityManager> activityManagerProvider;
    private final Provider<SysuiColorExtractor> colorExtractorProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DozeParameters> dozeParametersProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<WindowManager> windowManagerProvider;

    public NotificationShadeWindowController_Factory(Provider<Context> provider, Provider<WindowManager> provider2, Provider<IActivityManager> provider3, Provider<DozeParameters> provider4, Provider<StatusBarStateController> provider5, Provider<ConfigurationController> provider6, Provider<KeyguardBypassController> provider7, Provider<SysuiColorExtractor> provider8, Provider<DumpManager> provider9) {
        this.contextProvider = provider;
        this.windowManagerProvider = provider2;
        this.activityManagerProvider = provider3;
        this.dozeParametersProvider = provider4;
        this.statusBarStateControllerProvider = provider5;
        this.configurationControllerProvider = provider6;
        this.keyguardBypassControllerProvider = provider7;
        this.colorExtractorProvider = provider8;
        this.dumpManagerProvider = provider9;
    }

    public NotificationShadeWindowController get() {
        return provideInstance(this.contextProvider, this.windowManagerProvider, this.activityManagerProvider, this.dozeParametersProvider, this.statusBarStateControllerProvider, this.configurationControllerProvider, this.keyguardBypassControllerProvider, this.colorExtractorProvider, this.dumpManagerProvider);
    }

    public static NotificationShadeWindowController provideInstance(Provider<Context> provider, Provider<WindowManager> provider2, Provider<IActivityManager> provider3, Provider<DozeParameters> provider4, Provider<StatusBarStateController> provider5, Provider<ConfigurationController> provider6, Provider<KeyguardBypassController> provider7, Provider<SysuiColorExtractor> provider8, Provider<DumpManager> provider9) {
        NotificationShadeWindowController notificationShadeWindowController = new NotificationShadeWindowController((Context) provider.get(), (WindowManager) provider2.get(), (IActivityManager) provider3.get(), (DozeParameters) provider4.get(), (StatusBarStateController) provider5.get(), (ConfigurationController) provider6.get(), (KeyguardBypassController) provider7.get(), (SysuiColorExtractor) provider8.get(), (DumpManager) provider9.get());
        return notificationShadeWindowController;
    }

    public static NotificationShadeWindowController_Factory create(Provider<Context> provider, Provider<WindowManager> provider2, Provider<IActivityManager> provider3, Provider<DozeParameters> provider4, Provider<StatusBarStateController> provider5, Provider<ConfigurationController> provider6, Provider<KeyguardBypassController> provider7, Provider<SysuiColorExtractor> provider8, Provider<DumpManager> provider9) {
        NotificationShadeWindowController_Factory notificationShadeWindowController_Factory = new NotificationShadeWindowController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
        return notificationShadeWindowController_Factory;
    }
}
