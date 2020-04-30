package com.android.systemui.statusbar.policy;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SubscriptionInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.policy.NetworkController.EmergencyListener;
import com.android.systemui.statusbar.policy.NetworkController.IconState;
import com.android.systemui.statusbar.policy.NetworkController.SignalCallback;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CallbackHandler extends Handler implements EmergencyListener, SignalCallback {
    private final ArrayList<EmergencyListener> mEmergencyListeners = new ArrayList<>();
    private final ArrayList<SignalCallback> mSignalCallbacks = new ArrayList<>();

    public CallbackHandler() {
        super(Looper.getMainLooper());
    }

    @VisibleForTesting
    CallbackHandler(Looper looper) {
        super(looper);
    }

    public void handleMessage(Message message) {
        switch (message.what) {
            case 0:
                Iterator it = this.mEmergencyListeners.iterator();
                while (it.hasNext()) {
                    ((EmergencyListener) it.next()).setEmergencyCallsOnly(message.arg1 != 0);
                }
                return;
            case 1:
                Iterator it2 = this.mSignalCallbacks.iterator();
                while (it2.hasNext()) {
                    ((SignalCallback) it2.next()).setSubs((List) message.obj);
                }
                return;
            case 2:
                Iterator it3 = this.mSignalCallbacks.iterator();
                while (it3.hasNext()) {
                    ((SignalCallback) it3.next()).setNoSims(message.arg1 != 0, message.arg2 != 0);
                }
                return;
            case 3:
                Iterator it4 = this.mSignalCallbacks.iterator();
                while (it4.hasNext()) {
                    ((SignalCallback) it4.next()).setEthernetIndicators((IconState) message.obj);
                }
                return;
            case 4:
                Iterator it5 = this.mSignalCallbacks.iterator();
                while (it5.hasNext()) {
                    ((SignalCallback) it5.next()).setIsAirplaneMode((IconState) message.obj);
                }
                return;
            case 5:
                Iterator it6 = this.mSignalCallbacks.iterator();
                while (it6.hasNext()) {
                    ((SignalCallback) it6.next()).setMobileDataEnabled(message.arg1 != 0);
                }
                return;
            case 6:
                if (message.arg1 != 0) {
                    this.mEmergencyListeners.add((EmergencyListener) message.obj);
                    return;
                } else {
                    this.mEmergencyListeners.remove((EmergencyListener) message.obj);
                    return;
                }
            case 7:
                if (message.arg1 != 0) {
                    this.mSignalCallbacks.add((SignalCallback) message.obj);
                    return;
                } else {
                    this.mSignalCallbacks.remove((SignalCallback) message.obj);
                    return;
                }
            default:
                return;
        }
    }

    public void setWifiIndicators(boolean z, IconState iconState, IconState iconState2, boolean z2, boolean z3, String str, boolean z4, String str2) {
        $$Lambda$CallbackHandler$BL9Oe1XlhjuRCIkE3XITv_5klDM r0 = new Runnable(z, iconState, iconState2, z2, z3, str, z4, str2) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ IconState f$2;
            public final /* synthetic */ IconState f$3;
            public final /* synthetic */ boolean f$4;
            public final /* synthetic */ boolean f$5;
            public final /* synthetic */ String f$6;
            public final /* synthetic */ boolean f$7;
            public final /* synthetic */ String f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                CallbackHandler.this.lambda$setWifiIndicators$0$CallbackHandler(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        };
        post(r0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setWifiIndicators$0 */
    public /* synthetic */ void lambda$setWifiIndicators$0$CallbackHandler(boolean z, IconState iconState, IconState iconState2, boolean z2, boolean z3, String str, boolean z4, String str2) {
        Iterator it = this.mSignalCallbacks.iterator();
        while (it.hasNext()) {
            ((SignalCallback) it.next()).setWifiIndicators(z, iconState, iconState2, z2, z3, str, z4, str2);
        }
    }

    public void setMobileDataIndicators(IconState iconState, IconState iconState2, int i, int i2, boolean z, boolean z2, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, boolean z3, int i3, boolean z4) {
        $$Lambda$CallbackHandler$uMnAccxpYS4aQwu2V03dAeAi978 r0 = new Runnable(iconState, iconState2, i, i2, z, z2, charSequence, charSequence2, charSequence3, z3, i3, z4) {
            public final /* synthetic */ IconState f$1;
            public final /* synthetic */ boolean f$10;
            public final /* synthetic */ int f$11;
            public final /* synthetic */ boolean f$12;
            public final /* synthetic */ IconState f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ int f$4;
            public final /* synthetic */ boolean f$5;
            public final /* synthetic */ boolean f$6;
            public final /* synthetic */ CharSequence f$7;
            public final /* synthetic */ CharSequence f$8;
            public final /* synthetic */ CharSequence f$9;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
                this.f$9 = r10;
                this.f$10 = r11;
                this.f$11 = r12;
                this.f$12 = r13;
            }

            public final void run() {
                CallbackHandler.this.lambda$setMobileDataIndicators$1$CallbackHandler(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
            }
        };
        post(r0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setMobileDataIndicators$1 */
    public /* synthetic */ void lambda$setMobileDataIndicators$1$CallbackHandler(IconState iconState, IconState iconState2, int i, int i2, boolean z, boolean z2, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, boolean z3, int i3, boolean z4) {
        Iterator it = this.mSignalCallbacks.iterator();
        while (it.hasNext()) {
            ((SignalCallback) it.next()).setMobileDataIndicators(iconState, iconState2, i, i2, z, z2, charSequence, charSequence2, charSequence3, z3, i3, z4);
        }
    }

    public void setSubs(List<SubscriptionInfo> list) {
        obtainMessage(1, list).sendToTarget();
    }

    public void setNoSims(boolean z, boolean z2) {
        obtainMessage(2, z ? 1 : 0, z2 ? 1 : 0).sendToTarget();
    }

    public void setMobileDataEnabled(boolean z) {
        obtainMessage(5, z ? 1 : 0, 0).sendToTarget();
    }

    public void setEmergencyCallsOnly(boolean z) {
        obtainMessage(0, z ? 1 : 0, 0).sendToTarget();
    }

    public void setEthernetIndicators(IconState iconState) {
        obtainMessage(3, iconState).sendToTarget();
    }

    public void setIsAirplaneMode(IconState iconState) {
        obtainMessage(4, iconState).sendToTarget();
    }

    public void setListening(SignalCallback signalCallback, boolean z) {
        obtainMessage(7, z ? 1 : 0, 0, signalCallback).sendToTarget();
    }
}
