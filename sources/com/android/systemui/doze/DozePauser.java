package com.android.systemui.doze;

import android.app.AlarmManager;
import android.app.AlarmManager.OnAlarmListener;
import android.os.Handler;
import com.android.systemui.doze.DozeMachine.Part;
import com.android.systemui.doze.DozeMachine.State;
import com.android.systemui.util.AlarmTimeout;

public class DozePauser implements Part {
    public static final String TAG = "DozePauser";
    private final DozeMachine mMachine;
    private final AlarmTimeout mPauseTimeout;
    private final AlwaysOnDisplayPolicy mPolicy;

    /* renamed from: com.android.systemui.doze.DozePauser$1 */
    static /* synthetic */ class C08261 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$doze$DozeMachine$State;

        static {
            int[] iArr = new int[State.values().length];
            $SwitchMap$com$android$systemui$doze$DozeMachine$State = iArr;
            try {
                iArr[State.DOZE_AOD_PAUSING.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
        }
    }

    public DozePauser(Handler handler, DozeMachine dozeMachine, AlarmManager alarmManager, AlwaysOnDisplayPolicy alwaysOnDisplayPolicy) {
        this.mMachine = dozeMachine;
        this.mPauseTimeout = new AlarmTimeout(alarmManager, new OnAlarmListener() {
            public final void onAlarm() {
                DozePauser.this.onTimeout();
            }
        }, TAG, handler);
        this.mPolicy = alwaysOnDisplayPolicy;
    }

    public void transitionTo(State state, State state2) {
        if (C08261.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()] != 1) {
            this.mPauseTimeout.cancel();
        } else {
            this.mPauseTimeout.schedule(this.mPolicy.proxScreenOffDelayMs, 1);
        }
    }

    /* access modifiers changed from: private */
    public void onTimeout() {
        this.mMachine.requestState(State.DOZE_AOD_PAUSED);
    }
}
