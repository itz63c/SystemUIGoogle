package androidx.leanback.transition;

import android.animation.Animator;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashMap;

class CustomChangeBounds extends ChangeBounds {
    final HashMap<String, Integer> mClassStartDelays = new HashMap<>();
    int mDefaultStartDelay;
    final SparseIntArray mIdStartDelays = new SparseIntArray();
    final HashMap<View, Integer> mViewStartDelays = new HashMap<>();

    CustomChangeBounds() {
    }

    private int getDelay(View view) {
        Integer num = (Integer) this.mViewStartDelays.get(view);
        if (num != null) {
            return num.intValue();
        }
        int i = this.mIdStartDelays.get(view.getId(), -1);
        if (i != -1) {
            return i;
        }
        Integer num2 = (Integer) this.mClassStartDelays.get(view.getClass().getName());
        if (num2 != null) {
            return num2.intValue();
        }
        return this.mDefaultStartDelay;
    }

    public Animator createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
        Animator createAnimator = super.createAnimator(viewGroup, transitionValues, transitionValues2);
        if (!(createAnimator == null || transitionValues2 == null)) {
            View view = transitionValues2.view;
            if (view != null) {
                createAnimator.setStartDelay((long) getDelay(view));
            }
        }
        return createAnimator;
    }
}
