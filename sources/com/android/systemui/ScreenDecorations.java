package com.android.systemui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.drawable.VectorDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.view.DisplayCutout;
import android.view.DisplayInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.internal.util.Preconditions;
import com.android.systemui.CameraAvailabilityListener.CameraTransitionCallback;
import com.android.systemui.CameraAvailabilityListener.Factory;
import com.android.systemui.RegionInterceptingFrameLayout.RegionInterceptableView;
import com.android.systemui.ScreenDecorations.DisplayCutoutView;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p007qs.SecureSetting;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class ScreenDecorations extends SystemUI implements Tunable {
    private static final boolean DEBUG_COLOR;
    private static final boolean DEBUG_SCREENSHOT_ROUNDED_CORNERS;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private CameraAvailabilityListener mCameraListener;
    private CameraTransitionCallback mCameraTransitionCallback = new CameraTransitionCallback() {
        public void onApplyCameraProtection(Path path, Rect rect) {
            DisplayCutoutView[] access$000;
            for (DisplayCutoutView displayCutoutView : ScreenDecorations.this.mCutoutViews) {
                displayCutoutView.setProtection(path, rect);
                displayCutoutView.setShowProtection(true);
            }
        }

        public void onHideCameraProtection() {
            for (DisplayCutoutView showProtection : ScreenDecorations.this.mCutoutViews) {
                showProtection.setShowProtection(false);
            }
        }
    };
    /* access modifiers changed from: private */
    public SecureSetting mColorInversionSetting;
    /* access modifiers changed from: private */
    public DisplayCutoutView[] mCutoutViews = new DisplayCutoutView[4];
    private float mDensity;
    private DisplayListener mDisplayListener;
    private DisplayManager mDisplayManager;
    private Handler mHandler;
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.USER_SWITCHED")) {
                ScreenDecorations.this.mColorInversionSetting.setUserId(intent.getIntExtra("android.intent.extra.user_handle", ActivityManager.getCurrentUser()));
                ScreenDecorations screenDecorations = ScreenDecorations.this;
                screenDecorations.updateColorInversion(screenDecorations.mColorInversionSetting.getValue());
            }
        }
    };
    protected boolean mIsRegistered;
    private boolean mIsRoundedCornerMultipleRadius;
    private final Handler mMainHandler;
    protected View[] mOverlays;
    /* access modifiers changed from: private */
    public boolean mPendingRotationChange;
    /* access modifiers changed from: private */
    public int mRotation;
    protected int mRoundedDefault;
    protected int mRoundedDefaultBottom;
    protected int mRoundedDefaultTop;
    private final TunerService mTunerService;
    private WindowManager mWindowManager;

    public static class DisplayCutoutView extends View implements DisplayListener, RegionInterceptableView {
        private final Path mBoundingPath = new Path();
        private final Rect mBoundingRect = new Rect();
        private final List<Rect> mBounds = new ArrayList();
        /* access modifiers changed from: private */
        public ValueAnimator mCameraProtectionAnimator;
        private float mCameraProtectionProgress = 0.5f;
        private int mColor = -16777216;
        private final ScreenDecorations mDecorations;
        private final DisplayInfo mInfo = new DisplayInfo();
        private int mInitialPosition;
        private final int[] mLocation = new int[2];
        private final Paint mPaint = new Paint();
        private int mPosition;
        private Path mProtectionPath;
        private Path mProtectionPathOrig;
        private RectF mProtectionRect;
        private RectF mProtectionRectOrig;
        private int mRotation;
        /* access modifiers changed from: private */
        public boolean mShowProtection = false;
        private Rect mTotalBounds = new Rect();

        public void onDisplayAdded(int i) {
        }

        public void onDisplayRemoved(int i) {
        }

        public DisplayCutoutView(Context context, int i, ScreenDecorations screenDecorations) {
            super(context);
            this.mInitialPosition = i;
            this.mDecorations = screenDecorations;
            setId(C2011R$id.display_cutout);
        }

        public void setColor(int i) {
            this.mColor = i;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).registerDisplayListener(this, getHandler());
            update();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).unregisterDisplayListener(this);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            getLocationOnScreen(this.mLocation);
            int[] iArr = this.mLocation;
            canvas.translate((float) (-iArr[0]), (float) (-iArr[1]));
            if (!this.mBoundingPath.isEmpty()) {
                this.mPaint.setColor(this.mColor);
                this.mPaint.setStyle(Style.FILL);
                this.mPaint.setAntiAlias(true);
                canvas.drawPath(this.mBoundingPath, this.mPaint);
            }
            if (this.mCameraProtectionProgress > 0.5f && !this.mProtectionRect.isEmpty()) {
                float f = this.mCameraProtectionProgress;
                canvas.scale(f, f, this.mProtectionRect.centerX(), this.mProtectionRect.centerY());
                canvas.drawPath(this.mProtectionPath, this.mPaint);
            }
        }

        public void onDisplayChanged(int i) {
            if (i == getDisplay().getDisplayId()) {
                update();
            }
        }

        public void setRotation(int i) {
            this.mRotation = i;
            update();
        }

        /* access modifiers changed from: 0000 */
        public void setProtection(Path path, Rect rect) {
            if (this.mProtectionPathOrig == null) {
                this.mProtectionPathOrig = new Path();
                this.mProtectionPath = new Path();
            }
            this.mProtectionPathOrig.set(path);
            if (this.mProtectionRectOrig == null) {
                this.mProtectionRectOrig = new RectF();
                this.mProtectionRect = new RectF();
            }
            this.mProtectionRectOrig.set(rect);
        }

        /* access modifiers changed from: 0000 */
        public void setShowProtection(boolean z) {
            if (this.mShowProtection != z) {
                this.mShowProtection = z;
                updateBoundingPath();
                if (this.mShowProtection) {
                    requestLayout();
                }
                ValueAnimator valueAnimator = this.mCameraProtectionAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = this.mCameraProtectionProgress;
                fArr[1] = this.mShowProtection ? 1.0f : 0.5f;
                ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(750);
                this.mCameraProtectionAnimator = duration;
                duration.setInterpolator(Interpolators.DECELERATE_QUINT);
                this.mCameraProtectionAnimator.addUpdateListener(new AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        DisplayCutoutView.this.lambda$setShowProtection$1$ScreenDecorations$DisplayCutoutView(valueAnimator);
                    }
                });
                this.mCameraProtectionAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        DisplayCutoutView.this.mCameraProtectionAnimator = null;
                        if (!DisplayCutoutView.this.mShowProtection) {
                            DisplayCutoutView.this.requestLayout();
                        }
                    }
                });
                this.mCameraProtectionAnimator.start();
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$setShowProtection$1 */
        public /* synthetic */ void lambda$setShowProtection$1$ScreenDecorations$DisplayCutoutView(ValueAnimator valueAnimator) {
            this.mCameraProtectionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        private void update() {
            int i;
            if (isAttachedToWindow() && !this.mDecorations.mPendingRotationChange) {
                this.mPosition = ScreenDecorations.getBoundPositionFromRotation(this.mInitialPosition, this.mRotation);
                requestLayout();
                getDisplay().getDisplayInfo(this.mInfo);
                this.mBounds.clear();
                this.mBoundingRect.setEmpty();
                this.mBoundingPath.reset();
                if (!ScreenDecorations.shouldDrawCutout(getContext()) || !hasCutout()) {
                    i = 8;
                } else {
                    this.mBounds.addAll(this.mInfo.displayCutout.getBoundingRects());
                    localBounds(this.mBoundingRect);
                    updateGravity();
                    updateBoundingPath();
                    invalidate();
                    i = 0;
                }
                if (i != getVisibility()) {
                    setVisibility(i);
                }
            }
        }

        private void updateBoundingPath() {
            DisplayInfo displayInfo = this.mInfo;
            int i = displayInfo.logicalWidth;
            int i2 = displayInfo.logicalHeight;
            int i3 = displayInfo.rotation;
            boolean z = true;
            if (!(i3 == 1 || i3 == 3)) {
                z = false;
            }
            int i4 = z ? i2 : i;
            if (!z) {
                i = i2;
            }
            this.mBoundingPath.set(DisplayCutout.pathFromResources(getResources(), i4, i));
            Matrix matrix = new Matrix();
            transformPhysicalToLogicalCoordinates(this.mInfo.rotation, i4, i, matrix);
            this.mBoundingPath.transform(matrix);
            Path path = this.mProtectionPathOrig;
            if (path != null) {
                this.mProtectionPath.set(path);
                this.mProtectionPath.transform(matrix);
                matrix.mapRect(this.mProtectionRect, this.mProtectionRectOrig);
            }
        }

        private static void transformPhysicalToLogicalCoordinates(int i, int i2, int i3, Matrix matrix) {
            if (i == 0) {
                matrix.reset();
            } else if (i == 1) {
                matrix.setRotate(270.0f);
                matrix.postTranslate(0.0f, (float) i2);
            } else if (i == 2) {
                matrix.setRotate(180.0f);
                matrix.postTranslate((float) i2, (float) i3);
            } else if (i == 3) {
                matrix.setRotate(90.0f);
                matrix.postTranslate((float) i3, 0.0f);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Unknown rotation: ");
                sb.append(i);
                throw new IllegalArgumentException(sb.toString());
            }
        }

        private void updateGravity() {
            LayoutParams layoutParams = getLayoutParams();
            if (layoutParams instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) layoutParams;
                int gravity = getGravity(this.mInfo.displayCutout);
                if (layoutParams2.gravity != gravity) {
                    layoutParams2.gravity = gravity;
                    setLayoutParams(layoutParams2);
                }
            }
        }

        private boolean hasCutout() {
            DisplayCutout displayCutout = this.mInfo.displayCutout;
            if (displayCutout == null) {
                return false;
            }
            int i = this.mPosition;
            if (i == 0) {
                return !displayCutout.getBoundingRectLeft().isEmpty();
            }
            if (i == 1) {
                return !displayCutout.getBoundingRectTop().isEmpty();
            }
            if (i == 3) {
                return !displayCutout.getBoundingRectBottom().isEmpty();
            }
            if (i == 2) {
                return !displayCutout.getBoundingRectRight().isEmpty();
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            if (this.mBounds.isEmpty()) {
                super.onMeasure(i, i2);
                return;
            }
            if (this.mShowProtection) {
                this.mTotalBounds.union(this.mBoundingRect);
                Rect rect = this.mTotalBounds;
                RectF rectF = this.mProtectionRect;
                rect.union((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                setMeasuredDimension(View.resolveSizeAndState(this.mTotalBounds.width(), i, 0), View.resolveSizeAndState(this.mTotalBounds.height(), i2, 0));
            } else {
                setMeasuredDimension(View.resolveSizeAndState(this.mBoundingRect.width(), i, 0), View.resolveSizeAndState(this.mBoundingRect.height(), i2, 0));
            }
        }

        public static void boundsFromDirection(DisplayCutout displayCutout, int i, Rect rect) {
            if (i == 3) {
                rect.set(displayCutout.getBoundingRectLeft());
            } else if (i == 5) {
                rect.set(displayCutout.getBoundingRectRight());
            } else if (i == 48) {
                rect.set(displayCutout.getBoundingRectTop());
            } else if (i != 80) {
                rect.setEmpty();
            } else {
                rect.set(displayCutout.getBoundingRectBottom());
            }
        }

        private void localBounds(Rect rect) {
            DisplayCutout displayCutout = this.mInfo.displayCutout;
            boundsFromDirection(displayCutout, getGravity(displayCutout), rect);
        }

        private int getGravity(DisplayCutout displayCutout) {
            int i = this.mPosition;
            if (i == 0) {
                if (!displayCutout.getBoundingRectLeft().isEmpty()) {
                    return 3;
                }
            } else if (i == 1) {
                if (!displayCutout.getBoundingRectTop().isEmpty()) {
                    return 48;
                }
            } else if (i == 3) {
                if (!displayCutout.getBoundingRectBottom().isEmpty()) {
                    return 80;
                }
            } else if (i == 2 && !displayCutout.getBoundingRectRight().isEmpty()) {
                return 5;
            }
            return 0;
        }

        public boolean shouldInterceptTouch() {
            return this.mInfo.displayCutout != null && getVisibility() == 0;
        }

        public Region getInterceptRegion() {
            if (this.mInfo.displayCutout == null) {
                return null;
            }
            View rootView = getRootView();
            Region rectsToRegion = ScreenDecorations.rectsToRegion(this.mInfo.displayCutout.getBoundingRects());
            rootView.getLocationOnScreen(this.mLocation);
            int[] iArr = this.mLocation;
            rectsToRegion.translate(-iArr[0], -iArr[1]);
            rectsToRegion.op(rootView.getLeft(), rootView.getTop(), rootView.getRight(), rootView.getBottom(), Op.INTERSECT);
            return rectsToRegion;
        }
    }

    private class RestartingPreDrawListener implements OnPreDrawListener {
        private final int mTargetRotation;
        private final View mView;

        private RestartingPreDrawListener(View view, int i, int i2) {
            this.mView = view;
            this.mTargetRotation = i2;
        }

        public boolean onPreDraw() {
            this.mView.getViewTreeObserver().removeOnPreDrawListener(this);
            if (this.mTargetRotation == ScreenDecorations.this.mRotation) {
                return true;
            }
            ScreenDecorations.this.mPendingRotationChange = false;
            ScreenDecorations.this.updateOrientation();
            this.mView.invalidate();
            return false;
        }
    }

    private class ValidatingPreDrawListener implements OnPreDrawListener {
        private final View mView;

        public ValidatingPreDrawListener(View view) {
            this.mView = view;
        }

        public boolean onPreDraw() {
            if (ScreenDecorations.this.mContext.getDisplay().getRotation() == ScreenDecorations.this.mRotation || ScreenDecorations.this.mPendingRotationChange) {
                return true;
            }
            this.mView.invalidate();
            return false;
        }
    }

    /* access modifiers changed from: private */
    public static int getBoundPositionFromRotation(int i, int i2) {
        int i3 = i - i2;
        return i3 < 0 ? i3 + 4 : i3;
    }

    static {
        boolean z = SystemProperties.getBoolean("debug.screenshot_rounded_corners", false);
        DEBUG_SCREENSHOT_ROUNDED_CORNERS = z;
        DEBUG_COLOR = z;
    }

    public static Region rectsToRegion(List<Rect> list) {
        Region obtain = Region.obtain();
        if (list != null) {
            for (Rect rect : list) {
                if (rect != null && !rect.isEmpty()) {
                    obtain.op(rect, Op.UNION);
                }
            }
        }
        return obtain;
    }

    public ScreenDecorations(Context context, Handler handler, BroadcastDispatcher broadcastDispatcher, TunerService tunerService) {
        super(context);
        this.mMainHandler = handler;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mTunerService = tunerService;
    }

    public void start() {
        Handler startHandlerThread = startHandlerThread();
        this.mHandler = startHandlerThread;
        startHandlerThread.post(new Runnable() {
            public final void run() {
                ScreenDecorations.this.startOnScreenDecorationsThread();
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public Handler startHandlerThread() {
        HandlerThread handlerThread = new HandlerThread("ScreenDecorations");
        handlerThread.start();
        return handlerThread.getThreadHandler();
    }

    /* access modifiers changed from: private */
    public void startOnScreenDecorationsThread() {
        this.mRotation = this.mContext.getDisplay().getRotation();
        this.mWindowManager = (WindowManager) this.mContext.getSystemService(WindowManager.class);
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        this.mIsRoundedCornerMultipleRadius = this.mContext.getResources().getBoolean(C2007R$bool.config_roundedCornerMultipleRadius);
        updateRoundedCornerRadii();
        setupDecorations();
        setupCameraListener();
        C06852 r0 = new DisplayListener() {
            public void onDisplayAdded(int i) {
            }

            public void onDisplayRemoved(int i) {
            }

            public void onDisplayChanged(int i) {
                int rotation = ScreenDecorations.this.mContext.getDisplay().getRotation();
                ScreenDecorations screenDecorations = ScreenDecorations.this;
                if (!(screenDecorations.mOverlays == null || screenDecorations.mRotation == rotation)) {
                    ScreenDecorations.this.mPendingRotationChange = true;
                    for (int i2 = 0; i2 < 4; i2++) {
                        View[] viewArr = ScreenDecorations.this.mOverlays;
                        if (viewArr[i2] != null) {
                            ViewTreeObserver viewTreeObserver = viewArr[i2].getViewTreeObserver();
                            ScreenDecorations screenDecorations2 = ScreenDecorations.this;
                            RestartingPreDrawListener restartingPreDrawListener = new RestartingPreDrawListener(screenDecorations2.mOverlays[i2], i2, rotation);
                            viewTreeObserver.addOnPreDrawListener(restartingPreDrawListener);
                        }
                    }
                }
                ScreenDecorations.this.updateOrientation();
            }
        };
        this.mDisplayListener = r0;
        this.mDisplayManager.registerDisplayListener(r0, this.mHandler);
        updateOrientation();
    }

    private void setupDecorations() {
        Rect[] rectArr;
        if (hasRoundedCorners() || shouldDrawCutout()) {
            DisplayCutout cutout = getCutout();
            if (cutout == null) {
                rectArr = null;
            } else {
                rectArr = cutout.getBoundingRectsAll();
            }
            for (int i = 0; i < 4; i++) {
                int boundPositionFromRotation = getBoundPositionFromRotation(i, this.mRotation);
                if ((rectArr == null || rectArr[boundPositionFromRotation].isEmpty()) && !shouldShowRoundedCorner(i)) {
                    removeOverlay(i);
                } else {
                    createOverlay(i);
                }
            }
        } else {
            removeAllOverlays();
        }
        if (!hasOverlays()) {
            this.mMainHandler.post(new Runnable() {
                public final void run() {
                    ScreenDecorations.this.lambda$setupDecorations$1$ScreenDecorations();
                }
            });
            SecureSetting secureSetting = this.mColorInversionSetting;
            if (secureSetting != null) {
                secureSetting.setListening(false);
            }
            this.mBroadcastDispatcher.unregisterReceiver(this.mIntentReceiver);
            this.mIsRegistered = false;
        } else if (!this.mIsRegistered) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            this.mDisplayManager.getDisplay(0).getMetrics(displayMetrics);
            this.mDensity = displayMetrics.density;
            this.mMainHandler.post(new Runnable() {
                public final void run() {
                    ScreenDecorations.this.lambda$setupDecorations$0$ScreenDecorations();
                }
            });
            if (this.mColorInversionSetting == null) {
                this.mColorInversionSetting = new SecureSetting(this.mContext, this.mHandler, "accessibility_display_inversion_enabled") {
                    /* access modifiers changed from: protected */
                    public void handleValueChanged(int i, boolean z) {
                        ScreenDecorations.this.updateColorInversion(i);
                    }
                };
            }
            this.mColorInversionSetting.setListening(true);
            this.mColorInversionSetting.onChange(false);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            this.mBroadcastDispatcher.registerReceiverWithHandler(this.mIntentReceiver, intentFilter, this.mHandler);
            this.mIsRegistered = true;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setupDecorations$0 */
    public /* synthetic */ void lambda$setupDecorations$0$ScreenDecorations() {
        this.mTunerService.addTunable(this, "sysui_rounded_size");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setupDecorations$1 */
    public /* synthetic */ void lambda$setupDecorations$1$ScreenDecorations() {
        this.mTunerService.removeTunable(this);
    }

    /* access modifiers changed from: 0000 */
    public DisplayCutout getCutout() {
        return this.mContext.getDisplay().getCutout();
    }

    /* access modifiers changed from: 0000 */
    public boolean hasOverlays() {
        if (this.mOverlays == null) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (this.mOverlays[i] != null) {
                return true;
            }
        }
        this.mOverlays = null;
        return false;
    }

    private void removeAllOverlays() {
        if (this.mOverlays != null) {
            for (int i = 0; i < 4; i++) {
                if (this.mOverlays[i] != null) {
                    removeOverlay(i);
                }
            }
            this.mOverlays = null;
        }
    }

    private void removeOverlay(int i) {
        View[] viewArr = this.mOverlays;
        if (viewArr != null && viewArr[i] != null) {
            this.mWindowManager.removeViewImmediate(viewArr[i]);
            this.mOverlays[i] = null;
        }
    }

    private void createOverlay(final int i) {
        if (this.mOverlays == null) {
            this.mOverlays = new View[4];
        }
        if (this.mCutoutViews == null) {
            this.mCutoutViews = new DisplayCutoutView[4];
        }
        View[] viewArr = this.mOverlays;
        if (viewArr[i] == null) {
            viewArr[i] = LayoutInflater.from(this.mContext).inflate(C2013R$layout.rounded_corners, null);
            this.mCutoutViews[i] = new DisplayCutoutView(this.mContext, i, this);
            ((ViewGroup) this.mOverlays[i]).addView(this.mCutoutViews[i]);
            this.mOverlays[i].setSystemUiVisibility(256);
            this.mOverlays[i].setAlpha(0.0f);
            this.mOverlays[i].setForceDarkAllowed(false);
            updateView(i);
            this.mWindowManager.addView(this.mOverlays[i], getWindowLayoutParams(i));
            this.mOverlays[i].addOnLayoutChangeListener(new OnLayoutChangeListener() {
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    ScreenDecorations.this.mOverlays[i].removeOnLayoutChangeListener(this);
                    ScreenDecorations.this.mOverlays[i].animate().alpha(1.0f).setDuration(1000).start();
                }
            });
            this.mOverlays[i].getViewTreeObserver().addOnPreDrawListener(new ValidatingPreDrawListener(this.mOverlays[i]));
        }
    }

    private void updateView(int i) {
        View[] viewArr = this.mOverlays;
        if (viewArr != null && viewArr[i] != null) {
            updateRoundedCornerView(i, C2011R$id.left);
            updateRoundedCornerView(i, C2011R$id.right);
            DisplayCutoutView[] displayCutoutViewArr = this.mCutoutViews;
            if (displayCutoutViewArr != null && displayCutoutViewArr[i] != null) {
                displayCutoutViewArr[i].setRotation(this.mRotation);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public WindowManager.LayoutParams getWindowLayoutParams(int i) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(getWidthLayoutParamByPos(i), getHeightLayoutParamByPos(i), 2024, 545259816, -3);
        int i2 = layoutParams.privateFlags | 80;
        layoutParams.privateFlags = i2;
        if (!DEBUG_SCREENSHOT_ROUNDED_CORNERS) {
            layoutParams.privateFlags = i2 | 1048576;
        }
        layoutParams.setTitle(getWindowTitleByPos(i));
        layoutParams.gravity = getOverlayWindowGravity(i);
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.setFitInsetsTypes(0);
        layoutParams.privateFlags |= 16777216;
        return layoutParams;
    }

    private int getWidthLayoutParamByPos(int i) {
        int boundPositionFromRotation = getBoundPositionFromRotation(i, this.mRotation);
        return (boundPositionFromRotation == 1 || boundPositionFromRotation == 3) ? -1 : -2;
    }

    private int getHeightLayoutParamByPos(int i) {
        int boundPositionFromRotation = getBoundPositionFromRotation(i, this.mRotation);
        return (boundPositionFromRotation == 1 || boundPositionFromRotation == 3) ? -2 : -1;
    }

    private static String getWindowTitleByPos(int i) {
        if (i == 0) {
            return "ScreenDecorOverlayLeft";
        }
        if (i == 1) {
            return "ScreenDecorOverlay";
        }
        if (i == 2) {
            return "ScreenDecorOverlayRight";
        }
        if (i == 3) {
            return "ScreenDecorOverlayBottom";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown bound position: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    private int getOverlayWindowGravity(int i) {
        int boundPositionFromRotation = getBoundPositionFromRotation(i, this.mRotation);
        if (boundPositionFromRotation == 0) {
            return 3;
        }
        if (boundPositionFromRotation == 1) {
            return 48;
        }
        if (boundPositionFromRotation == 2) {
            return 5;
        }
        if (boundPositionFromRotation == 3) {
            return 80;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown bound position: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    private void setupCameraListener() {
        if (this.mContext.getResources().getBoolean(C2007R$bool.config_enableDisplayCutoutProtection)) {
            Factory factory = CameraAvailabilityListener.Factory;
            Context context = this.mContext;
            Handler handler = this.mHandler;
            Objects.requireNonNull(handler);
            CameraAvailabilityListener build = factory.build(context, new Executor(handler) {
                public final /* synthetic */ Handler f$0;

                {
                    this.f$0 = r1;
                }

                public final void execute(Runnable runnable) {
                    this.f$0.post(runnable);
                }
            });
            this.mCameraListener = build;
            build.addTransitionCallback(this.mCameraTransitionCallback);
            this.mCameraListener.startListening();
        }
    }

    /* access modifiers changed from: private */
    public void updateColorInversion(int i) {
        int i2 = i != 0 ? -1 : -16777216;
        if (DEBUG_COLOR) {
            i2 = -65536;
        }
        ColorStateList valueOf = ColorStateList.valueOf(i2);
        if (this.mOverlays != null) {
            for (int i3 = 0; i3 < 4; i3++) {
                View[] viewArr = this.mOverlays;
                if (viewArr[i3] != null) {
                    int childCount = ((ViewGroup) viewArr[i3]).getChildCount();
                    for (int i4 = 0; i4 < childCount; i4++) {
                        View childAt = ((ViewGroup) this.mOverlays[i3]).getChildAt(i4);
                        if (childAt instanceof ImageView) {
                            ((ImageView) childAt).setImageTintList(valueOf);
                        } else if (childAt instanceof DisplayCutoutView) {
                            ((DisplayCutoutView) childAt).setColor(i2);
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        this.mHandler.post(new Runnable() {
            public final void run() {
                ScreenDecorations.this.lambda$onConfigurationChanged$2$ScreenDecorations();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onConfigurationChanged$2 */
    public /* synthetic */ void lambda$onConfigurationChanged$2$ScreenDecorations() {
        this.mPendingRotationChange = false;
        updateOrientation();
        updateRoundedCornerRadii();
        setupDecorations();
        if (this.mOverlays != null) {
            updateLayoutParams();
        }
    }

    /* access modifiers changed from: private */
    public void updateOrientation() {
        boolean z = this.mHandler.getLooper().getThread() == Thread.currentThread();
        StringBuilder sb = new StringBuilder();
        sb.append("must call on ");
        sb.append(this.mHandler.getLooper().getThread());
        sb.append(", but was ");
        sb.append(Thread.currentThread());
        Preconditions.checkState(z, sb.toString());
        if (!this.mPendingRotationChange) {
            int rotation = this.mContext.getDisplay().getRotation();
            if (rotation != this.mRotation) {
                this.mRotation = rotation;
                if (this.mOverlays != null) {
                    updateLayoutParams();
                    for (int i = 0; i < 4; i++) {
                        if (this.mOverlays[i] != null) {
                            updateView(i);
                        }
                    }
                }
            }
        }
    }

    private void updateRoundedCornerRadii() {
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(17105436);
        int dimensionPixelSize2 = this.mContext.getResources().getDimensionPixelSize(17105440);
        int dimensionPixelSize3 = this.mContext.getResources().getDimensionPixelSize(17105438);
        if ((this.mRoundedDefault == dimensionPixelSize && this.mRoundedDefaultBottom == dimensionPixelSize3 && this.mRoundedDefaultTop == dimensionPixelSize2) ? false : true) {
            if (this.mIsRoundedCornerMultipleRadius) {
                VectorDrawable vectorDrawable = (VectorDrawable) this.mContext.getDrawable(C2010R$drawable.rounded);
                int max = Math.max(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
                this.mRoundedDefault = max;
                this.mRoundedDefaultBottom = max;
                this.mRoundedDefaultTop = max;
            } else {
                this.mRoundedDefault = dimensionPixelSize;
                this.mRoundedDefaultTop = dimensionPixelSize2;
                this.mRoundedDefaultBottom = dimensionPixelSize3;
            }
            onTuningChanged("sysui_rounded_size", null);
        }
    }

    private void updateRoundedCornerView(int i, int i2) {
        View findViewById = this.mOverlays[i].findViewById(i2);
        if (findViewById != null) {
            findViewById.setVisibility(8);
            if (shouldShowRoundedCorner(i)) {
                int roundedCornerGravity = getRoundedCornerGravity(i, i2 == C2011R$id.left);
                ((FrameLayout.LayoutParams) findViewById.getLayoutParams()).gravity = roundedCornerGravity;
                findViewById.setRotation((float) getRoundedCornerRotation(roundedCornerGravity));
                findViewById.setVisibility(0);
            }
        }
    }

    private int getRoundedCornerGravity(int i, boolean z) {
        int boundPositionFromRotation = getBoundPositionFromRotation(i, this.mRotation);
        int i2 = 51;
        int i3 = 83;
        if (boundPositionFromRotation != 0) {
            int i4 = 53;
            if (boundPositionFromRotation == 1) {
                if (!z) {
                    i2 = 53;
                }
                return i2;
            } else if (boundPositionFromRotation == 2) {
                if (!z) {
                    i4 = 85;
                }
                return i4;
            } else if (boundPositionFromRotation == 3) {
                if (!z) {
                    i3 = 85;
                }
                return i3;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Incorrect position: ");
                sb.append(boundPositionFromRotation);
                throw new IllegalArgumentException(sb.toString());
            }
        } else {
            if (!z) {
                i2 = 83;
            }
            return i2;
        }
    }

    private int getRoundedCornerRotation(int i) {
        if (i == 51) {
            return 0;
        }
        if (i == 53) {
            return 90;
        }
        if (i == 83) {
            return 270;
        }
        if (i == 85) {
            return 180;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unsupported gravity: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    private boolean hasRoundedCorners() {
        return this.mRoundedDefault > 0 || this.mRoundedDefaultBottom > 0 || this.mRoundedDefaultTop > 0 || this.mIsRoundedCornerMultipleRadius;
    }

    private boolean shouldShowRoundedCorner(int i) {
        boolean z = false;
        if (!hasRoundedCorners()) {
            return false;
        }
        DisplayCutout cutout = getCutout();
        boolean z2 = cutout == null || cutout.isBoundsEmpty();
        int boundPositionFromRotation = getBoundPositionFromRotation(1, this.mRotation);
        int boundPositionFromRotation2 = getBoundPositionFromRotation(3, this.mRotation);
        if (z2 || !cutout.getBoundingRectsAll()[boundPositionFromRotation].isEmpty() || !cutout.getBoundingRectsAll()[boundPositionFromRotation2].isEmpty()) {
            if (i == 1 || i == 3) {
                z = true;
            }
            return z;
        }
        if (i == 0 || i == 2) {
            z = true;
        }
        return z;
    }

    private boolean shouldDrawCutout() {
        return shouldDrawCutout(this.mContext);
    }

    static boolean shouldDrawCutout(Context context) {
        return context.getResources().getBoolean(17891458);
    }

    private void updateLayoutParams() {
        if (this.mOverlays != null) {
            for (int i = 0; i < 4; i++) {
                View[] viewArr = this.mOverlays;
                if (viewArr[i] != null) {
                    this.mWindowManager.updateViewLayout(viewArr[i], getWindowLayoutParams(i));
                }
            }
        }
    }

    public void onTuningChanged(String str, String str2) {
        this.mHandler.post(new Runnable(str, str2) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ String f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ScreenDecorations.this.lambda$onTuningChanged$3$ScreenDecorations(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onTuningChanged$3 */
    public /* synthetic */ void lambda$onTuningChanged$3$ScreenDecorations(String str, String str2) {
        if (this.mOverlays != null && "sysui_rounded_size".equals(str)) {
            int i = this.mRoundedDefault;
            int i2 = this.mRoundedDefaultTop;
            int i3 = this.mRoundedDefaultBottom;
            if (str2 != null) {
                try {
                    i = (int) (((float) Integer.parseInt(str2)) * this.mDensity);
                } catch (Exception unused) {
                }
            }
            if (i2 == 0) {
                i2 = i;
            }
            if (i3 == 0) {
                i3 = i;
            }
            for (int i4 = 0; i4 < 4; i4++) {
                View[] viewArr = this.mOverlays;
                if (viewArr[i4] != null) {
                    setSize(viewArr[i4].findViewById(C2011R$id.left), i2);
                    setSize(this.mOverlays[i4].findViewById(C2011R$id.right), i3);
                }
            }
        }
    }

    private void setSize(View view, int i) {
        LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = i;
        layoutParams.height = i;
        view.setLayoutParams(layoutParams);
    }
}
