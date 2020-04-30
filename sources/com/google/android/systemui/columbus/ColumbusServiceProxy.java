package com.google.android.systemui.columbus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ColumbusServiceProxy.kt */
public final class ColumbusServiceProxy extends Service {
    private final ColumbusServiceProxy$binder$1 binder = new ColumbusServiceProxy$binder$1(this);
    /* access modifiers changed from: private */
    public final List<ColumbusServiceListener> columbusServiceListeners = new ArrayList();

    /* compiled from: ColumbusServiceProxy.kt */
    private static final class ColumbusServiceListener implements DeathRecipient {
        private IColumbusServiceListener listener;
        private IBinder token;

        public ColumbusServiceListener(IBinder iBinder, IColumbusServiceListener iColumbusServiceListener) {
            this.token = iBinder;
            this.listener = iColumbusServiceListener;
            linkToDeath();
        }

        public final IBinder getToken() {
            return this.token;
        }

        public final IColumbusServiceListener getListener() {
            return this.listener;
        }

        private final void linkToDeath() {
            IBinder iBinder = this.token;
            if (iBinder != null) {
                try {
                    iBinder.linkToDeath(this, 0);
                    Unit unit = Unit.INSTANCE;
                } catch (RemoteException e) {
                    Log.e("Columbus/ColumbusServiceProxy", "Unable to linkToDeath", e);
                }
            }
        }

        public final Boolean unlinkToDeath() {
            IBinder iBinder = this.token;
            if (iBinder != null) {
                return Boolean.valueOf(iBinder.unlinkToDeath(this, 0));
            }
            return null;
        }

        public void binderDied() {
            Log.w("Columbus/ColumbusServiceProxy", "ColumbusServiceListener binder died");
            this.token = null;
            this.listener = null;
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        return 0;
    }

    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    /* access modifiers changed from: private */
    public final void checkPermission() {
        enforceCallingOrSelfPermission("com.google.android.columbus.permission.CONFIGURE_COLUMBUS_GESTURE", "Must have com.google.android.columbus.permission.CONFIGURE_COLUMBUS_GESTURE permission");
    }
}
