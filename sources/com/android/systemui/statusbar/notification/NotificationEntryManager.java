package com.android.systemui.statusbar.notification;

import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.systemui.statusbar.NotificationLifetimeExtender.NotificationSafeToRemoveCallback;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationListener.NotificationHandler;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationRemoveInterceptor;
import com.android.systemui.statusbar.NotificationUiAdjustment;
import com.android.systemui.statusbar.notification.VisualStabilityManager.Callback;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.NotificationRankingManager;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinder;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder.InflationCallback;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.util.Assert;
import com.android.systemui.util.leak.LeakDetector;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NotificationEntryManager implements CommonNotifCollection, Dumpable, InflationCallback, Callback {
    private static final boolean DEBUG = Log.isLoggable("NotificationEntryMgr", 3);
    /* access modifiers changed from: private */
    public final ArrayMap<String, NotificationEntry> mActiveNotifications = new ArrayMap<>();
    private final Set<NotificationEntry> mAllNotifications;
    private final FeatureFlags mFeatureFlags;
    private final ForegroundServiceDismissalFeatureController mFgsFeatureController;
    private final NotificationGroupManager mGroupManager;
    private final KeyguardEnvironment mKeyguardEnvironment;
    private RankingMap mLatestRankingMap;
    private final LeakDetector mLeakDetector;
    private final NotificationEntryManagerLogger mLogger;
    private final List<NotifCollectionListener> mNotifCollectionListeners;
    private final NotificationHandler mNotifListener;
    private final List<NotificationEntryListener> mNotificationEntryListeners;
    @VisibleForTesting
    final ArrayList<NotificationLifetimeExtender> mNotificationLifetimeExtenders;
    private final Lazy<NotificationRowBinder> mNotificationRowBinderLazy;
    @VisibleForTesting
    protected final HashMap<String, NotificationEntry> mPendingNotifications = new HashMap<>();
    private NotificationPresenter mPresenter;
    private final NotificationRankingManager mRankingManager;
    private final Set<NotificationEntry> mReadOnlyAllNotifications;
    private final List<NotificationEntry> mReadOnlyNotifications;
    private final Lazy<NotificationRemoteInputManager> mRemoteInputManagerLazy;
    private final List<NotificationRemoveInterceptor> mRemoveInterceptors;
    private final Map<NotificationEntry, NotificationLifetimeExtender> mRetainedNotifications;
    @VisibleForTesting
    protected final ArrayList<NotificationEntry> mSortedAndFiltered;

    public interface KeyguardEnvironment {
        boolean isDeviceProvisioned();

        boolean isNotificationForCurrentProfiles(StatusBarNotification statusBarNotification);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        printWriter.println("NotificationEntryManager state:");
        printWriter.print("  mPendingNotifications=");
        if (this.mPendingNotifications.size() == 0) {
            printWriter.println("null");
        } else {
            for (NotificationEntry sbn : this.mPendingNotifications.values()) {
                printWriter.println(sbn.getSbn());
            }
        }
        printWriter.println("  Remove interceptors registered:");
        Iterator it = this.mRemoveInterceptors.iterator();
        while (true) {
            str = "    ";
            if (!it.hasNext()) {
                break;
            }
            NotificationRemoveInterceptor notificationRemoveInterceptor = (NotificationRemoveInterceptor) it.next();
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(notificationRemoveInterceptor.getClass().getSimpleName());
            printWriter.println(sb.toString());
        }
        printWriter.println("  Lifetime extenders registered:");
        Iterator it2 = this.mNotificationLifetimeExtenders.iterator();
        while (it2.hasNext()) {
            NotificationLifetimeExtender notificationLifetimeExtender = (NotificationLifetimeExtender) it2.next();
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(notificationLifetimeExtender.getClass().getSimpleName());
            printWriter.println(sb2.toString());
        }
        printWriter.println("  Lifetime-extended notifications:");
        if (this.mRetainedNotifications.isEmpty()) {
            printWriter.println("    None");
            return;
        }
        for (Entry entry : this.mRetainedNotifications.entrySet()) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append(((NotificationEntry) entry.getKey()).getSbn());
            sb3.append(" retained by ");
            sb3.append(((NotificationLifetimeExtender) entry.getValue()).getClass().getName());
            printWriter.println(sb3.toString());
        }
    }

    public NotificationEntryManager(NotificationEntryManagerLogger notificationEntryManagerLogger, NotificationGroupManager notificationGroupManager, NotificationRankingManager notificationRankingManager, KeyguardEnvironment keyguardEnvironment, FeatureFlags featureFlags, Lazy<NotificationRowBinder> lazy, Lazy<NotificationRemoteInputManager> lazy2, LeakDetector leakDetector, ForegroundServiceDismissalFeatureController foregroundServiceDismissalFeatureController) {
        ArraySet arraySet = new ArraySet();
        this.mAllNotifications = arraySet;
        this.mReadOnlyAllNotifications = Collections.unmodifiableSet(arraySet);
        ArrayList<NotificationEntry> arrayList = new ArrayList<>();
        this.mSortedAndFiltered = arrayList;
        this.mReadOnlyNotifications = Collections.unmodifiableList(arrayList);
        this.mRetainedNotifications = new ArrayMap();
        this.mNotifCollectionListeners = new ArrayList();
        this.mNotificationLifetimeExtenders = new ArrayList<>();
        this.mNotificationEntryListeners = new ArrayList();
        this.mRemoveInterceptors = new ArrayList();
        this.mNotifListener = new NotificationHandler() {
            public void onNotificationPosted(StatusBarNotification statusBarNotification, RankingMap rankingMap) {
                if (NotificationEntryManager.this.mActiveNotifications.containsKey(statusBarNotification.getKey())) {
                    NotificationEntryManager.this.updateNotification(statusBarNotification, rankingMap);
                } else {
                    NotificationEntryManager.this.addNotification(statusBarNotification, rankingMap);
                }
            }

            public void onNotificationRemoved(StatusBarNotification statusBarNotification, RankingMap rankingMap, int i) {
                NotificationEntryManager.this.removeNotification(statusBarNotification.getKey(), rankingMap, i);
            }

            public void onNotificationRankingUpdate(RankingMap rankingMap) {
                NotificationEntryManager.this.updateNotificationRanking(rankingMap);
            }
        };
        this.mLogger = notificationEntryManagerLogger;
        this.mGroupManager = notificationGroupManager;
        this.mRankingManager = notificationRankingManager;
        this.mKeyguardEnvironment = keyguardEnvironment;
        this.mFeatureFlags = featureFlags;
        this.mNotificationRowBinderLazy = lazy;
        this.mRemoteInputManagerLazy = lazy2;
        this.mLeakDetector = leakDetector;
        this.mFgsFeatureController = foregroundServiceDismissalFeatureController;
    }

    public void attach(NotificationListener notificationListener) {
        notificationListener.addNotificationHandler(this.mNotifListener);
    }

    public void addNotificationEntryListener(NotificationEntryListener notificationEntryListener) {
        this.mNotificationEntryListeners.add(notificationEntryListener);
    }

    public void removeNotificationEntryListener(NotificationEntryListener notificationEntryListener) {
        this.mNotificationEntryListeners.remove(notificationEntryListener);
    }

    public void addNotificationRemoveInterceptor(NotificationRemoveInterceptor notificationRemoveInterceptor) {
        this.mRemoveInterceptors.add(notificationRemoveInterceptor);
    }

    public void setUpWithPresenter(NotificationPresenter notificationPresenter) {
        this.mPresenter = notificationPresenter;
    }

    public void addNotificationLifetimeExtenders(List<NotificationLifetimeExtender> list) {
        for (NotificationLifetimeExtender addNotificationLifetimeExtender : list) {
            addNotificationLifetimeExtender(addNotificationLifetimeExtender);
        }
    }

    public void addNotificationLifetimeExtender(NotificationLifetimeExtender notificationLifetimeExtender) {
        this.mNotificationLifetimeExtenders.add(notificationLifetimeExtender);
        notificationLifetimeExtender.setCallback(new NotificationSafeToRemoveCallback() {
            public final void onSafeToRemove(String str) {
                NotificationEntryManager.this.mo14827x96dd259f(str);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addNotificationLifetimeExtender$0 */
    public /* synthetic */ void mo14827x96dd259f(String str) {
        removeNotification(str, this.mLatestRankingMap, 0);
    }

    public void onReorderingAllowed() {
        updateNotifications("reordering is now allowed");
    }

    public void performRemoveNotification(StatusBarNotification statusBarNotification, int i) {
        removeNotificationInternal(statusBarNotification.getKey(), null, obtainVisibility(statusBarNotification.getKey()), false, true, i);
    }

    private NotificationVisibility obtainVisibility(String str) {
        NotificationEntry notificationEntry = (NotificationEntry) this.mActiveNotifications.get(str);
        return NotificationVisibility.obtain(str, notificationEntry != null ? notificationEntry.getRanking().getRank() : 0, this.mActiveNotifications.size(), true, NotificationLogger.getNotificationLocation(getActiveNotificationUnfiltered(str)));
    }

    private void abortExistingInflation(String str, String str2) {
        if (this.mPendingNotifications.containsKey(str)) {
            NotificationEntry notificationEntry = (NotificationEntry) this.mPendingNotifications.get(str);
            notificationEntry.abortTask();
            this.mPendingNotifications.remove(str);
            for (NotifCollectionListener onEntryCleanUp : this.mNotifCollectionListeners) {
                onEntryCleanUp.onEntryCleanUp(notificationEntry);
            }
            this.mLogger.logInflationAborted(str, "pending", str2);
        }
        NotificationEntry activeNotificationUnfiltered = getActiveNotificationUnfiltered(str);
        if (activeNotificationUnfiltered != null) {
            activeNotificationUnfiltered.abortTask();
            this.mLogger.logInflationAborted(str, "active", str2);
        }
    }

    public void handleInflationException(NotificationEntry notificationEntry, Exception exc) {
        handleInflationException(notificationEntry.getSbn(), exc);
    }

    public void handleInflationException(StatusBarNotification statusBarNotification, Exception exc) {
        removeNotificationInternal(statusBarNotification.getKey(), null, null, true, false, 4);
        for (NotificationEntryListener onInflationError : this.mNotificationEntryListeners) {
            onInflationError.onInflationError(statusBarNotification, exc);
        }
    }

    public void onAsyncInflationFinished(NotificationEntry notificationEntry) {
        this.mPendingNotifications.remove(notificationEntry.getKey());
        if (!notificationEntry.isRowRemoved()) {
            boolean z = getActiveNotificationUnfiltered(notificationEntry.getKey()) == null;
            this.mLogger.logNotifInflated(notificationEntry.getKey(), z);
            if (z) {
                for (NotificationEntryListener onEntryInflated : this.mNotificationEntryListeners) {
                    onEntryInflated.onEntryInflated(notificationEntry);
                }
                addActiveNotification(notificationEntry);
                updateNotifications("onAsyncInflationFinished");
                for (NotificationEntryListener onNotificationAdded : this.mNotificationEntryListeners) {
                    onNotificationAdded.onNotificationAdded(notificationEntry);
                }
                return;
            }
            for (NotificationEntryListener onEntryReinflated : this.mNotificationEntryListeners) {
                onEntryReinflated.onEntryReinflated(notificationEntry);
            }
        }
    }

    private void addActiveNotification(NotificationEntry notificationEntry) {
        Assert.isMainThread();
        this.mActiveNotifications.put(notificationEntry.getKey(), notificationEntry);
        this.mGroupManager.onEntryAdded(notificationEntry);
        updateRankingAndSort(this.mRankingManager.getRankingMap(), "addEntryInternalInternal");
    }

    @VisibleForTesting
    public void addActiveNotificationForTest(NotificationEntry notificationEntry) {
        this.mActiveNotifications.put(notificationEntry.getKey(), notificationEntry);
        this.mGroupManager.onEntryAdded(notificationEntry);
        reapplyFilterAndSort("addVisibleNotification");
    }

    public void removeNotification(String str, RankingMap rankingMap, int i) {
        removeNotificationInternal(str, rankingMap, obtainVisibility(str), false, false, i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0067  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void removeNotificationInternal(java.lang.String r9, android.service.notification.NotificationListenerService.RankingMap r10, com.android.internal.statusbar.NotificationVisibility r11, boolean r12, boolean r13, int r14) {
        /*
            r8 = this;
            com.android.systemui.statusbar.notification.collection.NotificationEntry r0 = r8.getActiveNotificationUnfiltered(r9)
            java.util.List<com.android.systemui.statusbar.NotificationRemoveInterceptor> r1 = r8.mRemoveInterceptors
            java.util.Iterator r1 = r1.iterator()
        L_0x000a:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0022
            java.lang.Object r2 = r1.next()
            com.android.systemui.statusbar.NotificationRemoveInterceptor r2 = (com.android.systemui.statusbar.NotificationRemoveInterceptor) r2
            boolean r2 = r2.onNotificationRemoveRequested(r9, r0, r14)
            if (r2 == 0) goto L_0x000a
            com.android.systemui.statusbar.notification.NotificationEntryManagerLogger r8 = r8.mLogger
            r8.logRemovalIntercepted(r9)
            return
        L_0x0022:
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x005d
            java.util.HashMap<java.lang.String, com.android.systemui.statusbar.notification.collection.NotificationEntry> r3 = r8.mPendingNotifications
            java.lang.Object r3 = r3.get(r9)
            com.android.systemui.statusbar.notification.collection.NotificationEntry r3 = (com.android.systemui.statusbar.notification.collection.NotificationEntry) r3
            if (r3 == 0) goto L_0x005d
            java.util.ArrayList<com.android.systemui.statusbar.NotificationLifetimeExtender> r4 = r8.mNotificationLifetimeExtenders
            java.util.Iterator r4 = r4.iterator()
            r5 = r2
        L_0x0037:
            boolean r6 = r4.hasNext()
            if (r6 == 0) goto L_0x005e
            java.lang.Object r6 = r4.next()
            com.android.systemui.statusbar.NotificationLifetimeExtender r6 = (com.android.systemui.statusbar.NotificationLifetimeExtender) r6
            boolean r7 = r6.shouldExtendLifetimeForPendingNotification(r3)
            if (r7 == 0) goto L_0x0037
            r8.extendLifetime(r3, r6)
            com.android.systemui.statusbar.notification.NotificationEntryManagerLogger r5 = r8.mLogger
            java.lang.Class r6 = r6.getClass()
            java.lang.String r6 = r6.getName()
            java.lang.String r7 = "pending"
            r5.logLifetimeExtended(r9, r6, r7)
            r5 = r1
            goto L_0x0037
        L_0x005d:
            r5 = r2
        L_0x005e:
            if (r5 != 0) goto L_0x0065
            java.lang.String r3 = "removeNotification"
            r8.abortExistingInflation(r9, r3)
        L_0x0065:
            if (r0 == 0) goto L_0x010d
            boolean r3 = r0.isRowDismissed()
            if (r12 != 0) goto L_0x009c
            if (r3 != 0) goto L_0x009c
            java.util.ArrayList<com.android.systemui.statusbar.NotificationLifetimeExtender> r12 = r8.mNotificationLifetimeExtenders
            java.util.Iterator r12 = r12.iterator()
        L_0x0075:
            boolean r4 = r12.hasNext()
            if (r4 == 0) goto L_0x009c
            java.lang.Object r4 = r12.next()
            com.android.systemui.statusbar.NotificationLifetimeExtender r4 = (com.android.systemui.statusbar.NotificationLifetimeExtender) r4
            boolean r6 = r4.shouldExtendLifetime(r0)
            if (r6 == 0) goto L_0x0075
            r8.mLatestRankingMap = r10
            r8.extendLifetime(r0, r4)
            com.android.systemui.statusbar.notification.NotificationEntryManagerLogger r10 = r8.mLogger
            java.lang.Class r12 = r4.getClass()
            java.lang.String r12 = r12.getName()
            java.lang.String r4 = "active"
            r10.logLifetimeExtended(r9, r12, r4)
            goto L_0x009d
        L_0x009c:
            r1 = r5
        L_0x009d:
            if (r1 != 0) goto L_0x010d
            r8.cancelLifetimeExtension(r0)
            boolean r10 = r0.rowExists()
            if (r10 == 0) goto L_0x00ab
            r0.removeRow()
        L_0x00ab:
            java.util.Set<com.android.systemui.statusbar.notification.collection.NotificationEntry> r10 = r8.mAllNotifications
            r10.remove(r0)
            r8.handleGroupSummaryRemoved(r9)
            r8.removeVisibleNotification(r9)
            java.lang.String r9 = "removeNotificationInternal"
            r8.updateNotifications(r9)
            com.android.systemui.util.leak.LeakDetector r9 = r8.mLeakDetector
            r9.trackGarbage(r0)
            r9 = r13 | r3
            com.android.systemui.statusbar.notification.NotificationEntryManagerLogger r10 = r8.mLogger
            java.lang.String r12 = r0.getKey()
            r10.logNotifRemoved(r12, r9)
            java.util.List<com.android.systemui.statusbar.notification.NotificationEntryListener> r10 = r8.mNotificationEntryListeners
            java.util.Iterator r10 = r10.iterator()
        L_0x00d1:
            boolean r12 = r10.hasNext()
            if (r12 == 0) goto L_0x00e1
            java.lang.Object r12 = r10.next()
            com.android.systemui.statusbar.notification.NotificationEntryListener r12 = (com.android.systemui.statusbar.notification.NotificationEntryListener) r12
            r12.onEntryRemoved(r0, r11, r9, r14)
            goto L_0x00d1
        L_0x00e1:
            java.util.List<com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener> r9 = r8.mNotifCollectionListeners
            java.util.Iterator r9 = r9.iterator()
        L_0x00e7:
            boolean r10 = r9.hasNext()
            if (r10 == 0) goto L_0x00f7
            java.lang.Object r10 = r9.next()
            com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener r10 = (com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener) r10
            r10.onEntryRemoved(r0, r2)
            goto L_0x00e7
        L_0x00f7:
            java.util.List<com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener> r8 = r8.mNotifCollectionListeners
            java.util.Iterator r8 = r8.iterator()
        L_0x00fd:
            boolean r9 = r8.hasNext()
            if (r9 == 0) goto L_0x010d
            java.lang.Object r9 = r8.next()
            com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener r9 = (com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener) r9
            r9.onEntryCleanUp(r0)
            goto L_0x00fd
        L_0x010d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.NotificationEntryManager.removeNotificationInternal(java.lang.String, android.service.notification.NotificationListenerService$RankingMap, com.android.internal.statusbar.NotificationVisibility, boolean, boolean, int):void");
    }

    private void handleGroupSummaryRemoved(String str) {
        NotificationEntry activeNotificationUnfiltered = getActiveNotificationUnfiltered(str);
        if (activeNotificationUnfiltered != null && activeNotificationUnfiltered.rowExists() && activeNotificationUnfiltered.isSummaryWithChildren() && (activeNotificationUnfiltered.getSbn().getOverrideGroupKey() == null || activeNotificationUnfiltered.isRowDismissed())) {
            List children = activeNotificationUnfiltered.getChildren();
            if (children != null) {
                for (int i = 0; i < children.size(); i++) {
                    NotificationEntry notificationEntry = (NotificationEntry) children.get(i);
                    boolean z = (activeNotificationUnfiltered.getSbn().getNotification().flags & 64) != 0;
                    boolean z2 = ((NotificationRemoteInputManager) this.mRemoteInputManagerLazy.get()).shouldKeepForRemoteInputHistory(notificationEntry) || ((NotificationRemoteInputManager) this.mRemoteInputManagerLazy.get()).shouldKeepForSmartReplyHistory(notificationEntry);
                    if (!z && !z2) {
                        notificationEntry.setKeepInParent(true);
                        notificationEntry.removeRow();
                    }
                }
            }
        }
    }

    private void addNotificationInternal(StatusBarNotification statusBarNotification, RankingMap rankingMap) throws InflationException {
        String key = statusBarNotification.getKey();
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("addNotification key=");
            sb.append(key);
            Log.d("NotificationEntryMgr", sb.toString());
        }
        updateRankingAndSort(rankingMap, "addNotificationInternal");
        Ranking ranking = new Ranking();
        rankingMap.getRanking(key, ranking);
        NotificationEntry notificationEntry = new NotificationEntry(statusBarNotification, ranking, this.mFgsFeatureController.isForegroundServiceDismissalEnabled());
        this.mAllNotifications.add(notificationEntry);
        this.mLeakDetector.trackInstance(notificationEntry);
        for (NotifCollectionListener onEntryInit : this.mNotifCollectionListeners) {
            onEntryInit.onEntryInit(notificationEntry);
        }
        if (!this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            ((NotificationRowBinder) this.mNotificationRowBinderLazy.get()).inflateViews(notificationEntry, new Runnable(statusBarNotification) {
                public final /* synthetic */ StatusBarNotification f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationEntryManager.this.lambda$addNotificationInternal$1$NotificationEntryManager(this.f$1);
                }
            });
        }
        abortExistingInflation(key, "addNotification");
        this.mPendingNotifications.put(key, notificationEntry);
        this.mLogger.logNotifAdded(notificationEntry.getKey());
        for (NotificationEntryListener onPendingEntryAdded : this.mNotificationEntryListeners) {
            onPendingEntryAdded.onPendingEntryAdded(notificationEntry);
        }
        for (NotifCollectionListener onEntryAdded : this.mNotifCollectionListeners) {
            onEntryAdded.onEntryAdded(notificationEntry);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addNotificationInternal$1 */
    public /* synthetic */ void lambda$addNotificationInternal$1$NotificationEntryManager(StatusBarNotification statusBarNotification) {
        performRemoveNotification(statusBarNotification, 2);
    }

    public void addNotification(StatusBarNotification statusBarNotification, RankingMap rankingMap) {
        try {
            addNotificationInternal(statusBarNotification, rankingMap);
        } catch (InflationException e) {
            handleInflationException(statusBarNotification, (Exception) e);
        }
    }

    private void updateNotificationInternal(StatusBarNotification statusBarNotification, RankingMap rankingMap) throws InflationException {
        String str = "NotificationEntryMgr";
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateNotification(");
            sb.append(statusBarNotification);
            sb.append(")");
            Log.d(str, sb.toString());
        }
        String key = statusBarNotification.getKey();
        abortExistingInflation(key, "updateNotification");
        NotificationEntry activeNotificationUnfiltered = getActiveNotificationUnfiltered(key);
        if (activeNotificationUnfiltered != null) {
            cancelLifetimeExtension(activeNotificationUnfiltered);
            String str2 = "updateNotificationInternal";
            updateRankingAndSort(rankingMap, str2);
            StatusBarNotification sbn = activeNotificationUnfiltered.getSbn();
            activeNotificationUnfiltered.setSbn(statusBarNotification);
            this.mGroupManager.onEntryUpdated(activeNotificationUnfiltered, sbn);
            this.mLogger.logNotifUpdated(activeNotificationUnfiltered.getKey());
            for (NotificationEntryListener onPreEntryUpdated : this.mNotificationEntryListeners) {
                onPreEntryUpdated.onPreEntryUpdated(activeNotificationUnfiltered);
            }
            for (NotifCollectionListener onEntryUpdated : this.mNotifCollectionListeners) {
                onEntryUpdated.onEntryUpdated(activeNotificationUnfiltered);
            }
            if (!this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
                ((NotificationRowBinder) this.mNotificationRowBinderLazy.get()).inflateViews(activeNotificationUnfiltered, new Runnable(statusBarNotification) {
                    public final /* synthetic */ StatusBarNotification f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        NotificationEntryManager.this.lambda$updateNotificationInternal$2$NotificationEntryManager(this.f$1);
                    }
                });
            }
            updateNotifications(str2);
            if (DEBUG) {
                boolean isNotificationForCurrentProfiles = this.mKeyguardEnvironment.isNotificationForCurrentProfiles(statusBarNotification);
                StringBuilder sb2 = new StringBuilder();
                sb2.append("notification is ");
                sb2.append(isNotificationForCurrentProfiles ? "" : "not ");
                sb2.append("for you");
                Log.d(str, sb2.toString());
            }
            for (NotificationEntryListener onPostEntryUpdated : this.mNotificationEntryListeners) {
                onPostEntryUpdated.onPostEntryUpdated(activeNotificationUnfiltered);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateNotificationInternal$2 */
    public /* synthetic */ void lambda$updateNotificationInternal$2$NotificationEntryManager(StatusBarNotification statusBarNotification) {
        performRemoveNotification(statusBarNotification, 2);
    }

    public void updateNotification(StatusBarNotification statusBarNotification, RankingMap rankingMap) {
        try {
            updateNotificationInternal(statusBarNotification, rankingMap);
        } catch (InflationException e) {
            handleInflationException(statusBarNotification, (Exception) e);
        }
    }

    public void updateNotifications(String str) {
        reapplyFilterAndSort(str);
        NotificationPresenter notificationPresenter = this.mPresenter;
        if (notificationPresenter != null) {
            notificationPresenter.updateNotificationViews();
        }
    }

    public void updateNotificationRanking(RankingMap rankingMap) {
        ArrayList<NotificationEntry> arrayList = new ArrayList<>();
        arrayList.addAll(getVisibleNotifications());
        arrayList.addAll(this.mPendingNotifications.values());
        ArrayMap arrayMap = new ArrayMap();
        ArrayMap arrayMap2 = new ArrayMap();
        for (NotificationEntry notificationEntry : arrayList) {
            arrayMap.put(notificationEntry.getKey(), NotificationUiAdjustment.extractFromNotificationEntry(notificationEntry));
            arrayMap2.put(notificationEntry.getKey(), Integer.valueOf(notificationEntry.getImportance()));
        }
        String str = "updateNotificationRanking";
        updateRankingAndSort(rankingMap, str);
        updateRankingOfPendingNotifications(rankingMap);
        for (NotificationEntry notificationEntry2 : arrayList) {
            ((NotificationRowBinder) this.mNotificationRowBinderLazy.get()).onNotificationRankingUpdated(notificationEntry2, (Integer) arrayMap2.get(notificationEntry2.getKey()), (NotificationUiAdjustment) arrayMap.get(notificationEntry2.getKey()), NotificationUiAdjustment.extractFromNotificationEntry(notificationEntry2));
        }
        updateNotifications(str);
        for (NotificationEntryListener onNotificationRankingUpdated : this.mNotificationEntryListeners) {
            onNotificationRankingUpdated.onNotificationRankingUpdated(rankingMap);
        }
        for (NotifCollectionListener onRankingUpdate : this.mNotifCollectionListeners) {
            onRankingUpdate.onRankingUpdate(rankingMap);
        }
    }

    private void updateRankingOfPendingNotifications(RankingMap rankingMap) {
        if (rankingMap != null) {
            for (NotificationEntry notificationEntry : this.mPendingNotifications.values()) {
                Ranking ranking = new Ranking();
                if (rankingMap.getRanking(notificationEntry.getKey(), ranking)) {
                    notificationEntry.setRanking(ranking);
                }
            }
        }
    }

    public Iterable<NotificationEntry> getPendingNotificationsIterator() {
        return this.mPendingNotifications.values();
    }

    public NotificationEntry getActiveNotificationUnfiltered(String str) {
        return (NotificationEntry) this.mActiveNotifications.get(str);
    }

    public NotificationEntry getPendingOrActiveNotif(String str) {
        if (this.mPendingNotifications.containsKey(str)) {
            return (NotificationEntry) this.mPendingNotifications.get(str);
        }
        return (NotificationEntry) this.mActiveNotifications.get(str);
    }

    private void extendLifetime(NotificationEntry notificationEntry, NotificationLifetimeExtender notificationLifetimeExtender) {
        NotificationLifetimeExtender notificationLifetimeExtender2 = (NotificationLifetimeExtender) this.mRetainedNotifications.get(notificationEntry);
        if (!(notificationLifetimeExtender2 == null || notificationLifetimeExtender2 == notificationLifetimeExtender)) {
            notificationLifetimeExtender2.setShouldManageLifetime(notificationEntry, false);
        }
        this.mRetainedNotifications.put(notificationEntry, notificationLifetimeExtender);
        notificationLifetimeExtender.setShouldManageLifetime(notificationEntry, true);
    }

    private void cancelLifetimeExtension(NotificationEntry notificationEntry) {
        NotificationLifetimeExtender notificationLifetimeExtender = (NotificationLifetimeExtender) this.mRetainedNotifications.remove(notificationEntry);
        if (notificationLifetimeExtender != null) {
            notificationLifetimeExtender.setShouldManageLifetime(notificationEntry, false);
        }
    }

    private void removeVisibleNotification(String str) {
        Assert.isMainThread();
        NotificationEntry notificationEntry = (NotificationEntry) this.mActiveNotifications.remove(str);
        if (notificationEntry != null) {
            this.mGroupManager.onEntryRemoved(notificationEntry);
        }
    }

    public List<NotificationEntry> getActiveNotificationsForCurrentUser() {
        Assert.isMainThread();
        ArrayList arrayList = new ArrayList();
        int size = this.mActiveNotifications.size();
        for (int i = 0; i < size; i++) {
            NotificationEntry notificationEntry = (NotificationEntry) this.mActiveNotifications.valueAt(i);
            if (this.mKeyguardEnvironment.isNotificationForCurrentProfiles(notificationEntry.getSbn())) {
                arrayList.add(notificationEntry);
            }
        }
        return arrayList;
    }

    public void reapplyFilterAndSort(String str) {
        updateRankingAndSort(this.mRankingManager.getRankingMap(), str);
    }

    private void updateRankingAndSort(RankingMap rankingMap, String str) {
        this.mSortedAndFiltered.clear();
        this.mSortedAndFiltered.addAll(this.mRankingManager.updateRanking(rankingMap, this.mActiveNotifications.values(), str));
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println("NotificationEntryManager");
        int size = this.mSortedAndFiltered.size();
        printWriter.print(str);
        StringBuilder sb = new StringBuilder();
        sb.append("active notifications: ");
        sb.append(size);
        printWriter.println(sb.toString());
        int i = 0;
        while (i < size) {
            dumpEntry(printWriter, str, i, (NotificationEntry) this.mSortedAndFiltered.get(i));
            i++;
        }
        synchronized (this.mActiveNotifications) {
            int size2 = this.mActiveNotifications.size();
            printWriter.print(str);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("inactive notifications: ");
            sb2.append(size2 - i);
            printWriter.println(sb2.toString());
            int i2 = 0;
            for (int i3 = 0; i3 < size2; i3++) {
                NotificationEntry notificationEntry = (NotificationEntry) this.mActiveNotifications.valueAt(i3);
                if (!this.mSortedAndFiltered.contains(notificationEntry)) {
                    dumpEntry(printWriter, str, i2, notificationEntry);
                    i2++;
                }
            }
        }
    }

    private void dumpEntry(PrintWriter printWriter, String str, int i, NotificationEntry notificationEntry) {
        printWriter.print(str);
        StringBuilder sb = new StringBuilder();
        sb.append("  [");
        sb.append(i);
        sb.append("] key=");
        sb.append(notificationEntry.getKey());
        sb.append(" icon=");
        sb.append(notificationEntry.getIcons().getStatusBarIcon());
        printWriter.println(sb.toString());
        StatusBarNotification sbn = notificationEntry.getSbn();
        printWriter.print(str);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("      pkg=");
        sb2.append(sbn.getPackageName());
        sb2.append(" id=");
        sb2.append(sbn.getId());
        sb2.append(" importance=");
        sb2.append(notificationEntry.getRanking().getImportance());
        printWriter.println(sb2.toString());
        printWriter.print(str);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("      notification=");
        sb3.append(sbn.getNotification());
        printWriter.println(sb3.toString());
    }

    public List<NotificationEntry> getVisibleNotifications() {
        return this.mReadOnlyNotifications;
    }

    public Collection<NotificationEntry> getAllNotifs() {
        return this.mReadOnlyAllNotifications;
    }

    public int getActiveNotificationsCount() {
        return this.mReadOnlyNotifications.size();
    }

    public boolean hasActiveNotifications() {
        return this.mReadOnlyNotifications.size() != 0;
    }

    public void addCollectionListener(NotifCollectionListener notifCollectionListener) {
        this.mNotifCollectionListeners.add(notifCollectionListener);
    }
}
