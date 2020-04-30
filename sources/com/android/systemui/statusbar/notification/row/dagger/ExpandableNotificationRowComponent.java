package com.android.systemui.statusbar.notification.row.dagger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.OnExpandClickListener;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder.InflationCallback;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.phone.StatusBar;

public interface ExpandableNotificationRowComponent {

    public interface Builder {
        ExpandableNotificationRowComponent build();

        Builder expandableNotificationRow(ExpandableNotificationRow expandableNotificationRow);

        Builder inflationCallback(InflationCallback inflationCallback);

        Builder notificationEntry(NotificationEntry notificationEntry);

        Builder onDismissRunnable(Runnable runnable);

        Builder onExpandClickListener(OnExpandClickListener onExpandClickListener);

        Builder rowContentBindStage(RowContentBindStage rowContentBindStage);
    }

    public static abstract class ExpandableNotificationRowModule {
        static StatusBarNotification provideStatusBarNotification(NotificationEntry notificationEntry) {
            return notificationEntry.getSbn();
        }

        static String provideNotificationKey(StatusBarNotification statusBarNotification) {
            return statusBarNotification.getKey();
        }

        static String provideAppName(Context context, StatusBarNotification statusBarNotification) {
            PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(context, statusBarNotification.getUser().getIdentifier());
            String packageName = statusBarNotification.getPackageName();
            try {
                ApplicationInfo applicationInfo = packageManagerForUser.getApplicationInfo(packageName, 8704);
                if (applicationInfo != null) {
                    return String.valueOf(packageManagerForUser.getApplicationLabel(applicationInfo));
                }
            } catch (NameNotFoundException unused) {
            }
            return packageName;
        }
    }

    ExpandableNotificationRowController getExpandableNotificationRowController();
}
