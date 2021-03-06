package com.android.systemui.power;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import com.android.systemui.C2013R$layout;
import java.util.Objects;

public class InattentiveSleepWarningView extends FrameLayout {
    /* access modifiers changed from: private */
    public boolean mDismissing;
    private Animator mFadeOutAnimator;
    private final WindowManager mWindowManager = ((WindowManager) this.mContext.getSystemService(WindowManager.class));
    private final IBinder mWindowToken = new Binder();

    static /* synthetic */ boolean lambda$new$0(View view, int i, KeyEvent keyEvent) {
        return true;
    }

    InattentiveSleepWarningView(Context context) {
        super(context);
        LayoutInflater.from(this.mContext).inflate(C2013R$layout.inattentive_sleep_warning, this, true);
        setFocusable(true);
        setOnKeyListener(C0941x92c2958e.INSTANCE);
        Animator loadAnimator = AnimatorInflater.loadAnimator(getContext(), 17498113);
        this.mFadeOutAnimator = loadAnimator;
        loadAnimator.setTarget(this);
        this.mFadeOutAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                InattentiveSleepWarningView.this.removeView();
            }

            public void onAnimationCancel(Animator animator) {
                InattentiveSleepWarningView.this.mDismissing = false;
                InattentiveSleepWarningView.this.setAlpha(1.0f);
                InattentiveSleepWarningView.this.setVisibility(0);
            }
        });
    }

    /* access modifiers changed from: private */
    public void removeView() {
        if (this.mDismissing) {
            setVisibility(4);
            this.mWindowManager.removeView(this);
        }
    }

    public void show() {
        if (getParent() != null) {
            if (this.mFadeOutAnimator.isStarted()) {
                this.mFadeOutAnimator.cancel();
            }
            return;
        }
        setAlpha(1.0f);
        setVisibility(0);
        this.mWindowManager.addView(this, getLayoutParams(this.mWindowToken));
    }

    public void dismiss(boolean z) {
        if (getParent() != null) {
            this.mDismissing = true;
            if (z) {
                Animator animator = this.mFadeOutAnimator;
                Objects.requireNonNull(animator);
                postOnAnimation(new Runnable(animator) {
                    public final /* synthetic */ Animator f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        this.f$0.start();
                    }
                });
            } else {
                removeView();
            }
        }
    }

    private LayoutParams getLayoutParams(IBinder iBinder) {
        LayoutParams layoutParams = new LayoutParams(-1, -1, 2038, 256, -3);
        layoutParams.privateFlags |= 16;
        layoutParams.setTitle("InattentiveSleepWarning");
        layoutParams.token = iBinder;
        return layoutParams;
    }
}
