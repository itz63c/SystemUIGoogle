package com.android.settingslib.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class ActionButtonsPreference extends Preference {
    private final ButtonInfo mButton1Info = new ButtonInfo();
    private final ButtonInfo mButton2Info = new ButtonInfo();
    private final ButtonInfo mButton3Info = new ButtonInfo();
    private final ButtonInfo mButton4Info = new ButtonInfo();

    static class ButtonInfo {
        /* access modifiers changed from: private */
        public Button mButton;
        private Drawable mIcon;
        private boolean mIsEnabled = true;
        private boolean mIsVisible = true;
        private OnClickListener mListener;
        private CharSequence mText;

        ButtonInfo() {
        }

        /* access modifiers changed from: 0000 */
        public void setUpButton() {
            this.mButton.setText(this.mText);
            this.mButton.setOnClickListener(this.mListener);
            this.mButton.setEnabled(this.mIsEnabled);
            this.mButton.setCompoundDrawablesWithIntrinsicBounds(null, this.mIcon, null, null);
            if (shouldBeVisible()) {
                this.mButton.setVisibility(0);
            } else {
                this.mButton.setVisibility(8);
            }
        }

        private boolean shouldBeVisible() {
            return this.mIsVisible && (!TextUtils.isEmpty(this.mText) || this.mIcon != null);
        }
    }

    public ActionButtonsPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        setLayoutResource(R$layout.settings_action_buttons);
        setSelectable(false);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedAbove(true);
        preferenceViewHolder.setDividerAllowedBelow(true);
        this.mButton1Info.mButton = (Button) preferenceViewHolder.findViewById(R$id.button1);
        this.mButton2Info.mButton = (Button) preferenceViewHolder.findViewById(R$id.button2);
        this.mButton3Info.mButton = (Button) preferenceViewHolder.findViewById(R$id.button3);
        this.mButton4Info.mButton = (Button) preferenceViewHolder.findViewById(R$id.button4);
        this.mButton1Info.setUpButton();
        this.mButton2Info.setUpButton();
        this.mButton3Info.setUpButton();
        this.mButton4Info.setUpButton();
    }
}
