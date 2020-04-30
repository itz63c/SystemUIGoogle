package com.android.systemui.util;

import android.graphics.Rect;
import kotlin.Lazy;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import kotlin.reflect.KProperty;

/* renamed from: com.android.systemui.util.FloatingContentCoordinator$Companion$findAreaForContentVertically$positionAboveInBounds$2 */
/* compiled from: FloatingContentCoordinator.kt */
final class C1733x994e5850 extends Lambda implements Function0<Boolean> {
    final /* synthetic */ Rect $allowedBounds;
    final /* synthetic */ Lazy $newContentBoundsAbove;
    final /* synthetic */ KProperty $newContentBoundsAbove$metadata;

    C1733x994e5850(Rect rect, Lazy lazy, KProperty kProperty) {
        this.$allowedBounds = rect;
        this.$newContentBoundsAbove = lazy;
        this.$newContentBoundsAbove$metadata = kProperty;
        super(0);
    }

    public final boolean invoke() {
        return this.$allowedBounds.contains((Rect) this.$newContentBoundsAbove.getValue());
    }
}
