package com.android.systemui.assist.p003ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.metrics.LogMaker;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.C2013R$layout;
import com.android.systemui.Dependency;
import com.android.systemui.assist.AssistHandleViewController;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.assist.AssistManager.UiController;
import com.android.systemui.statusbar.NavigationBarController;
import java.util.Locale;

/* renamed from: com.android.systemui.assist.ui.DefaultUiController */
public class DefaultUiController implements UiController {
    private static final boolean VERBOSE = (Build.TYPE.toLowerCase(Locale.ROOT).contains("debug") || Build.TYPE.toLowerCase(Locale.ROOT).equals("eng"));
    private boolean mAttached = false;
    private ValueAnimator mInvocationAnimator = new ValueAnimator();
    /* access modifiers changed from: private */
    public boolean mInvocationInProgress = false;
    protected InvocationLightsView mInvocationLightsView;
    /* access modifiers changed from: private */
    public float mLastInvocationProgress = 0.0f;
    private final LayoutParams mLayoutParams;
    private final PathInterpolator mProgressInterpolator = new PathInterpolator(0.83f, 0.0f, 0.84f, 1.0f);
    protected final FrameLayout mRoot;
    private final WindowManager mWindowManager;

    public DefaultUiController(Context context) {
        this.mRoot = new FrameLayout(context);
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        LayoutParams layoutParams = new LayoutParams(-1, -2, 0, 0, 2024, 808, -3);
        this.mLayoutParams = layoutParams;
        layoutParams.privateFlags = 64;
        layoutParams.gravity = 80;
        layoutParams.setFitInsetsTypes(0);
        this.mLayoutParams.setTitle("Assist");
        InvocationLightsView invocationLightsView = (InvocationLightsView) LayoutInflater.from(context).inflate(C2013R$layout.invocation_lights, this.mRoot, false);
        this.mInvocationLightsView = invocationLightsView;
        this.mRoot.addView(invocationLightsView);
    }

    public void onInvocationProgress(int i, float f) {
        boolean z = this.mInvocationInProgress;
        if (f == 1.0f) {
            animateInvocationCompletion(i, 0.0f);
        } else if (f == 0.0f) {
            hide();
        } else {
            if (!z) {
                attach();
                this.mInvocationInProgress = true;
                updateAssistHandleVisibility();
            }
            setProgressInternal(i, f);
        }
        this.mLastInvocationProgress = f;
        logInvocationProgressMetrics(i, f, z);
    }

    public void onGestureCompletion(float f) {
        animateInvocationCompletion(1, f);
        logInvocationProgressMetrics(1, 1.0f, this.mInvocationInProgress);
    }

    public void hide() {
        detach();
        if (this.mInvocationAnimator.isRunning()) {
            this.mInvocationAnimator.cancel();
        }
        this.mInvocationLightsView.hide();
        this.mInvocationInProgress = false;
        updateAssistHandleVisibility();
    }

    /* access modifiers changed from: protected */
    public void logInvocationProgressMetrics(int i, float f, boolean z) {
        String str = "DefaultUiController";
        if (f == 1.0f && VERBOSE) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invocation complete: type=");
            sb.append(i);
            Log.v(str, sb.toString());
        }
        if (!z && f > 0.0f) {
            if (VERBOSE) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Invocation started: type=");
                sb2.append(i);
                Log.v(str, sb2.toString());
            }
            MetricsLogger.action(new LogMaker(1716).setType(4).setSubtype(((AssistManager) Dependency.get(AssistManager.class)).toLoggingSubType(i)));
        }
        ValueAnimator valueAnimator = this.mInvocationAnimator;
        if ((valueAnimator == null || !valueAnimator.isRunning()) && z && f == 0.0f) {
            if (VERBOSE) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Invocation cancelled: type=");
                sb3.append(i);
                Log.v(str, sb3.toString());
            }
            MetricsLogger.action(new LogMaker(1716).setType(5).setSubtype(1));
        }
    }

    private void updateAssistHandleVisibility() {
        AssistHandleViewController assistHandleViewController;
        NavigationBarController navigationBarController = (NavigationBarController) Dependency.get(NavigationBarController.class);
        if (navigationBarController == null) {
            assistHandleViewController = null;
        } else {
            assistHandleViewController = navigationBarController.getAssistHandlerViewController();
        }
        if (assistHandleViewController != null) {
            assistHandleViewController.lambda$setAssistHintBlocked$1(this.mInvocationInProgress);
        }
    }

    private void attach() {
        if (!this.mAttached) {
            this.mWindowManager.addView(this.mRoot, this.mLayoutParams);
            this.mAttached = true;
        }
    }

    private void detach() {
        if (this.mAttached) {
            this.mWindowManager.removeViewImmediate(this.mRoot);
            this.mAttached = false;
        }
    }

    private void setProgressInternal(int i, float f) {
        this.mInvocationLightsView.onInvocationProgress(this.mProgressInterpolator.getInterpolation(f));
    }

    private void animateInvocationCompletion(int i, float f) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.mLastInvocationProgress, 1.0f});
        this.mInvocationAnimator = ofFloat;
        ofFloat.setStartDelay(1);
        this.mInvocationAnimator.setDuration(200);
        this.mInvocationAnimator.addUpdateListener(new AnimatorUpdateListener(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                DefaultUiController.this.lambda$animateInvocationCompletion$0$DefaultUiController(this.f$1, valueAnimator);
            }
        });
        this.mInvocationAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                DefaultUiController.this.mInvocationInProgress = false;
                DefaultUiController.this.mLastInvocationProgress = 0.0f;
                DefaultUiController.this.hide();
            }
        });
        this.mInvocationAnimator.start();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateInvocationCompletion$0 */
    public /* synthetic */ void lambda$animateInvocationCompletion$0$DefaultUiController(int i, ValueAnimator valueAnimator) {
        setProgressInternal(i, ((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}
