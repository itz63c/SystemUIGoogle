package com.google.android.systemui.dreamliner;

import android.util.Log;
import com.google.android.systemui.dreamliner.WirelessCharger.AlignInfoListener;

public class DockAlignmentController {
    private static final boolean DEBUG = Log.isLoggable("DockAlignmentController", 3);
    private int mAlignmentState = 0;
    private final DockObserver mDockObserver;
    private final WirelessCharger mWirelessCharger;

    private final class RegisterAlignInfoListener implements AlignInfoListener {
        private RegisterAlignInfoListener() {
        }

        public void onAlignInfoChanged(DockAlignInfo dockAlignInfo) {
            DockAlignmentController.this.onAlignInfoCallBack(dockAlignInfo);
        }
    }

    public DockAlignmentController(WirelessCharger wirelessCharger, DockObserver dockObserver) {
        this.mWirelessCharger = wirelessCharger;
        this.mDockObserver = dockObserver;
    }

    /* access modifiers changed from: 0000 */
    public void registerAlignInfoListener() {
        WirelessCharger wirelessCharger = this.mWirelessCharger;
        if (wirelessCharger == null) {
            Log.w("DockAlignmentController", "wirelessCharger is null");
        } else {
            wirelessCharger.registerAlignInfo(new RegisterAlignInfoListener());
        }
    }

    /* access modifiers changed from: private */
    public void onAlignInfoCallBack(DockAlignInfo dockAlignInfo) {
        int i = this.mAlignmentState;
        int alignmentState = getAlignmentState(dockAlignInfo);
        this.mAlignmentState = alignmentState;
        if (i != alignmentState) {
            this.mDockObserver.onAlignStateChanged(alignmentState);
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("onAlignStateChanged, state: ");
                sb.append(this.mAlignmentState);
                Log.d("DockAlignmentController", sb.toString());
            }
        }
    }

    private int getAlignmentState(DockAlignInfo dockAlignInfo) {
        String str = "DockAlignmentController";
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("onAlignInfo, state: ");
            sb.append(dockAlignInfo.getAlignState());
            sb.append(", alignPct: ");
            sb.append(dockAlignInfo.getAlignPct());
            Log.d(str, sb.toString());
        }
        int i = this.mAlignmentState;
        int alignState = dockAlignInfo.getAlignState();
        if (alignState == 0) {
            return i;
        }
        if (alignState == 1) {
            return 2;
        }
        if (alignState == 2) {
            int alignPct = dockAlignInfo.getAlignPct();
            if (alignPct >= 0) {
                if (alignPct < 100) {
                    return 1;
                }
                return 0;
            }
        } else if (alignState != 3) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Unexpected state: ");
            sb2.append(dockAlignInfo.getAlignState());
            Log.w(str, sb2.toString());
        }
        return -1;
    }
}
