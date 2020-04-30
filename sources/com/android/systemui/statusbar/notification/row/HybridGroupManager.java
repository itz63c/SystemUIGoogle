package com.android.systemui.statusbar.notification.row;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2015R$plurals;
import com.android.systemui.C2017R$string;
import com.android.systemui.C2018R$style;

public class HybridGroupManager {
    private final Context mContext;
    private int mOverflowNumberColor;
    private int mOverflowNumberPadding;
    private float mOverflowNumberSize;
    private final ViewGroup mParent;

    public HybridGroupManager(Context context, ViewGroup viewGroup) {
        this.mContext = context;
        this.mParent = viewGroup;
        initDimens();
    }

    public void initDimens() {
        Resources resources = this.mContext.getResources();
        this.mOverflowNumberSize = (float) resources.getDimensionPixelSize(C2009R$dimen.group_overflow_number_size);
        this.mOverflowNumberPadding = resources.getDimensionPixelSize(C2009R$dimen.group_overflow_number_padding);
    }

    private HybridNotificationView inflateHybridViewWithStyle(int i) {
        HybridNotificationView hybridNotificationView = (HybridNotificationView) ((LayoutInflater) new ContextThemeWrapper(this.mContext, i).getSystemService(LayoutInflater.class)).inflate(C2013R$layout.hybrid_notification, this.mParent, false);
        this.mParent.addView(hybridNotificationView);
        return hybridNotificationView;
    }

    private TextView inflateOverflowNumber() {
        TextView textView = (TextView) ((LayoutInflater) this.mContext.getSystemService(LayoutInflater.class)).inflate(C2013R$layout.hybrid_overflow_number, this.mParent, false);
        this.mParent.addView(textView);
        updateOverFlowNumberColor(textView);
        return textView;
    }

    private void updateOverFlowNumberColor(TextView textView) {
        textView.setTextColor(this.mOverflowNumberColor);
    }

    public void setOverflowNumberColor(TextView textView, int i) {
        this.mOverflowNumberColor = i;
        if (textView != null) {
            updateOverFlowNumberColor(textView);
        }
    }

    public HybridNotificationView bindFromNotification(HybridNotificationView hybridNotificationView, Notification notification) {
        return bindFromNotificationWithStyle(hybridNotificationView, notification, C2018R$style.HybridNotification);
    }

    private HybridNotificationView bindFromNotificationWithStyle(HybridNotificationView hybridNotificationView, Notification notification, int i) {
        if (hybridNotificationView == null) {
            hybridNotificationView = inflateHybridViewWithStyle(i);
        }
        hybridNotificationView.bind(resolveTitle(notification), resolveText(notification));
        return hybridNotificationView;
    }

    private CharSequence resolveText(Notification notification) {
        CharSequence charSequence = notification.extras.getCharSequence("android.text");
        return charSequence == null ? notification.extras.getCharSequence("android.bigText") : charSequence;
    }

    private CharSequence resolveTitle(Notification notification) {
        CharSequence charSequence = notification.extras.getCharSequence("android.title");
        return charSequence == null ? notification.extras.getCharSequence("android.title.big") : charSequence;
    }

    public TextView bindOverflowNumber(TextView textView, int i) {
        if (textView == null) {
            textView = inflateOverflowNumber();
        }
        String string = this.mContext.getResources().getString(C2017R$string.notification_group_overflow_indicator, new Object[]{Integer.valueOf(i)});
        if (!string.equals(textView.getText())) {
            textView.setText(string);
        }
        textView.setContentDescription(String.format(this.mContext.getResources().getQuantityString(C2015R$plurals.notification_group_overflow_description, i), new Object[]{Integer.valueOf(i)}));
        textView.setTextSize(0, this.mOverflowNumberSize);
        textView.setPaddingRelative(textView.getPaddingStart(), textView.getPaddingTop(), this.mOverflowNumberPadding, textView.getPaddingBottom());
        updateOverFlowNumberColor(textView);
        return textView;
    }
}
