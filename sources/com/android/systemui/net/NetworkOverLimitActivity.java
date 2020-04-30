package com.android.systemui.net;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.net.INetworkPolicyManager.Stub;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.android.systemui.C2017R$string;

public class NetworkOverLimitActivity extends Activity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        final NetworkTemplate parcelableExtra = getIntent().getParcelableExtra("android.net.NETWORK_TEMPLATE");
        Builder builder = new Builder(this);
        builder.setTitle(getLimitedDialogTitleForTemplate(parcelableExtra));
        builder.setMessage(C2017R$string.data_usage_disabled_dialog);
        builder.setPositiveButton(17039370, null);
        builder.setNegativeButton(C2017R$string.data_usage_disabled_dialog_enable, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                NetworkOverLimitActivity.this.snoozePolicy(parcelableExtra);
            }
        });
        AlertDialog create = builder.create();
        create.getWindow().setType(2003);
        create.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                NetworkOverLimitActivity.this.finish();
            }
        });
        create.show();
    }

    /* access modifiers changed from: private */
    public void snoozePolicy(NetworkTemplate networkTemplate) {
        try {
            Stub.asInterface(ServiceManager.getService("netpolicy")).snoozeLimit(networkTemplate);
        } catch (RemoteException e) {
            Log.w("NetworkOverLimitActivity", "problem snoozing network policy", e);
        }
    }

    private static int getLimitedDialogTitleForTemplate(NetworkTemplate networkTemplate) {
        if (networkTemplate.getMatchRule() != 1) {
            return C2017R$string.data_usage_disabled_dialog_title;
        }
        return C2017R$string.data_usage_disabled_dialog_mobile_title;
    }
}
