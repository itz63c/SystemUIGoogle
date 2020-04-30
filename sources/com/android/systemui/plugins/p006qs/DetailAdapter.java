package com.android.systemui.plugins.p006qs;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(version = 1)
/* renamed from: com.android.systemui.plugins.qs.DetailAdapter */
public interface DetailAdapter {
    public static final int VERSION = 1;

    View createDetailView(Context context, View view, ViewGroup viewGroup);

    int getMetricsCategory();

    Intent getSettingsIntent();

    CharSequence getTitle();

    boolean getToggleEnabled() {
        return true;
    }

    Boolean getToggleState();

    boolean hasHeader() {
        return true;
    }

    void setToggleState(boolean z);
}
