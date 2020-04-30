package com.android.systemui.statusbar;

import com.android.systemui.statusbar.SysuiStatusBarStateController.RankedListener;
import java.util.function.ToIntFunction;

/* renamed from: com.android.systemui.statusbar.-$$Lambda$StatusBarStateControllerImpl$7y8VOe44iFeEd9HPscwVVB7kUfw */
/* compiled from: lambda */
public final /* synthetic */ class C1111xf20ff57f implements ToIntFunction {
    public static final /* synthetic */ C1111xf20ff57f INSTANCE = new C1111xf20ff57f();

    private /* synthetic */ C1111xf20ff57f() {
    }

    public final int applyAsInt(Object obj) {
        return ((RankedListener) obj).mRank;
    }
}
