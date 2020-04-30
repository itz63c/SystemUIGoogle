package com.android.systemui;

import com.android.systemui.BootCompleteCache.BootCompleteListener;
import java.lang.ref.WeakReference;
import java.util.function.Predicate;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.BootCompleteCacheImpl$removeListener$$inlined$synchronized$lambda$1 */
/* compiled from: BootCompleteCacheImpl.kt */
final class C0668x854f7506<T> implements Predicate<WeakReference<BootCompleteListener>> {
    final /* synthetic */ BootCompleteListener $listener$inlined;

    C0668x854f7506(BootCompleteCacheImpl bootCompleteCacheImpl, BootCompleteListener bootCompleteListener) {
        this.$listener$inlined = bootCompleteListener;
    }

    public final boolean test(WeakReference<BootCompleteListener> weakReference) {
        Intrinsics.checkParameterIsNotNull(weakReference, "it");
        return weakReference.get() == null || ((BootCompleteListener) weakReference.get()) == this.$listener$inlined;
    }
}
