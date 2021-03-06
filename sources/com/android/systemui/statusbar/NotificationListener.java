package com.android.systemui.statusbar;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.systemui.statusbar.phone.NotificationListenerWithPlugins;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressLint({"OverrideAbstract"})
public class NotificationListener extends NotificationListenerWithPlugins {
    private final Context mContext;
    private final Handler mMainHandler;
    private final List<NotificationHandler> mNotificationHandlers = new ArrayList();
    private final NotificationManager mNotificationManager;
    private final ArrayList<NotificationSettingsListener> mSettingsListeners = new ArrayList<>();

    public interface NotificationHandler {
        void onNotificationPosted(StatusBarNotification statusBarNotification, RankingMap rankingMap);

        void onNotificationRankingUpdate(RankingMap rankingMap);

        void onNotificationRemoved(StatusBarNotification statusBarNotification, RankingMap rankingMap, int i);
    }

    public interface NotificationSettingsListener {
        void onStatusBarIconsBehaviorChanged(boolean z) {
        }
    }

    public NotificationListener(Context context, NotificationManager notificationManager, Handler handler) {
        this.mContext = context;
        this.mNotificationManager = notificationManager;
        this.mMainHandler = handler;
    }

    public void addNotificationHandler(NotificationHandler notificationHandler) {
        if (!this.mNotificationHandlers.contains(notificationHandler)) {
            this.mNotificationHandlers.add(notificationHandler);
            return;
        }
        throw new IllegalArgumentException("Listener is already added");
    }

    public void addNotificationSettingsListener(NotificationSettingsListener notificationSettingsListener) {
        this.mSettingsListeners.add(notificationSettingsListener);
    }

    public void onListenerConnected() {
        onPluginConnected();
        StatusBarNotification[] activeNotifications = getActiveNotifications();
        if (activeNotifications == null) {
            Log.w("NotificationListener", "onListenerConnected unable to get active notifications.");
            return;
        }
        this.mMainHandler.post(new Runnable(activeNotifications, getCurrentRanking()) {
            public final /* synthetic */ StatusBarNotification[] f$1;
            public final /* synthetic */ RankingMap f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                NotificationListener.this.lambda$onListenerConnected$0$NotificationListener(this.f$1, this.f$2);
            }
        });
        onSilentStatusBarIconsVisibilityChanged(this.mNotificationManager.shouldHideSilentStatusBarIcons());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onListenerConnected$0 */
    public /* synthetic */ void lambda$onListenerConnected$0$NotificationListener(StatusBarNotification[] statusBarNotificationArr, RankingMap rankingMap) {
        ArrayList arrayList = new ArrayList();
        for (StatusBarNotification key : statusBarNotificationArr) {
            arrayList.add(getRankingOrTemporaryStandIn(rankingMap, key.getKey()));
        }
        RankingMap rankingMap2 = new RankingMap((Ranking[]) arrayList.toArray(new Ranking[0]));
        for (StatusBarNotification statusBarNotification : statusBarNotificationArr) {
            for (NotificationHandler onNotificationPosted : this.mNotificationHandlers) {
                onNotificationPosted.onNotificationPosted(statusBarNotification, rankingMap2);
            }
        }
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification, RankingMap rankingMap) {
        if (statusBarNotification != null && !onPluginNotificationPosted(statusBarNotification, rankingMap)) {
            this.mMainHandler.post(new Runnable(statusBarNotification, rankingMap) {
                public final /* synthetic */ StatusBarNotification f$1;
                public final /* synthetic */ RankingMap f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NotificationListener.this.lambda$onNotificationPosted$1$NotificationListener(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onNotificationPosted$1 */
    public /* synthetic */ void lambda$onNotificationPosted$1$NotificationListener(StatusBarNotification statusBarNotification, RankingMap rankingMap) {
        RemoteInputController.processForRemoteInput(statusBarNotification.getNotification(), this.mContext);
        for (NotificationHandler onNotificationPosted : this.mNotificationHandlers) {
            onNotificationPosted.onNotificationPosted(statusBarNotification, rankingMap);
        }
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification, RankingMap rankingMap, int i) {
        if (statusBarNotification != null && !onPluginNotificationRemoved(statusBarNotification, rankingMap)) {
            this.mMainHandler.post(new Runnable(statusBarNotification, rankingMap, i) {
                public final /* synthetic */ StatusBarNotification f$1;
                public final /* synthetic */ RankingMap f$2;
                public final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    NotificationListener.this.lambda$onNotificationRemoved$2$NotificationListener(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onNotificationRemoved$2 */
    public /* synthetic */ void lambda$onNotificationRemoved$2$NotificationListener(StatusBarNotification statusBarNotification, RankingMap rankingMap, int i) {
        for (NotificationHandler onNotificationRemoved : this.mNotificationHandlers) {
            onNotificationRemoved.onNotificationRemoved(statusBarNotification, rankingMap, i);
        }
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification, RankingMap rankingMap) {
        onNotificationRemoved(statusBarNotification, rankingMap, 0);
    }

    public void onNotificationRankingUpdate(RankingMap rankingMap) {
        if (rankingMap != null) {
            this.mMainHandler.post(new Runnable(onPluginRankingUpdate(rankingMap)) {
                public final /* synthetic */ RankingMap f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationListener.this.lambda$onNotificationRankingUpdate$3$NotificationListener(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onNotificationRankingUpdate$3 */
    public /* synthetic */ void lambda$onNotificationRankingUpdate$3$NotificationListener(RankingMap rankingMap) {
        for (NotificationHandler onNotificationRankingUpdate : this.mNotificationHandlers) {
            onNotificationRankingUpdate.onNotificationRankingUpdate(rankingMap);
        }
    }

    public void onSilentStatusBarIconsVisibilityChanged(boolean z) {
        Iterator it = this.mSettingsListeners.iterator();
        while (it.hasNext()) {
            ((NotificationSettingsListener) it.next()).onStatusBarIconsBehaviorChanged(z);
        }
    }

    public final void unsnoozeNotification(String str) {
        if (isBound()) {
            try {
                getNotificationInterface().unsnoozeNotificationFromSystemListener(this.mWrapper, str);
            } catch (RemoteException e) {
                Log.v("NotificationListener", "Unable to contact notification manager", e);
            }
        }
    }

    public void registerAsSystemService() {
        try {
            registerAsSystemService(this.mContext, new ComponentName(this.mContext.getPackageName(), getClass().getCanonicalName()), -1);
        } catch (RemoteException e) {
            Log.e("NotificationListener", "Unable to register notification listener", e);
        }
    }

    private static Ranking getRankingOrTemporaryStandIn(RankingMap rankingMap, String str) {
        Ranking ranking = new Ranking();
        if (rankingMap.getRanking(str, ranking)) {
            return ranking;
        }
        ArrayList arrayList = r0;
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = r0;
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = r0;
        ArrayList arrayList6 = new ArrayList();
        ArrayList arrayList7 = r0;
        ArrayList arrayList8 = new ArrayList();
        Ranking ranking2 = ranking;
        ranking.populate(str, 0, false, 0, 0, 0, null, null, null, arrayList, arrayList3, false, 0, false, 0, false, arrayList5, arrayList7, false, false, false, null, false);
        return ranking2;
    }
}
