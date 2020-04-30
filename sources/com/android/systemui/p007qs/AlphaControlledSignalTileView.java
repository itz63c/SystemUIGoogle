package com.android.systemui.p007qs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import com.android.systemui.p007qs.tileimpl.SlashImageView;

/* renamed from: com.android.systemui.qs.AlphaControlledSignalTileView */
public class AlphaControlledSignalTileView extends SignalTileView {

    /* renamed from: com.android.systemui.qs.AlphaControlledSignalTileView$AlphaControlledSlashDrawable */
    public static class AlphaControlledSlashDrawable extends SlashDrawable {
        /* access modifiers changed from: protected */
        public void setDrawableTintList(ColorStateList colorStateList) {
        }

        AlphaControlledSlashDrawable(Drawable drawable) {
            super(drawable);
        }

        public void setFinalTintList(ColorStateList colorStateList) {
            super.setDrawableTintList(colorStateList);
        }
    }

    /* renamed from: com.android.systemui.qs.AlphaControlledSignalTileView$AlphaControlledSlashImageView */
    public static class AlphaControlledSlashImageView extends SlashImageView {
        public AlphaControlledSlashImageView(Context context) {
            super(context);
        }

        public void setFinalImageTintList(ColorStateList colorStateList) {
            super.setImageTintList(colorStateList);
            SlashDrawable slash = getSlash();
            if (slash != null) {
                ((AlphaControlledSlashDrawable) slash).setFinalTintList(colorStateList);
            }
        }

        /* access modifiers changed from: protected */
        public void ensureSlashDrawable() {
            if (getSlash() == null) {
                AlphaControlledSlashDrawable alphaControlledSlashDrawable = new AlphaControlledSlashDrawable(getDrawable());
                setSlash(alphaControlledSlashDrawable);
                alphaControlledSlashDrawable.setAnimationEnabled(getAnimationEnabled());
                setImageViewDrawable(alphaControlledSlashDrawable);
            }
        }
    }

    public AlphaControlledSignalTileView(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public SlashImageView createSlashImageView(Context context) {
        return new AlphaControlledSlashImageView(context);
    }
}
