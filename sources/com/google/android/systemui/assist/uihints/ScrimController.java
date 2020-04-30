package com.google.android.systemui.assist.uihints;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import android.util.MathUtils;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.CardInfoListener;
import com.google.android.systemui.assist.uihints.TranscriptionController.State;
import com.google.android.systemui.assist.uihints.TranscriptionController.TranscriptionSpaceListener;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsListener;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView.Mode;
import com.google.android.systemui.assist.uihints.edgelights.mode.FullListening;
import com.google.android.systemui.assist.uihints.input.TouchInsideRegion;
import java.util.Optional;

public class ScrimController implements TranscriptionSpaceListener, CardInfoListener, EdgeLightsListener, TouchInsideRegion {
    private static final LinearInterpolator ALPHA_INTERPOLATOR = new LinearInterpolator();
    private ValueAnimator mAlphaAnimator = new ValueAnimator();
    private boolean mCardForcesScrimGone = false;
    private boolean mCardTransitionAnimated = false;
    private boolean mCardVisible = false;
    private boolean mHaveAccurateLightness = false;
    private boolean mInFullListening = false;
    private float mInvocationProgress = 0.0f;
    private boolean mIsDozing = false;
    private final LightnessProvider mLightnessProvider;
    private float mMedianLightness;
    private final OverlappedElementController mOverlappedElement;
    private final View mScrimView;
    private boolean mTranscriptionVisible = false;
    private VisibilityListener mVisibilityListener;

    public ScrimController(ViewGroup viewGroup, OverlappedElementController overlappedElementController, LightnessProvider lightnessProvider, TouchInsideHandler touchInsideHandler) {
        View findViewById = viewGroup.findViewById(C2011R$id.scrim);
        this.mScrimView = findViewById;
        findViewById.setBackgroundTintBlendMode(BlendMode.SRC_IN);
        this.mLightnessProvider = lightnessProvider;
        this.mScrimView.setOnClickListener(touchInsideHandler);
        this.mScrimView.setOnTouchListener(touchInsideHandler);
        this.mOverlappedElement = overlappedElementController;
    }

    public void onCardInfo(boolean z, int i, boolean z2, boolean z3) {
        this.mCardVisible = z;
        this.mCardTransitionAnimated = z2;
        this.mCardForcesScrimGone = z3;
        refresh();
    }

    public void onModeStarted(Mode mode) {
        this.mInFullListening = mode instanceof FullListening;
        refresh();
    }

