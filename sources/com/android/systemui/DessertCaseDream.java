package com.android.systemui;

import android.service.dreams.DreamService;
import com.android.systemui.DessertCaseView.RescalingContainer;

public class DessertCaseDream extends DreamService {
    private RescalingContainer mContainer;
    /* access modifiers changed from: private */
    public DessertCaseView mView;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);
        this.mView = new DessertCaseView(this);
        RescalingContainer rescalingContainer = new RescalingContainer(this);
        this.mContainer = rescalingContainer;
        rescalingContainer.setView(this.mView);
        setContentView(this.mContainer);
    }

    public void onDreamingStarted() {
        super.onDreamingStarted();
        this.mView.postDelayed(new Runnable() {
            public void run() {
                DessertCaseDream.this.mView.start();
            }
        }, 1000);
    }

    public void onDreamingStopped() {
        super.onDreamingStopped();
        this.mView.stop();
    }
}
