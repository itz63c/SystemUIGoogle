package com.android.systemui.usb;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbPort;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;

public class UsbContaminantActivity extends Activity implements OnClickListener {
    private TextView mEnableUsb;
    private TextView mGotIt;
    private TextView mLearnMore;
    private TextView mMessage;
    private TextView mTitle;
    private UsbPort mUsbPort;

    public void onCreate(Bundle bundle) {
        Window window = getWindow();
        window.addSystemFlags(524288);
        window.setType(2008);
        requestWindowFeature(1);
        super.onCreate(bundle);
        setContentView(C2013R$layout.contaminant_dialog);
        this.mUsbPort = getIntent().getParcelableExtra("port").getUsbPort((UsbManager) getSystemService(UsbManager.class));
        this.mLearnMore = (TextView) findViewById(C2011R$id.learnMore);
        this.mEnableUsb = (TextView) findViewById(C2011R$id.enableUsb);
        this.mGotIt = (TextView) findViewById(C2011R$id.gotIt);
        this.mTitle = (TextView) findViewById(C2011R$id.title);
        this.mMessage = (TextView) findViewById(C2011R$id.message);
        this.mTitle.setText(getString(C2017R$string.usb_contaminant_title));
        this.mMessage.setText(getString(C2017R$string.usb_contaminant_message));
        this.mEnableUsb.setText(getString(C2017R$string.usb_disable_contaminant_detection));
        this.mGotIt.setText(getString(C2017R$string.got_it));
        this.mLearnMore.setText(getString(C2017R$string.learn_more));
        this.mEnableUsb.setOnClickListener(this);
        this.mGotIt.setOnClickListener(this);
        this.mLearnMore.setOnClickListener(this);
    }

    public void onWindowAttributesChanged(LayoutParams layoutParams) {
        super.onWindowAttributesChanged(layoutParams);
    }

    public void onClick(View view) {
        if (view == this.mEnableUsb) {
            try {
                this.mUsbPort.enableContaminantDetection(false);
                Toast.makeText(this, C2017R$string.usb_port_enabled, 0).show();
            } catch (Exception e) {
                Log.e("UsbContaminantActivity", "Unable to notify Usb service", e);
            }
        } else if (view == this.mLearnMore) {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.HelpTrampoline");
            intent.putExtra("android.intent.extra.TEXT", "help_url_usb_contaminant_detected");
            startActivity(intent);
        }
        finish();
    }
}
