package com.android.systemui.bubbles;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.util.PathParser;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2013R$layout;

public class BubbleOverflow implements BubbleViewProvider {
    private int mBitmapSize = this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.bubble_bitmap_size);
    private Context mContext;
    private int mDotColor;
    private BubbleExpandedView mExpandedView;
    private Bitmap mIcon;
    private int mIconBitmapSize = this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.bubble_overflow_icon_bitmap_size);
    private LayoutInflater mInflater;
    private BadgedImageView mOverflowBtn;
    private Path mPath;

    public String getKey() {
        return "Overflow";
    }

    public void logUIEvent(int i, int i2, float f, float f2, int i3) {
    }

    public boolean showDot() {
        return false;
    }

    public BubbleOverflow(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    /* access modifiers changed from: 0000 */
    public void setUpOverflow(ViewGroup viewGroup, BubbleStackView bubbleStackView) {
        BubbleExpandedView bubbleExpandedView = (BubbleExpandedView) this.mInflater.inflate(C2013R$layout.bubble_expanded_view, viewGroup, false);
        this.mExpandedView = bubbleExpandedView;
        bubbleExpandedView.setOverflow(true);
        this.mExpandedView.setStackView(bubbleStackView);
        updateIcon(this.mContext, viewGroup);
    }

    /* access modifiers changed from: 0000 */
    public void updateIcon(Context context, ViewGroup viewGroup) {
        LayoutInflater from = LayoutInflater.from(context);
        this.mInflater = from;
        this.mOverflowBtn = (BadgedImageView) from.inflate(C2013R$layout.bubble_overflow_button, viewGroup, false);
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(new int[]{16844002});
        int color = obtainStyledAttributes.getColor(0, -1);
        obtainStyledAttributes.recycle();
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843829, typedValue, true);
        int color2 = this.mContext.getColor(typedValue.resourceId);
        this.mOverflowBtn.getDrawable().setTint(color2);
        this.mDotColor = color2;
        AdaptiveIconDrawable adaptiveIconDrawable = new AdaptiveIconDrawable(new ColorDrawable(color), new InsetDrawable(this.mOverflowBtn.getDrawable(), this.mBitmapSize - this.mIconBitmapSize));
        BubbleIconFactory bubbleIconFactory = new BubbleIconFactory(context);
        this.mIcon = bubbleIconFactory.createBadgedIconBitmap(adaptiveIconDrawable, null, true).icon;
        float scale = bubbleIconFactory.getNormalizer().getScale(this.mOverflowBtn.getDrawable(), null, null, null);
        this.mPath = PathParser.createPathFromPathData(context.getResources().getString(17039801));
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale, 50.0f, 50.0f);
        this.mPath.transform(matrix);
        this.mOverflowBtn.setVisibility(8);
        this.mOverflowBtn.update(this);
    }

    /* access modifiers changed from: 0000 */
    public ImageView getBtn() {
        return this.mOverflowBtn;
    }

    /* access modifiers changed from: 0000 */
    public void setBtnVisible(int i) {
        this.mOverflowBtn.setVisibility(i);
    }

    public BubbleExpandedView getExpandedView() {
        return this.mExpandedView;
    }

    public int getDotColor() {
        return this.mDotColor;
    }

    public Bitmap getBadgedImage() {
        return this.mIcon;
    }

    public Path getDotPath() {
        return this.mPath;
    }

    public void setContentVisibility(boolean z) {
        this.mExpandedView.setContentVisibility(z);
    }

    public View getIconView() {
        return this.mOverflowBtn;
    }

    public int getDisplayId() {
        BubbleExpandedView bubbleExpandedView = this.mExpandedView;
        if (bubbleExpandedView != null) {
            return bubbleExpandedView.getVirtualDisplayId();
        }
        return -1;
    }
}
