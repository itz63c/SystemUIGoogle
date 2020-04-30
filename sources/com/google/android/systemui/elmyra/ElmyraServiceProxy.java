package com.google.android.systemui.elmyra;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.systemui.elmyra.IElmyraService.Stub;
import java.util.ArrayList;
import java.util.List;

public class ElmyraServiceProxy extends Service {
    private final Stub mBinder = new Stub() {
        public void registerGestureListener(IBinder iBinder, IBinder iBinder2) {
            ElmyraServiceProxy.this.checkPermission();
            try {
                for (int size = ElmyraServiceProxy.this.mElmyraServiceListeners.size() - 1; size >= 0; size--) {
                    IElmyraServiceListener listener = ((ElmyraServiceListener) ElmyraServiceProxy.this.mElmyraServiceListeners.get(size)).getListener();
                    if (listener == null) {
                        ElmyraServiceProxy.this.mElmyraServiceListeners.remove(size);
                    } else {
                        listener.setListener(iBinder, iBinder2);
                    }
                }
            } catch (RemoteException e) {
                Log.e("Elmyra/ElmyraServiceProxy", "Action isn't connected", e);
            }
        }

        public void triggerAction() {
            ElmyraServiceProxy.this.checkPermission();
            try {
                for (int size = ElmyraServiceProxy.this.mElmyraServiceListeners.size() - 1; size >= 0; size--) {
                    IElmyraServiceListener listener = ((ElmyraServiceListener) ElmyraServiceProxy.this.mElmyraServiceListeners.get(size)).getListener();
                    if (listener == null) {
                        ElmyraServiceProxy.this.mElmyraServiceListeners.remove(size);
                    } else {
                        listener.triggerAction();
                    }
                }
            } catch (RemoteException e) {
                Log.e("Elmyra/ElmyraServiceProxy", "Error launching assistant", e);
            }
        }

        public void registerServiceListener(IBinder iBinder, IBinder iBinder2) {
            ElmyraServiceProxy.this.checkPermission();
            if (iBinder == null) {
                Log.e("Elmyra/ElmyraServiceProxy", "Binder token must not be null");
                return;
            }
            if (iBinder2 == null) {
                int i = 0;
                while (true) {
                    if (i >= ElmyraServiceProxy.this.mElmyraServiceListeners.size()) {
                        break;
                    } else if (iBinder.equals(((ElmyraServiceListener) ElmyraServiceProxy.this.mElmyraServiceListeners.get(i)).getToken())) {
                        ((ElmyraServiceListener) ElmyraServiceProxy.this.mElmyraServiceListeners.get(i)).unlinkToDeath();
                        ElmyraServiceProxy.this.mElmyraServiceListeners.remove(i);
                        break;
                    } else {
                        i++;
                    }
                }
            } else {
                ElmyraServiceProxy.this.mElmyraServiceListeners.add(new ElmyraServiceListener(ElmyraServiceProxy.this, iBinder, IElmyraServiceListener.Stub.asInterface(iBinder2)));
            }
        }
    };
    /* access modifiers changed from: private */
    public final List<ElmyraServiceListener> mElmyraServiceListeners = new ArrayList();

    private class ElmyraServiceListener implements DeathRecipient {
        private IElmyraServiceListener mListener;
        private IBinder mToken;

        ElmyraServiceListener(ElmyraServiceProxy elmyraServiceProxy, IBinder iBinder, IElmyraServiceListener iElmyraServiceListener) {
            this.mToken = iBinder;
            this.mListener = iElmyraServiceListener;
            linkToDeath();
        }

        public IElmyraServiceListener getListener() {
            return this.mListener;
        }

        public IBinder getToken() {
            return this.mToken;
        }

        private void linkToDeath() {
            IBinder iBinder = this.mToken;
            if (iBinder != null) {
                try {
                    iBinder.linkToDeath(this, 0);
                } catch (RemoteException e) {
                    Log.e("Elmyra/ElmyraServiceProxy", "Unable to linkToDeath", e);
                }
            }
        }

        public void unlinkToDeath() {
            IBinder iBinder = this.mToken;
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
            }
        }

        public void binderDied() {
            Log.w("Elmyra/ElmyraServiceProxy", "ElmyraServiceListener binder died");
            this.mToken = null;
            this.mListener = null;
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return 0;
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    /* access modifiers changed from: private */
    public void checkPermission() {
        enforceCallingOrSelfPermission("com.google.android.elmyra.permission.CONFIGURE_ASSIST_GESTURE", "Must have com.google.android.elmyra.permission.CONFIGURE_ASSIST_GESTURE permission");
    }
}
