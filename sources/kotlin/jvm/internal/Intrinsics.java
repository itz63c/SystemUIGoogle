package kotlin.jvm.internal;

import java.util.Arrays;
import kotlin.KotlinNullPointerException;
import kotlin.UninitializedPropertyAccessException;

public class Intrinsics {
    public static int compare(int i, int i2) {
        if (i < i2) {
            return -1;
        }
        return i == i2 ? 0 : 1;
    }

    private Intrinsics() {
    }

    public static void throwNpe() {
        KotlinNullPointerException kotlinNullPointerException = new KotlinNullPointerException();
        sanitizeStackTrace(kotlinNullPointerException);
        throw kotlinNullPointerException;
    }

    public static void throwUninitializedProperty(String str) {
        UninitializedPropertyAccessException uninitializedPropertyAccessException = new UninitializedPropertyAccessException(str);
        sanitizeStackTrace(uninitializedPropertyAccessException);
        throw uninitializedPropertyAccessException;
    }

    public static void throwUninitializedPropertyAccessException(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("lateinit property ");
        sb.append(str);
        sb.append(" has not been initialized");
        throwUninitializedProperty(sb.toString());
        throw null;
    }

    public static void checkExpressionValueIsNotNull(Object obj, String str) {
        if (obj == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(" must not be null");
            IllegalStateException illegalStateException = new IllegalStateException(sb.toString());
            sanitizeStackTrace(illegalStateException);
            throw illegalStateException;
        }
    }

    public static void checkParameterIsNotNull(Object obj, String str) {
        if (obj == null) {
            throwParameterIsNullException(str);
            throw null;
        }
    }

    private static void throwParameterIsNullException(String str) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        StringBuilder sb = new StringBuilder();
        sb.append("Parameter specified as non-null is null: method ");
        sb.append(className);
        sb.append(".");
        sb.append(methodName);
        sb.append(", parameter ");
        sb.append(str);
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException(sb.toString());
        sanitizeStackTrace(illegalArgumentException);
        throw illegalArgumentException;
    }

    public static boolean areEqual(Object obj, Object obj2) {
        if (obj == null) {
            return obj2 == null;
        }
        return obj.equals(obj2);
    }

    public static boolean areEqual(Float f, float f2) {
        return f != null && f.floatValue() == f2;
    }

    public static boolean areEqual(float f, Float f2) {
        return f2 != null && f == f2.floatValue();
    }

    private static <T extends Throwable> T sanitizeStackTrace(T t) {
        sanitizeStackTrace(t, Intrinsics.class.getName());
        return t;
    }

    static <T extends Throwable> T sanitizeStackTrace(T t, String str) {
        StackTraceElement[] stackTrace = t.getStackTrace();
        int length = stackTrace.length;
        int i = -1;
        for (int i2 = 0; i2 < length; i2++) {
            if (str.equals(stackTrace[i2].getClassName())) {
                i = i2;
            }
        }
        t.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(stackTrace, i + 1, length));
        return t;
    }
}
