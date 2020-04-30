package com.android.systemui.statusbar.notification.icon;

import android.app.Notification;
import android.app.Notification.MessagingStyle.Message;
import android.app.NotificationChannel;
import android.app.Person;
import android.content.pm.LauncherApps;
import android.content.pm.LauncherApps.ShortcutQuery;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.ImageView.ScaleType;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.C2011R$id;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry.OnSensitivityChangedListener;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import java.util.List;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: IconManager.kt */
public final class IconManager {
    private final IconManager$entryListener$1 entryListener = new IconManager$entryListener$1(this);
    private final IconBuilder iconBuilder;
    private final LauncherApps launcherApps;
    /* access modifiers changed from: private */
    public final CommonNotifCollection notifCollection;
    /* access modifiers changed from: private */
    public final OnSensitivityChangedListener sensitivityListener = new IconManager$sensitivityListener$1(this);

    public IconManager(CommonNotifCollection commonNotifCollection, LauncherApps launcherApps2, IconBuilder iconBuilder2) {
        Intrinsics.checkParameterIsNotNull(commonNotifCollection, "notifCollection");
        Intrinsics.checkParameterIsNotNull(launcherApps2, "launcherApps");
        Intrinsics.checkParameterIsNotNull(iconBuilder2, "iconBuilder");
        this.notifCollection = commonNotifCollection;
        this.launcherApps = launcherApps2;
        this.iconBuilder = iconBuilder2;
    }

    public final void attach() {
        this.notifCollection.addCollectionListener(this.entryListener);
    }

