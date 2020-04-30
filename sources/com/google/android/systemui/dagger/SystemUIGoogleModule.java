package com.google.android.systemui.dagger;

import android.content.Context;
import com.android.systemui.C2007R$bool;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dock.DockManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsImplementation;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.google.android.systemui.batteryshare.ReverseWirelessCharger;
import com.google.android.systemui.dreamliner.DockObserver;
import com.google.android.systemui.dreamliner.DreamlinerUtils;
import java.util.Optional;

abstract class SystemUIGoogleModule {
    static boolean provideAllowNotificationLongPress() {
        return true;
    }

    static String provideLeakReportEmail() {
        return "buganizer-system+187317@google.com";
    }

    static DockManager provideDockManager(Context context, BroadcastDispatcher broadcastDispatcher, StatusBarStateController statusBarStateController, NotificationInterruptStateProvider notificationInterruptStateProvider) {
        DockObserver dockObserver = new DockObserver(context, DreamlinerUtils.getInstance(context), broadcastDispatcher, statusBarStateController, notificationInterruptStateProvider);
        return dockObserver;
    }

    static Optional<ReverseWirelessCharger> provideReverseWirelessCharger(Context context) {
        return context.getResources().getBoolean(C2007R$bool.config_wlc_support_enabled) ? Optional.of(new ReverseWirelessCharger(context)) : Optional.empty();
    }

    static HeadsUpManagerPhone provideHeadsUpManagerPhone(Context context, StatusBarStateController statusBarStateController, KeyguardBypassController keyguardBypassController, NotificationGroupManager notificationGroupManager, ConfigurationController configurationController) {
        HeadsUpManagerPhone headsUpManagerPhone = new HeadsUpManagerPhone(context, statusBarStateController, keyguardBypassController, notificationGroupManager, configurationController);
        return headsUpManagerPhone;
    }

    static Recents provideRecents(Context context, RecentsImplementation recentsImplementation, CommandQueue commandQueue) {
        return new Recents(context, recentsImplementation, commandQueue);
    }
}
