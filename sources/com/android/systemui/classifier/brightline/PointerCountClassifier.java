package com.android.systemui.classifier.brightline;

import android.view.MotionEvent;

class PointerCountClassifier extends FalsingClassifier {
    private int mMaxPointerCount;

    PointerCountClassifier(FalsingDataProvider falsingDataProvider) {
        super(falsingDataProvider);
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        int i = this.mMaxPointerCount;
        if (motionEvent.getActionMasked() == 0) {
            this.mMaxPointerCount = motionEvent.getPointerCount();
        } else {
            this.mMaxPointerCount = Math.max(this.mMaxPointerCount, motionEvent.getPointerCount());
        }
        if (i != this.mMaxPointerCount) {
            StringBuilder sb = new StringBuilder();
            sb.append("Pointers observed:");
            sb.append(this.mMaxPointerCount);
            FalsingClassifier.logDebug(sb.toString());
        }
    }

    public boolean isFalseTouch() {
        int interactionType = getInteractionType();
        boolean z = false;
        if (interactionType == 0 || interactionType == 2) {
            if (this.mMaxPointerCount > 2) {
                z = true;
            }
            return z;
        }
        if (this.mMaxPointerCount > 1) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public String getReason() {
        return String.format(null, "{pointersObserved=%d, threshold=%d}", new Object[]{Integer.valueOf(this.mMaxPointerCount), Integer.valueOf(1)});
    }
}
