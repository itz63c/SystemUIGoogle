package androidx.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ActionMode.Callback;
import android.widget.TextView;
import androidx.core.widget.TextViewCompat;
import androidx.leanback.R$styleable;

class ResizingTextView extends TextView {
    private float mDefaultLineSpacingExtra;
    private int mDefaultPaddingBottom;
    private int mDefaultPaddingTop;
    private int mDefaultTextSize;
    private boolean mDefaultsInitialized;
    private boolean mMaintainLineSpacing;
    private int mResizedPaddingAdjustmentBottom;
    private int mResizedPaddingAdjustmentTop;
    private int mResizedTextSize;
    private int mTriggerConditions;

    public ResizingTextView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i);
        this.mDefaultsInitialized = false;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.lbResizingTextView, i, i2);
        try {
            this.mTriggerConditions = obtainStyledAttributes.getInt(R$styleable.lbResizingTextView_resizeTrigger, 1);
            this.mResizedTextSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.lbResizingTextView_resizedTextSize, -1);
            this.mMaintainLineSpacing = obtainStyledAttributes.getBoolean(R$styleable.lbResizingTextView_maintainLineSpacing, false);
            this.mResizedPaddingAdjustmentTop = obtainStyledAttributes.getDimensionPixelOffset(R$styleable.lbResizingTextView_resizedPaddingAdjustmentTop, 0);
            this.mResizedPaddingAdjustmentBottom = obtainStyledAttributes.getDimensionPixelOffset(R$styleable.lbResizingTextView_resizedPaddingAdjustmentBottom, 0);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public ResizingTextView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ResizingTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842884);
    }

    public ResizingTextView(Context context) {
        this(context, null);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:47:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r6, int r7) {
        /*
            r5 = this;
            boolean r0 = r5.mDefaultsInitialized
            r1 = 1
            if (r0 != 0) goto L_0x0020
            float r0 = r5.getTextSize()
            int r0 = (int) r0
            r5.mDefaultTextSize = r0
            float r0 = r5.getLineSpacingExtra()
            r5.mDefaultLineSpacingExtra = r0
            int r0 = r5.getPaddingTop()
            r5.mDefaultPaddingTop = r0
            int r0 = r5.getPaddingBottom()
            r5.mDefaultPaddingBottom = r0
            r5.mDefaultsInitialized = r1
        L_0x0020:
            int r0 = r5.mDefaultTextSize
            float r0 = (float) r0
            r2 = 0
            r5.setTextSize(r2, r0)
            float r0 = r5.mDefaultLineSpacingExtra
            float r3 = r5.getLineSpacingMultiplier()
            r5.setLineSpacing(r0, r3)
            int r0 = r5.mDefaultPaddingTop
            int r3 = r5.mDefaultPaddingBottom
            r5.setPaddingTopAndBottom(r0, r3)
            super.onMeasure(r6, r7)
            android.text.Layout r0 = r5.getLayout()
            if (r0 == 0) goto L_0x0053
            int r3 = r5.mTriggerConditions
            r3 = r3 & r1
            if (r3 <= 0) goto L_0x0053
            int r0 = r0.getLineCount()
            int r3 = r5.getMaxLines()
            if (r3 <= r1) goto L_0x0053
            if (r0 != r3) goto L_0x0053
            r0 = r1
            goto L_0x0054
        L_0x0053:
            r0 = r2
        L_0x0054:
            float r3 = r5.getTextSize()
            int r3 = (int) r3
            r4 = -1
            if (r0 == 0) goto L_0x009f
            int r0 = r5.mResizedTextSize
            if (r0 == r4) goto L_0x0067
            if (r3 == r0) goto L_0x0067
            float r0 = (float) r0
            r5.setTextSize(r2, r0)
            r2 = r1
        L_0x0067:
            float r0 = r5.mDefaultLineSpacingExtra
            int r3 = r5.mDefaultTextSize
            float r3 = (float) r3
            float r0 = r0 + r3
            int r3 = r5.mResizedTextSize
            float r3 = (float) r3
            float r0 = r0 - r3
            boolean r3 = r5.mMaintainLineSpacing
            if (r3 == 0) goto L_0x0085
            float r3 = r5.getLineSpacingExtra()
            int r3 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r3 == 0) goto L_0x0085
            float r2 = r5.getLineSpacingMultiplier()
            r5.setLineSpacing(r0, r2)
            r2 = r1
        L_0x0085:
            int r0 = r5.mDefaultPaddingTop
            int r3 = r5.mResizedPaddingAdjustmentTop
            int r0 = r0 + r3
            int r3 = r5.mDefaultPaddingBottom
            int r4 = r5.mResizedPaddingAdjustmentBottom
            int r3 = r3 + r4
            int r4 = r5.getPaddingTop()
            if (r4 != r0) goto L_0x009b
            int r4 = r5.getPaddingBottom()
            if (r4 == r3) goto L_0x00d3
        L_0x009b:
            r5.setPaddingTopAndBottom(r0, r3)
            goto L_0x00dc
        L_0x009f:
            int r0 = r5.mResizedTextSize
            if (r0 == r4) goto L_0x00ac
            int r0 = r5.mDefaultTextSize
            if (r3 == r0) goto L_0x00ac
            float r0 = (float) r0
            r5.setTextSize(r2, r0)
            r2 = r1
        L_0x00ac:
            boolean r0 = r5.mMaintainLineSpacing
            if (r0 == 0) goto L_0x00c2
            float r0 = r5.getLineSpacingExtra()
            float r3 = r5.mDefaultLineSpacingExtra
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x00c2
            float r0 = r5.getLineSpacingMultiplier()
            r5.setLineSpacing(r3, r0)
            r2 = r1
        L_0x00c2:
            int r0 = r5.getPaddingTop()
            int r3 = r5.mDefaultPaddingTop
            if (r0 != r3) goto L_0x00d5
            int r0 = r5.getPaddingBottom()
            int r3 = r5.mDefaultPaddingBottom
            if (r0 == r3) goto L_0x00d3
            goto L_0x00d5
        L_0x00d3:
            r1 = r2
            goto L_0x00dc
        L_0x00d5:
            int r0 = r5.mDefaultPaddingTop
            int r2 = r5.mDefaultPaddingBottom
            r5.setPaddingTopAndBottom(r0, r2)
        L_0x00dc:
            if (r1 == 0) goto L_0x00e1
            super.onMeasure(r6, r7)
        L_0x00e1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.ResizingTextView.onMeasure(int, int):void");
    }

    private void setPaddingTopAndBottom(int i, int i2) {
        if (isPaddingRelative()) {
            setPaddingRelative(getPaddingStart(), i, getPaddingEnd(), i2);
        } else {
            setPadding(getPaddingLeft(), i, getPaddingRight(), i2);
        }
    }

    public void setCustomSelectionActionModeCallback(Callback callback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback(this, callback));
    }
}
