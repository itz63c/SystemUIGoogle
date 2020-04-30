package com.android.systemui.statusbar.notification.collection;

import android.os.RemoteException;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.Pair;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.collection.NotificationEntry.DismissState;
import com.android.systemui.statusbar.notification.collection.coalescer.CoalescedEvent;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescer;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescer.BatchableNotificationHandler;
import com.android.systemui.statusbar.notification.collection.notifcollection.CleanUpEntryEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.CollectionReadyForBuildListener;
import com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats;
import com.android.systemui.statusbar.notification.collection.notifcollection.EntryAddedEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.EntryRemovedEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.EntryUpdatedEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.InitEntryEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionLogger;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifDismissInterceptor;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifDismissInterceptor.OnEndDismissInterception;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender.OnEndLifetimeExtensionCallback;
import com.android.systemui.statusbar.notification.collection.notifcollection.RankingAppliedEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.RankingUpdatedEvent;
import com.android.systemui.util.Assert;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

public class NotifCollection implements Dumpable {
    private boolean mAmDispatchingToOtherCode;
    private boolean mAttached = false;
    private CollectionReadyForBuildListener mBuildListener;
    private final List<NotifDismissInterceptor> mDismissInterceptors = new ArrayList();
    private Queue<NotifEvent> mEventQueue = new ArrayDeque();
    private final FeatureFlags mFeatureFlags;
    private final List<NotifLifetimeExtender> mLifetimeExtenders = new ArrayList();
    private final NotifCollectionLogger mLogger;
    private final List<NotifCollectionListener> mNotifCollectionListeners = new ArrayList();
    private final BatchableNotificationHandler mNotifHandler = new BatchableNotificationHandler() {
        public void onNotificationPosted(StatusBarNotification statusBarNotification, RankingMap rankingMap) {
            NotifCollection.this.onNotificationPosted(statusBarNotification, rankingMap);
        }

        public void onNotificationBatchPosted(List<CoalescedEvent> list) {
            NotifCollection.this.onNotificationGroupPosted(list);
        }

        public void onNotificationRemoved(StatusBarNotification statusBarNotification, RankingMap rankingMap, int i) {
            NotifCollection.this.onNotificationRemoved(statusBarNotification, rankingMap, i);
        }

        public void onNotificationRankingUpdate(RankingMap rankingMap) {
            NotifCollection.this.onNotificationRankingUpdate(rankingMap);
        }
    };
    private final Map<String, NotificationEntry> mNotificationSet;
    private final Collection<NotificationEntry> mReadOnlyNotificationSet;
    private final IStatusBarService mStatusBarService;

    public NotifCollection(IStatusBarService iStatusBarService, DumpManager dumpManager, FeatureFlags featureFlags, NotifCollectionLogger notifCollectionLogger) {
        ArrayMap arrayMap = new ArrayMap();
        this.mNotificationSet = arrayMap;
        this.mReadOnlyNotificationSet = Collections.unmodifiableCollection(arrayMap.values());
        Assert.isMainThread();
        this.mStatusBarService = iStatusBarService;
        this.mLogger = notifCollectionLogger;
        dumpManager.registerDumpable("NotifCollection", this);
        this.mFeatureFlags = featureFlags;
    }

    public void attach(GroupCoalescer groupCoalescer) {
        Assert.isMainThread();
        if (!this.mAttached) {
            this.mAttached = true;
            groupCoalescer.setNotificationHandler(this.mNotifHandler);
            return;
        }
        throw new RuntimeException("attach() called twice");
    }

    /* access modifiers changed from: 0000 */
    public void setBuildListener(CollectionReadyForBuildListener collectionReadyForBuildListener) {
        Assert.isMainThread();
        this.mBuildListener = collectionReadyForBuildListener;
    }

    /* access modifiers changed from: 0000 */
    public Collection<NotificationEntry> getAllNotifs() {
        Assert.isMainThread();
        return this.mReadOnlyNotificationSet;
    }

    /* access modifiers changed from: 0000 */
    public void addCollectionListener(NotifCollectionListener notifCollectionListener) {
        Assert.isMainThread();
        this.mNotifCollectionListeners.add(notifCollectionListener);
    }

