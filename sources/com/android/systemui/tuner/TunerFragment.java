package com.android.systemui.tuner;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.C2017R$string;
import com.android.systemui.C2019R$xml;
import com.android.systemui.shared.plugins.PluginPrefs;

public class TunerFragment extends PreferenceFragment {
    private static final String[] DEBUG_ONLY = {"nav_bar", "lockscreen", "picture_in_picture"};
    private static final CharSequence KEY_DOZE = "doze";

    public static class TunerWarningFragment extends DialogFragment {
        public Dialog onCreateDialog(Bundle bundle) {
            return new Builder(getContext()).setTitle(C2017R$string.tuner_warning_title).setMessage(C2017R$string.tuner_warning).setPositiveButton(C2017R$string.got_it, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Secure.putInt(TunerWarningFragment.this.getContext().getContentResolver(), "seen_tuner_warning", 1);
                }
            }).show();
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(C2019R$xml.tuner_prefs);
        if (!PluginPrefs.hasPlugins(getContext())) {
            getPreferenceScreen().removePreference(findPreference("plugins"));
        }
        if (!alwaysOnAvailable()) {
            getPreferenceScreen().removePreference(findPreference(KEY_DOZE));
        }
        if (!Build.IS_DEBUGGABLE) {
            int i = 0;
            while (true) {
                String[] strArr = DEBUG_ONLY;
                if (i >= strArr.length) {
                    break;
                }
                Preference findPreference = findPreference(strArr[i]);
                if (findPreference != null) {
                    getPreferenceScreen().removePreference(findPreference);
                }
                i++;
            }
        }
        if (Secure.getInt(getContext().getContentResolver(), "seen_tuner_warning", 0) == 0) {
            String str2 = "tuner_warning";
            if (getFragmentManager().findFragmentByTag(str2) == null) {
                new TunerWarningFragment().show(getFragmentManager(), str2);
            }
        }
    }

    private boolean alwaysOnAvailable() {
        return new AmbientDisplayConfiguration(getContext()).alwaysOnAvailable();
    }

    public void onResume() {
        super.onResume();
        getActivity().setTitle(C2017R$string.system_ui_tuner);
        MetricsLogger.visibility(getContext(), 227, true);
    }

    public void onPause() {
        super.onPause();
        MetricsLogger.visibility(getContext(), 227, false);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.add(0, 2, 0, C2017R$string.remove_from_settings);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 2) {
            TunerService.showResetRequest(getContext(), new Runnable() {
                public void run() {
                    if (TunerFragment.this.getActivity() != null) {
                        TunerFragment.this.getActivity().finish();
                    }
                }
            });
            return true;
        } else if (itemId != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            getActivity().finish();
            return true;
        }
    }
}
