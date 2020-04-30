package com.android.systemui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DarkReceiverImpl.kt */
public final class DarkReceiverImpl extends View implements DarkReceiver {
    private final DualToneHandler dualToneHandler;

    public DarkReceiverImpl(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0, 0, 12, null);
    }

    public /* synthetic */ DarkReceiverImpl(Context context, AttributeSet attributeSet, int i, int i2, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        if ((i3 & 2) != 0) {
            attributeSet = null;
        }
        if ((i3 & 4) != 0) {
            i = 0;
        }
        if ((i3 & 8) != 0) {
            i2 = 0;
        }
        this(context, attributeSet, i, i2);
    }

    public DarkReceiverImpl(Context context, AttributeSet attributeSet, int i, int i2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context, attributeSet, i, i2);
        this.dualToneHandler = new DualToneHandler(context);
        onDarkChanged(new Rect(), 1.0f, -1);
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        if (!DarkIconDispatcher.isInArea(rect, this)) {
            f = 0.0f;
        }
        setBackgroundColor(this.dualToneHandler.getSingleColor(f));
    }
}
