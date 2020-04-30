package com.android.systemui.statusbar.policy;

import android.app.AlarmManager.AlarmClockInfo;
import com.android.systemui.Dumpable;

public interface NextAlarmController extends CallbackController<NextAlarmChangeCallback>, Dumpable {

    public interface NextAlarmChangeCallback {
        void onNextAlarmChanged(AlarmClockInfo alarmClockInfo);
    }
}
