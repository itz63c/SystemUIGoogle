package com.android.systemui.util.animation;

import android.os.Looper;
import android.util.ArrayMap;
import android.util.Log;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FloatCompanionObject;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PhysicsAnimator.kt */
public final class PhysicsAnimator<T> {
    public static final Companion Companion = new Companion(null);
    /* access modifiers changed from: private */
    public static Function1<Object, ? extends PhysicsAnimator<?>> instanceConstructor = PhysicsAnimator$Companion$instanceConstructor$1.INSTANCE;
    private Function1<? super Set<? extends FloatPropertyCompat<? super T>>, Unit> cancelAction;
    private final ArrayList<Function0<Unit>> endActions;
    private final ArrayList<EndListener<T>> endListeners;
    private final ArrayMap<FloatPropertyCompat<? super T>, FlingAnimation> flingAnimations;
    private final ArrayMap<FloatPropertyCompat<? super T>, FlingConfig> flingConfigs;
    private ArrayList<InternalListener> internalListeners;
    private final ArrayMap<FloatPropertyCompat<? super T>, SpringAnimation> springAnimations;
    private final ArrayMap<FloatPropertyCompat<? super T>, SpringConfig> springConfigs;
    private Function0<Unit> startAction;
    private final T target;
    private final ArrayList<UpdateListener<T>> updateListeners;

    /* compiled from: PhysicsAnimator.kt */
    public static final class AnimationUpdate {
        private final float value;
        private final float velocity;

        /* JADX WARNING: Code restructure failed: missing block: B:6:0x001a, code lost:
            if (java.lang.Float.compare(r2.velocity, r3.velocity) == 0) goto L_0x001f;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean equals(java.lang.Object r3) {
            /*
                r2 = this;
                if (r2 == r3) goto L_0x001f
                boolean r0 = r3 instanceof com.android.systemui.util.animation.PhysicsAnimator.AnimationUpdate
                if (r0 == 0) goto L_0x001d
                com.android.systemui.util.animation.PhysicsAnimator$AnimationUpdate r3 = (com.android.systemui.util.animation.PhysicsAnimator.AnimationUpdate) r3
                float r0 = r2.value
                float r1 = r3.value
                int r0 = java.lang.Float.compare(r0, r1)
                if (r0 != 0) goto L_0x001d
                float r2 = r2.velocity
                float r3 = r3.velocity
                int r2 = java.lang.Float.compare(r2, r3)
                if (r2 != 0) goto L_0x001d
                goto L_0x001f
            L_0x001d:
                r2 = 0
                return r2
            L_0x001f:
                r2 = 1
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.util.animation.PhysicsAnimator.AnimationUpdate.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            return (Float.hashCode(this.value) * 31) + Float.hashCode(this.velocity);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("AnimationUpdate(value=");
            sb.append(this.value);
            sb.append(", velocity=");
            sb.append(this.velocity);
            sb.append(")");
            return sb.toString();
        }

        public AnimationUpdate(float f, float f2) {
            this.value = f;
            this.velocity = f2;
        }
    }

    /* compiled from: PhysicsAnimator.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* renamed from: getInstanceConstructor$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final Function1<Object, PhysicsAnimator<?>> mo19412x45322b5() {
            return PhysicsAnimator.instanceConstructor;
        }

        public final <T> PhysicsAnimator<T> getInstance(T t) {
            Intrinsics.checkParameterIsNotNull(t, "target");
            if (!PhysicsAnimatorKt.getAnimators().containsKey(t)) {
                PhysicsAnimatorKt.getAnimators().put(t, mo19412x45322b5().invoke(t));
            }
            Object obj = PhysicsAnimatorKt.getAnimators().get(t);
            if (obj != null) {
                return (PhysicsAnimator) obj;
            }
            throw new TypeCastException("null cannot be cast to non-null type com.android.systemui.util.animation.PhysicsAnimator<T>");
        }

        public final float estimateFlingEndValue(float f, float f2, FlingConfig flingConfig) {
            Intrinsics.checkParameterIsNotNull(flingConfig, "flingConfig");
            return Math.min(flingConfig.mo19419x4440c074(), Math.max(flingConfig.mo19420x9e142a62(), f + (f2 / (flingConfig.mo19418xa5572db6() * 4.2f))));
        }

        public final String getReadablePropertyName(FloatPropertyCompat<?> floatPropertyCompat) {
            Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.TRANSLATION_X)) {
                return "translationX";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.TRANSLATION_Y)) {
                return "translationY";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.TRANSLATION_Z)) {
                return "translationZ";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.SCALE_X)) {
                return "scaleX";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.SCALE_Y)) {
                return "scaleY";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.ROTATION)) {
                return "rotation";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.ROTATION_X)) {
                return "rotationX";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.ROTATION_Y)) {
                return "rotationY";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.SCROLL_X)) {
                return "scrollX";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.SCROLL_Y)) {
                return "scrollY";
            }
            return Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.ALPHA) ? "alpha" : "Custom FloatPropertyCompat instance";
        }
    }

    /* compiled from: PhysicsAnimator.kt */
    public interface EndListener<T> {
        void onAnimationEnd(T t, FloatPropertyCompat<? super T> floatPropertyCompat, boolean z, boolean z2, float f, float f2, boolean z3);
    }

