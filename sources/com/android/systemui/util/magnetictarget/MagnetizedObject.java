package com.android.systemui.util.magnetictarget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings.System;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import com.android.systemui.util.animation.PhysicsAnimator;
import com.android.systemui.util.animation.PhysicsAnimator.SpringConfig;
import java.util.ArrayList;
import java.util.Iterator;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MagnetizedObject.kt */
public abstract class MagnetizedObject<T> {
    private final PhysicsAnimator<T> animator;
    private final ArrayList<MagneticTarget> associatedTargets = new ArrayList<>();
    private final Context context;
    private boolean flingToTargetEnabled;
    private float flingToTargetMinVelocity;
    private float flingToTargetWidthPercent;
    private float flingUnstuckFromTargetMinVelocity;
    private SpringConfig flungIntoTargetSpringConfig;
    private boolean hapticsEnabled;
    public MagnetListener magnetListener;
    private final int[] objectLocationOnScreen = new int[2];
    private SpringConfig springConfig;
    private float stickToTargetMaxVelocity;
    /* access modifiers changed from: private */
    public boolean systemHapticsEnabled;
    /* access modifiers changed from: private */
    public MagneticTarget targetObjectIsStuckTo;
    private final T underlyingObject;
    private final VelocityTracker velocityTracker;
    private final Vibrator vibrator;
    private final FloatPropertyCompat<? super T> xProperty;
    private final FloatPropertyCompat<? super T> yProperty;

    /* compiled from: MagnetizedObject.kt */
    public interface MagnetListener {
        void onReleasedInTarget(MagneticTarget magneticTarget);

        void onStuckToTarget(MagneticTarget magneticTarget);

        void onUnstuckFromTarget(MagneticTarget magneticTarget, float f, float f2, boolean z);
    }

    /* compiled from: MagnetizedObject.kt */
    public static final class MagneticTarget {
        private final PointF centerOnScreen = new PointF();
        private int magneticFieldRadiusPx;
        private final View targetView;
        private final int[] tempLoc = new int[2];

        public MagneticTarget(View view, int i) {
            Intrinsics.checkParameterIsNotNull(view, "targetView");
            this.targetView = view;
            this.magneticFieldRadiusPx = i;
        }

