package com.android.systemui.pip.phone;

import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;

public class PipTouchState {
    @VisibleForTesting
    static final long DOUBLE_TAP_TIMEOUT = 200;
    private int mActivePointerId;
    private boolean mAllowDraggingOffscreen = false;
    private boolean mAllowTouches = true;
    private final Runnable mDoubleTapTimeoutCallback;
    private final PointF mDownDelta = new PointF();
    private final PointF mDownTouch = new PointF();
    private long mDownTouchTime = 0;
    private final Handler mHandler;
    private boolean mIsDoubleTap = false;
    private boolean mIsDragging = false;
    private boolean mIsUserInteracting = false;
    private boolean mIsWaitingForDoubleTap = false;
    private final PointF mLastDelta = new PointF();
    private long mLastDownTouchTime = 0;
    private final PointF mLastTouch = new PointF();
    private boolean mPreviouslyDragging = false;
    private boolean mStartedDragging = false;
    private long mUpTouchTime = 0;
    private final PointF mVelocity = new PointF();
    private VelocityTracker mVelocityTracker;
    private final ViewConfiguration mViewConfig;

    public PipTouchState(ViewConfiguration viewConfiguration, Handler handler, Runnable runnable) {
        this.mViewConfig = viewConfiguration;
        this.mHandler = handler;
        this.mDoubleTapTimeoutCallback = runnable;
    }

