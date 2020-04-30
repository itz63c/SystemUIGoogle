package com.android.systemui.statusbar.notification.collection;

import com.android.systemui.statusbar.notification.collection.ShadeListBuilder.OnRenderListListener;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotifViewManager.kt */
final class NotifViewManager$attach$1 implements OnRenderListListener {
    final /* synthetic */ NotifViewManager this$0;

    NotifViewManager$attach$1(NotifViewManager notifViewManager) {
        this.this$0 = notifViewManager;
    }

    public final void onRenderList(List<? extends ListEntry> list) {
        Intrinsics.checkParameterIsNotNull(list, "entries");
        this.this$0.onNotifTreeBuilt(list);
    }
}
