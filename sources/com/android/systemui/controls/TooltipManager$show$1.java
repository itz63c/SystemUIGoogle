package com.android.systemui.controls;

import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

/* compiled from: TooltipManager.kt */
final class TooltipManager$show$1 implements Runnable {

    /* renamed from: $x */
    final /* synthetic */ int f39$x;

    /* renamed from: $y */
    final /* synthetic */ int f40$y;
    final /* synthetic */ TooltipManager this$0;

    TooltipManager$show$1(TooltipManager tooltipManager, int i, int i2) {
        this.this$0 = tooltipManager;
        this.f39$x = i;
        this.f40$y = i2;
    }

    public final void run() {
        int[] iArr = new int[2];
        this.this$0.getLayout().getLocationOnScreen(iArr);
        int i = 0;
        this.this$0.getLayout().setTranslationX((float) ((this.f39$x - iArr[0]) - (this.this$0.getLayout().getWidth() / 2)));
        ViewGroup layout = this.this$0.getLayout();
        float f = (float) (this.f40$y - iArr[1]);
        if (!this.this$0.below) {
            i = this.this$0.getLayout().getHeight();
        }
        layout.setTranslationY(f - ((float) i));
        if (this.this$0.getLayout().getAlpha() == 0.0f) {
            this.this$0.getLayout().animate().alpha(1.0f).withLayer().setStartDelay(500).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
        }
    }
}
