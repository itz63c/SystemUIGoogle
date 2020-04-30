package com.google.android.systemui.elmyra;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IElmyraService extends IInterface {

    public static abstract class Stub extends Binder implements IElmyraService {

        private static class Proxy implements IElmyraService {
            public static IElmyraService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void registerGestureListener(IBinder iBinder, IBinder iBinder2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraService");
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeStrongBinder(iBinder2);
                    if (this.mRemote.transact(1, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().registerGestureListener(iBinder, iBinder2);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void triggerAction() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraService");
                    if (this.mRemote.transact(2, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().triggerAction();
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void registerServiceListener(IBinder iBinder, IBinder iBinder2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraService");
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeStrongBinder(iBinder2);
                    if (this.mRemote.transact(3, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().registerServiceListener(iBinder, iBinder2);
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.google.android.systemui.elmyra.IElmyraService");
        }

        public static IElmyraService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.systemui.elmyra.IElmyraService");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IElmyraService)) {
                return new Proxy(iBinder);
            }
            return (IElmyraService) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = "com.google.android.systemui.elmyra.IElmyraService";
            if (i == 1) {
                parcel.enforceInterface(str);
                registerGestureListener(parcel.readStrongBinder(), parcel.readStrongBinder());
                return true;
            } else if (i == 2) {
                parcel.enforceInterface(str);
                triggerAction();
                return true;
            } else if (i == 3) {
                parcel.enforceInterface(str);
                registerServiceListener(parcel.readStrongBinder(), parcel.readStrongBinder());
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(str);
                return true;
            }
        }

        public static IElmyraService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void registerGestureListener(IBinder iBinder, IBinder iBinder2) throws RemoteException;

    void registerServiceListener(IBinder iBinder, IBinder iBinder2) throws RemoteException;

    void triggerAction() throws RemoteException;
}