    /* compiled from: PhysicsAnimator.kt */
    public static final class FlingConfig {
        private float friction;
        private float max;
        private float min;
        private float startVelocity;

        public static /* synthetic */ FlingConfig copy$default(FlingConfig flingConfig, float f, float f2, float f3, float f4, int i, Object obj) {
            if ((i & 1) != 0) {
                f = flingConfig.friction;
            }
            if ((i & 2) != 0) {
                f2 = flingConfig.min;
            }
            if ((i & 4) != 0) {
                f3 = flingConfig.max;
            }
            if ((i & 8) != 0) {
                f4 = flingConfig.startVelocity;
            }
            return flingConfig.copy(f, f2, f3, f4);
        }

        public final FlingConfig copy(float f, float f2, float f3, float f4) {
            return new FlingConfig(f, f2, f3, f4);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x002e, code lost:
            if (java.lang.Float.compare(r2.startVelocity, r3.startVelocity) == 0) goto L_0x0033;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean equals(java.lang.Object r3) {
            /*
                r2 = this;
                if (r2 == r3) goto L_0x0033
                boolean r0 = r3 instanceof com.android.systemui.util.animation.PhysicsAnimator.FlingConfig
                if (r0 == 0) goto L_0x0031
                com.android.systemui.util.animation.PhysicsAnimator$FlingConfig r3 = (com.android.systemui.util.animation.PhysicsAnimator.FlingConfig) r3
                float r0 = r2.friction
                float r1 = r3.friction
                int r0 = java.lang.Float.compare(r0, r1)
                if (r0 != 0) goto L_0x0031
                float r0 = r2.min
                float r1 = r3.min
                int r0 = java.lang.Float.compare(r0, r1)
                if (r0 != 0) goto L_0x0031
                float r0 = r2.max
                float r1 = r3.max
                int r0 = java.lang.Float.compare(r0, r1)
                if (r0 != 0) goto L_0x0031
                float r2 = r2.startVelocity
                float r3 = r3.startVelocity
                int r2 = java.lang.Float.compare(r2, r3)
                if (r2 != 0) goto L_0x0031
                goto L_0x0033
            L_0x0031:
                r2 = 0
                return r2
            L_0x0033:
                r2 = 1
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.util.animation.PhysicsAnimator.FlingConfig.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            return (((((Float.hashCode(this.friction) * 31) + Float.hashCode(this.min)) * 31) + Float.hashCode(this.max)) * 31) + Float.hashCode(this.startVelocity);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("FlingConfig(friction=");
            sb.append(this.friction);
            sb.append(", min=");
            sb.append(this.min);
            sb.append(", max=");
            sb.append(this.max);
            sb.append(", startVelocity=");
            sb.append(this.startVelocity);
            sb.append(")");
            return sb.toString();
        }

        public FlingConfig(float f, float f2, float f3, float f4) {
            this.friction = f;
            this.min = f2;
            this.max = f3;
            this.startVelocity = f4;
        }

        /* renamed from: getFriction$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final float mo19418xa5572db6() {
            return this.friction;
        }

        /* renamed from: getMin$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final float mo19420x9e142a62() {
            return this.min;
        }

        /* renamed from: setMin$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final void mo19423xdb5fd7d6(float f) {
            this.min = f;
        }

        /* renamed from: getMax$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final float mo19419x4440c074() {
            return this.max;
        }

        /* renamed from: setMax$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final void mo19422x818c6de8(float f) {
            this.max = f;
        }

        /* renamed from: setStartVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final void mo19424xa675e8e3(float f) {
            this.startVelocity = f;
        }

        public FlingConfig() {
            this(PhysicsAnimatorKt.defaultFling.friction);
        }

        public FlingConfig(float f) {
            this(f, PhysicsAnimatorKt.defaultFling.min, PhysicsAnimatorKt.defaultFling.max);
        }

        public FlingConfig(float f, float f2, float f3) {
            this(f, f2, f3, 0.0f);
        }

        /* renamed from: applyToAnimation$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final void mo19415xe32feec1(FlingAnimation flingAnimation) {
            Intrinsics.checkParameterIsNotNull(flingAnimation, "anim");
            flingAnimation.setFriction(this.friction);
            flingAnimation.setMinValue(this.min);
            flingAnimation.setMaxValue(this.max);
            flingAnimation.setStartVelocity(this.startVelocity);
        }
    }

    /* compiled from: PhysicsAnimator.kt */
    public final class InternalListener {
        private List<? extends Function0<Unit>> endActions;
        private List<? extends EndListener<T>> endListeners;
        private int numPropertiesAnimating;
        private Set<? extends FloatPropertyCompat<? super T>> properties;
        final /* synthetic */ PhysicsAnimator this$0;
        private final ArrayMap<FloatPropertyCompat<? super T>, AnimationUpdate> undispatchedUpdates = new ArrayMap<>();
        private List<? extends UpdateListener<T>> updateListeners;

        public InternalListener(PhysicsAnimator physicsAnimator, Set<? extends FloatPropertyCompat<? super T>> set, List<? extends UpdateListener<T>> list, List<? extends EndListener<T>> list2, List<? extends Function0<Unit>> list3) {
            Intrinsics.checkParameterIsNotNull(set, "properties");
            Intrinsics.checkParameterIsNotNull(list, "updateListeners");
            Intrinsics.checkParameterIsNotNull(list2, "endListeners");
            Intrinsics.checkParameterIsNotNull(list3, "endActions");
            this.this$0 = physicsAnimator;
            this.properties = set;
            this.updateListeners = list;
            this.endListeners = list2;
            this.endActions = list3;
            this.numPropertiesAnimating = set.size();
        }

        /* renamed from: onInternalAnimationUpdate$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final void mo19427xa29f54d7(FloatPropertyCompat<? super T> floatPropertyCompat, float f, float f2) {
            Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
            if (this.properties.contains(floatPropertyCompat)) {
                this.undispatchedUpdates.put(floatPropertyCompat, new AnimationUpdate(f, f2));
                maybeDispatchUpdates();
            }
        }

        /* renamed from: onInternalAnimationEnd$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final boolean mo19426x1484a259(FloatPropertyCompat<? super T> floatPropertyCompat, boolean z, float f, float f2, boolean z2) {
            FloatPropertyCompat<? super T> floatPropertyCompat2 = floatPropertyCompat;
            Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
            if (!this.properties.contains(floatPropertyCompat)) {
                return false;
            }
            this.numPropertiesAnimating--;
            maybeDispatchUpdates();
            if (this.undispatchedUpdates.containsKey(floatPropertyCompat)) {
                for (UpdateListener updateListener : this.updateListeners) {
                    Object target = this.this$0.getTarget();
                    ArrayMap arrayMap = new ArrayMap();
                    arrayMap.put(floatPropertyCompat, this.undispatchedUpdates.get(floatPropertyCompat));
                    updateListener.onAnimationUpdateForProperty(target, arrayMap);
                }
                this.undispatchedUpdates.remove(floatPropertyCompat);
            }
            boolean z3 = !this.this$0.arePropertiesAnimating(this.properties);
            for (EndListener onAnimationEnd : this.endListeners) {
                onAnimationEnd.onAnimationEnd(this.this$0.getTarget(), floatPropertyCompat, z2, z, f, f2, z3);
                if (this.this$0.isPropertyAnimating(floatPropertyCompat)) {
                    return false;
                }
            }
            if (z3 && !z) {
                for (Function0 invoke : this.endActions) {
                    invoke.invoke();
                }
            }
            return z3;
        }

        private final void maybeDispatchUpdates() {
            if (this.undispatchedUpdates.size() >= this.numPropertiesAnimating && this.undispatchedUpdates.size() > 0) {
                for (UpdateListener onAnimationUpdateForProperty : this.updateListeners) {
                    onAnimationUpdateForProperty.onAnimationUpdateForProperty(this.this$0.getTarget(), new ArrayMap(this.undispatchedUpdates));
                }
                this.undispatchedUpdates.clear();
            }
        }
    }

    /* compiled from: PhysicsAnimator.kt */
    public static final class SpringConfig {
        private float dampingRatio;
        private float finalPosition;
        private float startVelocity;
        private float stiffness;

        public static /* synthetic */ SpringConfig copy$default(SpringConfig springConfig, float f, float f2, float f3, float f4, int i, Object obj) {
            if ((i & 1) != 0) {
                f = springConfig.stiffness;
            }
            if ((i & 2) != 0) {
                f2 = springConfig.dampingRatio;
            }
            if ((i & 4) != 0) {
                f3 = springConfig.startVelocity;
            }
            if ((i & 8) != 0) {
                f4 = springConfig.finalPosition;
            }
            return springConfig.copy(f, f2, f3, f4);
        }

        public final SpringConfig copy(float f, float f2, float f3, float f4) {
            return new SpringConfig(f, f2, f3, f4);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x002e, code lost:
            if (java.lang.Float.compare(r2.finalPosition, r3.finalPosition) == 0) goto L_0x0033;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean equals(java.lang.Object r3) {
            /*
                r2 = this;
                if (r2 == r3) goto L_0x0033
                boolean r0 = r3 instanceof com.android.systemui.util.animation.PhysicsAnimator.SpringConfig
                if (r0 == 0) goto L_0x0031
                com.android.systemui.util.animation.PhysicsAnimator$SpringConfig r3 = (com.android.systemui.util.animation.PhysicsAnimator.SpringConfig) r3
                float r0 = r2.stiffness
                float r1 = r3.stiffness
                int r0 = java.lang.Float.compare(r0, r1)
                if (r0 != 0) goto L_0x0031
                float r0 = r2.dampingRatio
                float r1 = r3.dampingRatio
                int r0 = java.lang.Float.compare(r0, r1)
                if (r0 != 0) goto L_0x0031
                float r0 = r2.startVelocity
                float r1 = r3.startVelocity
                int r0 = java.lang.Float.compare(r0, r1)
                if (r0 != 0) goto L_0x0031
                float r2 = r2.finalPosition
                float r3 = r3.finalPosition
                int r2 = java.lang.Float.compare(r2, r3)
                if (r2 != 0) goto L_0x0031
                goto L_0x0033
            L_0x0031:
                r2 = 0
                return r2
            L_0x0033:
                r2 = 1
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.util.animation.PhysicsAnimator.SpringConfig.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            return (((((Float.hashCode(this.stiffness) * 31) + Float.hashCode(this.dampingRatio)) * 31) + Float.hashCode(this.startVelocity)) * 31) + Float.hashCode(this.finalPosition);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("SpringConfig(stiffness=");
            sb.append(this.stiffness);
            sb.append(", dampingRatio=");
            sb.append(this.dampingRatio);
            sb.append(", startVelocity=");
            sb.append(this.startVelocity);
            sb.append(", finalPosition=");
            sb.append(this.finalPosition);
            sb.append(")");
            return sb.toString();
        }

        public SpringConfig(float f, float f2, float f3, float f4) {
            this.stiffness = f;
            this.dampingRatio = f2;
            this.startVelocity = f3;
            this.finalPosition = f4;
        }

        /* renamed from: getStiffness$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final float mo19433x54ee954f() {
            return this.stiffness;
        }

        /* renamed from: getDampingRatio$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final float mo19431x3ad2c865() {
            return this.dampingRatio;
        }

        /* renamed from: setStartVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final void mo19436xa675e8e3(float f) {
            this.startVelocity = f;
        }

        public /* synthetic */ SpringConfig(float f, float f2, float f3, float f4, int i, DefaultConstructorMarker defaultConstructorMarker) {
            if ((i & 4) != 0) {
                f3 = 0.0f;
            }
            if ((i & 8) != 0) {
                f4 = PhysicsAnimatorKt.UNSET;
            }
            this(f, f2, f3, f4);
        }

        /* renamed from: getFinalPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final float mo19432xb78cd1cf() {
            return this.finalPosition;
        }

        /* renamed from: setFinalPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final void mo19435x17853e43(float f) {
            this.finalPosition = f;
        }

        public SpringConfig() {
            this(PhysicsAnimatorKt.defaultSpring.stiffness, PhysicsAnimatorKt.defaultSpring.dampingRatio);
        }

        public SpringConfig(float f, float f2) {
            this(f, f2, 0.0f, 0.0f, 8, null);
        }

        /* renamed from: applyToAnimation$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public final void mo19428xe32feec1(SpringAnimation springAnimation) {
            Intrinsics.checkParameterIsNotNull(springAnimation, "anim");
            SpringForce spring = springAnimation.getSpring();
            if (spring == null) {
                spring = new SpringForce();
            }
            spring.setStiffness(this.stiffness);
            spring.setDampingRatio(this.dampingRatio);
            spring.setFinalPosition(this.finalPosition);
            springAnimation.setSpring(spring);
            float f = this.startVelocity;
            if (f != 0.0f) {
                springAnimation.setStartVelocity(f);
            }
        }
    }

    /* compiled from: PhysicsAnimator.kt */
    public interface UpdateListener<T> {
        void onAnimationUpdateForProperty(T t, ArrayMap<FloatPropertyCompat<? super T>, AnimationUpdate> arrayMap);
    }

    public static final float estimateFlingEndValue(float f, float f2, FlingConfig flingConfig) {
        return Companion.estimateFlingEndValue(f, f2, flingConfig);
    }

    public static final <T> PhysicsAnimator<T> getInstance(T t) {
        return Companion.getInstance(t);
    }

    public final PhysicsAnimator<T> flingThenSpring(FloatPropertyCompat<? super T> floatPropertyCompat, float f, FlingConfig flingConfig, SpringConfig springConfig) {
        flingThenSpring$default(this, floatPropertyCompat, f, flingConfig, springConfig, false, 16, null);
        return this;
    }

    private PhysicsAnimator(T t) {
        this.target = t;
        this.springAnimations = new ArrayMap<>();
        this.flingAnimations = new ArrayMap<>();
        this.springConfigs = new ArrayMap<>();
        this.flingConfigs = new ArrayMap<>();
        this.updateListeners = new ArrayList<>();
        this.endListeners = new ArrayList<>();
        this.endActions = new ArrayList<>();
        this.internalListeners = new ArrayList<>();
        this.startAction = new PhysicsAnimator$startAction$1(this);
        this.cancelAction = new PhysicsAnimator$cancelAction$1(this);
    }

    public /* synthetic */ PhysicsAnimator(Object obj, DefaultConstructorMarker defaultConstructorMarker) {
        this(obj);
    }

    public final T getTarget() {
        return this.target;
    }

    /* renamed from: getInternalListeners$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final ArrayList<InternalListener> mo19397x7fa0b292() {
        return this.internalListeners;
    }

    public final PhysicsAnimator<T> spring(FloatPropertyCompat<? super T> floatPropertyCompat, float f, float f2, float f3, float f4) {
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
        if (PhysicsAnimatorKt.verboseLogging) {
            StringBuilder sb = new StringBuilder();
            sb.append("Springing ");
            sb.append(Companion.getReadablePropertyName(floatPropertyCompat));
            sb.append(" to ");
            sb.append(f);
            sb.append('.');
            Log.d("PhysicsAnimator", sb.toString());
        }
        this.springConfigs.put(floatPropertyCompat, new SpringConfig(f3, f4, f2, f));
        return this;
    }

    public final PhysicsAnimator<T> spring(FloatPropertyCompat<? super T> floatPropertyCompat, float f, float f2, SpringConfig springConfig) {
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
        Intrinsics.checkParameterIsNotNull(springConfig, "config");
        spring(floatPropertyCompat, f, f2, springConfig.mo19433x54ee954f(), springConfig.mo19431x3ad2c865());
        return this;
    }

    public final PhysicsAnimator<T> spring(FloatPropertyCompat<? super T> floatPropertyCompat, float f, SpringConfig springConfig) {
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
        Intrinsics.checkParameterIsNotNull(springConfig, "config");
        spring(floatPropertyCompat, f, 0.0f, springConfig);
        return this;
    }

    public static /* synthetic */ PhysicsAnimator flingThenSpring$default(PhysicsAnimator physicsAnimator, FloatPropertyCompat floatPropertyCompat, float f, FlingConfig flingConfig, SpringConfig springConfig, boolean z, int i, Object obj) {
        if ((i & 16) != 0) {
            z = false;
        }
        physicsAnimator.flingThenSpring(floatPropertyCompat, f, flingConfig, springConfig, z);
        return physicsAnimator;
    }

    public final PhysicsAnimator<T> flingThenSpring(FloatPropertyCompat<? super T> floatPropertyCompat, float f, FlingConfig flingConfig, SpringConfig springConfig, boolean z) {
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
        Intrinsics.checkParameterIsNotNull(flingConfig, "flingConfig");
        Intrinsics.checkParameterIsNotNull(springConfig, "springConfig");
        FlingConfig copy$default = FlingConfig.copy$default(flingConfig, 0.0f, 0.0f, 0.0f, 0.0f, 15, null);
        SpringConfig copy$default2 = SpringConfig.copy$default(springConfig, 0.0f, 0.0f, 0.0f, 0.0f, 15, null);
        float min$frameworks__base__packages__SystemUI__android_common__SystemUI_core = f < ((float) 0) ? flingConfig.mo19420x9e142a62() : flingConfig.mo19419x4440c074();
        if (!z || min$frameworks__base__packages__SystemUI__android_common__SystemUI_core == (-FloatCompanionObject.INSTANCE.getMAX_VALUE()) || min$frameworks__base__packages__SystemUI__android_common__SystemUI_core == FloatCompanionObject.INSTANCE.getMAX_VALUE()) {
            copy$default.mo19424xa675e8e3(f);
        } else {
            float value = min$frameworks__base__packages__SystemUI__android_common__SystemUI_core - floatPropertyCompat.getValue(this.target);
            float friction$frameworks__base__packages__SystemUI__android_common__SystemUI_core = flingConfig.mo19418xa5572db6() * 4.2f * value;
            if (value > 0.0f && f >= 0.0f) {
                f = Math.max(friction$frameworks__base__packages__SystemUI__android_common__SystemUI_core, f);
            } else if (value < 0.0f && f <= 0.0f) {
                f = Math.min(friction$frameworks__base__packages__SystemUI__android_common__SystemUI_core, f);
            }
            copy$default.mo19424xa675e8e3(f);
            copy$default2.mo19435x17853e43(min$frameworks__base__packages__SystemUI__android_common__SystemUI_core);
        }
        this.flingConfigs.put(floatPropertyCompat, copy$default);
        this.springConfigs.put(floatPropertyCompat, copy$default2);
        return this;
    }

    public final PhysicsAnimator<T> addUpdateListener(UpdateListener<T> updateListener) {
        Intrinsics.checkParameterIsNotNull(updateListener, "listener");
        this.updateListeners.add(updateListener);
        return this;
    }

    public final PhysicsAnimator<T> withEndActions(Function0<Unit>... function0Arr) {
        Intrinsics.checkParameterIsNotNull(function0Arr, "endActions");
        this.endActions.addAll(ArraysKt___ArraysKt.filterNotNull(function0Arr));
        return this;
    }

    public final PhysicsAnimator<T> withEndActions(Runnable... runnableArr) {
        Intrinsics.checkParameterIsNotNull(runnableArr, "endActions");
        ArrayList<Function0<Unit>> arrayList = this.endActions;
        List<Runnable> filterNotNull = ArraysKt___ArraysKt.filterNotNull(runnableArr);
        ArrayList arrayList2 = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(filterNotNull, 10));
        for (Runnable physicsAnimator$withEndActions$1$1 : filterNotNull) {
            arrayList2.add(new PhysicsAnimator$withEndActions$1$1(physicsAnimator$withEndActions$1$1));
        }
        arrayList.addAll(arrayList2);
        return this;
    }

    public final void start() {
        this.startAction.invoke();
    }

    /* renamed from: startInternal$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo19404x3d1adf05() {
        Looper mainLooper = Looper.getMainLooper();
        Intrinsics.checkExpressionValueIsNotNull(mainLooper, "Looper.getMainLooper()");
        if (!mainLooper.isCurrentThread()) {
            Log.e("PhysicsAnimator", "Animations can only be started on the main thread. If you are seeing this message in a test, call PhysicsAnimatorTestUtils#prepareForTest in your test setup.");
        }
        ArrayList<Function0> arrayList = new ArrayList<>();
        for (FloatPropertyCompat floatPropertyCompat : mo19396x98c25072()) {
            FlingConfig flingConfig = (FlingConfig) this.flingConfigs.get(floatPropertyCompat);
            SpringConfig springConfig = (SpringConfig) this.springConfigs.get(floatPropertyCompat);
            float value = floatPropertyCompat.getValue(this.target);
            if (flingConfig != null) {
                arrayList.add(new PhysicsAnimator$startInternal$1(this, flingConfig, value, floatPropertyCompat));
            }
            if (springConfig != null) {
                if (flingConfig == null) {
                    SpringAnimation springAnimation = getSpringAnimation(floatPropertyCompat);
                    springConfig.mo19428xe32feec1(springAnimation);
                    arrayList.add(new PhysicsAnimator$startInternal$2(springAnimation));
                } else {
                    float min$frameworks__base__packages__SystemUI__android_common__SystemUI_core = flingConfig.mo19420x9e142a62();
                    float max$frameworks__base__packages__SystemUI__android_common__SystemUI_core = flingConfig.mo19419x4440c074();
                    ArrayList<EndListener<T>> arrayList2 = this.endListeners;
                    PhysicsAnimator$startInternal$3 physicsAnimator$startInternal$3 = new PhysicsAnimator$startInternal$3(this, floatPropertyCompat, min$frameworks__base__packages__SystemUI__android_common__SystemUI_core, max$frameworks__base__packages__SystemUI__android_common__SystemUI_core, springConfig);
                    arrayList2.add(0, physicsAnimator$startInternal$3);
                }
            }
        }
        ArrayList<InternalListener> arrayList3 = this.internalListeners;
        InternalListener internalListener = new InternalListener(this, mo19396x98c25072(), new ArrayList(this.updateListeners), new ArrayList(this.endListeners), new ArrayList(this.endActions));
        arrayList3.add(internalListener);
        for (Function0 invoke : arrayList) {
            invoke.invoke();
        }
        clearAnimator();
    }

    private final void clearAnimator() {
        this.springConfigs.clear();
        this.flingConfigs.clear();
        this.updateListeners.clear();
        this.endListeners.clear();
        this.endActions.clear();
    }

    /* access modifiers changed from: private */
    public final SpringAnimation getSpringAnimation(FloatPropertyCompat<? super T> floatPropertyCompat) {
        ArrayMap<FloatPropertyCompat<? super T>, SpringAnimation> arrayMap = this.springAnimations;
        Object obj = arrayMap.get(floatPropertyCompat);
        if (obj == null) {
            SpringAnimation springAnimation = new SpringAnimation(this.target, floatPropertyCompat);
            configureDynamicAnimation(springAnimation, floatPropertyCompat);
            obj = springAnimation;
            arrayMap.put(floatPropertyCompat, obj);
        }
        Intrinsics.checkExpressionValueIsNotNull(obj, "springAnimations.getOrPu…    as SpringAnimation })");
        return (SpringAnimation) obj;
    }

    /* access modifiers changed from: private */
    public final FlingAnimation getFlingAnimation(FloatPropertyCompat<? super T> floatPropertyCompat) {
        ArrayMap<FloatPropertyCompat<? super T>, FlingAnimation> arrayMap = this.flingAnimations;
        Object obj = arrayMap.get(floatPropertyCompat);
        if (obj == null) {
            FlingAnimation flingAnimation = new FlingAnimation(this.target, floatPropertyCompat);
            configureDynamicAnimation(flingAnimation, floatPropertyCompat);
            obj = flingAnimation;
            arrayMap.put(floatPropertyCompat, obj);
        }
        Intrinsics.checkExpressionValueIsNotNull(obj, "flingAnimations.getOrPut…     as FlingAnimation })");
        return (FlingAnimation) obj;
    }

    private final DynamicAnimation<?> configureDynamicAnimation(DynamicAnimation<?> dynamicAnimation, FloatPropertyCompat<? super T> floatPropertyCompat) {
        dynamicAnimation.addUpdateListener(new PhysicsAnimator$configureDynamicAnimation$1(this, floatPropertyCompat));
        dynamicAnimation.addEndListener(new PhysicsAnimator$configureDynamicAnimation$2(this, floatPropertyCompat, dynamicAnimation));
        return dynamicAnimation;
    }

    public final boolean isPropertyAnimating(FloatPropertyCompat<? super T> floatPropertyCompat) {
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
        SpringAnimation springAnimation = (SpringAnimation) this.springAnimations.get(floatPropertyCompat);
        if (!(springAnimation != null ? springAnimation.isRunning() : false)) {
            FlingAnimation flingAnimation = (FlingAnimation) this.flingAnimations.get(floatPropertyCompat);
            if (!(flingAnimation != null ? flingAnimation.isRunning() : false)) {
                return false;
            }
        }
        return true;
    }

    /* renamed from: getAnimatedProperties$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final Set<FloatPropertyCompat<? super T>> mo19396x98c25072() {
        Set keySet = this.springConfigs.keySet();
        Intrinsics.checkExpressionValueIsNotNull(keySet, "springConfigs.keys");
        Set keySet2 = this.flingConfigs.keySet();
        Intrinsics.checkExpressionValueIsNotNull(keySet2, "flingConfigs.keys");
        return CollectionsKt___CollectionsKt.union(keySet, keySet2);
    }

    /* renamed from: cancelInternal$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo19393x1247251d(Set<? extends FloatPropertyCompat<? super T>> set) {
        Intrinsics.checkParameterIsNotNull(set, "properties");
        for (FloatPropertyCompat floatPropertyCompat : set) {
            FlingAnimation flingAnimation = (FlingAnimation) this.flingAnimations.get(floatPropertyCompat);
            if (flingAnimation != null) {
                flingAnimation.cancel();
            }
            SpringAnimation springAnimation = (SpringAnimation) this.springAnimations.get(floatPropertyCompat);
            if (springAnimation != null) {
                springAnimation.cancel();
            }
        }
    }

    public final void cancel() {
        Function1<? super Set<? extends FloatPropertyCompat<? super T>>, Unit> function1 = this.cancelAction;
        Set keySet = this.flingAnimations.keySet();
        Intrinsics.checkExpressionValueIsNotNull(keySet, "flingAnimations.keys");
        function1.invoke(keySet);
        Function1<? super Set<? extends FloatPropertyCompat<? super T>>, Unit> function12 = this.cancelAction;
        Set keySet2 = this.springAnimations.keySet();
        Intrinsics.checkExpressionValueIsNotNull(keySet2, "springAnimations.keys");
        function12.invoke(keySet2);
    }

    public final void cancel(FloatPropertyCompat<? super T>... floatPropertyCompatArr) {
        Intrinsics.checkParameterIsNotNull(floatPropertyCompatArr, "properties");
        this.cancelAction.invoke(ArraysKt___ArraysKt.toSet(floatPropertyCompatArr));
    }

    public final boolean arePropertiesAnimating(Set<? extends FloatPropertyCompat<? super T>> set) {
        Intrinsics.checkParameterIsNotNull(set, "properties");
        if ((set instanceof Collection) && set.isEmpty()) {
            return false;
        }
        for (FloatPropertyCompat isPropertyAnimating : set) {
            if (isPropertyAnimating(isPropertyAnimating)) {
                return true;
            }
        }
        return false;
    }
}
