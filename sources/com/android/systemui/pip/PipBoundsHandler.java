package com.android.systemui.pip;

import android.app.ActivityManager.StackInfo;
import android.app.ActivityTaskManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.DisplayInfo;
import android.view.Gravity;
import android.view.IWindowManager;
import android.view.WindowContainerTransaction;
import android.view.WindowManagerGlobal;
import java.io.PrintWriter;

public class PipBoundsHandler {
    private static final String TAG = "PipBoundsHandler";
    private float mAspectRatio;
    private final Context mContext;
    private int mCurrentMinSize;
    private float mDefaultAspectRatio;
    private int mDefaultMinSize;
    private int mDefaultStackGravity;
    private final DisplayInfo mDisplayInfo = new DisplayInfo();
    private int mImeHeight;
    private boolean mIsImeShowing;
    private boolean mIsShelfShowing;
    private final Rect mLastDestinationBounds = new Rect();
    private ComponentName mLastPipComponentName;
    private float mMaxAspectRatio;
    private float mMinAspectRatio;
    private Size mOverrideMinimalSize;
    private Size mReentrySize;
    private float mReentrySnapFraction = -1.0f;
    private Point mScreenEdgeInsets;
    private int mShelfHeight;
    private final PipSnapAlgorithm mSnapAlgorithm;
    private final Rect mTmpInsets = new Rect();
    private final IWindowManager mWindowManager;

    public PipBoundsHandler(Context context, PipSnapAlgorithm pipSnapAlgorithm) {
        this.mContext = context;
        this.mSnapAlgorithm = pipSnapAlgorithm;
        this.mWindowManager = WindowManagerGlobal.getWindowManagerService();
        reloadResources();
        this.mAspectRatio = this.mDefaultAspectRatio;
    }

    private void reloadResources() {
        Point point;
        Resources resources = this.mContext.getResources();
        this.mDefaultAspectRatio = resources.getFloat(17105071);
        this.mDefaultStackGravity = resources.getInteger(17694782);
        int dimensionPixelSize = resources.getDimensionPixelSize(17105147);
        this.mDefaultMinSize = dimensionPixelSize;
        this.mCurrentMinSize = dimensionPixelSize;
        String string = resources.getString(17039766);
        Size parseSize = !string.isEmpty() ? Size.parseSize(string) : null;
        if (parseSize == null) {
            point = new Point();
        } else {
            point = new Point(dpToPx((float) parseSize.getWidth(), resources.getDisplayMetrics()), dpToPx((float) parseSize.getHeight(), resources.getDisplayMetrics()));
        }
        this.mScreenEdgeInsets = point;
        this.mMinAspectRatio = resources.getFloat(17105074);
        this.mMaxAspectRatio = resources.getFloat(17105073);
    }

    public void setMinEdgeSize(int i) {
        this.mCurrentMinSize = i;
    }

    public boolean setShelfHeight(boolean z, int i) {
        if ((z && i > 0) == this.mIsShelfShowing && i == this.mShelfHeight) {
            return false;
        }
        this.mIsShelfShowing = z;
        this.mShelfHeight = i;
        return true;
    }

    public void onImeVisibilityChanged(boolean z, int i) {
        this.mIsImeShowing = z;
        this.mImeHeight = i;
    }

    public void onMovementBoundsChanged(Rect rect, Rect rect2, Rect rect3, DisplayInfo displayInfo) {
        getInsetBounds(rect);
        Rect defaultBounds = getDefaultBounds(-1.0f, null);
        rect2.set(defaultBounds);
        if (rect3.isEmpty()) {
            rect3.set(defaultBounds);
        }
        if (isValidPictureInPictureAspectRatio(this.mAspectRatio)) {
            transformBoundsToAspectRatio(rect2, this.mAspectRatio, false);
        }
        displayInfo.copyFrom(this.mDisplayInfo);
    }