        /* renamed from: getTargetView$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final View mo19522x1bb92312() {
            return this.targetView;
        }

        public final int getMagneticFieldRadiusPx() {
            return this.magneticFieldRadiusPx;
        }

        /* renamed from: getCenterOnScreen$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final PointF mo19520xe80aa5dc() {
            return this.centerOnScreen;
        }

        public final void updateLocationOnScreen() {
            this.targetView.getLocationOnScreen(this.tempLoc);
            this.centerOnScreen.set((((float) this.tempLoc[0]) + (((float) this.targetView.getWidth()) / 2.0f)) - this.targetView.getTranslationX(), (((float) this.tempLoc[1]) + (((float) this.targetView.getHeight()) / 2.0f)) - this.targetView.getTranslationY());
        }
    }

    public abstract float getHeight(T t);

    public abstract void getLocationOnScreen(T t, int[] iArr);

    public abstract float getWidth(T t);

    public MagnetizedObject(Context context2, T t, FloatPropertyCompat<? super T> floatPropertyCompat, FloatPropertyCompat<? super T> floatPropertyCompat2) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(t, "underlyingObject");
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "xProperty");
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat2, "yProperty");
        this.context = context2;
        this.underlyingObject = t;
        this.xProperty = floatPropertyCompat;
        this.yProperty = floatPropertyCompat2;
        this.animator = PhysicsAnimator.Companion.getInstance(t);
        VelocityTracker obtain = VelocityTracker.obtain();
        Intrinsics.checkExpressionValueIsNotNull(obtain, "VelocityTracker.obtain()");
        this.velocityTracker = obtain;
        Object systemService = this.context.getSystemService("vibrator");
        if (systemService != null) {
            this.vibrator = (Vibrator) systemService;
            this.flingToTargetEnabled = true;
            this.flingToTargetWidthPercent = 3.0f;
            this.flingToTargetMinVelocity = 4000.0f;
            this.flingUnstuckFromTargetMinVelocity = 1000.0f;
            this.stickToTargetMaxVelocity = 2000.0f;
            this.hapticsEnabled = true;
            SpringConfig springConfig2 = new SpringConfig(1500.0f, 1.0f);
            this.springConfig = springConfig2;
            this.flungIntoTargetSpringConfig = springConfig2;
            MagnetizedObject$hapticSettingObserver$1 magnetizedObject$hapticSettingObserver$1 = new MagnetizedObject$hapticSettingObserver$1(this, Handler.getMain());
            this.context.getContentResolver().registerContentObserver(System.getUriFor("haptic_feedback_enabled"), true, magnetizedObject$hapticSettingObserver$1);
            magnetizedObject$hapticSettingObserver$1.onChange(false);
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.os.Vibrator");
    }

    public final Context getContext() {
        return this.context;
    }

    public final T getUnderlyingObject() {
        return this.underlyingObject;
    }

    public final boolean getObjectStuckToTarget() {
        return this.targetObjectIsStuckTo != null;
    }

    public final MagnetListener getMagnetListener() {
        MagnetListener magnetListener2 = this.magnetListener;
        if (magnetListener2 != null) {
            return magnetListener2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("magnetListener");
        throw null;
    }

    public final void setMagnetListener(MagnetListener magnetListener2) {
        Intrinsics.checkParameterIsNotNull(magnetListener2, "<set-?>");
        this.magnetListener = magnetListener2;
    }

    public final void setFlingToTargetMinVelocity(float f) {
        this.flingToTargetMinVelocity = f;
    }

    public final void setHapticsEnabled(boolean z) {
        this.hapticsEnabled = z;
    }

    public final void addTarget(MagneticTarget magneticTarget) {
        Intrinsics.checkParameterIsNotNull(magneticTarget, "target");
        this.associatedTargets.add(magneticTarget);
        magneticTarget.updateLocationOnScreen();
    }

    public final boolean maybeConsumeMotionEvent(MotionEvent motionEvent) {
        Object obj;
        Object obj2;
        boolean z;
        Intrinsics.checkParameterIsNotNull(motionEvent, "ev");
        if (this.associatedTargets.size() == 0) {
            return false;
        }
        if (motionEvent.getAction() == 0) {
            mo19519x2ef79a65();
            this.velocityTracker.clear();
            this.targetObjectIsStuckTo = null;
        }
        addMovement(motionEvent);
        Iterator it = this.associatedTargets.iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            MagneticTarget magneticTarget = (MagneticTarget) obj;
            if (((float) Math.hypot((double) (motionEvent.getRawX() - magneticTarget.mo19520xe80aa5dc().x), (double) (motionEvent.getRawY() - magneticTarget.mo19520xe80aa5dc().y))) < ((float) magneticTarget.getMagneticFieldRadiusPx())) {
                z = true;
                continue;
            } else {
                z = false;
                continue;
            }
            if (z) {
                break;
            }
        }
        MagneticTarget magneticTarget2 = (MagneticTarget) obj;
        boolean z2 = !getObjectStuckToTarget() && magneticTarget2 != null;
        boolean z3 = getObjectStuckToTarget() && magneticTarget2 != null && (Intrinsics.areEqual((Object) this.targetObjectIsStuckTo, (Object) magneticTarget2) ^ true);
        String str = "magnetListener";
        if (z2 || z3) {
            this.velocityTracker.computeCurrentVelocity(1000);
            float xVelocity = this.velocityTracker.getXVelocity();
            float yVelocity = this.velocityTracker.getYVelocity();
            if (z2 && ((float) Math.hypot((double) xVelocity, (double) yVelocity)) > this.stickToTargetMaxVelocity) {
                return false;
            }
            this.targetObjectIsStuckTo = magneticTarget2;
            mo19510xa089fc4f();
            MagnetListener magnetListener2 = this.magnetListener;
            if (magnetListener2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException(str);
                throw null;
            } else if (magneticTarget2 != null) {
                magnetListener2.onStuckToTarget(magneticTarget2);
                animateStuckToTarget$default(this, magneticTarget2, xVelocity, yVelocity, false, null, 16, null);
                vibrateIfEnabled(5);
            } else {
                Intrinsics.throwNpe();
                throw null;
            }
        } else if (magneticTarget2 == null && getObjectStuckToTarget()) {
            this.velocityTracker.computeCurrentVelocity(1000);
            mo19510xa089fc4f();
            MagnetListener magnetListener3 = this.magnetListener;
            if (magnetListener3 != null) {
                MagneticTarget magneticTarget3 = this.targetObjectIsStuckTo;
                if (magneticTarget3 != null) {
                    magnetListener3.onUnstuckFromTarget(magneticTarget3, this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity(), false);
                    this.targetObjectIsStuckTo = null;
                    vibrateIfEnabled(2);
                } else {
                    Intrinsics.throwNpe();
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException(str);
                throw null;
            }
        }
        if (motionEvent.getAction() != 1) {
            return getObjectStuckToTarget();
        }
        this.velocityTracker.computeCurrentVelocity(1000);
        float xVelocity2 = this.velocityTracker.getXVelocity();
        float yVelocity2 = this.velocityTracker.getYVelocity();
        mo19510xa089fc4f();
        if (getObjectStuckToTarget()) {
            if (((float) Math.hypot((double) xVelocity2, (double) yVelocity2)) > this.flingUnstuckFromTargetMinVelocity) {
                MagnetListener magnetListener4 = this.magnetListener;
                if (magnetListener4 != null) {
                    MagneticTarget magneticTarget4 = this.targetObjectIsStuckTo;
                    if (magneticTarget4 != null) {
                        magnetListener4.onUnstuckFromTarget(magneticTarget4, xVelocity2, yVelocity2, true);
                    } else {
                        Intrinsics.throwNpe();
                        throw null;
                    }
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                }
            } else {
                MagnetListener magnetListener5 = this.magnetListener;
                if (magnetListener5 != null) {
                    MagneticTarget magneticTarget5 = this.targetObjectIsStuckTo;
                    if (magneticTarget5 != null) {
                        magnetListener5.onReleasedInTarget(magneticTarget5);
                        vibrateIfEnabled(5);
                    } else {
                        Intrinsics.throwNpe();
                        throw null;
                    }
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                }
            }
            this.targetObjectIsStuckTo = null;
            return true;
        }
        Iterator it2 = this.associatedTargets.iterator();
        while (true) {
            if (!it2.hasNext()) {
                obj2 = null;
                break;
            }
            obj2 = it2.next();
            if (isForcefulFlingTowardsTarget((MagneticTarget) obj2, motionEvent.getRawX(), motionEvent.getRawY(), xVelocity2, yVelocity2)) {
                break;
            }
        }
        MagneticTarget magneticTarget6 = (MagneticTarget) obj2;
        if (magneticTarget6 == null) {
            return false;
        }
        MagnetListener magnetListener6 = this.magnetListener;
        if (magnetListener6 != null) {
            magnetListener6.onStuckToTarget(magneticTarget6);
            this.targetObjectIsStuckTo = magneticTarget6;
            animateStuckToTarget(magneticTarget6, xVelocity2, yVelocity2, true, new MagnetizedObject$maybeConsumeMotionEvent$1(this, magneticTarget6));
            return true;
        }
        Intrinsics.throwUninitializedPropertyAccessException(str);
        throw null;
    }

    /* access modifiers changed from: private */
    @SuppressLint({"MissingPermission"})
    public final void vibrateIfEnabled(int i) {
        if (this.hapticsEnabled && this.systemHapticsEnabled) {
            this.vibrator.vibrate((long) i);
        }
    }

