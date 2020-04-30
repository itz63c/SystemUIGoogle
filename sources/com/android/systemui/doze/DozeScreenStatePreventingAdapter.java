package com.android.systemui.doze;

import com.android.systemui.doze.DozeMachine.Service;
import com.android.systemui.doze.DozeMachine.Service.Delegate;
import com.android.systemui.statusbar.phone.DozeParameters;

public class DozeScreenStatePreventingAdapter extends Delegate {
    DozeScreenStatePreventingAdapter(Service service) {
        super(service);
    }

    public void setDozeScreenState(int i) {
        if (i == 3) {
            i = 2;
        } else if (i == 4) {
            i = 6;
        }
        super.setDozeScreenState(i);
    }

    public static Service wrapIfNeeded(Service service, DozeParameters dozeParameters) {
        return isNeeded(dozeParameters) ? new DozeScreenStatePreventingAdapter(service) : service;
    }

    private static boolean isNeeded(DozeParameters dozeParameters) {
        return !dozeParameters.getDisplayStateSupported();
    }
}
