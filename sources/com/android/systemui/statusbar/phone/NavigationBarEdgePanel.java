package com.android.systemui.statusbar.phone;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.MathUtils;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.settingslib.Utils;
import com.android.systemui.C2006R$attr;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.plugins.NavigationEdgeBackPlugin;
import com.android.systemui.plugins.NavigationEdgeBackPlugin.BackCallback;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.phone.RegionSamplingHelper.SamplingCallback;

public class NavigationBarEdgePanel extends View implements NavigationEdgeBackPlugin {
    private static final FloatPropertyCompat<NavigationBarEdgePanel> CURRENT_ANGLE = new FloatPropertyCompat<NavigationBarEdgePanel>("currentAngle") {
        public void setValue(NavigationBarEdgePanel navigationBarEdgePanel, float f) {
            navigationBarEdgePanel.setCurrentAngle(f);
        }

        public float getValue(NavigationBarEdgePanel navigationBarEdgePanel) {
            return navigationBarEdgePanel.getCurrentAngle();
        }
    };
    private static final FloatPropertyCompat<NavigationBarEdgePanel> CURRENT_TRANSLATION = new FloatPropertyCompat<NavigationBarEdgePanel>("currentTranslation") {
        public void setValue(NavigationBarEdgePanel navigationBarEdgePanel, float f) {
            navigationBarEdgePanel.setCurrentTranslation(f);
        }

        public float getValue(NavigationBarEdgePanel navigationBarEdgePanel) {
            return navigationBarEdgePanel.getCurrentTranslation();
        }
    };
    private static final FloatPropertyCompat<NavigationBarEdgePanel> CURRENT_VERTICAL_TRANSLATION = new FloatPropertyCompat<NavigationBarEdgePanel>("verticalTranslation") {
        public void setValue(NavigationBarEdgePanel navigationBarEdgePanel, float f) {
            navigationBarEdgePanel.setVerticalTranslation(f);
        }

        public float getValue(NavigationBarEdgePanel navigationBarEdgePanel) {
            return navigationBarEdgePanel.getVerticalTranslation();
        }
    };
    private static final Interpolator RUBBER_BAND_INTERPOLATOR = new PathInterpolator(0.2f, 1.0f, 1.0f, 1.0f);
    private static final Interpolator RUBBER_BAND_INTERPOLATOR_APPEAR = new PathInterpolator(0.25f, 1.0f, 1.0f, 1.0f);
    private final SpringAnimation mAngleAnimation;
    private final SpringForce mAngleAppearForce;
    private final SpringForce mAngleDisappearForce;
    private float mAngleOffset;
    private int mArrowColor;
    private final ValueAnimator mArrowColorAnimator;
    private int mArrowColorDark;
    private int mArrowColorLight;
    private final ValueAnimator mArrowDisappearAnimation;
    private final float mArrowLength;
    private int mArrowPaddingEnd;
    private final Path mArrowPath = new Path();
    private int mArrowStartColor;
    private final float mArrowThickness;
    private boolean mArrowsPointLeft;
    private BackCallback mBackCallback;
    private final float mBaseTranslation;
    private float mCurrentAngle;
    private int mCurrentArrowColor;
    private float mCurrentTranslation;
    private final float mDensity;
    private float mDesiredAngle;
    private float mDesiredTranslation;
    private float mDesiredVerticalTranslation;
    private float mDisappearAmount;
    private final Point mDisplaySize = new Point();
    private boolean mDragSlopPassed;
    private int mFingerOffset;
    private boolean mIsDark = false;
    private boolean mIsLeftPanel;
    private LayoutParams mLayoutParams;
    private int mLeftInset;
    private float mMaxTranslation;
    private int mMinArrowPosition;
    private final float mMinDeltaForSwitch;
    private final Paint mPaint = new Paint();
    private float mPreviousTouchTranslation;
    private int mProtectionColor;
    private int mProtectionColorDark;
    private int mProtectionColorLight;
    private final Paint mProtectionPaint;
    private RegionSamplingHelper mRegionSamplingHelper;
    private final SpringForce mRegularTranslationSpring;
    private int mRightInset;
    /* access modifiers changed from: private */
    public final Rect mSamplingRect = new Rect();
    private int mScreenSize;
    private OnAnimationEndListener mSetGoneEndListener = new OnAnimationEndListener() {
        public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            dynamicAnimation.removeEndListener(this);
            if (!z) {
                NavigationBarEdgePanel.this.setVisibility(8);
            }
        }
    };
    private boolean mShowProtection = false;
    private float mStartX;
    private float mStartY;
    private final float mSwipeThreshold;
    private float mTotalTouchDelta;
    private final SpringAnimation mTranslationAnimation;
    private boolean mTriggerBack;
    private final SpringForce mTriggerBackSpring;
    private VelocityTracker mVelocityTracker;
    private float mVerticalTranslation;
    private final SpringAnimation mVerticalTranslationAnimation;
    private long mVibrationTime;
    private final VibratorHelper mVibratorHelper;
    private final WindowManager mWindowManager;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public NavigationBarEdgePanel(Context context) {
        super(context);
        this.mWindowManager = (WindowManager) context.getSystemService(WindowManager.class);
        this.mVibratorHelper = (VibratorHelper) Dependency.get(VibratorHelper.class);
        this.mDensity = context.getResources().getDisplayMetrics().density;
        this.mBaseTranslation = m67dp(32.0f);
        this.mArrowLength = m67dp(18.0f);
        this.mArrowThickness = m67dp(2.5f);
        this.mMinDeltaForSwitch = m67dp(32.0f);
        this.mPaint.setStrokeWidth(this.mArrowThickness);
        this.mPaint.setStrokeCap(Cap.ROUND);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeJoin(Join.ROUND);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mArrowColorAnimator = ofFloat;
        ofFloat.setDuration(120);
        this.mArrowColorAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                NavigationBarEdgePanel.this.lambda$new$0$NavigationBarEdgePanel(valueAnimator);
            }
        });
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mArrowDisappearAnimation = ofFloat2;
        ofFloat2.setDuration(100);
        this.mArrowDisappearAnimation.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        this.mArrowDisappearAnimation.addUpdateListener(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                NavigationBarEdgePanel.this.lambda$new$1$NavigationBarEdgePanel(valueAnimator);
            }
        });
        this.mAngleAnimation = new SpringAnimation(this, CURRENT_ANGLE);
        SpringForce springForce = new SpringForce();
        springForce.setStiffness(500.0f);
        springForce.setDampingRatio(0.5f);
        this.mAngleAppearForce = springForce;
        SpringForce springForce2 = new SpringForce();
        springForce2.setStiffness(1500.0f);
        springForce2.setDampingRatio(0.5f);
        springForce2.setFinalPosition(90.0f);
        this.mAngleDisappearForce = springForce2;
        SpringAnimation springAnimation = this.mAngleAnimation;
        springAnimation.setSpring(this.mAngleAppearForce);
        springAnimation.setMaxValue(90.0f);
        this.mTranslationAnimation = new SpringAnimation(this, CURRENT_TRANSLATION);
        SpringForce springForce3 = new SpringForce();
        springForce3.setStiffness(1500.0f);
        springForce3.setDampingRatio(0.75f);
        this.mRegularTranslationSpring = springForce3;
        SpringForce springForce4 = new SpringForce();
        springForce4.setStiffness(450.0f);
        springForce4.setDampingRatio(0.75f);
        this.mTriggerBackSpring = springForce4;
        this.mTranslationAnimation.setSpring(this.mRegularTranslationSpring);
        SpringAnimation springAnimation2 = new SpringAnimation(this, CURRENT_VERTICAL_TRANSLATION);
        this.mVerticalTranslationAnimation = springAnimation2;
        SpringForce springForce5 = new SpringForce();
        springForce5.setStiffness(1500.0f);
        springForce5.setDampingRatio(0.75f);
        springAnimation2.setSpring(springForce5);
        Paint paint = new Paint(this.mPaint);
        this.mProtectionPaint = paint;
        paint.setStrokeWidth(this.mArrowThickness + 2.0f);
        loadDimens();
        loadColors(context);
        updateArrowDirection();
        this.mSwipeThreshold = context.getResources().getDimension(C2009R$dimen.navigation_edge_action_drag_threshold);
        setVisibility(8);
        RegionSamplingHelper regionSamplingHelper = new RegionSamplingHelper(this, new SamplingCallback() {
            public void onRegionDarknessChanged(boolean z) {
                NavigationBarEdgePanel.this.setIsDark(!z, true);
            }

            public Rect getSampledRegion(View view) {
                return NavigationBarEdgePanel.this.mSamplingRect;
            }
        });
        this.mRegionSamplingHelper = regionSamplingHelper;
        regionSamplingHelper.setWindowVisible(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$NavigationBarEdgePanel(ValueAnimator valueAnimator) {
        setCurrentArrowColor(ColorUtils.blendARGB(this.mArrowStartColor, this.mArrowColor, valueAnimator.getAnimatedFraction()));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$NavigationBarEdgePanel(ValueAnimator valueAnimator) {
        this.mDisappearAmount = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void onDestroy() {
        this.mWindowManager.removeView(this);
        this.mRegionSamplingHelper.stop();
        this.mRegionSamplingHelper = null;
    }

    /* access modifiers changed from: private */
    public void setIsDark(boolean z, boolean z2) {
        this.mIsDark = z;
        updateIsDark(z2);
    }

    public void setIsLeftPanel(boolean z) {
        this.mIsLeftPanel = z;
        this.mLayoutParams.gravity = z ? 51 : 53;
    }

    public void setInsets(int i, int i2) {
        this.mLeftInset = i;
        this.mRightInset = i2;
    }

    public void setDisplaySize(Point point) {
        this.mDisplaySize.set(point.x, point.y);
        Point point2 = this.mDisplaySize;
        this.mScreenSize = Math.min(point2.x, point2.y);
    }

    public void setBackCallback(BackCallback backCallback) {
        this.mBackCallback = backCallback;
    }

    public void setLayoutParams(LayoutParams layoutParams) {
        this.mLayoutParams = layoutParams;
        this.mWindowManager.addView(this, layoutParams);
    }

    private void adjustSamplingRectToBoundingBox() {
        float f = this.mDesiredTranslation;
        if (!this.mTriggerBack) {
            f = this.mBaseTranslation;
            if ((this.mIsLeftPanel && this.mArrowsPointLeft) || (!this.mIsLeftPanel && !this.mArrowsPointLeft)) {
                f -= getStaticArrowWidth();
            }
        }
        float f2 = f - (this.mArrowThickness / 2.0f);
        if (!this.mIsLeftPanel) {
            f2 = ((float) this.mSamplingRect.width()) - f2;
        }
        float staticArrowWidth = getStaticArrowWidth();
        float polarToCartY = polarToCartY(56.0f) * this.mArrowLength * 2.0f;
        if (!this.mArrowsPointLeft) {
            f2 -= staticArrowWidth;
        }
        this.mSamplingRect.offset((int) f2, (int) (((((float) getHeight()) * 0.5f) + this.mDesiredVerticalTranslation) - (polarToCartY / 2.0f)));
        Rect rect = this.mSamplingRect;
        int i = rect.left;
        int i2 = rect.top;
        rect.set(i, i2, (int) (((float) i) + staticArrowWidth), (int) (((float) i2) + polarToCartY));
        this.mRegionSamplingHelper.updateSamplingRect();
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mDragSlopPassed = false;
            resetOnDown();
            this.mStartX = motionEvent.getX();
            this.mStartY = motionEvent.getY();
            setVisibility(0);
            updatePosition(motionEvent.getY());
            this.mRegionSamplingHelper.start(this.mSamplingRect);
            this.mWindowManager.updateViewLayout(this, this.mLayoutParams);
        } else if (actionMasked == 1) {
            if (this.mTriggerBack) {
                triggerBack();
            } else {
                cancelBack();
            }
            this.mRegionSamplingHelper.stop();
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        } else if (actionMasked == 2) {
            handleMoveEvent(motionEvent);
        } else if (actionMasked == 3) {
            cancelBack();
            this.mRegionSamplingHelper.stop();
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateArrowDirection();
        loadDimens();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f = this.mCurrentTranslation - (this.mArrowThickness / 2.0f);
        canvas.save();
        if (!this.mIsLeftPanel) {
            f = ((float) getWidth()) - f;
        }
        canvas.translate(f, (((float) getHeight()) * 0.5f) + this.mVerticalTranslation);
        Path calculatePath = calculatePath(polarToCartX(this.mCurrentAngle) * this.mArrowLength, polarToCartY(this.mCurrentAngle) * this.mArrowLength);
        if (this.mShowProtection) {
            canvas.drawPath(calculatePath, this.mProtectionPaint);
        }
        canvas.drawPath(calculatePath, this.mPaint);
        canvas.restore();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mMaxTranslation = (float) (getWidth() - this.mArrowPaddingEnd);
    }

    private void loadDimens() {
        Resources resources = getResources();
        this.mArrowPaddingEnd = resources.getDimensionPixelSize(C2009R$dimen.navigation_edge_panel_padding);
        this.mMinArrowPosition = resources.getDimensionPixelSize(C2009R$dimen.navigation_edge_arrow_min_y);
        this.mFingerOffset = resources.getDimensionPixelSize(C2009R$dimen.navigation_edge_finger_offset);
    }

    private void updateArrowDirection() {
        this.mArrowsPointLeft = getLayoutDirection() == 0;
        invalidate();
    }

    private void loadColors(Context context) {
        int themeAttr = Utils.getThemeAttr(context, C2006R$attr.darkIconTheme);
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, Utils.getThemeAttr(context, C2006R$attr.lightIconTheme));
        ContextThemeWrapper contextThemeWrapper2 = new ContextThemeWrapper(context, themeAttr);
        this.mArrowColorLight = Utils.getColorAttrDefaultColor(contextThemeWrapper, C2006R$attr.singleToneColor);
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(contextThemeWrapper2, C2006R$attr.singleToneColor);
        this.mArrowColorDark = colorAttrDefaultColor;
        this.mProtectionColorDark = this.mArrowColorLight;
        this.mProtectionColorLight = colorAttrDefaultColor;
        updateIsDark(false);
    }

    private void updateIsDark(boolean z) {
        int i = this.mIsDark ? this.mProtectionColorDark : this.mProtectionColorLight;
        this.mProtectionColor = i;
        this.mProtectionPaint.setColor(i);
        this.mArrowColor = this.mIsDark ? this.mArrowColorDark : this.mArrowColorLight;
        this.mArrowColorAnimator.cancel();
        if (!z) {
            setCurrentArrowColor(this.mArrowColor);
            return;
        }
        this.mArrowStartColor = this.mCurrentArrowColor;
        this.mArrowColorAnimator.start();
    }

    private void setCurrentArrowColor(int i) {
        this.mCurrentArrowColor = i;
        this.mPaint.setColor(i);
        invalidate();
    }

    private float getStaticArrowWidth() {
        return polarToCartX(56.0f) * this.mArrowLength;
    }

    private float polarToCartX(float f) {
        return (float) Math.cos(Math.toRadians((double) f));
    }

    private float polarToCartY(float f) {
        return (float) Math.sin(Math.toRadians((double) f));
    }

    private Path calculatePath(float f, float f2) {
        if (!this.mArrowsPointLeft) {
            f = -f;
        }
        float lerp = MathUtils.lerp(1.0f, 0.75f, this.mDisappearAmount);
        float f3 = f * lerp;
        float f4 = f2 * lerp;
        this.mArrowPath.reset();
        this.mArrowPath.moveTo(f3, f4);
        this.mArrowPath.lineTo(0.0f, 0.0f);
        this.mArrowPath.lineTo(f3, -f4);
        return this.mArrowPath;
    }

    /* access modifiers changed from: private */
    public float getCurrentAngle() {
        return this.mCurrentAngle;
    }

    /* access modifiers changed from: private */
    public float getCurrentTranslation() {
        return this.mCurrentTranslation;
    }

    private void triggerBack() {
        this.mBackCallback.triggerBack();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.computeCurrentVelocity(1000);
        if ((Math.abs(this.mVelocityTracker.getXVelocity()) < 500.0f) || SystemClock.uptimeMillis() - this.mVibrationTime >= 400) {
            this.mVibratorHelper.vibrate(0);
        }
        float f = this.mAngleOffset;
        if (f > -4.0f) {
            this.mAngleOffset = Math.max(-8.0f, f - 8.0f);
            updateAngle(true);
        }
        final $$Lambda$NavigationBarEdgePanel$8nVYqEm2UMLvGUzkKr5IU4GQaTc r0 = new Runnable() {
            public final void run() {
                NavigationBarEdgePanel.this.lambda$triggerBack$3$NavigationBarEdgePanel();
            }
        };
        if (this.mTranslationAnimation.isRunning()) {
            this.mTranslationAnimation.addEndListener(new OnAnimationEndListener(this) {
                public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    dynamicAnimation.removeEndListener(this);
                    if (!z) {
                        r0.run();
                    }
                }
            });
        } else {
            r0.run();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$triggerBack$3 */
    public /* synthetic */ void lambda$triggerBack$3$NavigationBarEdgePanel() {
        this.mAngleOffset = Math.max(0.0f, this.mAngleOffset + 8.0f);
        updateAngle(true);
        this.mTranslationAnimation.setSpring(this.mTriggerBackSpring);
        setDesiredTranslation(this.mDesiredTranslation - m67dp(32.0f), true);
        animate().alpha(0.0f).setDuration(80).withEndAction(new Runnable() {
            public final void run() {
                NavigationBarEdgePanel.this.lambda$triggerBack$2$NavigationBarEdgePanel();
            }
        });
        this.mArrowDisappearAnimation.start();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$triggerBack$2 */
    public /* synthetic */ void lambda$triggerBack$2$NavigationBarEdgePanel() {
        setVisibility(8);
    }

    private void cancelBack() {
        this.mBackCallback.cancelBack();
        if (this.mTranslationAnimation.isRunning()) {
            this.mTranslationAnimation.addEndListener(this.mSetGoneEndListener);
        } else {
            setVisibility(8);
        }
    }

    private void resetOnDown() {
        animate().cancel();
        this.mAngleAnimation.cancel();
        this.mTranslationAnimation.cancel();
        this.mVerticalTranslationAnimation.cancel();
        this.mArrowDisappearAnimation.cancel();
        this.mAngleOffset = 0.0f;
        this.mTranslationAnimation.setSpring(this.mRegularTranslationSpring);
        setTriggerBack(false, false);
        setDesiredTranslation(0.0f, false);
        setCurrentTranslation(0.0f);
        updateAngle(false);
        this.mPreviousTouchTranslation = 0.0f;
        this.mTotalTouchDelta = 0.0f;
        this.mVibrationTime = 0;
        setDesiredVerticalTransition(0.0f, false);
    }

    private void handleMoveEvent(MotionEvent motionEvent) {
        float f;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float abs = MathUtils.abs(x - this.mStartX);
        float f2 = y - this.mStartY;
        float f3 = abs - this.mPreviousTouchTranslation;
        if (Math.abs(f3) > 0.0f) {
            if (Math.signum(f3) == Math.signum(this.mTotalTouchDelta)) {
                this.mTotalTouchDelta += f3;
            } else {
                this.mTotalTouchDelta = f3;
            }
        }
        this.mPreviousTouchTranslation = abs;
        if (!this.mDragSlopPassed && abs > this.mSwipeThreshold) {
            this.mDragSlopPassed = true;
            this.mVibratorHelper.vibrate(2);
            this.mVibrationTime = SystemClock.uptimeMillis();
            this.mDisappearAmount = 0.0f;
            setAlpha(1.0f);
            setTriggerBack(true, true);
        }
        float f4 = this.mBaseTranslation;
        if (abs > f4) {
            float interpolation = RUBBER_BAND_INTERPOLATOR.getInterpolation(MathUtils.saturate((abs - f4) / (((float) this.mScreenSize) - f4)));
            float f5 = this.mMaxTranslation;
            float f6 = this.mBaseTranslation;
            f = f6 + (interpolation * (f5 - f6));
        } else {
            float interpolation2 = RUBBER_BAND_INTERPOLATOR_APPEAR.getInterpolation(MathUtils.saturate((f4 - abs) / f4));
            float f7 = this.mBaseTranslation;
            f = f7 - (interpolation2 * (f7 / 4.0f));
        }
        boolean z = this.mTriggerBack;
        boolean z2 = false;
        if (Math.abs(this.mTotalTouchDelta) > this.mMinDeltaForSwitch) {
            z = this.mTotalTouchDelta > 0.0f;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = this.mVelocityTracker.getXVelocity();
        this.mAngleOffset = Math.min((MathUtils.mag(xVelocity, this.mVelocityTracker.getYVelocity()) / 1000.0f) * 4.0f, 4.0f) * Math.signum(xVelocity);
        if ((this.mIsLeftPanel && this.mArrowsPointLeft) || (!this.mIsLeftPanel && !this.mArrowsPointLeft)) {
            this.mAngleOffset *= -1.0f;
        }
        if (Math.abs(f2) <= Math.abs(x - this.mStartX) * 2.0f) {
            z2 = z;
        }
        setTriggerBack(z2, true);
        if (!this.mTriggerBack) {
            f = 0.0f;
        } else if ((this.mIsLeftPanel && this.mArrowsPointLeft) || (!this.mIsLeftPanel && !this.mArrowsPointLeft)) {
            f -= getStaticArrowWidth();
        }
        setDesiredTranslation(f, true);
        updateAngle(true);
        float height = (((float) getHeight()) / 2.0f) - this.mArrowLength;
        setDesiredVerticalTransition(RUBBER_BAND_INTERPOLATOR.getInterpolation(MathUtils.constrain(Math.abs(f2) / (15.0f * height), 0.0f, 1.0f)) * height * Math.signum(f2), true);
        updateSamplingRect();
    }

    private void updatePosition(float f) {
        float max = Math.max(f - ((float) this.mFingerOffset), (float) this.mMinArrowPosition);
        LayoutParams layoutParams = this.mLayoutParams;
        layoutParams.y = MathUtils.constrain((int) (max - (((float) layoutParams.height) / 2.0f)), 0, this.mDisplaySize.y);
        updateSamplingRect();
    }

    private void updateSamplingRect() {
        LayoutParams layoutParams = this.mLayoutParams;
        int i = layoutParams.y;
        int i2 = this.mIsLeftPanel ? this.mLeftInset : (this.mDisplaySize.x - this.mRightInset) - layoutParams.width;
        LayoutParams layoutParams2 = this.mLayoutParams;
        this.mSamplingRect.set(i2, i, layoutParams2.width + i2, layoutParams2.height + i);
        adjustSamplingRectToBoundingBox();
    }

    private void setDesiredVerticalTransition(float f, boolean z) {
        if (this.mDesiredVerticalTranslation != f) {
            this.mDesiredVerticalTranslation = f;
            if (!z) {
                setVerticalTranslation(f);
            } else {
                this.mVerticalTranslationAnimation.animateToFinalPosition(f);
            }
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void setVerticalTranslation(float f) {
        this.mVerticalTranslation = f;
        invalidate();
    }

    /* access modifiers changed from: private */
    public float getVerticalTranslation() {
        return this.mVerticalTranslation;
    }

    private void setDesiredTranslation(float f, boolean z) {
        if (this.mDesiredTranslation != f) {
            this.mDesiredTranslation = f;
            if (!z) {
                setCurrentTranslation(f);
            } else {
                this.mTranslationAnimation.animateToFinalPosition(f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentTranslation(float f) {
        this.mCurrentTranslation = f;
        invalidate();
    }

    private void setTriggerBack(boolean z, boolean z2) {
        if (this.mTriggerBack != z) {
            this.mTriggerBack = z;
            this.mAngleAnimation.cancel();
            updateAngle(z2);
            this.mTranslationAnimation.cancel();
        }
    }

    private void updateAngle(boolean z) {
        float f = this.mTriggerBack ? this.mAngleOffset + 56.0f : 90.0f;
        if (f != this.mDesiredAngle) {
            if (!z) {
                setCurrentAngle(f);
            } else {
                this.mAngleAnimation.setSpring(this.mTriggerBack ? this.mAngleAppearForce : this.mAngleDisappearForce);
                this.mAngleAnimation.animateToFinalPosition(f);
            }
            this.mDesiredAngle = f;
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentAngle(float f) {
        this.mCurrentAngle = f;
        invalidate();
    }

    /* renamed from: dp */
    private float m67dp(float f) {
        return this.mDensity * f;
    }
}
