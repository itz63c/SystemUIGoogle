package com.google.android.systemui.assist.uihints.edgelights.mode;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.SystemClock;
import android.util.MathUtils;
import android.view.animation.PathInterpolator;
import com.android.systemui.C2008R$color;
import com.android.systemui.assist.p003ui.EdgeLight;
import com.android.systemui.assist.p003ui.PerimeterPathGuide;
import com.android.systemui.assist.p003ui.PerimeterPathGuide.Region;
import com.google.android.systemui.assist.uihints.RollingAverage;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightUpdateListener;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView.Mode;

public final class FullListening implements Mode {
    private static final PathInterpolator INTERPOLATOR = new PathInterpolator(0.33f, 0.0f, 0.67f, 1.0f);
    /* access modifiers changed from: private */
    public Animator mAnimator;
    private EdgeLightsView mEdgeLightsView;
    private final boolean mFakeForHalfListening;
    private PerimeterPathGuide mGuide;
    private boolean mLastPerturbationWasEven = false;
    private long mLastSpeechTimestampMs = 0;
    private final EdgeLight[] mLights;
    private RollingAverage mRollingConfidence = new RollingAverage(3);
    private State mState = State.NOT_STARTED;

    private enum State {
        NOT_STARTED,
        EXPANDING_TO_WIDTH,
        WAITING_FOR_SPEECH,
        LISTENING_TO_SPEECH,
        WAITING_FOR_ENDPOINTER
    }

    public int getSubType() {
        return 1;
    }

    public boolean preventsInvocations() {
        return true;
    }

    public FullListening(Context context, boolean z) {
        this.mFakeForHalfListening = z;
        this.mLights = new EdgeLight[]{new EdgeLight(context.getResources().getColor(C2008R$color.edge_light_blue, null), 0.0f, 0.0f), new EdgeLight(context.getResources().getColor(C2008R$color.edge_light_red, null), 0.0f, 0.0f), new EdgeLight(context.getResources().getColor(C2008R$color.edge_light_yellow, null), 0.0f, 0.0f), new EdgeLight(context.getResources().getColor(C2008R$color.edge_light_green, null), 0.0f, 0.0f)};
    }

    public void start(EdgeLightsView edgeLightsView, PerimeterPathGuide perimeterPathGuide, Mode mode) {
        this.mEdgeLightsView = edgeLightsView;
        this.mGuide = perimeterPathGuide;
        this.mState = State.EXPANDING_TO_WIDTH;
        edgeLightsView.setVisibility(0);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(getExpandToWidthDuration(edgeLightsView, mode));
        ofFloat.setInterpolator(INTERPOLATOR);
        ofFloat.addUpdateListener(new EdgeLightUpdateListener(getInitialLights(edgeLightsView, perimeterPathGuide, mode), getFinalLights(), this.mLights, edgeLightsView));
        ofFloat.addListener(createUpdateStateOnEndAnimatorListener());
        setAnimator(ofFloat);
    }

    public void onNewModeRequest(EdgeLightsView edgeLightsView, Mode mode) {
        if (!(mode instanceof FullListening) || ((FullListening) mode).mFakeForHalfListening != this.mFakeForHalfListening) {
            setAnimator(null);
            edgeLightsView.commitModeTransition(mode);
        }
    }

    public boolean isFakeForHalfListening() {
        return this.mFakeForHalfListening;
    }

    private long getExpandToWidthDuration(EdgeLightsView edgeLightsView, Mode mode) {
        if (mode instanceof FullListening) {
            return 0;
        }
        if (mode instanceof FulfillBottom) {
            return 300;
        }
        if (!edgeLightsView.getAssistInvocationLights().isEmpty()) {
            return 0;
        }
        return 500;
    }

    private EdgeLight[] getInitialLights(EdgeLightsView edgeLightsView, PerimeterPathGuide perimeterPathGuide, Mode mode) {
        float f;
        EdgeLight[] copy = EdgeLight.copy(this.mLights);
        EdgeLight[] copy2 = edgeLightsView.getAssistLights() != null ? EdgeLight.copy(edgeLightsView.getAssistLights()) : null;
        boolean z = (mode instanceof FulfillBottom) && copy2 != null && copy.length == copy2.length;
        for (int i = 0; i < copy.length; i++) {
            EdgeLight edgeLight = copy[i];
            if (z) {
                f = copy2[i].getStart();
            } else {
                f = perimeterPathGuide.getRegionCenter(Region.BOTTOM);
            }
            edgeLight.setStart(f);
            edgeLight.setLength(z ? copy2[i].getLength() : 0.0f);
        }
        return copy;
    }

    private EdgeLight[] getFinalLights() {
        EdgeLight[] copy = EdgeLight.copy(this.mLights);
        float regionWidth = this.mGuide.getRegionWidth(Region.BOTTOM) / 4.0f;
        for (int i = 0; i < copy.length; i++) {
            copy[i].setStart(((float) i) * regionWidth);
            copy[i].setLength(regionWidth);
        }
        return copy;
    }

    private void setAnimator(Animator animator) {
        Animator animator2 = this.mAnimator;
        if (animator2 != null) {
            animator2.cancel();
        }
        this.mAnimator = animator;
        if (animator != null) {
            animator.start();
        }
    }

