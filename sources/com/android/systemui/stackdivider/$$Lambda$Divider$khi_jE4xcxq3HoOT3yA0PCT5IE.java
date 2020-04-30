package com.android.systemui.stackdivider;

import com.android.systemui.recents.Recents;
import dagger.Lazy;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.stackdivider.-$$Lambda$Divider$khi_jE4xcxq3HoOT3yA0PC-T5IE reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Divider$khi_jE4xcxq3HoOT3yA0PCT5IE implements Consumer {
    public static final /* synthetic */ $$Lambda$Divider$khi_jE4xcxq3HoOT3yA0PCT5IE INSTANCE = new $$Lambda$Divider$khi_jE4xcxq3HoOT3yA0PCT5IE();

    private /* synthetic */ $$Lambda$Divider$khi_jE4xcxq3HoOT3yA0PCT5IE() {
    }

    public final void accept(Object obj) {
        ((Recents) ((Lazy) obj).get()).growRecents();
    }
}
