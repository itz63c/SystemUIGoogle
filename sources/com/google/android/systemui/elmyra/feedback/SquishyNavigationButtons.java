package com.google.android.systemui.elmyra.feedback;

import android.content.Context;
import android.view.View;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SquishyNavigationButtons extends NavigationBarEffect {
    private final KeyguardViewMediator mKeyguardViewMediator;
    private final SquishyViewController mViewController;

    public SquishyNavigationButtons(Context context, KeyguardViewMediator keyguardViewMediator, StatusBar statusBar) {
        super(statusBar);
        this.mViewController = new SquishyViewController(context);
        this.mKeyguardViewMediator = keyguardViewMediator;
    }

    /* access modifiers changed from: protected */
    public List<FeedbackEffect> findFeedbackEffects(NavigationBarView navigationBarView) {
        this.mViewController.clearViews();
        ArrayList views = navigationBarView.getBackButton().getViews();
        for (int i = 0; i < views.size(); i++) {
            this.mViewController.addLeftView((View) views.get(i));
        }
        ArrayList views2 = navigationBarView.getRecentsButton().getViews();
        for (int i2 = 0; i2 < views2.size(); i2++) {
            this.mViewController.addRightView((View) views2.get(i2));
        }
        return Arrays.asList(new FeedbackEffect[]{this.mViewController});
    }

    /* access modifiers changed from: protected */
    public boolean validateFeedbackEffects(List<FeedbackEffect> list) {
        boolean isAttachedToWindow = this.mViewController.isAttachedToWindow();
        if (!isAttachedToWindow) {
            this.mViewController.clearViews();
        }
        return isAttachedToWindow;
    }

    /* access modifiers changed from: protected */
    public boolean isActiveFeedbackEffect(FeedbackEffect feedbackEffect) {
        return !this.mKeyguardViewMediator.isShowingAndNotOccluded();
    }
}
