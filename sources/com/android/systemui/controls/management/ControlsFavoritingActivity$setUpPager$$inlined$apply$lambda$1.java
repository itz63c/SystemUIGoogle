package com.android.systemui.controls.management;

import android.text.TextUtils;
import android.widget.TextView;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

/* compiled from: ControlsFavoritingActivity.kt */
public final class ControlsFavoritingActivity$setUpPager$$inlined$apply$lambda$1 extends OnPageChangeCallback {
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$setUpPager$$inlined$apply$lambda$1(ControlsFavoritingActivity controlsFavoritingActivity) {
        this.this$0 = controlsFavoritingActivity;
    }

    public void onPageSelected(int i) {
        super.onPageSelected(i);
        CharSequence structureName = ((StructureContainer) this.this$0.listOfStructures.get(i)).getStructureName();
        TextView access$getTitleView$p = ControlsFavoritingActivity.access$getTitleView$p(this.this$0);
        if (TextUtils.isEmpty(structureName)) {
            structureName = this.this$0.appName;
        }
        access$getTitleView$p.setText(structureName);
    }

    public void onPageScrolled(int i, float f, int i2) {
        super.onPageScrolled(i, f, i2);
        ControlsFavoritingActivity.access$getPageIndicator$p(this.this$0).setLocation(((float) i) + f);
    }
}
