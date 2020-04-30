package com.android.systemui.assist;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IAssistHandleService extends IInterface {

    public static abstract class Stub extends Binder implements IAssistHandleService {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.android.systemui.assist.IAssistHandleService");
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = "com.android.systemui.assist.IAssistHandleService";
            if (i == 1) {
                parcel.enforceInterface(str);
                requestAssistHandles();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(str);
                return true;
            }
        }
    }

    void requestAssistHandles() throws RemoteException;
}
