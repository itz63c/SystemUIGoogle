package com.android.systemui.tuner;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;

public class PowerNotificationControlsFragment extends Fragment {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C2013R$layout.power_notification_controls_settings, viewGroup, false);
    }

    public void onViewCreated(View view, Bundle bundle) {
        String str;
        super.onViewCreated(view, bundle);
        View findViewById = view.findViewById(C2011R$id.switch_bar);
        final Switch switchR = (Switch) findViewById.findViewById(16908352);
        final TextView textView = (TextView) findViewById.findViewById(C2011R$id.switch_text);
        switchR.setChecked(isEnabled());
        if (isEnabled()) {
            str = getString(C2017R$string.switch_bar_on);
        } else {
            str = getString(C2017R$string.switch_bar_off);
        }
        textView.setText(str);
        switchR.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String str;
                boolean z = !PowerNotificationControlsFragment.this.isEnabled();
                MetricsLogger.action(PowerNotificationControlsFragment.this.getContext(), 393, z);
                Secure.putInt(PowerNotificationControlsFragment.this.getContext().getContentResolver(), "show_importance_slider", z ? 1 : 0);
                switchR.setChecked(z);
                TextView textView = textView;
                if (z) {
                    str = PowerNotificationControlsFragment.this.getString(C2017R$string.switch_bar_on);
                } else {
                    str = PowerNotificationControlsFragment.this.getString(C2017R$string.switch_bar_off);
                }
                textView.setText(str);
            }
        });
    }

    public void onResume() {
        super.onResume();
        MetricsLogger.visibility(getContext(), 392, true);
    }

    public void onPause() {
        super.onPause();
        MetricsLogger.visibility(getContext(), 392, false);
    }

    /* access modifiers changed from: private */
    public boolean isEnabled() {
        return Secure.getInt(getContext().getContentResolver(), "show_importance_slider", 0) == 1;
    }
}
