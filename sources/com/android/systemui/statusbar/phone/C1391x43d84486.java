package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Function;

/* renamed from: com.android.systemui.statusbar.phone.-$$Lambda$NotificationIconAreaController$b7MkWJaTAeTosmR_aU3q7JZNLpI */
/* compiled from: lambda */
public final /* synthetic */ class C1391x43d84486 implements Function {
    public static final /* synthetic */ C1391x43d84486 INSTANCE = new C1391x43d84486();

    private /* synthetic */ C1391x43d84486() {
    }

    public final Object apply(Object obj) {
        return ((NotificationEntry) obj).getIcons().getAodIcon();
    }
}
