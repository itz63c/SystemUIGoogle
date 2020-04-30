package com.android.systemui.bubbles;

import android.app.Notification.BubbleMetadata;
import android.app.Notification.BubbleMetadata.Builder;
import android.app.Notification.MessagingStyle;
import android.app.Notification.MessagingStyle.Message;
import android.app.Person;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.LauncherApps.ShortcutQuery;
import android.content.pm.ShortcutInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import com.android.internal.util.ArrayUtils;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BubbleExperimentConfig {
    static boolean allowAnyNotifToBubble(Context context) {
        return Secure.getInt(context.getContentResolver(), "allow_any_notif_to_bubble", 0) != 0;
    }

    static boolean allowBubbleOverflow(Context context) {
        return Secure.getInt(context.getContentResolver(), "allow_bubble_overflow", 0) != 0;
    }

    static boolean allowMessageNotifsToBubble(Context context) {
        return Secure.getInt(context.getContentResolver(), "allow_message_notifs_to_bubble", 1) != 0;
    }

    static boolean useShortcutInfoToBubble(Context context) {
        return Secure.getInt(context.getContentResolver(), "allow_shortcuts_to_bubble", 0) != 0;
    }

    static boolean isPackageWhitelistedToAutoBubble(Context context, String str) {
        String string = Secure.getString(context.getContentResolver(), "whitelisted_auto_bubble_apps");
        if (string != null) {
            String[] split = string.split(",");
            for (String trim : split) {
                if (trim.trim().equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x00fc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean adjustForExperiments(android.content.Context r11, com.android.systemui.statusbar.notification.collection.NotificationEntry r12, boolean r13, boolean r14) {
        /*
            android.service.notification.StatusBarNotification r0 = r12.getSbn()
            java.lang.String r0 = r0.getPackageName()
            boolean r0 = isPackageWhitelistedToAutoBubble(r11, r0)
            android.service.notification.StatusBarNotification r1 = r12.getSbn()
            android.app.Notification r1 = r1.getNotification()
            java.lang.Class<android.app.Notification$MessagingStyle> r2 = android.app.Notification.MessagingStyle.class
            java.lang.Class r1 = r1.getNotificationStyle()
            boolean r1 = r2.equals(r1)
            r2 = 0
            r3 = 1
            if (r1 == 0) goto L_0x0028
            boolean r4 = allowMessageNotifsToBubble(r11)
            if (r4 != 0) goto L_0x002e
        L_0x0028:
            boolean r4 = allowAnyNotifToBubble(r11)
            if (r4 == 0) goto L_0x0030
        L_0x002e:
            r4 = r3
            goto L_0x0031
        L_0x0030:
            r4 = r2
        L_0x0031:
            boolean r5 = useShortcutInfoToBubble(r11)
            android.service.notification.StatusBarNotification r6 = r12.getSbn()
            android.app.Notification r6 = r6.getNotification()
            java.lang.String r6 = r6.getShortcutId()
            android.app.Notification$BubbleMetadata r7 = r12.getBubbleMetadata()
            if (r7 == 0) goto L_0x0049
            r7 = r3
            goto L_0x004a
        L_0x0049:
            r7 = r2
        L_0x004a:
            java.lang.String r8 = "Bubbles"
            if (r7 != 0) goto L_0x0052
            if (r13 != 0) goto L_0x0054
            if (r4 != 0) goto L_0x0054
        L_0x0052:
            if (r5 == 0) goto L_0x009c
        L_0x0054:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Adjusting "
            r9.append(r10)
            java.lang.String r10 = r12.getKey()
            r9.append(r10)
            java.lang.String r10 = " for bubble experiment. allowMessages="
            r9.append(r10)
            boolean r10 = allowMessageNotifsToBubble(r11)
            r9.append(r10)
            java.lang.String r10 = " isMessage="
            r9.append(r10)
            r9.append(r1)
            java.lang.String r1 = " allowNotifs="
            r9.append(r1)
            boolean r1 = allowAnyNotifToBubble(r11)
            r9.append(r1)
            java.lang.String r1 = " useShortcutInfo="
            r9.append(r1)
            r9.append(r5)
            java.lang.String r1 = " previouslyUserCreated="
            r9.append(r1)
            r9.append(r13)
            java.lang.String r1 = r9.toString()
            android.util.Log.d(r8, r1)
        L_0x009c:
            if (r5 == 0) goto L_0x00eb
            if (r6 == 0) goto L_0x00eb
            android.service.notification.StatusBarNotification r1 = r12.getSbn()
            java.lang.String r1 = r1.getPackageName()
            android.service.notification.StatusBarNotification r5 = r12.getSbn()
            android.os.UserHandle r5 = r5.getUser()
            android.content.pm.ShortcutInfo r1 = getShortcutInfo(r11, r1, r5, r6)
            if (r1 == 0) goto L_0x00bb
            android.app.Notification$BubbleMetadata r1 = createForShortcut(r6)
            goto L_0x00bc
        L_0x00bb:
            r1 = 0
        L_0x00bc:
            android.app.Notification$BubbleMetadata r5 = r12.getBubbleMetadata()
            if (r5 != 0) goto L_0x00c9
            if (r4 != 0) goto L_0x00c9
            if (r13 == 0) goto L_0x00c7
            goto L_0x00c9
        L_0x00c7:
            r5 = r2
            goto L_0x00ca
        L_0x00c9:
            r5 = r3
        L_0x00ca:
            if (r5 == 0) goto L_0x00eb
            if (r1 == 0) goto L_0x00eb
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Adding experimental shortcut bubble for: "
            r5.append(r6)
            java.lang.String r6 = r12.getKey()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            android.util.Log.d(r8, r5)
            r12.setBubbleMetadata(r1)
            r1 = r3
            goto L_0x00ec
        L_0x00eb:
            r1 = r2
        L_0x00ec:
            android.app.Notification$BubbleMetadata r5 = r12.getBubbleMetadata()
            if (r5 != 0) goto L_0x0118
            if (r4 != 0) goto L_0x00f6
            if (r13 == 0) goto L_0x0118
        L_0x00f6:
            android.app.Notification$BubbleMetadata r11 = createFromNotif(r11, r12)
            if (r11 == 0) goto L_0x0118
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "Adding experimental notification bubble for: "
            r1.append(r4)
            java.lang.String r4 = r12.getKey()
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r8, r1)
            r12.setBubbleMetadata(r11)
            r1 = r3
        L_0x0118:
            if (r14 != 0) goto L_0x0121
            if (r0 == 0) goto L_0x0121
            if (r1 != 0) goto L_0x0120
            if (r7 == 0) goto L_0x0121
        L_0x0120:
            r2 = r3
        L_0x0121:
            if (r13 == 0) goto L_0x0125
            if (r1 != 0) goto L_0x0127
        L_0x0125:
            if (r2 == 0) goto L_0x0143
        L_0x0127:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r13 = "Setting FLAG_BUBBLE for: "
            r11.append(r13)
            java.lang.String r13 = r12.getKey()
            r11.append(r13)
            java.lang.String r11 = r11.toString()
            android.util.Log.d(r8, r11)
            r12.setFlagBubble(r3)
            return r3
        L_0x0143:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.bubbles.BubbleExperimentConfig.adjustForExperiments(android.content.Context, com.android.systemui.statusbar.notification.collection.NotificationEntry, boolean, boolean):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x006c  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0080 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static android.app.Notification.BubbleMetadata createFromNotif(android.content.Context r7, com.android.systemui.statusbar.notification.collection.NotificationEntry r8) {
        /*
            android.service.notification.StatusBarNotification r7 = r8.getSbn()
            android.app.Notification r7 = r7.getNotification()
            android.app.PendingIntent r0 = r7.contentIntent
            java.util.List r1 = getPeopleFromNotification(r8)
            int r2 = r1.size()
            r3 = 0
            r4 = 0
            if (r2 <= 0) goto L_0x0037
            java.lang.Object r1 = r1.get(r3)
            android.app.Person r1 = (android.app.Person) r1
            if (r1 == 0) goto L_0x0037
            android.graphics.drawable.Icon r1 = r1.getIcon()
            if (r1 != 0) goto L_0x0038
            android.graphics.drawable.Drawable r2 = com.android.systemui.statusbar.notification.people.PeopleHubNotificationListenerKt.extractAvatarFromRow(r8)
            boolean r5 = r2 instanceof android.graphics.drawable.BitmapDrawable
            if (r5 == 0) goto L_0x0038
            android.graphics.drawable.BitmapDrawable r2 = (android.graphics.drawable.BitmapDrawable) r2
            android.graphics.Bitmap r1 = r2.getBitmap()
            android.graphics.drawable.Icon r1 = android.graphics.drawable.Icon.createWithBitmap(r1)
            goto L_0x0038
        L_0x0037:
            r1 = r4
        L_0x0038:
            if (r1 != 0) goto L_0x006a
            android.graphics.drawable.Icon r1 = r7.getLargeIcon()
            r2 = 1
            if (r1 != 0) goto L_0x0042
            r3 = r2
        L_0x0042:
            if (r3 == 0) goto L_0x0049
            android.graphics.drawable.Icon r7 = r7.getSmallIcon()
            goto L_0x004d
        L_0x0049:
            android.graphics.drawable.Icon r7 = r7.getLargeIcon()
        L_0x004d:
            r1 = r7
            if (r3 == 0) goto L_0x006a
            android.service.notification.StatusBarNotification r7 = r8.getSbn()
            android.app.Notification r7 = r7.getNotification()
            int r7 = r7.color
            r8 = 255(0xff, float:3.57E-43)
            int r7 = com.android.internal.graphics.ColorUtils.setAlphaComponent(r7, r8)
            r8 = -1
            r5 = 4613937818241073152(0x4008000000000000, double:3.0)
            int r7 = com.android.internal.util.ContrastColorUtil.findContrastColor(r7, r8, r2, r5)
            r1.setTint(r7)
        L_0x006a:
            if (r0 == 0) goto L_0x0080
            android.app.Notification$BubbleMetadata$Builder r7 = new android.app.Notification$BubbleMetadata$Builder
            r7.<init>()
            android.app.Notification$BubbleMetadata$Builder r7 = r7.createIntentBubble(r0, r1)
            r8 = 10000(0x2710, float:1.4013E-41)
            android.app.Notification$BubbleMetadata$Builder r7 = r7.setDesiredHeight(r8)
            android.app.Notification$BubbleMetadata r7 = r7.build()
            return r7
        L_0x0080:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.bubbles.BubbleExperimentConfig.createFromNotif(android.content.Context, com.android.systemui.statusbar.notification.collection.NotificationEntry):android.app.Notification$BubbleMetadata");
    }

    static BubbleMetadata createForShortcut(String str) {
        return new Builder().setDesiredHeight(10000).createShortcutBubble(str).build();
    }

    static ShortcutInfo getShortcutInfo(Context context, String str, UserHandle userHandle, String str2) {
        LauncherApps launcherApps = (LauncherApps) context.getSystemService("launcherapps");
        ShortcutQuery shortcutQuery = new ShortcutQuery();
        if (str != null) {
            shortcutQuery.setPackage(str);
        }
        if (str2 != null) {
            shortcutQuery.setShortcutIds(Arrays.asList(new String[]{str2}));
        }
        shortcutQuery.setQueryFlags(11);
        List shortcuts = launcherApps.getShortcuts(shortcutQuery, userHandle);
        if (shortcuts == null || shortcuts.size() <= 0) {
            return null;
        }
        return (ShortcutInfo) shortcuts.get(0);
    }

    static List<Person> getPeopleFromNotification(NotificationEntry notificationEntry) {
        Bundle bundle = notificationEntry.getSbn().getNotification().extras;
        ArrayList arrayList = new ArrayList();
        if (bundle == null) {
            return arrayList;
        }
        ArrayList parcelableArrayList = bundle.getParcelableArrayList("android.people.list");
        if (parcelableArrayList != null) {
            arrayList.addAll(parcelableArrayList);
        }
        if (MessagingStyle.class.equals(notificationEntry.getSbn().getNotification().getNotificationStyle())) {
            Parcelable[] parcelableArray = bundle.getParcelableArray("android.messages");
            if (!ArrayUtils.isEmpty(parcelableArray)) {
                for (Message senderPerson : Message.getMessagesFromBundleArray(parcelableArray)) {
                    arrayList.add(senderPerson.getSenderPerson());
                }
            }
        }
        return arrayList;
    }
}
