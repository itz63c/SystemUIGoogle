package com.android.systemui.statusbar.dagger;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.notification.DynamicChildBindController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideNotificationViewHierarchyManagerFactory */
public final class C1173x3f8faa0a implements Factory<NotificationViewHierarchyManager> {
    private final Provider<BubbleController> bubbleControllerProvider;
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DynamicChildBindController> dynamicChildBindControllerProvider;
    private final Provider<ForegroundServiceSectionController> fgsSectionControllerProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<DynamicPrivacyController> privacyControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;

    public C1173x3f8faa0a(Provider<Context> provider, Provider<Handler> provider2, Provider<NotificationLockscreenUserManager> provider3, Provider<NotificationGroupManager> provider4, Provider<VisualStabilityManager> provider5, Provider<StatusBarStateController> provider6, Provider<NotificationEntryManager> provider7, Provider<KeyguardBypassController> provider8, Provider<BubbleController> provider9, Provider<DynamicPrivacyController> provider10, Provider<ForegroundServiceSectionController> provider11, Provider<DynamicChildBindController> provider12) {
        this.contextProvider = provider;
        this.mainHandlerProvider = provider2;
        this.notificationLockscreenUserManagerProvider = provider3;
        this.groupManagerProvider = provider4;
        this.visualStabilityManagerProvider = provider5;
        this.statusBarStateControllerProvider = provider6;
        this.notificationEntryManagerProvider = provider7;
        this.bypassControllerProvider = provider8;
        this.bubbleControllerProvider = provider9;
        this.privacyControllerProvider = provider10;
        this.fgsSectionControllerProvider = provider11;
        this.dynamicChildBindControllerProvider = provider12;
    }

    public NotificationViewHierarchyManager get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider, this.notificationLockscreenUserManagerProvider, this.groupManagerProvider, this.visualStabilityManagerProvider, this.statusBarStateControllerProvider, this.notificationEntryManagerProvider, this.bypassControllerProvider, this.bubbleControllerProvider, this.privacyControllerProvider, this.fgsSectionControllerProvider, this.dynamicChildBindControllerProvider);
    }

    public static NotificationViewHierarchyManager provideInstance(Provider<Context> provider, Provider<Handler> provider2, Provider<NotificationLockscreenUserManager> provider3, Provider<NotificationGroupManager> provider4, Provider<VisualStabilityManager> provider5, Provider<StatusBarStateController> provider6, Provider<NotificationEntryManager> provider7, Provider<KeyguardBypassController> provider8, Provider<BubbleController> provider9, Provider<DynamicPrivacyController> provider10, Provider<ForegroundServiceSectionController> provider11, Provider<DynamicChildBindController> provider12) {
        return proxyProvideNotificationViewHierarchyManager((Context) provider.get(), (Handler) provider2.get(), (NotificationLockscreenUserManager) provider3.get(), (NotificationGroupManager) provider4.get(), (VisualStabilityManager) provider5.get(), (StatusBarStateController) provider6.get(), (NotificationEntryManager) provider7.get(), (KeyguardBypassController) provider8.get(), (BubbleController) provider9.get(), (DynamicPrivacyController) provider10.get(), (ForegroundServiceSectionController) provider11.get(), (DynamicChildBindController) provider12.get());
    }

    public static C1173x3f8faa0a create(Provider<Context> provider, Provider<Handler> provider2, Provider<NotificationLockscreenUserManager> provider3, Provider<NotificationGroupManager> provider4, Provider<VisualStabilityManager> provider5, Provider<StatusBarStateController> provider6, Provider<NotificationEntryManager> provider7, Provider<KeyguardBypassController> provider8, Provider<BubbleController> provider9, Provider<DynamicPrivacyController> provider10, Provider<ForegroundServiceSectionController> provider11, Provider<DynamicChildBindController> provider12) {
        C1173x3f8faa0a statusBarDependenciesModule_ProvideNotificationViewHierarchyManagerFactory = new C1173x3f8faa0a(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12);
        return statusBarDependenciesModule_ProvideNotificationViewHierarchyManagerFactory;
    }

    public static NotificationViewHierarchyManager proxyProvideNotificationViewHierarchyManager(Context context, Handler handler, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationGroupManager notificationGroupManager, VisualStabilityManager visualStabilityManager, StatusBarStateController statusBarStateController, NotificationEntryManager notificationEntryManager, KeyguardBypassController keyguardBypassController, BubbleController bubbleController, DynamicPrivacyController dynamicPrivacyController, ForegroundServiceSectionController foregroundServiceSectionController, DynamicChildBindController dynamicChildBindController) {
        NotificationViewHierarchyManager provideNotificationViewHierarchyManager = StatusBarDependenciesModule.provideNotificationViewHierarchyManager(context, handler, notificationLockscreenUserManager, notificationGroupManager, visualStabilityManager, statusBarStateController, notificationEntryManager, keyguardBypassController, bubbleController, dynamicPrivacyController, foregroundServiceSectionController, dynamicChildBindController);
        Preconditions.checkNotNull(provideNotificationViewHierarchyManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationViewHierarchyManager;
    }
}
