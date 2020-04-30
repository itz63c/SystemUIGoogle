package com.android.systemui.statusbar.notification;

import android.app.Notification.Builder;
import android.app.Notification.MessagingStyle;
import android.app.Notification.Style;
import android.app.NotificationChannel;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.service.notification.NotificationListenerService.Ranking;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ConversationNotificationProcessor.kt */
public final class ConversationNotificationProcessor {
    private final LauncherApps launcherApps;

    public ConversationNotificationProcessor(LauncherApps launcherApps2) {
        Intrinsics.checkParameterIsNotNull(launcherApps2, "launcherApps");
        this.launcherApps = launcherApps2;
    }

    public final void processNotification(NotificationEntry notificationEntry, Builder builder) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        Intrinsics.checkParameterIsNotNull(builder, "recoveredBuilder");
        Style style = builder.getStyle();
        if (!(style instanceof MessagingStyle)) {
            style = null;
        }
        MessagingStyle messagingStyle = (MessagingStyle) style;
        if (messagingStyle != null) {
            Ranking ranking = notificationEntry.getRanking();
            String str = "entry.ranking";
            Intrinsics.checkExpressionValueIsNotNull(ranking, str);
            NotificationChannel channel = ranking.getChannel();
            Intrinsics.checkExpressionValueIsNotNull(channel, "entry.ranking.channel");
            messagingStyle.setConversationType(channel.isImportantConversation() ? 2 : 1);
            Ranking ranking2 = notificationEntry.getRanking();
            Intrinsics.checkExpressionValueIsNotNull(ranking2, str);
            ShortcutInfo shortcutInfo = ranking2.getShortcutInfo();
            if (shortcutInfo != null) {
                messagingStyle.setShortcutIcon(this.launcherApps.getShortcutIcon(shortcutInfo));
                CharSequence shortLabel = shortcutInfo.getShortLabel();
                if (shortLabel != null) {
                    messagingStyle.setConversationTitle(shortLabel);
                }
            }
        }
    }
}
