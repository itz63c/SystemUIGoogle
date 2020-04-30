package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Function;

/* renamed from: com.android.systemui.statusbar.phone.-$$Lambda$NotificationIconAreaController$afpYK1wAP1i0HTFHOa1jb1wzzAQ */
/* compiled from: lambda */
public final /* synthetic */ class C1390xeccd2fb2 implements Function {
    public static final /* synthetic */ C1390xeccd2fb2 INSTANCE = new C1390xeccd2fb2();

    private /* synthetic */ C1390xeccd2fb2() {
    }

    public final Object apply(Object obj) {
        return ((NotificationEntry) obj).getIcons().getShelfIcon();
    }
}