    public void onSaveReentryBounds(ComponentName componentName, Rect rect) {
        this.mReentrySnapFraction = getSnapFraction(rect);
        this.mReentrySize = new Size(rect.width(), rect.height());
        this.mLastPipComponentName = componentName;
    }

    public void onResetReentryBounds(ComponentName componentName) {
        if (componentName.equals(this.mLastPipComponentName)) {
            onResetReentryBoundsUnchecked();
        }
    }

    private void onResetReentryBoundsUnchecked() {
        this.mReentrySnapFraction = -1.0f;
        this.mReentrySize = null;
        this.mLastPipComponentName = null;
        this.mLastDestinationBounds.setEmpty();
    }

    public Rect getLastDestinationBounds() {
        return this.mLastDestinationBounds;
    }

    public void onDisplayInfoChanged(DisplayInfo displayInfo) {
        this.mDisplayInfo.copyFrom(displayInfo);
    }

    public void onConfigurationChanged() {
        reloadResources();
    }

    public void onAspectRatioChanged(float f) {
        this.mAspectRatio = f;
    }

    /* access modifiers changed from: 0000 */
    public Rect getDestinationBounds(float f, Rect rect, Size size) {
        Rect rect2;
        if (rect == null) {
            rect2 = new Rect(getDefaultBounds(this.mReentrySnapFraction, this.mReentrySize));
            if (this.mReentrySnapFraction == -1.0f && this.mReentrySize == null) {
                this.mOverrideMinimalSize = size;
            }
        } else {
            rect2 = new Rect(rect);
        }
        if (isValidPictureInPictureAspectRatio(f)) {
            transformBoundsToAspectRatio(rect2, f, false);
        }
        if (rect2.equals(rect)) {
            return rect;
        }
        this.mAspectRatio = f;
        onResetReentryBoundsUnchecked();
        this.mLastDestinationBounds.set(rect2);
        return rect2;
    }

    /* access modifiers changed from: 0000 */
    public float getDefaultAspectRatio() {
        return this.mDefaultAspectRatio;
    }

