package com.android.systemui.statusbar.notification.stack;

import android.service.notification.NotificationListenerService.Ranking;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Comparator;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Comparisons.kt */
public final class ForegroundServiceSectionController$$special$$inlined$sortedBy$1<T> implements Comparator<T> {
    public final int compare(T t, T t2) {
        Ranking ranking = ((NotificationEntry) t).getRanking();
        String str = "it.ranking";
        Intrinsics.checkExpressionValueIsNotNull(ranking, str);
        Integer valueOf = Integer.valueOf(ranking.getRank());
        Ranking ranking2 = ((NotificationEntry) t2).getRanking();
        Intrinsics.checkExpressionValueIsNotNull(ranking2, str);
        return ComparisonsKt__ComparisonsKt.compareValues(valueOf, Integer.valueOf(ranking2.getRank()));
    }
}
