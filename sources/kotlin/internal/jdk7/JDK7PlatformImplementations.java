package kotlin.internal.jdk7;

import kotlin.internal.PlatformImplementations;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: JDK7PlatformImplementations.kt */
public class JDK7PlatformImplementations extends PlatformImplementations {
    public void addSuppressed(Throwable th, Throwable th2) {
        Intrinsics.checkParameterIsNotNull(th, "cause");
        Intrinsics.checkParameterIsNotNull(th2, "exception");
        th.addSuppressed(th2);
    }
}