    public boolean onDisplayRotationChanged(Rect rect, int i, int i2, int i3, WindowContainerTransaction windowContainerTransaction) {
        if (i == this.mDisplayInfo.displayId && i2 != i3) {
            try {
                StackInfo stackInfo = ActivityTaskManager.getService().getStackInfo(2, 0);
                if (stackInfo == null) {
                    return false;
                }
                Rect rect2 = new Rect(this.mLastDestinationBounds);
                float snapFraction = getSnapFraction(rect2);
                this.mDisplayInfo.rotation = i3;
                updateDisplayInfoIfNeeded();
                this.mSnapAlgorithm.applySnapFraction(rect2, getMovementBounds(rect2, false), snapFraction);
                rect.set(rect2);
                this.mLastDestinationBounds.set(rect);
                windowContainerTransaction.setBounds(stackInfo.stackToken, rect);
                return true;
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to get StackInfo for pinned stack", e);
            }
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0010, code lost:
        if (r0.logicalWidth < r0.logicalHeight) goto L_0x001d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001b, code lost:
        if (r0.logicalWidth > r0.logicalHeight) goto L_0x001d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateDisplayInfoIfNeeded() {
        /*
            r5 = this;
            android.view.DisplayInfo r0 = r5.mDisplayInfo
            int r1 = r0.rotation
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0015
            r4 = 2
            if (r1 != r4) goto L_0x000c
            goto L_0x0015
        L_0x000c:
            int r1 = r0.logicalWidth
            int r0 = r0.logicalHeight
            if (r1 >= r0) goto L_0x0013
            goto L_0x001d
        L_0x0013:
            r2 = r3
            goto L_0x001d
        L_0x0015:
            android.view.DisplayInfo r0 = r5.mDisplayInfo
            int r1 = r0.logicalWidth
            int r0 = r0.logicalHeight
            if (r1 <= r0) goto L_0x0013
        L_0x001d:
            if (r2 == 0) goto L_0x0029
            android.view.DisplayInfo r5 = r5.mDisplayInfo
            int r0 = r5.logicalWidth
            int r1 = r5.logicalHeight
            r5.logicalWidth = r1
            r5.logicalHeight = r0
        L_0x0029:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.pip.PipBoundsHandler.updateDisplayInfoIfNeeded():void");
    }

    private boolean isValidPictureInPictureAspectRatio(float f) {
        return Float.compare(this.mMinAspectRatio, f) <= 0 && Float.compare(f, this.mMaxAspectRatio) <= 0;
    }

    public void transformBoundsToAspectRatio(Rect rect) {
        transformBoundsToAspectRatio(rect, this.mAspectRatio, true);
    }

    private void transformBoundsToAspectRatio(Rect rect, float f, boolean z) {
        Size size;
        float snapFraction = this.mSnapAlgorithm.getSnapFraction(rect, getMovementBounds(rect));
        if (z) {
            size = this.mSnapAlgorithm.getSizeForAspectRatio(new Size(rect.width(), rect.height()), f, (float) this.mCurrentMinSize);
        } else {
            int i = this.mDefaultMinSize;
            PipSnapAlgorithm pipSnapAlgorithm = this.mSnapAlgorithm;
            float f2 = (float) i;
            DisplayInfo displayInfo = this.mDisplayInfo;
            size = pipSnapAlgorithm.getSizeForAspectRatio(f, f2, displayInfo.logicalWidth, displayInfo.logicalHeight);
        }
        int centerX = (int) (((float) rect.centerX()) - (((float) size.getWidth()) / 2.0f));
        int centerY = (int) (((float) rect.centerY()) - (((float) size.getHeight()) / 2.0f));
        rect.set(centerX, centerY, size.getWidth() + centerX, size.getHeight() + centerY);
        Size size2 = this.mOverrideMinimalSize;
        if (size2 != null) {
            transformBoundsToMinimalSize(rect, f, size2);
        }
        this.mSnapAlgorithm.applySnapFraction(rect, getMovementBounds(rect), snapFraction);
    }

    private void transformBoundsToMinimalSize(Rect rect, float f, Size size) {
        Size size2;
        if (size != null) {
            if (((float) size.getWidth()) / ((float) size.getHeight()) > f) {
                size2 = new Size(size.getWidth(), (int) (((float) size.getWidth()) / f));
            } else {
                size2 = new Size((int) (((float) size.getHeight()) * f), size.getHeight());
            }
            Gravity.apply(this.mDefaultStackGravity, size2.getWidth(), size2.getHeight(), new Rect(rect), rect);
        }
    }

    private Rect getDefaultBounds(float f, Size size) {
        Rect rect = new Rect();
        int i = 0;
        if (f == -1.0f || size == null) {
            Rect rect2 = new Rect();
            getInsetBounds(rect2);
            PipSnapAlgorithm pipSnapAlgorithm = this.mSnapAlgorithm;
            float f2 = this.mDefaultAspectRatio;
            float f3 = (float) this.mDefaultMinSize;
            DisplayInfo displayInfo = this.mDisplayInfo;
            Size sizeForAspectRatio = pipSnapAlgorithm.getSizeForAspectRatio(f2, f3, displayInfo.logicalWidth, displayInfo.logicalHeight);
            int i2 = this.mDefaultStackGravity;
            int width = sizeForAspectRatio.getWidth();
            int height = sizeForAspectRatio.getHeight();
            int i3 = this.mIsImeShowing ? this.mImeHeight : 0;
            if (this.mIsShelfShowing) {
                i = this.mShelfHeight;
            }
            Gravity.apply(i2, width, height, rect2, 0, Math.max(i3, i), rect);
        } else {
            rect.set(0, 0, size.getWidth(), size.getHeight());
            this.mSnapAlgorithm.applySnapFraction(rect, getMovementBounds(rect), f);
        }
        return rect;
    }

    private void getInsetBounds(Rect rect) {
        try {
            this.mWindowManager.getStableInsets(this.mContext.getDisplayId(), this.mTmpInsets);
            rect.set(this.mTmpInsets.left + this.mScreenEdgeInsets.x, this.mTmpInsets.top + this.mScreenEdgeInsets.y, (this.mDisplayInfo.logicalWidth - this.mTmpInsets.right) - this.mScreenEdgeInsets.x, (this.mDisplayInfo.logicalHeight - this.mTmpInsets.bottom) - this.mScreenEdgeInsets.y);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get stable insets from WM", e);
        }
    }

    private Rect getMovementBounds(Rect rect) {
        return getMovementBounds(rect, true);
    }

    private Rect getMovementBounds(Rect rect, boolean z) {
        Rect rect2 = new Rect();
        getInsetBounds(rect2);
        this.mSnapAlgorithm.getMovementBounds(rect, rect2, rect2, (!z || !this.mIsImeShowing) ? 0 : this.mImeHeight);
        return rect2;
    }

    public float getSnapFraction(Rect rect) {
        return this.mSnapAlgorithm.getSnapFraction(rect, getMovementBounds(rect));
    }

    public void applySnapFraction(Rect rect, float f) {
        this.mSnapAlgorithm.applySnapFraction(rect, getMovementBounds(rect), f);
    }

    private int dpToPx(float f, DisplayMetrics displayMetrics) {
        return (int) TypedValue.applyDimension(1, f, displayMetrics);
    }

    public void dump(PrintWriter printWriter, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("  ");
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append(TAG);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(sb2);
        sb4.append("mLastPipComponentName=");
        sb4.append(this.mLastPipComponentName);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append(sb2);
        sb5.append("mReentrySnapFraction=");
        sb5.append(this.mReentrySnapFraction);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append(sb2);
        sb6.append("mDisplayInfo=");
        sb6.append(this.mDisplayInfo);
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append(sb2);
        sb7.append("mDefaultAspectRatio=");
        sb7.append(this.mDefaultAspectRatio);
        printWriter.println(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append(sb2);
        sb8.append("mMinAspectRatio=");
        sb8.append(this.mMinAspectRatio);
        printWriter.println(sb8.toString());
        StringBuilder sb9 = new StringBuilder();
        sb9.append(sb2);
        sb9.append("mMaxAspectRatio=");
        sb9.append(this.mMaxAspectRatio);
        printWriter.println(sb9.toString());
        StringBuilder sb10 = new StringBuilder();
        sb10.append(sb2);
        sb10.append("mAspectRatio=");
        sb10.append(this.mAspectRatio);
        printWriter.println(sb10.toString());
        StringBuilder sb11 = new StringBuilder();
        sb11.append(sb2);
        sb11.append("mDefaultStackGravity=");
        sb11.append(this.mDefaultStackGravity);
        printWriter.println(sb11.toString());
        StringBuilder sb12 = new StringBuilder();
        sb12.append(sb2);
        sb12.append("mIsImeShowing=");
        sb12.append(this.mIsImeShowing);
        printWriter.println(sb12.toString());
        StringBuilder sb13 = new StringBuilder();
        sb13.append(sb2);
        sb13.append("mImeHeight=");
        sb13.append(this.mImeHeight);
        printWriter.println(sb13.toString());
        StringBuilder sb14 = new StringBuilder();
        sb14.append(sb2);
        sb14.append("mIsShelfShowing=");
        sb14.append(this.mIsShelfShowing);
        printWriter.println(sb14.toString());
        StringBuilder sb15 = new StringBuilder();
        sb15.append(sb2);
        sb15.append("mShelfHeight=");
        sb15.append(this.mShelfHeight);
        printWriter.println(sb15.toString());
        this.mSnapAlgorithm.dump(printWriter, sb2);
    }
}
