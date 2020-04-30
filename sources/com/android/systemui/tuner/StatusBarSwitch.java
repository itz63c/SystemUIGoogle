package com.android.systemui.tuner;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import androidx.preference.SwitchPreference;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.tuner.TunerService.Tunable;
import java.util.Set;

public class StatusBarSwitch extends SwitchPreference implements Tunable {
    private Set<String> mBlacklist;

    public StatusBarSwitch(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void onAttached() {
        super.onAttached();
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, "icon_blacklist");
    }

    public void onDetached() {
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
        super.onDetached();
    }

    public void onTuningChanged(String str, String str2) {
        if ("icon_blacklist".equals(str)) {
            ArraySet iconBlacklist = StatusBarIconController.getIconBlacklist(getContext(), str2);
            this.mBlacklist = iconBlacklist;
            setChecked(!iconBlacklist.contains(getKey()));
        }
    }

    /* access modifiers changed from: protected */
    public boolean persistBoolean(boolean z) {
        if (!z) {
            if (!this.mBlacklist.contains(getKey())) {
                MetricsLogger.action(getContext(), 234, getKey());
                this.mBlacklist.add(getKey());
                setList(this.mBlacklist);
            }
        } else if (this.mBlacklist.remove(getKey())) {
            MetricsLogger.action(getContext(), 233, getKey());
            setList(this.mBlacklist);
        }
        return true;
    }

    private void setList(Set<String> set) {
        Secure.putStringForUser(getContext().getContentResolver(), "icon_blacklist", TextUtils.join(",", set), ActivityManager.getCurrentUser());
    }
}
