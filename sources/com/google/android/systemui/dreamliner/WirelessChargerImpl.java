package com.google.android.systemui.dreamliner;

import android.os.Handler;
import android.os.IHwBinder.DeathRecipient;
import android.os.Looper;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.google.android.systemui.dreamliner.WirelessCharger.AlignInfoListener;
import com.google.android.systemui.dreamliner.WirelessCharger.ChallengeCallback;
import com.google.android.systemui.dreamliner.WirelessCharger.GetInformationCallback;
import com.google.android.systemui.dreamliner.WirelessCharger.IsDockPresentCallback;
import com.google.android.systemui.dreamliner.WirelessCharger.KeyExchangeCallback;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import vendor.google.wireless_charger.V1_0.DockInfo;
import vendor.google.wireless_charger.V1_0.IWirelessCharger.challengeCallback;
import vendor.google.wireless_charger.V1_0.IWirelessCharger.getInformationCallback;
import vendor.google.wireless_charger.V1_0.IWirelessCharger.isDockPresentCallback;
import vendor.google.wireless_charger.V1_0.IWirelessCharger.keyExchangeCallback;
import vendor.google.wireless_charger.V1_0.KeyExchangeResponse;
import vendor.google.wireless_charger.V1_1.AlignInfo;
import vendor.google.wireless_charger.V1_1.IWirelessChargerInfoCallback.Stub;
import vendor.google.wireless_charger.V1_2.IWirelessCharger;

