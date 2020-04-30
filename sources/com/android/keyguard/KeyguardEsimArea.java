package com.android.keyguard;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.euicc.EuiccManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.android.systemui.C2017R$string;

class KeyguardEsimArea extends Button implements OnClickListener {
    private EuiccManager mEuiccManager;
    private BroadcastReceiver mReceiver;

    public KeyguardEsimArea(Context context) {
        this(context, null);
    }

    public KeyguardEsimArea(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KeyguardEsimArea(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 16974425);
    }

    public KeyguardEsimArea(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("com.android.keyguard.disable_esim".equals(intent.getAction())) {
                    int resultCode = getResultCode();
                    if (resultCode != 0) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Error disabling esim, result code = ");
                        sb.append(resultCode);
                        Log.e("KeyguardEsimArea", sb.toString());
                        AlertDialog create = new Builder(KeyguardEsimArea.this.mContext).setMessage(C2017R$string.error_disable_esim_msg).setTitle(C2017R$string.error_disable_esim_title).setCancelable(false).setPositiveButton(C2017R$string.f31ok, null).create();
                        create.getWindow().setType(2009);
                        create.show();
                    }
                }
            }
        };
        this.mEuiccManager = (EuiccManager) context.getSystemService("euicc");
        setOnClickListener(this);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("com.android.keyguard.disable_esim"), "com.android.systemui.permission.SELF", null);
    }

    public static boolean isEsimLocked(Context context, int i) {
        boolean z = false;
        if (!((EuiccManager) context.getSystemService("euicc")).isEnabled()) {
            return false;
        }
        SubscriptionInfo activeSubscriptionInfo = SubscriptionManager.from(context).getActiveSubscriptionInfo(i);
        if (activeSubscriptionInfo != null && activeSubscriptionInfo.isEmbedded()) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.mContext.unregisterReceiver(this.mReceiver);
        super.onDetachedFromWindow();
    }

    public void onClick(View view) {
        Intent intent = new Intent("com.android.keyguard.disable_esim");
        intent.setPackage(this.mContext.getPackageName());
        this.mEuiccManager.switchToSubscription(-1, PendingIntent.getBroadcastAsUser(this.mContext, 0, intent, 134217728, UserHandle.SYSTEM));
    }
}
