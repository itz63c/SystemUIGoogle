package com.android.settingslib.development;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import com.android.settingslib.R$array;
import com.android.settingslib.core.ConfirmationDialogController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnDestroy;

public abstract class AbstractLogpersistPreferenceController extends DeveloperOptionsPreferenceController implements OnPreferenceChangeListener, LifecycleObserver, OnCreate, OnDestroy, ConfirmationDialogController {
    static final String ACTUAL_LOGPERSIST_PROPERTY = "logd.logpersistd";
    static final String ACTUAL_LOGPERSIST_PROPERTY_BUFFER = "logd.logpersistd.buffer";
    static final String SELECT_LOGPERSIST_PROPERTY_SERVICE = "logcatd";
    private ListPreference mLogpersist;
    private boolean mLogpersistCleared;
    private final BroadcastReceiver mReceiver;

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != this.mLogpersist) {
            return false;
        }
        writeLogpersistOption(obj, false);
        return true;
    }

    public void onCreate(Bundle bundle) {
        LocalBroadcastManager.getInstance(this.mContext).registerReceiver(this.mReceiver, new IntentFilter("com.android.settingslib.development.AbstractLogdSizePreferenceController.LOGD_SIZE_UPDATED"));
    }

    public void onDestroy() {
        LocalBroadcastManager.getInstance(this.mContext).unregisterReceiver(this.mReceiver);
    }

    public void updateLogpersistValues() {
        char c;
        if (this.mLogpersist != null) {
            String str = ACTUAL_LOGPERSIST_PROPERTY;
            String str2 = SystemProperties.get(str);
            if (str2 == null) {
                str2 = "";
            }
            String str3 = SystemProperties.get(ACTUAL_LOGPERSIST_PROPERTY_BUFFER);
            String str4 = "all";
            if (str3 == null || str3.length() == 0) {
                str3 = str4;
            }
            if (str2.equals(SELECT_LOGPERSIST_PROPERTY_SERVICE)) {
                String str5 = "kernel";
                if (str3.equals(str5)) {
                    c = 3;
                } else {
                    if (!str3.equals(str4) && !str3.contains("radio") && str3.contains("security") && str3.contains(str5)) {
                        c = 2;
                        if (!str3.contains("default")) {
                            String[] strArr = {"main", "events", "system", "crash"};
                            int i = 0;
                            while (true) {
                                if (i >= 4) {
                                    break;
                                } else if (!str3.contains(strArr[i])) {
                                    break;
                                } else {
                                    i++;
                                }
                            }
                        }
                    }
                    c = 1;
                }
            } else {
                c = 0;
            }
            this.mLogpersist.setValue(this.mContext.getResources().getStringArray(R$array.select_logpersist_values)[c]);
            this.mLogpersist.setSummary(this.mContext.getResources().getStringArray(R$array.select_logpersist_summaries)[c]);
            if (c != 0) {
                this.mLogpersistCleared = false;
            } else if (!this.mLogpersistCleared) {
                SystemProperties.set(str, "clear");
                SystemPropPoker.getInstance().poke();
                this.mLogpersistCleared = true;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setLogpersistOff(boolean z) {
        String str = "";
        SystemProperties.set("persist.logd.logpersistd.buffer", str);
        SystemProperties.set(ACTUAL_LOGPERSIST_PROPERTY_BUFFER, str);
        SystemProperties.set("persist.logd.logpersistd", str);
        String str2 = z ? str : "stop";
        String str3 = ACTUAL_LOGPERSIST_PROPERTY;
        SystemProperties.set(str3, str2);
        SystemPropPoker.getInstance().poke();
        if (z) {
            updateLogpersistValues();
            return;
        }
        int i = 0;
        while (i < 3) {
            String str4 = SystemProperties.get(str3);
            if (str4 != null && !str4.equals(str)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException unused) {
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void writeLogpersistOption(Object obj, boolean z) {
        if (this.mLogpersist != null) {
            String str = SystemProperties.get("persist.log.tag");
            if (str != null && str.startsWith("Settings")) {
                obj = null;
                z = true;
            }
            String str2 = ACTUAL_LOGPERSIST_PROPERTY;
            String str3 = SELECT_LOGPERSIST_PROPERTY_SERVICE;
            if (obj == null || obj.toString().equals("")) {
                if (z) {
                    this.mLogpersistCleared = false;
                } else if (!this.mLogpersistCleared) {
                    String str4 = SystemProperties.get(str2);
                    if (str4 != null && str4.equals(str3)) {
                        showConfirmationDialog(this.mLogpersist);
                        return;
                    }
                }
                setLogpersistOff(true);
                return;
            }
            String str5 = SystemProperties.get(ACTUAL_LOGPERSIST_PROPERTY_BUFFER);
            if (str5 != null && !str5.equals(obj.toString())) {
                setLogpersistOff(false);
            }
            SystemProperties.set("persist.logd.logpersistd.buffer", obj.toString());
            SystemProperties.set("persist.logd.logpersistd", str3);
            SystemPropPoker.getInstance().poke();
            for (int i = 0; i < 3; i++) {
                String str6 = SystemProperties.get(str2);
                if (str6 != null && str6.equals(str3)) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException unused) {
                }
            }
            updateLogpersistValues();
        }
    }
}
