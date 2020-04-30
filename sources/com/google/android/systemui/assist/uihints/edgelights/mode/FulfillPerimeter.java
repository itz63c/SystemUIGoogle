package com.google.android.systemui.assist.uihints.edgelights.mode;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.os.Handler;
import android.view.animation.PathInterpolator;
import com.android.systemui.C2008R$color;
import com.android.systemui.assist.p003ui.EdgeLight;
import com.android.systemui.assist.p003ui.PerimeterPathGuide;
import com.android.systemui.assist.p003ui.PerimeterPathGuide.Region;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView.Mode;

public final class FulfillPerimeter implements Mode {
    private static final PathInterpolator FULFILL_PERIMETER_INTERPOLATOR = new PathInterpolator(0.2f, 0.0f, 0.2f, 1.0f);
    private final EdgeLight mBlueLight;
    /* access modifiers changed from: private */
    public boolean mDisappearing = false;
    private final EdgeLight mGreenLight;
    private final EdgeLight[] mLights;
    /* access modifiers changed from: private */
    public Mode mNextMode;
    private final EdgeLight mRedLight;
    private final EdgeLight mYellowLight;

    public int getSubType() {
        return 4;
    }

    public FulfillPerimeter(Context context) {
        this.mBlueLight = new EdgeLight(context.getResources().getColor(C2008R$color.edge_light_blue, null), 0.0f, 0.0f);
        this.mRedLight = new EdgeLight(context.getResources().getColor(C2008R$color.edge_light_red, null), 0.0f, 0.0f);
        this.mYellowLight = new EdgeLight(context.getResources().getColor(C2008R$color.edge_light_yellow, null), 0.0f, 0.0f);
        EdgeLight edgeLight = new EdgeLight(context.getResources().getColor(C2008R$color.edge_light_green, null), 0.0f, 0.0f);
        this.mGreenLight = edgeLight;
        this.mLights = new EdgeLight[]{this.mBlueLight, this.mRedLight, edgeLight, this.mYellowLight};
    }

    public void onNewModeRequest(EdgeLightsView edgeLightsView, Mode mode) {
        this.mNextMode = mode;
    }

