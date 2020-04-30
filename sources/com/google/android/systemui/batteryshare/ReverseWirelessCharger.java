package com.google.android.systemui.batteryshare;

import android.content.Context;
import android.os.IHwBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import vendor.google.wireless_charger.V1_2.IWirelessCharger;
import vendor.google.wireless_charger.V1_2.IWirelessCharger.setRtxModeCallback;
import vendor.google.wireless_charger.V1_2.IWirelessChargerRtxStatusCallback.Stub;
import vendor.google.wireless_charger.V1_2.RtxStatusInfo;

public class ReverseWirelessCharger extends Stub implements DeathRecipient {
    private final LocalRtxModeCallback mLocalRtxModeCallback = new LocalRtxModeCallback();
    private final Object mLock = new Object();
    private final ArrayList<RtxModeCallback> mRtxModeCallbacks = new ArrayList<>();
    private final ArrayList<RtxStatusCallback> mRtxStatusCallbacks = new ArrayList<>();
    private IWirelessCharger mWirelessCharger;

    class LocalRtxModeCallback implements setRtxModeCallback {
        LocalRtxModeCallback() {
        }

        public void onValues(byte b, RtxStatusInfo rtxStatusInfo) {
            ReverseWirelessCharger.this.dispatchRtxModeCallbacks(rtxStatusInfo);
        }
    }

    public interface RtxModeCallback {
        void onRtxModeChanged(RtxStatusInfo rtxStatusInfo);
    }

    public interface RtxStatusCallback {
        void onRtxStatusChanged(RtxStatusInfo rtxStatusInfo);
    }

    public ReverseWirelessCharger(Context context) {
        new ArrayList();
    }

    public void serviceDied(long j) {
        Log.i("ReverseWirelessCharger", "serviceDied");
        this.mWirelessCharger = null;
    }

    private void initHALInterface() {
        if (this.mWirelessCharger == null) {
            try {
                IWirelessCharger service = IWirelessCharger.getService();
                this.mWirelessCharger = service;
                service.linkToDeath(this, 0);
                this.mWirelessCharger.registerRtxCallback(this);
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("no wireless charger hal found: ");
                sb.append(e.getMessage());
                Log.i("ReverseWirelessCharger", sb.toString(), e);
                this.mWirelessCharger = null;
            }
        }
    }

    public boolean isRtxSupported() {
        initHALInterface();
        IWirelessCharger iWirelessCharger = this.mWirelessCharger;
        if (iWirelessCharger != null) {
            try {
                return iWirelessCharger.isRtxSupported();
            } catch (Exception e) {
                Log.i("ReverseWirelessCharger", "isRtxSupported fail: ", e);
            }
        }
        return false;
    }

    public void setRtxMode(boolean z) {
        initHALInterface();
        IWirelessCharger iWirelessCharger = this.mWirelessCharger;
        if (iWirelessCharger != null) {
            try {
                iWirelessCharger.setRtxMode(z, this.mLocalRtxModeCallback);
            } catch (Exception e) {
                Log.i("ReverseWirelessCharger", "setRtxMode fail: ", e);
            }
        }
    }

    public void addRtxModeCallback(RtxModeCallback rtxModeCallback) {
        synchronized (this.mLock) {
            this.mRtxModeCallbacks.add(rtxModeCallback);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchRtxModeCallbacks(RtxStatusInfo rtxStatusInfo) {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mRtxModeCallbacks);
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((RtxModeCallback) it.next()).onRtxModeChanged(rtxStatusInfo);
        }
    }

    public boolean isRtxModeOn() {
        initHALInterface();
        IWirelessCharger iWirelessCharger = this.mWirelessCharger;
        if (iWirelessCharger != null) {
            try {
                return iWirelessCharger.isRtxModeOn();
            } catch (Exception e) {
                Log.i("ReverseWirelessCharger", "isRtxModeOn fail: ", e);
            }
        }
        return false;
    }

    public void addRtxStatusCallback(RtxStatusCallback rtxStatusCallback) {
        synchronized (this.mLock) {
            this.mRtxStatusCallbacks.add(rtxStatusCallback);
        }
    }

    private void dispatchRtxStatusCallbacks(RtxStatusInfo rtxStatusInfo) {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mRtxStatusCallbacks);
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((RtxStatusCallback) it.next()).onRtxStatusChanged(rtxStatusInfo);
        }
    }

    public void rtxStatusInfoChanged(RtxStatusInfo rtxStatusInfo) throws RemoteException {
        dispatchRtxStatusCallbacks(rtxStatusInfo);
    }
}