    public void onStateChanged(State state, State state2) {
        boolean z = state2 != State.NONE;
        if (this.mTranscriptionVisible != z) {
            this.mTranscriptionVisible = z;
            refresh();
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isVisible() {
        return this.mScrimView.getVisibility() == 0;
    }

    /* access modifiers changed from: 0000 */
    public void setVisibilityListener(VisibilityListener visibilityListener) {
        this.mVisibilityListener = visibilityListener;
    }

    public Optional<Region> getTouchInsideRegion() {
        if (!isVisible()) {
            return Optional.empty();
        }
        Rect rect = new Rect();
        this.mScrimView.getHitRect(rect);
        rect.top = rect.bottom - this.mScrimView.getResources().getDimensionPixelSize(C2009R$dimen.scrim_touchable_height);
        return Optional.of(new Region(rect));
    }

    /* access modifiers changed from: 0000 */
    public SurfaceControl getSurfaceControllerHandle() {
        if (this.mScrimView.getViewRootImpl() == null) {
            return null;
        }
        return this.mScrimView.getViewRootImpl().getSurfaceControl();
    }

    /* access modifiers changed from: 0000 */
    public void setInvocationProgress(float f) {
        float constrain = MathUtils.constrain(f, 0.0f, 1.0f);
        if (this.mInvocationProgress != constrain) {
            this.mInvocationProgress = constrain;
            refresh();
        }
    }

    /* access modifiers changed from: 0000 */
    public void setIsDozing(boolean z) {
        this.mIsDozing = z;
        refresh();
    }

    /* access modifiers changed from: 0000 */
    public void setHasMedianLightness(float f) {
        this.mHaveAccurateLightness = true;
        this.mMedianLightness = f;
        refresh();
    }

    /* access modifiers changed from: 0000 */
    public void onLightnessInvalidated() {
        this.mHaveAccurateLightness = false;
        refresh();
    }

    private void refresh() {
        if (!this.mHaveAccurateLightness || this.mIsDozing) {
            setRelativeAlpha(0.0f, false);
        } else if (this.mCardVisible && this.mCardForcesScrimGone) {
            setRelativeAlpha(0.0f, this.mCardTransitionAnimated);
        } else if (this.mInFullListening || this.mTranscriptionVisible) {
            if (!this.mCardVisible || isVisible()) {
                setRelativeAlpha(1.0f, false);
            }
        } else if (this.mCardVisible) {
            setRelativeAlpha(0.0f, this.mCardTransitionAnimated);
        } else {
            float f = this.mInvocationProgress;
            if (f > 0.0f) {
                setRelativeAlpha(Math.min(1.0f, f), false);
            } else {
                setRelativeAlpha(0.0f, true);
            }
        }
    }

    private void setRelativeAlpha(float f, boolean z) {
        if (!this.mHaveAccurateLightness && f > 0.0f) {
            return;
        }
        if (f < 0.0f || f > 1.0f) {
            StringBuilder sb = new StringBuilder();
            sb.append("Got unexpected alpha: ");
            sb.append(f);
            sb.append(", ignoring");
            Log.e("ScrimController", sb.toString());
            return;
        }
        if (this.mAlphaAnimator.isRunning()) {
            this.mAlphaAnimator.cancel();
        }
        if (f > 0.0f) {
            if (this.mScrimView.getVisibility() != 0) {
                this.mScrimView.setBackgroundTintList(ColorStateList.valueOf(this.mMedianLightness <= 0.4f ? -16777216 : -1));
                setVisibility(0);
            }
            if (z) {
                ValueAnimator createRelativeAlphaAnimator = createRelativeAlphaAnimator(f);
                this.mAlphaAnimator = createRelativeAlphaAnimator;
                createRelativeAlphaAnimator.start();
            } else {
                setAlpha(f);
            }
        } else if (z) {
            ValueAnimator createRelativeAlphaAnimator2 = createRelativeAlphaAnimator(f);
            this.mAlphaAnimator = createRelativeAlphaAnimator2;
            createRelativeAlphaAnimator2.addListener(new AnimatorListenerAdapter() {
                private boolean mCancelled = false;

                public void onAnimationCancel(Animator animator) {
                    super.onAnimationCancel(animator);
                    this.mCancelled = true;
                }

                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    if (!this.mCancelled) {
                        ScrimController.this.setVisibility(8);
                    }
                }
            });
            this.mAlphaAnimator.start();
        } else {
            setAlpha(0.0f);
            setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void setVisibility(int i) {
        if (i != this.mScrimView.getVisibility()) {
            this.mScrimView.setVisibility(i);
            VisibilityListener visibilityListener = this.mVisibilityListener;
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChanged(i);
            }
            this.mLightnessProvider.setMuted(i == 0);
            View view = this.mScrimView;
            view.setBackground(i == 0 ? view.getContext().getDrawable(C2010R$drawable.scrim_strip) : null);
            if (i != 0) {
                this.mOverlappedElement.setAlpha(1.0f);
                refresh();
            }
        }
    }

    private void setAlpha(float f) {
        this.mScrimView.setAlpha(f);
        this.mOverlappedElement.setAlpha(1.0f - f);
    }

    private ValueAnimator createRelativeAlphaAnimator(float f) {
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{this.mScrimView.getAlpha(), f}).setDuration((long) (Math.abs(f - this.mScrimView.getAlpha()) * 300.0f));
        duration.setInterpolator(ALPHA_INTERPOLATOR);
        duration.addUpdateListener(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ScrimController.this.lambda$createRelativeAlphaAnimator$0$ScrimController(valueAnimator);
            }
        });
        return duration;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createRelativeAlphaAnimator$0 */
    public /* synthetic */ void lambda$createRelativeAlphaAnimator$0$ScrimController(ValueAnimator valueAnimator) {
        setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}
