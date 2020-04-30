package com.android.systemui.pip.phone;

import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.IPinnedStackController;
import android.view.InputEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.logging.MetricsLoggerWrapper;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.pip.PipBoundsHandler;
import com.android.systemui.pip.PipSnapAlgorithm;
import com.android.systemui.pip.PipTaskOrganizer;
import com.android.systemui.pip.phone.PipAccessibilityInteractionConnection.AccessibilityCallbacks;
import com.android.systemui.pip.phone.PipMenuActivityController.Listener;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.InputConsumerController.InputListener;
import com.android.systemui.shared.system.InputConsumerController.RegistrationListener;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.FloatingContentCoordinator;
import java.io.PrintWriter;
import java.util.Objects;

public class PipTouchHandler {
    private final AccessibilityManager mAccessibilityManager;
    /* access modifiers changed from: private */
    public final IActivityManager mActivityManager;
    private PipAccessibilityInteractionConnection mConnection;
    /* access modifiers changed from: private */
    public final Context mContext;
    private int mDeferResizeToNormalBoundsUntilRotation = -1;
    /* access modifiers changed from: private */
    public final PipDismissViewController mDismissViewController;
    private int mDisplayRotation;
    /* access modifiers changed from: private */
    public final boolean mEnableDismissDragToEdge;
    private final boolean mEnableResize;
    private Rect mExpandedBounds = new Rect();
    @VisibleForTesting
    Rect mExpandedMovementBounds = new Rect();
    private int mExpandedShortestEdgeSize;
    /* access modifiers changed from: private */
    public final FlingAnimationUtils mFlingAnimationUtils;
    private final FloatingContentCoordinator mFloatingContentCoordinator;
    private PipTouchGesture mGesture;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler();
    private int mImeHeight;
    private int mImeOffset;
    private Rect mInsetBounds = new Rect();
    private boolean mIsImeShowing;
    private boolean mIsShelfShowing;
    /* access modifiers changed from: private */
    public final PipMenuActivityController mMenuController;
    /* access modifiers changed from: private */
    public int mMenuState = 0;
    /* access modifiers changed from: private */
    public PipMotionHelper mMotionHelper;
    /* access modifiers changed from: private */
    public Rect mMovementBounds = new Rect();
    private int mMovementBoundsExtraOffsets;
    /* access modifiers changed from: private */
    public boolean mMovementWithinDismiss;
    private Rect mNormalBounds = new Rect();
    @VisibleForTesting
    Rect mNormalMovementBounds = new Rect();
    private IPinnedStackController mPinnedStackController;
    private final PipBoundsHandler mPipBoundsHandler;
    private PipResizeGestureHandler mPipResizeGestureHandler;
    @VisibleForTesting
    Rect mResizedBounds = new Rect();
    /* access modifiers changed from: private */
    public float mSavedSnapFraction = -1.0f;
    private boolean mSendingHoverAccessibilityEvents;
    private int mShelfHeight;
    /* access modifiers changed from: private */
    public Runnable mShowDismissAffordance = new Runnable() {
        public void run() {
            if (PipTouchHandler.this.mEnableDismissDragToEdge) {
                PipTouchHandler.this.mDismissViewController.showDismissTarget();
            }
        }
    };
    private boolean mShowPipMenuOnAnimationEnd = false;
    private final PipSnapAlgorithm mSnapAlgorithm;
    /* access modifiers changed from: private */
    public final Rect mTmpBounds = new Rect();
    /* access modifiers changed from: private */
    public final PipTouchState mTouchState;

    private class DefaultPipTouchGesture extends PipTouchGesture {
        private final PointF mDelta;
        private final Point mStartPosition;

        private DefaultPipTouchGesture() {
            this.mStartPosition = new Point();
            this.mDelta = new PointF();
        }

