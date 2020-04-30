package com.android.systemui.statusbar.policy;

import android.app.NotificationManager.Policy;
import android.net.Uri;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.ZenRule;

public interface ZenModeController extends CallbackController<Callback> {

    public interface Callback {
        void onConfigChanged(ZenModeConfig zenModeConfig) {
        }

        void onConsolidatedPolicyChanged(Policy policy) {
        }

        void onEffectsSupressorChanged() {
        }

        void onManualRuleChanged(ZenRule zenRule) {
        }

        void onNextAlarmChanged() {
        }

        void onZenAvailableChanged(boolean z) {
        }

        void onZenChanged(int i) {
        }
    }

    boolean areNotificationsHiddenInShade();

    ZenModeConfig getConfig();

    Policy getConsolidatedPolicy();

    ZenRule getManualRule();

    long getNextAlarm();

    int getZen();

    boolean isVolumeRestricted();

    void setZen(int i, Uri uri, String str);
}
