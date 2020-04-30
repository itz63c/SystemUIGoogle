package com.android.systemui.pip.phone;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.Interpolators;
import com.android.systemui.shared.system.WindowManagerWrapper;

public class PipDismissViewController {
    private Context mContext;
    /* access modifiers changed from: private */
    public View mDismissView;
    private View mTargetView;
    /* access modifiers changed from: private */
    public WindowManager mWindowManager;
    private Point mWindowSize;

    public PipDismissViewController(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
    }

    public void createDismissTarget() {
        if (this.mDismissView == null) {
            Rect rect = new Rect();
            WindowManagerWrapper.getInstance().getStableInsets(rect);
            this.mWindowSize = new Point();
            this.mWindowManager.getDefaultDisplay().getRealSize(this.mWindowSize);
            int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.pip_dismiss_gradient_height);
            int dimensionPixelSize2 = this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.pip_dismiss_text_bottom_margin);
            this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.bubble_dismiss_slop);
            View inflate = LayoutInflater.from(this.mContext).inflate(C2013R$layout.pip_dismiss_view, null);
            this.mDismissView = inflate;
            inflate.setSystemUiVisibility(256);
            this.mDismissView.forceHasOverlappingRendering(false);
            Drawable drawable = this.mContext.getResources().getDrawable(C2010R$drawable.pip_dismiss_scrim);
            drawable.setAlpha(216);
            this.mDismissView.setBackground(drawable);
            View findViewById = this.mDismissView.findViewById(C2011R$id.pip_dismiss_text);
            this.mTargetView = findViewById;
            LayoutParams layoutParams = (LayoutParams) findViewById.getLayoutParams();
            layoutParams.bottomMargin = rect.bottom + dimensionPixelSize2;
            this.mTargetView.setLayoutParams(layoutParams);
            WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(-1, dimensionPixelSize, 0, this.mWindowSize.y - dimensionPixelSize, 2024, 280, -3);
            layoutParams2.setTitle("pip-dismiss-overlay");
            layoutParams2.privateFlags |= 16;
            layoutParams2.gravity = 49;
            layoutParams2.setFitInsetsTypes(0);
            this.mWindowManager.addView(this.mDismissView, layoutParams2);
        }
        this.mDismissView.animate().cancel();
    }

    public void showDismissTarget() {
        this.mDismissView.animate().alpha(1.0f).setInterpolator(Interpolators.LINEAR).setStartDelay(100).setDuration(350).start();
    }

    public void destroyDismissTarget() {
        View view = this.mDismissView;
        if (view != null) {
            view.animate().alpha(0.0f).setInterpolator(Interpolators.LINEAR).setStartDelay(0).setDuration(225).withEndAction(new Runnable() {
                public void run() {
                    PipDismissViewController.this.mWindowManager.removeViewImmediate(PipDismissViewController.this.mDismissView);
                    PipDismissViewController.this.mDismissView = null;
                }
            }).start();
        }
    }
}
