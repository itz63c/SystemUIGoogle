package com.android.systemui.pip.phone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.hardware.input.InputManager;
import android.os.Looper;
import android.provider.DeviceConfig;
import android.provider.DeviceConfig.OnPropertiesChangedListener;
import android.provider.DeviceConfig.Properties;
import android.util.DisplayMetrics;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.InputMonitor;
import android.view.MotionEvent;
import com.android.internal.policy.TaskResizingAlgorithm;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.pip.PipBoundsHandler;
import com.android.systemui.pip.PipTaskOrganizer;
import com.android.systemui.util.DeviceConfigProxy;
import java.util.concurrent.Executor;

public class PipResizeGestureHandler {
    private boolean mAllowGesture = false;
    private int mCtrlType;
    private final int mDelta;
    private final int mDisplayId;
    private final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    private final PointF mDownPoint = new PointF();
    /* access modifiers changed from: private */
    public boolean mEnablePipResize;
    private InputEventReceiver mInputEventReceiver;
    private InputMonitor mInputMonitor;
    private boolean mIsAttached;
    private boolean mIsEnabled;
    private final Rect mLastResizeBounds = new Rect();
    private final Executor mMainExecutor;
    private final Point mMaxSize = new Point();
    private final Point mMinSize = new Point();
    private final PipMotionHelper mMotionHelper;
    private final PipBoundsHandler mPipBoundsHandler;
    private PipTaskOrganizer mPipTaskOrganizer;
    private final Rect mTmpBounds = new Rect();
    private final Region mTmpRegion = new Region();

    class SysUiInputEventReceiver extends InputEventReceiver {
        SysUiInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        public void onInputEvent(InputEvent inputEvent) {
            PipResizeGestureHandler.this.onInputEvent(inputEvent);
            finishInputEvent(inputEvent, true);
        }
    }

    public PipResizeGestureHandler(Context context, PipBoundsHandler pipBoundsHandler, PipTouchHandler pipTouchHandler, PipMotionHelper pipMotionHelper, DeviceConfigProxy deviceConfigProxy, PipTaskOrganizer pipTaskOrganizer) {
        Resources resources = context.getResources();
        context.getDisplay().getMetrics(this.mDisplayMetrics);
        this.mDisplayId = context.getDisplayId();
        this.mMainExecutor = context.getMainExecutor();
        this.mPipBoundsHandler = pipBoundsHandler;
        this.mMotionHelper = pipMotionHelper;
        this.mPipTaskOrganizer = pipTaskOrganizer;
        context.getDisplay().getRealSize(this.mMaxSize);
        this.mDelta = resources.getDimensionPixelSize(C2009R$dimen.pip_resize_edge_size);
        String str = "systemui";
        this.mEnablePipResize = DeviceConfig.getBoolean(str, "pip_user_resize", false);
        deviceConfigProxy.addOnPropertiesChangedListener(str, this.mMainExecutor, new OnPropertiesChangedListener() {
            public void onPropertiesChanged(Properties properties) {
                String str = "pip_user_resize";
                if (properties.getKeyset().contains(str)) {
                    PipResizeGestureHandler.this.mEnablePipResize = properties.getBoolean(str, false);
                }
            }
        });
    }

    private void disposeInputChannel() {
        InputEventReceiver inputEventReceiver = this.mInputEventReceiver;
        if (inputEventReceiver != null) {
            inputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        InputMonitor inputMonitor = this.mInputMonitor;
        if (inputMonitor != null) {
            inputMonitor.dispose();
            this.mInputMonitor = null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void onActivityPinned() {
        this.mIsAttached = true;
        updateIsEnabled();
    }

    /* access modifiers changed from: 0000 */
    public void onActivityUnpinned() {
        this.mIsAttached = false;
        updateIsEnabled();
    }

    private void updateIsEnabled() {
        boolean z = this.mIsAttached && this.mEnablePipResize;
        if (z != this.mIsEnabled) {
            this.mIsEnabled = z;
            disposeInputChannel();
            if (this.mIsEnabled) {
                this.mInputMonitor = InputManager.getInstance().monitorGestureInput("pip-resize", this.mDisplayId);
                this.mInputEventReceiver = new SysUiInputEventReceiver(this.mInputMonitor.getInputChannel(), Looper.getMainLooper());
            }
        }
    }

    /* access modifiers changed from: private */
    public void onInputEvent(InputEvent inputEvent) {
        if (inputEvent instanceof MotionEvent) {
            onMotionEvent((MotionEvent) inputEvent);
        }
    }

    private boolean isWithinTouchRegion(int i, int i2) {
        Rect bounds = this.mMotionHelper.getBounds();
        if (bounds == null) {
            return false;
        }
        this.mTmpBounds.set(bounds);
        Rect rect = this.mTmpBounds;
        int i3 = this.mDelta;
        rect.inset(-i3, -i3);
        this.mTmpRegion.set(this.mTmpBounds);
        this.mTmpRegion.op(bounds, Op.DIFFERENCE);
        if (!this.mTmpRegion.contains(i, i2)) {
            return false;
        }
        if (i < bounds.left) {
            this.mCtrlType |= 1;
        }
        if (i > bounds.right) {
            this.mCtrlType |= 2;
        }
        if (i2 < bounds.top) {
            this.mCtrlType |= 4;
        }
        if (i2 > bounds.bottom) {
            this.mCtrlType |= 8;
        }
        return true;
    }

    private void onMotionEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mLastResizeBounds.setEmpty();
            boolean isWithinTouchRegion = isWithinTouchRegion((int) motionEvent.getX(), (int) motionEvent.getY());
            this.mAllowGesture = isWithinTouchRegion;
            if (isWithinTouchRegion) {
                this.mDownPoint.set(motionEvent.getX(), motionEvent.getY());
            }
        } else if (this.mAllowGesture) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    this.mInputMonitor.pilferPointers();
                    Rect bounds = this.mMotionHelper.getBounds();
                    Rect rect = this.mLastResizeBounds;
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    PointF pointF = this.mDownPoint;
                    float f = pointF.x;
                    float f2 = pointF.y;
                    int i = this.mCtrlType;
                    Point point = this.mMinSize;
                    rect.set(TaskResizingAlgorithm.resizeDrag(x, y, f, f2, bounds, i, point.x, point.y, this.mMaxSize, true, true));
                    this.mPipBoundsHandler.transformBoundsToAspectRatio(this.mLastResizeBounds);
                    this.mPipTaskOrganizer.scheduleResizePip(this.mLastResizeBounds, null);
                    return;
                } else if (actionMasked != 3) {
                    if (actionMasked == 5) {
                        this.mAllowGesture = false;
                        return;
                    }
                    return;
                }
            }
            this.mPipTaskOrganizer.scheduleFinishResizePip(this.mLastResizeBounds);
            this.mMotionHelper.synchronizePinnedStackBounds();
            this.mCtrlType = 0;
            this.mAllowGesture = false;
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateMaxSize(int i, int i2) {
        this.mMaxSize.set(i, i2);
    }

    /* access modifiers changed from: 0000 */
    public void updateMinSize(int i, int i2) {
        this.mMinSize.set(i, i2);
    }
}
