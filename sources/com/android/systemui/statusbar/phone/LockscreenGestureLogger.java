package com.android.systemui.statusbar.phone;

import android.metrics.LogMaker;
import android.util.ArrayMap;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.EventLogConstants;
import com.android.systemui.EventLogTags;

public class LockscreenGestureLogger {
    private ArrayMap<Integer, Integer> mLegacyMap = new ArrayMap<>(EventLogConstants.METRICS_GESTURE_TYPE_MAP.length);
    private final MetricsLogger mMetricsLogger = ((MetricsLogger) Dependency.get(MetricsLogger.class));

    public LockscreenGestureLogger() {
        int i = 0;
        while (true) {
            int[] iArr = EventLogConstants.METRICS_GESTURE_TYPE_MAP;
            if (i < iArr.length) {
                this.mLegacyMap.put(Integer.valueOf(iArr[i]), Integer.valueOf(i));
                i++;
            } else {
                return;
            }
        }
    }

    public void write(int i, int i2, int i3) {
        this.mMetricsLogger.write(new LogMaker(i).setType(4).addTaggedData(826, Integer.valueOf(i2)).addTaggedData(827, Integer.valueOf(i3)));
        EventLogTags.writeSysuiLockscreenGesture(safeLookup(i), i2, i3);
    }

    public void writeAtFractionalPosition(int i, int i2, int i3, int i4) {
        this.mMetricsLogger.write(new LogMaker(i).setType(4).addTaggedData(1326, Integer.valueOf(i2)).addTaggedData(1327, Integer.valueOf(i3)).addTaggedData(1329, Integer.valueOf(i4)));
    }

    private int safeLookup(int i) {
        Integer num = (Integer) this.mLegacyMap.get(Integer.valueOf(i));
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }
}
