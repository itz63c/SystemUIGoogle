package com.android.systemui.bubbles.animation;

import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.DynamicAnimation.ViewProperty;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2012R$integer;
import com.android.systemui.Interpolators;
import com.android.systemui.util.magnetictarget.MagnetizedObject;
import com.android.systemui.util.magnetictarget.MagnetizedObject.MagneticTarget;
import com.google.android.collect.Sets;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Set;

public class ExpandedAnimationController extends PhysicsAnimationController {
    private Runnable mAfterCollapse;
    private Runnable mAfterExpand;
    private boolean mAnimatingCollapse = false;
    private boolean mAnimatingExpand = false;
    private boolean mBubbleDraggedOutEnough = false;
    private float mBubblePaddingTop;
    /* access modifiers changed from: private */
    public float mBubbleSizePx;
    private int mBubblesMaxRendered;
    private PointF mCollapsePoint;
    private Point mDisplaySize;
    private int mExpandedViewPadding;
    private MagnetizedObject<View> mMagnetizedBubbleDraggingOut;
    private int mScreenOrientation;
    private float mSpaceBetweenBubbles;
    private boolean mSpringToTouchOnNextMotionEvent = false;
    private boolean mSpringingBubbleToTouch = false;
    private float mStackOffsetPx;
    private float mStatusBarHeight;

    /* access modifiers changed from: 0000 */
    public int getNextAnimationInChain(ViewProperty viewProperty, int i) {
        return -1;
    }

    /* access modifiers changed from: 0000 */
    public float getOffsetForChainedPropertyAnimation(ViewProperty viewProperty) {
        return 0.0f;
    }

    public ExpandedAnimationController(Point point, int i, int i2) {
        updateOrientation(i2, point);
        this.mExpandedViewPadding = i;
    }

    public void expandFromStack(Runnable runnable) {
        this.mAnimatingCollapse = false;
        this.mAnimatingExpand = true;
        this.mAfterExpand = runnable;
        startOrUpdatePathAnimation(true);
    }

    public void collapseBackToStack(PointF pointF, Runnable runnable) {
        this.mAnimatingExpand = false;
        this.mAnimatingCollapse = true;
        this.mAfterCollapse = runnable;
        this.mCollapsePoint = pointF;
        startOrUpdatePathAnimation(false);
    }

    public void updateOrientation(int i, Point point) {
        this.mScreenOrientation = i;
        this.mDisplaySize = point;
        PhysicsAnimationLayout physicsAnimationLayout = this.mLayout;
        if (physicsAnimationLayout != null) {
            Resources resources = physicsAnimationLayout.getContext().getResources();
            this.mBubblePaddingTop = (float) resources.getDimensionPixelSize(C2009R$dimen.bubble_padding_top);
            this.mStatusBarHeight = (float) resources.getDimensionPixelSize(17105462);
        }
    }

