package com.android.systemui.statusbar.phone;

import android.content.res.Resources;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.MathUtils;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2012R$integer;
import com.android.systemui.doze.AlwaysOnDisplayPolicy;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;

public class DozeParameters implements Tunable, com.android.systemui.plugins.statusbar.DozeParameters {
    public static final boolean FORCE_BLANKING = SystemProperties.getBoolean("debug.force_blanking", false);
    public static final boolean FORCE_NO_BLANKING = SystemProperties.getBoolean("debug.force_no_blanking", false);
    private final AlwaysOnDisplayPolicy mAlwaysOnPolicy;
    private final AmbientDisplayConfiguration mAmbientDisplayConfiguration;
    private boolean mControlScreenOffAnimation;
    private boolean mDozeAlwaysOn;
    private final PowerManager mPowerManager;
    private final Resources mResources;

    protected DozeParameters(Resources resources, AmbientDisplayConfiguration ambientDisplayConfiguration, AlwaysOnDisplayPolicy alwaysOnDisplayPolicy, PowerManager powerManager, TunerService tunerService) {
        this.mResources = resources;
        this.mAmbientDisplayConfiguration = ambientDisplayConfiguration;
        this.mAlwaysOnPolicy = alwaysOnDisplayPolicy;
        boolean z = !getDisplayNeedsBlanking();
        this.mControlScreenOffAnimation = z;
        this.mPowerManager = powerManager;
        powerManager.setDozeAfterScreenOff(!z);
        tunerService.addTunable(this, "doze_always_on", "accessibility_display_inversion_enabled");
    }

    public boolean getDisplayStateSupported() {
        return getBoolean("doze.display.supported", C2007R$bool.doze_display_state_supported);
    }

    public boolean getDozeSuspendDisplayStateSupported() {
        return this.mResources.getBoolean(C2007R$bool.doze_suspend_display_state_supported);
    }

    public float getScreenBrightnessDoze() {
        return ((float) this.mResources.getInteger(17694884)) / 255.0f;
    }

    public int getPulseVisibleDuration() {
        return getInt("doze.pulse.duration.visible", C2012R$integer.doze_pulse_duration_visible);
    }

    public boolean getPulseOnSigMotion() {
        return getBoolean("doze.pulse.sigmotion", C2007R$bool.doze_pulse_on_significant_motion);
    }

    public boolean getProxCheckBeforePulse() {
        return getBoolean("doze.pulse.proxcheck", C2007R$bool.doze_proximity_check_before_pulse);
    }

    public int getPickupVibrationThreshold() {
        return getInt("doze.pickup.vibration.threshold", C2012R$integer.doze_pickup_vibration_threshold);
    }

    public long getWallpaperAodDuration() {
        if (shouldControlScreenOff()) {
            return 2500;
        }
        return this.mAlwaysOnPolicy.wallpaperVisibilityDuration;
    }

    public long getWallpaperFadeOutDuration() {
        return this.mAlwaysOnPolicy.wallpaperFadeOutDuration;
    }

    public boolean getAlwaysOn() {
        return this.mDozeAlwaysOn;
    }

    public boolean getDisplayNeedsBlanking() {
        return FORCE_BLANKING || (!FORCE_NO_BLANKING && this.mResources.getBoolean(17891414));
    }

    public boolean shouldControlScreenOff() {
        return this.mControlScreenOffAnimation;
    }

    public void setControlScreenOffAnimation(boolean z) {
        if (this.mControlScreenOffAnimation != z) {
            this.mControlScreenOffAnimation = z;
            this.mPowerManager.setDozeAfterScreenOff(!z);
        }
    }

    private boolean getBoolean(String str, int i) {
        return SystemProperties.getBoolean(str, this.mResources.getBoolean(i));
    }

    private int getInt(String str, int i) {
        return MathUtils.constrain(SystemProperties.getInt(str, this.mResources.getInteger(i)), 0, 60000);
    }

    public int getPulseVisibleDurationExtended() {
        return getPulseVisibleDuration() * 2;
    }

    public boolean doubleTapReportsTouchCoordinates() {
        return this.mResources.getBoolean(C2007R$bool.doze_double_tap_reports_touch_coordinates);
    }

    public void onTuningChanged(String str, String str2) {
        this.mDozeAlwaysOn = this.mAmbientDisplayConfiguration.alwaysOnEnabled(-2);
    }

    public AlwaysOnDisplayPolicy getPolicy() {
        return this.mAlwaysOnPolicy;
    }
}
