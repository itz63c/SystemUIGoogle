package com.google.android.systemui.assist.uihints;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import android.util.MathUtils;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.Dependency;
import com.android.systemui.assist.p003ui.EdgeLight;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.AudioInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.CardInfoListener;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsListener;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView.Mode;
import com.google.android.systemui.assist.uihints.edgelights.mode.FulfillBottom;
import com.google.android.systemui.assist.uihints.edgelights.mode.FullListening;
import com.google.android.systemui.assist.uihints.edgelights.mode.Gone;
import com.google.android.systemui.assist.uihints.input.TouchInsideRegion;
import java.util.Optional;

public final class GlowController implements AudioInfoListener, CardInfoListener, EdgeLightsListener, TouchInsideRegion {
    /* access modifiers changed from: private */
    public ValueAnimator mAnimator = null;
    private boolean mCardVisible = false;
    private final Context mContext;
    private EdgeLight[] mEdgeLights = null;
    private Mode mEdgeLightsMode = null;
    private final GlowView mGlowView;
    private int mGlowsY = 0;
    private int mGlowsYDestination = 0;
    private boolean mInvocationCompleting = false;
    private float mMedianLightness;
    private RollingAverage mSpeechRolling = new RollingAverage(3);
    private VisibilityListener mVisibilityListener;

    private enum GlowState {
        SHORT_DARK_BACKGROUND,
        SHORT_LIGHT_BACKGROUND,
        TALL_DARK_BACKGROUND,
        TALL_LIGHT_BACKGROUND,
        GONE
    }

    private float getGlowWidthToViewWidth() {
        return 0.55f;
    }

