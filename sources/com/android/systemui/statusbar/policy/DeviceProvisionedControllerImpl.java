package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.util.Log;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener;
import java.util.ArrayList;

public class DeviceProvisionedControllerImpl extends CurrentUserTracker implements DeviceProvisionedController {
    protected static final String TAG = "DeviceProvisionedControllerImpl";
    private final ContentResolver mContentResolver;
    private final Uri mDeviceProvisionedUri;
    protected final ArrayList<DeviceProvisionedListener> mListeners = new ArrayList<>();
    protected final ContentObserver mSettingsObserver;
    /* access modifiers changed from: private */
    public final Uri mUserSetupUri;

    public DeviceProvisionedControllerImpl(Context context, Handler handler, BroadcastDispatcher broadcastDispatcher) {
        super(broadcastDispatcher);
        this.mContentResolver = context.getContentResolver();
        this.mDeviceProvisionedUri = Global.getUriFor("device_provisioned");
        this.mUserSetupUri = Secure.getUriFor("user_setup_complete");
        this.mSettingsObserver = new ContentObserver(handler) {
            public void onChange(boolean z, Uri uri, int i) {
                String str = DeviceProvisionedControllerImpl.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Setting change: ");
                sb.append(uri);
                Log.d(str, sb.toString());
                if (DeviceProvisionedControllerImpl.this.mUserSetupUri.equals(uri)) {
                    DeviceProvisionedControllerImpl.this.notifySetupChanged();
                } else {
                    DeviceProvisionedControllerImpl.this.notifyProvisionedChanged();
                }
            }
        };
    }

    public boolean isDeviceProvisioned() {
        return Global.getInt(this.mContentResolver, "device_provisioned", 0) != 0;
    }

    public boolean isUserSetup(int i) {
        return Secure.getIntForUser(this.mContentResolver, "user_setup_complete", 0, i) != 0;
    }

    public int getCurrentUser() {
        return ActivityManager.getCurrentUser();
    }

    public void addCallback(DeviceProvisionedListener deviceProvisionedListener) {
        this.mListeners.add(deviceProvisionedListener);
        if (this.mListeners.size() == 1) {
            startListening(getCurrentUser());
        }
        deviceProvisionedListener.onUserSetupChanged();
        deviceProvisionedListener.onDeviceProvisionedChanged();
    }

    public void removeCallback(DeviceProvisionedListener deviceProvisionedListener) {
        this.mListeners.remove(deviceProvisionedListener);
        if (this.mListeners.size() == 0) {
            stopListening();
        }
    }

    /* access modifiers changed from: protected */
    public void startListening(int i) {
        this.mContentResolver.registerContentObserver(this.mDeviceProvisionedUri, true, this.mSettingsObserver, 0);
        this.mContentResolver.registerContentObserver(this.mUserSetupUri, true, this.mSettingsObserver, i);
        startTracking();
    }

    /* access modifiers changed from: protected */
    public void stopListening() {
        stopTracking();
        this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
    }

    public void onUserSwitched(int i) {
        this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
        this.mContentResolver.registerContentObserver(this.mDeviceProvisionedUri, true, this.mSettingsObserver, 0);
        this.mContentResolver.registerContentObserver(this.mUserSetupUri, true, this.mSettingsObserver, i);
        notifyUserChanged();
    }

    private void notifyUserChanged() {
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            ((DeviceProvisionedListener) this.mListeners.get(size)).onUserSwitched();
        }
    }

    /* access modifiers changed from: private */
    public void notifySetupChanged() {
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            ((DeviceProvisionedListener) this.mListeners.get(size)).onUserSetupChanged();
        }
    }

    /* access modifiers changed from: private */
    public void notifyProvisionedChanged() {
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            ((DeviceProvisionedListener) this.mListeners.get(size)).onDeviceProvisionedChanged();
        }
    }
}