    /* access modifiers changed from: 0000 */
    public void addNotificationLifetimeExtender(NotifLifetimeExtender notifLifetimeExtender) {
        Assert.isMainThread();
        checkForReentrantCall();
        if (!this.mLifetimeExtenders.contains(notifLifetimeExtender)) {
            this.mLifetimeExtenders.add(notifLifetimeExtender);
            notifLifetimeExtender.setCallback(new OnEndLifetimeExtensionCallback() {
                public final void onEndLifetimeExtension(NotifLifetimeExtender notifLifetimeExtender, NotificationEntry notificationEntry) {
                    NotifCollection.this.onEndLifetimeExtension(notifLifetimeExtender, notificationEntry);
                }
            });
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Extender ");
        sb.append(notifLifetimeExtender);
        sb.append(" already added.");
        throw new IllegalArgumentException(sb.toString());
    }

    /* access modifiers changed from: 0000 */
    public void addNotificationDismissInterceptor(NotifDismissInterceptor notifDismissInterceptor) {
        Assert.isMainThread();
        checkForReentrantCall();
        if (!this.mDismissInterceptors.contains(notifDismissInterceptor)) {
            this.mDismissInterceptors.add(notifDismissInterceptor);
            notifDismissInterceptor.setCallback(new OnEndDismissInterception() {
                public final void onEndDismissInterception(NotifDismissInterceptor notifDismissInterceptor, NotificationEntry notificationEntry, DismissedByUserStats dismissedByUserStats) {
                    NotifCollection.this.onEndDismissInterception(notifDismissInterceptor, notificationEntry, dismissedByUserStats);
                }
            });
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Interceptor ");
        sb.append(notifDismissInterceptor);
        sb.append(" already added.");
        throw new IllegalArgumentException(sb.toString());
    }

    public void dismissNotifications(List<Pair<NotificationEntry, DismissedByUserStats>> list) {
        Assert.isMainThread();
        checkForReentrantCall();
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < list.size()) {
            NotificationEntry notificationEntry = (NotificationEntry) ((Pair) list.get(i)).first;
            DismissedByUserStats dismissedByUserStats = (DismissedByUserStats) ((Pair) list.get(i)).second;
            Objects.requireNonNull(dismissedByUserStats);
            if (notificationEntry == this.mNotificationSet.get(notificationEntry.getKey())) {
                if (notificationEntry.getDismissState() != DismissState.DISMISSED) {
                    updateDismissInterceptors(notificationEntry);
                    if (isDismissIntercepted(notificationEntry)) {
                        this.mLogger.logNotifDismissedIntercepted(notificationEntry.getKey());
                    } else {
                        arrayList.add(notificationEntry);
                        if (!isCanceled(notificationEntry)) {
                            try {
                                this.mStatusBarService.onNotificationClear(notificationEntry.getSbn().getPackageName(), notificationEntry.getSbn().getTag(), notificationEntry.getSbn().getId(), notificationEntry.getSbn().getUser().getIdentifier(), notificationEntry.getSbn().getKey(), dismissedByUserStats.dismissalSurface, dismissedByUserStats.dismissalSentiment, dismissedByUserStats.notificationVisibility);
                            } catch (RemoteException unused) {
                            }
                        }
                    }
                }
                i++;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Invalid entry: ");
                sb.append(notificationEntry.getKey());
                throw new IllegalStateException(sb.toString());
            }
        }
        locallyDismissNotifications(arrayList);
        dispatchEventsAndRebuildList();
    }

    public void dismissNotification(NotificationEntry notificationEntry, DismissedByUserStats dismissedByUserStats) {
        dismissNotifications(List.of(new Pair(notificationEntry, dismissedByUserStats)));
    }

    public void dismissAllNotifications(int i) {
        Assert.isMainThread();
        checkForReentrantCall();
        try {
            this.mStatusBarService.onClearAllNotifications(i);
        } catch (RemoteException unused) {
        }
        ArrayList arrayList = new ArrayList(getAllNotifs());
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            NotificationEntry notificationEntry = (NotificationEntry) arrayList.get(size);
            if (!shouldDismissOnClearAll(notificationEntry, i)) {
                updateDismissInterceptors(notificationEntry);
                if (isDismissIntercepted(notificationEntry)) {
                    this.mLogger.logNotifClearAllDismissalIntercepted(notificationEntry.getKey());
                }
                arrayList.remove(size);
            }
        }
        locallyDismissNotifications(arrayList);
        dispatchEventsAndRebuildList();
    }

