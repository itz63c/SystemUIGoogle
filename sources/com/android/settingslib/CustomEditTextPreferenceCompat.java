package com.android.settingslib;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.preference.EditTextPreference;
import androidx.preference.EditTextPreferenceDialogFragmentCompat;

public class CustomEditTextPreferenceCompat extends EditTextPreference {

    public static class CustomPreferenceDialogFragment extends EditTextPreferenceDialogFragmentCompat {
        private CustomEditTextPreferenceCompat getCustomizablePreference() {
            return (CustomEditTextPreferenceCompat) getPreference();
        }

        /* access modifiers changed from: protected */
        public void onBindDialogView(View view) {
            super.onBindDialogView(view);
            getCustomizablePreference().onBindDialogView(view);
        }

        /* access modifiers changed from: protected */
        public void onPrepareDialogBuilder(Builder builder) {
            super.onPrepareDialogBuilder(builder);
            getCustomizablePreference().setFragment(this);
            getCustomizablePreference().onPrepareDialogBuilder(builder, this);
        }

        public void onDialogClosed(boolean z) {
            super.onDialogClosed(z);
            getCustomizablePreference().onDialogClosed(z);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            super.onClick(dialogInterface, i);
            getCustomizablePreference().onClick(dialogInterface, i);
        }
    }

    /* access modifiers changed from: private */
    public void setFragment(CustomPreferenceDialogFragment customPreferenceDialogFragment) {
    }

    /* access modifiers changed from: protected */
    public void onClick(DialogInterface dialogInterface, int i) {
    }

    /* access modifiers changed from: protected */
    public void onDialogClosed(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialogBuilder(Builder builder, OnClickListener onClickListener) {
    }

    public CustomEditTextPreferenceCompat(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onBindDialogView(View view) {
        EditText editText = (EditText) view.findViewById(16908291);
        if (editText != null) {
            editText.setInputType(16385);
            editText.requestFocus();
        }
    }
}
