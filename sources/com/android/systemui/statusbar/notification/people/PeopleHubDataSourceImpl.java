package com.android.systemui.statusbar.notification.people;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.StatusBarNotification;
import android.util.IconDrawableFactory;
import android.util.SparseArray;
import com.android.settingslib.notification.ConversationIconFactory;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PeopleHubNotificationListener.kt */
public final class PeopleHubDataSourceImpl implements DataSource<PeopleHubModel> {
    private final Executor bgExecutor;
    /* access modifiers changed from: private */
    public final List<DataListener<PeopleHubModel>> dataListeners = new ArrayList();
    private final NotificationPersonExtractor extractor;
    private final ConversationIconFactory iconFactory;
    /* access modifiers changed from: private */
    public final Executor mainExecutor;
    private final NotificationLockscreenUserManager notifLockscreenUserMgr;
    /* access modifiers changed from: private */
    public final PeopleHubDataSourceImpl$notificationEntryListener$1 notificationEntryListener;
    /* access modifiers changed from: private */
    public final NotificationEntryManager notificationEntryManager;
    /* access modifiers changed from: private */
    public final NotificationListener notificationListener;
    /* access modifiers changed from: private */
    public final SparseArray<PeopleHubManager> peopleHubManagerForUser = new SparseArray<>();
    private final PeopleNotificationIdentifier peopleNotificationIdentifier;
    /* access modifiers changed from: private */
    public Subscription userChangeSubscription;
    /* access modifiers changed from: private */
    public final UserManager userManager;

    public PeopleHubDataSourceImpl(NotificationEntryManager notificationEntryManager2, NotificationPersonExtractor notificationPersonExtractor, UserManager userManager2, LauncherApps launcherApps, PackageManager packageManager, Context context, NotificationListener notificationListener2, Executor executor, Executor executor2, NotificationLockscreenUserManager notificationLockscreenUserManager, PeopleNotificationIdentifier peopleNotificationIdentifier2) {
        Intrinsics.checkParameterIsNotNull(notificationEntryManager2, "notificationEntryManager");
        Intrinsics.checkParameterIsNotNull(notificationPersonExtractor, "extractor");
        Intrinsics.checkParameterIsNotNull(userManager2, "userManager");
        Intrinsics.checkParameterIsNotNull(launcherApps, "launcherApps");
        Intrinsics.checkParameterIsNotNull(packageManager, "packageManager");
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(notificationListener2, "notificationListener");
        Intrinsics.checkParameterIsNotNull(executor, "bgExecutor");
        Intrinsics.checkParameterIsNotNull(executor2, "mainExecutor");
        Intrinsics.checkParameterIsNotNull(notificationLockscreenUserManager, "notifLockscreenUserMgr");
        Intrinsics.checkParameterIsNotNull(peopleNotificationIdentifier2, "peopleNotificationIdentifier");
        this.notificationEntryManager = notificationEntryManager2;
        this.extractor = notificationPersonExtractor;
        this.userManager = userManager2;
        this.notificationListener = notificationListener2;
        this.bgExecutor = executor;
        this.mainExecutor = executor2;
        this.notifLockscreenUserMgr = notificationLockscreenUserManager;
        this.peopleNotificationIdentifier = peopleNotificationIdentifier2;
        Context applicationContext = context.getApplicationContext();
        IconDrawableFactory newInstance = IconDrawableFactory.newInstance(applicationContext);
        Intrinsics.checkExpressionValueIsNotNull(applicationContext, "appContext");
        ConversationIconFactory conversationIconFactory = new ConversationIconFactory(applicationContext, launcherApps, packageManager, newInstance, applicationContext.getResources().getDimensionPixelSize(C2009R$dimen.notification_guts_conversation_icon_size));
        this.iconFactory = conversationIconFactory;
        this.notificationEntryListener = new PeopleHubDataSourceImpl$notificationEntryListener$1(this);
    }

