package com.android.systemui.usb;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.IUsbManager.Stub;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.widget.CheckBox;
import com.android.internal.app.IntentForwarderActivity;
import com.android.internal.app.ResolverActivity;
import com.android.internal.app.chooser.TargetInfo;
import com.android.systemui.C2017R$string;
import java.util.ArrayList;
import java.util.Iterator;

public class UsbResolverActivity extends ResolverActivity {
    private UsbAccessory mAccessory;
    private UsbDevice mDevice;
    private UsbDisconnectedReceiver mDisconnectedReceiver;
    private ResolveInfo mForwardResolveInfo;
    private Intent mOtherProfileIntent;

    /* access modifiers changed from: protected */
    public boolean shouldShowTabs() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        boolean z;
        Intent intent = getIntent();
        Parcelable parcelableExtra = intent.getParcelableExtra("android.intent.extra.INTENT");
        String str = "UsbResolverActivity";
        if (!(parcelableExtra instanceof Intent)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Target is not an intent: ");
            sb.append(parcelableExtra);
            Log.w(str, sb.toString());
            finish();
            return;
        }
        Intent intent2 = (Intent) parcelableExtra;
        String str2 = "rlist";
        ArrayList arrayList = new ArrayList(intent.getParcelableArrayListExtra(str2));
        ArrayList arrayList2 = new ArrayList();
        this.mForwardResolveInfo = null;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ResolveInfo resolveInfo = (ResolveInfo) it.next();
            if (resolveInfo.getComponentInfo().name.equals(IntentForwarderActivity.FORWARD_INTENT_TO_MANAGED_PROFILE)) {
                this.mForwardResolveInfo = resolveInfo;
            } else if (UserHandle.getUserId(resolveInfo.activityInfo.applicationInfo.uid) != UserHandle.myUserId()) {
                it.remove();
                arrayList2.add(resolveInfo);
            }
        }
        String str3 = "device";
        UsbDevice usbDevice = (UsbDevice) intent2.getParcelableExtra(str3);
        this.mDevice = usbDevice;
        String str4 = "accessory";
        if (usbDevice != null) {
            this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity) this, this.mDevice);
            z = this.mDevice.getHasAudioCapture();
        } else {
            UsbAccessory usbAccessory = (UsbAccessory) intent2.getParcelableExtra(str4);
            this.mAccessory = usbAccessory;
            if (usbAccessory == null) {
                Log.e(str, "no device or accessory");
                finish();
                return;
            }
            this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity) this, this.mAccessory);
            z = false;
        }
        if (this.mForwardResolveInfo != null) {
            if (arrayList2.size() > 1) {
                Intent intent3 = new Intent(intent);
                this.mOtherProfileIntent = intent3;
                intent3.putParcelableArrayListExtra(str2, arrayList2);
            } else {
                Intent intent4 = new Intent(this, UsbConfirmActivity.class);
                this.mOtherProfileIntent = intent4;
                intent4.putExtra("rinfo", (Parcelable) arrayList2.get(0));
                UsbDevice usbDevice2 = this.mDevice;
                if (usbDevice2 != null) {
                    this.mOtherProfileIntent.putExtra(str3, usbDevice2);
                }
                UsbAccessory usbAccessory2 = this.mAccessory;
                if (usbAccessory2 != null) {
                    this.mOtherProfileIntent.putExtra(str4, usbAccessory2);
                }
            }
        }
        getIntent().putExtra("is_audio_capture_device", z);
        UsbResolverActivity.super.onCreate(bundle, intent2, getResources().getText(17039703), null, arrayList, true);
        CheckBox checkBox = (CheckBox) findViewById(16908744);
        if (checkBox != null) {
            if (this.mDevice == null) {
                checkBox.setText(C2017R$string.always_use_accessory);
            } else {
                checkBox.setText(C2017R$string.always_use_device);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        UsbDisconnectedReceiver usbDisconnectedReceiver = this.mDisconnectedReceiver;
        if (usbDisconnectedReceiver != null) {
            unregisterReceiver(usbDisconnectedReceiver);
        }
        UsbResolverActivity.super.onDestroy();
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [com.android.internal.app.ResolverActivity, com.android.systemui.usb.UsbResolverActivity, android.app.Activity] */
    /* access modifiers changed from: protected */
    public boolean onTargetSelected(TargetInfo targetInfo, boolean z) {
        String str = "UsbResolverActivity";
        ResolveInfo resolveInfo = targetInfo.getResolveInfo();
        ResolveInfo resolveInfo2 = this.mForwardResolveInfo;
        if (resolveInfo == resolveInfo2) {
            startActivityAsUser(this.mOtherProfileIntent, null, UserHandle.of(resolveInfo2.targetUserId));
            return true;
        }
        try {
            IUsbManager asInterface = Stub.asInterface(ServiceManager.getService("usb"));
            int i = resolveInfo.activityInfo.applicationInfo.uid;
            int myUserId = UserHandle.myUserId();
            if (this.mDevice != null) {
                asInterface.grantDevicePermission(this.mDevice, i);
                if (z) {
                    asInterface.setDevicePackage(this.mDevice, resolveInfo.activityInfo.packageName, myUserId);
                } else {
                    asInterface.setDevicePackage(this.mDevice, null, myUserId);
                }
            } else if (this.mAccessory != null) {
                asInterface.grantAccessoryPermission(this.mAccessory, i);
                if (z) {
                    asInterface.setAccessoryPackage(this.mAccessory, resolveInfo.activityInfo.packageName, myUserId);
                } else {
                    asInterface.setAccessoryPackage(this.mAccessory, null, myUserId);
                }
            }
            try {
                targetInfo.startAsUser(this, null, UserHandle.of(myUserId));
            } catch (ActivityNotFoundException e) {
                Log.e(str, "startActivity failed", e);
            }
        } catch (RemoteException e2) {
            Log.e(str, "onIntentSelected failed", e2);
        }
        return true;
    }
}
