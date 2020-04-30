package com.google.android.systemui.assist;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.util.ArraySet;
import android.view.RenderNodeAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.internal.app.AssistUtils;
import com.android.systemui.C2009R$dimen;

public final class OpaUtils {
    static final Interpolator INTERPOLATOR_40_40 = new PathInterpolator(0.4f, 0.0f, 0.6f, 1.0f);
    static final Interpolator INTERPOLATOR_40_OUT = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);

    static float getDeltaDiamondPositionBottomX() {
        return 0.0f;
    }

    static float getDeltaDiamondPositionLeftY() {
        return 0.0f;
    }

    static float getDeltaDiamondPositionRightY() {
        return 0.0f;
    }

    static float getDeltaDiamondPositionTopX() {
        return 0.0f;
    }

    static Animator getScaleAnimatorX(View view, float f, int i, Interpolator interpolator) {
        RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(3, f);
        renderNodeAnimator.setTarget(view);
        renderNodeAnimator.setInterpolator(interpolator);
        renderNodeAnimator.setDuration((long) i);
        return renderNodeAnimator;
    }

    static Animator getScaleAnimatorY(View view, float f, int i, Interpolator interpolator) {
        RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(4, f);
        renderNodeAnimator.setTarget(view);
        renderNodeAnimator.setInterpolator(interpolator);
        renderNodeAnimator.setDuration((long) i);
        return renderNodeAnimator;
    }

    static Animator getDeltaAnimatorX(View view, Interpolator interpolator, float f, int i) {
        RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(8, view.getX() + f);
        renderNodeAnimator.setTarget(view);
        renderNodeAnimator.setInterpolator(interpolator);
        renderNodeAnimator.setDuration((long) i);
        return renderNodeAnimator;
    }

    static Animator getDeltaAnimatorY(View view, Interpolator interpolator, float f, int i) {
        RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(9, view.getY() + f);
        renderNodeAnimator.setTarget(view);
        renderNodeAnimator.setInterpolator(interpolator);
        renderNodeAnimator.setDuration((long) i);
        return renderNodeAnimator;
    }

    static Animator getTranslationAnimatorX(View view, Interpolator interpolator, int i) {
        RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(0, 0.0f);
        renderNodeAnimator.setTarget(view);
        renderNodeAnimator.setInterpolator(interpolator);
        renderNodeAnimator.setDuration((long) i);
        return renderNodeAnimator;
    }

    static Animator getTranslationAnimatorY(View view, Interpolator interpolator, int i) {
        RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(1, 0.0f);
        renderNodeAnimator.setTarget(view);
        renderNodeAnimator.setInterpolator(interpolator);
        renderNodeAnimator.setDuration((long) i);
        return renderNodeAnimator;
    }

    static ObjectAnimator getAlphaObjectAnimator(View view, float f, int i, int i2, Interpolator interpolator) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{f});
        ofFloat.setInterpolator(interpolator);
        ofFloat.setDuration((long) i);
        ofFloat.setStartDelay((long) i2);
        return ofFloat;
    }

    static Animator getAlphaAnimator(View view, float f, int i, Interpolator interpolator) {
        return getAlphaAnimator(view, f, i, 0, interpolator);
    }

    static Animator getAlphaAnimator(View view, float f, int i, int i2, Interpolator interpolator) {
        RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(11, f);
        renderNodeAnimator.setTarget(view);
        renderNodeAnimator.setInterpolator(interpolator);
        renderNodeAnimator.setDuration((long) i);
        renderNodeAnimator.setStartDelay((long) i2);
        return renderNodeAnimator;
    }

    static Animator getLongestAnim(ArraySet<Animator> arraySet) {
        long j = Long.MIN_VALUE;
        Animator animator = null;
        for (int size = arraySet.size() - 1; size >= 0; size--) {
            Animator animator2 = (Animator) arraySet.valueAt(size);
            if (animator2.getTotalDuration() > j) {
                j = animator2.getTotalDuration();
                animator = animator2;
            }
        }
        return animator;
    }

    static ObjectAnimator getScaleObjectAnimator(View view, float f, int i, Interpolator interpolator) {
        ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(View.SCALE_X, new float[]{f}), PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[]{f})});
        ofPropertyValuesHolder.setDuration((long) i);
        ofPropertyValuesHolder.setInterpolator(interpolator);
        return ofPropertyValuesHolder;
    }

    static ObjectAnimator getTranslationObjectAnimatorY(View view, Interpolator interpolator, float f, float f2, int i) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.Y, new float[]{f2, f2 + f});
        ofFloat.setInterpolator(interpolator);
        ofFloat.setDuration((long) i);
        return ofFloat;
    }

    static ObjectAnimator getTranslationObjectAnimatorX(View view, Interpolator interpolator, float f, float f2, int i) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.X, new float[]{f2, f2 + f});
        ofFloat.setInterpolator(interpolator);
        ofFloat.setDuration((long) i);
        return ofFloat;
    }

    static float getPxVal(Resources resources, int i) {
        return (float) resources.getDimensionPixelOffset(i);
    }

    static boolean isAGSACurrentAssistant(Context context) {
        ComponentName assistComponentForUser = new AssistUtils(context).getAssistComponentForUser(-2);
        if (assistComponentForUser != null) {
            if ("com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService".equals(assistComponentForUser.flattenToString())) {
                return true;
            }
        }
        return false;
    }

    static float getDeltaDiamondPositionTopY(Resources resources) {
        return -getPxVal(resources, C2009R$dimen.opa_diamond_translation);
    }

    static float getDeltaDiamondPositionLeftX(Resources resources) {
        return -getPxVal(resources, C2009R$dimen.opa_diamond_translation);
    }

    static float getDeltaDiamondPositionRightX(Resources resources) {
        return getPxVal(resources, C2009R$dimen.opa_diamond_translation);
    }

    static float getDeltaDiamondPositionBottomY(Resources resources) {
        return getPxVal(resources, C2009R$dimen.opa_diamond_translation);
    }
}
