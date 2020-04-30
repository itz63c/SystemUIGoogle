package com.android.systemui.util;

import android.graphics.Rect;
import java.util.Comparator;

/* renamed from: com.android.systemui.util.FloatingContentCoordinator$Companion$findAreaForContentAboveOrBelow$$inlined$sortedBy$1 */
/* compiled from: Comparisons.kt */
public final class C1730x8b489ee0<T> implements Comparator<T> {
    final /* synthetic */ boolean $findAbove$inlined;

    public C1730x8b489ee0(boolean z) {
        this.$findAbove$inlined = z;
    }

    public final int compare(T t, T t2) {
        Rect rect = (Rect) t;
        boolean z = this.$findAbove$inlined;
        int i = rect.top;
        if (z) {
            i = -i;
        }
        Rect rect2 = (Rect) t2;
        return ComparisonsKt__ComparisonsKt.compareValues(Integer.valueOf(i), Integer.valueOf(this.$findAbove$inlined ? -rect2.top : rect2.top));
    }
}
