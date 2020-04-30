package com.android.systemui.statusbar;

import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.settingslib.WirelessUtils;
import com.android.systemui.DemoMode;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkController.IconState;
import com.android.systemui.statusbar.policy.NetworkController.SignalCallback;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import java.util.List;

public class OperatorNameView extends TextView implements DemoMode, DarkReceiver, SignalCallback, Tunable {
    private final KeyguardUpdateMonitorCallback mCallback;
    private boolean mDemoMode;
    private KeyguardUpdateMonitor mKeyguardUpdateMonitor;

    public OperatorNameView(Context context) {
        this(context, null);
    }

    public OperatorNameView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OperatorNameView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCallback = new KeyguardUpdateMonitorCallback() {
            public void onRefreshCarrierInfo() {
                OperatorNameView.this.updateText();
            }
        };
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        KeyguardUpdateMonitor keyguardUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        keyguardUpdateMonitor.registerCallback(this.mCallback);
        ((DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class)).addDarkReceiver((DarkReceiver) this);
        ((NetworkController) Dependency.get(NetworkController.class)).addCallback(this);
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, "show_operator_name");
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mKeyguardUpdateMonitor.removeCallback(this.mCallback);
        ((DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class)).removeDarkReceiver((DarkReceiver) this);
        ((NetworkController) Dependency.get(NetworkController.class)).removeCallback(this);
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        setTextColor(DarkIconDispatcher.getTint(rect, this, i));
    }

    public void setIsAirplaneMode(IconState iconState) {
        update();
    }

    public void onTuningChanged(String str, String str2) {
        update();
    }

    public void dispatchDemoCommand(String str, Bundle bundle) {
        if (!this.mDemoMode && str.equals("enter")) {
            this.mDemoMode = true;
        } else if (this.mDemoMode && str.equals("exit")) {
            this.mDemoMode = false;
            update();
        } else if (this.mDemoMode && str.equals("operator")) {
            setText(bundle.getString("name"));
        }
    }

    private void update() {
        boolean z = true;
        if (((TunerService) Dependency.get(TunerService.class)).getValue("show_operator_name", 1) == 0) {
            z = false;
        }
        setVisibility(z ? 0 : 8);
        boolean isNetworkSupported = ConnectivityManager.from(this.mContext).isNetworkSupported(0);
        boolean isAirplaneModeOn = WirelessUtils.isAirplaneModeOn(this.mContext);
        if (!isNetworkSupported || isAirplaneModeOn) {
            setText(null);
            setVisibility(8);
            return;
        }
        if (!this.mDemoMode) {
            updateText();
        }
    }

    /* access modifiers changed from: private */
    public void updateText() {
        CharSequence charSequence;
        int i = 0;
        List filteredSubscriptionInfo = this.mKeyguardUpdateMonitor.getFilteredSubscriptionInfo(false);
        int size = filteredSubscriptionInfo.size();
        while (true) {
            if (i >= size) {
                charSequence = null;
                break;
            }
            int subscriptionId = ((SubscriptionInfo) filteredSubscriptionInfo.get(i)).getSubscriptionId();
            int simState = this.mKeyguardUpdateMonitor.getSimState(subscriptionId);
            charSequence = ((SubscriptionInfo) filteredSubscriptionInfo.get(i)).getCarrierName();
            if (!TextUtils.isEmpty(charSequence) && simState == 5) {
                ServiceState serviceState = this.mKeyguardUpdateMonitor.getServiceState(subscriptionId);
                if (serviceState != null && serviceState.getState() == 0) {
                    break;
                }
            }
            i++;
        }
        setText(charSequence);
    }
}
