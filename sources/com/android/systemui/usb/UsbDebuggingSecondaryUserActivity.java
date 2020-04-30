package com.android.systemui.usb;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;
import com.android.systemui.C2017R$string;

public class UsbDebuggingSecondaryUserActivity extends AlertActivity implements OnClickListener {
    public void onCreate(Bundle bundle) {
        UsbDebuggingSecondaryUserActivity.super.onCreate(bundle);
        AlertParams alertParams = this.mAlertParams;
        alertParams.mTitle = getString(C2017R$string.usb_debugging_secondary_user_title);
        alertParams.mMessage = getString(C2017R$string.usb_debugging_secondary_user_message);
        alertParams.mPositiveButtonText = getString(17039370);
        alertParams.mPositiveButtonListener = this;
        setupAlert();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        finish();
    }
}
