package com.android.systemui.bubbles;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.recents.TriangleShape;

public class BubbleManageEducationView extends LinearLayout {
    private View mManageView;
    private View mPointerView;

    public BubbleManageEducationView(Context context) {
        this(context, null);
    }

    public BubbleManageEducationView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BubbleManageEducationView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public BubbleManageEducationView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mManageView = findViewById(C2011R$id.manage_education_view);
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(new int[]{16843829, 16842809});
        int color = obtainStyledAttributes.getColor(0, -16777216);
        int color2 = obtainStyledAttributes.getColor(1, -1);
        obtainStyledAttributes.recycle();
        ((TextView) findViewById(C2011R$id.user_education_description)).setTextColor(ContrastColorUtil.ensureTextContrast(color2, color, true));
        Resources resources = getResources();
        ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.create((float) resources.getDimensionPixelSize(C2009R$dimen.bubble_pointer_width), (float) resources.getDimensionPixelSize(C2009R$dimen.bubble_pointer_height), false));
        shapeDrawable.setTint(color);
        View findViewById = findViewById(C2011R$id.user_education_pointer);
        this.mPointerView = findViewById;
        findViewById.setBackground(shapeDrawable);
    }

    public void setPointerPosition(int i) {
        View view = this.mPointerView;
        view.setTranslationX((float) (i - (view.getWidth() / 2)));
    }

    public void setManageViewPosition(int i, int i2) {
        this.mManageView.setTranslationX((float) i);
        this.mManageView.setTranslationY((float) i2);
    }

    public int getManageViewHeight() {
        return this.mManageView.getHeight();
    }
}