    public void reset() {
        this.mAllowDraggingOffscreen = false;
        this.mIsDragging = false;
        this.mStartedDragging = false;
        this.mIsUserInteracting = false;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        boolean z = false;
        boolean z2 = true;
        if (actionMasked != 0) {
            String str = "PipTouchHandler";
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        if (actionMasked == 6 && this.mIsUserInteracting) {
                            addMovement(motionEvent);
                            int actionIndex = motionEvent.getActionIndex();
                            if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
                                if (actionIndex == 0) {
                                    z = true;
                                }
                                this.mActivePointerId = motionEvent.getPointerId(z ? 1 : 0);
                                this.mLastTouch.set(motionEvent.getRawX(z), motionEvent.getRawY(z));
                            }
                        }
                    }
                } else if (this.mIsUserInteracting) {
                    addMovement(motionEvent);
                    int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (findPointerIndex == -1) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Invalid active pointer id on MOVE: ");
                        sb.append(this.mActivePointerId);
                        Log.e(str, sb.toString());
                    } else {
                        float rawX = motionEvent.getRawX(findPointerIndex);
                        float rawY = motionEvent.getRawY(findPointerIndex);
                        PointF pointF = this.mLastDelta;
                        PointF pointF2 = this.mLastTouch;
                        pointF.set(rawX - pointF2.x, rawY - pointF2.y);
                        PointF pointF3 = this.mDownDelta;
                        PointF pointF4 = this.mDownTouch;
                        pointF3.set(rawX - pointF4.x, rawY - pointF4.y);
                        boolean z3 = this.mDownDelta.length() > ((float) this.mViewConfig.getScaledTouchSlop());
                        if (this.mIsDragging) {
                            this.mStartedDragging = false;
                        } else if (z3) {
                            this.mIsDragging = true;
                            this.mStartedDragging = true;
                        }
                        this.mLastTouch.set(rawX, rawY);
                    }
                }
            } else if (this.mIsUserInteracting) {
                addMovement(motionEvent);
                this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mViewConfig.getScaledMaximumFlingVelocity());
                this.mVelocity.set(this.mVelocityTracker.getXVelocity(), this.mVelocityTracker.getYVelocity());
                int findPointerIndex2 = motionEvent.findPointerIndex(this.mActivePointerId);
                if (findPointerIndex2 == -1) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Invalid active pointer id on UP: ");
                    sb2.append(this.mActivePointerId);
                    Log.e(str, sb2.toString());
                } else {
                    this.mUpTouchTime = motionEvent.getEventTime();
                    this.mLastTouch.set(motionEvent.getRawX(findPointerIndex2), motionEvent.getRawY(findPointerIndex2));
                    boolean z4 = this.mIsDragging;
                    this.mPreviouslyDragging = z4;
                    if (!this.mIsDoubleTap && !z4 && this.mUpTouchTime - this.mDownTouchTime < DOUBLE_TAP_TIMEOUT) {
                        z = true;
                    }
                    this.mIsWaitingForDoubleTap = z;
                }
            }
            recycleVelocityTracker();
        } else if (this.mAllowTouches) {
            initOrResetVelocityTracker();
            addMovement(motionEvent);
            this.mActivePointerId = motionEvent.getPointerId(0);
            this.mLastTouch.set(motionEvent.getRawX(), motionEvent.getRawY());
            this.mDownTouch.set(this.mLastTouch);
            this.mAllowDraggingOffscreen = true;
            this.mIsUserInteracting = true;
            long eventTime = motionEvent.getEventTime();
            this.mDownTouchTime = eventTime;
            if (this.mPreviouslyDragging || eventTime - this.mLastDownTouchTime >= DOUBLE_TAP_TIMEOUT) {
                z2 = false;
            }
            this.mIsDoubleTap = z2;
            this.mIsWaitingForDoubleTap = false;
            this.mIsDragging = false;
            this.mLastDownTouchTime = this.mDownTouchTime;
            Runnable runnable = this.mDoubleTapTimeoutCallback;
            if (runnable != null) {
                this.mHandler.removeCallbacks(runnable);
            }
        }
    }

    public PointF getVelocity() {
        return this.mVelocity;
    }

    public PointF getLastTouchPosition() {
        return this.mLastTouch;
    }

    public PointF getLastTouchDelta() {
        return this.mLastDelta;
    }

    public PointF getDownTouchPosition() {
        return this.mDownTouch;
    }

    public boolean isDragging() {
        return this.mIsDragging;
    }

    public boolean isUserInteracting() {
        return this.mIsUserInteracting;
    }

    public boolean startedDragging() {
        return this.mStartedDragging;
    }

    public void setAllowTouches(boolean z) {
        this.mAllowTouches = z;
        if (this.mIsUserInteracting) {
            reset();
        }
    }

    public boolean isDoubleTap() {
        return this.mIsDoubleTap;
    }

    public boolean isWaitingForDoubleTap() {
        return this.mIsWaitingForDoubleTap;
    }

    public void scheduleDoubleTapTimeoutCallback() {
        if (this.mIsWaitingForDoubleTap) {
            long doubleTapTimeoutCallbackDelay = getDoubleTapTimeoutCallbackDelay();
            this.mHandler.removeCallbacks(this.mDoubleTapTimeoutCallback);
            this.mHandler.postDelayed(this.mDoubleTapTimeoutCallback, doubleTapTimeoutCallbackDelay);
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public long getDoubleTapTimeoutCallbackDelay() {
        if (this.mIsWaitingForDoubleTap) {
            return Math.max(0, DOUBLE_TAP_TIMEOUT - (this.mUpTouchTime - this.mDownTouchTime));
        }
        return -1;
    }

    private void initOrResetVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
    }

    private void recycleVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void addMovement(MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX() - motionEvent.getX();
        float rawY = motionEvent.getRawY() - motionEvent.getY();
        motionEvent.offsetLocation(rawX, rawY);
        this.mVelocityTracker.addMovement(motionEvent);
        motionEvent.offsetLocation(-rawX, -rawY);
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
        sb4.append("mAllowTouches=");
        sb4.append(this.mAllowTouches);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append(sb2);
        sb5.append("mActivePointerId=");
        sb5.append(this.mActivePointerId);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append(sb2);
        sb6.append("mDownTouch=");
        sb6.append(this.mDownTouch);
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append(sb2);
        sb7.append("mDownDelta=");
        sb7.append(this.mDownDelta);
        printWriter.println(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append(sb2);
        sb8.append("mLastTouch=");
        sb8.append(this.mLastTouch);
        printWriter.println(sb8.toString());
        StringBuilder sb9 = new StringBuilder();
        sb9.append(sb2);
        sb9.append("mLastDelta=");
        sb9.append(this.mLastDelta);
        printWriter.println(sb9.toString());
        StringBuilder sb10 = new StringBuilder();
        sb10.append(sb2);
        sb10.append("mVelocity=");
        sb10.append(this.mVelocity);
        printWriter.println(sb10.toString());
        StringBuilder sb11 = new StringBuilder();
        sb11.append(sb2);
        sb11.append("mIsUserInteracting=");
        sb11.append(this.mIsUserInteracting);
        printWriter.println(sb11.toString());
        StringBuilder sb12 = new StringBuilder();
        sb12.append(sb2);
        sb12.append("mIsDragging=");
        sb12.append(this.mIsDragging);
        printWriter.println(sb12.toString());
        StringBuilder sb13 = new StringBuilder();
        sb13.append(sb2);
        sb13.append("mStartedDragging=");
        sb13.append(this.mStartedDragging);
        printWriter.println(sb13.toString());
        StringBuilder sb14 = new StringBuilder();
        sb14.append(sb2);
        sb14.append("mAllowDraggingOffscreen=");
        sb14.append(this.mAllowDraggingOffscreen);
        printWriter.println(sb14.toString());
    }
}
