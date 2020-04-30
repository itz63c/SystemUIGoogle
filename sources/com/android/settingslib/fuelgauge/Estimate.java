package com.android.settingslib.fuelgauge;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings.Global;
import java.time.Duration;
import java.time.Instant;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Estimate.kt */
public final class Estimate {
    public static final Companion Companion = new Companion(null);
    private final long averageDischargeTime;
    private final long estimateMillis;
    private final boolean isBasedOnUsage;

    /* compiled from: Estimate.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final Estimate getCachedEstimateIfAvailable(Context context) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            ContentResolver contentResolver = context.getContentResolver();
            if (Duration.between(Instant.ofEpochMilli(Global.getLong(contentResolver, "battery_estimates_last_update_time", -1)), Instant.now()).compareTo(Duration.ofMinutes(1)) > 0) {
                return null;
            }
            long j = (long) -1;
            long j2 = Global.getLong(contentResolver, "time_remaining_estimate_millis", j);
            boolean z = false;
            if (Global.getInt(contentResolver, "time_remaining_estimate_based_on_usage", 0) == 1) {
                z = true;
            }
            Estimate estimate = new Estimate(j2, z, Global.getLong(contentResolver, "average_time_to_discharge", j));
            return estimate;
        }

        public final void storeCachedEstimate(Context context, Estimate estimate) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(estimate, "estimate");
            ContentResolver contentResolver = context.getContentResolver();
            Global.putLong(contentResolver, "time_remaining_estimate_millis", estimate.getEstimateMillis());
            Global.putInt(contentResolver, "time_remaining_estimate_based_on_usage", estimate.isBasedOnUsage() ? 1 : 0);
            Global.putLong(contentResolver, "average_time_to_discharge", estimate.getAverageDischargeTime());
            Global.putLong(contentResolver, "battery_estimates_last_update_time", System.currentTimeMillis());
        }
    }

    public static final Estimate getCachedEstimateIfAvailable(Context context) {
        return Companion.getCachedEstimateIfAvailable(context);
    }

    public static final void storeCachedEstimate(Context context, Estimate estimate) {
        Companion.storeCachedEstimate(context, estimate);
    }

    public Estimate(long j, boolean z, long j2) {
        this.estimateMillis = j;
        this.isBasedOnUsage = z;
        this.averageDischargeTime = j2;
    }

    public final long getEstimateMillis() {
        return this.estimateMillis;
    }

    public final boolean isBasedOnUsage() {
        return this.isBasedOnUsage;
    }

    public final long getAverageDischargeTime() {
        return this.averageDischargeTime;
    }
}
