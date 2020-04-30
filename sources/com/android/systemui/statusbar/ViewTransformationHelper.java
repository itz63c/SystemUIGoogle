package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import com.android.systemui.C2011R$id;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.TransformState;
import com.android.systemui.statusbar.notification.TransformState.TransformInfo;
import java.util.Stack;

public class ViewTransformationHelper implements TransformableView, TransformInfo {
    private static final int TAG_CONTAINS_TRANSFORMED_VIEW = C2011R$id.contains_transformed_view;
    private ArrayMap<Integer, CustomTransformation> mCustomTransformations = new ArrayMap<>();
    private ArraySet<Integer> mKeysTransformingToSimilar = new ArraySet<>();
    private ArrayMap<Integer, View> mTransformedViews = new ArrayMap<>();
    /* access modifiers changed from: private */
    public ValueAnimator mViewTransformationAnimation;

    public static abstract class CustomTransformation {
        public boolean customTransformTarget(TransformState transformState, TransformState transformState2) {
            return false;
        }

        public Interpolator getCustomInterpolator(int i, boolean z) {
            return null;
        }

        public boolean initTransformation(TransformState transformState, TransformState transformState2) {
            return false;
        }

        public abstract boolean transformFrom(TransformState transformState, TransformableView transformableView, float f);

        public abstract boolean transformTo(TransformState transformState, TransformableView transformableView, float f);
    }

    public void addTransformedView(int i, View view) {
        this.mTransformedViews.put(Integer.valueOf(i), view);
    }

    public void addViewTransformingToSimilar(int i, View view) {
        addTransformedView(i, view);
        this.mKeysTransformingToSimilar.add(Integer.valueOf(i));
    }

    public void reset() {
        this.mTransformedViews.clear();
        this.mKeysTransformingToSimilar.clear();
    }

    public void setCustomTransformation(CustomTransformation customTransformation, int i) {
        this.mCustomTransformations.put(Integer.valueOf(i), customTransformation);
    }

    public TransformState getCurrentState(int i) {
        View view = (View) this.mTransformedViews.get(Integer.valueOf(i));
        if (view == null || view.getVisibility() == 8) {
            return null;
        }
        TransformState createFrom = TransformState.createFrom(view, this);
        if (this.mKeysTransformingToSimilar.contains(Integer.valueOf(i))) {
            createFrom.setIsSameAsAnyView(true);
        }
        return createFrom;
    }

