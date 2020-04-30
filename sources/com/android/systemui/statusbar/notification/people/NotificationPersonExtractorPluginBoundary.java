package com.android.systemui.statusbar.notification.people;

import android.service.notification.StatusBarNotification;
import com.android.systemui.plugins.NotificationPersonExtractorPlugin;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PeopleHubNotificationListener.kt */
public final class NotificationPersonExtractorPluginBoundary implements NotificationPersonExtractor {
    /* access modifiers changed from: private */
    public NotificationPersonExtractorPlugin plugin;

    public NotificationPersonExtractorPluginBoundary(ExtensionController extensionController) {
        Class<NotificationPersonExtractorPlugin> cls = NotificationPersonExtractorPlugin.class;
        Intrinsics.checkParameterIsNotNull(extensionController, "extensionController");
        ExtensionBuilder newExtension = extensionController.newExtension(cls);
        newExtension.withPlugin(cls);
        newExtension.withCallback(new Consumer<NotificationPersonExtractorPlugin>(this) {
            final /* synthetic */ NotificationPersonExtractorPluginBoundary this$0;

            {
                this.this$0 = r1;
            }

            public final void accept(NotificationPersonExtractorPlugin notificationPersonExtractorPlugin) {
                this.this$0.plugin = notificationPersonExtractorPlugin;
            }
        });
        this.plugin = (NotificationPersonExtractorPlugin) newExtension.build().get();
    }

    public String extractPersonKey(StatusBarNotification statusBarNotification) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
        NotificationPersonExtractorPlugin notificationPersonExtractorPlugin = this.plugin;
        if (notificationPersonExtractorPlugin != null) {
            return notificationPersonExtractorPlugin.extractPersonKey(statusBarNotification);
        }
        return null;
    }

    public boolean isPersonNotification(StatusBarNotification statusBarNotification) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
        NotificationPersonExtractorPlugin notificationPersonExtractorPlugin = this.plugin;
        if (notificationPersonExtractorPlugin != null) {
            return notificationPersonExtractorPlugin.isPersonNotification(statusBarNotification);
        }
        return false;
    }
}
