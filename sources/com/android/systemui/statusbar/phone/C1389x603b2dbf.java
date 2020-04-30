package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Function;

/* renamed from: com.android.systemui.statusbar.phone.-$$Lambda$NotificationIconAreaController$S6CJ2tXrA2ieNVmUpwBa8v9eeEY */
/* compiled from: lambda */
public final /* synthetic */ class C1389x603b2dbf implements Function {
    public static final /* synthetic */ C1389x603b2dbf INSTANCE = new C1389x603b2dbf();

    private /* synthetic */ C1389x603b2dbf() {
    }

    public final Object apply(Object obj) {
        return ((NotificationEntry) obj).getIcons().getCenteredIcon();
    }
}
