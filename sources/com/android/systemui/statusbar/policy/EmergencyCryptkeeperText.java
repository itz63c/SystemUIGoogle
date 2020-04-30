package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.provider.Settings.Global;
import android.telephony.SubscriptionInfo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;
import java.util.List;

public class EmergencyCryptkeeperText extends TextView {
    private final KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback() {
        public void onPhoneStateChanged(int i) {
            EmergencyCryptkeeperText.this.update();
        }

        public void onRefreshCarrierInfo() {
            EmergencyCryptkeeperText.this.update();
        }
    };
    private KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.AIRPLANE_MODE".equals(intent.getAction())) {
                EmergencyCryptkeeperText.this.update();
            }
        }
    };

    private boolean iccCardExist(int i) {
        return i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 9 || i == 10;
    }

    public EmergencyCryptkeeperText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setVisibility(8);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        KeyguardUpdateMonitor keyguardUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        keyguardUpdateMonitor.registerCallback(this.mCallback);
        getContext().registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.AIRPLANE_MODE"));
        update();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KeyguardUpdateMonitor keyguardUpdateMonitor = this.mKeyguardUpdateMonitor;
        if (keyguardUpdateMonitor != null) {
            keyguardUpdateMonitor.removeCallback(this.mCallback);
        }
        getContext().unregisterReceiver(this.mReceiver);
    }

    public void update() {
        int i = 0;
        boolean isNetworkSupported = ConnectivityManager.from(this.mContext).isNetworkSupported(0);
        boolean z = true;
        boolean z2 = Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
        if (!isNetworkSupported || z2) {
            setText(null);
            setVisibility(8);
            return;
        }
        List filteredSubscriptionInfo = this.mKeyguardUpdateMonitor.getFilteredSubscriptionInfo(false);
        int size = filteredSubscriptionInfo.size();
        CharSequence charSequence = null;
        for (int i2 = 0; i2 < size; i2++) {
            int simState = this.mKeyguardUpdateMonitor.getSimState(((SubscriptionInfo) filteredSubscriptionInfo.get(i2)).getSubscriptionId());
            CharSequence carrierName = ((SubscriptionInfo) filteredSubscriptionInfo.get(i2)).getCarrierName();
            if (iccCardExist(simState) && !TextUtils.isEmpty(carrierName)) {
                z = false;
                charSequence = carrierName;
            }
        }
        if (z) {
            if (size != 0) {
                charSequence = ((SubscriptionInfo) filteredSubscriptionInfo.get(0)).getCarrierName();
            } else {
                charSequence = getContext().getText(17039980);
                Intent registerReceiver = getContext().registerReceiver(null, new IntentFilter("android.telephony.action.SERVICE_PROVIDERS_UPDATED"));
                if (registerReceiver != null) {
                    charSequence = registerReceiver.getStringExtra("android.telephony.extra.PLMN");
                }
            }
        }
        setText(charSequence);
        if (TextUtils.isEmpty(charSequence)) {
            i = 8;
        }
        setVisibility(i);
    }
}
