package com.android.systemui.screenrecord;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import com.android.systemui.statusbar.policy.CallbackController;
import java.util.ArrayList;
import java.util.Iterator;

public class RecordingController implements CallbackController<RecordingStateChangeCallback> {
    private final Context mContext;
    private CountDownTimer mCountDownTimer = null;
    /* access modifiers changed from: private */
    public boolean mIsRecording;
    /* access modifiers changed from: private */
    public boolean mIsStarting;
    /* access modifiers changed from: private */
    public ArrayList<RecordingStateChangeCallback> mListeners = new ArrayList<>();
    private PendingIntent mStopIntent;

    public interface RecordingStateChangeCallback {
        void onCountdown(long j) {
        }

        void onCountdownEnd() {
        }

        void onRecordingEnd() {
        }

        void onRecordingStart() {
        }
    }

    public RecordingController(Context context) {
        this.mContext = context;
    }

    public void launchRecordPrompt() {
        ComponentName componentName = new ComponentName("com.android.systemui", "com.android.systemui.screenrecord.ScreenRecordDialog");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.setFlags(268435456);
        this.mContext.startActivity(intent);
    }

    public void startCountdown(long j, long j2, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        this.mIsStarting = true;
        this.mStopIntent = pendingIntent2;
        final PendingIntent pendingIntent3 = pendingIntent;
        C10501 r1 = new CountDownTimer(j, j2) {
            public void onTick(long j) {
                Iterator it = RecordingController.this.mListeners.iterator();
                while (it.hasNext()) {
                    ((RecordingStateChangeCallback) it.next()).onCountdown(j);
                }
            }

            public void onFinish() {
                String str = "RecordingController";
                RecordingController.this.mIsStarting = false;
                RecordingController.this.mIsRecording = true;
                Iterator it = RecordingController.this.mListeners.iterator();
                while (it.hasNext()) {
                    ((RecordingStateChangeCallback) it.next()).onCountdownEnd();
                }
                try {
                    pendingIntent3.send();
                    Log.d(str, "sent start intent");
                } catch (CanceledException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Pending intent was cancelled: ");
                    sb.append(e.getMessage());
                    Log.e(str, sb.toString());
                }
            }
        };
        this.mCountDownTimer = r1;
        r1.start();
    }

    public void cancelCountdown() {
        CountDownTimer countDownTimer = this.mCountDownTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        } else {
            Log.e("RecordingController", "Timer was null");
        }
        this.mIsStarting = false;
        Iterator it = this.mListeners.iterator();
        while (it.hasNext()) {
            ((RecordingStateChangeCallback) it.next()).onCountdownEnd();
        }
    }

    public boolean isStarting() {
        return this.mIsStarting;
    }

    public boolean isRecording() {
        return this.mIsRecording;
    }

    public void stopRecording() {
        try {
            this.mStopIntent.send();
            updateState(false);
        } catch (CanceledException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error stopping: ");
            sb.append(e.getMessage());
            Log.e("RecordingController", sb.toString());
        }
    }

    public void updateState(boolean z) {
        this.mIsRecording = z;
        Iterator it = this.mListeners.iterator();
        while (it.hasNext()) {
            RecordingStateChangeCallback recordingStateChangeCallback = (RecordingStateChangeCallback) it.next();
            if (z) {
                recordingStateChangeCallback.onRecordingStart();
            } else {
                recordingStateChangeCallback.onRecordingEnd();
            }
        }
    }

    public void addCallback(RecordingStateChangeCallback recordingStateChangeCallback) {
        this.mListeners.add(recordingStateChangeCallback);
    }

    public void removeCallback(RecordingStateChangeCallback recordingStateChangeCallback) {
        this.mListeners.remove(recordingStateChangeCallback);
    }
}
