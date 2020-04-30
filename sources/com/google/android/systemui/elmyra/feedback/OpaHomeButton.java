package com.google.android.systemui.elmyra.feedback;

import android.view.View;
import android.view.ViewParent;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.ArrayList;
import java.util.List;

public class OpaHomeButton extends NavigationBarEffect {
    private final KeyguardViewMediator mKeyguardViewMediator;
    private NavigationBarView mNavigationBar;

    public OpaHomeButton(KeyguardViewMediator keyguardViewMediator, StatusBar statusBar) {
        super(statusBar);
        this.mKeyguardViewMediator = keyguardViewMediator;
    }

    /* access modifiers changed from: protected */
    public List<FeedbackEffect> findFeedbackEffects(NavigationBarView navigationBarView) {
        ArrayList arrayList = new ArrayList();
        ArrayList views = navigationBarView.getHomeButton().getViews();
        for (int i = 0; i < views.size(); i++) {
            View view = (View) views.get(i);
            if (view instanceof FeedbackEffect) {
                arrayList.add((FeedbackEffect) view);
            }
        }
        this.mNavigationBar = navigationBarView;
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public boolean validateFeedbackEffects(List<FeedbackEffect> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!((View) list.get(i)).isAttachedToWindow()) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean isActiveFeedbackEffect(FeedbackEffect feedbackEffect) {
        if (this.mKeyguardViewMediator.isShowingAndNotOccluded()) {
            return false;
        }
        View currentView = this.mNavigationBar.getCurrentView();
        for (ViewParent parent = ((View) feedbackEffect).getParent(); parent != null; parent = parent.getParent()) {
            if (parent.equals(currentView)) {
                return true;
            }
        }
        return false;
    }
}
