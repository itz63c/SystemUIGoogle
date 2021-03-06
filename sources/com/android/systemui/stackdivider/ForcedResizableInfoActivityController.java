package com.android.systemui.stackdivider;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.UserHandle;
import android.util.ArraySet;
import android.widget.Toast;
import com.android.systemui.C2017R$string;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import java.util.function.Consumer;

public class ForcedResizableInfoActivityController {
    private final Context mContext;
    private boolean mDividerDragging;
    private final Consumer<Boolean> mDockedStackExistsListener = new Consumer() {
        public final void accept(Object obj) {
            ForcedResizableInfoActivityController.this.lambda$new$0$ForcedResizableInfoActivityController((Boolean) obj);
        }
    };
    private final Handler mHandler = new Handler();
    private final ArraySet<String> mPackagesShownInSession = new ArraySet<>();
    private final ArraySet<PendingTaskRecord> mPendingTasks = new ArraySet<>();
    private final Runnable mTimeoutRunnable = new Runnable() {
        public void run() {
            ForcedResizableInfoActivityController.this.showPending();
        }
    };

    private class PendingTaskRecord {
        int reason;
        int taskId;

        PendingTaskRecord(ForcedResizableInfoActivityController forcedResizableInfoActivityController, int i, int i2) {
            this.taskId = i;
            this.reason = i2;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ForcedResizableInfoActivityController(Boolean bool) {
        if (!bool.booleanValue()) {
            this.mPackagesShownInSession.clear();
        }
    }

    public ForcedResizableInfoActivityController(Context context, Divider divider) {
        this.mContext = context;
        ActivityManagerWrapper.getInstance().registerTaskStackListener(new TaskStackChangeListener() {
            public void onActivityForcedResizable(String str, int i, int i2) {
                ForcedResizableInfoActivityController.this.activityForcedResizable(str, i, i2);
            }

            public void onActivityDismissingDockedStack() {
                ForcedResizableInfoActivityController.this.activityDismissingDockedStack();
            }

            public void onActivityLaunchOnSecondaryDisplayFailed() {
                ForcedResizableInfoActivityController.this.activityLaunchOnSecondaryDisplayFailed();
            }
        });
        divider.registerInSplitScreenListener(this.mDockedStackExistsListener);
    }

    public void onAppTransitionFinished() {
        if (!this.mDividerDragging) {
            showPending();
        }
    }

    /* access modifiers changed from: 0000 */
    public void onDraggingStart() {
        this.mDividerDragging = true;
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
    }

    /* access modifiers changed from: 0000 */
    public void onDraggingEnd() {
        this.mDividerDragging = false;
        showPending();
    }

    /* access modifiers changed from: private */
    public void activityForcedResizable(String str, int i, int i2) {
        if (!debounce(str)) {
            this.mPendingTasks.add(new PendingTaskRecord(this, i, i2));
            postTimeout();
        }
    }

    /* access modifiers changed from: private */
    public void activityDismissingDockedStack() {
        Toast.makeText(this.mContext, C2017R$string.dock_non_resizeble_failed_to_dock_text, 0).show();
    }

    /* access modifiers changed from: private */
    public void activityLaunchOnSecondaryDisplayFailed() {
        Toast.makeText(this.mContext, C2017R$string.activity_launch_on_secondary_display_failed_text, 0).show();
    }

    /* access modifiers changed from: private */
    public void showPending() {
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        for (int size = this.mPendingTasks.size() - 1; size >= 0; size--) {
            PendingTaskRecord pendingTaskRecord = (PendingTaskRecord) this.mPendingTasks.valueAt(size);
            Intent intent = new Intent(this.mContext, ForcedResizableInfoActivity.class);
            ActivityOptions makeBasic = ActivityOptions.makeBasic();
            makeBasic.setLaunchTaskId(pendingTaskRecord.taskId);
            makeBasic.setTaskOverlay(true, true);
            intent.putExtra("extra_forced_resizeable_reason", pendingTaskRecord.reason);
            this.mContext.startActivityAsUser(intent, makeBasic.toBundle(), UserHandle.CURRENT);
        }
        this.mPendingTasks.clear();
    }

    private void postTimeout() {
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        this.mHandler.postDelayed(this.mTimeoutRunnable, 1000);
    }

    private boolean debounce(String str) {
        if (str == null) {
            return false;
        }
        if ("com.android.systemui".equals(str)) {
            return true;
        }
        boolean contains = this.mPackagesShownInSession.contains(str);
        this.mPackagesShownInSession.add(str);
        return contains;
    }
}
