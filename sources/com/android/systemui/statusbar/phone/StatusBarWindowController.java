package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.os.Binder;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;

public class StatusBarWindowController {
    private int mBarHeight = -1;
    private final Context mContext;
    private final State mCurrentState = new State();
    private LayoutParams mLp;
    private final LayoutParams mLpChanged;
    private final Resources mResources;
    private ViewGroup mStatusBarView;
    private final SuperStatusBarViewFactory mSuperStatusBarViewFactory;
    private final WindowManager mWindowManager;

    private static class State {
        boolean mForceStatusBarVisible;

        private State() {
        }
    }

    public StatusBarWindowController(Context context, WindowManager windowManager, SuperStatusBarViewFactory superStatusBarViewFactory, Resources resources) {
        this.mContext = context;
        this.mWindowManager = windowManager;
        this.mSuperStatusBarViewFactory = superStatusBarViewFactory;
        this.mStatusBarView = superStatusBarViewFactory.getStatusBarWindowView();
        this.mLpChanged = new LayoutParams();
        this.mResources = resources;
        if (this.mBarHeight < 0) {
            this.mBarHeight = resources.getDimensionPixelSize(17105462);
        }
    }

    public int getStatusBarHeight() {
        return this.mBarHeight;
    }

    public void refreshStatusBarHeight() {
        int dimensionPixelSize = this.mResources.getDimensionPixelSize(17105462);
        if (this.mBarHeight != dimensionPixelSize) {
            this.mBarHeight = dimensionPixelSize;
            apply(this.mCurrentState);
        }
    }

    public void attach() {
        LayoutParams layoutParams = new LayoutParams(-1, this.mBarHeight, 2000, -2139095032, -3);
        this.mLp = layoutParams;
        layoutParams.token = new Binder();
        LayoutParams layoutParams2 = this.mLp;
        layoutParams2.gravity = 48;
        layoutParams2.setFitInsetsTypes(0);
        this.mLp.setTitle("StatusBar");
        this.mLp.packageName = this.mContext.getPackageName();
        LayoutParams layoutParams3 = this.mLp;
        layoutParams3.layoutInDisplayCutoutMode = 3;
        this.mWindowManager.addView(this.mStatusBarView, layoutParams3);
        this.mLpChanged.copyFrom(this.mLp);
    }

    public void setForceStatusBarVisible(boolean z) {
        State state = this.mCurrentState;
        state.mForceStatusBarVisible = z;
        apply(state);
    }

    private void applyHeight() {
        this.mLpChanged.height = this.mBarHeight;
    }

    private void apply(State state) {
        applyForceStatusBarVisibleFlag(state);
        applyHeight();
        LayoutParams layoutParams = this.mLp;
        if (layoutParams != null && layoutParams.copyFrom(this.mLpChanged) != 0) {
            this.mWindowManager.updateViewLayout(this.mStatusBarView, this.mLp);
        }
    }

    private void applyForceStatusBarVisibleFlag(State state) {
        if (state.mForceStatusBarVisible) {
            this.mLpChanged.privateFlags |= 4096;
            return;
        }
        this.mLpChanged.privateFlags &= -4097;
    }
}
