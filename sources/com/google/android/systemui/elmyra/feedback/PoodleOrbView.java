package com.google.android.systemui.elmyra.feedback;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class PoodleOrbView extends FrameLayout implements AnimatorListener, FeedbackEffect {
    private ArrayList<ValueAnimator> mAnimations = new ArrayList<>();
    private View mBackground;
    private View mBlue;
    private int mFeedbackHeight;
    private View mGreen;
    private View mRed;
    private int mState = 0;
    private View mYellow;

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }

    public PoodleOrbView(Context context) {
        super(context);
    }

    public PoodleOrbView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PoodleOrbView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public PoodleOrbView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mBackground = findViewById(C2011R$id.elmyra_feedback_background);
        this.mBlue = findViewById(C2011R$id.elmyra_feedback_blue);
        this.mGreen = findViewById(C2011R$id.elmyra_feedback_green);
        this.mRed = findViewById(C2011R$id.elmyra_feedback_red);
        this.mYellow = findViewById(C2011R$id.elmyra_feedback_yellow);
        this.mFeedbackHeight = getResources().getDimensionPixelSize(C2009R$dimen.opa_elmyra_orb_height);
        this.mBackground.setScaleX(0.0f);
        this.mBackground.setScaleY(0.0f);
        View view = this.mBlue;
        view.setTranslationY(view.getTranslationY() + ((float) this.mFeedbackHeight));
        View view2 = this.mGreen;
        view2.setTranslationY(view2.getTranslationY() + ((float) this.mFeedbackHeight));
        View view3 = this.mRed;
        view3.setTranslationY(view3.getTranslationY() + ((float) this.mFeedbackHeight));
        View view4 = this.mYellow;
        view4.setTranslationY(view4.getTranslationY() + ((float) this.mFeedbackHeight));
        this.mAnimations.addAll(Arrays.asList(createBackgroundAnimator(this.mBackground)));
        ((ValueAnimator) this.mAnimations.get(0)).addListener(this);
        Path path = new Path();
        path.moveTo(this.mBlue.getTranslationX(), this.mBlue.getTranslationY() - ((float) this.mFeedbackHeight));
        path.cubicTo(m123px(-32.5f), m123px(-27.5f), m123px(15.0f), m123px(-33.75f), m123px(-2.5f), m123px(-20.0f));
        this.mAnimations.addAll(Arrays.asList(createDotAnimator(this.mBlue, 0.0f, path)));
        Path path2 = new Path();
        path2.moveTo(this.mRed.getTranslationX(), this.mRed.getTranslationY() - ((float) this.mFeedbackHeight));
        path2.cubicTo(m123px(-25.0f), m123px(-17.5f), m123px(-20.0f), m123px(-27.5f), m123px(2.5f), m123px(-20.0f));
        this.mAnimations.addAll(Arrays.asList(createDotAnimator(this.mRed, 0.05f, path2)));
        Path path3 = new Path();
        path3.moveTo(this.mYellow.getTranslationX(), this.mYellow.getTranslationY() - ((float) this.mFeedbackHeight));
        path3.cubicTo(m123px(21.25f), m123px(-33.75f), m123px(15.0f), m123px(-27.5f), m123px(0.0f), m123px(-20.0f));
        this.mAnimations.addAll(Arrays.asList(createDotAnimator(this.mYellow, 0.1f, path3)));
        Path path4 = new Path();
        path4.moveTo(this.mGreen.getTranslationX(), this.mGreen.getTranslationY() - ((float) this.mFeedbackHeight));
        path4.cubicTo(m123px(-27.5f), m123px(-20.0f), m123px(35.0f), m123px(-30.0f), m123px(0.0f), m123px(-20.0f));
        this.mAnimations.addAll(Arrays.asList(createDotAnimator(this.mGreen, 0.2f, path4)));
    }

    public void onProgress(float f, int i) {
        if (this.mState != 3) {
            float f2 = (0.75f * f) + 0.0f;
            Iterator it = this.mAnimations.iterator();
            while (it.hasNext()) {
                ValueAnimator valueAnimator = (ValueAnimator) it.next();
                valueAnimator.cancel();
                valueAnimator.setCurrentFraction(f2);
            }
            if (f == 0.0f) {
                this.mState = 0;
            } else if (f == 1.0f) {
                this.mState = 2;
            } else {
                this.mState = 1;
            }
        }
    }

    public void onRelease() {
        int i = this.mState;
        if (i == 2 || i == 1) {
            Iterator it = this.mAnimations.iterator();
            while (it.hasNext()) {
                ((ValueAnimator) it.next()).reverse();
            }
            this.mState = 0;
        }
    }

    public void onResolve(DetectionProperties detectionProperties) {
        Iterator it = this.mAnimations.iterator();
        while (it.hasNext()) {
            ((ValueAnimator) it.next()).start();
        }
        this.mState = 3;
    }

    public void onAnimationEnd(Animator animator) {
        this.mState = 0;
        onProgress(0.0f, 0);
    }

    /* renamed from: px */
    private float m123px(float f) {
        return TypedValue.applyDimension(1, f, getResources().getDisplayMetrics());
    }

    private ObjectAnimator[] createBackgroundAnimator(View view) {
        Keyframe[] keyframeArr = {Keyframe.ofFloat(0.0f, 0.0f), Keyframe.ofFloat(0.375f, 1.2f), Keyframe.ofFloat(0.75f, 1.2f), Keyframe.ofFloat(0.95f, 0.2f), Keyframe.ofFloat(1.0f, 0.0f)};
        keyframeArr[1].setInterpolator(new OvershootInterpolator());
        ObjectAnimator[] objectAnimatorArr = {ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.SCALE_X, keyframeArr)}), ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.SCALE_Y, keyframeArr)}), ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y, new Keyframe[]{Keyframe.ofFloat(0.0f, view.getTranslationY()), Keyframe.ofFloat(0.375f, m123px(27.5f)), Keyframe.ofFloat(0.75f, m123px(27.5f)), Keyframe.ofFloat(0.95f, m123px(21.75f))})})};
        for (int i = 0; i < 3; i++) {
            objectAnimatorArr[i].setDuration(1000);
        }
        return objectAnimatorArr;
    }

    private Keyframe[][] approximatePath(Path path, float f, float f2) {
        float[] approximate = path.approximate(0.5f);
        Keyframe[] keyframeArr = new Keyframe[(approximate.length / 3)];
        Keyframe[] keyframeArr2 = new Keyframe[(approximate.length / 3)];
        int i = 0;
        int i2 = 0;
        while (i < approximate.length) {
            int i3 = i + 1;
            float f3 = ((f2 - f) * approximate[i]) + f;
            int i4 = i3 + 1;
            keyframeArr[i2] = Keyframe.ofFloat(f3, approximate[i3]);
            int i5 = i4 + 1;
            keyframeArr2[i2] = Keyframe.ofFloat(f3, approximate[i4]);
            i2++;
            i = i5;
        }
        return new Keyframe[][]{keyframeArr, keyframeArr2};
    }

    private ObjectAnimator[] createDotAnimator(View view, float f, Path path) {
        Keyframe[] keyframeArr = {Keyframe.ofFloat(0.0f, view.getScaleX()), Keyframe.ofFloat(0.75f, view.getScaleX()), Keyframe.ofFloat(0.95f, 0.3f), Keyframe.ofFloat(1.0f, 0.0f)};
        Keyframe[] keyframeArr2 = {Keyframe.ofFloat(0.0f, 1.0f), Keyframe.ofFloat(0.75f, 1.0f), Keyframe.ofFloat(0.95f, 0.25f), Keyframe.ofFloat(1.0f, 0.0f)};
        Keyframe[][] approximatePath = approximatePath(path, 0.75f, 1.0f);
        Keyframe[] keyframeArr3 = new Keyframe[(approximatePath[0].length + 2)];
        keyframeArr3[0] = Keyframe.ofFloat(0.0f, view.getTranslationX());
        keyframeArr3[1] = Keyframe.ofFloat(0.75f, view.getTranslationX());
        System.arraycopy(approximatePath[0], 0, keyframeArr3, 2, approximatePath[0].length);
        Keyframe[] keyframeArr4 = new Keyframe[(approximatePath[1].length + 3)];
        keyframeArr4[0] = Keyframe.ofFloat(0.0f, view.getTranslationY());
        keyframeArr4[1] = Keyframe.ofFloat(f, view.getTranslationY());
        keyframeArr4[2] = Keyframe.ofFloat(0.75f, view.getTranslationY() - ((float) this.mFeedbackHeight));
        System.arraycopy(approximatePath[1], 0, keyframeArr4, 3, approximatePath[1].length);
        keyframeArr4[2].setInterpolator(new OvershootInterpolator());
        ObjectAnimator[] objectAnimatorArr = {ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.SCALE_X, keyframeArr)}), ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.SCALE_Y, keyframeArr)}), ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X, keyframeArr3)}), ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y, keyframeArr4)}), ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.ALPHA, keyframeArr2)})};
        for (int i = 0; i < 5; i++) {
            objectAnimatorArr[i].setDuration(1000);
        }
        return objectAnimatorArr;
    }
}
