package com.android.systemui.controls.management;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.systemui.p007qs.PageIndicator;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ManagementPageIndicator.kt */
public final class ManagementPageIndicator extends PageIndicator {
    private Function1<? super Integer, Unit> visibilityListener = ManagementPageIndicator$visibilityListener$1.INSTANCE;

    public ManagementPageIndicator(Context context, AttributeSet attributeSet) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(attributeSet, "attrs");
        super(context, attributeSet);
    }

    public void setLocation(float f) {
        if (getLayoutDirection() == 1) {
            super.setLocation(((float) (getChildCount() - 1)) - f);
        } else {
            super.setLocation(f);
        }
    }

    public final void setVisibilityListener(Function1<? super Integer, Unit> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "<set-?>");
        this.visibilityListener = function1;
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChanged(View view, int i) {
        Intrinsics.checkParameterIsNotNull(view, "changedView");
        super.onVisibilityChanged(view, i);
        if (Intrinsics.areEqual((Object) view, (Object) this)) {
            this.visibilityListener.invoke(Integer.valueOf(i));
        }
    }
}
