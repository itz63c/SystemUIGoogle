package com.android.settingslib.widget;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceDialogFragmentCompat;
import com.android.internal.R;
import java.util.ArrayList;

public class UpdatableListPreferenceDialogFragment extends PreferenceDialogFragmentCompat {
    private ArrayAdapter mAdapter;
    private int mClickedDialogEntryIndex;
    private ArrayList<CharSequence> mEntries;
    private CharSequence[] mEntryValues;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getArguments().getInt("metrics_category_key", 0);
        if (bundle == null) {
            this.mEntries = new ArrayList<>();
            setPreferenceData(getListPreference());
            return;
        }
        this.mClickedDialogEntryIndex = bundle.getInt("UpdatableListPreferenceDialogFragment.index", 0);
        this.mEntries = bundle.getCharSequenceArrayList("UpdatableListPreferenceDialogFragment.entries");
        this.mEntryValues = bundle.getCharSequenceArray("UpdatableListPreferenceDialogFragment.entryValues");
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("UpdatableListPreferenceDialogFragment.index", this.mClickedDialogEntryIndex);
        bundle.putCharSequenceArrayList("UpdatableListPreferenceDialogFragment.entries", this.mEntries);
        bundle.putCharSequenceArray("UpdatableListPreferenceDialogFragment.entryValues", this.mEntryValues);
    }

    public void onDialogClosed(boolean z) {
        if (z && this.mClickedDialogEntryIndex >= 0) {
            ListPreference listPreference = getListPreference();
            String charSequence = this.mEntryValues[this.mClickedDialogEntryIndex].toString();
            if (listPreference.callChangeListener(charSequence)) {
                listPreference.setValue(charSequence);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setAdapter(ArrayAdapter arrayAdapter) {
        this.mAdapter = arrayAdapter;
    }

    /* access modifiers changed from: 0000 */
    public void setEntries(ArrayList<CharSequence> arrayList) {
        this.mEntries = arrayList;
    }

    /* access modifiers changed from: 0000 */
    public ArrayAdapter getAdapter() {
        return this.mAdapter;
    }

    /* access modifiers changed from: 0000 */
    public void setMetricsCategory(Bundle bundle) {
        bundle.getInt("metrics_category_key", 0);
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(null, R.styleable.AlertDialog, 16842845, 0);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), obtainStyledAttributes.getResourceId(21, 17367058), this.mEntries);
        this.mAdapter = arrayAdapter;
        builder.setSingleChoiceItems((ListAdapter) arrayAdapter, this.mClickedDialogEntryIndex, (OnClickListener) new OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                UpdatableListPreferenceDialogFragment.this.mo9211x3c3a9cf7(dialogInterface, i);
            }
        });
        builder.setPositiveButton((CharSequence) null, (OnClickListener) null);
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onPrepareDialogBuilder$0 */
    public /* synthetic */ void mo9211x3c3a9cf7(DialogInterface dialogInterface, int i) {
        this.mClickedDialogEntryIndex = i;
        onClick(dialogInterface, -1);
        dialogInterface.dismiss();
    }

    /* access modifiers changed from: 0000 */
    public ListPreference getListPreference() {
        return (ListPreference) getPreference();
    }

    private void setPreferenceData(ListPreference listPreference) {
        this.mEntries.clear();
        this.mClickedDialogEntryIndex = listPreference.findIndexOfValue(listPreference.getValue());
        for (CharSequence add : listPreference.getEntries()) {
            this.mEntries.add(add);
        }
        this.mEntryValues = listPreference.getEntryValues();
    }
}
