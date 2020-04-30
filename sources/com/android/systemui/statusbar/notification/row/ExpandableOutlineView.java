package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.Path.Op;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.android.settingslib.Utils;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;

public abstract class ExpandableOutlineView extends ExpandableView {
    private static final AnimatableProperty BOTTOM_ROUNDNESS = AnimatableProperty.from("bottomRoundness", $$Lambda$ExpandableOutlineView$ZLqiUGCQzNj3P4m8kfbTwbzfyaI.INSTANCE, $$Lambda$RLFq7_ULx7AWbuaAJNsAxNrN1PI.INSTANCE, C2011R$id.bottom_roundess_animator_tag, C2011R$id.bottom_roundess_animator_end_tag, C2011R$id.bottom_roundess_animator_start_tag);
    private static final Path EMPTY_PATH = new Path();
    private static final AnimationProperties ROUNDNESS_PROPERTIES;
    private static final AnimatableProperty TOP_ROUNDNESS = AnimatableProperty.from("topRoundness", $$Lambda$ExpandableOutlineView$lgIjKBD4iaJhFeEZ5izPzOddhds.INSTANCE, $$Lambda$iDWyu_PNFZfGQr9kcCLSWoFYxpI.INSTANCE, C2011R$id.top_roundess_animator_tag, C2011R$id.top_roundess_animator_end_tag, C2011R$id.top_roundess_animator_start_tag);
    /* access modifiers changed from: private */
    public boolean mAlwaysRoundBothCorners;
    /* access modifiers changed from: private */
    public int mBackgroundTop;
    private float mBottomRoundness;
    private final Path mClipPath = new Path();
    /* access modifiers changed from: private */
    public float mCurrentBottomRoundness;
    /* access modifiers changed from: private */
    public float mCurrentTopRoundness;
    /* access modifiers changed from: private */
    public boolean mCustomOutline;
    private float mDistanceToTopRoundness = -1.0f;
    /* access modifiers changed from: private */
    public float mOutlineAlpha = -1.0f;
    protected float mOutlineRadius;
    private final Rect mOutlineRect = new Rect();
    private final ViewOutlineProvider mProvider;
    protected boolean mShouldTranslateContents;
    private Path mTmpPath = new Path();
    /* access modifiers changed from: private */
    public boolean mTopAmountRounded;
    private float mTopRoundness;

    /* access modifiers changed from: protected */
    public boolean childNeedsClipping(View view) {
        return false;
    }

    public Path getCustomClipPath(View view) {
        return null;
    }

    public boolean topAmountNeedsClipping() {
        return true;
    }

    static {
        AnimationProperties animationProperties = new AnimationProperties();
        animationProperties.setDuration(360);
        ROUNDNESS_PROPERTIES = animationProperties;
    }

    /* access modifiers changed from: protected */
    public Path getClipPath(boolean z) {
        int i;
        int i2;
        int i3;
        int i4;
        float currentBackgroundRadiusTop = this.mAlwaysRoundBothCorners ? this.mOutlineRadius : getCurrentBackgroundRadiusTop();
        if (!this.mCustomOutline) {
            int translation = (!this.mShouldTranslateContents || z) ? 0 : (int) getTranslation();
            int i5 = (int) (this.mExtraWidthForClipping / 2.0f);
            i4 = Math.max(translation, 0) - i5;
            i3 = this.mClipTopAmount + this.mBackgroundTop;
            i2 = getWidth() + i5 + Math.min(translation, 0);
            i = Math.max(this.mMinimumHeightForClipping, Math.max(getActualHeight() - this.mClipBottomAmount, (int) (((float) i3) + currentBackgroundRadiusTop)));
        } else {
            Rect rect = this.mOutlineRect;
            i4 = rect.left;
            i3 = rect.top;
            i2 = rect.right;
            i = rect.bottom;
        }
        int i6 = i;
        int i7 = i4;
        int i8 = i3;
        int i9 = i2;
        int i10 = i6 - i8;
        if (i10 == 0) {
            return EMPTY_PATH;
        }
        float currentBackgroundRadiusBottom = this.mAlwaysRoundBothCorners ? this.mOutlineRadius : getCurrentBackgroundRadiusBottom();
        float f = currentBackgroundRadiusTop + currentBackgroundRadiusBottom;
        float f2 = (float) i10;
        if (f > f2) {
            float f3 = f - f2;
            float f4 = this.mCurrentTopRoundness;
            float f5 = f3 * f4;
            float f6 = this.mCurrentBottomRoundness;
            currentBackgroundRadiusTop -= f5 / (f4 + f6);
            currentBackgroundRadiusBottom -= (f3 * f6) / (f4 + f6);
        }
        getRoundedRectPath(i7, i8, i9, i6, currentBackgroundRadiusTop, currentBackgroundRadiusBottom, this.mTmpPath);
        return this.mTmpPath;
    }