        public void onDown(PipTouchState pipTouchState) {
            if (pipTouchState.isUserInteracting()) {
                Rect bounds = PipTouchHandler.this.mMotionHelper.getBounds();
                this.mDelta.set(0.0f, 0.0f);
                this.mStartPosition.set(bounds.left, bounds.top);
                PipTouchHandler.this.mMovementWithinDismiss = pipTouchState.getDownTouchPosition().y >= ((float) PipTouchHandler.this.mMovementBounds.bottom);
                if (PipTouchHandler.this.mMenuState != 0) {
                    PipTouchHandler.this.mMenuController.pokeMenu();
                }
                if (PipTouchHandler.this.mEnableDismissDragToEdge) {
                    PipTouchHandler.this.mDismissViewController.createDismissTarget();
                    PipTouchHandler.this.mHandler.postDelayed(PipTouchHandler.this.mShowDismissAffordance, 225);
                }
            }
        }

        public boolean onMove(PipTouchState pipTouchState) {
            boolean z = false;
            if (!pipTouchState.isUserInteracting()) {
                return false;
            }
            if (pipTouchState.startedDragging()) {
                PipTouchHandler.this.mSavedSnapFraction = -1.0f;
                if (PipTouchHandler.this.mEnableDismissDragToEdge) {
                    PipTouchHandler.this.mHandler.removeCallbacks(PipTouchHandler.this.mShowDismissAffordance);
                    PipTouchHandler.this.mDismissViewController.showDismissTarget();
                }
            }
            if (!pipTouchState.isDragging()) {
                return false;
            }
            PointF lastTouchDelta = pipTouchState.getLastTouchDelta();
            Point point = this.mStartPosition;
            float f = (float) point.x;
            PointF pointF = this.mDelta;
            float f2 = pointF.x;
            float f3 = f + f2;
            float f4 = (float) point.y;
            float f5 = pointF.y;
            float f6 = f4 + f5;
            float f7 = lastTouchDelta.x + f3;
            float f8 = lastTouchDelta.y + f6;
            pointF.x = f2 + (f7 - f3);
            pointF.y = f5 + (f8 - f6);
            PipTouchHandler.this.mTmpBounds.set(PipTouchHandler.this.mMotionHelper.getBounds());
            PipTouchHandler.this.mTmpBounds.offsetTo((int) f7, (int) f8);
            PipTouchHandler.this.mMotionHelper.movePip(PipTouchHandler.this.mTmpBounds, true);
            if (PipTouchHandler.this.mEnableDismissDragToEdge) {
                PipTouchHandler.this.updateDismissFraction();
            }
            PointF lastTouchPosition = pipTouchState.getLastTouchPosition();
            if (PipTouchHandler.this.mMovementWithinDismiss) {
                PipTouchHandler pipTouchHandler = PipTouchHandler.this;
                if (lastTouchPosition.y >= ((float) pipTouchHandler.mMovementBounds.bottom)) {
                    z = true;
                }
                pipTouchHandler.mMovementWithinDismiss = z;
            }
            return true;
        }