    private long getMaxYAnimationDuration() {
        return 400;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$GlowController(int i) {
    }

    GlowController(Context context, ViewGroup viewGroup, TouchInsideHandler touchInsideHandler) {
        this.mContext = context;
        ((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(new ModeChangedListener() {
            public final void onNavigationModeChanged(int i) {
                GlowController.this.lambda$new$0$GlowController(i);
            }
        });
        GlowView glowView = (GlowView) viewGroup.findViewById(C2011R$id.glow);
        this.mGlowView = glowView;
        int i = this.mGlowsY;
        glowView.setGlowsY(i, i, null);
        this.mGlowView.setOnClickListener(touchInsideHandler);
        this.mGlowView.setOnTouchListener(touchInsideHandler);
        this.mGlowView.setGlowsY(getMinTranslationY(), getMinTranslationY(), null);
        this.mGlowView.setGlowWidthRatio(getGlowWidthToViewWidth());
    }

    public void onAudioInfo(float f, float f2) {
        this.mSpeechRolling.add(f2);
        maybeAnimateForSpeechConfidence();
    }

    public void onCardInfo(boolean z, int i, boolean z2, boolean z3) {
        this.mCardVisible = z;
    }

    public void onModeStarted(Mode mode) {
        boolean z = mode instanceof Gone;
        if (!z || this.mEdgeLightsMode != null) {
            this.mInvocationCompleting = !z;
            this.mEdgeLightsMode = mode;
            if (z) {
                this.mSpeechRolling = new RollingAverage(3);
            }
            animateGlowTranslationY(getMinTranslationY());
            return;
        }
        this.mEdgeLightsMode = mode;
    }

    public void onAssistLightsUpdated(Mode mode, EdgeLight[] edgeLightArr) {
        int i;
        if (!getTranslationYProportionalToEdgeLights()) {
            this.mEdgeLights = null;
            this.mGlowView.distributeEvenly();
            return;
        }
        this.mEdgeLights = edgeLightArr;
        if ((!this.mInvocationCompleting || !(mode instanceof Gone)) && (mode instanceof FullListening)) {
            if (edgeLightArr == null || edgeLightArr.length != 4) {
                StringBuilder sb = new StringBuilder();
                sb.append("Expected 4 lights, have ");
                if (edgeLightArr == null) {
                    i = 0;
                } else {
                    i = edgeLightArr.length;
                }
                sb.append(i);
                Log.e("GlowController", sb.toString());
            } else {
                maybeAnimateForSpeechConfidence();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setVisibilityListener(VisibilityListener visibilityListener) {
        this.mVisibilityListener = visibilityListener;
    }

    /* access modifiers changed from: 0000 */
    public void setInvocationProgress(float f) {
        if (this.mEdgeLightsMode instanceof Gone) {
            setVisibility(f > 0.0f ? 0 : 8);
            this.mGlowView.setBlurRadius(getInvocationBlurRadius(f));
            int invocationTranslationY = getInvocationTranslationY(f);
            this.mGlowsY = invocationTranslationY;
            this.mGlowsYDestination = invocationTranslationY;
            this.mGlowView.setGlowsY(invocationTranslationY, invocationTranslationY, null);
            this.mGlowView.distributeEvenly();
        }
    }

    /* access modifiers changed from: 0000 */
    public void setMedianLightness(float f) {
        this.mGlowView.setGlowsBlendMode(f <= 0.4f ? PorterDuff.Mode.LIGHTEN : PorterDuff.Mode.SRC_OVER);
        this.mMedianLightness = f;
    }

    public Optional<Region> getTouchInsideRegion() {
        if (this.mGlowView.getVisibility() != 0) {
            return Optional.empty();
        }
        Rect rect = new Rect();
        this.mGlowView.getBoundsOnScreen(rect);
        rect.top = rect.bottom - getMaxTranslationY();
        return Optional.of(new Region(rect));
    }

    public boolean isVisible() {
        return this.mGlowView.getVisibility() == 0;
    }

    private boolean shouldAnimateForSpeechConfidence() {
        Mode mode = this.mEdgeLightsMode;
        boolean z = false;
        if (!(mode instanceof FullListening) && !(mode instanceof FulfillBottom)) {
            return false;
        }
        if (this.mSpeechRolling.getAverage() >= 0.30000001192092896d || this.mGlowsYDestination > getMinTranslationY()) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void maybeAnimateForSpeechConfidence() {
        if (shouldAnimateForSpeechConfidence()) {
            animateGlowTranslationY((int) MathUtils.lerp((float) getMinTranslationY(), (float) getMaxTranslationY(), (float) this.mSpeechRolling.getAverage()));
        }
    }

    /* access modifiers changed from: private */
    public GlowState getState() {
        GlowState glowState;
        Mode mode = this.mEdgeLightsMode;
        boolean z = true;
        boolean z2 = (mode instanceof FulfillBottom) && !((FulfillBottom) mode).isListening();
        Mode mode2 = this.mEdgeLightsMode;
        if ((mode2 instanceof Gone) || mode2 == null || z2) {
            return GlowState.GONE;
        }
        boolean z3 = this.mCardVisible;
        if (this.mMedianLightness >= 0.4f) {
            z = false;
        }
        if (z) {
            glowState = GlowState.TALL_DARK_BACKGROUND;
        } else {
            glowState = GlowState.TALL_LIGHT_BACKGROUND;
        }
        return glowState;
    }

    private int getInvocationBlurRadius(float f) {
        return (int) MathUtils.lerp((float) getBlurRadius(), (float) this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.glow_tall_blur), Math.min(1.0f, f * 5.0f));
    }

    private int getInvocationTranslationY(float f) {
        return (int) MathUtils.min((int) MathUtils.lerp((float) getMinTranslationY(), (float) this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.glow_tall_min_y), f), this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.glow_invocation_max));
    }

    private int getBlurRadius() {
        if (getState() == GlowState.GONE) {
            return this.mGlowView.getBlurRadius();
        }
        if (getState() == GlowState.SHORT_DARK_BACKGROUND || getState() == GlowState.SHORT_LIGHT_BACKGROUND) {
            return this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.glow_short_blur);
        }
        if (getState() == GlowState.TALL_DARK_BACKGROUND || getState() == GlowState.TALL_LIGHT_BACKGROUND) {
            return this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.glow_tall_blur);
        }
        return 0;
    }

    private int getMinTranslationY() {
        if (getState() == GlowState.SHORT_DARK_BACKGROUND || getState() == GlowState.SHORT_LIGHT_BACKGROUND) {
            return this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.glow_short_min_y);
        }
        if (getState() == GlowState.TALL_DARK_BACKGROUND || getState() == GlowState.TALL_LIGHT_BACKGROUND) {
            return this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.glow_tall_min_y);
        }
        return this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.glow_gone_min_y);
    }

    private int getMaxTranslationY() {
        if (getState() == GlowState.SHORT_DARK_BACKGROUND || getState() == GlowState.SHORT_LIGHT_BACKGROUND) {
            return this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.glow_short_max_y);
        }
        if (getState() == GlowState.TALL_DARK_BACKGROUND || getState() == GlowState.TALL_LIGHT_BACKGROUND) {
            return this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.glow_tall_max_y);
        }
        return this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.glow_gone_max_y);
    }

    private boolean getTranslationYProportionalToEdgeLights() {
        return this.mEdgeLightsMode instanceof FullListening;
    }

    private long getYAnimationDuration(float f) {
        return (long) Math.min((float) getMaxYAnimationDuration(), Math.abs(f) / ((float) (((long) Math.abs(getMaxTranslationY() - getMinTranslationY())) / getMaxYAnimationDuration())));
    }

    private void animateGlowTranslationY(int i) {
        animateGlowTranslationY(i, getYAnimationDuration((float) (i - this.mGlowsY)));
    }

    private void animateGlowTranslationY(int i, long j) {
        if (i == this.mGlowsYDestination) {
            this.mGlowView.setGlowsY(this.mGlowsY, getMinTranslationY(), getTranslationYProportionalToEdgeLights() ? this.mEdgeLights : null);
            return;
        }
        this.mGlowsYDestination = i;
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{this.mGlowsY, i});
        this.mAnimator = ofInt;
        ofInt.addUpdateListener(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GlowController.this.lambda$animateGlowTranslationY$1$GlowController(valueAnimator);
            }
        });
        this.mAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                GlowController.this.mAnimator = null;
                if (GlowState.GONE.equals(GlowController.this.getState())) {
                    GlowController.this.setVisibility(8);
                } else {
                    GlowController.this.maybeAnimateForSpeechConfidence();
                }
            }
        });
        this.mAnimator.setInterpolator(new LinearInterpolator());
        this.mAnimator.setDuration(j);
        this.mAnimator.addUpdateListener(new AnimatorUpdateListener(this.mGlowView.getBlurRadius(), getBlurRadius()) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GlowController.this.lambda$animateGlowTranslationY$2$GlowController(this.f$1, this.f$2, valueAnimator);
            }
        });
        float glowWidthRatio = this.mGlowView.getGlowWidthRatio();
        this.mGlowView.setGlowWidthRatio(glowWidthRatio + ((getGlowWidthToViewWidth() - glowWidthRatio) * 1.0f));
        if (this.mGlowView.getVisibility() != 0) {
            setVisibility(0);
        }
        this.mAnimator.start();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateGlowTranslationY$1 */
    public /* synthetic */ void lambda$animateGlowTranslationY$1$GlowController(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.mGlowsY = intValue;
        this.mGlowView.setGlowsY(intValue, getMinTranslationY(), getTranslationYProportionalToEdgeLights() ? this.mEdgeLights : null);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateGlowTranslationY$2 */
    public /* synthetic */ void lambda$animateGlowTranslationY$2$GlowController(int i, int i2, ValueAnimator valueAnimator) {
        this.mGlowView.setBlurRadius((int) MathUtils.lerp((float) i, (float) i2, valueAnimator.getAnimatedFraction()));
    }

    /* access modifiers changed from: private */
    public void setVisibility(int i) {
        this.mGlowView.setVisibility(i);
        if ((i == 0) != isVisible()) {
            VisibilityListener visibilityListener = this.mVisibilityListener;
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChanged(i);
            }
            if (!isVisible()) {
                this.mGlowView.clearCaches();
            }
        }
    }
}
