package com.google.android.systemui.elmyra.feedback;

import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.ArrayList;
import java.util.List;

public abstract class NavigationBarEffect implements FeedbackEffect {
    private final List<FeedbackEffect> mFeedbackEffects = new ArrayList();
    private final StatusBar mStatusBar;

    /* access modifiers changed from: protected */
    public abstract List<FeedbackEffect> findFeedbackEffects(NavigationBarView navigationBarView);

    /* access modifiers changed from: protected */
    public abstract boolean isActiveFeedbackEffect(FeedbackEffect feedbackEffect);

    /* access modifiers changed from: protected */
    public abstract boolean validateFeedbackEffects(List<FeedbackEffect> list);

    public NavigationBarEffect(StatusBar statusBar) {
        this.mStatusBar = statusBar;
    }

    public void onProgress(float f, int i) {
        refreshFeedbackEffects();
        for (int i2 = 0; i2 < this.mFeedbackEffects.size(); i2++) {
            FeedbackEffect feedbackEffect = (FeedbackEffect) this.mFeedbackEffects.get(i2);
            if (isActiveFeedbackEffect(feedbackEffect)) {
                feedbackEffect.onProgress(f, i);
            }
        }
    }

    public void onRelease() {
        refreshFeedbackEffects();
        for (int i = 0; i < this.mFeedbackEffects.size(); i++) {
            ((FeedbackEffect) this.mFeedbackEffects.get(i)).onRelease();
        }
    }

    public void onResolve(DetectionProperties detectionProperties) {
        refreshFeedbackEffects();
        for (int i = 0; i < this.mFeedbackEffects.size(); i++) {
            ((FeedbackEffect) this.mFeedbackEffects.get(i)).onResolve(detectionProperties);
        }
    }

    private void refreshFeedbackEffects() {
        NavigationBarView navigationBarView = this.mStatusBar.getNavigationBarView();
        if (navigationBarView == null) {
            this.mFeedbackEffects.clear();
            return;
        }
        if (!validateFeedbackEffects(this.mFeedbackEffects)) {
            this.mFeedbackEffects.clear();
        }
        if (this.mFeedbackEffects.isEmpty()) {
            this.mFeedbackEffects.addAll(findFeedbackEffects(navigationBarView));
        }
    }
}