        public boolean onUp(PipTouchState pipTouchState) {
            if (PipTouchHandler.this.mEnableDismissDragToEdge) {
                PipTouchHandler.this.cleanUpDismissTarget();
            }
            if (!pipTouchState.isUserInteracting()) {
                return false;
            }
            PointF velocity = pipTouchState.getVelocity();
            boolean z = Math.abs(velocity.x) > Math.abs(velocity.y);
            boolean z2 = PointF.length(velocity.x, velocity.y) > PipTouchHandler.this.mFlingAnimationUtils.getMinVelocityPxPerSecond();
            boolean z3 = z2 && velocity.y > 0.0f && !z && PipTouchHandler.this.mMovementWithinDismiss;
            if (!PipTouchHandler.this.mEnableDismissDragToEdge || (!PipTouchHandler.this.mMotionHelper.shouldDismissPip() && !z3)) {
                if (pipTouchState.isDragging()) {
                    Runnable runnable = null;
                    if (PipTouchHandler.this.mMenuState != 0) {
                        PipTouchHandler.this.mMenuController.showMenu(PipTouchHandler.this.mMenuState, PipTouchHandler.this.mMotionHelper.getBounds(), PipTouchHandler.this.mMovementBounds, true, PipTouchHandler.this.willResizeMenu());
                    } else {
                        PipMenuActivityController access$800 = PipTouchHandler.this.mMenuController;
                        Objects.requireNonNull(access$800);
                        runnable = new Runnable() {
                            public final void run() {
                                PipMenuActivityController.this.hideMenu();
                            }
                        };
                    }
                    if (z2) {
                        PipTouchHandler.this.mMotionHelper.flingToSnapTarget(velocity.x, velocity.y, new Runnable() {
                            public final void run() {
                                PipTouchHandler.this.updateDismissFraction();
                            }
                        }, runnable);
                    } else {
                        PipTouchHandler.this.mMotionHelper.animateToClosestSnapTarget();
                    }
                } else if (PipTouchHandler.this.mTouchState.isDoubleTap()) {
                    PipTouchHandler.this.setTouchEnabled(false);
                    PipTouchHandler.this.mMotionHelper.expandPip();
                } else if (PipTouchHandler.this.mMenuState != 2) {
                    if (!PipTouchHandler.this.mTouchState.isWaitingForDoubleTap()) {
                        PipTouchHandler.this.mMenuController.showMenu(2, PipTouchHandler.this.mMotionHelper.getBounds(), PipTouchHandler.this.mMovementBounds, true, PipTouchHandler.this.willResizeMenu());
                    } else {
                        PipTouchHandler.this.mTouchState.scheduleDoubleTapTimeoutCallback();
                    }
                }
                return true;
            }
            MetricsLoggerWrapper.logPictureInPictureDismissByDrag(PipTouchHandler.this.mContext, PipUtils.getTopPipActivity(PipTouchHandler.this.mContext, PipTouchHandler.this.mActivityManager));
            PipTouchHandler.this.mMotionHelper.animateDismiss(velocity.x, velocity.y, new Runnable() {
                public final void run() {
                    PipTouchHandler.this.updateDismissFraction();
                }
            });
            return true;
        }
    }

    private class PipMenuListener implements Listener {
        private PipMenuListener() {
        }

        public void onPipMenuStateChanged(int i, boolean z) {
            PipTouchHandler.this.setMenuState(i, z);
        }

        public void onPipExpand() {
            PipTouchHandler.this.mMotionHelper.expandPip();
        }

        public void onPipDismiss() {
            Pair topPipActivity = PipUtils.getTopPipActivity(PipTouchHandler.this.mContext, PipTouchHandler.this.mActivityManager);
            if (topPipActivity.first != null) {
                MetricsLoggerWrapper.logPictureInPictureDismissByTap(PipTouchHandler.this.mContext, topPipActivity);
            }
            PipTouchHandler.this.mMotionHelper.dismissPip();
        }

        public void onPipShowMenu() {
            PipTouchHandler.this.mMenuController.showMenu(2, PipTouchHandler.this.mMotionHelper.getBounds(), PipTouchHandler.this.mMovementBounds, true, PipTouchHandler.this.willResizeMenu());
        }
    }

