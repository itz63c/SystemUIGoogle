package kotlin;

import java.io.Serializable;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Result.kt */
public final class Result<T> implements Serializable {
    public static final Companion Companion = new Companion(null);
    private final Object value;

    /* compiled from: Result.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }

    /* compiled from: Result.kt */
    public static final class Failure implements Serializable {
        public final Throwable exception;

        public Failure(Throwable th) {
            Intrinsics.checkParameterIsNotNull(th, "exception");
            this.exception = th;
        }

        public boolean equals(Object obj) {
            return (obj instanceof Failure) && Intrinsics.areEqual((Object) this.exception, (Object) ((Failure) obj).exception);
        }

        public int hashCode() {
            return this.exception.hashCode();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Failure(");
            sb.append(this.exception);
            sb.append(')');
            return sb.toString();
        }
    }

    /* renamed from: constructor-impl reason: not valid java name */
    public static Object m171constructorimpl(Object obj) {
        return obj;
    }

    /* renamed from: equals-impl reason: not valid java name */
    public static boolean m172equalsimpl(Object obj, Object obj2) {
        return (obj2 instanceof Result) && Intrinsics.areEqual(obj, ((Result) obj2).m175unboximpl());
    }

    /* renamed from: hashCode-impl reason: not valid java name */
    public static int m173hashCodeimpl(Object obj) {
        if (obj != null) {
            return obj.hashCode();
        }
        return 0;
    }

    public boolean equals(Object obj) {
        return m172equalsimpl(this.value, obj);
    }

    public int hashCode() {
        return m173hashCodeimpl(this.value);
    }

    public String toString() {
        return m174toStringimpl(this.value);
    }

    /* renamed from: unbox-impl reason: not valid java name */
    public final /* synthetic */ Object m175unboximpl() {
        return this.value;
    }

    /* renamed from: toString-impl reason: not valid java name */
    public static String m174toStringimpl(Object obj) {
        if (obj instanceof Failure) {
            return obj.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Success(");
        sb.append(obj);
        sb.append(')');
        return sb.toString();
    }
}
