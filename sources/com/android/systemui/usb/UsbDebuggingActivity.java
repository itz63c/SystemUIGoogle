package com.android.systemui.usb;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.debug.IAdbManager;
import android.debug.IAdbManager.Stub;
import android.os.Bundle;
import android.os.ServiceManager;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.Toast;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;
import com.android.systemui.C2017R$string;

public class UsbDebuggingActivity extends AlertActivity implements OnClickListener {
    private CheckBox mAlwaysAllow;
    private String mKey;

    public void onCreate(Bundle bundle) {
        Window window = getWindow();
        window.addSystemFlags(524288);
        window.setType(2008);
        UsbDebuggingActivity.super.onCreate(bundle);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("fingerprints");
        String stringExtra2 = intent.getStringExtra("key");
        this.mKey = stringExtra2;
        if (stringExtra == null || stringExtra2 == null) {
            finish();
            return;
        }
        AlertParams alertParams = this.mAlertParams;
        alertParams.mTitle = getString(C2017R$string.usb_debugging_title);
        alertParams.mMessage = getString(C2017R$string.usb_debugging_message, new Object[]{stringExtra});
        alertParams.mPositiveButtonText = getString(C2017R$string.usb_debugging_allow);
        alertParams.mNegativeButtonText = getString(17039360);
        alertParams.mPositiveButtonListener = this;
        alertParams.mNegativeButtonListener = this;
        View inflate = LayoutInflater.from(alertParams.mContext).inflate(17367090, null);
        CheckBox checkBox = (CheckBox) inflate.findViewById(16908744);
        this.mAlwaysAllow = checkBox;
        checkBox.setText(getString(C2017R$string.usb_debugging_always));
        alertParams.mView = inflate;
        window.setCloseOnTouchOutside(false);
        setupAlert();
        this.mAlert.getButton(-1).setOnTouchListener($$Lambda$UsbDebuggingActivity$XWtqGCtWBJlTLnAvCSF7AuSg8.INSTANCE);
    }

    static /* synthetic */ boolean lambda$onCreate$0(View view, MotionEvent motionEvent) {
        if ((motionEvent.getFlags() & 1) == 0 && (motionEvent.getFlags() & 2) == 0) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            EventLog.writeEvent(1397638484, "62187985");
            Toast.makeText(view.getContext(), C2017R$string.touch_filtered_warning, 0).show();
        }
        return true;
    }

    public void onWindowAttributesChanged(LayoutParams layoutParams) {
        UsbDebuggingActivity.super.onWindowAttributesChanged(layoutParams);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        boolean z = true;
        boolean z2 = i == -1;
        if (!z2 || !this.mAlwaysAllow.isChecked()) {
            z = false;
        }
        try {
            IAdbManager asInterface = Stub.asInterface(ServiceManager.getService("adb"));
            if (z2) {
                asInterface.allowDebugging(z, this.mKey);
            } else {
                asInterface.denyDebugging();
            }
        } catch (Exception e) {
            Log.e("UsbDebuggingActivity", "Unable to notify Usb service", e);
        }
        finish();
    }
}