    private void startOrUpdatePathAnimation(boolean z) {
        Runnable runnable;
        if (z) {
            runnable = new Runnable() {
                public final void run() {
                    ExpandedAnimationController.this.lambda$startOrUpdatePathAnimation$0$ExpandedAnimationController();
                }
            };
        } else {
            runnable = new Runnable() {
                public final void run() {
                    ExpandedAnimationController.this.lambda$startOrUpdatePathAnimation$1$ExpandedAnimationController();
                }
            };
        }
        animationsForChildrenFromIndex(0, new ChildAnimationConfigurator(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void configureAnimationForChildAtIndex(int i, PhysicsPropertyAnimator physicsPropertyAnimator) {
                ExpandedAnimationController.this.lambda$startOrUpdatePathAnimation$2$ExpandedAnimationController(this.f$1, i, physicsPropertyAnimator);
            }
        }).startAll(runnable);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startOrUpdatePathAnimation$0 */
    public /* synthetic */ void lambda$startOrUpdatePathAnimation$0$ExpandedAnimationController() {
        this.mAnimatingExpand = false;
        Runnable runnable = this.mAfterExpand;
        if (runnable != null) {
            runnable.run();
        }
        this.mAfterExpand = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startOrUpdatePathAnimation$1 */
    public /* synthetic */ void lambda$startOrUpdatePathAnimation$1$ExpandedAnimationController() {
        this.mAnimatingCollapse = false;
        Runnable runnable = this.mAfterCollapse;
        if (runnable != null) {
            runnable.run();
        }
        this.mAfterCollapse = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startOrUpdatePathAnimation$2 */
    public /* synthetic */ void lambda$startOrUpdatePathAnimation$2$ExpandedAnimationController(boolean z, int i, PhysicsPropertyAnimator physicsPropertyAnimator) {
        int i2;
        View childAt = this.mLayout.getChildAt(i);
        Path path = new Path();
        path.moveTo(childAt.getTranslationX(), childAt.getTranslationY());
        float expandedY = getExpandedY();
        if (z) {
            path.lineTo(childAt.getTranslationX(), expandedY);
            path.lineTo(getBubbleLeft(i), expandedY);
        } else {
            float f = this.mCollapsePoint.x + ((this.mLayout.isFirstChildXLeftOfCenter(this.mCollapsePoint.x) ? -1.0f : 1.0f) * ((float) i) * this.mStackOffsetPx);
            path.lineTo(f, expandedY);
            path.lineTo(f, this.mCollapsePoint.y);
        }
        if ((z && !this.mLayout.isFirstChildXLeftOfCenter(childAt.getTranslationX())) || (!z && this.mLayout.isFirstChildXLeftOfCenter(this.mCollapsePoint.x))) {
            i2 = i * 10;
        } else {
            i2 = (this.mLayout.getChildCount() - i) * 10;
        }
        physicsPropertyAnimator.followAnimatedTargetAlongPath(path, 175, Interpolators.LINEAR, new Runnable[0]);
        physicsPropertyAnimator.withStartDelay((long) i2);
        physicsPropertyAnimator.withStiffness(1000.0f);
    }

    public void onUnstuckFromTarget() {
        this.mSpringToTouchOnNextMotionEvent = true;
    }

    public void prepareForBubbleDrag(View view, MagneticTarget magneticTarget) {
        this.mLayout.cancelAnimationsOnView(view);
        view.setTranslationZ(32767.0f);
        final View view2 = view;
        C07931 r1 = new MagnetizedObject<View>(this.mLayout.getContext(), view, DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y) {
            public float getWidth(View view) {
                return ExpandedAnimationController.this.mBubbleSizePx;
            }

            public float getHeight(View view) {
                return ExpandedAnimationController.this.mBubbleSizePx;
            }

            public void getLocationOnScreen(View view, int[] iArr) {
                iArr[0] = (int) view2.getTranslationX();
                iArr[1] = (int) view2.getTranslationY();
            }
        };
        this.mMagnetizedBubbleDraggingOut = r1;
        r1.addTarget(magneticTarget);
        this.mMagnetizedBubbleDraggingOut.setHapticsEnabled(true);
        this.mMagnetizedBubbleDraggingOut.setFlingToTargetMinVelocity(6000.0f);
    }

    private void springBubbleTo(View view, float f, float f2) {
        PhysicsPropertyAnimator animationForChild = animationForChild(view);
        animationForChild.translationX(f, new Runnable[0]);
        animationForChild.translationY(f2, new Runnable[0]);
        animationForChild.withStiffness(10000.0f);
        animationForChild.start(new Runnable[0]);
    }

    public void dragBubbleOut(View view, float f, float f2) {
        boolean z = true;
        if (this.mSpringToTouchOnNextMotionEvent) {
            springBubbleTo((View) this.mMagnetizedBubbleDraggingOut.getUnderlyingObject(), f, f2);
            this.mSpringToTouchOnNextMotionEvent = false;
            this.mSpringingBubbleToTouch = true;
        } else if (this.mSpringingBubbleToTouch) {
            if (this.mLayout.arePropertiesAnimatingOnView(view, DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y)) {
                springBubbleTo((View) this.mMagnetizedBubbleDraggingOut.getUnderlyingObject(), f, f2);
            } else {
                this.mSpringingBubbleToTouch = false;
            }
        }
        if (!this.mSpringingBubbleToTouch && !this.mMagnetizedBubbleDraggingOut.getObjectStuckToTarget()) {
            view.setTranslationX(f);
            view.setTranslationY(f2);
        }
        if (f2 <= getExpandedY() + this.mBubbleSizePx && f2 >= getExpandedY() - this.mBubbleSizePx) {
            z = false;
        }
        if (z != this.mBubbleDraggedOutEnough) {
            updateBubblePositions();
            this.mBubbleDraggedOutEnough = z;
        }
    }

    public void dismissDraggedOutBubble(View view, Runnable runnable) {
        PhysicsPropertyAnimator animationForChild = animationForChild(view);
        animationForChild.withStiffness(10000.0f);
        animationForChild.scaleX(1.1f, new Runnable[0]);
        animationForChild.scaleY(1.1f, new Runnable[0]);
        animationForChild.alpha(0.0f, runnable);
        animationForChild.start(new Runnable[0]);
        updateBubblePositions();
    }

    public View getDraggedOutBubble() {
        MagnetizedObject<View> magnetizedObject = this.mMagnetizedBubbleDraggingOut;
        if (magnetizedObject == null) {
            return null;
        }
        return (View) magnetizedObject.getUnderlyingObject();
    }

    public MagnetizedObject<View> getMagnetizedBubbleDraggingOut() {
        return this.mMagnetizedBubbleDraggingOut;
    }

    public void snapBubbleBack(View view, float f, float f2) {
        int indexOfChild = this.mLayout.indexOfChild(view);
        PhysicsPropertyAnimator animationForChildAtIndex = animationForChildAtIndex(indexOfChild);
        animationForChildAtIndex.position(getBubbleLeft(indexOfChild), getExpandedY(), new Runnable[0]);
        animationForChildAtIndex.withPositionStartVelocities(f, f2);
        animationForChildAtIndex.start(new Runnable(view) {
            public final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.setTranslationZ(0.0f);
            }
        });
        this.mMagnetizedBubbleDraggingOut = null;
        updateBubblePositions();
    }

    public void onGestureFinished() {
        this.mBubbleDraggedOutEnough = false;
        updateBubblePositions();
    }

    public void updateYPosition(Runnable runnable) {
        if (this.mLayout != null) {
            animationsForChildrenFromIndex(0, new ChildAnimationConfigurator() {
                public final void configureAnimationForChildAtIndex(int i, PhysicsPropertyAnimator physicsPropertyAnimator) {
                    ExpandedAnimationController.this.lambda$updateYPosition$4$ExpandedAnimationController(i, physicsPropertyAnimator);
                }
            }).startAll(runnable);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateYPosition$4 */
    public /* synthetic */ void lambda$updateYPosition$4$ExpandedAnimationController(int i, PhysicsPropertyAnimator physicsPropertyAnimator) {
        physicsPropertyAnimator.translationY(getExpandedY(), new Runnable[0]);
    }

    public float getExpandedY() {
        PhysicsAnimationLayout physicsAnimationLayout = this.mLayout;
        float f = 0.0f;
        if (physicsAnimationLayout == null || physicsAnimationLayout.getRootWindowInsets() == null) {
            return 0.0f;
        }
        WindowInsets rootWindowInsets = this.mLayout.getRootWindowInsets();
        float f2 = this.mBubblePaddingTop;
        float f3 = this.mStatusBarHeight;
        if (rootWindowInsets.getDisplayCutout() != null) {
            f = (float) rootWindowInsets.getDisplayCutout().getSafeInsetTop();
        }
        return f2 + Math.max(f3, f);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("ExpandedAnimationController state:");
        printWriter.print("  isActive:          ");
        printWriter.println(isActiveController());
        printWriter.print("  animatingExpand:   ");
        printWriter.println(this.mAnimatingExpand);
        printWriter.print("  animatingCollapse: ");
        printWriter.println(this.mAnimatingCollapse);
        printWriter.print("  springingBubble:   ");
        printWriter.println(this.mSpringingBubbleToTouch);
    }

    /* access modifiers changed from: 0000 */
    public void onActiveControllerForLayout(PhysicsAnimationLayout physicsAnimationLayout) {
        Resources resources = physicsAnimationLayout.getResources();
        this.mStackOffsetPx = (float) resources.getDimensionPixelSize(C2009R$dimen.bubble_stack_offset);
        this.mBubblePaddingTop = (float) resources.getDimensionPixelSize(C2009R$dimen.bubble_padding_top);
        this.mBubbleSizePx = (float) resources.getDimensionPixelSize(C2009R$dimen.individual_bubble_size);
        this.mStatusBarHeight = (float) resources.getDimensionPixelSize(17105462);
        this.mBubblesMaxRendered = resources.getInteger(C2012R$integer.bubbles_max_rendered);
        float widthForDisplayingBubbles = getWidthForDisplayingBubbles() - ((float) (this.mExpandedViewPadding * 2));
        int i = this.mBubblesMaxRendered;
        this.mSpaceBetweenBubbles = (widthForDisplayingBubbles - (((float) (i + 1)) * this.mBubbleSizePx)) / ((float) i);
        this.mLayout.setVisibility(0);
        animationsForChildrenFromIndex(0, C0781x64c2867e.INSTANCE).startAll(new Runnable[0]);
    }

    static /* synthetic */ void lambda$onActiveControllerForLayout$5(int i, PhysicsPropertyAnimator physicsPropertyAnimator) {
        physicsPropertyAnimator.scaleX(1.0f, new Runnable[0]);
        physicsPropertyAnimator.scaleY(1.0f, new Runnable[0]);
        physicsPropertyAnimator.alpha(1.0f, new Runnable[0]);
    }

    /* access modifiers changed from: 0000 */
    public Set<ViewProperty> getAnimatedProperties() {
        return Sets.newHashSet(new ViewProperty[]{DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y, DynamicAnimation.SCALE_X, DynamicAnimation.SCALE_Y, DynamicAnimation.ALPHA});
    }

    /* access modifiers changed from: 0000 */
    public SpringForce getSpringForce(ViewProperty viewProperty, View view) {
        SpringForce springForce = new SpringForce();
        springForce.setDampingRatio(0.75f);
        springForce.setStiffness(200.0f);
        return springForce;
    }

    /* access modifiers changed from: 0000 */
    public void onChildAdded(View view, int i) {
        if (this.mAnimatingExpand) {
            startOrUpdatePathAnimation(true);
        } else if (this.mAnimatingCollapse) {
            startOrUpdatePathAnimation(false);
        } else {
            view.setTranslationX(getBubbleLeft(i));
            PhysicsPropertyAnimator animationForChild = animationForChild(view);
            animationForChild.translationY(getExpandedY() - (this.mBubbleSizePx * 4.0f), getExpandedY(), new Runnable[0]);
            animationForChild.start(new Runnable[0]);
            updateBubblePositions();
        }
    }

    /* access modifiers changed from: 0000 */
    public void onChildRemoved(View view, int i, Runnable runnable) {
        PhysicsPropertyAnimator animationForChild = animationForChild(view);
        if (view.equals(getDraggedOutBubble())) {
            this.mMagnetizedBubbleDraggingOut = null;
            runnable.run();
        } else {
            animationForChild.alpha(0.0f, runnable);
            animationForChild.withStiffness(10000.0f);
            animationForChild.withDampingRatio(1.0f);
            animationForChild.scaleX(1.1f, new Runnable[0]);
            animationForChild.scaleY(1.1f, new Runnable[0]);
            animationForChild.start(new Runnable[0]);
        }
        updateBubblePositions();
    }

    /* access modifiers changed from: 0000 */
    public void onChildReordered(View view, int i, int i2) {
        updateBubblePositions();
        if (this.mAnimatingCollapse) {
            startOrUpdatePathAnimation(false);
        }
    }

    private void updateBubblePositions() {
        if (!this.mAnimatingExpand && !this.mAnimatingCollapse) {
            int i = 0;
            while (i < this.mLayout.getChildCount()) {
                View childAt = this.mLayout.getChildAt(i);
                if (!childAt.equals(getDraggedOutBubble())) {
                    PhysicsPropertyAnimator animationForChild = animationForChild(childAt);
                    animationForChild.translationX(getBubbleLeft(i), new Runnable[0]);
                    animationForChild.start(new Runnable[0]);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public float getBubbleLeft(int i) {
        return getRowLeft() + (((float) i) * (this.mBubbleSizePx + this.mSpaceBetweenBubbles));
    }

    public float getWidthForDisplayingBubbles() {
        float availableScreenWidth = getAvailableScreenWidth(true);
        return this.mScreenOrientation == 2 ? Math.max((float) this.mDisplaySize.y, availableScreenWidth * 0.66f) : availableScreenWidth;
    }

    private float getAvailableScreenWidth(boolean z) {
        int i;
        int i2;
        float f = (float) this.mDisplaySize.x;
        PhysicsAnimationLayout physicsAnimationLayout = this.mLayout;
        WindowInsets rootWindowInsets = physicsAnimationLayout != null ? physicsAnimationLayout.getRootWindowInsets() : null;
        if (rootWindowInsets == null) {
            return f;
        }
        DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
        int i3 = 0;
        if (displayCutout != null) {
            i = displayCutout.getSafeInsetLeft();
            i2 = displayCutout.getSafeInsetRight();
        } else {
            i2 = 0;
            i = 0;
        }
        int stableInsetLeft = z ? rootWindowInsets.getStableInsetLeft() : 0;
        if (z) {
            i3 = rootWindowInsets.getStableInsetRight();
        }
        return (f - ((float) Math.max(stableInsetLeft, i))) - ((float) Math.max(i3, i2));
    }

    private float getRowLeft() {
        PhysicsAnimationLayout physicsAnimationLayout = this.mLayout;
        if (physicsAnimationLayout == null) {
            return 0.0f;
        }
        return (getAvailableScreenWidth(false) / 2.0f) - (((((float) physicsAnimationLayout.getChildCount()) * this.mBubbleSizePx) + (((float) (this.mLayout.getChildCount() - 1)) * this.mSpaceBetweenBubbles)) / 2.0f);
    }
}
