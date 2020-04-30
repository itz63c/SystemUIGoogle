package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Interpolator;
import com.android.internal.util.LatencyTracker;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.DejankUtils;
import com.android.systemui.Interpolators;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.FlingAnimationUtils.Builder;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public abstract class PanelViewController {
    public static final String TAG = PanelView.class.getSimpleName();
    /* access modifiers changed from: private */
    public boolean mAnimateAfterExpanding;
    /* access modifiers changed from: private */
    public boolean mAnimatingOnDown;
    PanelBar mBar;
    private Interpolator mBounceInterpolator;
    /* access modifiers changed from: private */
    public boolean mClosing;
    /* access modifiers changed from: private */
    public boolean mCollapsedAndHeadsUpOnDown;
    protected long mDownTime;
    private final DozeLog mDozeLog;
    private boolean mExpandLatencyTracking;
    private float mExpandedFraction = 0.0f;
    protected float mExpandedHeight = 0.0f;
    protected boolean mExpanding;
    protected ArrayList<PanelExpansionListener> mExpansionListeners = new ArrayList<>();
    private final FalsingManager mFalsingManager;
    private int mFixedDuration = -1;
    private FlingAnimationUtils mFlingAnimationUtils;
    private FlingAnimationUtils mFlingAnimationUtilsClosing;
    private FlingAnimationUtils mFlingAnimationUtilsDismissing;
    private final Runnable mFlingCollapseRunnable = new Runnable() {
        public void run() {
            PanelViewController panelViewController = PanelViewController.this;
            panelViewController.fling(0.0f, false, panelViewController.mNextCollapseSpeedUpFactor, false);
        }
    };
    /* access modifiers changed from: private */
    public boolean mGestureWaitForTouchSlop;
    /* access modifiers changed from: private */
    public boolean mHasLayoutedSinceDown;
    protected HeadsUpManagerPhone mHeadsUpManager;
    /* access modifiers changed from: private */
    public ValueAnimator mHeightAnimator;
    protected boolean mHintAnimationRunning;
    private float mHintDistance;
    /* access modifiers changed from: private */
    public boolean mIgnoreXTouchSlop;
    /* access modifiers changed from: private */
    public float mInitialOffsetOnTouch;
    /* access modifiers changed from: private */
    public float mInitialTouchX;
    /* access modifiers changed from: private */
    public float mInitialTouchY;
    /* access modifiers changed from: private */
    public boolean mInstantExpanding;
    /* access modifiers changed from: private */
    public boolean mJustPeeked;
    protected KeyguardBottomAreaView mKeyguardBottomArea;
    protected final KeyguardStateController mKeyguardStateController;
    private final LatencyTracker mLatencyTracker;
    protected boolean mLaunchingNotification;
    private LockscreenGestureLogger mLockscreenGestureLogger = new LockscreenGestureLogger();
    /* access modifiers changed from: private */
    public float mMinExpandHeight;
    /* access modifiers changed from: private */
    public boolean mMotionAborted;
    /* access modifiers changed from: private */
    public float mNextCollapseSpeedUpFactor = 1.0f;
    /* access modifiers changed from: private */
    public boolean mNotificationsDragEnabled;
    private boolean mOverExpandedBeforeFling;
    /* access modifiers changed from: private */
    public boolean mPanelClosedOnDown;
    private boolean mPanelUpdateWhenAnimatorEnds;
    /* access modifiers changed from: private */
    public ObjectAnimator mPeekAnimator;
    /* access modifiers changed from: private */
    public float mPeekHeight;
    /* access modifiers changed from: private */
    public boolean mPeekTouching;
    protected final Runnable mPostCollapseRunnable = new Runnable() {
        public void run() {
            PanelViewController.this.collapse(false, 1.0f);
        }
    };
    protected final Resources mResources;
    protected StatusBar mStatusBar;
    protected final SysuiStatusBarStateController mStatusBarStateController;
    protected final StatusBarTouchableRegionManager mStatusBarTouchableRegionManager;
    /* access modifiers changed from: private */
    public boolean mTouchAboveFalsingThreshold;
    /* access modifiers changed from: private */
    public boolean mTouchDisabled;
    protected int mTouchSlop;
    /* access modifiers changed from: private */
    public boolean mTouchSlopExceeded;
    protected boolean mTouchSlopExceededBeforeDown;
    /* access modifiers changed from: private */
    public boolean mTouchStartedInEmptyArea;
    protected boolean mTracking;
    /* access modifiers changed from: private */
    public int mTrackingPointer;
    private int mUnlockFalsingThreshold;
    /* access modifiers changed from: private */
    public boolean mUpdateFlingOnLayout;
    /* access modifiers changed from: private */
    public float mUpdateFlingVelocity;
    /* access modifiers changed from: private */
    public boolean mUpwardsWhenThresholdReached;
    /* access modifiers changed from: private */
    public final VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private boolean mVibrateOnOpening;
    private final VibratorHelper mVibratorHelper;
    /* access modifiers changed from: private */
    public final PanelView mView;
    /* access modifiers changed from: private */
    public String mViewName;

    public class OnConfigurationChangedListener implements OnConfigurationChangedListener {
        public OnConfigurationChangedListener() {
        }

        public void onConfigurationChanged(Configuration configuration) {
            PanelViewController.this.loadDimens();
        }
    }

    public class OnLayoutChangeListener implements android.view.View.OnLayoutChangeListener {
        public OnLayoutChangeListener() {
        }

        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            PanelViewController.this.mStatusBar.onPanelLaidOut();
            PanelViewController.this.requestPanelHeightUpdate();
            PanelViewController.this.mHasLayoutedSinceDown = true;
            if (PanelViewController.this.mUpdateFlingOnLayout) {
                PanelViewController.this.abortAnimations();
                PanelViewController panelViewController = PanelViewController.this;
                panelViewController.fling(panelViewController.mUpdateFlingVelocity, true);
                PanelViewController.this.mUpdateFlingOnLayout = false;
            }
        }
    }

    public class TouchHandler implements OnTouchListener {
        public TouchHandler() {
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (!PanelViewController.this.mInstantExpanding && PanelViewController.this.mNotificationsDragEnabled && !PanelViewController.this.mTouchDisabled && (!PanelViewController.this.mMotionAborted || motionEvent.getActionMasked() == 0)) {
                int findPointerIndex = motionEvent.findPointerIndex(PanelViewController.this.mTrackingPointer);
                if (findPointerIndex < 0) {
                    PanelViewController.this.mTrackingPointer = motionEvent.getPointerId(0);
                    findPointerIndex = 0;
                }
                float x = motionEvent.getX(findPointerIndex);
                float y = motionEvent.getY(findPointerIndex);
                boolean isScrolledToBottom = PanelViewController.this.isScrolledToBottom();
                int actionMasked = motionEvent.getActionMasked();
                int i = 1;
                if (actionMasked != 0) {
                    if (actionMasked != 1) {
                        if (actionMasked == 2) {
                            float access$1800 = y - PanelViewController.this.mInitialTouchY;
                            PanelViewController.this.addMovement(motionEvent);
                            if (isScrolledToBottom || PanelViewController.this.mTouchStartedInEmptyArea || PanelViewController.this.mAnimatingOnDown) {
                                float abs = Math.abs(access$1800);
                                PanelViewController panelViewController = PanelViewController.this;
                                if ((access$1800 < ((float) (-panelViewController.mTouchSlop)) || (panelViewController.mAnimatingOnDown && abs > ((float) PanelViewController.this.mTouchSlop))) && abs > Math.abs(x - PanelViewController.this.mInitialTouchX)) {
                                    PanelViewController.this.cancelHeightAnimator();
                                    PanelViewController panelViewController2 = PanelViewController.this;
                                    panelViewController2.startExpandMotion(x, y, true, panelViewController2.mExpandedHeight);
                                    return true;
                                }
                            }
                        } else if (actionMasked != 3) {
                            if (actionMasked != 5) {
                                if (actionMasked == 6) {
                                    int pointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                                    if (PanelViewController.this.mTrackingPointer == pointerId) {
                                        if (motionEvent.getPointerId(0) != pointerId) {
                                            i = 0;
                                        }
                                        PanelViewController.this.mTrackingPointer = motionEvent.getPointerId(i);
                                        PanelViewController.this.mInitialTouchX = motionEvent.getX(i);
                                        PanelViewController.this.mInitialTouchY = motionEvent.getY(i);
                                    }
                                }
                            } else if (PanelViewController.this.mStatusBarStateController.getState() == 1) {
                                PanelViewController.this.mMotionAborted = true;
                                PanelViewController.this.mVelocityTracker.clear();
                            }
                        }
                    }
                    PanelViewController.this.mVelocityTracker.clear();
                } else {
                    PanelViewController.this.mStatusBar.userActivity();
                    PanelViewController panelViewController3 = PanelViewController.this;
                    panelViewController3.mAnimatingOnDown = panelViewController3.mHeightAnimator != null;
                    PanelViewController.this.mMinExpandHeight = 0.0f;
                    PanelViewController.this.mDownTime = SystemClock.uptimeMillis();
                    if ((!PanelViewController.this.mAnimatingOnDown || !PanelViewController.this.mClosing || PanelViewController.this.mHintAnimationRunning) && PanelViewController.this.mPeekAnimator == null) {
                        PanelViewController.this.mInitialTouchY = y;
                        PanelViewController.this.mInitialTouchX = x;
                        PanelViewController panelViewController4 = PanelViewController.this;
                        panelViewController4.mTouchStartedInEmptyArea = !panelViewController4.isInContentBounds(x, y);
                        PanelViewController panelViewController5 = PanelViewController.this;
                        panelViewController5.mTouchSlopExceeded = panelViewController5.mTouchSlopExceededBeforeDown;
                        PanelViewController.this.mJustPeeked = false;
                        PanelViewController.this.mMotionAborted = false;
                        PanelViewController panelViewController6 = PanelViewController.this;
                        panelViewController6.mPanelClosedOnDown = panelViewController6.isFullyCollapsed();
                        PanelViewController.this.mCollapsedAndHeadsUpOnDown = false;
                        PanelViewController.this.mHasLayoutedSinceDown = false;
                        PanelViewController.this.mUpdateFlingOnLayout = false;
                        PanelViewController.this.mTouchAboveFalsingThreshold = false;
                        PanelViewController.this.addMovement(motionEvent);
                    } else {
                        PanelViewController.this.cancelHeightAnimator();
                        PanelViewController.this.cancelPeek();
                        PanelViewController.this.mTouchSlopExceeded = true;
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean z = false;
            if (!PanelViewController.this.mInstantExpanding && ((!PanelViewController.this.mTouchDisabled || motionEvent.getActionMasked() == 3) && (!PanelViewController.this.mMotionAborted || motionEvent.getActionMasked() == 0))) {
                if (!PanelViewController.this.mNotificationsDragEnabled) {
                    PanelViewController panelViewController = PanelViewController.this;
                    if (panelViewController.mTracking) {
                        panelViewController.onTrackingStopped(true);
                    }
                    return false;
                } else if (!PanelViewController.this.isFullyCollapsed() || !motionEvent.isFromSource(8194)) {
                    int findPointerIndex = motionEvent.findPointerIndex(PanelViewController.this.mTrackingPointer);
                    if (findPointerIndex < 0) {
                        PanelViewController.this.mTrackingPointer = motionEvent.getPointerId(0);
                        findPointerIndex = 0;
                    }
                    float x = motionEvent.getX(findPointerIndex);
                    float y = motionEvent.getY(findPointerIndex);
                    if (motionEvent.getActionMasked() == 0) {
                        PanelViewController panelViewController2 = PanelViewController.this;
                        panelViewController2.mGestureWaitForTouchSlop = panelViewController2.shouldGestureWaitForTouchSlop();
                        PanelViewController panelViewController3 = PanelViewController.this;
                        panelViewController3.mIgnoreXTouchSlop = panelViewController3.isFullyCollapsed() || PanelViewController.this.shouldGestureIgnoreXTouchSlop(x, y);
                    }
                    int actionMasked = motionEvent.getActionMasked();
                    if (actionMasked != 0) {
                        if (actionMasked != 1) {
                            if (actionMasked == 2) {
                                PanelViewController.this.addMovement(motionEvent);
                                float access$1800 = y - PanelViewController.this.mInitialTouchY;
                                if (Math.abs(access$1800) > ((float) PanelViewController.this.mTouchSlop) && (Math.abs(access$1800) > Math.abs(x - PanelViewController.this.mInitialTouchX) || PanelViewController.this.mIgnoreXTouchSlop)) {
                                    PanelViewController.this.mTouchSlopExceeded = true;
                                    if (PanelViewController.this.mGestureWaitForTouchSlop) {
                                        PanelViewController panelViewController4 = PanelViewController.this;
                                        if (!panelViewController4.mTracking && !panelViewController4.mCollapsedAndHeadsUpOnDown) {
                                            if (!PanelViewController.this.mJustPeeked && PanelViewController.this.mInitialOffsetOnTouch != 0.0f) {
                                                PanelViewController panelViewController5 = PanelViewController.this;
                                                panelViewController5.startExpandMotion(x, y, false, panelViewController5.mExpandedHeight);
                                                access$1800 = 0.0f;
                                            }
                                            PanelViewController.this.cancelHeightAnimator();
                                            PanelViewController.this.onTrackingStarted();
                                        }
                                    }
                                }
                                float max = Math.max(0.0f, PanelViewController.this.mInitialOffsetOnTouch + access$1800);
                                if (max > PanelViewController.this.mPeekHeight) {
                                    if (PanelViewController.this.mPeekAnimator != null) {
                                        PanelViewController.this.mPeekAnimator.cancel();
                                    }
                                    PanelViewController.this.mJustPeeked = false;
                                } else if (PanelViewController.this.mPeekAnimator == null && PanelViewController.this.mJustPeeked) {
                                    PanelViewController panelViewController6 = PanelViewController.this;
                                    panelViewController6.mInitialOffsetOnTouch = panelViewController6.mExpandedHeight;
                                    PanelViewController.this.mInitialTouchY = y;
                                    PanelViewController panelViewController7 = PanelViewController.this;
                                    panelViewController7.mMinExpandHeight = panelViewController7.mExpandedHeight;
                                    PanelViewController.this.mJustPeeked = false;
                                }
                                float max2 = Math.max(max, PanelViewController.this.mMinExpandHeight);
                                if ((-access$1800) >= ((float) PanelViewController.this.getFalsingThreshold())) {
                                    PanelViewController.this.mTouchAboveFalsingThreshold = true;
                                    PanelViewController panelViewController8 = PanelViewController.this;
                                    panelViewController8.mUpwardsWhenThresholdReached = panelViewController8.isDirectionUpwards(x, y);
                                }
                                if (!PanelViewController.this.mJustPeeked && ((!PanelViewController.this.mGestureWaitForTouchSlop || PanelViewController.this.mTracking) && !PanelViewController.this.isTrackingBlocked())) {
                                    PanelViewController.this.setExpandedHeightInternal(max2);
                                }
                            } else if (actionMasked != 3) {
                                if (actionMasked != 5) {
                                    if (actionMasked == 6) {
                                        int pointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                                        if (PanelViewController.this.mTrackingPointer == pointerId) {
                                            int i = motionEvent.getPointerId(0) != pointerId ? 0 : 1;
                                            float y2 = motionEvent.getY(i);
                                            float x2 = motionEvent.getX(i);
                                            PanelViewController.this.mTrackingPointer = motionEvent.getPointerId(i);
                                            PanelViewController panelViewController9 = PanelViewController.this;
                                            panelViewController9.startExpandMotion(x2, y2, true, panelViewController9.mExpandedHeight);
                                        }
                                    }
                                } else if (PanelViewController.this.mStatusBarStateController.getState() == 1) {
                                    PanelViewController.this.mMotionAborted = true;
                                    PanelViewController.this.endMotionEvent(motionEvent, x, y, true);
                                    return false;
                                }
                            }
                        }
                        PanelViewController.this.addMovement(motionEvent);
                        PanelViewController.this.endMotionEvent(motionEvent, x, y, false);
                    } else {
                        PanelViewController panelViewController10 = PanelViewController.this;
                        panelViewController10.startExpandMotion(x, y, false, panelViewController10.mExpandedHeight);
                        PanelViewController.this.mJustPeeked = false;
                        PanelViewController.this.mMinExpandHeight = 0.0f;
                        PanelViewController panelViewController11 = PanelViewController.this;
                        panelViewController11.mPanelClosedOnDown = panelViewController11.isFullyCollapsed();
                        PanelViewController.this.mHasLayoutedSinceDown = false;
                        PanelViewController.this.mUpdateFlingOnLayout = false;
                        PanelViewController.this.mMotionAborted = false;
                        PanelViewController panelViewController12 = PanelViewController.this;
                        panelViewController12.mPeekTouching = panelViewController12.mPanelClosedOnDown;
                        PanelViewController.this.mDownTime = SystemClock.uptimeMillis();
                        PanelViewController.this.mTouchAboveFalsingThreshold = false;
                        PanelViewController panelViewController13 = PanelViewController.this;
                        panelViewController13.mCollapsedAndHeadsUpOnDown = panelViewController13.isFullyCollapsed() && PanelViewController.this.mHeadsUpManager.hasPinnedHeadsUp();
                        PanelViewController.this.addMovement(motionEvent);
                        if (!PanelViewController.this.mGestureWaitForTouchSlop || ((PanelViewController.this.mHeightAnimator != null && !PanelViewController.this.mHintAnimationRunning) || PanelViewController.this.mPeekAnimator != null)) {
                            PanelViewController panelViewController14 = PanelViewController.this;
                            panelViewController14.mTouchSlopExceeded = (panelViewController14.mHeightAnimator != null && !PanelViewController.this.mHintAnimationRunning) || PanelViewController.this.mPeekAnimator != null || PanelViewController.this.mTouchSlopExceededBeforeDown;
                            PanelViewController.this.cancelHeightAnimator();
                            PanelViewController.this.cancelPeek();
                            PanelViewController.this.onTrackingStarted();
                        }
                        if (PanelViewController.this.isFullyCollapsed() && !PanelViewController.this.mHeadsUpManager.hasPinnedHeadsUp() && !PanelViewController.this.mStatusBar.isBouncerShowing()) {
                            PanelViewController.this.startOpening(motionEvent);
                        }
                    }
                    if (!PanelViewController.this.mGestureWaitForTouchSlop || PanelViewController.this.mTracking) {
                        z = true;
                    }
                } else {
                    if (motionEvent.getAction() == 1) {
                        PanelViewController.this.expand(true);
                    }
                    return true;
                }
            }
            return z;
        }
    }

    public abstract OnLayoutChangeListener createLayoutChangeListener();

    /* access modifiers changed from: protected */
    public abstract OnConfigurationChangedListener createOnConfigurationChangedListener();

    /* access modifiers changed from: protected */
    public abstract TouchHandler createTouchHandler();

    /* access modifiers changed from: protected */
    public abstract boolean fullyExpandedClearAllVisible();

    /* access modifiers changed from: protected */
    public abstract int getClearAllHeight();

    /* access modifiers changed from: protected */
    public abstract int getMaxPanelHeight();

    /* access modifiers changed from: protected */
    public abstract float getOpeningHeight();

    /* access modifiers changed from: protected */
    public abstract float getOverExpansionAmount();

    /* access modifiers changed from: protected */
    public abstract float getOverExpansionPixels();

    /* access modifiers changed from: protected */
    public abstract float getPeekHeight();

    /* access modifiers changed from: protected */
    public abstract boolean isClearAllVisible();

    /* access modifiers changed from: protected */
    public abstract boolean isInContentBounds(float f, float f2);

    /* access modifiers changed from: protected */
    public abstract boolean isPanelVisibleBecauseOfHeadsUp();

    /* access modifiers changed from: protected */
    public abstract boolean isScrolledToBottom();

    /* access modifiers changed from: protected */
    public abstract boolean isTrackingBlocked();

    /* access modifiers changed from: protected */
    public void onExpandingStarted() {
    }

    /* access modifiers changed from: protected */
    public abstract void onHeightUpdated(float f);

    /* access modifiers changed from: protected */
    public abstract boolean onMiddleClicked();

    public abstract void resetViews(boolean z);

    /* access modifiers changed from: protected */
    public abstract void setOverExpansion(float f, boolean z);

    /* access modifiers changed from: protected */
    public abstract boolean shouldGestureIgnoreXTouchSlop(float f, float f2);

    /* access modifiers changed from: protected */
    public abstract boolean shouldGestureWaitForTouchSlop();

    /* access modifiers changed from: protected */
    public abstract boolean shouldUseDismissingAnimation();

    /* access modifiers changed from: protected */
    public void onExpandingFinished() {
        this.mBar.onExpandingFinished();
    }

    /* access modifiers changed from: private */
    public void notifyExpandingStarted() {
        if (!this.mExpanding) {
            this.mExpanding = true;
            onExpandingStarted();
        }
    }

    /* access modifiers changed from: protected */
    public final void notifyExpandingFinished() {
        endClosing();
        if (this.mExpanding) {
            this.mExpanding = false;
            onExpandingFinished();
        }
    }

    private void runPeekAnimation(long j, float f, final boolean z) {
        this.mPeekHeight = f;
        if (this.mHeightAnimator == null) {
            ObjectAnimator objectAnimator = this.mPeekAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            ObjectAnimator duration = ObjectAnimator.ofFloat(this, "expandedHeight", new float[]{this.mPeekHeight}).setDuration(j);
            this.mPeekAnimator = duration;
            duration.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
            this.mPeekAnimator.addListener(new AnimatorListenerAdapter() {
                private boolean mCancelled;

                public void onAnimationCancel(Animator animator) {
                    this.mCancelled = true;
                }

                public void onAnimationEnd(Animator animator) {
                    PanelViewController.this.mPeekAnimator = null;
                    if (!this.mCancelled && z) {
                        PanelViewController.this.mView.postOnAnimation(PanelViewController.this.mPostCollapseRunnable);
                    }
                }
            });
            notifyExpandingStarted();
            this.mPeekAnimator.start();
            this.mJustPeeked = true;
        }
    }

    public PanelViewController(PanelView panelView, FalsingManager falsingManager, DozeLog dozeLog, KeyguardStateController keyguardStateController, SysuiStatusBarStateController sysuiStatusBarStateController, VibratorHelper vibratorHelper, LatencyTracker latencyTracker, Builder builder, StatusBarTouchableRegionManager statusBarTouchableRegionManager) {
        this.mView = panelView;
        panelView.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            public void onViewDetachedFromWindow(View view) {
            }

            public void onViewAttachedToWindow(View view) {
                PanelViewController panelViewController = PanelViewController.this;
                panelViewController.mViewName = panelViewController.mResources.getResourceName(panelViewController.mView.getId());
            }
        });
        this.mView.addOnLayoutChangeListener(createLayoutChangeListener());
        this.mView.setOnTouchListener(createTouchHandler());
        this.mView.setOnConfigurationChangedListener(createOnConfigurationChangedListener());
        this.mResources = this.mView.getResources();
        this.mKeyguardStateController = keyguardStateController;
        this.mStatusBarStateController = sysuiStatusBarStateController;
        builder.reset();
        builder.setMaxLengthSeconds(0.6f);
        builder.setSpeedUpFactor(0.6f);
        this.mFlingAnimationUtils = builder.build();
        builder.reset();
        builder.setMaxLengthSeconds(0.5f);
        builder.setSpeedUpFactor(0.6f);
        this.mFlingAnimationUtilsClosing = builder.build();
        builder.reset();
        builder.setMaxLengthSeconds(0.5f);
        builder.setSpeedUpFactor(0.6f);
        builder.setX2(0.6f);
        builder.setY2(0.84f);
        this.mFlingAnimationUtilsDismissing = builder.build();
        this.mLatencyTracker = latencyTracker;
        this.mBounceInterpolator = new BounceInterpolator();
        this.mFalsingManager = falsingManager;
        this.mDozeLog = dozeLog;
        this.mNotificationsDragEnabled = this.mResources.getBoolean(C2007R$bool.config_enableNotificationShadeDrag);
        this.mVibratorHelper = vibratorHelper;
        this.mVibrateOnOpening = this.mResources.getBoolean(C2007R$bool.config_vibrateOnIconAnimation);
        this.mStatusBarTouchableRegionManager = statusBarTouchableRegionManager;
    }

    /* access modifiers changed from: protected */
    public void loadDimens() {
        this.mTouchSlop = ViewConfiguration.get(this.mView.getContext()).getScaledTouchSlop();
        this.mHintDistance = this.mResources.getDimension(C2009R$dimen.hint_move_distance);
        this.mUnlockFalsingThreshold = this.mResources.getDimensionPixelSize(C2009R$dimen.unlock_falsing_threshold);
    }

    /* access modifiers changed from: private */
    public void addMovement(MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX() - motionEvent.getX();
        float rawY = motionEvent.getRawY() - motionEvent.getY();
        motionEvent.offsetLocation(rawX, rawY);
        this.mVelocityTracker.addMovement(motionEvent);
        motionEvent.offsetLocation(-rawX, -rawY);
    }

    public void setTouchAndAnimationDisabled(boolean z) {
        this.mTouchDisabled = z;
        if (z) {
            cancelHeightAnimator();
            if (this.mTracking) {
                onTrackingStopped(true);
            }
            notifyExpandingFinished();
        }
    }

    public void startExpandLatencyTracking() {
        if (this.mLatencyTracker.isEnabled()) {
            this.mLatencyTracker.onActionStart(0);
            this.mExpandLatencyTracking = true;
        }
    }

    /* access modifiers changed from: private */
    public void startOpening(MotionEvent motionEvent) {
        runPeekAnimation(200, getOpeningHeight(), false);
        notifyBarPanelExpansionChanged();
        maybeVibrateOnOpening();
        float displayWidth = this.mStatusBar.getDisplayWidth();
        float displayHeight = this.mStatusBar.getDisplayHeight();
        this.mLockscreenGestureLogger.writeAtFractionalPosition(1328, (int) ((motionEvent.getX() / displayWidth) * 100.0f), (int) ((motionEvent.getY() / displayHeight) * 100.0f), this.mStatusBar.getRotation());
    }

    /* access modifiers changed from: protected */
    public void maybeVibrateOnOpening() {
        if (this.mVibrateOnOpening) {
            this.mVibratorHelper.vibrate(2);
        }
    }

    /* access modifiers changed from: private */
    public boolean isDirectionUpwards(float f, float f2) {
        float f3 = f - this.mInitialTouchX;
        float f4 = f2 - this.mInitialTouchY;
        boolean z = false;
        if (f4 >= 0.0f) {
            return false;
        }
        if (Math.abs(f4) >= Math.abs(f3)) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public void startExpandingFromPeek() {
        this.mStatusBar.handlePeekToExpandTransistion();
    }

    /* access modifiers changed from: protected */
    public void startExpandMotion(float f, float f2, boolean z, float f3) {
        this.mInitialOffsetOnTouch = f3;
        this.mInitialTouchY = f2;
        this.mInitialTouchX = f;
        if (z) {
            this.mTouchSlopExceeded = true;
            setExpandedHeight(f3);
            onTrackingStarted();
        }
    }

    /* access modifiers changed from: private */
    public void endMotionEvent(MotionEvent motionEvent, float f, float f2, boolean z) {
        this.mTrackingPointer = -1;
        boolean z2 = true;
        if ((this.mTracking && this.mTouchSlopExceeded) || Math.abs(f - this.mInitialTouchX) > ((float) this.mTouchSlop) || Math.abs(f2 - this.mInitialTouchY) > ((float) this.mTouchSlop) || motionEvent.getActionMasked() == 3 || z) {
            this.mVelocityTracker.computeCurrentVelocity(1000);
            float yVelocity = this.mVelocityTracker.getYVelocity();
            boolean z3 = flingExpands(yVelocity, (float) Math.hypot((double) this.mVelocityTracker.getXVelocity(), (double) this.mVelocityTracker.getYVelocity()), f, f2) || motionEvent.getActionMasked() == 3 || z;
            this.mDozeLog.traceFling(z3, this.mTouchAboveFalsingThreshold, this.mStatusBar.isFalsingThresholdNeeded(), this.mStatusBar.isWakeUpComingFromTouch());
            if (!z3 && this.mStatusBarStateController.getState() == 1) {
                float displayDensity = this.mStatusBar.getDisplayDensity();
                this.mLockscreenGestureLogger.write(186, (int) Math.abs((f2 - this.mInitialTouchY) / displayDensity), (int) Math.abs(yVelocity / displayDensity));
            }
            fling(yVelocity, z3, isFalseTouch(f, f2));
            onTrackingStopped(z3);
            if (!z3 || !this.mPanelClosedOnDown || this.mHasLayoutedSinceDown) {
                z2 = false;
            }
            this.mUpdateFlingOnLayout = z2;
            if (z2) {
                this.mUpdateFlingVelocity = yVelocity;
            }
        } else if (!this.mPanelClosedOnDown || this.mHeadsUpManager.hasPinnedHeadsUp() || this.mTracking || this.mStatusBar.isBouncerShowing() || this.mKeyguardStateController.isKeyguardFadingAway()) {
            if (!this.mStatusBar.isBouncerShowing()) {
                onTrackingStopped(onEmptySpaceClick(this.mInitialTouchX));
            }
        } else if (SystemClock.uptimeMillis() - this.mDownTime < ((long) ViewConfiguration.getLongPressTimeout())) {
            runPeekAnimation(360, getPeekHeight(), true);
        } else {
            this.mView.postOnAnimation(this.mPostCollapseRunnable);
        }
        this.mVelocityTracker.clear();
        this.mPeekTouching = false;
    }

    /* access modifiers changed from: protected */
    public float getCurrentExpandVelocity() {
        this.mVelocityTracker.computeCurrentVelocity(1000);
        return this.mVelocityTracker.getYVelocity();
    }

    /* access modifiers changed from: private */
    public int getFalsingThreshold() {
        return (int) (((float) this.mUnlockFalsingThreshold) * (this.mStatusBar.isWakeUpComingFromTouch() ? 1.5f : 1.0f));
    }

    /* access modifiers changed from: protected */
    public void onTrackingStopped(boolean z) {
        this.mTracking = false;
        this.mBar.onTrackingStopped(z);
        notifyBarPanelExpansionChanged();
    }

    /* access modifiers changed from: protected */
    public void onTrackingStarted() {
        endClosing();
        this.mTracking = true;
        this.mBar.onTrackingStarted();
        notifyExpandingStarted();
        notifyBarPanelExpansionChanged();
    }

    /* access modifiers changed from: protected */
    public void cancelHeightAnimator() {
        ValueAnimator valueAnimator = this.mHeightAnimator;
        if (valueAnimator != null) {
            if (valueAnimator.isRunning()) {
                this.mPanelUpdateWhenAnimatorEnds = false;
            }
            this.mHeightAnimator.cancel();
        }
        endClosing();
    }

    private void endClosing() {
        if (this.mClosing) {
            this.mClosing = false;
            onClosingFinished();
        }
    }

    /* access modifiers changed from: protected */
    public boolean flingExpands(float f, float f2, float f3, float f4) {
        boolean z = true;
        if (this.mFalsingManager.isUnlockingDisabled() || isFalseTouch(f3, f4)) {
            return true;
        }
        if (Math.abs(f2) < this.mFlingAnimationUtils.getMinVelocityPxPerSecond()) {
            return shouldExpandWhenNotFlinging();
        }
        if (f <= 0.0f) {
            z = false;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public boolean shouldExpandWhenNotFlinging() {
        return getExpandedFraction() > 0.5f;
    }

    private boolean isFalseTouch(float f, float f2) {
        if (!this.mStatusBar.isFalsingThresholdNeeded()) {
            return false;
        }
        if (this.mFalsingManager.isClassifierEnabled()) {
            return this.mFalsingManager.isFalseTouch();
        }
        if (!this.mTouchAboveFalsingThreshold) {
            return true;
        }
        if (this.mUpwardsWhenThresholdReached) {
            return false;
        }
        return !isDirectionUpwards(f, f2);
    }

    /* access modifiers changed from: protected */
    public void fling(float f, boolean z) {
        fling(f, z, 1.0f, false);
    }

    /* access modifiers changed from: protected */
    public void fling(float f, boolean z, boolean z2) {
        fling(f, z, 1.0f, z2);
    }

    /* access modifiers changed from: protected */
    public void fling(float f, boolean z, float f2, boolean z2) {
        cancelPeek();
        float maxPanelHeight = z ? (float) getMaxPanelHeight() : 0.0f;
        if (!z) {
            this.mClosing = true;
        }
        flingToHeight(f, z, maxPanelHeight, f2, z2);
    }

    /* access modifiers changed from: protected */
    public void flingToHeight(float f, boolean z, float f2, float f3, boolean z2) {
        boolean z3 = true;
        final boolean z4 = z && fullyExpandedClearAllVisible() && this.mExpandedHeight < ((float) (getMaxPanelHeight() - getClearAllHeight())) && !isClearAllVisible();
        if (z4) {
            f2 = (float) (getMaxPanelHeight() - getClearAllHeight());
        }
        float f4 = f2;
        if (f4 == this.mExpandedHeight || (getOverExpansionAmount() > 0.0f && z)) {
            notifyExpandingFinished();
            return;
        }
        if (getOverExpansionAmount() <= 0.0f) {
            z3 = false;
        }
        this.mOverExpandedBeforeFling = z3;
        ValueAnimator createHeightAnimator = createHeightAnimator(f4);
        if (z) {
            if (z2 && f < 0.0f) {
                f = 0.0f;
            }
            this.mFlingAnimationUtils.apply(createHeightAnimator, this.mExpandedHeight, f4, f, (float) this.mView.getHeight());
            if (f == 0.0f) {
                createHeightAnimator.setDuration(350);
            }
        } else {
            if (!shouldUseDismissingAnimation()) {
                this.mFlingAnimationUtilsClosing.apply(createHeightAnimator, this.mExpandedHeight, f4, f, (float) this.mView.getHeight());
            } else if (f == 0.0f) {
                createHeightAnimator.setInterpolator(Interpolators.PANEL_CLOSE_ACCELERATED);
                createHeightAnimator.setDuration((long) (((this.mExpandedHeight / ((float) this.mView.getHeight())) * 100.0f) + 200.0f));
            } else {
                this.mFlingAnimationUtilsDismissing.apply(createHeightAnimator, this.mExpandedHeight, f4, f, (float) this.mView.getHeight());
            }
            if (f == 0.0f) {
                createHeightAnimator.setDuration((long) (((float) createHeightAnimator.getDuration()) / f3));
            }
            int i = this.mFixedDuration;
            if (i != -1) {
                createHeightAnimator.setDuration((long) i);
            }
        }
        createHeightAnimator.addListener(new AnimatorListenerAdapter() {
            private boolean mCancelled;

            public void onAnimationCancel(Animator animator) {
                this.mCancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (z4 && !this.mCancelled) {
                    PanelViewController panelViewController = PanelViewController.this;
                    panelViewController.setExpandedHeightInternal((float) panelViewController.getMaxPanelHeight());
                }
                PanelViewController.this.setAnimator(null);
                if (!this.mCancelled) {
                    PanelViewController.this.notifyExpandingFinished();
                }
                PanelViewController.this.notifyBarPanelExpansionChanged();
            }
        });
        setAnimator(createHeightAnimator);
        createHeightAnimator.start();
    }

    public void setExpandedHeight(float f) {
        setExpandedHeightInternal(f + getOverExpansionPixels());
    }

    /* access modifiers changed from: protected */
    public void requestPanelHeightUpdate() {
        float maxPanelHeight = (float) getMaxPanelHeight();
        if (isFullyCollapsed() || maxPanelHeight == this.mExpandedHeight || this.mPeekAnimator != null || this.mPeekTouching || (this.mTracking && !isTrackingBlocked())) {
            return;
        }
        if (this.mHeightAnimator != null) {
            this.mPanelUpdateWhenAnimatorEnds = true;
        } else {
            setExpandedHeight(maxPanelHeight);
        }
    }

    public void setExpandedHeightInternal(float f) {
        float f2 = 0.0f;
        if (this.mExpandLatencyTracking && f != 0.0f) {
            DejankUtils.postAfterTraversal(new Runnable() {
                public final void run() {
                    PanelViewController.this.lambda$setExpandedHeightInternal$0$PanelViewController();
                }
            });
            this.mExpandLatencyTracking = false;
        }
        float maxPanelHeight = ((float) getMaxPanelHeight()) - getOverExpansionAmount();
        if (this.mHeightAnimator == null) {
            float max = Math.max(0.0f, f - maxPanelHeight);
            if (getOverExpansionPixels() != max && this.mTracking) {
                setOverExpansion(max, true);
            }
            this.mExpandedHeight = Math.min(f, maxPanelHeight) + getOverExpansionAmount();
        } else {
            this.mExpandedHeight = f;
            if (this.mOverExpandedBeforeFling) {
                setOverExpansion(Math.max(0.0f, f - maxPanelHeight), false);
            }
        }
        float f3 = this.mExpandedHeight;
        if (f3 < 1.0f && f3 != 0.0f && this.mClosing) {
            this.mExpandedHeight = 0.0f;
            ValueAnimator valueAnimator = this.mHeightAnimator;
            if (valueAnimator != null) {
                valueAnimator.end();
            }
        }
        if (maxPanelHeight != 0.0f) {
            f2 = this.mExpandedHeight / maxPanelHeight;
        }
        this.mExpandedFraction = Math.min(1.0f, f2);
        onHeightUpdated(this.mExpandedHeight);
        notifyBarPanelExpansionChanged();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setExpandedHeightInternal$0 */
    public /* synthetic */ void lambda$setExpandedHeightInternal$0$PanelViewController() {
        this.mLatencyTracker.onActionEnd(0);
    }

    public void setExpandedFraction(float f) {
        setExpandedHeight(((float) getMaxPanelHeight()) * f);
    }

    public float getExpandedHeight() {
        return this.mExpandedHeight;
    }

    public float getExpandedFraction() {
        return this.mExpandedFraction;
    }

    public boolean isFullyExpanded() {
        return this.mExpandedHeight >= ((float) getMaxPanelHeight());
    }

    public boolean isFullyCollapsed() {
        return this.mExpandedFraction <= 0.0f;
    }

    public boolean isCollapsing() {
        return this.mClosing || this.mLaunchingNotification;
    }

    public boolean isTracking() {
        return this.mTracking;
    }

    public void setBar(PanelBar panelBar) {
        this.mBar = panelBar;
    }

    public void collapse(boolean z, float f) {
        if (canPanelBeCollapsed()) {
            cancelHeightAnimator();
            notifyExpandingStarted();
            this.mClosing = true;
            if (z) {
                this.mNextCollapseSpeedUpFactor = f;
                this.mView.postDelayed(this.mFlingCollapseRunnable, 120);
                return;
            }
            fling(0.0f, false, f, false);
        }
    }

    public boolean canPanelBeCollapsed() {
        return !isFullyCollapsed() && !this.mTracking && !this.mClosing;
    }

    public void cancelPeek() {
        boolean z;
        ObjectAnimator objectAnimator = this.mPeekAnimator;
        if (objectAnimator != null) {
            z = true;
            objectAnimator.cancel();
        } else {
            z = false;
        }
        if (z) {
            notifyBarPanelExpansionChanged();
        }
    }

    public void expand(boolean z) {
        if (isFullyCollapsed() || isCollapsing()) {
            this.mInstantExpanding = true;
            this.mAnimateAfterExpanding = z;
            this.mUpdateFlingOnLayout = false;
            abortAnimations();
            cancelPeek();
            if (this.mTracking) {
                onTrackingStopped(true);
            }
            if (this.mExpanding) {
                notifyExpandingFinished();
            }
            notifyBarPanelExpansionChanged();
            this.mView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if (!PanelViewController.this.mInstantExpanding) {
                        PanelViewController.this.mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        return;
                    }
                    if (PanelViewController.this.mStatusBar.getNotificationShadeWindowView().isVisibleToUser()) {
                        PanelViewController.this.mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (PanelViewController.this.mAnimateAfterExpanding) {
                            PanelViewController.this.notifyExpandingStarted();
                            PanelViewController.this.fling(0.0f, true);
                        } else {
                            PanelViewController.this.setExpandedFraction(1.0f);
                        }
                        PanelViewController.this.mInstantExpanding = false;
                    }
                }
            });
            this.mView.requestLayout();
        }
    }

    public void instantCollapse() {
        abortAnimations();
        setExpandedFraction(0.0f);
        if (this.mExpanding) {
            notifyExpandingFinished();
        }
        if (this.mInstantExpanding) {
            this.mInstantExpanding = false;
            notifyBarPanelExpansionChanged();
        }
    }

    /* access modifiers changed from: private */
    public void abortAnimations() {
        cancelPeek();
        cancelHeightAnimator();
        this.mView.removeCallbacks(this.mPostCollapseRunnable);
        this.mView.removeCallbacks(this.mFlingCollapseRunnable);
    }

    /* access modifiers changed from: protected */
    public void onClosingFinished() {
        this.mBar.onClosingFinished();
    }

    /* access modifiers changed from: protected */
    public void startUnlockHintAnimation() {
        if (this.mHeightAnimator == null && !this.mTracking) {
            cancelPeek();
            notifyExpandingStarted();
            startUnlockHintAnimationPhase1(new Runnable() {
                public final void run() {
                    PanelViewController.this.lambda$startUnlockHintAnimation$1$PanelViewController();
                }
            });
            onUnlockHintStarted();
            this.mHintAnimationRunning = true;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startUnlockHintAnimation$1 */
    public /* synthetic */ void lambda$startUnlockHintAnimation$1$PanelViewController() {
        notifyExpandingFinished();
        onUnlockHintFinished();
        this.mHintAnimationRunning = false;
    }

    /* access modifiers changed from: protected */
    public void onUnlockHintFinished() {
        this.mStatusBar.onHintFinished();
    }

    /* access modifiers changed from: protected */
    public void onUnlockHintStarted() {
        this.mStatusBar.onUnlockHintStarted();
    }

    public boolean isUnlockHintRunning() {
        return this.mHintAnimationRunning;
    }

    private void startUnlockHintAnimationPhase1(final Runnable runnable) {
        ValueAnimator createHeightAnimator = createHeightAnimator(Math.max(0.0f, ((float) getMaxPanelHeight()) - this.mHintDistance));
        createHeightAnimator.setDuration(250);
        createHeightAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        createHeightAnimator.addListener(new AnimatorListenerAdapter() {
            private boolean mCancelled;

            public void onAnimationCancel(Animator animator) {
                this.mCancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (this.mCancelled) {
                    PanelViewController.this.setAnimator(null);
                    runnable.run();
                    return;
                }
                PanelViewController.this.startUnlockHintAnimationPhase2(runnable);
            }
        });
        createHeightAnimator.start();
        setAnimator(createHeightAnimator);
        View[] viewArr = {this.mKeyguardBottomArea.getIndicationArea(), this.mStatusBar.getAmbientIndicationContainer()};
        for (int i = 0; i < 2; i++) {
            View view = viewArr[i];
            if (view != null) {
                view.animate().translationY(-this.mHintDistance).setDuration(250).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).withEndAction(new Runnable(view) {
                    public final /* synthetic */ View f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        PanelViewController.this.lambda$startUnlockHintAnimationPhase1$2$PanelViewController(this.f$1);
                    }
                }).start();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startUnlockHintAnimationPhase1$2 */
    public /* synthetic */ void lambda$startUnlockHintAnimationPhase1$2$PanelViewController(View view) {
        view.animate().translationY(0.0f).setDuration(450).setInterpolator(this.mBounceInterpolator).start();
    }

    /* access modifiers changed from: private */
    public void setAnimator(ValueAnimator valueAnimator) {
        this.mHeightAnimator = valueAnimator;
        if (valueAnimator == null && this.mPanelUpdateWhenAnimatorEnds) {
            this.mPanelUpdateWhenAnimatorEnds = false;
            requestPanelHeightUpdate();
        }
    }

    /* access modifiers changed from: private */
    public void startUnlockHintAnimationPhase2(final Runnable runnable) {
        ValueAnimator createHeightAnimator = createHeightAnimator((float) getMaxPanelHeight());
        createHeightAnimator.setDuration(450);
        createHeightAnimator.setInterpolator(this.mBounceInterpolator);
        createHeightAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PanelViewController.this.setAnimator(null);
                runnable.run();
                PanelViewController.this.notifyBarPanelExpansionChanged();
            }
        });
        createHeightAnimator.start();
        setAnimator(createHeightAnimator);
    }

    private ValueAnimator createHeightAnimator(float f) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.mExpandedHeight, f});
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PanelViewController.this.lambda$createHeightAnimator$3$PanelViewController(valueAnimator);
            }
        });
        return ofFloat;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createHeightAnimator$3 */
    public /* synthetic */ void lambda$createHeightAnimator$3$PanelViewController(ValueAnimator valueAnimator) {
        setExpandedHeightInternal(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: protected */
    public void notifyBarPanelExpansionChanged() {
        PanelBar panelBar = this.mBar;
        if (panelBar != null) {
            float f = this.mExpandedFraction;
            panelBar.panelExpansionChanged(f, f > 0.0f || this.mPeekAnimator != null || this.mInstantExpanding || isPanelVisibleBecauseOfHeadsUp() || this.mTracking || this.mHeightAnimator != null);
        }
        for (int i = 0; i < this.mExpansionListeners.size(); i++) {
            ((PanelExpansionListener) this.mExpansionListeners.get(i)).onPanelExpansionChanged(this.mExpandedFraction, this.mTracking);
        }
    }

    public void addExpansionListener(PanelExpansionListener panelExpansionListener) {
        this.mExpansionListeners.add(panelExpansionListener);
    }

    /* access modifiers changed from: protected */
    public boolean onEmptySpaceClick(float f) {
        if (this.mHintAnimationRunning) {
            return true;
        }
        return onMiddleClicked();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Object[] objArr = new Object[11];
        objArr[0] = getClass().getSimpleName();
        objArr[1] = Float.valueOf(getExpandedHeight());
        objArr[2] = Integer.valueOf(getMaxPanelHeight());
        String str = "T";
        String str2 = "f";
        objArr[3] = this.mClosing ? str : str2;
        objArr[4] = this.mTracking ? str : str2;
        objArr[5] = this.mJustPeeked ? str : str2;
        ObjectAnimator objectAnimator = this.mPeekAnimator;
        objArr[6] = objectAnimator;
        String str3 = " (started)";
        String str4 = "";
        objArr[7] = (objectAnimator == null || !objectAnimator.isStarted()) ? str4 : str3;
        ValueAnimator valueAnimator = this.mHeightAnimator;
        objArr[8] = valueAnimator;
        if (valueAnimator == null || !valueAnimator.isStarted()) {
            str3 = str4;
        }
        objArr[9] = str3;
        if (!this.mTouchDisabled) {
            str = str2;
        }
        objArr[10] = str;
        printWriter.println(String.format("[PanelView(%s): expandedHeight=%f maxPanelHeight=%d closing=%s tracking=%s justPeeked=%s peekAnim=%s%s timeAnim=%s%s touchDisabled=%s]", objArr));
    }

    public void setHeadsUpManager(HeadsUpManagerPhone headsUpManagerPhone) {
        this.mHeadsUpManager = headsUpManagerPhone;
    }

    public void setLaunchingNotification(boolean z) {
        this.mLaunchingNotification = z;
    }

    public void collapseWithDuration(int i) {
        this.mFixedDuration = i;
        collapse(false, 1.0f);
        this.mFixedDuration = -1;
    }

    public ViewGroup getView() {
        return this.mView;
    }

    public boolean isEnabled() {
        return this.mView.isEnabled();
    }
}