    public void onConfigurationChanged() {
        setAnimator(null);
        float f = 0.0f;
        for (EdgeLight length : this.mLights) {
            f += length.getLength();
        }
        float regionWidth = this.mGuide.getRegionWidth(Region.BOTTOM);
        this.mLights[0].setStart(0.0f);
        EdgeLight[] edgeLightArr = this.mLights;
        edgeLightArr[0].setLength((edgeLightArr[0].getLength() / f) * regionWidth);
        EdgeLight[] edgeLightArr2 = this.mLights;
        edgeLightArr2[1].setStart(edgeLightArr2[0].getStart() + this.mLights[0].getLength());
        EdgeLight[] edgeLightArr3 = this.mLights;
        edgeLightArr3[1].setLength((edgeLightArr3[1].getLength() / f) * regionWidth);
        EdgeLight[] edgeLightArr4 = this.mLights;
        edgeLightArr4[2].setStart(edgeLightArr4[1].getStart() + this.mLights[1].getLength());
        EdgeLight[] edgeLightArr5 = this.mLights;
        edgeLightArr5[2].setLength((edgeLightArr5[2].getLength() / f) * regionWidth);
        EdgeLight[] edgeLightArr6 = this.mLights;
        edgeLightArr6[3].setStart(edgeLightArr6[2].getStart() + this.mLights[2].getLength());
        EdgeLight[] edgeLightArr7 = this.mLights;
        edgeLightArr7[3].setLength((edgeLightArr7[3].getLength() / f) * regionWidth);
        updateStateAndAnimation();
    }

    /* access modifiers changed from: private */
    public void updateStateAndAnimation() {
        int i;
        EdgeLight[] edgeLightArr;
        if (!(this.mRollingConfidence.getAverage() > 0.10000000149011612d)) {
            State state = this.mState;
            if (state == State.LISTENING_TO_SPEECH || state == State.WAITING_FOR_ENDPOINTER) {
                if (this.mAnimator != null) {
                    State state2 = this.mState;
                    if (state2 == State.WAITING_FOR_ENDPOINTER || state2 == State.LISTENING_TO_SPEECH) {
                        return;
                    }
                }
                this.mState = State.WAITING_FOR_ENDPOINTER;
                edgeLightArr = getFinalLights();
                i = 2000;
            } else if (state != State.WAITING_FOR_SPEECH || this.mAnimator == null) {
                this.mState = State.WAITING_FOR_SPEECH;
                edgeLightArr = createPerturbedLights();
                i = 1200;
            } else {
                return;
            }
        } else if (this.mState != State.LISTENING_TO_SPEECH || this.mAnimator == null) {
            this.mState = State.LISTENING_TO_SPEECH;
            edgeLightArr = createPerturbedLights();
            i = (int) MathUtils.lerp(400.0f, 150.0f, (float) this.mRollingConfidence.getAverage());
        } else {
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addListener(createUpdateStateOnEndAnimatorListener());
        ofFloat.setDuration((long) i);
        ofFloat.setInterpolator(INTERPOLATOR);
        ofFloat.addUpdateListener(new EdgeLightUpdateListener(EdgeLight.copy(this.mLights), edgeLightArr, this.mLights, this.mEdgeLightsView));
        setAnimator(ofFloat);
    }

    private EdgeLight[] createPerturbedLights() {
        float regionWidth = this.mGuide.getRegionWidth(Region.BOTTOM);
        float f = this.mState == State.LISTENING_TO_SPEECH ? this.mLastPerturbationWasEven ? 0.39999998f : 0.6f : this.mLastPerturbationWasEven ? 0.49f : 0.51f;
        float f2 = f * regionWidth;
        float f3 = regionWidth / 2.0f;
        float lerp = MathUtils.lerp(Math.min(f3, f2), Math.max(f3, f2), (float) this.mRollingConfidence.getAverage());
        float f4 = regionWidth - lerp;
        this.mLastPerturbationWasEven = !this.mLastPerturbationWasEven;
        double d = this.mState == State.LISTENING_TO_SPEECH ? 0.6d : 0.52d;
        double d2 = this.mState == State.LISTENING_TO_SPEECH ? 0.4d : 0.48d;
        double d3 = d - d2;
        float random = ((float) ((Math.random() * d3) + d2)) * lerp;
        float f5 = lerp - random;
        float random2 = ((float) ((Math.random() * d3) + d2)) * f4;
        float f6 = f4 - random2;
        EdgeLight[] copy = EdgeLight.copy(this.mLights);
        copy[0].setLength(random);
        copy[1].setLength(random2);
        copy[2].setLength(f6);
        copy[3].setLength(f5);
        copy[0].setStart(0.0f);
        copy[1].setStart(random);
        float f7 = random + random2;
        copy[2].setStart(f7);
        copy[3].setStart(f7 + f6);
        return copy;
    }

    private AnimatorListenerAdapter createUpdateStateOnEndAnimatorListener() {
        return new AnimatorListenerAdapter() {
            private boolean mCancelled = false;

            public void onAnimationCancel(Animator animator) {
                super.onAnimationCancel(animator);
                this.mCancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                if (FullListening.this.mAnimator == animator) {
                    FullListening.this.mAnimator = null;
                }
                if (!this.mCancelled) {
                    FullListening.this.updateStateAndAnimation();
                }
            }
        };
    }

    public void onAudioLevelUpdate(float f, float f2) {
        this.mRollingConfidence.add(f);
        this.mLastSpeechTimestampMs = f > 0.1f ? SystemClock.uptimeMillis() : this.mLastSpeechTimestampMs;
        if (this.mState != State.EXPANDING_TO_WIDTH) {
            updateStateAndAnimation();
        }
    }
}
