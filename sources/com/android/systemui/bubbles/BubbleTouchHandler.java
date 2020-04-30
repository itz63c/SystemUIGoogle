package com.android.systemui.bubbles;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import com.android.systemui.Dependency;

class BubbleTouchHandler implements OnTouchListener {
    private final BubbleData mBubbleData;
    private BubbleController mController = ((BubbleController) Dependency.get(BubbleController.class));
    private boolean mMovedEnough;
    private final BubbleStackView mStack;
    private final PointF mTouchDown = new PointF();
    private int mTouchSlopSquared;
    private View mTouchedView;
    private VelocityTracker mVelocityTracker;
    private final PointF mViewPositionOnTouchDown = new PointF();

    BubbleTouchHandler(BubbleStackView bubbleStackView, BubbleData bubbleData, Context context) {
        int scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mTouchSlopSquared = scaledTouchSlop * scaledTouchSlop;
        this.mBubbleData = bubbleData;
        this.mStack = bubbleStackView;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        MotionEvent motionEvent2 = motionEvent;
        int actionMasked = motionEvent.getActionMasked();
        if (this.mTouchedView == null) {
            this.mTouchedView = this.mStack.getTargetView(motionEvent2);
        }
        if (actionMasked != 4) {
            View view2 = this.mTouchedView;
            if (view2 != null) {
                if ((view2 instanceof BadgedImageView) || (view2 instanceof BubbleStackView) || (view2 instanceof BubbleFlyoutView)) {
                    boolean equals = this.mStack.equals(this.mTouchedView);
                    boolean equals2 = this.mStack.getFlyoutView().equals(this.mTouchedView);
                    float rawX = motionEvent.getRawX();
                    float rawY = motionEvent.getRawY();
                    PointF pointF = this.mViewPositionOnTouchDown;
                    float f = pointF.x + rawX;
                    PointF pointF2 = this.mTouchDown;
                    float f2 = f - pointF2.x;
                    float f3 = (pointF.y + rawY) - pointF2.y;
                    if (actionMasked == 0) {
                        trackMovement(motionEvent2);
                        this.mTouchDown.set(rawX, rawY);
                        this.mStack.onGestureStart();
                        if (equals) {
                            this.mViewPositionOnTouchDown.set(this.mStack.getStackPosition());
                            this.mStack.setReleasedInDismissTargetAction(new Runnable() {
                                public final void run() {
                                    BubbleTouchHandler.this.lambda$onTouch$0$BubbleTouchHandler();
                                }
                            });
                            this.mStack.onDragStart();
                            this.mStack.passEventToMagnetizedObject(motionEvent2);
                        } else if (equals2) {
                            this.mStack.onFlyoutDragStart();
                        } else {
                            this.mViewPositionOnTouchDown.set(this.mTouchedView.getTranslationX(), this.mTouchedView.getTranslationY());
                            this.mStack.setReleasedInDismissTargetAction(new Runnable(((BadgedImageView) this.mTouchedView).getKey()) {
                                public final /* synthetic */ String f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    BubbleTouchHandler.this.lambda$onTouch$1$BubbleTouchHandler(this.f$1);
                                }
                            });
                            this.mStack.onBubbleDragStart(this.mTouchedView);
                            this.mStack.passEventToMagnetizedObject(motionEvent2);
                        }
                    } else if (actionMasked == 1) {
                        trackMovement(motionEvent2);
                        this.mVelocityTracker.computeCurrentVelocity(1000);
                        float xVelocity = this.mVelocityTracker.getXVelocity();
                        float yVelocity = this.mVelocityTracker.getYVelocity();
                        if (equals2 && this.mMovedEnough) {
                            this.mStack.onFlyoutDragFinished(rawX - this.mTouchDown.x, xVelocity);
                        } else if (equals2) {
                            if (!this.mBubbleData.isExpanded() && !this.mMovedEnough) {
                                this.mStack.onFlyoutTapped();
                            }
                        } else if (this.mMovedEnough) {
                            if (!this.mStack.passEventToMagnetizedObject(motionEvent2)) {
                                if (equals) {
                                    this.mStack.onDragFinish(f2, f3, xVelocity, yVelocity);
                                } else {
                                    this.mStack.onBubbleDragFinish(this.mTouchedView, f2, f3, xVelocity, yVelocity);
                                }
                            }
                        } else if (this.mTouchedView == this.mStack.getExpandedBubbleView()) {
                            this.mBubbleData.setExpanded(false);
                        } else if (equals) {
                            this.mStack.onStackTapped();
                        } else {
                            String key = ((BadgedImageView) this.mTouchedView).getKey();
                            if (key == "Overflow") {
                                this.mStack.showOverflow();
                            } else {
                                this.mStack.expandBubble(this.mBubbleData.getBubbleWithKey(key));
                            }
                        }
                        resetForNextGesture();
                    } else if (actionMasked == 2) {
                        trackMovement(motionEvent2);
                        PointF pointF3 = this.mTouchDown;
                        float f4 = rawX - pointF3.x;
                        float f5 = rawY - pointF3.y;
                        if ((f4 * f4) + (f5 * f5) > ((float) this.mTouchSlopSquared) && !this.mMovedEnough) {
                            this.mMovedEnough = true;
                        }
                        if (this.mMovedEnough) {
                            if (equals2) {
                                this.mStack.onFlyoutDragged(f4);
                            } else if (!this.mStack.passEventToMagnetizedObject(motionEvent2)) {
                                if (equals) {
                                    this.mStack.onDragged(f2, f3);
                                } else {
                                    this.mStack.onBubbleDragged(this.mTouchedView, f2, f3);
                                }
                            }
                        }
                    } else if (actionMasked == 3) {
                        resetForNextGesture();
                    }
                    return true;
                }
                this.mStack.maybeShowManageEducation(false);
                resetForNextGesture();
                return false;
            }
        }
        this.mBubbleData.setExpanded(false);
        this.mStack.hideStackUserEducation(false);
        resetForNextGesture();
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onTouch$0 */
    public /* synthetic */ void lambda$onTouch$0$BubbleTouchHandler() {
        this.mController.dismissStack(1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onTouch$1 */
    public /* synthetic */ void lambda$onTouch$1$BubbleTouchHandler(String str) {
        Bubble bubbleWithKey = this.mBubbleData.getBubbleWithKey(str);
        if (bubbleWithKey != null) {
            this.mController.removeBubble(bubbleWithKey.getEntry(), 1);
        }
    }

    private void resetForNextGesture() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        this.mTouchedView = null;
        this.mMovedEnough = false;
        this.mStack.onGestureFinished();
    }

    private void trackMovement(MotionEvent motionEvent) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
    }
}
