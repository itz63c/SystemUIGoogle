package vendor.google.wireless_charger.V1_2;

import android.os.HidlSupport;
import android.os.HwBinder;
import android.os.HwParcel;
import android.os.IHwBinder;
import android.os.IHwBinder.DeathRecipient;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import vendor.google.wireless_charger.V1_0.DockInfo;
import vendor.google.wireless_charger.V1_0.IWirelessCharger.challengeCallback;
import vendor.google.wireless_charger.V1_0.IWirelessCharger.getInformationCallback;
import vendor.google.wireless_charger.V1_0.IWirelessCharger.isDockPresentCallback;
import vendor.google.wireless_charger.V1_0.IWirelessCharger.keyExchangeCallback;
import vendor.google.wireless_charger.V1_0.KeyExchangeResponse;
import vendor.google.wireless_charger.V1_1.IWirelessChargerInfoCallback;

public interface IWirelessCharger extends vendor.google.wireless_charger.V1_1.IWirelessCharger {

    public static final class Proxy implements IWirelessCharger {
        private IHwBinder mRemote;

        public Proxy(IHwBinder iHwBinder) {
            Objects.requireNonNull(iHwBinder);
            this.mRemote = iHwBinder;
        }

        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(interfaceDescriptor());
                sb.append("@Proxy");
                return sb.toString();
            } catch (RemoteException unused) {
                return "[class or subclass of vendor.google.wireless_charger@1.2::IWirelessCharger]@Proxy";
            }
        }

        public final boolean equals(Object obj) {
            return HidlSupport.interfacesEqual(this, obj);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        public void isDockPresent(isDockPresentCallback isdockpresentcallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.0::IWirelessCharger");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(1, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                isdockpresentcallback.onValues(hwParcel2.readBool(), hwParcel2.readInt8(), hwParcel2.readInt8(), hwParcel2.readBool(), hwParcel2.readInt32());
            } finally {
                hwParcel2.release();
            }
        }

        public void getInformation(getInformationCallback getinformationcallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.0::IWirelessCharger");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(2, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                byte readInt8 = hwParcel2.readInt8();
                DockInfo dockInfo = new DockInfo();
                dockInfo.readFromParcel(hwParcel2);
                getinformationcallback.onValues(readInt8, dockInfo);
            } finally {
                hwParcel2.release();
            }
        }

        public void keyExchange(ArrayList<Byte> arrayList, keyExchangeCallback keyexchangecallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.0::IWirelessCharger");
            hwParcel.writeInt8Vector(arrayList);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(3, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                byte readInt8 = hwParcel2.readInt8();
                KeyExchangeResponse keyExchangeResponse = new KeyExchangeResponse();
                keyExchangeResponse.readFromParcel(hwParcel2);
                keyexchangecallback.onValues(readInt8, keyExchangeResponse);
            } finally {
                hwParcel2.release();
            }
        }

        public void challenge(byte b, ArrayList<Byte> arrayList, challengeCallback challengecallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.0::IWirelessCharger");
            hwParcel.writeInt8(b);
            hwParcel.writeInt8Vector(arrayList);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(4, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                challengecallback.onValues(hwParcel2.readInt8(), hwParcel2.readInt8Vector());
            } finally {
                hwParcel2.release();
            }
        }

        public byte registerCallback(IWirelessChargerInfoCallback iWirelessChargerInfoCallback) throws RemoteException {
            IHwBinder iHwBinder;
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.1::IWirelessCharger");
            if (iWirelessChargerInfoCallback == null) {
                iHwBinder = null;
            } else {
                iHwBinder = iWirelessChargerInfoCallback.asBinder();
            }
            hwParcel.writeStrongBinder(iHwBinder);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(12, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt8();
            } finally {
                hwParcel2.release();
            }
        }

        public byte registerRtxCallback(IWirelessChargerRtxStatusCallback iWirelessChargerRtxStatusCallback) throws RemoteException {
            IHwBinder iHwBinder;
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.2::IWirelessCharger");
            if (iWirelessChargerRtxStatusCallback == null) {
                iHwBinder = null;
            } else {
                iHwBinder = iWirelessChargerRtxStatusCallback.asBinder();
            }
            hwParcel.writeStrongBinder(iHwBinder);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(15, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt8();
            } finally {
                hwParcel2.release();
            }
        }

        public boolean isRtxSupported() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.2::IWirelessCharger");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(17, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readBool();
            } finally {
                hwParcel2.release();
            }
        }

        public boolean isRtxModeOn() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.2::IWirelessCharger");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(18, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readBool();
            } finally {
                hwParcel2.release();
            }
        }

        public void setRtxMode(boolean z, setRtxModeCallback setrtxmodecallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.2::IWirelessCharger");
            hwParcel.writeBool(z);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(20, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                byte readInt8 = hwParcel2.readInt8();
                RtxStatusInfo rtxStatusInfo = new RtxStatusInfo();
                rtxStatusInfo.readFromParcel(hwParcel2);
                setrtxmodecallback.onValues(readInt8, rtxStatusInfo);
            } finally {
                hwParcel2.release();
            }
        }

        public ArrayList<String> interfaceChain() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hidl.base@1.0::IBase");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(256067662, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readStringVector();
            } finally {
                hwParcel2.release();
            }
        }

        public String interfaceDescriptor() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hidl.base@1.0::IBase");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(256136003, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readString();
            } finally {
                hwParcel2.release();
            }
        }

        public boolean linkToDeath(DeathRecipient deathRecipient, long j) throws RemoteException {
            return this.mRemote.linkToDeath(deathRecipient, j);
        }
    }

    @FunctionalInterface
    public interface setRtxModeCallback {
        void onValues(byte b, RtxStatusInfo rtxStatusInfo);
    }

    ArrayList<String> interfaceChain() throws RemoteException;

    boolean isRtxModeOn() throws RemoteException;

    boolean isRtxSupported() throws RemoteException;

    boolean linkToDeath(DeathRecipient deathRecipient, long j) throws RemoteException;

    byte registerRtxCallback(IWirelessChargerRtxStatusCallback iWirelessChargerRtxStatusCallback) throws RemoteException;

    void setRtxMode(boolean z, setRtxModeCallback setrtxmodecallback) throws RemoteException;

    static IWirelessCharger asInterface(IHwBinder iHwBinder) {
        if (iHwBinder == null) {
            return null;
        }
        String str = "vendor.google.wireless_charger@1.2::IWirelessCharger";
        IWirelessCharger queryLocalInterface = iHwBinder.queryLocalInterface(str);
        if (queryLocalInterface != null && (queryLocalInterface instanceof IWirelessCharger)) {
            return queryLocalInterface;
        }
        Proxy proxy = new Proxy(iHwBinder);
        try {
            Iterator it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                if (((String) it.next()).equals(str)) {
                    return proxy;
                }
            }
        } catch (RemoteException unused) {
        }
        return null;
    }

    static IWirelessCharger getService(String str) throws RemoteException {
        return asInterface(HwBinder.getService("vendor.google.wireless_charger@1.2::IWirelessCharger", str));
    }

    static IWirelessCharger getService() throws RemoteException {
        return getService("default");
    }
}
