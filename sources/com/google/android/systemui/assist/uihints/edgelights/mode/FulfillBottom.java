package com.google.android.systemui.assist.uihints.edgelights.mode;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.MathUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import com.android.systemui.C2008R$color;
import com.android.systemui.assist.p003ui.EdgeLight;
import com.android.systemui.assist.p003ui.PerimeterPathGuide;
import com.android.systemui.assist.p003ui.PerimeterPathGuide.Region;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightUpdateListener;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView.Mode;
import java.util.Random;

public final class FulfillBottom implements Mode {
    private static final PathInterpolator CRADLE_INTERPOLATOR = new PathInterpolator(0.4f, 0.0f, 0.6f, 1.0f);
    private static final LinearInterpolator EXIT_FADE_INTERPOLATOR = new LinearInterpolator();
    private static final PathInterpolator EXIT_TO_CORNER_INTERPOLATOR = new PathInterpolator(0.1f, 0.0f, 0.5f, 1.0f);
    private EdgeLight mBlueLight;
    private AnimatorSet mCradleAnimations = new AnimatorSet();
    /* access modifiers changed from: private */
    public EdgeLightsView mEdgeLightsView = null;
    private AnimatorSet mExitAnimations = new AnimatorSet();
    private EdgeLight mGreenLight;
    private PerimeterPathGuide mGuide = null;
    private final boolean mIsListening;
    private EdgeLight[] mLightsArray;
    /* access modifiers changed from: private */
    public Mode mNextMode = null;
    private final Random mRandom = new Random();
    private EdgeLight mRedLight;
    private final Resources mResources;
    private boolean mSwingLeft = false;
    private EdgeLight mYellowLight;

    public int getSubType() {
        return 3;
    }

    public FulfillBottom(Context context, boolean z) {
        this.mResources = context.getResources();
        this.mIsListening = z;
    }

    public boolean isListening() {
        return this.mIsListening;
    }

