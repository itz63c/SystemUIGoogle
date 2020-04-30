package com.android.settingslib.widget;

import android.graphics.drawable.Drawable;
import android.view.View.OnClickListener;

public class BarViewInfo implements Comparable<BarViewInfo> {
    /* access modifiers changed from: 0000 */
    public abstract OnClickListener getClickListener();

    public abstract CharSequence getContentDescription();

    /* access modifiers changed from: 0000 */
    public abstract int getHeight();

    /* access modifiers changed from: 0000 */
    public abstract Drawable getIcon();

    /* access modifiers changed from: 0000 */
    public abstract int getNormalizedHeight();

    /* access modifiers changed from: 0000 */
    public abstract CharSequence getSummary();

    /* access modifiers changed from: 0000 */
    public abstract CharSequence getTitle();

    /* access modifiers changed from: 0000 */
    public abstract void setNormalizedHeight(int i);
}