    public static void getRoundedRectPath(int i, int i2, int i3, int i4, float f, float f2, Path path) {
        path.reset();
        float f3 = (float) ((i3 - i) / 2);
        float min = Math.min(f3, f);
        float min2 = Math.min(f3, f2);
        if (f > 0.0f) {
            float f4 = (float) i;
            float f5 = (float) i2;
            float f6 = f + f5;
            path.moveTo(f4, f6);
            path.quadTo(f4, f5, f4 + min, f5);
            float f7 = (float) i3;
            path.lineTo(f7 - min, f5);
            path.quadTo(f7, f5, f7, f6);
        } else {
            float f8 = (float) i2;
            path.moveTo((float) i, f8);
            path.lineTo((float) i3, f8);
        }
        if (f2 > 0.0f) {
            float f9 = (float) i3;
            float f10 = (float) i4;
            float f11 = f10 - f2;
            path.lineTo(f9, f11);
            path.quadTo(f9, f10, f9 - min2, f10);
            float f12 = (float) i;
            path.lineTo(min2 + f12, f10);
            path.quadTo(f12, f10, f12, f11);
        } else {
            float f13 = (float) i3;
            float f14 = (float) i4;
            path.lineTo(f13, f14);
            path.lineTo((float) i, f14);
        }
        path.close();
    }

