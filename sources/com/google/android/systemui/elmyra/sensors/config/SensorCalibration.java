package com.google.android.systemui.elmyra.sensors.config;

import android.util.ArrayMap;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class SensorCalibration {
    private final Map<String, Float> mProperties = new ArrayMap();

    public static SensorCalibration getCalibration(int i) {
        String str = "Elmyra/SensorCalibration";
        try {
            return new SensorCalibration(new FileInputStream(String.format("/persist/sensors/elmyra/calibration.%d", new Object[]{Integer.valueOf(i)})));
        } catch (FileNotFoundException e) {
            Log.e(str, "Could not find calibration file", e);
            return null;
        } catch (SecurityException e2) {
            Log.e(str, "Could not open calibration file", e2);
            return null;
        }
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:1:0x0014 */
    /* JADX WARNING: Removed duplicated region for block: B:1:0x0014 A[LOOP:0: B:1:0x0014->B:12:0x0014, LOOP_START, SYNTHETIC, Splitter:B:1:0x0014] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    SensorCalibration(java.io.InputStream r4) {
        /*
            r3 = this;
            r3.<init>()
            android.util.ArrayMap r0 = new android.util.ArrayMap
            r0.<init>()
            r3.mProperties = r0
            java.io.BufferedReader r0 = new java.io.BufferedReader
            java.io.InputStreamReader r1 = new java.io.InputStreamReader
            r1.<init>(r4)
            r0.<init>(r1)
        L_0x0014:
            java.lang.String r4 = r0.readLine()     // Catch:{ IOException -> 0x0041 }
            if (r4 == 0) goto L_0x0049
            r1 = 58
            int r1 = r4.indexOf(r1)     // Catch:{ IOException -> 0x0041 }
            r2 = -1
            if (r1 != r2) goto L_0x0024
            goto L_0x0014
        L_0x0024:
            r2 = 0
            java.lang.String r2 = r4.substring(r2, r1)     // Catch:{ IOException -> 0x0041 }
            int r1 = r1 + 1
            java.lang.String r4 = r4.substring(r1)     // Catch:{ IOException -> 0x0041 }
            java.util.Map<java.lang.String, java.lang.Float> r1 = r3.mProperties     // Catch:{ NumberFormatException -> 0x0014 }
            java.lang.String r2 = r2.trim()     // Catch:{ NumberFormatException -> 0x0014 }
            float r4 = java.lang.Float.parseFloat(r4)     // Catch:{ NumberFormatException -> 0x0014 }
            java.lang.Float r4 = java.lang.Float.valueOf(r4)     // Catch:{ NumberFormatException -> 0x0014 }
            r1.put(r2, r4)     // Catch:{ NumberFormatException -> 0x0014 }
            goto L_0x0014
        L_0x0041:
            r3 = move-exception
            java.lang.String r4 = "Elmyra/SensorCalibration"
            java.lang.String r0 = "Error reading calibration file"
            android.util.Log.e(r4, r0, r3)
        L_0x0049:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.elmyra.sensors.config.SensorCalibration.<init>(java.io.InputStream):void");
    }

    public boolean contains(String str) {
        return this.mProperties.containsKey(str);
    }

    public float get(String str) {
        return ((Float) this.mProperties.get(str)).floatValue();
    }
}
