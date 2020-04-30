package com.android.systemui.tuner;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Global;
import android.view.MenuItem;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.C2017R$string;

public class DemoModeFragment extends PreferenceFragment implements OnPreferenceChangeListener {
    private static final String[] STATUS_ICONS = {"volume", "bluetooth", "location", "alarm", "zen", "sync", "tty", "eri", "mute", "speakerphone", "managed_profile"};
    private final ContentObserver mDemoModeObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        public void onChange(boolean z) {
            DemoModeFragment.this.updateDemoModeEnabled();
            DemoModeFragment.this.updateDemoModeOn();
        }
    };
    private SwitchPreference mEnabledSwitch;
    private SwitchPreference mOnSwitch;

    public void onCreatePreferences(Bundle bundle, String str) {
        Context context = getContext();
        SwitchPreference switchPreference = new SwitchPreference(context);
        this.mEnabledSwitch = switchPreference;
        switchPreference.setTitle(C2017R$string.enable_demo_mode);
        this.mEnabledSwitch.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference2 = new SwitchPreference(context);
        this.mOnSwitch = switchPreference2;
        switchPreference2.setTitle(C2017R$string.show_demo_mode);
        this.mOnSwitch.setEnabled(false);
        this.mOnSwitch.setOnPreferenceChangeListener(this);
        PreferenceScreen createPreferenceScreen = getPreferenceManager().createPreferenceScreen(context);
        createPreferenceScreen.addPreference(this.mEnabledSwitch);
        createPreferenceScreen.addPreference(this.mOnSwitch);
        setPreferenceScreen(createPreferenceScreen);
        updateDemoModeEnabled();
        updateDemoModeOn();
        ContentResolver contentResolver = getContext().getContentResolver();
        contentResolver.registerContentObserver(Global.getUriFor("sysui_demo_allowed"), false, this.mDemoModeObserver);
        contentResolver.registerContentObserver(Global.getUriFor("sysui_tuner_demo_on"), false, this.mDemoModeObserver);
        setHasOptionsMenu(true);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            getFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onResume() {
        super.onResume();
        MetricsLogger.visibility(getContext(), 229, true);
    }

    public void onPause() {
        super.onPause();
        MetricsLogger.visibility(getContext(), 229, false);
    }

    public void onDestroy() {
        getContext().getContentResolver().unregisterContentObserver(this.mDemoModeObserver);
        super.onDestroy();
    }

    /* access modifiers changed from: private */
    public void updateDemoModeEnabled() {
        boolean z = false;
        if (Global.getInt(getContext().getContentResolver(), "sysui_demo_allowed", 0) != 0) {
            z = true;
        }
        this.mEnabledSwitch.setChecked(z);
        this.mOnSwitch.setEnabled(z);
    }

    /* access modifiers changed from: private */
    public void updateDemoModeOn() {
        boolean z = false;
        if (Global.getInt(getContext().getContentResolver(), "sysui_tuner_demo_on", 0) != 0) {
            z = true;
        }
        this.mOnSwitch.setChecked(z);
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean z = obj == Boolean.TRUE;
        if (preference == this.mEnabledSwitch) {
            if (!z) {
                this.mOnSwitch.setChecked(false);
                stopDemoMode();
            }
            MetricsLogger.action(getContext(), 235, z);
            setGlobal("sysui_demo_allowed", z ? 1 : 0);
        } else if (preference != this.mOnSwitch) {
            return false;
        } else {
            MetricsLogger.action(getContext(), 236, z);
            if (z) {
                startDemoMode();
            } else {
                stopDemoMode();
            }
        }
        return true;
    }

    private void startDemoMode() {
        String str;
        Intent intent = new Intent("com.android.systemui.demo");
        String str2 = "command";
        intent.putExtra(str2, "enter");
        getContext().sendBroadcast(intent);
        intent.putExtra(str2, "clock");
        try {
            str = String.format("%02d00", new Object[]{Integer.valueOf(Integer.valueOf(VERSION.RELEASE_OR_CODENAME.split("\\.")[0]).intValue() % 24)});
        } catch (IllegalArgumentException unused) {
            str = "1010";
        }
        intent.putExtra("hhmm", str);
        getContext().sendBroadcast(intent);
        intent.putExtra(str2, "network");
        String str3 = "show";
        intent.putExtra("wifi", str3);
        intent.putExtra("mobile", str3);
        intent.putExtra("sims", "1");
        String str4 = "false";
        intent.putExtra("nosim", str4);
        String str5 = "level";
        intent.putExtra(str5, "4");
        intent.putExtra("datatype", "lte");
        getContext().sendBroadcast(intent);
        intent.putExtra("fully", "true");
        getContext().sendBroadcast(intent);
        intent.putExtra(str2, "battery");
        intent.putExtra(str5, "100");
        intent.putExtra("plugged", str4);
        getContext().sendBroadcast(intent);
        intent.putExtra(str2, "status");
        for (String putExtra : STATUS_ICONS) {
            intent.putExtra(putExtra, "hide");
        }
        getContext().sendBroadcast(intent);
        intent.putExtra(str2, "notifications");
        intent.putExtra("visible", str4);
        getContext().sendBroadcast(intent);
        setGlobal("sysui_tuner_demo_on", 1);
    }

    private void stopDemoMode() {
        Intent intent = new Intent("com.android.systemui.demo");
        intent.putExtra("command", "exit");
        getContext().sendBroadcast(intent);
        setGlobal("sysui_tuner_demo_on", 0);
    }

    private void setGlobal(String str, int i) {
        Global.putInt(getContext().getContentResolver(), str, i);
    }
}