    public ExpandableOutlineView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        C12741 r1 = new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                if (ExpandableOutlineView.this.mCustomOutline || ExpandableOutlineView.this.mCurrentTopRoundness != 0.0f || ExpandableOutlineView.this.mCurrentBottomRoundness != 0.0f || ExpandableOutlineView.this.mAlwaysRoundBothCorners || ExpandableOutlineView.this.mTopAmountRounded) {
                    Path clipPath = ExpandableOutlineView.this.getClipPath(false);
                    if (clipPath != null) {
                        outline.setPath(clipPath);
                    }
                } else {
                    ExpandableOutlineView expandableOutlineView = ExpandableOutlineView.this;
                    int translation = expandableOutlineView.mShouldTranslateContents ? (int) expandableOutlineView.getTranslation() : 0;
                    int max = Math.max(translation, 0);
                    ExpandableOutlineView expandableOutlineView2 = ExpandableOutlineView.this;
                    int access$500 = expandableOutlineView2.mClipTopAmount + expandableOutlineView2.mBackgroundTop;
                    outline.setRect(max, access$500, ExpandableOutlineView.this.getWidth() + Math.min(translation, 0), Math.max(ExpandableOutlineView.this.getActualHeight() - ExpandableOutlineView.this.mClipBottomAmount, access$500));
                }
                outline.setAlpha(ExpandableOutlineView.this.mOutlineAlpha);
            }
        };
        this.mProvider = r1;
        setOutlineProvider(r1);
        initDimens();
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        Path path;
        canvas.save();
        if (!this.mTopAmountRounded || !topAmountNeedsClipping()) {
            path = null;
        } else {
            int i = (int) ((-this.mExtraWidthForClipping) / 2.0f);
            int i2 = (int) (((float) this.mClipTopAmount) - this.mDistanceToTopRoundness);
            getRoundedRectPath(i, i2, ((int) (this.mExtraWidthForClipping + ((float) i))) + getWidth(), (int) Math.max((float) this.mMinimumHeightForClipping, Math.max((float) (getActualHeight() - this.mClipBottomAmount), ((float) i2) + this.mOutlineRadius)), this.mOutlineRadius, 0.0f, this.mClipPath);
            path = this.mClipPath;
        }
        boolean z = false;
        if (childNeedsClipping(view)) {
            Path customClipPath = getCustomClipPath(view);
            if (customClipPath == null) {
                customClipPath = getClipPath(false);
            }
            if (customClipPath != null) {
                if (path != null) {
                    customClipPath.op(path, Op.INTERSECT);
                }
                canvas.clipPath(customClipPath);
                z = true;
            }
        }
        if (!z && path != null) {
            canvas.clipPath(path);
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restore();
        return drawChild;
    }

    public void setExtraWidthForClipping(float f) {
        super.setExtraWidthForClipping(f);
        invalidate();
    }

    public void setMinimumHeightForClipping(int i) {
        super.setMinimumHeightForClipping(i);
        invalidate();
    }

    public void setDistanceToTopRoundness(float f) {
        super.setDistanceToTopRoundness(f);
        if (f != this.mDistanceToTopRoundness) {
            this.mTopAmountRounded = f >= 0.0f;
            this.mDistanceToTopRoundness = f;
            applyRoundness();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isClippingNeeded() {
        return this.mAlwaysRoundBothCorners || this.mCustomOutline || getTranslation() != 0.0f;
    }

    private void initDimens() {
        Resources resources = getResources();
        this.mShouldTranslateContents = resources.getBoolean(C2007R$bool.config_translateNotificationContentsOnSwipe);
        this.mOutlineRadius = resources.getDimension(C2009R$dimen.notification_shadow_radius);
        boolean z = resources.getBoolean(C2007R$bool.config_clipNotificationsToOutline);
        this.mAlwaysRoundBothCorners = z;
        if (!z) {
            this.mOutlineRadius = (float) resources.getDimensionPixelSize(Utils.getThemeAttr(this.mContext, 16844145));
        }
        setClipToOutline(this.mAlwaysRoundBothCorners);
    }

    public boolean setTopRoundness(float f, boolean z) {
        if (this.mTopRoundness == f) {
            return false;
        }
        this.mTopRoundness = f;
        PropertyAnimator.setProperty(this, TOP_ROUNDNESS, f, ROUNDNESS_PROPERTIES, z);
        return true;
    }

    /* access modifiers changed from: protected */
    public void applyRoundness() {
        invalidateOutline();
        invalidate();
    }

    public float getCurrentBackgroundRadiusTop() {
        if (this.mTopAmountRounded) {
            return this.mOutlineRadius;
        }
        return this.mCurrentTopRoundness * this.mOutlineRadius;
    }

    public float getCurrentTopRoundness() {
        return this.mCurrentTopRoundness;
    }

    public float getCurrentBottomRoundness() {
        return this.mCurrentBottomRoundness;
    }

    /* access modifiers changed from: protected */
    public float getCurrentBackgroundRadiusBottom() {
        return this.mCurrentBottomRoundness * this.mOutlineRadius;
    }

    public boolean setBottomRoundness(float f, boolean z) {
        if (this.mBottomRoundness == f) {
            return false;
        }
        this.mBottomRoundness = f;
        PropertyAnimator.setProperty(this, BOTTOM_ROUNDNESS, f, ROUNDNESS_PROPERTIES, z);
        return true;
    }

    /* access modifiers changed from: private */
    public void setTopRoundnessInternal(float f) {
        this.mCurrentTopRoundness = f;
        applyRoundness();
    }

    /* access modifiers changed from: private */
    public void setBottomRoundnessInternal(float f) {
        this.mCurrentBottomRoundness = f;
        applyRoundness();
    }

    public void onDensityOrFontScaleChanged() {
        initDimens();
        applyRoundness();
    }

    public void setActualHeight(int i, boolean z) {
        int actualHeight = getActualHeight();
        super.setActualHeight(i, z);
        if (actualHeight != i) {
            applyRoundness();
        }
    }

    public void setClipTopAmount(int i) {
        int clipTopAmount = getClipTopAmount();
        super.setClipTopAmount(i);
        if (clipTopAmount != i) {
            applyRoundness();
        }
    }

    public void setClipBottomAmount(int i) {
        int clipBottomAmount = getClipBottomAmount();
        super.setClipBottomAmount(i);
        if (clipBottomAmount != i) {
            applyRoundness();
        }
    }

    /* access modifiers changed from: protected */
    public void setOutlineAlpha(float f) {
        if (f != this.mOutlineAlpha) {
            this.mOutlineAlpha = f;
            applyRoundness();
        }
    }

    public float getOutlineAlpha() {
        return this.mOutlineAlpha;
    }

    /* access modifiers changed from: protected */
    public void setOutlineRect(RectF rectF) {
        if (rectF != null) {
            setOutlineRect(rectF.left, rectF.top, rectF.right, rectF.bottom);
            return;
        }
        this.mCustomOutline = false;
        applyRoundness();
    }

    public int getOutlineTranslation() {
        return this.mCustomOutline ? this.mOutlineRect.left : (int) getTranslation();
    }

    public void updateOutline() {
        if (!this.mCustomOutline) {
            setOutlineProvider(needsOutline() ? this.mProvider : null);
        }
    }

    /* access modifiers changed from: protected */
    public boolean needsOutline() {
        boolean z = false;
        if (isChildInGroup()) {
            if (isGroupExpanded() && !isGroupExpansionChanging()) {
                z = true;
            }
            return z;
        } else if (!isSummaryWithChildren()) {
            return true;
        } else {
            if (!isGroupExpanded() || isGroupExpansionChanging()) {
                z = true;
            }
            return z;
        }
    }

    /* access modifiers changed from: protected */
    public void setOutlineRect(float f, float f2, float f3, float f4) {
        this.mCustomOutline = true;
        this.mOutlineRect.set((int) f, (int) f2, (int) f3, (int) f4);
        Rect rect = this.mOutlineRect;
        rect.bottom = (int) Math.max(f2, (float) rect.bottom);
        Rect rect2 = this.mOutlineRect;
        rect2.right = (int) Math.max(f, (float) rect2.right);
        applyRoundness();
    }
}
