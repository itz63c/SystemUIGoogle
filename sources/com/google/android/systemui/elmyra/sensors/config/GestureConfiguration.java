package com.google.android.systemui.elmyra.sensors.config;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.util.Range;
import com.android.systemui.DejankUtils;
import com.google.android.systemui.elmyra.UserContentObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GestureConfiguration {
    private static final Range<Float> SENSITIVITY_RANGE = Range.create(Float.valueOf(0.0f), Float.valueOf(1.0f));
    private final Consumer<Adjustment> mAdjustmentCallback = new Consumer() {
        public final void accept(Object obj) {
            GestureConfiguration.this.lambda$new$0$GestureConfiguration((Adjustment) obj);
        }
    };
    private final List<Adjustment> mAdjustments;
    private final Context mContext;
    private Listener mListener;
    private float mSensitivity;

    public interface Listener {
        void onGestureConfigurationChanged(GestureConfiguration gestureConfiguration);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$GestureConfiguration(Adjustment adjustment) {
        onSensitivityChanged();
    }

    public GestureConfiguration(Context context, List<Adjustment> list) {
        this.mContext = context;
        ArrayList arrayList = new ArrayList(list);
        this.mAdjustments = arrayList;
        arrayList.forEach(new Consumer() {
            public final void accept(Object obj) {
                GestureConfiguration.this.lambda$new$1$GestureConfiguration((Adjustment) obj);
            }
        });
        new UserContentObserver(this.mContext, Secure.getUriFor("assist_gesture_sensitivity"), new Consumer() {
            public final void accept(Object obj) {
                GestureConfiguration.this.lambda$new$2$GestureConfiguration((Uri) obj);
            }
        });
        this.mSensitivity = getUserSensitivity();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$GestureConfiguration(Adjustment adjustment) {
        adjustment.setCallback(this.mAdjustmentCallback);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$GestureConfiguration(Uri uri) {
        onSensitivityChanged();
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public float getSensitivity() {
        float f = this.mSensitivity;
        for (int i = 0; i < this.mAdjustments.size(); i++) {
            f = ((Float) SENSITIVITY_RANGE.clamp(Float.valueOf(((Adjustment) this.mAdjustments.get(i)).adjustSensitivity(f)))).floatValue();
        }
        return f;
    }

    private float getUserSensitivity() {
        float floatValue = ((Float) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier() {
            public final Object get() {
                return GestureConfiguration.this.lambda$getUserSensitivity$3$GestureConfiguration();
            }
        })).floatValue();
        if (!SENSITIVITY_RANGE.contains(Float.valueOf(floatValue))) {
            return 0.5f;
        }
        return floatValue;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getUserSensitivity$3 */
    public /* synthetic */ Float lambda$getUserSensitivity$3$GestureConfiguration() {
        return Float.valueOf(Secure.getFloatForUser(this.mContext.getContentResolver(), "assist_gesture_sensitivity", 0.5f, -2));
    }

    public void onSensitivityChanged() {
        this.mSensitivity = getUserSensitivity();
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onGestureConfigurationChanged(this);
        }
    }
}
