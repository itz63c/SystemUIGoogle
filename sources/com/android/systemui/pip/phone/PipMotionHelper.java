package com.android.systemui.pip.phone;

import android.app.ActivityManager.StackInfo;
import android.app.IActivityTaskManager;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import com.android.systemui.pip.PipSnapAlgorithm;
import com.android.systemui.pip.PipTaskOrganizer;
import com.android.systemui.pip.phone.PipAppOpsListener.Callback;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.util.FloatingContentCoordinator;
import com.android.systemui.util.FloatingContentCoordinator.FloatingContent;
import com.android.systemui.util.animation.FloatProperties;
import com.android.systemui.util.animation.PhysicsAnimator;
import com.android.systemui.util.animation.PhysicsAnimator.FlingConfig;
import com.android.systemui.util.animation.PhysicsAnimator.SpringConfig;
import com.android.systemui.util.animation.PhysicsAnimator.UpdateListener;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.function.Consumer;

public class PipMotionHelper implements Callback, FloatingContent {
    private final IActivityTaskManager mActivityTaskManager;
    private final Rect mAnimatedBounds = new Rect();
    private PhysicsAnimator<Rect> mAnimatedBoundsPhysicsAnimator = PhysicsAnimator.getInstance(this.mAnimatedBounds);
    private final Rect mAnimatingToBounds = new Rect();
    private final Rect mBounds = new Rect();
    private final SpringConfig mConflictResolutionSpringConfig = new SpringConfig(200.0f, 0.75f);
    private final Context mContext;
    private FlingAnimationUtils mFlingAnimationUtils;
    private FlingConfig mFlingConfigX;
    private FlingConfig mFlingConfigY;
    private final Rect mFloatingAllowedArea = new Rect();
    private FloatingContentCoordinator mFloatingContentCoordinator;
    private PipMenuActivityController mMenuController;
    private final Rect mMovementBounds = new Rect();
    private final PipTaskOrganizer mPipTaskOrganizer;
    private final UpdateListener<Rect> mResizePipUpdateListener = new UpdateListener() {
        public final void onAnimationUpdateForProperty(Object obj, ArrayMap arrayMap) {
            PipMotionHelper.this.lambda$new$0$PipMotionHelper((Rect) obj, arrayMap);
        }
    };
    private PipSnapAlgorithm mSnapAlgorithm;
    private final SpringConfig mSpringConfig = new SpringConfig(1500.0f, 0.75f);
    private final Rect mStableInsets = new Rect();
    private final Consumer<Rect> mUpdateBoundsCallback;

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$PipMotionHelper(Rect rect, ArrayMap arrayMap) {
        resizePipUnchecked(this.mAnimatedBounds);
    }

