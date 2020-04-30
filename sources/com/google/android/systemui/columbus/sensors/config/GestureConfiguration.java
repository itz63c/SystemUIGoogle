package com.google.android.systemui.columbus.sensors.config;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.util.Range;
import com.android.systemui.DejankUtils;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.ColumbusContentObserver.Factory;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: GestureConfiguration.kt */
public final class GestureConfiguration {
    private static final Range<Float> SENSITIVITY_RANGE = Range.create(Float.valueOf(0.0f), Float.valueOf(1.0f));
    private final Function1<Adjustment, Unit> adjustmentCallback = new GestureConfiguration$adjustmentCallback$1(this);
    private final List<Adjustment> adjustmentsList;
    /* access modifiers changed from: private */
    public final Context context;
    private Listener listener;
    private final ColumbusContentObserver settingsObserver;

    /* compiled from: GestureConfiguration.kt */
    public interface Listener {
        void onGestureConfigurationChanged(GestureConfiguration gestureConfiguration);
    }

    public GestureConfiguration(Context context2, Set<Adjustment> set, Factory factory) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(set, "adjustments");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        this.context = context2;
        this.adjustmentsList = CollectionsKt___CollectionsKt.toList(set);
        Uri uriFor = Secure.getUriFor("assist_gesture_sensitivity");
        Intrinsics.checkExpressionValueIsNotNull(uriFor, "Settings.Secure.getUriFo…SIST_GESTURE_SENSITIVITY)");
        this.settingsObserver = factory.create(uriFor, new GestureConfiguration$settingsObserver$1(this));
        getUserSensitivity();
        this.settingsObserver.activate();
        for (Adjustment callback : this.adjustmentsList) {
            callback.setCallback(this.adjustmentCallback);
        }
    }

    private final float getUserSensitivity() {
        Float f = (Float) DejankUtils.whitelistIpcs((Supplier<T>) new GestureConfiguration$getUserSensitivity$sensitivity$1<T>(this));
        if (!SENSITIVITY_RANGE.contains(f)) {
            f = Float.valueOf(0.5f);
        }
        Intrinsics.checkExpressionValueIsNotNull(f, "sensitivity");
        return f.floatValue();
    }

    public final void onSensitivityChanged() {
        getUserSensitivity();
        Listener listener2 = this.listener;
        if (listener2 != null) {
            listener2.onGestureConfigurationChanged(this);
        }
    }
}
