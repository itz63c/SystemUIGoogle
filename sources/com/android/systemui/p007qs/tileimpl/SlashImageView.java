package com.android.systemui.p007qs.tileimpl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.p007qs.SlashDrawable;
import com.android.systemui.plugins.p006qs.QSTile.SlashState;

/* renamed from: com.android.systemui.qs.tileimpl.SlashImageView */
public class SlashImageView extends ImageView {
    private boolean mAnimationEnabled = true;
    @VisibleForTesting
    protected SlashDrawable mSlash;

    public SlashImageView(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public SlashDrawable getSlash() {
        return this.mSlash;
    }

    /* access modifiers changed from: protected */
    public void setSlash(SlashDrawable slashDrawable) {
        this.mSlash = slashDrawable;
    }

    /* access modifiers changed from: protected */
    public void ensureSlashDrawable() {
        if (this.mSlash == null) {
            SlashDrawable slashDrawable = new SlashDrawable(getDrawable());
            this.mSlash = slashDrawable;
            slashDrawable.setAnimationEnabled(this.mAnimationEnabled);
            super.setImageDrawable(this.mSlash);
        }
    }

    public void setImageDrawable(Drawable drawable) {
        if (drawable == null) {
            this.mSlash = null;
            super.setImageDrawable(null);
            return;
        }
        SlashDrawable slashDrawable = this.mSlash;
        if (slashDrawable == null) {
            setImageLevel(drawable.getLevel());
            super.setImageDrawable(drawable);
            return;
        }
        slashDrawable.setAnimationEnabled(this.mAnimationEnabled);
        this.mSlash.setDrawable(drawable);
    }

    /* access modifiers changed from: protected */
    public void setImageViewDrawable(SlashDrawable slashDrawable) {
        super.setImageDrawable(slashDrawable);
    }

    public void setAnimationEnabled(boolean z) {
        this.mAnimationEnabled = z;
    }

    public boolean getAnimationEnabled() {
        return this.mAnimationEnabled;
    }

    private void setSlashState(SlashState slashState) {
        ensureSlashDrawable();
        this.mSlash.setRotation(slashState.rotation);
        this.mSlash.setSlashed(slashState.isSlashed);
    }

    public void setState(SlashState slashState, Drawable drawable) {
        if (slashState != null) {
            setImageDrawable(drawable);
            setSlashState(slashState);
            return;
        }
        this.mSlash = null;
        setImageDrawable(drawable);
    }
}
