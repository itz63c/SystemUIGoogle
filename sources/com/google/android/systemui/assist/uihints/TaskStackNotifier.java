package com.google.android.systemui.assist.uihints;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.util.Log;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfo;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfoListener;

class TaskStackNotifier implements ConfigInfoListener {
    private PendingIntent mIntent;
    private final TaskStackChangeListener mListener = new TaskStackChangeListener() {
        public void onTaskMovedToFront(RunningTaskInfo runningTaskInfo) {
            TaskStackNotifier.this.sendIntent();
        }

        public void onTaskCreated(int i, ComponentName componentName) {
            TaskStackNotifier.this.sendIntent();
        }
    };
    private boolean mListenerRegistered = false;
    private final ActivityManagerWrapper mWrapper = ActivityManagerWrapper.getInstance();

    TaskStackNotifier() {
    }

    public void onConfigInfo(ConfigInfo configInfo) {
        PendingIntent pendingIntent = configInfo.onTaskChange;
        this.mIntent = pendingIntent;
        if (pendingIntent != null && !this.mListenerRegistered) {
            this.mWrapper.registerTaskStackListener(this.mListener);
            this.mListenerRegistered = true;
        } else if (this.mIntent == null && this.mListenerRegistered) {
            this.mWrapper.unregisterTaskStackListener(this.mListener);
            this.mListenerRegistered = false;
        }
    }

    /* access modifiers changed from: private */
    public void sendIntent() {
        PendingIntent pendingIntent = this.mIntent;
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
            } catch (CanceledException e) {
                Log.e("TaskStackNotifier", "could not send intent", e);
            }
        }
    }
}
