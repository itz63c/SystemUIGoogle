package com.android.systemui.statusbar.notification.collection.notifcollection;

import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotifEvent.kt */
public final class RankingAppliedEvent extends NotifEvent {
    public RankingAppliedEvent() {
        super(null);
    }

    public void dispatchToListener(NotifCollectionListener notifCollectionListener) {
        Intrinsics.checkParameterIsNotNull(notifCollectionListener, "listener");
        notifCollectionListener.onRankingApplied();
    }
}