    /* access modifiers changed from: private */
    public final void removeVisibleEntry(NotificationEntry notificationEntry, int i) {
        NotificationPersonExtractor notificationPersonExtractor = this.extractor;
        StatusBarNotification sbn = notificationEntry.getSbn();
        String str = "entry.sbn";
        Intrinsics.checkExpressionValueIsNotNull(sbn, str);
        String extractPersonKey = notificationPersonExtractor.extractPersonKey(sbn);
        if (extractPersonKey == null) {
            extractPersonKey = extractPersonKey(notificationEntry);
        }
        String str2 = extractPersonKey;
        if (str2 != null) {
            StatusBarNotification sbn2 = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn2, str);
            UserHandle user = sbn2.getUser();
            Intrinsics.checkExpressionValueIsNotNull(user, "entry.sbn.user");
            int identifier = user.getIdentifier();
            Executor executor = this.bgExecutor;
            PeopleHubDataSourceImpl$removeVisibleEntry$$inlined$let$lambda$1 peopleHubDataSourceImpl$removeVisibleEntry$$inlined$let$lambda$1 = new PeopleHubDataSourceImpl$removeVisibleEntry$$inlined$let$lambda$1(identifier, str2, this, notificationEntry, i);
            executor.execute(peopleHubDataSourceImpl$removeVisibleEntry$$inlined$let$lambda$1);
        }
    }

    /* access modifiers changed from: private */
    public final void addVisibleEntry(NotificationEntry notificationEntry) {
        PersonModel extractPerson = extractPerson(notificationEntry);
        if (extractPerson != null) {
            StatusBarNotification sbn = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn, "entry.sbn");
            UserHandle user = sbn.getUser();
            Intrinsics.checkExpressionValueIsNotNull(user, "entry.sbn.user");
            this.bgExecutor.execute(new PeopleHubDataSourceImpl$addVisibleEntry$$inlined$let$lambda$1(user.getIdentifier(), extractPerson, this, notificationEntry));
        }
    }

    public Subscription registerListener(DataListener<? super PeopleHubModel> dataListener) {
        Intrinsics.checkParameterIsNotNull(dataListener, "listener");
        boolean isEmpty = this.dataListeners.isEmpty();
        this.dataListeners.add(dataListener);
        if (isEmpty) {
            this.userChangeSubscription = PeopleHubNotificationListenerKt.registerListener(this.notifLockscreenUserMgr, new PeopleHubDataSourceImpl$registerListener$1(this));
            this.notificationEntryManager.addNotificationEntryListener(this.notificationEntryListener);
        } else {
            PeopleHubModel peopleHubModelForCurrentUser = getPeopleHubModelForCurrentUser();
            if (peopleHubModelForCurrentUser != null) {
                dataListener.onDataChanged(peopleHubModelForCurrentUser);
            }
        }
        return new PeopleHubDataSourceImpl$registerListener$3(this, dataListener);
    }

    private final PeopleHubModel getPeopleHubModelForCurrentUser() {
        PeopleHubManager peopleHubManager = (PeopleHubManager) this.peopleHubManagerForUser.get(this.notifLockscreenUserMgr.getCurrentUserId());
        if (peopleHubManager != null) {
            PeopleHubModel peopleHubModel = peopleHubManager.getPeopleHubModel();
            if (peopleHubModel != null) {
                SparseArray currentProfiles = this.notifLockscreenUserMgr.getCurrentProfiles();
                Collection people = peopleHubModel.getPeople();
                ArrayList arrayList = new ArrayList();
                for (Object next : people) {
                    UserInfo userInfo = (UserInfo) currentProfiles.get(((PersonModel) next).getUserId());
                    if (userInfo != null && !userInfo.isQuietModeEnabled()) {
                        arrayList.add(next);
                    }
                }
                return peopleHubModel.copy(arrayList);
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public final void updateUi() {
        PeopleHubModel peopleHubModelForCurrentUser = getPeopleHubModelForCurrentUser();
        if (peopleHubModelForCurrentUser != null) {
            for (DataListener onDataChanged : this.dataListeners) {
                onDataChanged.onDataChanged(peopleHubModelForCurrentUser);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x003f, code lost:
        if (r3 != null) goto L_0x0048;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final com.android.systemui.statusbar.notification.people.PersonModel extractPerson(com.android.systemui.statusbar.notification.collection.NotificationEntry r12) {
        /*
            r11 = this;
            com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier r0 = r11.peopleNotificationIdentifier
            android.service.notification.StatusBarNotification r1 = r12.getSbn()
            java.lang.String r2 = "sbn"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r1, r2)
            android.service.notification.NotificationListenerService$Ranking r3 = r12.getRanking()
            java.lang.String r4 = "ranking"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r3, r4)
            int r0 = r0.getPeopleNotificationType(r1, r3)
            r1 = 0
            if (r0 != 0) goto L_0x001c
            return r1
        L_0x001c:
            com.android.systemui.statusbar.notification.people.PeopleHubDataSourceImpl$extractPerson$clickRunnable$1 r10 = new com.android.systemui.statusbar.notification.people.PeopleHubDataSourceImpl$extractPerson$clickRunnable$1
            r10.<init>(r11, r12)
            android.service.notification.StatusBarNotification r0 = r12.getSbn()
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r2)
            android.app.Notification r0 = r0.getNotification()
            android.os.Bundle r0 = r0.extras
            android.service.notification.NotificationListenerService$Ranking r3 = r12.getRanking()
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r3, r4)
            android.content.pm.ShortcutInfo r3 = r3.getShortcutInfo()
            if (r3 == 0) goto L_0x0042
            java.lang.CharSequence r3 = r3.getShortLabel()
            if (r3 == 0) goto L_0x0042
            goto L_0x0048
        L_0x0042:
            java.lang.String r3 = "android.conversationTitle"
            java.lang.String r3 = r0.getString(r3)
        L_0x0048:
            if (r3 == 0) goto L_0x004c
            r8 = r3
            goto L_0x0053
        L_0x004c:
            java.lang.String r3 = "android.title"
            java.lang.String r0 = r0.getString(r3)
            r8 = r0
        L_0x0053:
            if (r8 == 0) goto L_0x00cb
            android.service.notification.NotificationListenerService$Ranking r0 = r12.getRanking()
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r4)
            com.android.settingslib.notification.ConversationIconFactory r1 = r11.iconFactory
            android.service.notification.StatusBarNotification r3 = r12.getSbn()
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r3, r2)
            android.graphics.drawable.Drawable r0 = r11.getIcon(r0, r1, r3)
            if (r0 == 0) goto L_0x006d
            r9 = r0
            goto L_0x00a2
        L_0x006d:
            com.android.settingslib.notification.ConversationIconFactory r11 = r11.iconFactory
            android.graphics.drawable.Drawable r0 = com.android.systemui.statusbar.notification.people.PeopleHubNotificationListenerKt.extractAvatarFromRow(r12)
            android.service.notification.StatusBarNotification r1 = r12.getSbn()
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r1, r2)
            java.lang.String r1 = r1.getPackageName()
            android.service.notification.StatusBarNotification r3 = r12.getSbn()
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r3, r2)
            int r3 = r3.getUid()
            android.service.notification.NotificationListenerService$Ranking r5 = r12.getRanking()
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r5, r4)
            android.app.NotificationChannel r4 = r5.getChannel()
            java.lang.String r5 = "ranking.channel"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r4, r5)
            boolean r4 = r4.isImportantConversation()
            android.graphics.drawable.Drawable r11 = r11.getConversationDrawable(r0, r1, r3, r4)
            r9 = r11
        L_0x00a2:
            com.android.systemui.statusbar.notification.people.PersonModel r11 = new com.android.systemui.statusbar.notification.people.PersonModel
            java.lang.String r6 = r12.getKey()
            java.lang.String r0 = "key"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r6, r0)
            android.service.notification.StatusBarNotification r12 = r12.getSbn()
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r12, r2)
            android.os.UserHandle r12 = r12.getUser()
            java.lang.String r0 = "sbn.user"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r12, r0)
            int r7 = r12.getIdentifier()
            java.lang.String r12 = "drawable"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r9, r12)
            r5 = r11
            r5.<init>(r6, r7, r8, r9, r10)
            return r11
        L_0x00cb:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.people.PeopleHubDataSourceImpl.extractPerson(com.android.systemui.statusbar.notification.collection.NotificationEntry):com.android.systemui.statusbar.notification.people.PersonModel");
    }

    private final Drawable getIcon(Ranking ranking, ConversationIconFactory conversationIconFactory, StatusBarNotification statusBarNotification) {
        ShortcutInfo shortcutInfo = ranking.getShortcutInfo();
        if (shortcutInfo == null) {
            return null;
        }
        String packageName = statusBarNotification.getPackageName();
        int uid = statusBarNotification.getUid();
        NotificationChannel channel = ranking.getChannel();
        Intrinsics.checkExpressionValueIsNotNull(channel, "channel");
        return conversationIconFactory.getConversationDrawable(shortcutInfo, packageName, uid, channel.isImportantConversation());
    }

    private final String extractPersonKey(NotificationEntry notificationEntry) {
        PeopleNotificationIdentifier peopleNotificationIdentifier2 = this.peopleNotificationIdentifier;
        StatusBarNotification sbn = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn, "sbn");
        Ranking ranking = notificationEntry.getRanking();
        Intrinsics.checkExpressionValueIsNotNull(ranking, "ranking");
        if (peopleNotificationIdentifier2.getPeopleNotificationType(sbn, ranking) != 0) {
            return notificationEntry.getKey();
        }
        return null;
    }
}
