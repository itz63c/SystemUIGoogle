package com.android.systemui.util;

import android.app.AlarmManager;
import android.app.AlarmManager.OnAlarmListener;
import android.os.Handler;
import android.os.SystemClock;

public class AlarmTimeout implements OnAlarmListener {
    private final AlarmManager mAlarmManager;
    private final Handler mHandler;
    private final OnAlarmListener mListener;
    private boolean mScheduled;
    private final String mTag;

    public AlarmTimeout(AlarmManager alarmManager, OnAlarmListener onAlarmListener, String str, Handler handler) {
        this.mAlarmManager = alarmManager;
        this.mListener = onAlarmListener;
        this.mTag = str;
        this.mHandler = handler;
    }

    public boolean schedule(long j, int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Illegal mode: ");
                    sb.append(i);
                    throw new IllegalArgumentException(sb.toString());
                } else if (this.mScheduled) {
                    cancel();
                }
            } else if (this.mScheduled) {
                return false;
            }
        } else if (this.mScheduled) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(this.mTag);
            sb2.append(" timeout is already scheduled");
            throw new IllegalStateException(sb2.toString());
        }
        this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + j, this.mTag, this, this.mHandler);
        this.mScheduled = true;
        return true;
    }

    public boolean isScheduled() {
        return this.mScheduled;
    }

    public void cancel() {
        if (this.mScheduled) {
            this.mAlarmManager.cancel(this);
            this.mScheduled = false;
        }
    }

    public void onAlarm() {
        if (this.mScheduled) {
            this.mScheduled = false;
            this.mListener.onAlarm();
        }
    }
}
