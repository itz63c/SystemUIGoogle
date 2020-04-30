package com.android.settingslib.widget;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

public class AdaptiveIcon extends LayerDrawable {
    private AdaptiveConstantState mAdaptiveConstantState;
    int mBackgroundColor = -1;

    static class AdaptiveConstantState extends ConstantState {
        int mColor;
        Context mContext;
        Drawable mDrawable;

        public int getChangingConfigurations() {
            return 0;
        }

        AdaptiveConstantState(Context context, Drawable drawable) {
            this.mContext = context;
            this.mDrawable = drawable;
        }

        public Drawable newDrawable() {
            AdaptiveIcon adaptiveIcon = new AdaptiveIcon(this.mContext, this.mDrawable);
            adaptiveIcon.setBackgroundColor(this.mColor);
            return adaptiveIcon;
        }
    }

    public AdaptiveIcon(Context context, Drawable drawable) {
        super(new Drawable[]{new AdaptiveIconShapeDrawable(context.getResources()), drawable});
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.dashboard_tile_foreground_image_inset);
        setLayerInset(1, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
        this.mAdaptiveConstantState = new AdaptiveConstantState(context, drawable);
    }

    public void setBackgroundColor(int i) {
        this.mBackgroundColor = i;
        getDrawable(0).setColorFilter(i, Mode.SRC_ATOP);
        StringBuilder sb = new StringBuilder();
        sb.append("Setting background color ");
        sb.append(this.mBackgroundColor);
        Log.d("AdaptiveHomepageIcon", sb.toString());
        this.mAdaptiveConstantState.mColor = i;
    }

    public ConstantState getConstantState() {
        return this.mAdaptiveConstantState;
    }
}
