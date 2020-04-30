package com.android.systemui.dagger;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsImplementation;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.ConfigurationController;

public abstract class SystemUIDefaultModule {
    static boolean provideAllowNotificationLongPress() {
        return true;
    }

    static String provideLeakReportEmail() {
        return null;
    }

    static HeadsUpManagerPhone provideHeadsUpManagerPhone(Context context, StatusBarStateController statusBarStateController, KeyguardBypassController keyguardBypassController, NotificationGroupManager notificationGroupManager, ConfigurationController configurationController) {
        HeadsUpManagerPhone headsUpManagerPhone = new HeadsUpManagerPhone(context, statusBarStateController, keyguardBypassController, notificationGroupManager, configurationController);
        return headsUpManagerPhone;
    }

    static Recents provideRecents(Context context, RecentsImplementation recentsImplementation, CommandQueue commandQueue) {
        return new Recents(context, recentsImplementation, commandQueue);
    }
}
