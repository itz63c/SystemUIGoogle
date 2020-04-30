package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import com.android.systemui.C2017R$string;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

public class SettingsAction extends ServiceAction {
    private final LaunchOpa mLaunchOpa;
    private final String mSettingsPackageName;
    private final StatusBar mStatusBar;

    public static class Builder {
        private final Context mContext;
        private LaunchOpa mLaunchOpa;
        private final StatusBar mStatusBar;

        public Builder(Context context, StatusBar statusBar) {
            this.mContext = context;
            this.mStatusBar = statusBar;
        }

        public Builder setLaunchOpa(LaunchOpa launchOpa) {
            this.mLaunchOpa = launchOpa;
            return this;
        }

        public SettingsAction build() {
            return new SettingsAction(this.mContext, this.mStatusBar, this.mLaunchOpa);
        }
    }

    private SettingsAction(Context context, StatusBar statusBar, LaunchOpa launchOpa) {
        super(context, null);
        this.mSettingsPackageName = context.getResources().getString(C2017R$string.settings_app_package_name);
        this.mStatusBar = statusBar;
        this.mLaunchOpa = launchOpa;
    }

    /* access modifiers changed from: protected */
    public boolean checkSupportedCaller() {
        return checkSupportedCaller(this.mSettingsPackageName);
    }

    public void onTrigger(DetectionProperties detectionProperties) {
        this.mStatusBar.collapseShade();
        super.onTrigger(detectionProperties);
    }

    /* access modifiers changed from: protected */
    public void triggerAction() {
        if (this.mLaunchOpa.isAvailable()) {
            this.mLaunchOpa.launchOpa();
        }
    }
}
