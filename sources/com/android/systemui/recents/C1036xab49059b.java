package com.android.systemui.recents;

import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Function;

/* renamed from: com.android.systemui.recents.-$$Lambda$ScreenPinningRequest$RequestWindowView$iq7_kF2IL9FTwkRZM6zjXuxpxgs */
/* compiled from: lambda */
public final /* synthetic */ class C1036xab49059b implements Function {
    public static final /* synthetic */ C1036xab49059b INSTANCE = new C1036xab49059b();

    private /* synthetic */ C1036xab49059b() {
    }

    public final Object apply(Object obj) {
        return ((StatusBar) ((Lazy) obj).get()).getNavigationBarView();
    }
}
