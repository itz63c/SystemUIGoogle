package com.android.settingslib;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class CustomDialogPreferenceCompat extends DialogPreference {
    private OnShowListener mOnShowListener;

    public static class CustomPreferenceDialogFragment extends PreferenceDialogFragmentCompat {
        private CustomDialogPreferenceCompat getCustomizablePreference() {
            return (CustomDialogPreferenceCompat) getPreference();
        }

        /* access modifiers changed from: protected */
        public void onPrepareDialogBuilder(Builder builder) {
            super.onPrepareDialogBuilder(builder);
            getCustomizablePreference().setFragment(this);
            getCustomizablePreference().onPrepareDialogBuilder(builder, this);
        }

        public void onDialogClosed(boolean z) {
            getCustomizablePreference().onDialogClosed(z);
        }

        /* access modifiers changed from: protected */
        public void onBindDialogView(View view) {
            super.onBindDialogView(view);
            getCustomizablePreference().onBindDialogView(view);
        }

        public Dialog onCreateDialog(Bundle bundle) {
            Dialog onCreateDialog = super.onCreateDialog(bundle);
            onCreateDialog.setOnShowListener(getCustomizablePreference().getOnShowListener());
            return onCreateDialog;
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
    public void onBindDialogView(View view) {
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

    public CustomDialogPreferenceCompat(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: private */
    public OnShowListener getOnShowListener() {
        return this.mOnShowListener;
    }
}