    public PipMotionHelper(Context context, IActivityTaskManager iActivityTaskManager, PipTaskOrganizer pipTaskOrganizer, PipMenuActivityController pipMenuActivityController, PipSnapAlgorithm pipSnapAlgorithm, FlingAnimationUtils flingAnimationUtils, FloatingContentCoordinator floatingContentCoordinator) {
        Rect rect = this.mBounds;
        Objects.requireNonNull(rect);
        this.mUpdateBoundsCallback = new Consumer(rect) {
            public final /* synthetic */ Rect f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                this.f$0.set((Rect) obj);
            }
        };
        this.mContext = context;
        this.mActivityTaskManager = iActivityTaskManager;
        this.mPipTaskOrganizer = pipTaskOrganizer;
        this.mMenuController = pipMenuActivityController;
        this.mSnapAlgorithm = pipSnapAlgorithm;
        this.mFlingAnimationUtils = flingAnimationUtils;
        this.mFloatingContentCoordinator = floatingContentCoordinator;
        onConfigurationChanged();
    }

    public Rect getFloatingBoundsOnScreen() {
        return !this.mAnimatingToBounds.isEmpty() ? this.mAnimatingToBounds : this.mBounds;
    }

    public Rect getAllowedFloatingBoundsRegion() {
        return this.mFloatingAllowedArea;
    }

    public void moveToBounds(Rect rect) {
        animateToBounds(rect, this.mConflictResolutionSpringConfig);
    }

    /* access modifiers changed from: 0000 */
    public void onConfigurationChanged() {
        this.mSnapAlgorithm.onConfigurationChanged();
        WindowManagerWrapper.getInstance().getStableInsets(this.mStableInsets);
    }

    /* access modifiers changed from: 0000 */
    public void synchronizePinnedStackBounds() {
        cancelAnimations();
        try {
            StackInfo stackInfo = this.mActivityTaskManager.getStackInfo(2, 0);
            if (stackInfo != null) {
                this.mBounds.set(stackInfo.bounds);
            }
        } catch (RemoteException unused) {
            Log.w("PipMotionHelper", "Failed to get pinned stack bounds");
        }
    }

    /* access modifiers changed from: 0000 */
    public void movePip(Rect rect) {
        movePip(rect, false);
    }

    /* access modifiers changed from: 0000 */
    public void movePip(Rect rect, boolean z) {
        if (!z) {
            this.mFloatingContentCoordinator.onContentMoved(this);
        }
        cancelAnimations();
        resizePipUnchecked(rect);
        this.mBounds.set(rect);
    }

    /* access modifiers changed from: 0000 */
    public void expandPip() {
        expandPip(false);
    }

    /* access modifiers changed from: 0000 */
    public void expandPip(boolean z) {
        cancelAnimations();
        this.mMenuController.hideMenuWithoutResize();
        this.mPipTaskOrganizer.getUpdateHandler().post(new Runnable(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PipMotionHelper.this.lambda$expandPip$1$PipMotionHelper(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$expandPip$1 */
    public /* synthetic */ void lambda$expandPip$1$PipMotionHelper(boolean z) {
        try {
            this.mActivityTaskManager.dismissPip(!z, 300);
        } catch (RemoteException e) {
            Log.e("PipMotionHelper", "Error expanding PiP activity", e);
        }
    }

    public void dismissPip() {
        cancelAnimations();
        this.mMenuController.hideMenuWithoutResize();
        this.mPipTaskOrganizer.getUpdateHandler().post(new Runnable() {
            public final void run() {
                PipMotionHelper.this.lambda$dismissPip$2$PipMotionHelper();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$dismissPip$2 */
    public /* synthetic */ void lambda$dismissPip$2$PipMotionHelper() {
        try {
            this.mActivityTaskManager.removeStacksInWindowingModes(new int[]{2});
        } catch (RemoteException e) {
            Log.e("PipMotionHelper", "Failed to remove PiP", e);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setCurrentMovementBounds(Rect rect) {
        this.mMovementBounds.set(rect);
        rebuildFlingConfigs();
        this.mFloatingAllowedArea.set(this.mMovementBounds);
        this.mFloatingAllowedArea.right += this.mBounds.width();
        this.mFloatingAllowedArea.bottom += this.mBounds.height();
    }

    /* access modifiers changed from: 0000 */
    public Rect getBounds() {
        return this.mBounds;
    }

    /* access modifiers changed from: 0000 */
    public boolean shouldDismissPip() {
        Point point = new Point();
        this.mContext.getDisplay().getRealSize(point);
        int i = point.y - this.mStableInsets.bottom;
        Rect rect = this.mBounds;
        int i2 = rect.bottom;
        if (i2 <= i || ((float) (i2 - i)) / ((float) rect.height()) < 0.3f) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void flingToSnapTarget(float f, float f2, Runnable runnable, Runnable runnable2) {
        this.mAnimatedBounds.set(this.mBounds);
        PhysicsAnimator<Rect> physicsAnimator = this.mAnimatedBoundsPhysicsAnimator;
        physicsAnimator.flingThenSpring(FloatProperties.RECT_X, f, this.mFlingConfigX, this.mSpringConfig, true);
        physicsAnimator.flingThenSpring(FloatProperties.RECT_Y, f2, this.mFlingConfigY, this.mSpringConfig);
        physicsAnimator.addUpdateListener(new UpdateListener(runnable) {
            public final /* synthetic */ Runnable f$0;

            {
                this.f$0 = r1;
            }

            public final void onAnimationUpdateForProperty(Object obj, ArrayMap arrayMap) {
                this.f$0.run();
            }
        });
        physicsAnimator.withEndActions(runnable2);
        int i = (int) ((float) (f < 0.0f ? this.mMovementBounds.left : this.mMovementBounds.right));
        int estimateFlingEndValue = (int) PhysicsAnimator.estimateFlingEndValue((float) this.mBounds.top, f2, this.mFlingConfigY);
        setAnimatingToBounds(new Rect(i, estimateFlingEndValue, this.mBounds.width() + i, this.mBounds.height() + estimateFlingEndValue));
        startBoundsAnimation();
    }

    /* access modifiers changed from: 0000 */
    public void animateToClosestSnapTarget() {
        Rect rect = new Rect();
        this.mSnapAlgorithm.snapRectToClosestEdge(this.mBounds, this.mMovementBounds, rect);
        animateToBounds(rect, this.mSpringConfig);
    }

    /* access modifiers changed from: 0000 */
    public void animateToBounds(Rect rect, SpringConfig springConfig) {
        this.mAnimatedBounds.set(this.mBounds);
        PhysicsAnimator<Rect> physicsAnimator = this.mAnimatedBoundsPhysicsAnimator;
        physicsAnimator.spring(FloatProperties.RECT_X, (float) rect.left, springConfig);
        physicsAnimator.spring(FloatProperties.RECT_Y, (float) rect.top, springConfig);
        startBoundsAnimation();
        setAnimatingToBounds(rect);
    }

    /* access modifiers changed from: 0000 */
    public void animateDismiss(float f, float f2, Runnable runnable) {
        Point dismissEndPoint = getDismissEndPoint(this.mBounds, f, f2, PointF.length(f, f2) > this.mFlingAnimationUtils.getMinVelocityPxPerSecond());
        this.mAnimatedBounds.set(this.mBounds);
        PhysicsAnimator<Rect> physicsAnimator = this.mAnimatedBoundsPhysicsAnimator;
        physicsAnimator.spring(FloatProperties.RECT_X, (float) dismissEndPoint.x, f, this.mSpringConfig);
        physicsAnimator.spring(FloatProperties.RECT_Y, (float) dismissEndPoint.y, f2, this.mSpringConfig);
        physicsAnimator.withEndActions(new Runnable() {
            public final void run() {
                PipMotionHelper.this.dismissPip();
            }
        });
        if (runnable != null) {
            this.mAnimatedBoundsPhysicsAnimator.addUpdateListener(new UpdateListener(runnable) {
                public final /* synthetic */ Runnable f$0;

                {
                    this.f$0 = r1;
                }

                public final void onAnimationUpdateForProperty(Object obj, ArrayMap arrayMap) {
                    this.f$0.run();
                }
            });
        }
        startBoundsAnimation();
    }

    /* access modifiers changed from: 0000 */
    public float animateToExpandedState(Rect rect, Rect rect2, Rect rect3) {
        float snapFraction = this.mSnapAlgorithm.getSnapFraction(new Rect(this.mBounds), rect2);
        this.mSnapAlgorithm.applySnapFraction(rect, rect3, snapFraction);
        resizeAndAnimatePipUnchecked(rect, 250);
        return snapFraction;
    }

    /* access modifiers changed from: 0000 */
    public void animateToUnexpandedState(Rect rect, float f, Rect rect2, Rect rect3, boolean z) {
        if (f < 0.0f) {
            f = this.mSnapAlgorithm.getSnapFraction(new Rect(this.mBounds), rect3);
        }
        this.mSnapAlgorithm.applySnapFraction(rect, rect2, f);
        if (z) {
            movePip(rect);
        } else {
            resizeAndAnimatePipUnchecked(rect, 250);
        }
    }

    /* access modifiers changed from: 0000 */
    public void animateToOffset(Rect rect, int i) {
        cancelAnimations();
        this.mPipTaskOrganizer.scheduleOffsetPip(rect, i, 300, this.mUpdateBoundsCallback);
    }

    private void cancelAnimations() {
        this.mAnimatedBoundsPhysicsAnimator.cancel();
        this.mAnimatingToBounds.setEmpty();
    }

    private void rebuildFlingConfigs() {
        Rect rect = this.mMovementBounds;
        this.mFlingConfigX = new FlingConfig(2.0f, (float) rect.left, (float) rect.right);
        Rect rect2 = this.mMovementBounds;
        this.mFlingConfigY = new FlingConfig(2.0f, (float) rect2.top, (float) rect2.bottom);
    }

    private void startBoundsAnimation() {
        cancelAnimations();
        PhysicsAnimator<Rect> physicsAnimator = this.mAnimatedBoundsPhysicsAnimator;
        physicsAnimator.withEndActions(new Runnable() {
            public final void run() {
                PipMotionHelper.this.lambda$startBoundsAnimation$5$PipMotionHelper();
            }
        });
        physicsAnimator.addUpdateListener(this.mResizePipUpdateListener);
        physicsAnimator.start();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startBoundsAnimation$5 */
    public /* synthetic */ void lambda$startBoundsAnimation$5$PipMotionHelper() {
        this.mPipTaskOrganizer.scheduleFinishResizePip(this.mAnimatedBounds);
    }

    private void setAnimatingToBounds(Rect rect) {
        this.mAnimatingToBounds.set(rect);
        this.mFloatingContentCoordinator.onContentMoved(this);
    }

    private void resizePipUnchecked(Rect rect) {
        if (!rect.equals(this.mBounds)) {
            this.mPipTaskOrganizer.scheduleResizePip(rect, this.mUpdateBoundsCallback);
        }
    }

    private void resizeAndAnimatePipUnchecked(Rect rect, int i) {
        if (!rect.equals(this.mBounds)) {
            this.mPipTaskOrganizer.scheduleAnimateResizePip(rect, i, this.mUpdateBoundsCallback);
            setAnimatingToBounds(rect);
        }
    }

    private Point getDismissEndPoint(Rect rect, float f, float f2, boolean z) {
        Point point = new Point();
        this.mContext.getDisplay().getRealSize(point);
        float height = ((float) point.y) + (((float) rect.height()) * 0.1f);
        if (!z || f == 0.0f || f2 == 0.0f) {
            return new Point(rect.left, (int) height);
        }
        float f3 = f2 / f;
        return new Point((int) ((height - (((float) rect.top) - (((float) rect.left) * f3))) / f3), (int) height);
    }

    public void dump(PrintWriter printWriter, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("  ");
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append("PipMotionHelper");
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(sb2);
        sb4.append("mBounds=");
        sb4.append(this.mBounds);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append(sb2);
        sb5.append("mStableInsets=");
        sb5.append(this.mStableInsets);
        printWriter.println(sb5.toString());
    }
}
