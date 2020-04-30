package com.android.systemui.keyguard;

import android.app.ActivityManager;
import android.app.ActivityManager.TaskDescription;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;

public class WorkLockActivityController {
    private static final String TAG = "WorkLockActivityController";
    private final Context mContext;
    private final IActivityTaskManager mIatm;
    private final TaskStackChangeListener mLockListener;

    public WorkLockActivityController(Context context) {
        this(context, ActivityManagerWrapper.getInstance(), ActivityTaskManager.getService());
    }

    @VisibleForTesting
    WorkLockActivityController(Context context, ActivityManagerWrapper activityManagerWrapper, IActivityTaskManager iActivityTaskManager) {
        C08851 r0 = new TaskStackChangeListener() {
            public void onTaskProfileLocked(int i, int i2) {
                WorkLockActivityController.this.startWorkChallengeInTask(i, i2);
            }
        };
        this.mLockListener = r0;
        this.mContext = context;
        this.mIatm = iActivityTaskManager;
        activityManagerWrapper.registerTaskStackListener(r0);
    }

    /* access modifiers changed from: private */
    public void startWorkChallengeInTask(int i, int i2) {
        TaskDescription taskDescription;
        String str = "Failed to get description for task=";
        try {
            taskDescription = this.mIatm.getTaskDescription(i);
        } catch (RemoteException unused) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(i);
            Log.w(str2, sb.toString());
            taskDescription = null;
        }
        Intent addFlags = new Intent("android.app.action.CONFIRM_DEVICE_CREDENTIAL_WITH_USER").setComponent(new ComponentName(this.mContext, WorkLockActivity.class)).putExtra("android.intent.extra.USER_ID", i2).putExtra("com.android.systemui.keyguard.extra.TASK_DESCRIPTION", taskDescription).addFlags(67239936);
        ActivityOptions makeBasic = ActivityOptions.makeBasic();
        makeBasic.setLaunchTaskId(i);
        makeBasic.setTaskOverlay(true, false);
        if (!ActivityManager.isStartResultSuccessful(startActivityAsUser(addFlags, makeBasic.toBundle(), -2))) {
            try {
                this.mIatm.removeTask(i);
            } catch (RemoteException unused2) {
                String str3 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append(i);
                Log.w(str3, sb2.toString());
            }
        }
    }

    private int startActivityAsUser(Intent intent, Bundle bundle, int i) {
        try {
            return this.mIatm.startActivityAsUser(this.mContext.getIApplicationThread(), this.mContext.getBasePackageName(), this.mContext.getAttributionTag(), intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), null, null, 0, 268435456, null, bundle, i);
        } catch (RemoteException | Exception unused) {
            return -96;
        }
    }
}