    public PipTouchHandler(Context context, IActivityManager iActivityManager, IActivityTaskManager iActivityTaskManager, PipMenuActivityController pipMenuActivityController, InputConsumerController inputConsumerController, PipBoundsHandler pipBoundsHandler, PipTaskOrganizer pipTaskOrganizer, FloatingContentCoordinator floatingContentCoordinator, DeviceConfigProxy deviceConfigProxy, PipSnapAlgorithm pipSnapAlgorithm) {
        Context context2 = context;
        PipMenuActivityController pipMenuActivityController2 = pipMenuActivityController;
        InputConsumerController inputConsumerController2 = inputConsumerController;
        this.mContext = context2;
        this.mActivityManager = iActivityManager;
        this.mAccessibilityManager = (AccessibilityManager) context2.getSystemService(AccessibilityManager.class);
        this.mMenuController = pipMenuActivityController2;
        pipMenuActivityController2.addListener(new PipMenuListener());
        this.mDismissViewController = new PipDismissViewController(context2);
        this.mSnapAlgorithm = pipSnapAlgorithm;
        this.mFlingAnimationUtils = new FlingAnimationUtils(context.getResources().getDisplayMetrics(), 2.5f);
        this.mGesture = new DefaultPipTouchGesture();
        PipMotionHelper pipMotionHelper = new PipMotionHelper(this.mContext, iActivityTaskManager, pipTaskOrganizer, this.mMenuController, this.mSnapAlgorithm, this.mFlingAnimationUtils, floatingContentCoordinator);
        this.mMotionHelper = pipMotionHelper;
        PipResizeGestureHandler pipResizeGestureHandler = new PipResizeGestureHandler(context, pipBoundsHandler, this, pipMotionHelper, deviceConfigProxy, pipTaskOrganizer);
        this.mPipResizeGestureHandler = pipResizeGestureHandler;
        this.mTouchState = new PipTouchState(ViewConfiguration.get(context), this.mHandler, new Runnable() {
            public final void run() {
                PipTouchHandler.this.lambda$new$0$PipTouchHandler();
            }
        });
        Resources resources = context.getResources();
        this.mExpandedShortestEdgeSize = resources.getDimensionPixelSize(C2009R$dimen.pip_expanded_shortest_edge_size);
        this.mImeOffset = resources.getDimensionPixelSize(C2009R$dimen.pip_ime_offset);
        this.mEnableDismissDragToEdge = resources.getBoolean(C2007R$bool.config_pipEnableDismissDragToEdge);
        this.mEnableResize = resources.getBoolean(C2007R$bool.config_pipEnableResizeForMenu);
        inputConsumerController2.setInputListener(new InputListener() {
            public final boolean onInputEvent(InputEvent inputEvent) {
                return PipTouchHandler.this.handleTouchEvent(inputEvent);
            }
        });
        inputConsumerController2.setRegistrationListener(new RegistrationListener() {
            public final void onRegistrationChanged(boolean z) {
                PipTouchHandler.this.onRegistrationChanged(z);
            }
        });
        this.mPipBoundsHandler = pipBoundsHandler;
        this.mFloatingContentCoordinator = floatingContentCoordinator;
        this.mConnection = new PipAccessibilityInteractionConnection(this.mMotionHelper, new AccessibilityCallbacks() {
            public final void onAccessibilityShowMenu() {
                PipTouchHandler.this.onAccessibilityShowMenu();
            }
        }, this.mHandler);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$PipTouchHandler() {
        this.mMenuController.showMenu(2, this.mMotionHelper.getBounds(), this.mMovementBounds, true, willResizeMenu());
    }

    public void setTouchEnabled(boolean z) {
        this.mTouchState.setAllowTouches(z);
    }

    public void showPictureInPictureMenu() {
        if (!this.mTouchState.isUserInteracting()) {
            this.mMenuController.showMenu(2, this.mMotionHelper.getBounds(), this.mMovementBounds, false, willResizeMenu());
        }
    }

    public void onActivityPinned() {
        cleanUpDismissTarget();
        this.mShowPipMenuOnAnimationEnd = true;
        this.mPipResizeGestureHandler.onActivityPinned();
        this.mFloatingContentCoordinator.onContentAdded(this.mMotionHelper);
    }

    public void onActivityUnpinned(ComponentName componentName) {
        if (componentName == null) {
            cleanUpDismissTarget();
            this.mFloatingContentCoordinator.onContentRemoved(this.mMotionHelper);
        }
        this.mResizedBounds.setEmpty();
        this.mPipResizeGestureHandler.onActivityUnpinned();
    }

    public void onPinnedStackAnimationEnded() {
        this.mMotionHelper.synchronizePinnedStackBounds();
        updateMovementBounds();
        this.mResizedBounds.set(this.mMotionHelper.getBounds());
        if (this.mShowPipMenuOnAnimationEnd) {
            this.mMenuController.showMenu(1, this.mMotionHelper.getBounds(), this.mMovementBounds, true, false);
            this.mShowPipMenuOnAnimationEnd = false;
        }
    }

    public void onConfigurationChanged() {
        this.mMotionHelper.onConfigurationChanged();
        this.mMotionHelper.synchronizePinnedStackBounds();
    }

    public void onImeVisibilityChanged(boolean z, int i) {
        this.mIsImeShowing = z;
        this.mImeHeight = i;
    }

    public void onShelfVisibilityChanged(boolean z, int i) {
        this.mIsShelfShowing = z;
        this.mShelfHeight = i;
    }

    public void onMovementBoundsChanged(Rect rect, Rect rect2, Rect rect3, boolean z, boolean z2, int i) {
        Rect rect4;
        int i2 = 0;
        int i3 = this.mIsImeShowing ? this.mImeHeight : 0;
        boolean z3 = this.mDisplayRotation != i;
        if (z3) {
            this.mTouchState.reset();
        }
        this.mNormalBounds = rect2;
        Rect rect5 = new Rect();
        this.mSnapAlgorithm.getMovementBounds(this.mNormalBounds, rect, rect5, i3);
        float width = ((float) rect2.width()) / ((float) rect2.height());
        Point point = new Point();
        this.mContext.getDisplay().getRealSize(point);
        Size sizeForAspectRatio = this.mSnapAlgorithm.getSizeForAspectRatio(width, (float) this.mExpandedShortestEdgeSize, point.x, point.y);
        this.mExpandedBounds.set(0, 0, sizeForAspectRatio.getWidth(), sizeForAspectRatio.getHeight());
        Rect rect6 = new Rect();
        this.mSnapAlgorithm.getMovementBounds(this.mExpandedBounds, rect, rect6, i3);
        this.mPipResizeGestureHandler.updateMinSize(this.mNormalBounds.width(), this.mNormalBounds.height());
        this.mPipResizeGestureHandler.updateMaxSize(this.mExpandedBounds.width(), this.mExpandedBounds.height());
        int i4 = this.mIsImeShowing ? this.mImeOffset : 0;
        if (!this.mIsImeShowing && this.mIsShelfShowing) {
            i2 = this.mShelfHeight;
        }
        int max = Math.max(i4, i2);
        if ((z || z2 || z3) && !this.mTouchState.isUserInteracting()) {
            float f = this.mContext.getResources().getDisplayMetrics().density * 1.0f;
            if (this.mMenuState != 2 || !willResizeMenu()) {
                rect4 = new Rect(rect5);
            } else {
                rect4 = new Rect(rect6);
            }
            int i5 = this.mMovementBounds.bottom - this.mMovementBoundsExtraOffsets;
            int i6 = rect4.bottom;
            if (i6 >= rect4.top) {
                i6 -= max;
            }
            float min = ((float) Math.min(i5, i6)) - f;
            int i7 = rect3.top;
            if (min <= ((float) i7) && ((float) i7) <= ((float) Math.max(i5, i6)) + f) {
                this.mMotionHelper.animateToOffset(rect3, i6 - rect3.top);
            }
        }
        this.mNormalMovementBounds = rect5;
        this.mExpandedMovementBounds = rect6;
        this.mDisplayRotation = i;
        this.mInsetBounds.set(rect);
        updateMovementBounds();
        this.mMovementBoundsExtraOffsets = max;
        if (this.mDeferResizeToNormalBoundsUntilRotation == i) {
            this.mMotionHelper.animateToUnexpandedState(rect2, this.mSavedSnapFraction, this.mNormalMovementBounds, this.mMovementBounds, true);
            this.mSavedSnapFraction = -1.0f;
            this.mDeferResizeToNormalBoundsUntilRotation = -1;
        }
    }

    /* access modifiers changed from: private */
    public void onRegistrationChanged(boolean z) {
        this.mAccessibilityManager.setPictureInPictureActionReplacingConnection(z ? this.mConnection : null);
        if (!z && this.mTouchState.isUserInteracting()) {
            cleanUpDismissTarget();
        }
    }

    /* access modifiers changed from: private */
    public void onAccessibilityShowMenu() {
        this.mMenuController.showMenu(2, this.mMotionHelper.getBounds(), this.mMovementBounds, true, willResizeMenu());
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0084, code lost:
        if (r11.mGesture.onUp(r11.mTouchState) != false) goto L_0x00ab;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean handleTouchEvent(android.view.InputEvent r12) {
        /*
            r11 = this;
            boolean r0 = r12 instanceof android.view.MotionEvent
            r1 = 1
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            android.view.IPinnedStackController r0 = r11.mPinnedStackController
            if (r0 != 0) goto L_0x000b
            return r1
        L_0x000b:
            android.view.MotionEvent r12 = (android.view.MotionEvent) r12
            com.android.systemui.pip.phone.PipTouchState r0 = r11.mTouchState
            r0.onTouchEvent(r12)
            int r0 = r11.mMenuState
            r2 = 0
            if (r0 == 0) goto L_0x0019
            r0 = r1
            goto L_0x001a
        L_0x0019:
            r0 = r2
        L_0x001a:
            int r3 = r12.getAction()
            r4 = 3
            if (r3 == 0) goto L_0x009f
            if (r3 == r1) goto L_0x0079
            r5 = 2
            if (r3 == r5) goto L_0x0066
            if (r3 == r4) goto L_0x0087
            r5 = 7
            if (r3 == r5) goto L_0x0058
            r5 = 9
            if (r3 == r5) goto L_0x0048
            r5 = 10
            if (r3 == r5) goto L_0x0035
            goto L_0x00ab
        L_0x0035:
            com.android.systemui.pip.phone.PipMenuActivityController r3 = r11.mMenuController
            r3.hideMenu()
            if (r0 != 0) goto L_0x00ab
            boolean r3 = r11.mSendingHoverAccessibilityEvents
            if (r3 == 0) goto L_0x00ab
            r3 = 256(0x100, float:3.59E-43)
            r11.sendAccessibilityHoverEvent(r3)
            r11.mSendingHoverAccessibilityEvents = r2
            goto L_0x00ab
        L_0x0048:
            com.android.systemui.pip.phone.PipMenuActivityController r5 = r11.mMenuController
            r6 = 2
            com.android.systemui.pip.phone.PipMotionHelper r2 = r11.mMotionHelper
            android.graphics.Rect r7 = r2.getBounds()
            android.graphics.Rect r8 = r11.mMovementBounds
            r9 = 0
            r10 = 0
            r5.showMenu(r6, r7, r8, r9, r10)
        L_0x0058:
            if (r0 != 0) goto L_0x00ab
            boolean r2 = r11.mSendingHoverAccessibilityEvents
            if (r2 != 0) goto L_0x00ab
            r2 = 128(0x80, float:1.794E-43)
            r11.sendAccessibilityHoverEvent(r2)
            r11.mSendingHoverAccessibilityEvents = r1
            goto L_0x00ab
        L_0x0066:
            com.android.systemui.pip.phone.PipTouchGesture r2 = r11.mGesture
            com.android.systemui.pip.phone.PipTouchState r3 = r11.mTouchState
            boolean r2 = r2.onMove(r3)
            if (r2 == 0) goto L_0x0071
            goto L_0x00ab
        L_0x0071:
            com.android.systemui.pip.phone.PipTouchState r0 = r11.mTouchState
            boolean r0 = r0.isDragging()
            r0 = r0 ^ r1
            goto L_0x00ab
        L_0x0079:
            r11.updateMovementBounds()
            com.android.systemui.pip.phone.PipTouchGesture r3 = r11.mGesture
            com.android.systemui.pip.phone.PipTouchState r5 = r11.mTouchState
            boolean r3 = r3.onUp(r5)
            if (r3 == 0) goto L_0x0087
            goto L_0x00ab
        L_0x0087:
            com.android.systemui.pip.phone.PipTouchState r0 = r11.mTouchState
            boolean r0 = r0.startedDragging()
            if (r0 != 0) goto L_0x0098
            com.android.systemui.pip.phone.PipTouchState r0 = r11.mTouchState
            boolean r0 = r0.isDragging()
            if (r0 != 0) goto L_0x0098
            r2 = r1
        L_0x0098:
            com.android.systemui.pip.phone.PipTouchState r0 = r11.mTouchState
            r0.reset()
            r0 = r2
            goto L_0x00ab
        L_0x009f:
            com.android.systemui.pip.phone.PipMotionHelper r2 = r11.mMotionHelper
            r2.synchronizePinnedStackBounds()
            com.android.systemui.pip.phone.PipTouchGesture r2 = r11.mGesture
            com.android.systemui.pip.phone.PipTouchState r3 = r11.mTouchState
            r2.onDown(r3)
        L_0x00ab:
            if (r0 == 0) goto L_0x00c6
            android.view.MotionEvent r12 = android.view.MotionEvent.obtain(r12)
            com.android.systemui.pip.phone.PipTouchState r0 = r11.mTouchState
            boolean r0 = r0.startedDragging()
            if (r0 == 0) goto L_0x00c1
            r12.setAction(r4)
            com.android.systemui.pip.phone.PipMenuActivityController r0 = r11.mMenuController
            r0.pokeMenu()
        L_0x00c1:
            com.android.systemui.pip.phone.PipMenuActivityController r11 = r11.mMenuController
            r11.handlePointerEvent(r12)
        L_0x00c6:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.pip.phone.PipTouchHandler.handleTouchEvent(android.view.InputEvent):boolean");
    }

    private void sendAccessibilityHoverEvent(int i) {
        if (this.mAccessibilityManager.isEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain(i);
            obtain.setImportantForAccessibility(true);
            obtain.setSourceNodeId(AccessibilityNodeInfo.ROOT_NODE_ID);
            obtain.setWindowId(-3);
            this.mAccessibilityManager.sendAccessibilityEvent(obtain);
        }
    }

    /* access modifiers changed from: private */
    public void updateDismissFraction() {
        if (this.mMenuController != null && !this.mIsImeShowing) {
            Rect bounds = this.mMotionHelper.getBounds();
            float f = (float) this.mInsetBounds.bottom;
            int i = bounds.bottom;
            float min = ((float) i) > f ? Math.min((((float) i) - f) / ((float) bounds.height()), 1.0f) : 0.0f;
            if (Float.compare(min, 0.0f) != 0 || this.mMenuController.isMenuActivityVisible()) {
                this.mMenuController.setDismissFraction(min);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setPinnedStackController(IPinnedStackController iPinnedStackController) {
        this.mPinnedStackController = iPinnedStackController;
    }

    /* access modifiers changed from: private */
    public void setMenuState(int i, boolean z) {
        if (this.mMenuState != i || z) {
            boolean z2 = false;
            if (i != 2 || this.mMenuState == 2) {
                if (i == 0 && this.mMenuState == 2) {
                    if (z) {
                        if (this.mDeferResizeToNormalBoundsUntilRotation == -1) {
                            try {
                                int displayRotation = this.mPinnedStackController.getDisplayRotation();
                                if (this.mDisplayRotation != displayRotation) {
                                    this.mDeferResizeToNormalBoundsUntilRotation = displayRotation;
                                }
                            } catch (RemoteException unused) {
                                Log.e("PipTouchHandler", "Could not get display rotation from controller");
                            }
                        }
                        if (this.mDeferResizeToNormalBoundsUntilRotation == -1) {
                            Rect rect = new Rect(this.mResizedBounds);
                            Rect rect2 = new Rect();
                            this.mSnapAlgorithm.getMovementBounds(rect, this.mInsetBounds, rect2, this.mIsImeShowing ? this.mImeHeight : 0);
                            this.mMotionHelper.animateToUnexpandedState(rect, this.mSavedSnapFraction, rect2, this.mMovementBounds, false);
                            this.mSavedSnapFraction = -1.0f;
                        }
                    } else {
                        this.mSavedSnapFraction = -1.0f;
                    }
                }
            } else if (z) {
                this.mResizedBounds.set(this.mMotionHelper.getBounds());
                this.mSavedSnapFraction = this.mMotionHelper.animateToExpandedState(new Rect(this.mExpandedBounds), this.mMovementBounds, this.mExpandedMovementBounds);
            }
            this.mMenuState = i;
            updateMovementBounds();
            onRegistrationChanged(i == 0);
            if (i != 1) {
                Context context = this.mContext;
                if (i == 2) {
                    z2 = true;
                }
                MetricsLoggerWrapper.logPictureInPictureMenuVisible(context, z2);
            }
        }
    }

    public PipMotionHelper getMotionHelper() {
        return this.mMotionHelper;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public PipResizeGestureHandler getPipResizeGestureHandler() {
        return this.mPipResizeGestureHandler;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setPipResizeGestureHandler(PipResizeGestureHandler pipResizeGestureHandler) {
        this.mPipResizeGestureHandler = pipResizeGestureHandler;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setPipMotionHelper(PipMotionHelper pipMotionHelper) {
        this.mMotionHelper = pipMotionHelper;
    }

    public Rect getNormalBounds() {
        return this.mNormalBounds;
    }

    private void updateMovementBounds() {
        int i = 0;
        this.mSnapAlgorithm.getMovementBounds(this.mMotionHelper.getBounds(), this.mInsetBounds, this.mMovementBounds, this.mIsImeShowing ? this.mImeHeight : 0);
        this.mMotionHelper.setCurrentMovementBounds(this.mMovementBounds);
        boolean z = this.mMenuState == 2;
        PipBoundsHandler pipBoundsHandler = this.mPipBoundsHandler;
        if (z && willResizeMenu()) {
            i = this.mExpandedShortestEdgeSize;
        }
        pipBoundsHandler.setMinEdgeSize(i);
    }

    /* access modifiers changed from: private */
    public void cleanUpDismissTarget() {
        this.mHandler.removeCallbacks(this.mShowDismissAffordance);
        this.mDismissViewController.destroyDismissTarget();
    }

    /* access modifiers changed from: private */
    public boolean willResizeMenu() {
        boolean z = false;
        if (!this.mEnableResize) {
            return false;
        }
        if (!(this.mExpandedBounds.width() == this.mNormalBounds.width() && this.mExpandedBounds.height() == this.mNormalBounds.height())) {
            z = true;
        }
        return z;
    }

    public void dump(PrintWriter printWriter, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("  ");
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append("PipTouchHandler");
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(sb2);
        sb4.append("mMovementBounds=");
        sb4.append(this.mMovementBounds);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append(sb2);
        sb5.append("mNormalBounds=");
        sb5.append(this.mNormalBounds);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append(sb2);
        sb6.append("mNormalMovementBounds=");
        sb6.append(this.mNormalMovementBounds);
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append(sb2);
        sb7.append("mExpandedBounds=");
        sb7.append(this.mExpandedBounds);
        printWriter.println(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append(sb2);
        sb8.append("mExpandedMovementBounds=");
        sb8.append(this.mExpandedMovementBounds);
        printWriter.println(sb8.toString());
        StringBuilder sb9 = new StringBuilder();
        sb9.append(sb2);
        sb9.append("mMenuState=");
        sb9.append(this.mMenuState);
        printWriter.println(sb9.toString());
        StringBuilder sb10 = new StringBuilder();
        sb10.append(sb2);
        sb10.append("mIsImeShowing=");
        sb10.append(this.mIsImeShowing);
        printWriter.println(sb10.toString());
        StringBuilder sb11 = new StringBuilder();
        sb11.append(sb2);
        sb11.append("mImeHeight=");
        sb11.append(this.mImeHeight);
        printWriter.println(sb11.toString());
        StringBuilder sb12 = new StringBuilder();
        sb12.append(sb2);
        sb12.append("mIsShelfShowing=");
        sb12.append(this.mIsShelfShowing);
        printWriter.println(sb12.toString());
        StringBuilder sb13 = new StringBuilder();
        sb13.append(sb2);
        sb13.append("mShelfHeight=");
        sb13.append(this.mShelfHeight);
        printWriter.println(sb13.toString());
        StringBuilder sb14 = new StringBuilder();
        sb14.append(sb2);
        sb14.append("mSavedSnapFraction=");
        sb14.append(this.mSavedSnapFraction);
        printWriter.println(sb14.toString());
        StringBuilder sb15 = new StringBuilder();
        sb15.append(sb2);
        sb15.append("mEnableDragToEdgeDismiss=");
        sb15.append(this.mEnableDismissDragToEdge);
        printWriter.println(sb15.toString());
        this.mSnapAlgorithm.dump(printWriter, sb2);
        this.mTouchState.dump(printWriter, sb2);
        this.mMotionHelper.dump(printWriter, sb2);
    }
}
