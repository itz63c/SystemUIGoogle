package kotlin.math;

/* compiled from: MathJVM.kt */
class MathKt__MathJVMKt extends MathKt__MathHKt {
    public static int roundToInt(float f) {
        if (!Float.isNaN(f)) {
            return Math.round(f);
        }
        throw new IllegalArgumentException("Cannot round NaN value.");
    }
}