    public final void createIcons(NotificationEntry notificationEntry) throws InflationException {
        StatusBarIconView statusBarIconView;
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        StatusBarIconView createIconView = this.iconBuilder.createIconView(notificationEntry);
        createIconView.setScaleType(ScaleType.CENTER_INSIDE);
        StatusBarIconView createIconView2 = this.iconBuilder.createIconView(notificationEntry);
        createIconView2.setScaleType(ScaleType.CENTER_INSIDE);
        createIconView2.setVisibility(4);
        createIconView2.setOnVisibilityChangedListener(new IconManager$createIcons$1(notificationEntry));
        StatusBarIconView createIconView3 = this.iconBuilder.createIconView(notificationEntry);
        createIconView3.setScaleType(ScaleType.CENTER_INSIDE);
        createIconView3.setIncreasedSize(true);
        StatusBarNotification sbn = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn, "entry.sbn");
        Notification notification = sbn.getNotification();
        Intrinsics.checkExpressionValueIsNotNull(notification, "entry.sbn.notification");
        if (notification.isMediaNotification()) {
            statusBarIconView = this.iconBuilder.createIconView(notificationEntry);
            statusBarIconView.setScaleType(ScaleType.CENTER_INSIDE);
        } else {
            statusBarIconView = null;
        }
        Pair iconDescriptors = getIconDescriptors(notificationEntry);
        StatusBarIcon statusBarIcon = (StatusBarIcon) iconDescriptors.component1();
        StatusBarIcon statusBarIcon2 = (StatusBarIcon) iconDescriptors.component2();
        try {
            setIcon(notificationEntry, statusBarIcon, createIconView);
            setIcon(notificationEntry, statusBarIcon2, createIconView2);
            setIcon(notificationEntry, statusBarIcon2, createIconView3);
            if (statusBarIconView != null) {
                setIcon(notificationEntry, statusBarIcon, statusBarIconView);
            }
            notificationEntry.setIcons(IconPack.buildPack(createIconView, createIconView2, createIconView3, statusBarIconView, notificationEntry.getIcons()));
        } catch (InflationException e) {
            notificationEntry.setIcons(IconPack.buildEmptyPack(notificationEntry.getIcons()));
            throw e;
        }
    }

    public final void updateIcons(NotificationEntry notificationEntry) throws InflationException {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        IconPack icons = notificationEntry.getIcons();
        String str = "entry.icons";
        Intrinsics.checkExpressionValueIsNotNull(icons, str);
        if (icons.getAreIconsAvailable()) {
            IconPack icons2 = notificationEntry.getIcons();
            Intrinsics.checkExpressionValueIsNotNull(icons2, str);
            icons2.setSmallIconDescriptor(null);
            IconPack icons3 = notificationEntry.getIcons();
            Intrinsics.checkExpressionValueIsNotNull(icons3, str);
            icons3.setPeopleAvatarDescriptor(null);
            Pair iconDescriptors = getIconDescriptors(notificationEntry);
            StatusBarIcon statusBarIcon = (StatusBarIcon) iconDescriptors.component1();
            StatusBarIcon statusBarIcon2 = (StatusBarIcon) iconDescriptors.component2();
            IconPack icons4 = notificationEntry.getIcons();
            Intrinsics.checkExpressionValueIsNotNull(icons4, str);
            StatusBarIconView statusBarIcon3 = icons4.getStatusBarIcon();
            String str2 = "it";
            if (statusBarIcon3 != null) {
                Intrinsics.checkExpressionValueIsNotNull(statusBarIcon3, str2);
                statusBarIcon3.setNotification(notificationEntry.getSbn());
                setIcon(notificationEntry, statusBarIcon, statusBarIcon3);
            }
            IconPack icons5 = notificationEntry.getIcons();
            Intrinsics.checkExpressionValueIsNotNull(icons5, str);
            StatusBarIconView shelfIcon = icons5.getShelfIcon();
            if (shelfIcon != null) {
                Intrinsics.checkExpressionValueIsNotNull(shelfIcon, str2);
                shelfIcon.setNotification(notificationEntry.getSbn());
                setIcon(notificationEntry, statusBarIcon, shelfIcon);
            }
            IconPack icons6 = notificationEntry.getIcons();
            Intrinsics.checkExpressionValueIsNotNull(icons6, str);
            StatusBarIconView aodIcon = icons6.getAodIcon();
            if (aodIcon != null) {
                Intrinsics.checkExpressionValueIsNotNull(aodIcon, str2);
                aodIcon.setNotification(notificationEntry.getSbn());
                setIcon(notificationEntry, statusBarIcon2, aodIcon);
            }
            IconPack icons7 = notificationEntry.getIcons();
            Intrinsics.checkExpressionValueIsNotNull(icons7, str);
            StatusBarIconView centeredIcon = icons7.getCenteredIcon();
            if (centeredIcon != null) {
                Intrinsics.checkExpressionValueIsNotNull(centeredIcon, str2);
                centeredIcon.setNotification(notificationEntry.getSbn());
                setIcon(notificationEntry, statusBarIcon2, centeredIcon);
            }
        }
    }

    public final void updateIconTags(NotificationEntry notificationEntry, int i) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        IconPack icons = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons, "entry.icons");
        setTagOnIconViews(icons, C2011R$id.icon_is_pre_L, Boolean.valueOf(i < 21));
    }

    /* access modifiers changed from: private */
    public final void updateIconsSafe(NotificationEntry notificationEntry) {
        try {
            updateIcons(notificationEntry);
        } catch (InflationException e) {
            Log.e("IconManager", "Unable to update icon", e);
        }
    }

    private final Pair<StatusBarIcon, StatusBarIcon> getIconDescriptors(NotificationEntry notificationEntry) throws InflationException {
        StatusBarIcon iconDescriptor = getIconDescriptor(notificationEntry, false);
        return new Pair<>(iconDescriptor, notificationEntry.isSensitive() ? getIconDescriptor(notificationEntry, true) : iconDescriptor);
    }

    private final StatusBarIcon getIconDescriptor(NotificationEntry notificationEntry, boolean z) throws InflationException {
        Icon icon;
        StatusBarNotification sbn = notificationEntry.getSbn();
        String str = "entry.sbn";
        Intrinsics.checkExpressionValueIsNotNull(sbn, str);
        Notification notification = sbn.getNotification();
        boolean z2 = isImportantConversation(notificationEntry) && !z;
        IconPack icons = notificationEntry.getIcons();
        String str2 = "entry.icons";
        Intrinsics.checkExpressionValueIsNotNull(icons, str2);
        StatusBarIcon peopleAvatarDescriptor = icons.getPeopleAvatarDescriptor();
        IconPack icons2 = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons2, str2);
        StatusBarIcon smallIconDescriptor = icons2.getSmallIconDescriptor();
        if (z2 && peopleAvatarDescriptor != null) {
            return peopleAvatarDescriptor;
        }
        if (!z2 && smallIconDescriptor != null) {
            return smallIconDescriptor;
        }
        String str3 = "n";
        if (z2) {
            icon = createPeopleAvatar(notificationEntry);
        } else {
            Intrinsics.checkExpressionValueIsNotNull(notification, str3);
            icon = notification.getSmallIcon();
        }
        Icon icon2 = icon;
        if (icon2 != null) {
            StatusBarNotification sbn2 = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn2, str);
            UserHandle user = sbn2.getUser();
            StatusBarNotification sbn3 = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn3, str);
            String packageName = sbn3.getPackageName();
            int i = notification.iconLevel;
            int i2 = notification.number;
            IconBuilder iconBuilder2 = this.iconBuilder;
            Intrinsics.checkExpressionValueIsNotNull(notification, str3);
            StatusBarIcon statusBarIcon = new StatusBarIcon(user, packageName, icon2, i, i2, iconBuilder2.getIconContentDescription(notification));
            if (isImportantConversation(notificationEntry)) {
                if (z2) {
                    IconPack icons3 = notificationEntry.getIcons();
                    Intrinsics.checkExpressionValueIsNotNull(icons3, str2);
                    icons3.setPeopleAvatarDescriptor(statusBarIcon);
                } else {
                    IconPack icons4 = notificationEntry.getIcons();
                    Intrinsics.checkExpressionValueIsNotNull(icons4, str2);
                    icons4.setSmallIconDescriptor(statusBarIcon);
                }
            }
            return statusBarIcon;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("No icon in notification from ");
        StatusBarNotification sbn4 = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn4, str);
        sb.append(sbn4.getPackageName());
        throw new InflationException(sb.toString());
    }

    private final void setIcon(NotificationEntry notificationEntry, StatusBarIcon statusBarIcon, StatusBarIconView statusBarIconView) throws InflationException {
        statusBarIconView.setTintIcons(shouldTintIconView(notificationEntry, statusBarIconView));
        if (!statusBarIconView.set(statusBarIcon)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Couldn't create icon ");
            sb.append(statusBarIcon);
            throw new InflationException(sb.toString());
        }
    }

    private final Icon createPeopleAvatar(NotificationEntry notificationEntry) throws InflationException {
        Icon icon;
        Ranking ranking = notificationEntry.getRanking();
        Intrinsics.checkExpressionValueIsNotNull(ranking, "entry.ranking");
        NotificationChannel channel = ranking.getChannel();
        Intrinsics.checkExpressionValueIsNotNull(channel, "entry.ranking.channel");
        String conversationId = channel.getConversationId();
        ShortcutQuery shortcutQuery = new ShortcutQuery();
        StatusBarNotification sbn = notificationEntry.getSbn();
        String str = "entry.sbn";
        Intrinsics.checkExpressionValueIsNotNull(sbn, str);
        ShortcutQuery shortcutIds = shortcutQuery.setPackage(sbn.getPackageName()).setQueryFlags(3).setShortcutIds(CollectionsKt__CollectionsJVMKt.listOf(conversationId));
        LauncherApps launcherApps2 = this.launcherApps;
        StatusBarNotification sbn2 = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn2, str);
        List shortcuts = launcherApps2.getShortcuts(shortcutIds, sbn2.getUser());
        if (shortcuts == null || !(!shortcuts.isEmpty())) {
            icon = null;
        } else {
            Object obj = shortcuts.get(0);
            Intrinsics.checkExpressionValueIsNotNull(obj, "shortcuts[0]");
            icon = ((ShortcutInfo) obj).getIcon();
        }
        if (icon == null) {
            StatusBarNotification sbn3 = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn3, str);
            icon = sbn3.getNotification().getLargeIcon();
        }
        if (icon == null) {
            StatusBarNotification sbn4 = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn4, str);
            Bundle bundle = sbn4.getNotification().extras;
            Intrinsics.checkExpressionValueIsNotNull(bundle, "entry.sbn.notification.extras");
            List messagesFromBundleArray = Message.getMessagesFromBundleArray(bundle.getParcelableArray("android.messages"));
            Person person = (Person) bundle.getParcelable("android.messagingUser");
            Intrinsics.checkExpressionValueIsNotNull(messagesFromBundleArray, "messages");
            int size = messagesFromBundleArray.size();
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                Message message = (Message) messagesFromBundleArray.get(size);
                Intrinsics.checkExpressionValueIsNotNull(message, "message");
                Person senderPerson = message.getSenderPerson();
                if (senderPerson != null && senderPerson != person) {
                    Person senderPerson2 = message.getSenderPerson();
                    if (senderPerson2 != null) {
                        icon = senderPerson2.getIcon();
                    } else {
                        Intrinsics.throwNpe();
                        throw null;
                    }
                }
            }
        }
        if (icon == null) {
            StatusBarNotification sbn5 = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn5, str);
            Notification notification = sbn5.getNotification();
            Intrinsics.checkExpressionValueIsNotNull(notification, "entry.sbn.notification");
            icon = notification.getSmallIcon();
        }
        if (icon != null) {
            return icon;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("No icon in notification from ");
        StatusBarNotification sbn6 = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn6, str);
        sb.append(sbn6.getPackageName());
        throw new InflationException(sb.toString());
    }

    private final boolean shouldTintIconView(NotificationEntry notificationEntry, StatusBarIconView statusBarIconView) {
        boolean z;
        IconPack icons = notificationEntry.getIcons();
        String str = "entry.icons";
        Intrinsics.checkExpressionValueIsNotNull(icons, str);
        if (statusBarIconView != icons.getShelfIcon()) {
            IconPack icons2 = notificationEntry.getIcons();
            Intrinsics.checkExpressionValueIsNotNull(icons2, str);
            if (statusBarIconView != icons2.getAodIcon()) {
                z = false;
                if (isImportantConversation(notificationEntry) || (z && notificationEntry.isSensitive())) {
                    return true;
                }
                return false;
            }
        }
        z = true;
        if (isImportantConversation(notificationEntry)) {
        }
        return true;
    }

    /* access modifiers changed from: private */
    public final boolean isImportantConversation(NotificationEntry notificationEntry) {
        Ranking ranking = notificationEntry.getRanking();
        String str = "entry.ranking";
        Intrinsics.checkExpressionValueIsNotNull(ranking, str);
        if (ranking.getChannel() != null) {
            Ranking ranking2 = notificationEntry.getRanking();
            Intrinsics.checkExpressionValueIsNotNull(ranking2, str);
            NotificationChannel channel = ranking2.getChannel();
            Intrinsics.checkExpressionValueIsNotNull(channel, "entry.ranking.channel");
            if (channel.isImportantConversation()) {
                return true;
            }
        }
        return false;
    }

    private final void setTagOnIconViews(IconPack iconPack, int i, Object obj) {
        StatusBarIconView statusBarIcon = iconPack.getStatusBarIcon();
        if (statusBarIcon != null) {
            statusBarIcon.setTag(i, obj);
        }
        StatusBarIconView shelfIcon = iconPack.getShelfIcon();
        if (shelfIcon != null) {
            shelfIcon.setTag(i, obj);
        }
        StatusBarIconView aodIcon = iconPack.getAodIcon();
        if (aodIcon != null) {
            aodIcon.setTag(i, obj);
        }
        StatusBarIconView centeredIcon = iconPack.getCenteredIcon();
        if (centeredIcon != null) {
            centeredIcon.setTag(i, obj);
        }
    }
}
