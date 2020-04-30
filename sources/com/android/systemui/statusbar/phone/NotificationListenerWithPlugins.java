package com.android.systemui.statusbar.phone;

import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.StatusBarNotification;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.NotificationListenerController;
import com.android.systemui.plugins.NotificationListenerController.NotificationProvider;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public class NotificationListenerWithPlugins extends NotificationListenerService implements PluginListener<NotificationListenerController> {
    private boolean mConnected;
    private ArrayList<NotificationListenerController> mPlugins = new ArrayList<>();

    public void registerAsSystemService(Context context, ComponentName componentName, int i) throws RemoteException {
        super.registerAsSystemService(context, componentName, i);
        ((PluginManager) Dependency.get(PluginManager.class)).addPluginListener(this, NotificationListenerController.class);
    }

    public void unregisterAsSystemService() throws RemoteException {
        super.unregisterAsSystemService();
        ((PluginManager) Dependency.get(PluginManager.class)).removePluginListener(this);
    }

    public StatusBarNotification[] getActiveNotifications() {
        StatusBarNotification[] activeNotifications = super.getActiveNotifications();
        Iterator it = this.mPlugins.iterator();
        while (it.hasNext()) {
            activeNotifications = ((NotificationListenerController) it.next()).getActiveNotifications(activeNotifications);
        }
        return activeNotifications;
    }

    public RankingMap getCurrentRanking() {
        RankingMap currentRanking = super.getCurrentRanking();
        Iterator it = this.mPlugins.iterator();
        while (it.hasNext()) {
            currentRanking = ((NotificationListenerController) it.next()).getCurrentRanking(currentRanking);
        }
        return currentRanking;
    }

    public void onPluginConnected() {
        this.mConnected = true;
        this.mPlugins.forEach(new Consumer() {
            public final void accept(Object obj) {
                NotificationListenerWithPlugins.this.lambda$onPluginConnected$0$NotificationListenerWithPlugins((NotificationListenerController) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onPluginConnected$0 */
    public /* synthetic */ void lambda$onPluginConnected$0$NotificationListenerWithPlugins(NotificationListenerController notificationListenerController) {
        notificationListenerController.onListenerConnected(getProvider());
    }

    public boolean onPluginNotificationPosted(StatusBarNotification statusBarNotification, RankingMap rankingMap) {
        Iterator it = this.mPlugins.iterator();
        while (it.hasNext()) {
            if (((NotificationListenerController) it.next()).onNotificationPosted(statusBarNotification, rankingMap)) {
                return true;
            }
        }
        return false;
    }

    public boolean onPluginNotificationRemoved(StatusBarNotification statusBarNotification, RankingMap rankingMap) {
        Iterator it = this.mPlugins.iterator();
        while (it.hasNext()) {
            if (((NotificationListenerController) it.next()).onNotificationRemoved(statusBarNotification, rankingMap)) {
                return true;
            }
        }
        return false;
    }

    public RankingMap onPluginRankingUpdate(RankingMap rankingMap) {
        return getCurrentRanking();
    }

    public void onPluginConnected(NotificationListenerController notificationListenerController, Context context) {
        this.mPlugins.add(notificationListenerController);
        if (this.mConnected) {
            notificationListenerController.onListenerConnected(getProvider());
        }
    }

    public void onPluginDisconnected(NotificationListenerController notificationListenerController) {
        this.mPlugins.remove(notificationListenerController);
    }

    private NotificationProvider getProvider() {
        return new NotificationProvider() {
            public StatusBarNotification[] getActiveNotifications() {
                return NotificationListenerWithPlugins.super.getActiveNotifications();
            }

            public RankingMap getRankingMap() {
                return NotificationListenerWithPlugins.super.getCurrentRanking();
            }

            public void addNotification(StatusBarNotification statusBarNotification) {
                NotificationListenerWithPlugins.this.onNotificationPosted(statusBarNotification, getRankingMap());
            }

            public void removeNotification(StatusBarNotification statusBarNotification) {
                NotificationListenerWithPlugins.this.onNotificationRemoved(statusBarNotification, getRankingMap());
            }

            public void updateRanking() {
                NotificationListenerWithPlugins.this.onNotificationRankingUpdate(getRankingMap());
            }
        };
    }
}
