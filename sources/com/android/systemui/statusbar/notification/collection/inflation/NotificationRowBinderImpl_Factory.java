package com.android.systemui.statusbar.notification.collection.inflation;

import android.content.Context;
import com.android.internal.util.NotificationMessagingUtil;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.icon.IconManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.notification.row.RowInflaterTask;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent.Builder;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotificationRowBinderImpl_Factory implements Factory<NotificationRowBinderImpl> {
    private final Provider<Context> contextProvider;
    private final Provider<Builder> expandableNotificationRowComponentBuilderProvider;
    private final Provider<IconManager> iconManagerProvider;
    private final Provider<NotifBindPipeline> notifBindPipelineProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptionStateProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<NotificationMessagingUtil> notificationMessagingUtilProvider;
    private final Provider<NotificationRemoteInputManager> notificationRemoteInputManagerProvider;
    private final Provider<RowContentBindStage> rowContentBindStageProvider;
    private final Provider<RowInflaterTask> rowInflaterTaskProvider;

    public NotificationRowBinderImpl_Factory(Provider<Context> provider, Provider<NotificationMessagingUtil> provider2, Provider<NotificationRemoteInputManager> provider3, Provider<NotificationLockscreenUserManager> provider4, Provider<NotifBindPipeline> provider5, Provider<RowContentBindStage> provider6, Provider<NotificationInterruptStateProvider> provider7, Provider<RowInflaterTask> provider8, Provider<Builder> provider9, Provider<IconManager> provider10) {
        this.contextProvider = provider;
        this.notificationMessagingUtilProvider = provider2;
        this.notificationRemoteInputManagerProvider = provider3;
        this.notificationLockscreenUserManagerProvider = provider4;
        this.notifBindPipelineProvider = provider5;
        this.rowContentBindStageProvider = provider6;
        this.notificationInterruptionStateProvider = provider7;
        this.rowInflaterTaskProvider = provider8;
        this.expandableNotificationRowComponentBuilderProvider = provider9;
        this.iconManagerProvider = provider10;
    }

    public NotificationRowBinderImpl get() {
        return provideInstance(this.contextProvider, this.notificationMessagingUtilProvider, this.notificationRemoteInputManagerProvider, this.notificationLockscreenUserManagerProvider, this.notifBindPipelineProvider, this.rowContentBindStageProvider, this.notificationInterruptionStateProvider, this.rowInflaterTaskProvider, this.expandableNotificationRowComponentBuilderProvider, this.iconManagerProvider);
    }

    public static NotificationRowBinderImpl provideInstance(Provider<Context> provider, Provider<NotificationMessagingUtil> provider2, Provider<NotificationRemoteInputManager> provider3, Provider<NotificationLockscreenUserManager> provider4, Provider<NotifBindPipeline> provider5, Provider<RowContentBindStage> provider6, Provider<NotificationInterruptStateProvider> provider7, Provider<RowInflaterTask> provider8, Provider<Builder> provider9, Provider<IconManager> provider10) {
        NotificationRowBinderImpl notificationRowBinderImpl = new NotificationRowBinderImpl((Context) provider.get(), (NotificationMessagingUtil) provider2.get(), (NotificationRemoteInputManager) provider3.get(), (NotificationLockscreenUserManager) provider4.get(), (NotifBindPipeline) provider5.get(), (RowContentBindStage) provider6.get(), (NotificationInterruptStateProvider) provider7.get(), provider8, (Builder) provider9.get(), (IconManager) provider10.get());
        return notificationRowBinderImpl;
    }

    public static NotificationRowBinderImpl_Factory create(Provider<Context> provider, Provider<NotificationMessagingUtil> provider2, Provider<NotificationRemoteInputManager> provider3, Provider<NotificationLockscreenUserManager> provider4, Provider<NotifBindPipeline> provider5, Provider<RowContentBindStage> provider6, Provider<NotificationInterruptStateProvider> provider7, Provider<RowInflaterTask> provider8, Provider<Builder> provider9, Provider<IconManager> provider10) {
        NotificationRowBinderImpl_Factory notificationRowBinderImpl_Factory = new NotificationRowBinderImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10);
        return notificationRowBinderImpl_Factory;
    }
}
