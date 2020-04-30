package com.android.systemui.statusbar.policy;

import android.content.Context;
import com.android.systemui.statusbar.policy.NetworkController.IconState;
import com.android.systemui.statusbar.policy.NetworkController.SignalCallback;
import java.util.BitSet;

public class EthernetSignalController extends SignalController<State, IconGroup> {
    public EthernetSignalController(Context context, CallbackHandler callbackHandler, NetworkControllerImpl networkControllerImpl) {
        super("EthernetSignalController", context, 3, callbackHandler, networkControllerImpl);
        T t = this.mCurrentState;
        T t2 = this.mLastState;
        int[][] iArr = EthernetIcons.ETHERNET_ICONS;
        int[] iArr2 = AccessibilityContentDescriptions.ETHERNET_CONNECTION_VALUES;
        IconGroup iconGroup = new IconGroup("Ethernet Icons", iArr, null, iArr2, 0, 0, 0, 0, iArr2[0]);
        t2.iconGroup = iconGroup;
        t.iconGroup = iconGroup;
    }

    public void updateConnectivity(BitSet bitSet, BitSet bitSet2) {
        this.mCurrentState.connected = bitSet.get(this.mTransportType);
        super.updateConnectivity(bitSet, bitSet2);
    }

    public void notifyListeners(SignalCallback signalCallback) {
        signalCallback.setEthernetIndicators(new IconState(this.mCurrentState.connected, getCurrentIconId(), getTextIfExists(getContentDescription()).toString()));
    }

    public State cleanState() {
        return new State();
    }
}
