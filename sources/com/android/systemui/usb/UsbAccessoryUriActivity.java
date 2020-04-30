package com.android.systemui.usb;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;
import com.android.systemui.C2017R$string;

public class UsbAccessoryUriActivity extends AlertActivity implements OnClickListener {
    private UsbAccessory mAccessory;
    private Uri mUri;

    public void onCreate(Bundle bundle) {
        Uri uri;
        UsbAccessoryUriActivity.super.onCreate(bundle);
        Intent intent = getIntent();
        this.mAccessory = (UsbAccessory) intent.getParcelableExtra("accessory");
        String stringExtra = intent.getStringExtra("uri");
        if (stringExtra == null) {
            uri = null;
        } else {
            uri = Uri.parse(stringExtra);
        }
        this.mUri = uri;
        String str = "UsbAccessoryUriActivity";
        if (uri == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("could not parse Uri ");
            sb.append(stringExtra);
            Log.e(str, sb.toString());
            finish();
            return;
        }
        String scheme = uri.getScheme();
        if ("http".equals(scheme) || "https".equals(scheme)) {
            AlertParams alertParams = this.mAlertParams;
            String description = this.mAccessory.getDescription();
            alertParams.mTitle = description;
            if (description == null || description.length() == 0) {
                alertParams.mTitle = getString(C2017R$string.title_usb_accessory);
            }
            alertParams.mMessage = getString(C2017R$string.usb_accessory_uri_prompt, new Object[]{this.mUri});
            alertParams.mPositiveButtonText = getString(C2017R$string.label_view);
            alertParams.mNegativeButtonText = getString(17039360);
            alertParams.mPositiveButtonListener = this;
            alertParams.mNegativeButtonListener = this;
            setupAlert();
            return;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Uri not http or https: ");
        sb2.append(this.mUri);
        Log.e(str, sb2.toString());
        finish();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            Intent intent = new Intent("android.intent.action.VIEW", this.mUri);
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.addFlags(268435456);
            try {
                startActivityAsUser(intent, UserHandle.CURRENT);
            } catch (ActivityNotFoundException unused) {
                StringBuilder sb = new StringBuilder();
                sb.append("startActivity failed for ");
                sb.append(this.mUri);
                Log.e("UsbAccessoryUriActivity", sb.toString());
            }
        }
        finish();
    }
}
