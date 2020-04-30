package com.android.systemui.assist;

import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.provider.Settings.Secure;
import androidx.slice.Clock;
import com.android.systemui.BootCompleteCache;
import com.android.systemui.BootCompleteCache.BootCompleteListener;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle.Observer;
import com.android.systemui.model.SysUiState;
import com.android.systemui.model.SysUiState.SysUiStateCallback;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.OverviewProxyService.OverviewProxyListener;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import dagger.Lazy;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

final class AssistHandleReminderExpBehavior implements BehaviorController {
    private static final String[] DEFAULT_HOME_CHANGE_ACTIONS = {"android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED", "android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REMOVED"};
    private static final long DEFAULT_LEARNING_TIME_MS = TimeUnit.DAYS.toMillis(10);
    private static final long DEFAULT_SHOW_AND_GO_DELAYED_LONG_DELAY_MS = TimeUnit.SECONDS.toMillis(1);
    private static final long DEFAULT_SHOW_AND_GO_DELAY_RESET_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(3);
    private final Lazy<ActivityManagerWrapper> mActivityManagerWrapper;
    private AssistHandleCallbacks mAssistHandleCallbacks;
    private final Lazy<BootCompleteCache> mBootCompleteCache;
    private final BootCompleteListener mBootCompleteListener = new BootCompleteListener() {
        public void onBootComplete() {
            AssistHandleReminderExpBehavior assistHandleReminderExpBehavior = AssistHandleReminderExpBehavior.this;
            assistHandleReminderExpBehavior.mDefaultHome = assistHandleReminderExpBehavior.getCurrentDefaultHome();
        }
    };
    private final Lazy<BroadcastDispatcher> mBroadcastDispatcher;
    private final Clock mClock;
    private int mConsecutiveTaskSwitches;
    private Context mContext;
    /* access modifiers changed from: private */
    public ComponentName mDefaultHome;
    private final BroadcastReceiver mDefaultHomeBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            AssistHandleReminderExpBehavior assistHandleReminderExpBehavior = AssistHandleReminderExpBehavior.this;
            assistHandleReminderExpBehavior.mDefaultHome = assistHandleReminderExpBehavior.getCurrentDefaultHome();
        }
    };
    private final IntentFilter mDefaultHomeIntentFilter;
    private final DeviceConfigHelper mDeviceConfigHelper;
    private final Handler mHandler;
    private boolean mIsAwake;
    private boolean mIsDozing;
    private boolean mIsLauncherShowing;
    private boolean mIsLearned;
    private boolean mIsNavBarHidden;
    private long mLastLearningTimestamp;
    private long mLearnedHintLastShownEpochDay;
    private int mLearningCount;
    private long mLearningTimeElapsed;
    private boolean mOnLockscreen;
    private final OverviewProxyListener mOverviewProxyListener = new OverviewProxyListener() {
        public void onOverviewShown(boolean z) {
            AssistHandleReminderExpBehavior.this.handleOverviewShown();
        }
    };
    private final Lazy<OverviewProxyService> mOverviewProxyService;
    private final Lazy<PackageManagerWrapper> mPackageManagerWrapper;
    private final Runnable mResetConsecutiveTaskSwitches = new Runnable() {
        public final void run() {
            AssistHandleReminderExpBehavior.this.resetConsecutiveTaskSwitches();
        }
    };
    private int mRunningTaskId;
    private final Lazy<StatusBarStateController> mStatusBarStateController;
    private final StateListener mStatusBarStateListener = new StateListener() {
        public void onStateChanged(int i) {
            AssistHandleReminderExpBehavior.this.handleStatusBarStateChanged(i);
        }

        public void onDozingChanged(boolean z) {
            AssistHandleReminderExpBehavior.this.handleDozingChanged(z);
        }
    };
    private final Lazy<SysUiState> mSysUiFlagContainer;
    private final SysUiStateCallback mSysUiStateCallback = new SysUiStateCallback() {
        public final void onSystemUiStateChanged(int i) {
            AssistHandleReminderExpBehavior.this.handleSystemUiStateChanged(i);
        }
    };
    private final TaskStackChangeListener mTaskStackChangeListener = new TaskStackChangeListener() {
        public void onTaskMovedToFront(RunningTaskInfo runningTaskInfo) {
            AssistHandleReminderExpBehavior.this.handleTaskStackTopChanged(runningTaskInfo.taskId, runningTaskInfo.topActivity);
        }

        public void onTaskCreated(int i, ComponentName componentName) {
            AssistHandleReminderExpBehavior.this.handleTaskStackTopChanged(i, componentName);
        }
    };
    private final Lazy<WakefulnessLifecycle> mWakefulnessLifecycle;
    private final Observer mWakefulnessLifecycleObserver = new Observer() {
        public void onStartedWakingUp() {
            AssistHandleReminderExpBehavior.this.handleWakefullnessChanged(false);
        }

        public void onFinishedWakingUp() {
            AssistHandleReminderExpBehavior.this.handleWakefullnessChanged(true);
        }

        public void onStartedGoingToSleep() {
            AssistHandleReminderExpBehavior.this.handleWakefullnessChanged(false);
        }

        public void onFinishedGoingToSleep() {
            AssistHandleReminderExpBehavior.this.handleWakefullnessChanged(false);
        }
    };

    private boolean onLockscreen(int i) {
        return i == 1 || i == 2;
    }

    AssistHandleReminderExpBehavior(Clock clock, Handler handler, DeviceConfigHelper deviceConfigHelper, Lazy<StatusBarStateController> lazy, Lazy<ActivityManagerWrapper> lazy2, Lazy<OverviewProxyService> lazy3, Lazy<SysUiState> lazy4, Lazy<WakefulnessLifecycle> lazy5, Lazy<PackageManagerWrapper> lazy6, Lazy<BroadcastDispatcher> lazy7, Lazy<BootCompleteCache> lazy8) {
        this.mClock = clock;
        this.mHandler = handler;
        this.mDeviceConfigHelper = deviceConfigHelper;
        this.mStatusBarStateController = lazy;
        this.mActivityManagerWrapper = lazy2;
        this.mOverviewProxyService = lazy3;
        this.mSysUiFlagContainer = lazy4;
        this.mWakefulnessLifecycle = lazy5;
        this.mPackageManagerWrapper = lazy6;
        this.mDefaultHomeIntentFilter = new IntentFilter();
        for (String addAction : DEFAULT_HOME_CHANGE_ACTIONS) {
            this.mDefaultHomeIntentFilter.addAction(addAction);
        }
        this.mBroadcastDispatcher = lazy7;
        this.mBootCompleteCache = lazy8;
    }

    public void onModeActivated(Context context, AssistHandleCallbacks assistHandleCallbacks) {
        int i;
        this.mContext = context;
        this.mAssistHandleCallbacks = assistHandleCallbacks;
        this.mConsecutiveTaskSwitches = 0;
        ((BootCompleteCache) this.mBootCompleteCache.get()).addListener(this.mBootCompleteListener);
        this.mDefaultHome = getCurrentDefaultHome();
        ((BroadcastDispatcher) this.mBroadcastDispatcher.get()).registerReceiver(this.mDefaultHomeBroadcastReceiver, this.mDefaultHomeIntentFilter);
        this.mOnLockscreen = onLockscreen(((StatusBarStateController) this.mStatusBarStateController.get()).getState());
        this.mIsDozing = ((StatusBarStateController) this.mStatusBarStateController.get()).isDozing();
        ((StatusBarStateController) this.mStatusBarStateController.get()).addCallback(this.mStatusBarStateListener);
        RunningTaskInfo runningTask = ((ActivityManagerWrapper) this.mActivityManagerWrapper.get()).getRunningTask();
        if (runningTask == null) {
            i = 0;
        } else {
            i = runningTask.taskId;
        }
        this.mRunningTaskId = i;
        ((ActivityManagerWrapper) this.mActivityManagerWrapper.get()).registerTaskStackListener(this.mTaskStackChangeListener);
        ((OverviewProxyService) this.mOverviewProxyService.get()).addCallback(this.mOverviewProxyListener);
        ((SysUiState) this.mSysUiFlagContainer.get()).addCallback(this.mSysUiStateCallback);
        this.mIsAwake = ((WakefulnessLifecycle) this.mWakefulnessLifecycle.get()).getWakefulness() == 2;
        ((WakefulnessLifecycle) this.mWakefulnessLifecycle.get()).addObserver(this.mWakefulnessLifecycleObserver);
        this.mLearningTimeElapsed = Secure.getLong(context.getContentResolver(), "reminder_exp_learning_time_elapsed", 0);
        this.mLearningCount = Secure.getInt(context.getContentResolver(), "reminder_exp_learning_event_count", 0);
        this.mLearnedHintLastShownEpochDay = Secure.getLong(context.getContentResolver(), "reminder_exp_learned_hint_last_shown", 0);
        this.mLastLearningTimestamp = this.mClock.currentTimeMillis();
        callbackForCurrentState(false);
    }

    public void onModeDeactivated() {
        this.mAssistHandleCallbacks = null;
        if (this.mContext != null) {
            ((BroadcastDispatcher) this.mBroadcastDispatcher.get()).unregisterReceiver(this.mDefaultHomeBroadcastReceiver);
            ((BootCompleteCache) this.mBootCompleteCache.get()).removeListener(this.mBootCompleteListener);
            Secure.putLong(this.mContext.getContentResolver(), "reminder_exp_learning_time_elapsed", 0);
            Secure.putInt(this.mContext.getContentResolver(), "reminder_exp_learning_event_count", 0);
            Secure.putLong(this.mContext.getContentResolver(), "reminder_exp_learned_hint_last_shown", 0);
            this.mContext = null;
        }
        ((StatusBarStateController) this.mStatusBarStateController.get()).removeCallback(this.mStatusBarStateListener);
        ((ActivityManagerWrapper) this.mActivityManagerWrapper.get()).unregisterTaskStackListener(this.mTaskStackChangeListener);
        ((OverviewProxyService) this.mOverviewProxyService.get()).removeCallback(this.mOverviewProxyListener);
        ((SysUiState) this.mSysUiFlagContainer.get()).removeCallback(this.mSysUiStateCallback);
        ((WakefulnessLifecycle) this.mWakefulnessLifecycle.get()).removeObserver(this.mWakefulnessLifecycleObserver);
    }

    public void onAssistantGesturePerformed() {
        Context context = this.mContext;
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            int i = this.mLearningCount + 1;
            this.mLearningCount = i;
            Secure.putLong(contentResolver, "reminder_exp_learning_event_count", (long) i);
        }
    }

    public void onAssistHandlesRequested() {
        if (this.mAssistHandleCallbacks != null && isFullyAwake() && !this.mIsNavBarHidden && !this.mOnLockscreen) {
            this.mAssistHandleCallbacks.showAndGo();
        }
    }

    /* access modifiers changed from: private */
    public ComponentName getCurrentDefaultHome() {
        ArrayList arrayList = new ArrayList();
        ComponentName homeActivities = ((PackageManagerWrapper) this.mPackageManagerWrapper.get()).getHomeActivities(arrayList);
        if (homeActivities != null) {
            return homeActivities;
        }
        int i = Integer.MIN_VALUE;
        Iterator it = arrayList.iterator();
        while (true) {
            ComponentName componentName = null;
            while (true) {
                if (!it.hasNext()) {
                    return componentName;
                }
                ResolveInfo resolveInfo = (ResolveInfo) it.next();
                int i2 = resolveInfo.priority;
                if (i2 > i) {
                    componentName = resolveInfo.activityInfo.getComponentName();
                    i = resolveInfo.priority;
                } else if (i2 == i) {
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleStatusBarStateChanged(int i) {
        boolean onLockscreen = onLockscreen(i);
        if (this.mOnLockscreen != onLockscreen) {
            resetConsecutiveTaskSwitches();
            this.mOnLockscreen = onLockscreen;
            callbackForCurrentState(!onLockscreen);
        }
    }

    /* access modifiers changed from: private */
    public void handleDozingChanged(boolean z) {
        if (this.mIsDozing != z) {
            resetConsecutiveTaskSwitches();
            this.mIsDozing = z;
            callbackForCurrentState(false);
        }
    }

    /* access modifiers changed from: private */
    public void handleWakefullnessChanged(boolean z) {
        if (this.mIsAwake != z) {
            resetConsecutiveTaskSwitches();
            this.mIsAwake = z;
            callbackForCurrentState(false);
        }
    }

    /* access modifiers changed from: private */
    public void handleTaskStackTopChanged(int i, ComponentName componentName) {
        if (this.mRunningTaskId != i && componentName != null) {
            this.mRunningTaskId = i;
            boolean equals = componentName.equals(this.mDefaultHome);
            this.mIsLauncherShowing = equals;
            if (equals) {
                resetConsecutiveTaskSwitches();
            } else {
                rescheduleConsecutiveTaskSwitchesReset();
                this.mConsecutiveTaskSwitches++;
            }
            callbackForCurrentState(false);
        }
    }

    /* access modifiers changed from: private */
    public void handleSystemUiStateChanged(int i) {
        boolean z = (i & 2) != 0;
        if (this.mIsNavBarHidden != z) {
            resetConsecutiveTaskSwitches();
            this.mIsNavBarHidden = z;
            callbackForCurrentState(false);
        }
    }

    /* access modifiers changed from: private */
    public void handleOverviewShown() {
        resetConsecutiveTaskSwitches();
        callbackForCurrentState(false);
    }

    private void callbackForCurrentState(boolean z) {
        updateLearningStatus();
        if (this.mIsLearned) {
            callbackForLearnedState(z);
        } else {
            callbackForUnlearnedState();
        }
    }

    private void callbackForLearnedState(boolean z) {
        if (this.mAssistHandleCallbacks != null) {
            if (!isFullyAwake() || this.mIsNavBarHidden || this.mOnLockscreen || !getShowWhenTaught()) {
                this.mAssistHandleCallbacks.hide();
            } else if (z) {
                long epochDay = LocalDate.now().toEpochDay();
                if (this.mLearnedHintLastShownEpochDay < epochDay) {
                    Context context = this.mContext;
                    if (context != null) {
                        Secure.putLong(context.getContentResolver(), "reminder_exp_learned_hint_last_shown", epochDay);
                    }
                    this.mLearnedHintLastShownEpochDay = epochDay;
                    this.mAssistHandleCallbacks.showAndGo();
                }
            }
        }
    }

    private void callbackForUnlearnedState() {
        if (this.mAssistHandleCallbacks != null) {
            if (!isFullyAwake() || this.mIsNavBarHidden || isSuppressed()) {
                this.mAssistHandleCallbacks.hide();
            } else if (this.mOnLockscreen) {
                this.mAssistHandleCallbacks.showAndStay();
            } else if (this.mIsLauncherShowing) {
                this.mAssistHandleCallbacks.showAndGo();
            } else if (this.mConsecutiveTaskSwitches == 1) {
                this.mAssistHandleCallbacks.showAndGoDelayed(getShowAndGoDelayedShortDelayMs(), false);
            } else {
                this.mAssistHandleCallbacks.showAndGoDelayed(getShowAndGoDelayedLongDelayMs(), true);
            }
        }
    }

    private boolean isSuppressed() {
        if (this.mOnLockscreen) {
            return getSuppressOnLockscreen();
        }
        if (this.mIsLauncherShowing) {
            return getSuppressOnLauncher();
        }
        return getSuppressOnApps();
    }

    private void updateLearningStatus() {
        if (this.mContext != null) {
            long currentTimeMillis = this.mClock.currentTimeMillis();
            this.mLearningTimeElapsed += currentTimeMillis - this.mLastLearningTimestamp;
            this.mLastLearningTimestamp = currentTimeMillis;
            this.mIsLearned = this.mLearningCount >= getLearningCount() || this.mLearningTimeElapsed >= getLearningTimeMs();
            this.mHandler.post(new Runnable() {
                public final void run() {
                    AssistHandleReminderExpBehavior.this.lambda$updateLearningStatus$0$AssistHandleReminderExpBehavior();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateLearningStatus$0 */
    public /* synthetic */ void lambda$updateLearningStatus$0$AssistHandleReminderExpBehavior() {
        Secure.putLong(this.mContext.getContentResolver(), "reminder_exp_learning_time_elapsed", this.mLearningTimeElapsed);
    }

    /* access modifiers changed from: private */
    public void resetConsecutiveTaskSwitches() {
        this.mHandler.removeCallbacks(this.mResetConsecutiveTaskSwitches);
        this.mConsecutiveTaskSwitches = 0;
    }

    private void rescheduleConsecutiveTaskSwitchesReset() {
        this.mHandler.removeCallbacks(this.mResetConsecutiveTaskSwitches);
        this.mHandler.postDelayed(this.mResetConsecutiveTaskSwitches, getShowAndGoDelayResetTimeoutMs());
    }

    private boolean isFullyAwake() {
        return this.mIsAwake && !this.mIsDozing;
    }

    private long getLearningTimeMs() {
        return this.mDeviceConfigHelper.getLong("assist_handles_learn_time_ms", DEFAULT_LEARNING_TIME_MS);
    }

    private int getLearningCount() {
        return this.mDeviceConfigHelper.getInt("assist_handles_learn_count", 10);
    }

    private long getShowAndGoDelayedShortDelayMs() {
        return this.mDeviceConfigHelper.getLong("assist_handles_show_and_go_delayed_short_delay_ms", 150);
    }

    private long getShowAndGoDelayedLongDelayMs() {
        return this.mDeviceConfigHelper.getLong("assist_handles_show_and_go_delayed_long_delay_ms", DEFAULT_SHOW_AND_GO_DELAYED_LONG_DELAY_MS);
    }

    private long getShowAndGoDelayResetTimeoutMs() {
        return this.mDeviceConfigHelper.getLong("assist_handles_show_and_go_delay_reset_timeout_ms", DEFAULT_SHOW_AND_GO_DELAY_RESET_TIMEOUT_MS);
    }

    private boolean getSuppressOnLockscreen() {
        return this.mDeviceConfigHelper.getBoolean("assist_handles_suppress_on_lockscreen", false);
    }

    private boolean getSuppressOnLauncher() {
        return this.mDeviceConfigHelper.getBoolean("assist_handles_suppress_on_launcher", false);
    }

    private boolean getSuppressOnApps() {
        return this.mDeviceConfigHelper.getBoolean("assist_handles_suppress_on_apps", true);
    }

    private boolean getShowWhenTaught() {
        return this.mDeviceConfigHelper.getBoolean("assist_handles_show_when_taught", false);
    }

    public void dump(PrintWriter printWriter, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("Current AssistHandleReminderExpBehavior State:");
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append("   mOnLockscreen=");
        sb2.append(this.mOnLockscreen);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append("   mIsDozing=");
        sb3.append(this.mIsDozing);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str);
        sb4.append("   mIsAwake=");
        sb4.append(this.mIsAwake);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append(str);
        sb5.append("   mRunningTaskId=");
        sb5.append(this.mRunningTaskId);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append(str);
        sb6.append("   mDefaultHome=");
        sb6.append(this.mDefaultHome);
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append(str);
        sb7.append("   mIsNavBarHidden=");
        sb7.append(this.mIsNavBarHidden);
        printWriter.println(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append(str);
        sb8.append("   mIsLauncherShowing=");
        sb8.append(this.mIsLauncherShowing);
        printWriter.println(sb8.toString());
        StringBuilder sb9 = new StringBuilder();
        sb9.append(str);
        sb9.append("   mConsecutiveTaskSwitches=");
        sb9.append(this.mConsecutiveTaskSwitches);
        printWriter.println(sb9.toString());
        StringBuilder sb10 = new StringBuilder();
        sb10.append(str);
        sb10.append("   mIsLearned=");
        sb10.append(this.mIsLearned);
        printWriter.println(sb10.toString());
        StringBuilder sb11 = new StringBuilder();
        sb11.append(str);
        sb11.append("   mLastLearningTimestamp=");
        sb11.append(this.mLastLearningTimestamp);
        printWriter.println(sb11.toString());
        StringBuilder sb12 = new StringBuilder();
        sb12.append(str);
        sb12.append("   mLearningTimeElapsed=");
        sb12.append(this.mLearningTimeElapsed);
        printWriter.println(sb12.toString());
        StringBuilder sb13 = new StringBuilder();
        sb13.append(str);
        sb13.append("   mLearningCount=");
        sb13.append(this.mLearningCount);
        printWriter.println(sb13.toString());
        StringBuilder sb14 = new StringBuilder();
        sb14.append(str);
        sb14.append("   mLearnedHintLastShownEpochDay=");
        sb14.append(this.mLearnedHintLastShownEpochDay);
        printWriter.println(sb14.toString());
        StringBuilder sb15 = new StringBuilder();
        sb15.append(str);
        sb15.append("   mAssistHandleCallbacks present: ");
        sb15.append(this.mAssistHandleCallbacks != null);
        printWriter.println(sb15.toString());
        StringBuilder sb16 = new StringBuilder();
        sb16.append(str);
        sb16.append("   Phenotype Flags:");
        printWriter.println(sb16.toString());
        StringBuilder sb17 = new StringBuilder();
        sb17.append(str);
        String str2 = "      ";
        sb17.append(str2);
        sb17.append("assist_handles_learn_time_ms");
        String str3 = "=";
        sb17.append(str3);
        sb17.append(getLearningTimeMs());
        printWriter.println(sb17.toString());
        StringBuilder sb18 = new StringBuilder();
        sb18.append(str);
        sb18.append(str2);
        sb18.append("assist_handles_learn_count");
        sb18.append(str3);
        sb18.append(getLearningCount());
        printWriter.println(sb18.toString());
        StringBuilder sb19 = new StringBuilder();
        sb19.append(str);
        sb19.append(str2);
        sb19.append("assist_handles_show_and_go_delayed_short_delay_ms");
        sb19.append(str3);
        sb19.append(getShowAndGoDelayedShortDelayMs());
        printWriter.println(sb19.toString());
        StringBuilder sb20 = new StringBuilder();
        sb20.append(str);
        sb20.append(str2);
        sb20.append("assist_handles_show_and_go_delayed_long_delay_ms");
        sb20.append(str3);
        sb20.append(getShowAndGoDelayedLongDelayMs());
        printWriter.println(sb20.toString());
        StringBuilder sb21 = new StringBuilder();
        sb21.append(str);
        sb21.append(str2);
        sb21.append("assist_handles_show_and_go_delay_reset_timeout_ms");
        sb21.append(str3);
        sb21.append(getShowAndGoDelayResetTimeoutMs());
        printWriter.println(sb21.toString());
        StringBuilder sb22 = new StringBuilder();
        sb22.append(str);
        sb22.append(str2);
        sb22.append("assist_handles_suppress_on_lockscreen");
        sb22.append(str3);
        sb22.append(getSuppressOnLockscreen());
        printWriter.println(sb22.toString());
        StringBuilder sb23 = new StringBuilder();
        sb23.append(str);
        sb23.append(str2);
        sb23.append("assist_handles_suppress_on_launcher");
        sb23.append(str3);
        sb23.append(getSuppressOnLauncher());
        printWriter.println(sb23.toString());
        StringBuilder sb24 = new StringBuilder();
        sb24.append(str);
        sb24.append(str2);
        sb24.append("assist_handles_suppress_on_apps");
        sb24.append(str3);
        sb24.append(getSuppressOnApps());
        printWriter.println(sb24.toString());
        StringBuilder sb25 = new StringBuilder();
        sb25.append(str);
        sb25.append(str2);
        sb25.append("assist_handles_show_when_taught");
        sb25.append(str3);
        sb25.append(getShowWhenTaught());
        printWriter.println(sb25.toString());
    }
}
