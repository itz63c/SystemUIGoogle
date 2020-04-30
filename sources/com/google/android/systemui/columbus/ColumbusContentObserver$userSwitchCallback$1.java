package com.google.android.systemui.columbus;

import android.app.SynchronousUserSwitchObserver;
import android.os.RemoteException;

/* compiled from: ColumbusContentObserver.kt */
public final class ColumbusContentObserver$userSwitchCallback$1 extends SynchronousUserSwitchObserver {
    final /* synthetic */ ColumbusContentObserver this$0;

    ColumbusContentObserver$userSwitchCallback$1(ColumbusContentObserver columbusContentObserver) {
        this.this$0 = columbusContentObserver;
    }

    public void onUserSwitching(int i) throws RemoteException {
        this.this$0.updateContentObserver();
        this.this$0.callback.invoke(this.this$0.settingsUri);
    }
}
