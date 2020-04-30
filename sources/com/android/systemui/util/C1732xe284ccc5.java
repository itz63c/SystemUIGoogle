package com.android.systemui.util;

import android.graphics.Rect;
import java.util.Collection;
import java.util.List;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import kotlin.jvm.internal.Ref$ObjectRef;

/* renamed from: com.android.systemui.util.FloatingContentCoordinator$Companion$findAreaForContentVertically$newContentBoundsBelow$2 */
/* compiled from: FloatingContentCoordinator.kt */
final class C1732xe284ccc5 extends Lambda implements Function0<Rect> {
    final /* synthetic */ Rect $contentRect;
    final /* synthetic */ Rect $newlyOverlappingRect;
    final /* synthetic */ Ref$ObjectRef $rectsToAvoidBelow;

    C1732xe284ccc5(Rect rect, Ref$ObjectRef ref$ObjectRef, Rect rect2) {
        this.$contentRect = rect;
        this.$rectsToAvoidBelow = ref$ObjectRef;
        this.$newlyOverlappingRect = rect2;
        super(0);
    }

    public final Rect invoke() {
        return FloatingContentCoordinator.Companion.findAreaForContentAboveOrBelow(this.$contentRect, CollectionsKt___CollectionsKt.plus((Collection) (List) this.$rectsToAvoidBelow.element, (Object) this.$newlyOverlappingRect), false);
    }
}
