package com.android.wifitrackerlib;

import android.net.wifi.hotspot2.PasspointConfiguration;
import java.util.function.Function;

/* renamed from: com.android.wifitrackerlib.-$$Lambda$WifiPickerTracker$yWqMIohRea3hwyWs7tE9nZPE-I0 reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$WifiPickerTracker$yWqMIohRea3hwyWs7tE9nZPEI0 implements Function {
    public static final /* synthetic */ $$Lambda$WifiPickerTracker$yWqMIohRea3hwyWs7tE9nZPEI0 INSTANCE = new $$Lambda$WifiPickerTracker$yWqMIohRea3hwyWs7tE9nZPEI0();

    private /* synthetic */ $$Lambda$WifiPickerTracker$yWqMIohRea3hwyWs7tE9nZPEI0() {
    }

    public final Object apply(Object obj) {
        return PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(((PasspointConfiguration) obj).getUniqueId());
    }
}
