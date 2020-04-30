package androidx.leanback.transition;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import androidx.leanback.R$id;
import androidx.leanback.widget.Parallax;

public class ParallaxTransition extends Visibility {
    static Interpolator sInterpolator = new LinearInterpolator();

    public ParallaxTransition() {
    }

    public ParallaxTransition(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: 0000 */
    public Animator createAnimator(View view) {
        final Parallax parallax = (Parallax) view.getTag(R$id.lb_parallax_source);
        if (parallax == null) {
            return null;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setInterpolator(sInterpolator);
        ofFloat.addUpdateListener(new AnimatorUpdateListener(this) {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                parallax.updateValues();
            }
        });
        return ofFloat;
    }

    public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
        if (transitionValues2 == null) {
            return null;
        }
        return createAnimator(view);
    }

    public Animator onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
        if (transitionValues == null) {
            return null;
        }
        return createAnimator(view);
    }
}
