package com.android.systemui.statusbar.notification.collection;

import android.app.Notification;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.notification.NotificationEntryManagerLogger;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import kotlin.Lazy;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import kotlin.sequences.Sequence;

/* compiled from: NotificationRankingManager.kt */
public class NotificationRankingManager {
    static final /* synthetic */ KProperty[] $$delegatedProperties;
    private final NotificationGroupManager groupManager;
    /* access modifiers changed from: private */
    public final HeadsUpManager headsUpManager;
    private final HighPriorityProvider highPriorityProvider;
    private final NotificationEntryManagerLogger logger;
    private final Lazy mediaManager$delegate = LazyKt__LazyJVMKt.lazy(new NotificationRankingManager$mediaManager$2(this));
    /* access modifiers changed from: private */
    public final dagger.Lazy<NotificationMediaManager> mediaManagerLazy;
    /* access modifiers changed from: private */
    public final NotificationFilter notifFilter;
    private final PeopleNotificationIdentifier peopleNotificationIdentifier;
    private final Comparator<NotificationEntry> rankingComparator;
    private RankingMap rankingMap;
    /* access modifiers changed from: private */
    public final boolean usePeopleFiltering;

    static {
        PropertyReference1Impl propertyReference1Impl = new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(NotificationRankingManager.class), "mediaManager", "getMediaManager()Lcom/android/systemui/statusbar/NotificationMediaManager;");
        Reflection.property1(propertyReference1Impl);
        $$delegatedProperties = new KProperty[]{propertyReference1Impl};
    }

    private final NotificationMediaManager getMediaManager() {
        Lazy lazy = this.mediaManager$delegate;
        KProperty kProperty = $$delegatedProperties[0];
        return (NotificationMediaManager) lazy.getValue();
    }

    public NotificationRankingManager(dagger.Lazy<NotificationMediaManager> lazy, NotificationGroupManager notificationGroupManager, HeadsUpManager headsUpManager2, NotificationFilter notificationFilter, NotificationEntryManagerLogger notificationEntryManagerLogger, NotificationSectionsFeatureManager notificationSectionsFeatureManager, PeopleNotificationIdentifier peopleNotificationIdentifier2, HighPriorityProvider highPriorityProvider2) {
        Intrinsics.checkParameterIsNotNull(lazy, "mediaManagerLazy");
        Intrinsics.checkParameterIsNotNull(notificationGroupManager, "groupManager");
        Intrinsics.checkParameterIsNotNull(headsUpManager2, "headsUpManager");
        Intrinsics.checkParameterIsNotNull(notificationFilter, "notifFilter");
        Intrinsics.checkParameterIsNotNull(notificationEntryManagerLogger, "logger");
        Intrinsics.checkParameterIsNotNull(notificationSectionsFeatureManager, "sectionsFeatureManager");
        Intrinsics.checkParameterIsNotNull(peopleNotificationIdentifier2, "peopleNotificationIdentifier");
        Intrinsics.checkParameterIsNotNull(highPriorityProvider2, "highPriorityProvider");
        this.mediaManagerLazy = lazy;
        this.groupManager = notificationGroupManager;
        this.headsUpManager = headsUpManager2;
        this.notifFilter = notificationFilter;
        this.logger = notificationEntryManagerLogger;
        this.peopleNotificationIdentifier = peopleNotificationIdentifier2;
        this.highPriorityProvider = highPriorityProvider2;
        this.usePeopleFiltering = notificationSectionsFeatureManager.isFilteringEnabled();
        this.rankingComparator = new NotificationRankingManager$rankingComparator$1(this);
    }

    public final RankingMap getRankingMap() {
        return this.rankingMap;
    }

    /* access modifiers changed from: private */
    public final boolean isImportantMedia(NotificationEntry notificationEntry) {
        Ranking ranking = notificationEntry.getRanking();
        Intrinsics.checkExpressionValueIsNotNull(ranking, "entry.ranking");
        int importance = ranking.getImportance();
        String key = notificationEntry.getKey();
        NotificationMediaManager mediaManager = getMediaManager();
        Intrinsics.checkExpressionValueIsNotNull(mediaManager, "mediaManager");
        return Intrinsics.areEqual((Object) key, (Object) mediaManager.getMediaNotificationKey()) && importance > 1;
    }

    public final List<NotificationEntry> updateRanking(RankingMap rankingMap2, Collection<NotificationEntry> collection, String str) {
        Sequence filterAndSortLocked;
        Intrinsics.checkParameterIsNotNull(collection, "entries");
        Intrinsics.checkParameterIsNotNull(str, "reason");
        Sequence asSequence = CollectionsKt___CollectionsKt.asSequence(collection);
        if (rankingMap2 != null) {
            this.rankingMap = rankingMap2;
            updateRankingForEntries(asSequence);
        }
        synchronized (this) {
            filterAndSortLocked = filterAndSortLocked(asSequence, str);
            Unit unit = Unit.INSTANCE;
        }
        return SequencesKt___SequencesKt.toList(filterAndSortLocked);
    }

    private final Sequence<NotificationEntry> filterAndSortLocked(Sequence<NotificationEntry> sequence, String str) {
        this.logger.logFilterAndSort(str);
        return SequencesKt___SequencesKt.map(SequencesKt___SequencesKt.sortedWith(SequencesKt___SequencesKt.filter(sequence, new NotificationRankingManager$filterAndSortLocked$1(this)), this.rankingComparator), new NotificationRankingManager$filterAndSortLocked$2(this));
    }

    /* access modifiers changed from: private */
    public final void assignBucketForEntry(NotificationEntry notificationEntry) {
        setBucket(notificationEntry, notificationEntry.isRowHeadsUp(), isImportantMedia(notificationEntry), NotificationRankingManagerKt.isSystemMax(notificationEntry));
    }

    private final void setBucket(NotificationEntry notificationEntry, boolean z, boolean z2, boolean z3) {
        if (this.usePeopleFiltering && z) {
            notificationEntry.setBucket(0);
        } else if (this.usePeopleFiltering && getPeopleNotificationType(notificationEntry) != 0) {
            notificationEntry.setBucket(1);
        } else if (z || z2 || z3 || isHighPriority(notificationEntry)) {
            notificationEntry.setBucket(2);
        } else {
            notificationEntry.setBucket(3);
        }
    }

    private final void updateRankingForEntries(Sequence<NotificationEntry> sequence) {
        RankingMap rankingMap2 = this.rankingMap;
        if (rankingMap2 != null) {
            synchronized (sequence) {
                for (NotificationEntry notificationEntry : sequence) {
                    Ranking ranking = new Ranking();
                    if (rankingMap2.getRanking(notificationEntry.getKey(), ranking)) {
                        notificationEntry.setRanking(ranking);
                        String overrideGroupKey = ranking.getOverrideGroupKey();
                        StatusBarNotification sbn = notificationEntry.getSbn();
                        Intrinsics.checkExpressionValueIsNotNull(sbn, "entry.sbn");
                        if (!Objects.equals(sbn.getOverrideGroupKey(), overrideGroupKey)) {
                            StatusBarNotification sbn2 = notificationEntry.getSbn();
                            Intrinsics.checkExpressionValueIsNotNull(sbn2, "entry.sbn");
                            String groupKey = sbn2.getGroupKey();
                            StatusBarNotification sbn3 = notificationEntry.getSbn();
                            Intrinsics.checkExpressionValueIsNotNull(sbn3, "entry.sbn");
                            boolean isGroup = sbn3.isGroup();
                            StatusBarNotification sbn4 = notificationEntry.getSbn();
                            Intrinsics.checkExpressionValueIsNotNull(sbn4, "entry.sbn");
                            Notification notification = sbn4.getNotification();
                            Intrinsics.checkExpressionValueIsNotNull(notification, "entry.sbn.notification");
                            boolean isGroupSummary = notification.isGroupSummary();
                            StatusBarNotification sbn5 = notificationEntry.getSbn();
                            Intrinsics.checkExpressionValueIsNotNull(sbn5, "entry.sbn");
                            sbn5.setOverrideGroupKey(overrideGroupKey);
                            this.groupManager.onEntryUpdated(notificationEntry, groupKey, isGroup, isGroupSummary);
                        }
                    }
                }
                Unit unit = Unit.INSTANCE;
            }
        }
    }

    /* access modifiers changed from: private */
    public final int getPeopleNotificationType(NotificationEntry notificationEntry) {
        PeopleNotificationIdentifier peopleNotificationIdentifier2 = this.peopleNotificationIdentifier;
        StatusBarNotification sbn = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn, "sbn");
        Ranking ranking = notificationEntry.getRanking();
        Intrinsics.checkExpressionValueIsNotNull(ranking, "ranking");
        return peopleNotificationIdentifier2.getPeopleNotificationType(sbn, ranking);
    }

    /* access modifiers changed from: private */
    public final boolean isHighPriority(NotificationEntry notificationEntry) {
        return this.highPriorityProvider.isHighPriority(notificationEntry);
    }
}
