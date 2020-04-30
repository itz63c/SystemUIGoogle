package com.android.systemui.controls.p004ui;

import com.android.systemui.controls.ControlsServiceInfo;
import java.util.Comparator;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$createCallback$1$onServicesUpdated$1$$special$$inlined$compareBy$1 */
/* compiled from: Comparisons.kt */
public final class C0815xb37c682a<T> implements Comparator<T> {
    final /* synthetic */ Comparator $comparator;

    public C0815xb37c682a(Comparator comparator) {
        this.$comparator = comparator;
    }

    public final int compare(T t, T t2) {
        Comparator comparator = this.$comparator;
        CharSequence loadLabel = ((ControlsServiceInfo) t).loadLabel();
        String str = "it.loadLabel()";
        Intrinsics.checkExpressionValueIsNotNull(loadLabel, str);
        CharSequence loadLabel2 = ((ControlsServiceInfo) t2).loadLabel();
        Intrinsics.checkExpressionValueIsNotNull(loadLabel2, str);
        return comparator.compare(loadLabel, loadLabel2);
    }
}
