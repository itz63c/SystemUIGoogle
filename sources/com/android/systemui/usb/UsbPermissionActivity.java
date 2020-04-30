package com.android.systemui.usb;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.PermissionChecker;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.IUsbManager.Stub;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;
import com.android.systemui.C2017R$string;

public class UsbPermissionActivity extends AlertActivity implements OnClickListener, OnCheckedChangeListener {
    private UsbAccessory mAccessory;
    private CheckBox mAlwaysUse;
    private TextView mClearDefaultHint;
    private UsbDevice mDevice;
    private UsbDisconnectedReceiver mDisconnectedReceiver;
    private String mPackageName;
    private PendingIntent mPendingIntent;
    private boolean mPermissionGranted;
    private int mUid;

    /* JADX WARNING: type inference failed for: r9v0, types: [android.content.DialogInterface$OnClickListener, android.content.Context, com.android.internal.app.AlertActivity, com.android.systemui.usb.UsbPermissionActivity, android.widget.CompoundButton$OnCheckedChangeListener, android.app.Activity] */
    public void onCreate(Bundle bundle) {
        boolean z;
        int i;
        UsbPermissionActivity.super.onCreate(bundle);
        Intent intent = getIntent();
        this.mDevice = (UsbDevice) intent.getParcelableExtra("device");
        this.mAccessory = (UsbAccessory) intent.getParcelableExtra("accessory");
        this.mPendingIntent = (PendingIntent) intent.getParcelableExtra("android.intent.extra.INTENT");
        this.mUid = intent.getIntExtra("android.intent.extra.UID", -1);
        this.mPackageName = intent.getStringExtra("android.hardware.usb.extra.PACKAGE");
        boolean booleanExtra = intent.getBooleanExtra("android.hardware.usb.extra.CAN_BE_DEFAULT", false);
        PackageManager packageManager = getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.mPackageName, 0);
            String charSequence = applicationInfo.loadLabel(packageManager).toString();
            AlertParams alertParams = this.mAlertParams;
            alertParams.mTitle = charSequence;
            if (this.mDevice == null) {
                alertParams.mMessage = getString(C2017R$string.usb_accessory_permission_prompt, new Object[]{charSequence, this.mAccessory.getDescription()});
                this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity) this, this.mAccessory);
                z = false;
            } else {
                z = this.mDevice.getHasAudioCapture() && !(PermissionChecker.checkPermissionForPreflight(this, "android.permission.RECORD_AUDIO", -1, applicationInfo.uid, this.mPackageName) == 0);
                if (z) {
                    i = C2017R$string.usb_device_permission_prompt_warn;
                } else {
                    i = C2017R$string.usb_device_permission_prompt;
                }
                alertParams.mMessage = getString(i, new Object[]{charSequence, this.mDevice.getProductName()});
                this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity) this, this.mDevice);
            }
            alertParams.mPositiveButtonText = getString(17039370);
            alertParams.mNegativeButtonText = getString(17039360);
            alertParams.mPositiveButtonListener = this;
            alertParams.mNegativeButtonListener = this;
            if (!z && booleanExtra && !(this.mDevice == null && this.mAccessory == null)) {
                View inflate = ((LayoutInflater) getSystemService("layout_inflater")).inflate(17367090, null);
                alertParams.mView = inflate;
                CheckBox checkBox = (CheckBox) inflate.findViewById(16908744);
                this.mAlwaysUse = checkBox;
                UsbDevice usbDevice = this.mDevice;
                if (usbDevice == null) {
                    checkBox.setText(getString(C2017R$string.always_use_accessory, new Object[]{charSequence, this.mAccessory.getDescription()}));
                } else {
                    checkBox.setText(getString(C2017R$string.always_use_device, new Object[]{charSequence, usbDevice.getProductName()}));
                }
                this.mAlwaysUse.setOnCheckedChangeListener(this);
                TextView textView = (TextView) alertParams.mView.findViewById(16908834);
                this.mClearDefaultHint = textView;
                textView.setVisibility(8);
            }
            setupAlert();
        } catch (NameNotFoundException e) {
            Log.e("UsbPermissionActivity", "unable to look up package name", e);
            finish();
        }
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, com.android.internal.app.AlertActivity, com.android.systemui.usb.UsbPermissionActivity] */
    public void onDestroy() {
        String str = "UsbPermissionActivity";
        IUsbManager asInterface = Stub.asInterface(ServiceManager.getService("usb"));
        Intent intent = new Intent();
        try {
            if (this.mDevice != null) {
                intent.putExtra("device", this.mDevice);
                if (this.mPermissionGranted) {
                    asInterface.grantDevicePermission(this.mDevice, this.mUid);
                    if (this.mAlwaysUse != null && this.mAlwaysUse.isChecked()) {
                        asInterface.setDevicePackage(this.mDevice, this.mPackageName, UserHandle.getUserId(this.mUid));
                    }
                }
            }
            if (this.mAccessory != null) {
                intent.putExtra("accessory", this.mAccessory);
                if (this.mPermissionGranted) {
                    asInterface.grantAccessoryPermission(this.mAccessory, this.mUid);
                    if (this.mAlwaysUse != null && this.mAlwaysUse.isChecked()) {
                        asInterface.setAccessoryPackage(this.mAccessory, this.mPackageName, UserHandle.getUserId(this.mUid));
                    }
                }
            }
            intent.putExtra("permission", this.mPermissionGranted);
            this.mPendingIntent.send(this, 0, intent);
        } catch (CanceledException unused) {
            Log.w(str, "PendingIntent was cancelled");
        } catch (RemoteException e) {
            Log.e(str, "IUsbService connection failed", e);
        }
        UsbDisconnectedReceiver usbDisconnectedReceiver = this.mDisconnectedReceiver;
        if (usbDisconnectedReceiver != null) {
            unregisterReceiver(usbDisconnectedReceiver);
        }
        UsbPermissionActivity.super.onDestroy();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            this.mPermissionGranted = true;
        }
        finish();
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        TextView textView = this.mClearDefaultHint;
        if (textView != null) {
            if (z) {
                textView.setVisibility(0);
            } else {
                textView.setVisibility(8);
            }
        }
    }
}
