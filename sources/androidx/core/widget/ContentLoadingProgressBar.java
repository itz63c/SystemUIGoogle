package androidx.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class ContentLoadingProgressBar extends ProgressBar {
    private final Runnable mDelayedHide = new Runnable() {
        public void run() {
            ContentLoadingProgressBar contentLoadingProgressBar = ContentLoadingProgressBar.this;
            contentLoadingProgressBar.mPostedHide = false;
            contentLoadingProgressBar.mStartTime = -1;
            contentLoadingProgressBar.setVisibility(8);
        }
    };
    private final Runnable mDelayedShow = new Runnable() {
        public void run() {
            ContentLoadingProgressBar contentLoadingProgressBar = ContentLoadingProgressBar.this;
            contentLoadingProgressBar.mPostedShow = false;
            if (!contentLoadingProgressBar.mDismissed) {
                contentLoadingProgressBar.mStartTime = System.currentTimeMillis();
                ContentLoadingProgressBar.this.setVisibility(0);
            }
        }
    };
    boolean mDismissed = false;
    boolean mPostedHide;
    boolean mPostedShow;
    long mStartTime;

    public ContentLoadingProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks();
    }

    private void removeCallbacks() {
        removeCallbacks(this.mDelayedHide);
        removeCallbacks(this.mDelayedShow);
    }
}