    private void locallyDismissNotifications(List<NotificationEntry> list) {
        ArrayList<NotificationEntry> arrayList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            NotificationEntry notificationEntry = (NotificationEntry) list.get(i);
            notificationEntry.setDismissState(DismissState.DISMISSED);
            this.mLogger.logNotifDismissed(notificationEntry.getKey());
            if (isCanceled(notificationEntry)) {
                arrayList.add(notificationEntry);
            } else if (notificationEntry.getSbn().getNotification().isGroupSummary()) {
                for (NotificationEntry notificationEntry2 : this.mNotificationSet.values()) {
                    if (shouldAutoDismissChildren(notificationEntry2, notificationEntry.getSbn().getGroupKey())) {
                        notificationEntry2.setDismissState(DismissState.PARENT_DISMISSED);
                        if (isCanceled(notificationEntry2)) {
                            arrayList.add(notificationEntry2);
                        }
                    }
                }
            }
        }
        for (NotificationEntry tryRemoveNotification : arrayList) {
            tryRemoveNotification(tryRemoveNotification);
        }
    }

    /* access modifiers changed from: private */
    public void onNotificationPosted(StatusBarNotification statusBarNotification, RankingMap rankingMap) {
        Assert.isMainThread();
        postNotification(statusBarNotification, requireRanking(rankingMap, statusBarNotification.getKey()));
        applyRanking(rankingMap);
        dispatchEventsAndRebuildList();
    }

    /* access modifiers changed from: private */
    public void onNotificationGroupPosted(List<CoalescedEvent> list) {
        Assert.isMainThread();
        this.mLogger.logNotifGroupPosted(((CoalescedEvent) list.get(0)).getSbn().getGroupKey(), list.size());
        for (CoalescedEvent coalescedEvent : list) {
            postNotification(coalescedEvent.getSbn(), coalescedEvent.getRanking());
        }
        dispatchEventsAndRebuildList();
    }

    /* access modifiers changed from: private */
    public void onNotificationRemoved(StatusBarNotification statusBarNotification, RankingMap rankingMap, int i) {
        Assert.isMainThread();
        this.mLogger.logNotifRemoved(statusBarNotification.getKey(), i);
        NotificationEntry notificationEntry = (NotificationEntry) this.mNotificationSet.get(statusBarNotification.getKey());
        if (notificationEntry != null) {
            notificationEntry.mCancellationReason = i;
            tryRemoveNotification(notificationEntry);
            applyRanking(rankingMap);
            dispatchEventsAndRebuildList();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("No notification to remove with key ");
        sb.append(statusBarNotification.getKey());
        throw new IllegalStateException(sb.toString());
    }

    /* access modifiers changed from: private */
    public void onNotificationRankingUpdate(RankingMap rankingMap) {
        Assert.isMainThread();
        this.mEventQueue.add(new RankingUpdatedEvent(rankingMap));
        applyRanking(rankingMap);
        dispatchEventsAndRebuildList();
    }

    private void postNotification(StatusBarNotification statusBarNotification, Ranking ranking) {
        NotificationEntry notificationEntry = (NotificationEntry) this.mNotificationSet.get(statusBarNotification.getKey());
        if (notificationEntry == null) {
            NotificationEntry notificationEntry2 = new NotificationEntry(statusBarNotification, ranking);
            this.mNotificationSet.put(statusBarNotification.getKey(), notificationEntry2);
            this.mLogger.logNotifPosted(statusBarNotification.getKey());
            this.mEventQueue.add(new InitEntryEvent(notificationEntry2));
            this.mEventQueue.add(new EntryAddedEvent(notificationEntry2));
            return;
        }
        cancelLocalDismissal(notificationEntry);
        cancelLifetimeExtension(notificationEntry);
        cancelDismissInterception(notificationEntry);
        notificationEntry.mCancellationReason = -1;
        notificationEntry.setSbn(statusBarNotification);
        this.mLogger.logNotifUpdated(statusBarNotification.getKey());
        this.mEventQueue.add(new EntryUpdatedEvent(notificationEntry));
    }

    private boolean tryRemoveNotification(NotificationEntry notificationEntry) {
        if (this.mNotificationSet.get(notificationEntry.getKey()) != notificationEntry) {
            StringBuilder sb = new StringBuilder();
            sb.append("No notification to remove with key ");
            sb.append(notificationEntry.getKey());
            throw new IllegalStateException(sb.toString());
        } else if (isCanceled(notificationEntry)) {
            if (isDismissedByUser(notificationEntry)) {
                cancelLifetimeExtension(notificationEntry);
            } else {
                updateLifetimeExtension(notificationEntry);
            }
            if (isLifetimeExtended(notificationEntry)) {
                return false;
            }
            this.mNotificationSet.remove(notificationEntry.getKey());
            cancelDismissInterception(notificationEntry);
            this.mEventQueue.add(new EntryRemovedEvent(notificationEntry, notificationEntry.mCancellationReason));
            this.mEventQueue.add(new CleanUpEntryEvent(notificationEntry));
            return true;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Cannot remove notification ");
            sb2.append(notificationEntry.getKey());
            sb2.append(": has not been marked for removal");
            throw new IllegalStateException(sb2.toString());
        }
    }

    private void applyRanking(RankingMap rankingMap) {
        for (NotificationEntry notificationEntry : this.mNotificationSet.values()) {
            if (!isCanceled(notificationEntry)) {
                Ranking ranking = new Ranking();
                if (rankingMap.getRanking(notificationEntry.getKey(), ranking)) {
                    notificationEntry.setRanking(ranking);
                    if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
                        String overrideGroupKey = ranking.getOverrideGroupKey();
                        if (!Objects.equals(notificationEntry.getSbn().getOverrideGroupKey(), overrideGroupKey)) {
                            notificationEntry.getSbn().setOverrideGroupKey(overrideGroupKey);
                        }
                    }
                } else {
                    this.mLogger.logRankingMissing(notificationEntry.getKey(), rankingMap);
                }
            }
        }
        this.mEventQueue.add(new RankingAppliedEvent());
    }

    private void dispatchEventsAndRebuildList() {
        this.mAmDispatchingToOtherCode = true;
        while (!this.mEventQueue.isEmpty()) {
            ((NotifEvent) this.mEventQueue.remove()).dispatchTo(this.mNotifCollectionListeners);
        }
        this.mAmDispatchingToOtherCode = false;
        CollectionReadyForBuildListener collectionReadyForBuildListener = this.mBuildListener;
        if (collectionReadyForBuildListener != null) {
            collectionReadyForBuildListener.onBuildList(this.mReadOnlyNotificationSet);
        }
    }

    /* access modifiers changed from: private */
    public void onEndLifetimeExtension(NotifLifetimeExtender notifLifetimeExtender, NotificationEntry notificationEntry) {
        Assert.isMainThread();
        if (this.mAttached) {
            checkForReentrantCall();
            if (notificationEntry.mLifetimeExtenders.remove(notifLifetimeExtender)) {
                if (!isLifetimeExtended(notificationEntry) && tryRemoveNotification(notificationEntry)) {
                    dispatchEventsAndRebuildList();
                }
                return;
            }
            throw new IllegalStateException(String.format("Cannot end lifetime extension for extender \"%s\" (%s)", new Object[]{notifLifetimeExtender.getName(), notifLifetimeExtender}));
        }
    }

    private void cancelLifetimeExtension(NotificationEntry notificationEntry) {
        this.mAmDispatchingToOtherCode = true;
        for (NotifLifetimeExtender cancelLifetimeExtension : notificationEntry.mLifetimeExtenders) {
            cancelLifetimeExtension.cancelLifetimeExtension(notificationEntry);
        }
        this.mAmDispatchingToOtherCode = false;
        notificationEntry.mLifetimeExtenders.clear();
    }

    private boolean isLifetimeExtended(NotificationEntry notificationEntry) {
        return notificationEntry.mLifetimeExtenders.size() > 0;
    }

    private void updateLifetimeExtension(NotificationEntry notificationEntry) {
        notificationEntry.mLifetimeExtenders.clear();
        this.mAmDispatchingToOtherCode = true;
        for (NotifLifetimeExtender notifLifetimeExtender : this.mLifetimeExtenders) {
            if (notifLifetimeExtender.shouldExtendLifetime(notificationEntry, notificationEntry.mCancellationReason)) {
                notificationEntry.mLifetimeExtenders.add(notifLifetimeExtender);
            }
        }
        this.mAmDispatchingToOtherCode = false;
    }

    private void updateDismissInterceptors(NotificationEntry notificationEntry) {
        notificationEntry.mDismissInterceptors.clear();
        this.mAmDispatchingToOtherCode = true;
        for (NotifDismissInterceptor notifDismissInterceptor : this.mDismissInterceptors) {
            if (notifDismissInterceptor.shouldInterceptDismissal(notificationEntry)) {
                notificationEntry.mDismissInterceptors.add(notifDismissInterceptor);
            }
        }
        this.mAmDispatchingToOtherCode = false;
    }

    private void cancelLocalDismissal(NotificationEntry notificationEntry) {
        if (isDismissedByUser(notificationEntry)) {
            notificationEntry.setDismissState(DismissState.NOT_DISMISSED);
            if (notificationEntry.getSbn().getNotification().isGroupSummary()) {
                for (NotificationEntry notificationEntry2 : this.mNotificationSet.values()) {
                    if (notificationEntry2.getSbn().getGroupKey().equals(notificationEntry.getSbn().getGroupKey()) && notificationEntry2.getDismissState() == DismissState.PARENT_DISMISSED) {
                        notificationEntry2.setDismissState(DismissState.NOT_DISMISSED);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void onEndDismissInterception(NotifDismissInterceptor notifDismissInterceptor, NotificationEntry notificationEntry, DismissedByUserStats dismissedByUserStats) {
        Assert.isMainThread();
        if (this.mAttached) {
            checkForReentrantCall();
            if (notificationEntry.mDismissInterceptors.remove(notifDismissInterceptor)) {
                if (!isDismissIntercepted(notificationEntry)) {
                    dismissNotification(notificationEntry, dismissedByUserStats);
                }
                return;
            }
            throw new IllegalStateException(String.format("Cannot end dismiss interceptor for interceptor \"%s\" (%s)", new Object[]{notifDismissInterceptor.getName(), notifDismissInterceptor}));
        }
    }

    private void cancelDismissInterception(NotificationEntry notificationEntry) {
        this.mAmDispatchingToOtherCode = true;
        for (NotifDismissInterceptor cancelDismissInterception : notificationEntry.mDismissInterceptors) {
            cancelDismissInterception.cancelDismissInterception(notificationEntry);
        }
        this.mAmDispatchingToOtherCode = false;
        notificationEntry.mDismissInterceptors.clear();
    }

    private boolean isDismissIntercepted(NotificationEntry notificationEntry) {
        return notificationEntry.mDismissInterceptors.size() > 0;
    }

    private void checkForReentrantCall() {
        if (this.mAmDispatchingToOtherCode) {
            throw new IllegalStateException("Reentrant call detected");
        }
    }

    private static Ranking requireRanking(RankingMap rankingMap, String str) {
        Ranking ranking = new Ranking();
        if (rankingMap.getRanking(str, ranking)) {
            return ranking;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Ranking map doesn't contain key: ");
        sb.append(str);
        throw new IllegalArgumentException(sb.toString());
    }

    private static boolean isCanceled(NotificationEntry notificationEntry) {
        return notificationEntry.mCancellationReason != -1;
    }

    private static boolean isDismissedByUser(NotificationEntry notificationEntry) {
        return notificationEntry.getDismissState() != DismissState.NOT_DISMISSED;
    }

    private static boolean shouldAutoDismissChildren(NotificationEntry notificationEntry, String str) {
        return notificationEntry.getSbn().getGroupKey().equals(str) && !notificationEntry.getSbn().getNotification().isGroupSummary() && !hasFlag(notificationEntry, 64) && !hasFlag(notificationEntry, 4096) && notificationEntry.getDismissState() != DismissState.DISMISSED;
    }

    private static boolean shouldDismissOnClearAll(NotificationEntry notificationEntry, int i) {
        return userIdMatches(notificationEntry, i) && notificationEntry.isClearable() && !hasFlag(notificationEntry, 4096) && notificationEntry.getDismissState() != DismissState.DISMISSED;
    }

    private static boolean hasFlag(NotificationEntry notificationEntry, int i) {
        return (notificationEntry.getSbn().getNotification().flags & i) != 0;
    }

    private static boolean userIdMatches(NotificationEntry notificationEntry, int i) {
        return i == -1 || notificationEntry.getSbn().getUser().getIdentifier() == -1 || notificationEntry.getSbn().getUser().getIdentifier() == i;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        ArrayList arrayList = new ArrayList(getAllNotifs());
        printWriter.println("\tNotifCollection unsorted/unfiltered notifications:");
        if (arrayList.size() == 0) {
            printWriter.println("\t\t None");
        }
        printWriter.println(ListDumper.dumpList(arrayList, true, "\t\t"));
    }
}
