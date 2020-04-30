package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings.Secure;
import com.google.android.systemui.elmyra.UserContentObserver;
import java.util.function.Consumer;

public class WakeMode extends PowerState {
    private final UserContentObserver mSettingsObserver = new UserContentObserver(getContext(), Secure.getUriFor("assist_gesture_wake_enabled"), new Consumer() {
        public final void accept(Object obj) {
            WakeMode.this.lambda$new$0$WakeMode((Uri) obj);
        }
    }, false);
    private boolean mWakeSettingEnabled;

    public WakeMode(Context context) {
        super(context);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$WakeMode(Uri uri) {
        updateWakeSetting();
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.mWakeSettingEnabled = isWakeSettingEnabled();
        this.mSettingsObserver.activate();
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.mSettingsObserver.deactivate();
    }

    private void updateWakeSetting() {
        boolean isWakeSettingEnabled = isWakeSettingEnabled();
        if (isWakeSettingEnabled != this.mWakeSettingEnabled) {
            this.mWakeSettingEnabled = isWakeSettingEnabled;
            notifyListener();
        }
    }

    private boolean isWakeSettingEnabled() {
        return Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_wake_enabled", 1, -2) != 0;
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        if (this.mWakeSettingEnabled) {
            return false;
        }
        return super.isBlocked();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mWakeSettingEnabled -> ");
        sb.append(this.mWakeSettingEnabled);
        sb.append("]");
        return sb.toString();
    }
}
