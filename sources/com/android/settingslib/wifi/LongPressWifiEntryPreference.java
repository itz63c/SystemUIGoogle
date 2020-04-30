package com.android.settingslib.wifi;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceViewHolder;

public class LongPressWifiEntryPreference extends WifiEntryPreference {
    private final Fragment mFragment;

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Fragment fragment = this.mFragment;
        if (fragment != null) {
            preferenceViewHolder.itemView.setOnCreateContextMenuListener(fragment);
            preferenceViewHolder.itemView.setTag(this);
            preferenceViewHolder.itemView.setLongClickable(true);
        }
    }
}
