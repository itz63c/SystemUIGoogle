package com.android.systemui.power;

/* compiled from: BatteryStateSnapshot.kt */
public final class BatteryStateSnapshot {
    private final long averageTimeToDischargeMillis;
    private final int batteryLevel;
    private final int batteryStatus;
    private final int bucket;
    private final boolean isBasedOnUsage;
    private boolean isHybrid = false;
    private final boolean isLowWarningEnabled;
    private final boolean isPowerSaver;
    private final int lowLevelThreshold;
    private final long lowThresholdMillis;
    private final boolean plugged;
    private final int severeLevelThreshold;
    private final long severeThresholdMillis;
    private final long timeRemainingMillis;

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x005c, code lost:
        if (r4.isLowWarningEnabled == r5.isLowWarningEnabled) goto L_0x0061;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r5) {
        /*
            r4 = this;
            if (r4 == r5) goto L_0x0061
            boolean r0 = r5 instanceof com.android.systemui.power.BatteryStateSnapshot
            if (r0 == 0) goto L_0x005f
            com.android.systemui.power.BatteryStateSnapshot r5 = (com.android.systemui.power.BatteryStateSnapshot) r5
            int r0 = r4.batteryLevel
            int r1 = r5.batteryLevel
            if (r0 != r1) goto L_0x005f
            boolean r0 = r4.isPowerSaver
            boolean r1 = r5.isPowerSaver
            if (r0 != r1) goto L_0x005f
            boolean r0 = r4.plugged
            boolean r1 = r5.plugged
            if (r0 != r1) goto L_0x005f
            int r0 = r4.bucket
            int r1 = r5.bucket
            if (r0 != r1) goto L_0x005f
            int r0 = r4.batteryStatus
            int r1 = r5.batteryStatus
            if (r0 != r1) goto L_0x005f
            int r0 = r4.severeLevelThreshold
            int r1 = r5.severeLevelThreshold
            if (r0 != r1) goto L_0x005f
            int r0 = r4.lowLevelThreshold
            int r1 = r5.lowLevelThreshold
            if (r0 != r1) goto L_0x005f
            long r0 = r4.timeRemainingMillis
            long r2 = r5.timeRemainingMillis
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x005f
            long r0 = r4.averageTimeToDischargeMillis
            long r2 = r5.averageTimeToDischargeMillis
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x005f
            long r0 = r4.severeThresholdMillis
            long r2 = r5.severeThresholdMillis
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x005f
            long r0 = r4.lowThresholdMillis
            long r2 = r5.lowThresholdMillis
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x005f
            boolean r0 = r4.isBasedOnUsage
            boolean r1 = r5.isBasedOnUsage
            if (r0 != r1) goto L_0x005f
            boolean r4 = r4.isLowWarningEnabled
            boolean r5 = r5.isLowWarningEnabled
            if (r4 != r5) goto L_0x005f
            goto L_0x0061
        L_0x005f:
            r4 = 0
            return r4
        L_0x0061:
            r4 = 1
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.power.BatteryStateSnapshot.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        int hashCode = Integer.hashCode(this.batteryLevel) * 31;
        int i = this.isPowerSaver;
        int i2 = 1;
        if (i != 0) {
            i = 1;
        }
        int i3 = (hashCode + i) * 31;
        int i4 = this.plugged;
        if (i4 != 0) {
            i4 = 1;
        }
        int hashCode2 = (((((((((((((((((i3 + i4) * 31) + Integer.hashCode(this.bucket)) * 31) + Integer.hashCode(this.batteryStatus)) * 31) + Integer.hashCode(this.severeLevelThreshold)) * 31) + Integer.hashCode(this.lowLevelThreshold)) * 31) + Long.hashCode(this.timeRemainingMillis)) * 31) + Long.hashCode(this.averageTimeToDischargeMillis)) * 31) + Long.hashCode(this.severeThresholdMillis)) * 31) + Long.hashCode(this.lowThresholdMillis)) * 31;
        int i5 = this.isBasedOnUsage;
        if (i5 != 0) {
            i5 = 1;
        }
        int i6 = (hashCode2 + i5) * 31;
        boolean z = this.isLowWarningEnabled;
        if (!z) {
            i2 = z;
        }
        return i6 + i2;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BatteryStateSnapshot(batteryLevel=");
        sb.append(this.batteryLevel);
        sb.append(", isPowerSaver=");
        sb.append(this.isPowerSaver);
        sb.append(", plugged=");
        sb.append(this.plugged);
        sb.append(", bucket=");
        sb.append(this.bucket);
        sb.append(", batteryStatus=");
        sb.append(this.batteryStatus);
        sb.append(", severeLevelThreshold=");
        sb.append(this.severeLevelThreshold);
        sb.append(", lowLevelThreshold=");
        sb.append(this.lowLevelThreshold);
        sb.append(", timeRemainingMillis=");
        sb.append(this.timeRemainingMillis);
        sb.append(", averageTimeToDischargeMillis=");
        sb.append(this.averageTimeToDischargeMillis);
        sb.append(", severeThresholdMillis=");
        sb.append(this.severeThresholdMillis);
        sb.append(", lowThresholdMillis=");
        sb.append(this.lowThresholdMillis);
        sb.append(", isBasedOnUsage=");
        sb.append(this.isBasedOnUsage);
        sb.append(", isLowWarningEnabled=");
        sb.append(this.isLowWarningEnabled);
        sb.append(")");
        return sb.toString();
    }

    public BatteryStateSnapshot(int i, boolean z, boolean z2, int i2, int i3, int i4, int i5, long j, long j2, long j3, long j4, boolean z3, boolean z4) {
        this.batteryLevel = i;
        this.isPowerSaver = z;
        this.plugged = z2;
        this.bucket = i2;
        this.batteryStatus = i3;
        this.severeLevelThreshold = i4;
        this.lowLevelThreshold = i5;
        this.timeRemainingMillis = j;
        this.averageTimeToDischargeMillis = j2;
        this.severeThresholdMillis = j3;
        this.lowThresholdMillis = j4;
        this.isBasedOnUsage = z3;
        this.isLowWarningEnabled = z4;
    }

    public final int getBatteryLevel() {
        return this.batteryLevel;
    }

    public final boolean isPowerSaver() {
        return this.isPowerSaver;
    }

    public final boolean getPlugged() {
        return this.plugged;
    }

    public final int getBucket() {
        return this.bucket;
    }

    public final int getBatteryStatus() {
        return this.batteryStatus;
    }

    public final int getSevereLevelThreshold() {
        return this.severeLevelThreshold;
    }

    public final int getLowLevelThreshold() {
        return this.lowLevelThreshold;
    }

    public final long getTimeRemainingMillis() {
        return this.timeRemainingMillis;
    }

    public final long getAverageTimeToDischargeMillis() {
        return this.averageTimeToDischargeMillis;
    }

    public final long getSevereThresholdMillis() {
        return this.severeThresholdMillis;
    }

    public final long getLowThresholdMillis() {
        return this.lowThresholdMillis;
    }

    public final boolean isBasedOnUsage() {
        return this.isBasedOnUsage;
    }

    public final boolean isLowWarningEnabled() {
        return this.isLowWarningEnabled;
    }

    public final boolean isHybrid() {
        return this.isHybrid;
    }

    public BatteryStateSnapshot(int i, boolean z, boolean z2, int i2, int i3, int i4, int i5) {
        long j = (long) -1;
        this(i, z, z2, i2, i3, i4, i5, j, j, j, j, false, true);
    }
}