    public void start(EdgeLightsView edgeLightsView, PerimeterPathGuide perimeterPathGuide, Mode mode) {
        final EdgeLightsView edgeLightsView2 = edgeLightsView;
        PerimeterPathGuide perimeterPathGuide2 = perimeterPathGuide;
        boolean z = false;
        edgeLightsView2.setVisibility(0);
        final AnimatorSet animatorSet = new AnimatorSet();
        EdgeLight[] edgeLightArr = this.mLights;
        int length = edgeLightArr.length;
        int i = 0;
        while (i < length) {
            EdgeLight edgeLight = edgeLightArr[i];
            boolean z2 = (edgeLight == this.mBlueLight || edgeLight == this.mRedLight) ? true : z;
            boolean z3 = (edgeLight == this.mRedLight || edgeLight == this.mYellowLight) ? true : z;
            float regionCenter = perimeterPathGuide2.getRegionCenter(Region.BOTTOM);
            float makeClockwise = (z2 ? PerimeterPathGuide.makeClockwise(perimeterPathGuide2.getRegionCenter(Region.TOP)) : regionCenter) - regionCenter;
            float regionCenter2 = perimeterPathGuide2.getRegionCenter(Region.TOP) - perimeterPathGuide2.getRegionCenter(Region.BOTTOM);
            float f = regionCenter2 - 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.setStartDelay(z3 ? 100 : 0);
            ofFloat.setDuration(433);
            ofFloat.setInterpolator(FULFILL_PERIMETER_INTERPOLATOR);
            $$Lambda$FulfillPerimeter$MZtUjbRyns2SZEYMcv6IQbgrRY r11 = r0;
            ValueAnimator valueAnimator = ofFloat;
            EdgeLight edgeLight2 = edgeLight;
            $$Lambda$FulfillPerimeter$MZtUjbRyns2SZEYMcv6IQbgrRY r0 = new AnimatorUpdateListener(edgeLight, makeClockwise, regionCenter, f, 0.0f, edgeLightsView) {
                public final /* synthetic */ EdgeLight f$1;
                public final /* synthetic */ float f$2;
                public final /* synthetic */ float f$3;
                public final /* synthetic */ float f$4;
                public final /* synthetic */ float f$5;
                public final /* synthetic */ EdgeLightsView f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    FulfillPerimeter.this.lambda$start$0$FulfillPerimeter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, valueAnimator);
                }
            };
            ValueAnimator valueAnimator2 = valueAnimator;
            valueAnimator2.addUpdateListener(r11);
            if (!z3) {
                animatorSet.play(valueAnimator2);
            } else {
                float interpolation = valueAnimator2.getInterpolator().getInterpolation(100.0f / ((float) valueAnimator2.getDuration())) * regionCenter2;
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                ofFloat2.setStartDelay(valueAnimator2.getStartDelay() + 100);
                ofFloat2.setDuration(733);
                ofFloat2.setInterpolator(FULFILL_PERIMETER_INTERPOLATOR);
                $$Lambda$FulfillPerimeter$4qfpqiVttSOidi4h0dCycMmHzTE r02 = new AnimatorUpdateListener(edgeLight2, interpolation, perimeterPathGuide, edgeLightsView) {
                    public final /* synthetic */ EdgeLight f$1;
                    public final /* synthetic */ float f$2;
                    public final /* synthetic */ PerimeterPathGuide f$3;
                    public final /* synthetic */ EdgeLightsView f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        FulfillPerimeter.this.lambda$start$1$FulfillPerimeter(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
                    }
                };
                ofFloat2.addUpdateListener(r02);
                animatorSet.play(valueAnimator2);
                animatorSet.play(ofFloat2);
            }
            i++;
            perimeterPathGuide2 = perimeterPathGuide;
            z = false;
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                if (FulfillPerimeter.this.mNextMode == null) {
                    FulfillPerimeter.this.mDisappearing = false;
                    animatorSet.start();
                } else if (FulfillPerimeter.this.mNextMode != null) {
                    new Handler().postDelayed(new Runnable(edgeLightsView2) {
                        public final /* synthetic */ EdgeLightsView f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            C18831.this.lambda$onAnimationEnd$0$FulfillPerimeter$1(this.f$1);
                        }
                    }, 500);
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onAnimationEnd$0 */
            public /* synthetic */ void lambda$onAnimationEnd$0$FulfillPerimeter$1(EdgeLightsView edgeLightsView) {
                edgeLightsView.commitModeTransition(FulfillPerimeter.this.mNextMode);
            }
        });
        animatorSet.start();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$start$0 */
    public /* synthetic */ void lambda$start$0$FulfillPerimeter(EdgeLight edgeLight, float f, float f2, float f3, float f4, EdgeLightsView edgeLightsView, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        edgeLight.setStart((f * animatedFraction) + f2);
        if (!this.mDisappearing) {
            edgeLight.setLength((f3 * animatedFraction) + f4);
        }
        edgeLightsView.setAssistLights(this.mLights);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$start$1 */
    public /* synthetic */ void lambda$start$1$FulfillPerimeter(EdgeLight edgeLight, float f, PerimeterPathGuide perimeterPathGuide, EdgeLightsView edgeLightsView, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        if (animatedFraction != 0.0f) {
            this.mDisappearing = true;
            EdgeLight edgeLight2 = this.mRedLight;
            if (edgeLight == edgeLight2) {
                edgeLight2.setLength(Math.max(((0.0f - f) * animatedFraction) + f, 0.0f));
                EdgeLight edgeLight3 = this.mBlueLight;
                edgeLight3.setLength(Math.abs(edgeLight3.getStart()) - Math.abs(this.mRedLight.getStart()));
            } else {
                EdgeLight edgeLight4 = this.mYellowLight;
                if (edgeLight == edgeLight4) {
                    edgeLight4.setStart((perimeterPathGuide.getRegionCenter(Region.BOTTOM) * 2.0f) - (this.mRedLight.getStart() + this.mRedLight.getLength()));
                    this.mYellowLight.setLength(this.mRedLight.getLength());
                    this.mGreenLight.setStart((perimeterPathGuide.getRegionCenter(Region.BOTTOM) * 2.0f) - (this.mBlueLight.getStart() + this.mBlueLight.getLength()));
                    this.mGreenLight.setLength(this.mBlueLight.getLength());
                }
            }
            edgeLightsView.setAssistLights(this.mLights);
        }
    }
}
