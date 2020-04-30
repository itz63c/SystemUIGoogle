package com.android.systemui.util;

import android.graphics.Rect;
import kotlin.Lazy;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import kotlin.reflect.KProperty;

/* renamed from: com.android.systemui.util.FloatingContentCoordinator$Companion$findAreaForContentVertically$positionBelowInBounds$2 */
/* compiled from: FloatingContentCoordinator.kt */
final class C1734xf7317e4 extends Lambda implements Function0<Boolean> {
    final /* synthetic */ Rect $allowedBounds;
    final /* synthetic */ Lazy $newContentBoundsBelow;
    final /* synthetic */ KProperty $newContentBoundsBelow$metadata;

    C1734xf7317e4(Rect rect, Lazy lazy, KProperty kProperty) {
        this.$allowedBounds = rect;
        this.$newContentBoundsBelow = lazy;
        this.$newContentBoundsBelow$metadata = kProperty;
        super(0);
    }

    public final boolean invoke() {
        return this.$allowedBounds.contains((Rect) this.$newContentBoundsBelow.getValue());
    }
}
