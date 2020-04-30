package com.android.systemui.controls.p004ui;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.android.systemui.controls.p004ui.ToggleRangeBehavior.ToggleRangeGestureListener;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ToggleRangeBehavior$initialize$1 */
/* compiled from: ToggleRangeBehavior.kt */
final class ToggleRangeBehavior$initialize$1 implements OnTouchListener {
    final /* synthetic */ GestureDetector $gestureDetector;
    final /* synthetic */ ToggleRangeGestureListener $gestureListener;
    final /* synthetic */ ToggleRangeBehavior this$0;

    ToggleRangeBehavior$initialize$1(ToggleRangeBehavior toggleRangeBehavior, GestureDetector gestureDetector, ToggleRangeGestureListener toggleRangeGestureListener) {
        this.this$0 = toggleRangeBehavior;
        this.$gestureDetector = gestureDetector;
        this.$gestureListener = toggleRangeGestureListener;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        Intrinsics.checkParameterIsNotNull(view, "<anonymous parameter 0>");
        Intrinsics.checkParameterIsNotNull(motionEvent, "e");
        if (this.$gestureDetector.onTouchEvent(motionEvent)) {
            return true;
        }
        if (motionEvent.getAction() != 1 || !this.$gestureListener.isDragging()) {
            return false;
        }
        this.$gestureListener.setDragging(false);
        this.this$0.endUpdateRange();
        return true;
    }
}
