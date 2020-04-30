package com.android.settingslib.deviceinfo;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public abstract class AbstractConnectivityPreferenceController extends AbstractPreferenceController implements LifecycleObserver, OnStart, OnStop {
    private final BroadcastReceiver mConnectivityReceiver;

    /* access modifiers changed from: protected */
    public abstract String[] getConnectivityIntents();

    public void onStop() {
        this.mContext.unregisterReceiver(this.mConnectivityReceiver);
    }

    public void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        for (String addAction : getConnectivityIntents()) {
            intentFilter.addAction(addAction);
        }
        this.mContext.registerReceiver(this.mConnectivityReceiver, intentFilter, "android.permission.CHANGE_NETWORK_STATE", null);
    }
}
