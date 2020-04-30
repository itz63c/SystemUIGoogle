package com.android.systemui.bubbles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnDrawListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ContrastColorUtil;
import com.android.internal.widget.ViewClippingUtil;
import com.android.internal.widget.ViewClippingUtil.ClippingParameters;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2012R$integer;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.Interpolators;
import com.android.systemui.Prefs;
import com.android.systemui.bubbles.Bubble.FlyoutMessage;
import com.android.systemui.bubbles.BubbleController.BubbleExpandListener;
import com.android.systemui.bubbles.animation.ExpandedAnimationController;
import com.android.systemui.bubbles.animation.PhysicsAnimationLayout;
import com.android.systemui.bubbles.animation.StackAnimationController;
import com.android.systemui.util.DismissCircleView;
import com.android.systemui.util.FloatingContentCoordinator;
import com.android.systemui.util.animation.PhysicsAnimator;
import com.android.systemui.util.animation.PhysicsAnimator.SpringConfig;
import com.android.systemui.util.magnetictarget.MagnetizedObject;
import com.android.systemui.util.magnetictarget.MagnetizedObject.MagnetListener;
import com.android.systemui.util.magnetictarget.MagnetizedObject.MagneticTarget;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.function.IntSupplier;

public class BubbleStackView extends FrameLayout {
    private static final SurfaceSynchronizer DEFAULT_SURFACE_SYNCHRONIZER = new SurfaceSynchronizer() {
        public void syncSurfaceAndRun(final Runnable runnable) {
            Choreographer.getInstance().postFrameCallback(new FrameCallback(this) {
                private int mFrameWait = 2;

                public void doFrame(long j) {
                    int i = this.mFrameWait - 1;
                    this.mFrameWait = i;
                    if (i > 0) {
                        Choreographer.getInstance().postFrameCallback(this);
                    } else {
                        runnable.run();
                    }
                }
            });
        }
    };
    @VisibleForTesting
    static final int FLYOUT_HIDE_AFTER = 5000;
    private static final SpringConfig FLYOUT_IME_ANIMATION_SPRING_CONFIG = new SpringConfig(200.0f, 0.9f);
    private Runnable mAfterFlyoutHidden;
    private final OnAnimationEndListener mAfterFlyoutTransitionSpring = new OnAnimationEndListener() {
        public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            BubbleStackView.this.lambda$new$1$BubbleStackView(dynamicAnimation, z, f, f2);
        }
    };
    private Runnable mAnimateInFlyout;
    private boolean mAnimatingEducationAway;
    private boolean mAnimatingManageEducationAway;
    /* access modifiers changed from: private */
    public PhysicsAnimationLayout mBubbleContainer;
    private final BubbleData mBubbleData;
    private int mBubbleElevation;
    private BubbleOverflow mBubbleOverflow;
    private int mBubblePaddingTop;
    private int mBubbleSize;
    private Bubble mBubbleToExpandAfterFlyoutCollapse = null;
    private int mBubbleTouchPadding;
    private ClippingParameters mClippingParameters = new ClippingParameters() {
        public boolean shouldFinish(View view) {
            return false;
        }

        public boolean isClippingEnablingAllowed(View view) {
            return !BubbleStackView.this.mIsExpanded;
        }
    };
    private final ValueAnimator mDesaturateAndDarkenAnimator;
    private final Paint mDesaturateAndDarkenPaint = new Paint();
    private View mDesaturateAndDarkenTargetView;
    private PhysicsAnimator<View> mDismissTargetAnimator;
    private ViewGroup mDismissTargetContainer;
    private SpringConfig mDismissTargetSpring = new SpringConfig(200.0f, 0.75f);
    private Point mDisplaySize;
    private BubbleExpandListener mExpandListener;
    private int mExpandedAnimateXDistance;
    private int mExpandedAnimateYDistance;
    /* access modifiers changed from: private */
    public ExpandedAnimationController mExpandedAnimationController;
    private BubbleViewProvider mExpandedBubble;
    private FrameLayout mExpandedViewContainer;
    private int mExpandedViewPadding;
    private final SpringAnimation mExpandedViewXAnim;
    private final SpringAnimation mExpandedViewYAnim;
    private BubbleFlyoutView mFlyout;
    private final FloatPropertyCompat mFlyoutCollapseProperty = new FloatPropertyCompat("FlyoutCollapseSpring") {
        public float getValue(Object obj) {
            return BubbleStackView.this.mFlyoutDragDeltaX;
        }

        public void setValue(Object obj, float f) {
            BubbleStackView.this.onFlyoutDragged(f);
        }
    };
    /* access modifiers changed from: private */
    public float mFlyoutDragDeltaX = 0.0f;
    private final SpringAnimation mFlyoutTransitionSpring = new SpringAnimation(this, this.mFlyoutCollapseProperty);
    private Runnable mHideFlyout = new Runnable() {
        public final void run() {
            BubbleStackView.this.lambda$new$0$BubbleStackView();
        }
    };
    private int mImeOffset;
    private final MagnetListener mIndividualBubbleMagnetListener = new MagnetListener() {
        public void onStuckToTarget(MagneticTarget magneticTarget) {
            BubbleStackView bubbleStackView = BubbleStackView.this;
            bubbleStackView.animateDesaturateAndDarken(bubbleStackView.mExpandedAnimationController.getDraggedOutBubble(), true);
        }

        public void onUnstuckFromTarget(MagneticTarget magneticTarget, float f, float f2, boolean z) {
            BubbleStackView bubbleStackView = BubbleStackView.this;
            bubbleStackView.animateDesaturateAndDarken(bubbleStackView.mExpandedAnimationController.getDraggedOutBubble(), false);
            if (z) {
                BubbleStackView.this.mExpandedAnimationController.snapBubbleBack(BubbleStackView.this.mExpandedAnimationController.getDraggedOutBubble(), f, f2);
                BubbleStackView.this.hideDismissTarget();
                return;
            }
            BubbleStackView.this.mExpandedAnimationController.onUnstuckFromTarget();
        }

        public void onReleasedInTarget(MagneticTarget magneticTarget) {
            BubbleStackView.this.mExpandedAnimationController.dismissDraggedOutBubble(BubbleStackView.this.mExpandedAnimationController.getDraggedOutBubble(), BubbleStackView.this.mReleasedInDismissTargetAction);
            BubbleStackView.this.hideDismissTarget();
        }
    };
    private LayoutInflater mInflater;
    /* access modifiers changed from: private */
    public boolean mIsExpanded;
    private boolean mIsExpansionAnimating = false;
    private boolean mIsGestureInProgress = false;
    private MagneticTarget mMagneticTarget;
    private MagnetizedObject<?> mMagnetizedObject;
    private BubbleManageEducationView mManageEducationView;
    private int mMaxBubbles;
    private int mOrientation = 0;
    private OnLayoutChangeListener mOrientationChangedListener;
    private int mPointerHeight;
    /* access modifiers changed from: private */
    public Runnable mReleasedInDismissTargetAction;
    private boolean mShouldShowManageEducation;
    private boolean mShouldShowUserEducation;
    private boolean mShowingDismiss = false;
    /* access modifiers changed from: private */
    public StackAnimationController mStackAnimationController;
    private final MagnetListener mStackMagnetListener = new MagnetListener() {
        public void onStuckToTarget(MagneticTarget magneticTarget) {
            BubbleStackView bubbleStackView = BubbleStackView.this;
            bubbleStackView.animateDesaturateAndDarken(bubbleStackView.mBubbleContainer, true);
        }

        public void onUnstuckFromTarget(MagneticTarget magneticTarget, float f, float f2, boolean z) {
            BubbleStackView bubbleStackView = BubbleStackView.this;
            bubbleStackView.animateDesaturateAndDarken(bubbleStackView.mBubbleContainer, false);
            if (z) {
                BubbleStackView.this.mStackAnimationController.flingStackThenSpringToEdge(BubbleStackView.this.mStackAnimationController.getStackPosition().x, f, f2);
                BubbleStackView.this.hideDismissTarget();
                return;
            }
            BubbleStackView.this.mStackAnimationController.onUnstuckFromTarget();
        }

        public void onReleasedInTarget(MagneticTarget magneticTarget) {
            BubbleStackView.this.mStackAnimationController.implodeStack(new Runnable() {
                public final void run() {
                    C07776.this.lambda$onReleasedInTarget$0$BubbleStackView$6();
                }
            });
            BubbleStackView.this.hideDismissTarget();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onReleasedInTarget$0 */
        public /* synthetic */ void lambda$onReleasedInTarget$0$BubbleStackView$6() {
            BubbleStackView.this.resetDesaturationAndDarken();
            BubbleStackView.this.mReleasedInDismissTargetAction.run();
        }
    };
    private boolean mStackOnLeftOrWillBe = false;
    private int mStatusBarHeight;
    private final SurfaceSynchronizer mSurfaceSynchronizer;
    private OnDrawListener mSystemGestureExcludeUpdater = new OnDrawListener() {
        public final void onDraw() {
            BubbleStackView.this.updateSystemGestureExcludeRects();
        }
    };
    private final List<Rect> mSystemGestureExclusionRects = Collections.singletonList(new Rect());
    int[] mTempLoc = new int[2];
    RectF mTempRect = new RectF();
    private BubbleTouchHandler mTouchHandler;
    private View mUserEducationView;
    private float mVerticalPosPercentBeforeRotation = -1.0f;
    /* access modifiers changed from: private */
    public boolean mViewUpdatedRequested = false;
    /* access modifiers changed from: private */
    public OnPreDrawListener mViewUpdater = new OnPreDrawListener() {
        public boolean onPreDraw() {
            BubbleStackView.this.getViewTreeObserver().removeOnPreDrawListener(BubbleStackView.this.mViewUpdater);
            BubbleStackView.this.updateExpandedView();
            BubbleStackView.this.mViewUpdatedRequested = false;
            return true;
        }
    };
    private boolean mWasOnLeftBeforeRotation = false;

    interface SurfaceSynchronizer {
        void syncSurfaceAndRun(Runnable runnable);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$BubbleStackView() {
        animateFlyoutCollapsed(true, 0.0f);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("Stack view state:");
        printWriter.print("  gestureInProgress:    ");
        printWriter.println(this.mIsGestureInProgress);
        printWriter.print("  showingDismiss:       ");
        printWriter.println(this.mShowingDismiss);
        printWriter.print("  isExpansionAnimating: ");
        printWriter.println(this.mIsExpansionAnimating);
        this.mStackAnimationController.dump(fileDescriptor, printWriter, strArr);
        this.mExpandedAnimationController.dump(fileDescriptor, printWriter, strArr);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$BubbleStackView(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (this.mFlyoutDragDeltaX == 0.0f) {
            this.mFlyout.postDelayed(this.mHideFlyout, 5000);
        } else {
            this.mFlyout.hideFlyout();
        }
    }

    public BubbleStackView(Context context, BubbleData bubbleData, SurfaceSynchronizer surfaceSynchronizer, FloatingContentCoordinator floatingContentCoordinator) {
        super(context);
        this.mBubbleData = bubbleData;
        this.mInflater = LayoutInflater.from(context);
        BubbleTouchHandler bubbleTouchHandler = new BubbleTouchHandler(this, bubbleData, context);
        this.mTouchHandler = bubbleTouchHandler;
        setOnTouchListener(bubbleTouchHandler);
        Resources resources = getResources();
        this.mMaxBubbles = resources.getInteger(C2012R$integer.bubbles_max_rendered);
        this.mBubbleSize = resources.getDimensionPixelSize(C2009R$dimen.individual_bubble_size);
        this.mBubbleElevation = resources.getDimensionPixelSize(C2009R$dimen.bubble_elevation);
        this.mBubblePaddingTop = resources.getDimensionPixelSize(C2009R$dimen.bubble_padding_top);
        this.mBubbleTouchPadding = resources.getDimensionPixelSize(C2009R$dimen.bubble_touch_padding);
        this.mExpandedAnimateXDistance = resources.getDimensionPixelSize(C2009R$dimen.bubble_expanded_animate_x_distance);
        this.mExpandedAnimateYDistance = resources.getDimensionPixelSize(C2009R$dimen.bubble_expanded_animate_y_distance);
        this.mPointerHeight = resources.getDimensionPixelSize(C2009R$dimen.bubble_pointer_height);
        this.mStatusBarHeight = resources.getDimensionPixelSize(17105462);
        this.mImeOffset = resources.getDimensionPixelSize(C2009R$dimen.pip_ime_offset);
        this.mDisplaySize = new Point();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRealSize(this.mDisplaySize);
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        this.mExpandedViewPadding = resources.getDimensionPixelSize(C2009R$dimen.bubble_expanded_view_padding);
        int dimensionPixelSize = resources.getDimensionPixelSize(C2009R$dimen.bubble_elevation);
        this.mStackAnimationController = new StackAnimationController(floatingContentCoordinator, new IntSupplier() {
            public final int getAsInt() {
                return BubbleStackView.this.getBubbleCount();
            }
        });
        this.mExpandedAnimationController = new ExpandedAnimationController(this.mDisplaySize, this.mExpandedViewPadding, resources.getConfiguration().orientation);
        if (surfaceSynchronizer == null) {
            surfaceSynchronizer = DEFAULT_SURFACE_SYNCHRONIZER;
        }
        this.mSurfaceSynchronizer = surfaceSynchronizer;
        setUpUserEducation();
        PhysicsAnimationLayout physicsAnimationLayout = new PhysicsAnimationLayout(context);
        this.mBubbleContainer = physicsAnimationLayout;
        physicsAnimationLayout.setActiveController(this.mStackAnimationController);
        float f = (float) dimensionPixelSize;
        this.mBubbleContainer.setElevation(f);
        this.mBubbleContainer.setClipChildren(false);
        addView(this.mBubbleContainer, new LayoutParams(-1, -1));
        FrameLayout frameLayout = new FrameLayout(context);
        this.mExpandedViewContainer = frameLayout;
        frameLayout.setElevation(f);
        FrameLayout frameLayout2 = this.mExpandedViewContainer;
        int i = this.mExpandedViewPadding;
        frameLayout2.setPadding(i, i, i, i);
        this.mExpandedViewContainer.setClipChildren(false);
        addView(this.mExpandedViewContainer);
        setUpFlyout();
        SpringAnimation springAnimation = this.mFlyoutTransitionSpring;
        SpringForce springForce = new SpringForce();
        springForce.setStiffness(200.0f);
        springForce.setDampingRatio(0.75f);
        springAnimation.setSpring(springForce);
        this.mFlyoutTransitionSpring.addEndListener(this.mAfterFlyoutTransitionSpring);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(C2009R$dimen.dismiss_circle_size);
        DismissCircleView dismissCircleView = new DismissCircleView(context);
        LayoutParams layoutParams = new LayoutParams(dimensionPixelSize2, dimensionPixelSize2);
        layoutParams.gravity = 17;
        dismissCircleView.setLayoutParams(layoutParams);
        this.mDismissTargetAnimator = PhysicsAnimator.getInstance(dismissCircleView);
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.mDismissTargetContainer = frameLayout3;
        frameLayout3.setLayoutParams(new LayoutParams(-1, getResources().getDimensionPixelSize(C2009R$dimen.pip_dismiss_gradient_height), 80));
        this.mDismissTargetContainer.setClipChildren(false);
        this.mDismissTargetContainer.addView(dismissCircleView);
        this.mDismissTargetContainer.setVisibility(4);
        addView(this.mDismissTargetContainer);
        dismissCircleView.setTranslationY((float) getResources().getDimensionPixelSize(C2009R$dimen.pip_dismiss_gradient_height));
        this.mMagneticTarget = new MagneticTarget(dismissCircleView, this.mBubbleSize * 2);
        SpringAnimation springAnimation2 = new SpringAnimation(this.mExpandedViewContainer, DynamicAnimation.TRANSLATION_X);
        this.mExpandedViewXAnim = springAnimation2;
        SpringForce springForce2 = new SpringForce();
        springForce2.setStiffness(200.0f);
        springForce2.setDampingRatio(0.75f);
        springAnimation2.setSpring(springForce2);
        SpringAnimation springAnimation3 = new SpringAnimation(this.mExpandedViewContainer, DynamicAnimation.TRANSLATION_Y);
        this.mExpandedViewYAnim = springAnimation3;
        SpringForce springForce3 = new SpringForce();
        springForce3.setStiffness(200.0f);
        springForce3.setDampingRatio(0.75f);
        springAnimation3.setSpring(springForce3);
        this.mExpandedViewYAnim.addEndListener(new OnAnimationEndListener() {
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                BubbleStackView.this.lambda$new$2$BubbleStackView(dynamicAnimation, z, f, f2);
            }
        });
        setClipChildren(false);
        setFocusable(true);
        this.mBubbleContainer.bringToFront();
        setUpOverflow();
        setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
            public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                return BubbleStackView.this.lambda$new$4$BubbleStackView(view, windowInsets);
            }
        });
        this.mOrientationChangedListener = new OnLayoutChangeListener() {
            public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                BubbleStackView.this.lambda$new$6$BubbleStackView(view, i, i2, i3, i4, i5, i6, i7, i8);
            }
        };
        getViewTreeObserver().addOnDrawListener(this.mSystemGestureExcludeUpdater);
        ColorMatrix colorMatrix = new ColorMatrix();
        ColorMatrix colorMatrix2 = new ColorMatrix();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        this.mDesaturateAndDarkenAnimator = ofFloat;
        ofFloat.addUpdateListener(new AnimatorUpdateListener(colorMatrix, colorMatrix2) {
            public final /* synthetic */ ColorMatrix f$1;
            public final /* synthetic */ ColorMatrix f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BubbleStackView.this.lambda$new$7$BubbleStackView(this.f$1, this.f$2, valueAnimator);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$BubbleStackView(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (this.mIsExpanded) {
            BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
            if (bubbleViewProvider != null) {
                bubbleViewProvider.getExpandedView().updateView();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ WindowInsets lambda$new$4$BubbleStackView(View view, WindowInsets windowInsets) {
        if (!this.mIsExpanded || this.mIsExpansionAnimating) {
            return view.onApplyWindowInsets(windowInsets);
        }
        this.mExpandedAnimationController.updateYPosition(new Runnable(windowInsets) {
            public final /* synthetic */ WindowInsets f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                BubbleStackView.this.lambda$new$3$BubbleStackView(this.f$1);
            }
        });
        return view.onApplyWindowInsets(windowInsets);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$BubbleStackView(WindowInsets windowInsets) {
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (bubbleViewProvider != null) {
            bubbleViewProvider.getExpandedView().updateInsets(windowInsets);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$6 */
    public /* synthetic */ void lambda$new$6$BubbleStackView(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        int i9;
        int i10;
        this.mExpandedAnimationController.updateOrientation(this.mOrientation, this.mDisplaySize);
        this.mStackAnimationController.updateOrientation(this.mOrientation);
        if (this.mIsExpanded) {
            this.mExpandedViewContainer.setTranslationY(getExpandedViewY());
            BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
            if (bubbleViewProvider != null) {
                bubbleViewProvider.getExpandedView().updateView();
            }
        }
        WindowInsets rootWindowInsets = getRootWindowInsets();
        int i11 = this.mExpandedViewPadding;
        if (rootWindowInsets != null) {
            DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
            int i12 = 0;
            if (displayCutout != null) {
                i12 = displayCutout.getSafeInsetLeft();
                i10 = displayCutout.getSafeInsetRight();
            } else {
                i10 = 0;
            }
            int max = Math.max(i12, rootWindowInsets.getStableInsetLeft()) + i11;
            i9 = i11 + Math.max(i10, rootWindowInsets.getStableInsetRight());
            i11 = max;
        } else {
            i9 = i11;
        }
        FrameLayout frameLayout = this.mExpandedViewContainer;
        int i13 = this.mExpandedViewPadding;
        frameLayout.setPadding(i11, i13, i9, i13);
        if (this.mIsExpanded) {
            this.mExpandedAnimationController.expandFromStack(new Runnable() {
                public final void run() {
                    BubbleStackView.this.lambda$new$5$BubbleStackView();
                }
            });
        }
        float f = this.mVerticalPosPercentBeforeRotation;
        if (f >= 0.0f) {
            this.mStackAnimationController.moveStackToSimilarPositionAfterRotation(this.mWasOnLeftBeforeRotation, f);
        }
        removeOnLayoutChangeListener(this.mOrientationChangedListener);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$7 */
    public /* synthetic */ void lambda$new$7$BubbleStackView(ColorMatrix colorMatrix, ColorMatrix colorMatrix2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        colorMatrix.setSaturation(floatValue);
        float f = 1.0f - ((1.0f - floatValue) * 0.3f);
        colorMatrix2.setScale(f, f, f, 1.0f);
        colorMatrix.postConcat(colorMatrix2);
        this.mDesaturateAndDarkenPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        this.mDesaturateAndDarkenTargetView.setLayerPaint(this.mDesaturateAndDarkenPaint);
    }

    private void setUpUserEducation() {
        View view = this.mUserEducationView;
        if (view != null) {
            removeView(view);
        }
        boolean shouldShowBubblesEducation = shouldShowBubblesEducation();
        this.mShouldShowUserEducation = shouldShowBubblesEducation;
        if (shouldShowBubblesEducation) {
            View inflate = this.mInflater.inflate(C2013R$layout.bubble_stack_user_education, this, false);
            this.mUserEducationView = inflate;
            inflate.setVisibility(8);
            TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(new int[]{16843829, 16842809});
            int color = obtainStyledAttributes.getColor(0, -16777216);
            int color2 = obtainStyledAttributes.getColor(1, -1);
            obtainStyledAttributes.recycle();
            int ensureTextContrast = ContrastColorUtil.ensureTextContrast(color2, color, true);
            TextView textView = (TextView) this.mUserEducationView.findViewById(C2011R$id.user_education_description);
            ((TextView) this.mUserEducationView.findViewById(C2011R$id.user_education_title)).setTextColor(ensureTextContrast);
            textView.setTextColor(ensureTextContrast);
            addView(this.mUserEducationView);
        }
        BubbleManageEducationView bubbleManageEducationView = this.mManageEducationView;
        if (bubbleManageEducationView != null) {
            removeView(bubbleManageEducationView);
        }
        boolean shouldShowManageEducation = shouldShowManageEducation();
        this.mShouldShowManageEducation = shouldShowManageEducation;
        if (shouldShowManageEducation) {
            BubbleManageEducationView bubbleManageEducationView2 = (BubbleManageEducationView) this.mInflater.inflate(C2013R$layout.bubbles_manage_button_education, this, false);
            this.mManageEducationView = bubbleManageEducationView2;
            bubbleManageEducationView2.setVisibility(8);
            this.mManageEducationView.setElevation((float) this.mBubbleElevation);
            addView(this.mManageEducationView);
        }
    }

    private void setUpFlyout() {
        BubbleFlyoutView bubbleFlyoutView = this.mFlyout;
        if (bubbleFlyoutView != null) {
            removeView(bubbleFlyoutView);
        }
        BubbleFlyoutView bubbleFlyoutView2 = new BubbleFlyoutView(getContext());
        this.mFlyout = bubbleFlyoutView2;
        bubbleFlyoutView2.setVisibility(8);
        this.mFlyout.animate().setDuration(100).setInterpolator(new AccelerateDecelerateInterpolator());
        addView(this.mFlyout, new LayoutParams(-2, -2));
    }

    private void setUpOverflow() {
        if (BubbleExperimentConfig.allowBubbleOverflow(this.mContext)) {
            int i = 0;
            BubbleOverflow bubbleOverflow = this.mBubbleOverflow;
            if (bubbleOverflow == null) {
                BubbleOverflow bubbleOverflow2 = new BubbleOverflow(getContext());
                this.mBubbleOverflow = bubbleOverflow2;
                bubbleOverflow2.setUpOverflow(this.mBubbleContainer, this);
            } else {
                this.mBubbleContainer.removeView(bubbleOverflow.getBtn());
                this.mBubbleOverflow.updateIcon(this.mContext, this);
                i = this.mBubbleContainer.getChildCount() - 1;
            }
            this.mBubbleContainer.addView(this.mBubbleOverflow.getBtn(), i, new LayoutParams(-2, -2));
        }
    }

    public void onThemeChanged() {
        setUpFlyout();
        setUpOverflow();
        setUpUserEducation();
    }

    public void onOrientationChanged(int i) {
        this.mOrientation = i;
        ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getRealSize(this.mDisplaySize);
        Resources resources = getContext().getResources();
        this.mStatusBarHeight = resources.getDimensionPixelSize(17105462);
        this.mBubblePaddingTop = resources.getDimensionPixelSize(C2009R$dimen.bubble_padding_top);
        RectF allowableStackPositionRegion = this.mStackAnimationController.getAllowableStackPositionRegion();
        this.mWasOnLeftBeforeRotation = this.mStackAnimationController.isStackOnLeftSide();
        float f = this.mStackAnimationController.getStackPosition().y;
        float f2 = allowableStackPositionRegion.top;
        this.mVerticalPosPercentBeforeRotation = (f - f2) / (allowableStackPositionRegion.bottom - f2);
        addOnLayoutChangeListener(this.mOrientationChangedListener);
        hideFlyoutImmediate();
    }

    public void getBoundsOnScreen(Rect rect, boolean z) {
        getBoundsOnScreen(rect);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnPreDrawListener(this.mViewUpdater);
    }

    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(new AccessibilityAction(C2011R$id.action_move_top_left, getContext().getResources().getString(C2017R$string.bubble_accessibility_action_move_top_left)));
        accessibilityNodeInfo.addAction(new AccessibilityAction(C2011R$id.action_move_top_right, getContext().getResources().getString(C2017R$string.bubble_accessibility_action_move_top_right)));
        accessibilityNodeInfo.addAction(new AccessibilityAction(C2011R$id.action_move_bottom_left, getContext().getResources().getString(C2017R$string.bubble_accessibility_action_move_bottom_left)));
        accessibilityNodeInfo.addAction(new AccessibilityAction(C2011R$id.action_move_bottom_right, getContext().getResources().getString(C2017R$string.bubble_accessibility_action_move_bottom_right)));
        accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_DISMISS);
        if (this.mIsExpanded) {
            accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_COLLAPSE);
        } else {
            accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_EXPAND);
        }
    }

    public boolean performAccessibilityActionInternal(int i, Bundle bundle) {
        if (super.performAccessibilityActionInternal(i, bundle)) {
            return true;
        }
        RectF allowableStackPositionRegion = this.mStackAnimationController.getAllowableStackPositionRegion();
        if (i == 1048576) {
            this.mBubbleData.dismissAll(6);
            return true;
        } else if (i == 524288) {
            this.mBubbleData.setExpanded(false);
            return true;
        } else if (i == 262144) {
            this.mBubbleData.setExpanded(true);
            return true;
        } else if (i == C2011R$id.action_move_top_left) {
            this.mStackAnimationController.springStackAfterFling(allowableStackPositionRegion.left, allowableStackPositionRegion.top);
            return true;
        } else if (i == C2011R$id.action_move_top_right) {
            this.mStackAnimationController.springStackAfterFling(allowableStackPositionRegion.right, allowableStackPositionRegion.top);
            return true;
        } else if (i == C2011R$id.action_move_bottom_left) {
            this.mStackAnimationController.springStackAfterFling(allowableStackPositionRegion.left, allowableStackPositionRegion.bottom);
            return true;
        } else if (i != C2011R$id.action_move_bottom_right) {
            return false;
        } else {
            this.mStackAnimationController.springStackAfterFling(allowableStackPositionRegion.right, allowableStackPositionRegion.bottom);
            return true;
        }
    }

    public void updateContentDescription() {
        if (!this.mBubbleData.getBubbles().isEmpty()) {
            Bubble bubble = (Bubble) this.mBubbleData.getBubbles().get(0);
            String appName = bubble.getAppName();
            CharSequence charSequence = bubble.getEntry().getSbn().getNotification().extras.getCharSequence("android.title");
            String string = getResources().getString(C2017R$string.notification_bubble_title);
            if (charSequence != null) {
                string = charSequence.toString();
            }
            int childCount = this.mBubbleContainer.getChildCount() - 1;
            String string2 = getResources().getString(C2017R$string.bubble_content_description_single, new Object[]{string, appName});
            String string3 = getResources().getString(C2017R$string.bubble_content_description_stack, new Object[]{string, appName, Integer.valueOf(childCount)});
            if (!this.mIsExpanded) {
                if (childCount > 0) {
                    this.mBubbleContainer.setContentDescription(string3);
                } else {
                    this.mBubbleContainer.setContentDescription(string2);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSystemGestureExcludeRects() {
        Rect rect = (Rect) this.mSystemGestureExclusionRects.get(0);
        if (getBubbleCount() > 0) {
            View childAt = this.mBubbleContainer.getChildAt(0);
            rect.set(childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom());
            rect.offset((int) (childAt.getTranslationX() + 0.5f), (int) (childAt.getTranslationY() + 0.5f));
            this.mBubbleContainer.setSystemGestureExclusionRects(this.mSystemGestureExclusionRects);
            return;
        }
        rect.setEmpty();
        this.mBubbleContainer.setSystemGestureExclusionRects(Collections.emptyList());
    }

    public void setExpandListener(BubbleExpandListener bubbleExpandListener) {
        this.mExpandListener = bubbleExpandListener;
    }

    public boolean isExpanded() {
        return this.mIsExpanded;
    }

    public boolean isExpansionAnimating() {
        return this.mIsExpansionAnimating;
    }

    /* access modifiers changed from: 0000 */
    public View getExpandedBubbleView() {
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (bubbleViewProvider != null) {
            return bubbleViewProvider.getIconView();
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public BubbleViewProvider getExpandedBubble() {
        return this.mExpandedBubble;
    }

    /* access modifiers changed from: 0000 */
    public void addBubble(Bubble bubble) {
        if (getBubbleCount() == 0 && this.mShouldShowUserEducation) {
            StackAnimationController stackAnimationController = this.mStackAnimationController;
            stackAnimationController.setStackPosition(stackAnimationController.getDefaultStartPosition());
        }
        if (getBubbleCount() == 0) {
            this.mStackOnLeftOrWillBe = this.mStackAnimationController.isStackOnLeftSide();
        }
        bubble.getIconView().setDotPosition(!this.mStackOnLeftOrWillBe, false);
        this.mBubbleContainer.addView(bubble.getIconView(), 0, new LayoutParams(-2, -2));
        ViewClippingUtil.setClippingDeactivated(bubble.getIconView(), true, this.mClippingParameters);
        animateInFlyoutForBubble(bubble);
        lambda$updateOverflowBtnVisibility$8();
        updateOverflowBtnVisibility(true);
        requestUpdate();
        logBubbleEvent(bubble, 1);
    }

    /* access modifiers changed from: 0000 */
    public void removeBubble(Bubble bubble) {
        int indexOfChild = this.mBubbleContainer.indexOfChild(bubble.getIconView());
        if (indexOfChild >= 0) {
            this.mBubbleContainer.removeViewAt(indexOfChild);
            bubble.cleanupExpandedState();
            bubble.setInflated(false);
            logBubbleEvent(bubble, 5);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("was asked to remove Bubble, but didn't find the view! ");
            sb.append(bubble);
            Log.d("Bubbles", sb.toString());
        }
        updateOverflowBtnVisibility(true);
    }

    private void updateOverflowBtnVisibility(boolean z) {
        if (BubbleExperimentConfig.allowBubbleOverflow(this.mContext)) {
            if (this.mIsExpanded) {
                this.mBubbleOverflow.setBtnVisible(0);
                if (z) {
                    this.mExpandedAnimationController.expandFromStack(new Runnable() {
                        public final void run() {
                            BubbleStackView.this.lambda$updateOverflowBtnVisibility$8$BubbleStackView();
                        }
                    });
                }
            } else {
                this.mBubbleOverflow.setBtnVisible(8);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateBubble(Bubble bubble) {
        animateInFlyoutForBubble(bubble);
        requestUpdate();
        logBubbleEvent(bubble, 2);
    }

    public void updateBubbleOrder(List<Bubble> list) {
        for (int i = 0; i < list.size(); i++) {
            this.mBubbleContainer.reorderView(((Bubble) list.get(i)).getIconView(), i);
        }
        updateBubbleZOrdersAndDotPosition(false);
    }

    /* access modifiers changed from: 0000 */
    public void showOverflow() {
        setSelectedBubble(this.mBubbleOverflow);
    }

    public void setSelectedBubble(BubbleViewProvider bubbleViewProvider) {
        BubbleViewProvider bubbleViewProvider2 = this.mExpandedBubble;
        if (bubbleViewProvider2 == null || !bubbleViewProvider2.equals(bubbleViewProvider)) {
            BubbleViewProvider bubbleViewProvider3 = this.mExpandedBubble;
            this.mExpandedBubble = bubbleViewProvider;
            if (this.mIsExpanded) {
                this.mExpandedViewContainer.setAlpha(0.0f);
                this.mSurfaceSynchronizer.syncSurfaceAndRun(new Runnable(bubbleViewProvider3, bubbleViewProvider) {
                    public final /* synthetic */ BubbleViewProvider f$1;
                    public final /* synthetic */ BubbleViewProvider f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        BubbleStackView.this.lambda$setSelectedBubble$9$BubbleStackView(this.f$1, this.f$2);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setSelectedBubble$9 */
    public /* synthetic */ void lambda$setSelectedBubble$9$BubbleStackView(BubbleViewProvider bubbleViewProvider, BubbleViewProvider bubbleViewProvider2) {
        bubbleViewProvider.setContentVisibility(false);
        updateExpandedBubble();
        lambda$updateOverflowBtnVisibility$8();
        requestUpdate();
        logBubbleEvent(bubbleViewProvider, 4);
        logBubbleEvent(bubbleViewProvider2, 3);
        notifyExpansionChanged(bubbleViewProvider, false);
        notifyExpansionChanged(bubbleViewProvider2, true);
    }

    public void setExpanded(boolean z) {
        boolean z2 = this.mIsExpanded;
        if (z != z2) {
            if (z2) {
                animateCollapse();
                logBubbleEvent(this.mExpandedBubble, 4);
            } else {
                animateExpansion();
                logBubbleEvent(this.mExpandedBubble, 3);
                logBubbleEvent(this.mExpandedBubble, 15);
            }
            notifyExpansionChanged(this.mExpandedBubble, this.mIsExpanded);
        }
    }

    private boolean maybeShowStackUserEducation() {
        if (!this.mShouldShowUserEducation || this.mUserEducationView.getVisibility() == 0) {
            return false;
        }
        this.mUserEducationView.setAlpha(0.0f);
        this.mUserEducationView.setVisibility(0);
        this.mUserEducationView.post(new Runnable() {
            public final void run() {
                BubbleStackView.this.lambda$maybeShowStackUserEducation$10$BubbleStackView();
            }
        });
        Prefs.putBoolean(getContext(), "HasSeenBubblesOnboarding", true);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$maybeShowStackUserEducation$10 */
    public /* synthetic */ void lambda$maybeShowStackUserEducation$10$BubbleStackView() {
        this.mUserEducationView.setTranslationY((this.mStackAnimationController.getDefaultStartPosition().y + ((float) (this.mBubbleSize / 2))) - ((float) (this.mUserEducationView.getHeight() / 2)));
        this.mUserEducationView.animate().setDuration(200).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).alpha(1.0f);
    }

    /* access modifiers changed from: 0000 */
    public void hideStackUserEducation(boolean z) {
        if (this.mShouldShowUserEducation && this.mUserEducationView.getVisibility() == 0 && !this.mAnimatingEducationAway) {
            this.mAnimatingEducationAway = true;
            this.mUserEducationView.animate().alpha(0.0f).setDuration(z ? 40 : 200).withEndAction(new Runnable() {
                public final void run() {
                    BubbleStackView.this.lambda$hideStackUserEducation$11$BubbleStackView();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hideStackUserEducation$11 */
    public /* synthetic */ void lambda$hideStackUserEducation$11$BubbleStackView() {
        this.mAnimatingEducationAway = false;
        this.mShouldShowUserEducation = shouldShowBubblesEducation();
        this.mUserEducationView.setVisibility(8);
    }

    /* access modifiers changed from: 0000 */
    public void maybeShowManageEducation(boolean z) {
        BubbleManageEducationView bubbleManageEducationView = this.mManageEducationView;
        if (bubbleManageEducationView != null) {
            if (z && this.mShouldShowManageEducation && bubbleManageEducationView.getVisibility() != 0 && this.mIsExpanded) {
                this.mManageEducationView.setAlpha(0.0f);
                this.mManageEducationView.setVisibility(0);
                this.mManageEducationView.post(new Runnable() {
                    public final void run() {
                        BubbleStackView.this.lambda$maybeShowManageEducation$12$BubbleStackView();
                    }
                });
                Prefs.putBoolean(getContext(), "HasSeenBubblesManageOnboarding", true);
            } else if (!z && this.mManageEducationView.getVisibility() == 0 && !this.mAnimatingManageEducationAway) {
                this.mManageEducationView.animate().alpha(0.0f).setDuration(this.mIsExpansionAnimating ? 40 : 200).withEndAction(new Runnable() {
                    public final void run() {
                        BubbleStackView.this.lambda$maybeShowManageEducation$13$BubbleStackView();
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$maybeShowManageEducation$12 */
    public /* synthetic */ void lambda$maybeShowManageEducation$12$BubbleStackView() {
        Rect manageButtonLocationOnScreen = this.mExpandedBubble.getExpandedView().getManageButtonLocationOnScreen();
        int manageViewHeight = this.mManageEducationView.getManageViewHeight();
        int dimensionPixelSize = getResources().getDimensionPixelSize(C2009R$dimen.bubbles_manage_education_top_inset);
        this.mManageEducationView.bringToFront();
        this.mManageEducationView.setManageViewPosition(manageButtonLocationOnScreen.left, (manageButtonLocationOnScreen.top - manageViewHeight) + dimensionPixelSize);
        this.mManageEducationView.setPointerPosition(manageButtonLocationOnScreen.centerX() - manageButtonLocationOnScreen.left);
        this.mManageEducationView.animate().setDuration(200).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).alpha(1.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$maybeShowManageEducation$13 */
    public /* synthetic */ void lambda$maybeShowManageEducation$13$BubbleStackView() {
        this.mAnimatingManageEducationAway = false;
        this.mShouldShowManageEducation = shouldShowManageEducation();
        this.mManageEducationView.setVisibility(8);
    }

    public void setReleasedInDismissTargetAction(Runnable runnable) {
        this.mReleasedInDismissTargetAction = runnable;
    }

    public View getTargetView(MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        if (this.mIsExpanded) {
            if (isIntersecting(this.mBubbleContainer, rawX, rawY)) {
                if (BubbleExperimentConfig.allowBubbleOverflow(this.mContext) && isIntersecting(this.mBubbleOverflow.getBtn(), rawX, rawY)) {
                    return this.mBubbleOverflow.getBtn();
                }
                for (int i = 0; i < getBubbleCount(); i++) {
                    BadgedImageView badgedImageView = (BadgedImageView) this.mBubbleContainer.getChildAt(i);
                    if (isIntersecting(badgedImageView, rawX, rawY)) {
                        return badgedImageView;
                    }
                }
            }
            BubbleExpandedView bubbleExpandedView = (BubbleExpandedView) this.mExpandedViewContainer.getChildAt(0);
            if (bubbleExpandedView.intersectingTouchableContent((int) rawX, (int) rawY)) {
                return bubbleExpandedView;
            }
            return null;
        } else if (this.mFlyout.getVisibility() == 0 && isIntersecting(this.mFlyout, rawX, rawY)) {
            return this.mFlyout;
        } else {
            View view = this.mUserEducationView;
            if (view == null || view.getVisibility() != 0 || isIntersecting(this.mBubbleContainer.getChildAt(0), rawX, rawY)) {
                return this;
            }
            if (isIntersecting(this.mUserEducationView, rawX, rawY)) {
                return this.mUserEducationView;
            }
            return null;
        }
    }

    /* access modifiers changed from: 0000 */
    public View getFlyoutView() {
        return this.mFlyout;
    }

    /* access modifiers changed from: 0000 */
    @Deprecated
    public void collapseStack(Runnable runnable) {
        this.mBubbleData.setExpanded(false);
        runnable.run();
    }

    /* access modifiers changed from: 0000 */
    public void showExpandedViewContents(int i) {
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (bubbleViewProvider != null && bubbleViewProvider.getExpandedView().getVirtualDisplayId() == i) {
            this.mExpandedBubble.setContentVisibility(true);
        }
    }

    private void beforeExpandedViewAnimation() {
        hideFlyoutImmediate();
        updateExpandedBubble();
        updateExpandedView();
        this.mIsExpansionAnimating = true;
    }

    private void afterExpandedViewAnimation() {
        updateExpandedView();
        this.mIsExpansionAnimating = false;
        requestUpdate();
    }

    private void animateCollapse() {
        this.mIsExpanded = false;
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        beforeExpandedViewAnimation();
        maybeShowManageEducation(false);
        updateOverflowBtnVisibility(false);
        this.mBubbleContainer.cancelAllAnimations();
        this.mExpandedAnimationController.collapseBackToStack(this.mStackAnimationController.getStackPositionAlongNearestHorizontalEdge(), new Runnable(bubbleViewProvider) {
            public final /* synthetic */ BubbleViewProvider f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                BubbleStackView.this.lambda$animateCollapse$14$BubbleStackView(this.f$1);
            }
        });
        this.mExpandedViewXAnim.animateToFinalPosition(getCollapsedX());
        this.mExpandedViewYAnim.animateToFinalPosition(getCollapsedY());
        this.mExpandedViewContainer.animate().setDuration(100).alpha(0.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateCollapse$14 */
    public /* synthetic */ void lambda$animateCollapse$14$BubbleStackView(BubbleViewProvider bubbleViewProvider) {
        this.mBubbleContainer.setActiveController(this.mStackAnimationController);
        afterExpandedViewAnimation();
        bubbleViewProvider.setContentVisibility(false);
    }

    private void animateExpansion() {
        this.mIsExpanded = true;
        hideStackUserEducation(true);
        beforeExpandedViewAnimation();
        this.mBubbleContainer.setActiveController(this.mExpandedAnimationController);
        updateOverflowBtnVisibility(false);
        this.mExpandedAnimationController.expandFromStack(new Runnable() {
            public final void run() {
                BubbleStackView.this.lambda$animateExpansion$15$BubbleStackView();
            }
        });
        this.mExpandedViewContainer.setTranslationX(getCollapsedX());
        this.mExpandedViewContainer.setTranslationY(getCollapsedY());
        this.mExpandedViewContainer.setAlpha(0.0f);
        this.mExpandedViewXAnim.animateToFinalPosition(0.0f);
        this.mExpandedViewYAnim.animateToFinalPosition(getExpandedViewY());
        this.mExpandedViewContainer.animate().setDuration(100).alpha(1.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateExpansion$15 */
    public /* synthetic */ void lambda$animateExpansion$15$BubbleStackView() {
        lambda$updateOverflowBtnVisibility$8();
        afterExpandedViewAnimation();
        maybeShowManageEducation(true);
    }

    private float getCollapsedX() {
        int i;
        if (this.mStackAnimationController.getStackPosition().x < ((float) (getWidth() / 2))) {
            i = -this.mExpandedAnimateXDistance;
        } else {
            i = this.mExpandedAnimateXDistance;
        }
        return (float) i;
    }

    private float getCollapsedY() {
        return Math.min(this.mStackAnimationController.getStackPosition().y, (float) this.mExpandedAnimateYDistance);
    }

    private void notifyExpansionChanged(BubbleViewProvider bubbleViewProvider, boolean z) {
        BubbleExpandListener bubbleExpandListener = this.mExpandListener;
        if (bubbleExpandListener != null && bubbleViewProvider != null) {
            bubbleExpandListener.onBubbleExpandChanged(z, bubbleViewProvider.getKey());
        }
    }

    public void onImeVisibilityChanged(boolean z, int i) {
        this.mStackAnimationController.setImeHeight(z ? i + this.mImeOffset : 0);
        if (!this.mIsExpanded && getBubbleCount() > 0) {
            float animateForImeVisibility = this.mStackAnimationController.animateForImeVisibility(z) - this.mStackAnimationController.getStackPosition().y;
            if (this.mFlyout.getVisibility() == 0) {
                PhysicsAnimator instance = PhysicsAnimator.getInstance(this.mFlyout);
                instance.spring(DynamicAnimation.TRANSLATION_Y, this.mFlyout.getTranslationY() + animateForImeVisibility, FLYOUT_IME_ANIMATION_SPRING_CONFIG);
                instance.start();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void onStackTapped() {
        if (!maybeShowStackUserEducation()) {
            this.mBubbleData.setExpanded(true);
        }
    }

    public void onBubbleDragStart(View view) {
        BubbleOverflow bubbleOverflow = this.mBubbleOverflow;
        if (bubbleOverflow == null || !view.equals(bubbleOverflow.getIconView())) {
            this.mExpandedAnimationController.prepareForBubbleDrag(view, this.mMagneticTarget);
            MagnetizedObject<?> magnetizedBubbleDraggingOut = this.mExpandedAnimationController.getMagnetizedBubbleDraggingOut();
            this.mMagnetizedObject = magnetizedBubbleDraggingOut;
            magnetizedBubbleDraggingOut.setMagnetListener(this.mIndividualBubbleMagnetListener);
            maybeShowManageEducation(false);
        }
    }

    public void onBubbleDragged(View view, float f, float f2) {
        if (this.mIsExpanded && !this.mIsExpansionAnimating) {
            BubbleOverflow bubbleOverflow = this.mBubbleOverflow;
            if (bubbleOverflow == null || !view.equals(bubbleOverflow.getIconView())) {
                this.mExpandedAnimationController.dragBubbleOut(view, f, f2);
                springInDismissTarget();
            }
        }
    }

    public void onBubbleDragFinish(View view, float f, float f2, float f3, float f4) {
        if (this.mIsExpanded && !this.mIsExpansionAnimating) {
            BubbleOverflow bubbleOverflow = this.mBubbleOverflow;
            if (bubbleOverflow == null || !view.equals(bubbleOverflow.getIconView())) {
                this.mExpandedAnimationController.snapBubbleBack(view, f3, f4);
                hideDismissTarget();
            }
        }
    }

    public void expandBubble(Bubble bubble) {
        if (bubble.equals(this.mBubbleData.getSelectedBubble())) {
            setSelectedBubble(bubble);
        } else {
            this.mBubbleData.setSelectedBubble(bubble);
        }
    }

    /* access modifiers changed from: 0000 */
    public void onDragStart() {
        if (!this.mIsExpanded && !this.mIsExpansionAnimating) {
            this.mStackAnimationController.cancelStackPositionAnimations();
            this.mBubbleContainer.setActiveController(this.mStackAnimationController);
            hideFlyoutImmediate();
            MagnetizedObject<?> magnetizedStack = this.mStackAnimationController.getMagnetizedStack(this.mMagneticTarget);
            this.mMagnetizedObject = magnetizedStack;
            magnetizedStack.setMagnetListener(this.mStackMagnetListener);
        }
    }

    /* access modifiers changed from: 0000 */
    public void onDragged(float f, float f2) {
        if (!this.mIsExpanded && !this.mIsExpansionAnimating) {
            hideStackUserEducation(false);
            springInDismissTarget();
            this.mStackAnimationController.moveStackFromTouch(f, f2);
        }
    }

    /* access modifiers changed from: 0000 */
    public void onDragFinish(float f, float f2, float f3, float f4) {
        if (!this.mIsExpanded && !this.mIsExpansionAnimating) {
            float flingStackThenSpringToEdge = this.mStackAnimationController.flingStackThenSpringToEdge(f, f3, f4);
            logBubbleEvent(null, 7);
            this.mStackOnLeftOrWillBe = flingStackThenSpringToEdge <= 0.0f;
            updateBubbleZOrdersAndDotPosition(true);
            hideDismissTarget();
        }
    }

    /* access modifiers changed from: 0000 */
    public void onFlyoutDragStart() {
        this.mFlyout.removeCallbacks(this.mHideFlyout);
    }

    /* access modifiers changed from: 0000 */
    public void onFlyoutDragged(float f) {
        if (this.mFlyout.getWidth() > 0) {
            boolean isStackOnLeftSide = this.mStackAnimationController.isStackOnLeftSide();
            this.mFlyoutDragDeltaX = f;
            if (isStackOnLeftSide) {
                f = -f;
            }
            float width = f / ((float) this.mFlyout.getWidth());
            float f2 = 0.0f;
            this.mFlyout.setCollapsePercent(Math.min(1.0f, Math.max(0.0f, width)));
            int i = (width > 0.0f ? 1 : (width == 0.0f ? 0 : -1));
            if (i < 0 || width > 1.0f) {
                int i2 = (width > 1.0f ? 1 : (width == 1.0f ? 0 : -1));
                boolean z = false;
                int i3 = 1;
                boolean z2 = i2 > 0;
                if ((isStackOnLeftSide && i2 > 0) || (!isStackOnLeftSide && i < 0)) {
                    z = true;
                }
                float f3 = (z2 ? width - 1.0f : width * -1.0f) * ((float) (z ? -1 : 1));
                float width2 = (float) this.mFlyout.getWidth();
                if (z2) {
                    i3 = 2;
                }
                f2 = f3 * (width2 / (8.0f / ((float) i3)));
            }
            BubbleFlyoutView bubbleFlyoutView = this.mFlyout;
            bubbleFlyoutView.setTranslationX(bubbleFlyoutView.getRestingTranslationX() + f2);
        }
    }

    /* access modifiers changed from: 0000 */
    public void onFlyoutTapped() {
        if (maybeShowStackUserEducation()) {
            this.mBubbleToExpandAfterFlyoutCollapse = null;
        } else {
            this.mBubbleToExpandAfterFlyoutCollapse = this.mBubbleData.getSelectedBubble();
        }
        this.mFlyout.removeCallbacks(this.mHideFlyout);
        this.mHideFlyout.run();
    }

    /* access modifiers changed from: 0000 */
    public void onFlyoutDragFinished(float f, float f2) {
        boolean isStackOnLeftSide = this.mStackAnimationController.isStackOnLeftSide();
        boolean z = true;
        boolean z2 = !isStackOnLeftSide ? f2 > 2000.0f : f2 < -2000.0f;
        boolean z3 = !isStackOnLeftSide ? f > ((float) this.mFlyout.getWidth()) * 0.25f : f < ((float) (-this.mFlyout.getWidth())) * 0.25f;
        boolean z4 = !isStackOnLeftSide ? f2 < 0.0f : f2 > 0.0f;
        if (!z2 && (!z3 || z4)) {
            z = false;
        }
        this.mFlyout.removeCallbacks(this.mHideFlyout);
        animateFlyoutCollapsed(z, f2);
        maybeShowStackUserEducation();
    }

    /* access modifiers changed from: 0000 */
    public void onGestureStart() {
        this.mIsGestureInProgress = true;
    }

    /* access modifiers changed from: 0000 */
    public void onGestureFinished() {
        this.mIsGestureInProgress = false;
        if (this.mIsExpanded) {
            this.mExpandedAnimationController.onGestureFinished();
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean passEventToMagnetizedObject(MotionEvent motionEvent) {
        MagnetizedObject<?> magnetizedObject = this.mMagnetizedObject;
        return magnetizedObject != null && magnetizedObject.maybeConsumeMotionEvent(motionEvent);
    }

    /* access modifiers changed from: private */
    public void animateDesaturateAndDarken(View view, boolean z) {
        this.mDesaturateAndDarkenTargetView = view;
        if (z) {
            view.setLayerType(2, this.mDesaturateAndDarkenPaint);
            this.mDesaturateAndDarkenAnimator.removeAllListeners();
            this.mDesaturateAndDarkenAnimator.start();
            return;
        }
        this.mDesaturateAndDarkenAnimator.removeAllListeners();
        this.mDesaturateAndDarkenAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                BubbleStackView.this.resetDesaturationAndDarken();
            }
        });
        this.mDesaturateAndDarkenAnimator.reverse();
    }

    /* access modifiers changed from: private */
    public void resetDesaturationAndDarken() {
        this.mDesaturateAndDarkenAnimator.removeAllListeners();
        this.mDesaturateAndDarkenAnimator.cancel();
        this.mDesaturateAndDarkenTargetView.setLayerType(0, null);
    }

    private void springInDismissTarget() {
        if (!this.mShowingDismiss) {
            this.mShowingDismiss = true;
            this.mDismissTargetContainer.bringToFront();
            this.mDismissTargetContainer.setZ(32766.0f);
            this.mDismissTargetContainer.setVisibility(0);
            this.mDismissTargetAnimator.cancel();
            PhysicsAnimator<View> physicsAnimator = this.mDismissTargetAnimator;
            physicsAnimator.spring(DynamicAnimation.TRANSLATION_Y, 0.0f, this.mDismissTargetSpring);
            physicsAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public void hideDismissTarget() {
        if (this.mShowingDismiss) {
            this.mShowingDismiss = false;
            PhysicsAnimator<View> physicsAnimator = this.mDismissTargetAnimator;
            physicsAnimator.spring(DynamicAnimation.TRANSLATION_Y, (float) this.mDismissTargetContainer.getHeight(), this.mDismissTargetSpring);
            physicsAnimator.withEndActions(new Runnable() {
                public final void run() {
                    BubbleStackView.this.lambda$hideDismissTarget$16$BubbleStackView();
                }
            });
            physicsAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hideDismissTarget$16 */
    public /* synthetic */ void lambda$hideDismissTarget$16$BubbleStackView() {
        this.mDismissTargetContainer.setVisibility(4);
    }

    private void animateFlyoutCollapsed(boolean z, float f) {
        float f2;
        boolean isStackOnLeftSide = this.mStackAnimationController.isStackOnLeftSide();
        this.mFlyoutTransitionSpring.getSpring().setStiffness(this.mBubbleToExpandAfterFlyoutCollapse != null ? 1500.0f : 200.0f);
        SpringAnimation springAnimation = this.mFlyoutTransitionSpring;
        springAnimation.setStartValue(this.mFlyoutDragDeltaX);
        SpringAnimation springAnimation2 = springAnimation;
        springAnimation2.setStartVelocity(f);
        SpringAnimation springAnimation3 = springAnimation2;
        if (z) {
            int width = this.mFlyout.getWidth();
            if (isStackOnLeftSide) {
                width = -width;
            }
            f2 = (float) width;
        } else {
            f2 = 0.0f;
        }
        springAnimation3.animateToFinalPosition(f2);
    }

    /* access modifiers changed from: 0000 */
    public float getExpandedViewY() {
        return (float) (getStatusBarHeight() + this.mBubbleSize + this.mBubblePaddingTop + this.mPointerHeight);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void animateInFlyoutForBubble(Bubble bubble) {
        FlyoutMessage flyoutMessage = bubble.getFlyoutMessage();
        BadgedImageView iconView = bubble.getIconView();
        if (!(flyoutMessage == null || flyoutMessage.message == null || !bubble.showFlyout())) {
            View view = this.mUserEducationView;
            if ((view == null || view.getVisibility() != 0) && !isExpanded() && !this.mIsExpansionAnimating && !this.mIsGestureInProgress && this.mBubbleToExpandAfterFlyoutCollapse == null && iconView != null) {
                this.mFlyoutDragDeltaX = 0.0f;
                clearFlyoutOnHide();
                this.mAfterFlyoutHidden = new Runnable(iconView) {
                    public final /* synthetic */ BadgedImageView f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        BubbleStackView.this.lambda$animateInFlyoutForBubble$17$BubbleStackView(this.f$1);
                    }
                };
                this.mFlyout.setVisibility(4);
                iconView.setDotState(1);
                post(new Runnable(flyoutMessage, bubble) {
                    public final /* synthetic */ FlyoutMessage f$1;
                    public final /* synthetic */ Bubble f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        BubbleStackView.this.lambda$animateInFlyoutForBubble$20$BubbleStackView(this.f$1, this.f$2);
                    }
                });
                this.mFlyout.removeCallbacks(this.mHideFlyout);
                this.mFlyout.postDelayed(this.mHideFlyout, 5000);
                logBubbleEvent(bubble, 16);
                return;
            }
        }
        if (iconView != null) {
            iconView.setDotState(0);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateInFlyoutForBubble$17 */
    public /* synthetic */ void lambda$animateInFlyoutForBubble$17$BubbleStackView(BadgedImageView badgedImageView) {
        this.mAfterFlyoutHidden = null;
        Bubble bubble = this.mBubbleToExpandAfterFlyoutCollapse;
        if (bubble != null) {
            this.mBubbleData.setSelectedBubble(bubble);
            this.mBubbleData.setExpanded(true);
            this.mBubbleToExpandAfterFlyoutCollapse = null;
        }
        badgedImageView.setDotState(0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateInFlyoutForBubble$20 */
    public /* synthetic */ void lambda$animateInFlyoutForBubble$20$BubbleStackView(FlyoutMessage flyoutMessage, Bubble bubble) {
        if (!isExpanded()) {
            FlyoutMessage flyoutMessage2 = flyoutMessage;
            this.mFlyout.setupFlyoutStartingAsDot(flyoutMessage2, this.mStackAnimationController.getStackPosition(), (float) getWidth(), this.mStackAnimationController.isStackOnLeftSide(), bubble.getIconView().getDotColor(), new Runnable() {
                public final void run() {
                    BubbleStackView.this.lambda$animateInFlyoutForBubble$19$BubbleStackView();
                }
            }, this.mAfterFlyoutHidden, bubble.getIconView().getDotCenter(), !bubble.showDot());
            this.mFlyout.bringToFront();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateInFlyoutForBubble$19 */
    public /* synthetic */ void lambda$animateInFlyoutForBubble$19$BubbleStackView() {
        $$Lambda$BubbleStackView$m82dgXt_d3RRAvy1wwm1VdRtrc r0 = new Runnable() {
            public final void run() {
                BubbleStackView.this.lambda$animateInFlyoutForBubble$18$BubbleStackView();
            }
        };
        this.mAnimateInFlyout = r0;
        this.mFlyout.postDelayed(r0, 200);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateInFlyoutForBubble$18 */
    public /* synthetic */ void lambda$animateInFlyoutForBubble$18$BubbleStackView() {
        int i;
        this.mFlyout.setVisibility(0);
        if (this.mStackAnimationController.isStackOnLeftSide()) {
            i = -this.mFlyout.getWidth();
        } else {
            i = this.mFlyout.getWidth();
        }
        this.mFlyoutDragDeltaX = (float) i;
        animateFlyoutCollapsed(false, 0.0f);
        this.mFlyout.postDelayed(this.mHideFlyout, 5000);
    }

    private void hideFlyoutImmediate() {
        clearFlyoutOnHide();
        this.mFlyout.removeCallbacks(this.mAnimateInFlyout);
        this.mFlyout.removeCallbacks(this.mHideFlyout);
        this.mFlyout.hideFlyout();
    }

    private void clearFlyoutOnHide() {
        this.mFlyout.removeCallbacks(this.mAnimateInFlyout);
        Runnable runnable = this.mAfterFlyoutHidden;
        if (runnable != null) {
            runnable.run();
            this.mAfterFlyoutHidden = null;
        }
    }

    public void getBoundsOnScreen(Rect rect) {
        View view = this.mUserEducationView;
        if (view == null || view.getVisibility() != 0) {
            if (!this.mIsExpanded) {
                if (getBubbleCount() > 0) {
                    this.mBubbleContainer.getChildAt(0).getBoundsOnScreen(rect);
                }
                int i = rect.top;
                int i2 = this.mBubbleTouchPadding;
                rect.top = i - i2;
                rect.left -= i2;
                rect.right += i2;
                rect.bottom += i2;
            } else {
                this.mBubbleContainer.getBoundsOnScreen(rect);
            }
            if (this.mFlyout.getVisibility() == 0) {
                Rect rect2 = new Rect();
                this.mFlyout.getBoundsOnScreen(rect2);
                rect.union(rect2);
            }
            return;
        }
        rect.set(0, 0, getWidth(), getHeight());
    }

    private int getStatusBarHeight() {
        int i = 0;
        if (getRootWindowInsets() == null) {
            return 0;
        }
        WindowInsets rootWindowInsets = getRootWindowInsets();
        int i2 = this.mStatusBarHeight;
        if (rootWindowInsets.getDisplayCutout() != null) {
            i = rootWindowInsets.getDisplayCutout().getSafeInsetTop();
        }
        return Math.max(i2, i);
    }

    private boolean isIntersecting(View view, float f, float f2) {
        int[] locationOnScreen = view.getLocationOnScreen();
        this.mTempLoc = locationOnScreen;
        this.mTempRect.set((float) locationOnScreen[0], (float) locationOnScreen[1], (float) (locationOnScreen[0] + view.getWidth()), (float) (this.mTempLoc[1] + view.getHeight()));
        return this.mTempRect.contains(f, f2);
    }

    private void requestUpdate() {
        if (!this.mViewUpdatedRequested && !this.mIsExpansionAnimating) {
            this.mViewUpdatedRequested = true;
            getViewTreeObserver().addOnPreDrawListener(this.mViewUpdater);
            invalidate();
        }
    }

    private void updateExpandedBubble() {
        this.mExpandedViewContainer.removeAllViews();
        if (this.mIsExpanded) {
            BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
            if (bubbleViewProvider != null) {
                BubbleExpandedView expandedView = bubbleViewProvider.getExpandedView();
                this.mExpandedViewContainer.addView(expandedView);
                expandedView.populateExpandedView();
                this.mExpandedViewContainer.setVisibility(0);
                this.mExpandedViewContainer.setAlpha(1.0f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateExpandedView() {
        this.mExpandedViewContainer.setVisibility(this.mIsExpanded ? 0 : 8);
        if (this.mIsExpanded) {
            float expandedViewY = getExpandedViewY();
            if (!this.mExpandedViewYAnim.isRunning()) {
                this.mExpandedViewContainer.setTranslationY(expandedViewY);
                BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
                if (bubbleViewProvider != null) {
                    bubbleViewProvider.getExpandedView().updateView();
                }
            } else {
                this.mExpandedViewYAnim.animateToFinalPosition(expandedViewY);
            }
        }
        this.mStackOnLeftOrWillBe = this.mStackAnimationController.isStackOnLeftSide();
        updateBubbleZOrdersAndDotPosition(false);
    }

    private void updateBubbleZOrdersAndDotPosition(boolean z) {
        int bubbleCount = getBubbleCount();
        for (int i = 0; i < bubbleCount; i++) {
            BadgedImageView badgedImageView = (BadgedImageView) this.mBubbleContainer.getChildAt(i);
            badgedImageView.setZ((float) ((this.mMaxBubbles * this.mBubbleElevation) - i));
            boolean dotPositionOnLeft = badgedImageView.getDotPositionOnLeft();
            boolean z2 = this.mStackOnLeftOrWillBe;
            if (dotPositionOnLeft == z2) {
                badgedImageView.setDotPosition(!z2, z);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: updatePointerPosition */
    public void lambda$updateOverflowBtnVisibility$8() {
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (bubbleViewProvider != null) {
            this.mExpandedBubble.getExpandedView().setPointerPosition((this.mExpandedAnimationController.getBubbleLeft(getBubbleIndex(bubbleViewProvider)) + (((float) this.mBubbleSize) / 2.0f)) - ((float) this.mExpandedViewContainer.getPaddingLeft()));
        }
    }

    public int getBubbleCount() {
        if (BubbleExperimentConfig.allowBubbleOverflow(this.mContext)) {
            return this.mBubbleContainer.getChildCount() - 1;
        }
        return this.mBubbleContainer.getChildCount();
    }

    /* access modifiers changed from: 0000 */
    public int getBubbleIndex(BubbleViewProvider bubbleViewProvider) {
        if (bubbleViewProvider == null) {
            return 0;
        }
        return this.mBubbleContainer.indexOfChild(bubbleViewProvider.getIconView());
    }

    public float getNormalizedXPosition() {
        BigDecimal bigDecimal = new BigDecimal((double) (getStackPosition().x / ((float) this.mDisplaySize.x)));
        RoundingMode roundingMode = RoundingMode.CEILING;
        return bigDecimal.setScale(4, RoundingMode.HALF_UP).floatValue();
    }

    public float getNormalizedYPosition() {
        BigDecimal bigDecimal = new BigDecimal((double) (getStackPosition().y / ((float) this.mDisplaySize.y)));
        RoundingMode roundingMode = RoundingMode.CEILING;
        return bigDecimal.setScale(4, RoundingMode.HALF_UP).floatValue();
    }

    public PointF getStackPosition() {
        return this.mStackAnimationController.getStackPosition();
    }

    private void logBubbleEvent(BubbleViewProvider bubbleViewProvider, int i) {
        if (bubbleViewProvider != null) {
            bubbleViewProvider.logUIEvent(getBubbleCount(), i, getNormalizedXPosition(), getNormalizedYPosition(), getBubbleIndex(bubbleViewProvider));
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean performBackPressIfNeeded() {
        if (isExpanded()) {
            BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
            if (bubbleViewProvider != null) {
                return bubbleViewProvider.getExpandedView().performBackPressIfNeeded();
            }
        }
        return false;
    }

    private boolean shouldShowBubblesEducation() {
        if (BubbleDebugConfig.forceShowUserEducation(getContext()) || !Prefs.getBoolean(getContext(), "HasSeenBubblesOnboarding", false)) {
            return true;
        }
        return false;
    }

    private boolean shouldShowManageEducation() {
        if (BubbleDebugConfig.forceShowUserEducation(getContext()) || !Prefs.getBoolean(getContext(), "HasSeenBubblesManageOnboarding", false)) {
            return true;
        }
        return false;
    }
}
