package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsets.Type;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.systemui.ScreenDecorations.DisplayCutoutView;

public class StatusBarWindowView extends FrameLayout {
    private int mLeftInset = 0;
    private int mRightInset = 0;
    private int mTopInset = 0;

    public StatusBarWindowView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        Insets insetsIgnoringVisibility = windowInsets.getInsetsIgnoringVisibility(Type.systemBars());
        this.mLeftInset = insetsIgnoringVisibility.left;
        this.mRightInset = insetsIgnoringVisibility.right;
        this.mTopInset = 0;
        DisplayCutout displayCutout = getRootWindowInsets().getDisplayCutout();
        if (displayCutout != null) {
            this.mTopInset = displayCutout.getWaterfallInsets().top;
        }
        applyMargins();
        return windowInsets;
    }

    private void applyMargins() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getLayoutParams() instanceof LayoutParams) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.rightMargin != this.mRightInset || layoutParams.leftMargin != this.mLeftInset || layoutParams.topMargin != this.mTopInset) {
                    layoutParams.rightMargin = this.mRightInset;
                    layoutParams.leftMargin = this.mLeftInset;
                    layoutParams.topMargin = this.mTopInset;
                    childAt.requestLayout();
                }
            }
        }
    }

    public static Pair<Integer, Integer> paddingNeededForCutoutAndRoundedCorner(DisplayCutout displayCutout, Pair<Integer, Integer> pair, int i) {
        int i2;
        if (displayCutout == null) {
            return new Pair<>(Integer.valueOf(i), Integer.valueOf(i));
        }
        int i3 = 0;
        if (pair != null) {
            i3 = ((Integer) pair.first).intValue();
            i2 = ((Integer) pair.second).intValue();
        } else {
            i2 = 0;
        }
        return new Pair<>(Integer.valueOf(Math.max(i3, i)), Integer.valueOf(Math.max(i2, i)));
    }

    public static Pair<Integer, Integer> cornerCutoutMargins(DisplayCutout displayCutout, Display display) {
        return statusBarCornerCutoutMargins(displayCutout, display, 0, 0);
    }

    public static Pair<Integer, Integer> statusBarCornerCutoutMargins(DisplayCutout displayCutout, Display display, int i, int i2) {
        if (displayCutout == null) {
            return null;
        }
        Point point = new Point();
        display.getRealSize(point);
        Rect rect = new Rect();
        if (i == 0) {
            DisplayCutoutView.boundsFromDirection(displayCutout, 48, rect);
        } else if (i == 1) {
            DisplayCutoutView.boundsFromDirection(displayCutout, 3, rect);
        } else if (i == 2) {
            DisplayCutoutView.boundsFromDirection(displayCutout, 5, rect);
        } else if (i == 3) {
            return null;
        }
        if (i2 >= 0 && rect.top > i2) {
            return null;
        }
        if (rect.left <= 0) {
            return new Pair<>(Integer.valueOf(rect.right), Integer.valueOf(0));
        }
        if (rect.right >= point.x) {
            return new Pair<>(Integer.valueOf(0), Integer.valueOf(point.x - rect.left));
        }
        return null;
    }
}