    public void transformTo(final TransformableView transformableView, final Runnable runnable) {
        ValueAnimator valueAnimator = this.mViewTransformationAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mViewTransformationAnimation = ofFloat;
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewTransformationHelper.this.transformTo(transformableView, valueAnimator.getAnimatedFraction());
            }
        });
        this.mViewTransformationAnimation.setInterpolator(Interpolators.LINEAR);
        this.mViewTransformationAnimation.setDuration(360);
        this.mViewTransformationAnimation.addListener(new AnimatorListenerAdapter() {
            public boolean mCancelled;

            public void onAnimationEnd(Animator animator) {
                if (!this.mCancelled) {
                    Runnable runnable = runnable;
                    if (runnable != null) {
                        runnable.run();
                    }
                    ViewTransformationHelper.this.setVisible(false);
                    ViewTransformationHelper.this.mViewTransformationAnimation = null;
                    return;
                }
                ViewTransformationHelper.this.abortTransformations();
            }

            public void onAnimationCancel(Animator animator) {
                this.mCancelled = true;
            }
        });
        this.mViewTransformationAnimation.start();
    }

    public void transformTo(TransformableView transformableView, float f) {
        for (Integer num : this.mTransformedViews.keySet()) {
            TransformState currentState = getCurrentState(num.intValue());
            if (currentState != null) {
                CustomTransformation customTransformation = (CustomTransformation) this.mCustomTransformations.get(num);
                if (customTransformation == null || !customTransformation.transformTo(currentState, transformableView, f)) {
                    TransformState currentState2 = transformableView.getCurrentState(num.intValue());
                    if (currentState2 != null) {
                        currentState.transformViewTo(currentState2, f);
                        currentState2.recycle();
                    } else {
                        currentState.disappear(f, transformableView);
                    }
                    currentState.recycle();
                } else {
                    currentState.recycle();
                }
            }
        }
    }

    public void transformFrom(final TransformableView transformableView) {
        ValueAnimator valueAnimator = this.mViewTransformationAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mViewTransformationAnimation = ofFloat;
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewTransformationHelper.this.transformFrom(transformableView, valueAnimator.getAnimatedFraction());
            }
        });
        this.mViewTransformationAnimation.addListener(new AnimatorListenerAdapter() {
            public boolean mCancelled;

            public void onAnimationEnd(Animator animator) {
                if (!this.mCancelled) {
                    ViewTransformationHelper.this.setVisible(true);
                } else {
                    ViewTransformationHelper.this.abortTransformations();
                }
            }

            public void onAnimationCancel(Animator animator) {
                this.mCancelled = true;
            }
        });
        this.mViewTransformationAnimation.setInterpolator(Interpolators.LINEAR);
        this.mViewTransformationAnimation.setDuration(360);
        this.mViewTransformationAnimation.start();
    }

    public void transformFrom(TransformableView transformableView, float f) {
        for (Integer num : this.mTransformedViews.keySet()) {
            TransformState currentState = getCurrentState(num.intValue());
            if (currentState != null) {
                CustomTransformation customTransformation = (CustomTransformation) this.mCustomTransformations.get(num);
                if (customTransformation == null || !customTransformation.transformFrom(currentState, transformableView, f)) {
                    TransformState currentState2 = transformableView.getCurrentState(num.intValue());
                    if (currentState2 != null) {
                        currentState.transformViewFrom(currentState2, f);
                        currentState2.recycle();
                    } else {
                        currentState.appear(f, transformableView);
                    }
                    currentState.recycle();
                } else {
                    currentState.recycle();
                }
            }
        }
    }

    public void setVisible(boolean z) {
        ValueAnimator valueAnimator = this.mViewTransformationAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        for (Integer intValue : this.mTransformedViews.keySet()) {
            TransformState currentState = getCurrentState(intValue.intValue());
            if (currentState != null) {
                currentState.setVisible(z, false);
                currentState.recycle();
            }
        }
    }

    /* access modifiers changed from: private */
    public void abortTransformations() {
        for (Integer intValue : this.mTransformedViews.keySet()) {
            TransformState currentState = getCurrentState(intValue.intValue());
            if (currentState != null) {
                currentState.abortTransformation();
                currentState.recycle();
            }
        }
    }

    public void addRemainingTransformTypes(View view) {
        int i = TAG_CONTAINS_TRANSFORMED_VIEW;
        int size = this.mTransformedViews.size();
        for (int i2 = 0; i2 < size; i2++) {
            Object valueAt = this.mTransformedViews.valueAt(i2);
            while (true) {
                View view2 = (View) valueAt;
                if (view2 == view.getParent()) {
                    break;
                }
                view2.setTag(i, Boolean.TRUE);
                valueAt = view2.getParent();
            }
        }
        Stack stack = new Stack();
        stack.push(view);
        while (!stack.isEmpty()) {
            View view3 = (View) stack.pop();
            if (((Boolean) view3.getTag(i)) == null) {
                int id = view3.getId();
                if (id != -1) {
                    addTransformedView(id, view3);
                }
            }
            view3.setTag(i, null);
            if ((view3 instanceof ViewGroup) && !this.mTransformedViews.containsValue(view3)) {
                ViewGroup viewGroup = (ViewGroup) view3;
                for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
                    stack.push(viewGroup.getChildAt(i3));
                }
            }
        }
    }

    public void resetTransformedView(View view) {
        TransformState createFrom = TransformState.createFrom(view, this);
        createFrom.setVisible(true, true);
        createFrom.recycle();
    }

    public ArraySet<View> getAllTransformingViews() {
        return new ArraySet<>(this.mTransformedViews.values());
    }

    public boolean isAnimating() {
        ValueAnimator valueAnimator = this.mViewTransformationAnimation;
        return valueAnimator != null && valueAnimator.isRunning();
    }
}