    private final void addMovement(MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX() - motionEvent.getX();
        float rawY = motionEvent.getRawY() - motionEvent.getY();
        motionEvent.offsetLocation(rawX, rawY);
        this.velocityTracker.addMovement(motionEvent);
        motionEvent.offsetLocation(-rawX, -rawY);
    }

    static /* synthetic */ void animateStuckToTarget$default(MagnetizedObject magnetizedObject, MagneticTarget magneticTarget, float f, float f2, boolean z, Function0 function0, int i, Object obj) {
        if (obj == null) {
            if ((i & 16) != 0) {
                function0 = null;
            }
            magnetizedObject.animateStuckToTarget(magneticTarget, f, f2, z, function0);
            return;
        }
        throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: animateStuckToTarget");
    }

    private final void animateStuckToTarget(MagneticTarget magneticTarget, float f, float f2, boolean z, Function0<Unit> function0) {
        magneticTarget.updateLocationOnScreen();
        getLocationOnScreen(this.underlyingObject, this.objectLocationOnScreen);
        float width = (magneticTarget.mo19520xe80aa5dc().x - (getWidth(this.underlyingObject) / 2.0f)) - ((float) this.objectLocationOnScreen[0]);
        float height = (magneticTarget.mo19520xe80aa5dc().y - (getHeight(this.underlyingObject) / 2.0f)) - ((float) this.objectLocationOnScreen[1]);
        SpringConfig springConfig2 = z ? this.flungIntoTargetSpringConfig : this.springConfig;
        mo19510xa089fc4f();
        PhysicsAnimator<T> physicsAnimator = this.animator;
        FloatPropertyCompat<? super T> floatPropertyCompat = this.xProperty;
        physicsAnimator.spring(floatPropertyCompat, floatPropertyCompat.getValue(this.underlyingObject) + width, f, springConfig2);
        FloatPropertyCompat<? super T> floatPropertyCompat2 = this.yProperty;
        physicsAnimator.spring(floatPropertyCompat2, floatPropertyCompat2.getValue(this.underlyingObject) + height, f2, springConfig2);
        if (function0 != null) {
            this.animator.withEndActions((Function0<Unit>[]) new Function0[]{function0});
        }
        this.animator.start();
    }

    private final boolean isForcefulFlingTowardsTarget(MagneticTarget magneticTarget, float f, float f2, float f3, float f4) {
        boolean z = false;
        if (!this.flingToTargetEnabled) {
            return false;
        }
        if (!(f2 >= magneticTarget.mo19520xe80aa5dc().y ? f4 < this.flingToTargetMinVelocity : f4 > this.flingToTargetMinVelocity)) {
            return false;
        }
        if (f3 != 0.0f) {
            float f5 = f4 / f3;
            f = (magneticTarget.mo19520xe80aa5dc().y - (f2 - (f * f5))) / f5;
        }
        float width = (((float) magneticTarget.mo19522x1bb92312().getWidth()) * this.flingToTargetWidthPercent) / ((float) 2);
        if (f > magneticTarget.mo19520xe80aa5dc().x - width && f < magneticTarget.mo19520xe80aa5dc().x + width) {
            z = true;
        }
        return z;
    }

    /* renamed from: cancelAnimations$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo19510xa089fc4f() {
        this.animator.cancel(this.xProperty, this.yProperty);
    }

    /* renamed from: updateTargetViewLocations$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo19519x2ef79a65() {
        for (MagneticTarget updateLocationOnScreen : this.associatedTargets) {
            updateLocationOnScreen.updateLocationOnScreen();
        }
    }
}
