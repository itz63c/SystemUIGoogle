package com.android.systemui.doze;

import com.android.systemui.doze.DozeMachine.Service;
import com.android.systemui.doze.DozeMachine.Service.Delegate;

public class DozeBrightnessHostForwarder extends Delegate {
    private final DozeHost mHost;

    public DozeBrightnessHostForwarder(Service service, DozeHost dozeHost) {
        super(service);
        this.mHost = dozeHost;
    }

    public void setDozeScreenBrightness(int i) {
        super.setDozeScreenBrightness(i);
        this.mHost.setDozeScreenBrightness(i);
    }
}