    public void onNewModeRequest(EdgeLightsView edgeLightsView, Mode mode) {
        this.mNextMode = mode;
        if (this.mCradleAnimations.isRunning()) {
            this.mCradleAnimations.cancel();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("got mode ");
        sb.append(mode.getClass().getSimpleName());
        Log.v("FulfillBottom", sb.toString());
        if (!(mode instanceof Gone)) {
            if (this.mExitAnimations.isRunning()) {
                this.mExitAnimations.cancel();
            }
            this.mEdgeLightsView.commitModeTransition(this.mNextMode);
        } else if (!this.mExitAnimations.isRunning()) {
            animateExit();
        }
    }

    public void start(EdgeLightsView edgeLightsView, PerimeterPathGuide perimeterPathGuide, Mode mode) {
        this.mEdgeLightsView = edgeLightsView;
        this.mGuide = perimeterPathGuide;
        edgeLightsView.setVisibility(0);
        EdgeLight[] assistLights = edgeLightsView.getAssistLights();
        if (((mode instanceof FullListening) || (mode instanceof FulfillBottom)) && assistLights.length == 4) {
            this.mBlueLight = assistLights[0];
            this.mRedLight = assistLights[1];
            this.mYellowLight = assistLights[2];
            this.mGreenLight = assistLights[3];
        } else {
            this.mBlueLight = new EdgeLight(this.mResources.getColor(C2008R$color.edge_light_blue, null), 0.0f, 0.0f);
            this.mRedLight = new EdgeLight(this.mResources.getColor(C2008R$color.edge_light_red, null), 0.0f, 0.0f);
            this.mYellowLight = new EdgeLight(this.mResources.getColor(C2008R$color.edge_light_yellow, null), 0.0f, 0.0f);
            this.mGreenLight = new EdgeLight(this.mResources.getColor(C2008R$color.edge_light_green, null), 0.0f, 0.0f);
        }
        this.mLightsArray = new EdgeLight[]{this.mBlueLight, this.mRedLight, this.mYellowLight, this.mGreenLight};
        this.mSwingLeft = mode instanceof FulfillBottom ? ((FulfillBottom) mode).swingingToLeft() : this.mRandom.nextBoolean();
        animateCradle();
    }

    public void onConfigurationChanged() {
        if (this.mNextMode == null) {
            start(this.mEdgeLightsView, this.mGuide, this);
            return;
        }
        if (this.mExitAnimations.isRunning()) {
            this.mExitAnimations.cancel();
        }
        onNewModeRequest(this.mEdgeLightsView, this.mNextMode);
    }

    private void setRelativePoints(float f, float f2, float f3) {
        float regionWidth = this.mGuide.getRegionWidth(Region.BOTTOM);
        float f4 = f * regionWidth;
        this.mBlueLight.setEndpoints(0.0f, f4);
        float f5 = f2 * regionWidth;
        this.mRedLight.setEndpoints(f4, f5);
        float f6 = f3 * regionWidth;
        this.mYellowLight.setEndpoints(f5, f6);
        this.mGreenLight.setEndpoints(f6, regionWidth);
        this.mEdgeLightsView.setAssistLights(this.mLightsArray);
    }

    private void animateCradle() {
        float regionWidth = this.mGuide.getRegionWidth(Region.BOTTOM);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addUpdateListener(new AnimatorUpdateListener(this.mBlueLight.getEnd() / regionWidth, this.mRedLight.getEnd() / regionWidth, this.mYellowLight.getEnd() / regionWidth) {
            public final /* synthetic */ float f$1;
            public final /* synthetic */ float f$2;
            public final /* synthetic */ float f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                FulfillBottom.this.lambda$animateCradle$0$FulfillBottom(this.f$1, this.f$2, this.f$3, valueAnimator);
            }
        });
        ofFloat.setDuration(1000);
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat2.addUpdateListener(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                FulfillBottom.this.lambda$animateCradle$1$FulfillBottom(valueAnimator);
            }
        });
        ofFloat2.setDuration(1300);
        ofFloat2.setInterpolator(CRADLE_INTERPOLATOR);
        ofFloat2.setRepeatMode(2);
        ofFloat2.setRepeatCount(-1);
        AnimatorSet animatorSet = new AnimatorSet();
        this.mCradleAnimations = animatorSet;
        animatorSet.playSequentially(new Animator[]{ofFloat, ofFloat2});
        this.mCradleAnimations.start();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateCradle$0 */
    public /* synthetic */ void lambda$animateCradle$0$FulfillBottom(float f, float f2, float f3, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        setRelativePoints(MathUtils.lerp(f, this.mSwingLeft ? 0.69f : 0.035f, animatedFraction), MathUtils.lerp(f2, this.mSwingLeft ? 0.87f : 0.13f, animatedFraction), MathUtils.lerp(f3, this.mSwingLeft ? 0.965f : 0.31f, animatedFraction));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateCradle$1 */
    public /* synthetic */ void lambda$animateCradle$1$FulfillBottom(ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        if (!this.mSwingLeft) {
            animatedFraction = 1.0f - animatedFraction;
        }
        setRelativePoints(MathUtils.lerp(0.69f, 0.035f, animatedFraction), MathUtils.lerp(0.87f, 0.13f, animatedFraction), MathUtils.lerp(0.965f, 0.31f, animatedFraction));
    }

    private boolean swingingToLeft() {
        return this.mSwingLeft;
    }

    private void animateExit() {
        ValueAnimator createToCornersAnimator = createToCornersAnimator();
        ValueAnimator createFadeOutAnimator = createFadeOutAnimator();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(createToCornersAnimator);
        animatorSet.play(createFadeOutAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            private boolean mCancelled = false;

            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                FulfillBottom.this.mEdgeLightsView.setVisibility(8);
                if (FulfillBottom.this.mNextMode != null && !this.mCancelled) {
                    FulfillBottom.this.mEdgeLightsView.commitModeTransition(FulfillBottom.this.mNextMode);
                }
            }

            public void onAnimationCancel(Animator animator) {
                super.onAnimationCancel(animator);
                this.mCancelled = true;
            }
        });
        this.mExitAnimations = animatorSet;
        animatorSet.start();
    }

    private ValueAnimator createToCornersAnimator() {
        EdgeLight[] copy = EdgeLight.copy(this.mLightsArray);
        EdgeLight[] copy2 = EdgeLight.copy(this.mLightsArray);
        float regionWidth = this.mGuide.getRegionWidth(Region.BOTTOM_LEFT) * 0.8f;
        float f = -1.0f * regionWidth;
        float regionWidth2 = this.mGuide.getRegionWidth(Region.BOTTOM);
        copy2[0].setEndpoints(f, f);
        copy2[1].setEndpoints(f, f);
        float f2 = regionWidth2 + regionWidth;
        copy2[2].setEndpoints(f2, f2);
        copy2[3].setEndpoints(f2, f2);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setInterpolator(EXIT_TO_CORNER_INTERPOLATOR);
        ofFloat.setDuration(350);
        ofFloat.addUpdateListener(new EdgeLightUpdateListener(copy, copy2, this.mLightsArray, this.mEdgeLightsView));
        return ofFloat;
    }

    private ValueAnimator createFadeOutAnimator() {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        ofFloat.setInterpolator(EXIT_FADE_INTERPOLATOR);
        ofFloat.setDuration(350);
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                FulfillBottom.this.lambda$createFadeOutAnimator$2$FulfillBottom(valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                FulfillBottom.this.mEdgeLightsView.setAssistLights(new EdgeLight[0]);
                FulfillBottom.this.mEdgeLightsView.setAlpha(1.0f);
            }
        });
        return ofFloat;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createFadeOutAnimator$2 */
    public /* synthetic */ void lambda$createFadeOutAnimator$2$FulfillBottom(ValueAnimator valueAnimator) {
        this.mEdgeLightsView.setAlpha(1.0f - valueAnimator.getAnimatedFraction());
    }
}
