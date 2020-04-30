package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextClock;
import com.android.systemui.C2011R$id;

public class SplitClockView extends LinearLayout {
    private TextClock mAmPmView;
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.TIME_SET".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action) || "android.intent.action.LOCALE_CHANGED".equals(action) || "android.intent.action.CONFIGURATION_CHANGED".equals(action) || "android.intent.action.USER_SWITCHED".equals(action)) {
                SplitClockView.this.updatePatterns();
            }
        }
    };
    private TextClock mTimeView;

    public SplitClockView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTimeView = (TextClock) findViewById(C2011R$id.time_view);
        this.mAmPmView = (TextClock) findViewById(C2011R$id.am_pm_view);
        this.mTimeView.setShowCurrentUserTime(true);
        this.mAmPmView.setShowCurrentUserTime(true);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        getContext().registerReceiverAsUser(this.mIntentReceiver, UserHandle.ALL, intentFilter, null, null);
        updatePatterns();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(this.mIntentReceiver);
    }

    /* access modifiers changed from: private */
    public void updatePatterns() {
        String str;
        String str2;
        String timeFormatString = DateFormat.getTimeFormatString(getContext(), ActivityManager.getCurrentUser());
        int amPmPartEndIndex = getAmPmPartEndIndex(timeFormatString);
        if (amPmPartEndIndex == -1) {
            str2 = "";
            str = timeFormatString;
        } else {
            str = timeFormatString.substring(0, amPmPartEndIndex);
            str2 = timeFormatString.substring(amPmPartEndIndex);
        }
        this.mTimeView.setFormat12Hour(str);
        this.mTimeView.setFormat24Hour(str);
        this.mTimeView.setContentDescriptionFormat12Hour(timeFormatString);
        this.mTimeView.setContentDescriptionFormat24Hour(timeFormatString);
        this.mAmPmView.setFormat12Hour(str2);
        this.mAmPmView.setFormat24Hour(str2);
    }

    private static int getAmPmPartEndIndex(String str) {
        int length = str.length() - 1;
        int i = 0;
        int i2 = length;
        boolean z = false;
        while (true) {
            int i3 = -1;
            if (i2 >= 0) {
                char charAt = str.charAt(i2);
                boolean z2 = charAt == 'a';
                boolean isWhitespace = Character.isWhitespace(charAt);
                if (z2) {
                    z = true;
                }
                if (z2 || isWhitespace) {
                    i2--;
                } else if (i2 == length) {
                    return -1;
                } else {
                    if (z) {
                        i3 = i2 + 1;
                    }
                    return i3;
                }
            } else {
                if (!z) {
                    i = -1;
                }
                return i;
            }
        }
    }
}
