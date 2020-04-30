package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Function;

/* renamed from: com.android.systemui.statusbar.phone.-$$Lambda$NotificationIconAreaController$ujxUr-qwlryo8PHBzga56kRshsA */
/* compiled from: lambda */
public final /* synthetic */ class C1393x339b1a97 implements Function {
    public static final /* synthetic */ C1393x339b1a97 INSTANCE = new C1393x339b1a97();

    private /* synthetic */ C1393x339b1a97() {
    }

    public final Object apply(Object obj) {
        return ((NotificationEntry) obj).getIcons().getStatusBarIcon();
    }
}
