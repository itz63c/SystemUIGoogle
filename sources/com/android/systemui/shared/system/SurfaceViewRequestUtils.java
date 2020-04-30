package com.android.systemui.shared.system;

import android.os.Bundle;
import android.os.IBinder;
import android.view.SurfaceControl;

public class SurfaceViewRequestUtils {
    public static SurfaceControl getSurfaceControl(Bundle bundle) {
        return bundle.getParcelable("surface_control");
    }

    public static IBinder getHostToken(Bundle bundle) {
        return bundle.getBinder("host_token");
    }
}