public class WirelessChargerImpl extends WirelessCharger implements DeathRecipient, isDockPresentCallback {
    private static final long MAX_POLLING_TIMEOUT_NS = TimeUnit.SECONDS.toNanos(5);
    private isDockPresentCallback mCallback;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private long mPollingStartedTimeNs;
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            WirelessChargerImpl wirelessChargerImpl = WirelessChargerImpl.this;
            wirelessChargerImpl.isDockPresentInternal(wirelessChargerImpl);
        }
    };
    private IWirelessCharger mWirelessCharger;

    final class ChallengeCallbackWrapper implements challengeCallback {
        private ChallengeCallback mCallback;

        public ChallengeCallbackWrapper(WirelessChargerImpl wirelessChargerImpl, ChallengeCallback challengeCallback) {
            this.mCallback = challengeCallback;
        }

        public void onValues(byte b, ArrayList<Byte> arrayList) {
            this.mCallback.onCallback(new Byte(b).intValue(), arrayList);
        }
    }

    final class GetInformationCallbackWrapper implements getInformationCallback {
        private GetInformationCallback mCallback;

        public GetInformationCallbackWrapper(WirelessChargerImpl wirelessChargerImpl, GetInformationCallback getInformationCallback) {
            this.mCallback = getInformationCallback;
        }

        public void onValues(byte b, DockInfo dockInfo) {
            this.mCallback.onCallback(new Byte(b).intValue(), convertDockInfo(dockInfo));
        }

        private DockInfo convertDockInfo(DockInfo dockInfo) {
            return new DockInfo(dockInfo.manufacturer, dockInfo.model, dockInfo.serial, new Byte(dockInfo.type).intValue());
        }
    }

    final class IsDockPresentCallbackWrapper implements isDockPresentCallback {
        private IsDockPresentCallback mCallback;

        public IsDockPresentCallbackWrapper(WirelessChargerImpl wirelessChargerImpl, IsDockPresentCallback isDockPresentCallback) {
            this.mCallback = isDockPresentCallback;
        }

        public void onValues(boolean z, byte b, byte b2, boolean z2, int i) {
            this.mCallback.onCallback(z, b, b2, z2, i);
        }
    }

    final class KeyExchangeCallbackWrapper implements keyExchangeCallback {
        private KeyExchangeCallback mCallback;

        public KeyExchangeCallbackWrapper(WirelessChargerImpl wirelessChargerImpl, KeyExchangeCallback keyExchangeCallback) {
            this.mCallback = keyExchangeCallback;
        }

        public void onValues(byte b, KeyExchangeResponse keyExchangeResponse) {
            if (keyExchangeResponse != null) {
                this.mCallback.onCallback(new Byte(b).intValue(), keyExchangeResponse.dockId, keyExchangeResponse.dockPublicKey);
            } else {
                this.mCallback.onCallback(new Byte(b).intValue(), -1, null);
            }
        }
    }

    final class WirelessChargerInfoCallback extends Stub {
        private AlignInfoListener mListener;

        public WirelessChargerInfoCallback(WirelessChargerImpl wirelessChargerImpl, AlignInfoListener alignInfoListener) {
            this.mListener = alignInfoListener;
        }

        public void alignInfoChanged(AlignInfo alignInfo) {
            this.mListener.onAlignInfoChanged(convertAlignInfo(alignInfo));
        }

        private DockAlignInfo convertAlignInfo(AlignInfo alignInfo) {
            return new DockAlignInfo(new Byte(alignInfo.alignState).intValue(), new Byte(alignInfo.alignPct).intValue());
        }
    }

    @VisibleForTesting(visibility = Visibility.PACKAGE)
    public void asyncIsDockPresent(IsDockPresentCallback isDockPresentCallback) {
        initHALInterface();
        if (this.mWirelessCharger != null) {
            this.mPollingStartedTimeNs = System.nanoTime();
            this.mCallback = new IsDockPresentCallbackWrapper(this, isDockPresentCallback);
            this.mHandler.removeCallbacks(this.mRunnable);
            this.mHandler.postDelayed(this.mRunnable, 100);
        }
    }

    @VisibleForTesting(visibility = Visibility.PACKAGE)
    public void getInformation(GetInformationCallback getInformationCallback) {
        initHALInterface();
        IWirelessCharger iWirelessCharger = this.mWirelessCharger;
        if (iWirelessCharger != null) {
            try {
                iWirelessCharger.getInformation(new GetInformationCallbackWrapper(this, getInformationCallback));
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("getInformation fail: ");
                sb.append(e.getMessage());
                Log.i("Dreamliner-WLC_HAL", sb.toString());
            }
        }
    }

    @VisibleForTesting(visibility = Visibility.PACKAGE)
    public void keyExchange(byte[] bArr, KeyExchangeCallback keyExchangeCallback) {
        initHALInterface();
        if (this.mWirelessCharger != null) {
            try {
                this.mWirelessCharger.keyExchange(convertPrimitiveArrayToArrayList(bArr), new KeyExchangeCallbackWrapper(this, keyExchangeCallback));
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("keyExchange fail: ");
                sb.append(e.getMessage());
                Log.i("Dreamliner-WLC_HAL", sb.toString());
            }
        }
    }

    @VisibleForTesting(visibility = Visibility.PACKAGE)
    public void challenge(byte b, byte[] bArr, ChallengeCallback challengeCallback) {
        initHALInterface();
        if (this.mWirelessCharger != null) {
            try {
                this.mWirelessCharger.challenge(b, convertPrimitiveArrayToArrayList(bArr), new ChallengeCallbackWrapper(this, challengeCallback));
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("challenge fail: ");
                sb.append(e.getMessage());
                Log.i("Dreamliner-WLC_HAL", sb.toString());
            }
        }
    }

    @VisibleForTesting(visibility = Visibility.PACKAGE)
    public void registerAlignInfo(AlignInfoListener alignInfoListener) {
        initHALInterface();
        IWirelessCharger iWirelessCharger = this.mWirelessCharger;
        if (iWirelessCharger != null) {
            try {
                iWirelessCharger.registerCallback(new WirelessChargerInfoCallback(this, alignInfoListener));
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("register alignInfo callback fail: ");
                sb.append(e.getMessage());
                Log.i("Dreamliner-WLC_HAL", sb.toString());
            }
        }
    }

    public void serviceDied(long j) {
        Log.i("Dreamliner-WLC_HAL", "serviceDied");
        this.mWirelessCharger = null;
    }

    private ArrayList<Byte> convertPrimitiveArrayToArrayList(byte[] bArr) {
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        ArrayList<Byte> arrayList = new ArrayList<>();
        for (byte valueOf : bArr) {
            arrayList.add(Byte.valueOf(valueOf));
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public void isDockPresentInternal(isDockPresentCallback isdockpresentcallback) {
        initHALInterface();
        IWirelessCharger iWirelessCharger = this.mWirelessCharger;
        if (iWirelessCharger != null) {
            try {
                iWirelessCharger.isDockPresent(isdockpresentcallback);
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("isDockPresent fail: ");
                sb.append(e.getMessage());
                Log.i("Dreamliner-WLC_HAL", sb.toString());
            }
        }
    }

    public void onValues(boolean z, byte b, byte b2, boolean z2, int i) {
        if (System.nanoTime() >= this.mPollingStartedTimeNs + MAX_POLLING_TIMEOUT_NS || i != 0) {
            isDockPresentCallback isdockpresentcallback = this.mCallback;
            if (isdockpresentcallback != null) {
                isdockpresentcallback.onValues(z, b, b2, z2, i);
                this.mCallback = null;
                return;
            }
            return;
        }
        this.mHandler.postDelayed(this.mRunnable, 100);
    }

    private void initHALInterface() {
        if (this.mWirelessCharger == null) {
            try {
                IWirelessCharger service = IWirelessCharger.getService();
                this.mWirelessCharger = service;
                service.linkToDeath(this, 0);
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("no wireless charger hal found: ");
                sb.append(e.getMessage());
                Log.i("Dreamliner-WLC_HAL", sb.toString());
                this.mWirelessCharger = null;
            }
        }
    }
}
