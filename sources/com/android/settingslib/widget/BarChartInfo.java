package com.android.settingslib.widget;

import android.view.View.OnClickListener;

public class BarChartInfo {
    public abstract BarViewInfo[] getBarViewInfos();

    public abstract int getDetails();

    public abstract OnClickListener getDetailsOnClickListener();

    public abstract int getEmptyText();

    public abstract int getTitle();
}
