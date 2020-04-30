package com.android.systemui.assist;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.DeviceConfig.OnPropertiesChangedListener;
import android.provider.DeviceConfig.Properties;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.AssistUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.inject.Provider;

public final class AssistHandleBehaviorController implements AssistHandleCallbacks, Dumpable {
    private static final AssistHandleBehavior DEFAULT_BEHAVIOR = AssistHandleBehavior.REMINDER_EXP;
    private static final long DEFAULT_SHOW_AND_GO_DURATION_MS = TimeUnit.SECONDS.toMillis(3);
    private final Provider<AssistHandleViewController> mAssistHandleViewController;
    private final AssistUtils mAssistUtils;
    private final Map<AssistHandleBehavior, BehaviorController> mBehaviorMap;
    private final Context mContext;
    private AssistHandleBehavior mCurrentBehavior = AssistHandleBehavior.OFF;
    private final DeviceConfigHelper mDeviceConfigHelper;
    private final Handler mHandler;
    private long mHandlesLastHiddenAt;
    private boolean mHandlesShowing = false;
    private final Runnable mHideHandles = new Runnable() {
        public final void run() {
            AssistHandleBehaviorController.this.hideHandles();
        }
    };
    private boolean mInGesturalMode;
    private final Runnable mShowAndGo = new Runnable() {
        public final void run() {
            AssistHandleBehaviorController.this.showAndGoInternal();
        }
    };
    private long mShowAndGoEndsAt;

    interface BehaviorController {
        void dump(PrintWriter printWriter, String str) {
        }

        void onAssistHandlesRequested() {
        }

        void onAssistantGesturePerformed() {
        }

        void onModeActivated(Context context, AssistHandleCallbacks assistHandleCallbacks);

        void onModeDeactivated() {
        }
    }

    AssistHandleBehaviorController(Context context, AssistUtils assistUtils, Handler handler, Provider<AssistHandleViewController> provider, DeviceConfigHelper deviceConfigHelper, Map<AssistHandleBehavior, BehaviorController> map, NavigationModeController navigationModeController, DumpManager dumpManager) {
        this.mContext = context;
        this.mAssistUtils = assistUtils;
        this.mHandler = handler;
        this.mAssistHandleViewController = provider;
        this.mDeviceConfigHelper = deviceConfigHelper;
        this.mBehaviorMap = map;
        this.mInGesturalMode = QuickStepContract.isGesturalMode(navigationModeController.addListener(new ModeChangedListener() {
            public final void onNavigationModeChanged(int i) {
                AssistHandleBehaviorController.this.handleNavigationModeChange(i);
            }
        }));
        setBehavior(getBehaviorMode());
        DeviceConfigHelper deviceConfigHelper2 = this.mDeviceConfigHelper;
        Handler handler2 = this.mHandler;
        Objects.requireNonNull(handler2);
        deviceConfigHelper2.addOnPropertiesChangedListener(new Executor(handler2) {
            public final /* synthetic */ Handler f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(Runnable runnable) {
                this.f$0.post(runnable);
            }
        }, new OnPropertiesChangedListener() {
            public final void onPropertiesChanged(Properties properties) {
                AssistHandleBehaviorController.this.lambda$new$0$AssistHandleBehaviorController(properties);
            }
        });
        dumpManager.registerDumpable("AssistHandleBehavior", this);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$AssistHandleBehaviorController(Properties properties) {
        String str = "assist_handles_behavior_mode";
        if (properties.getKeyset().contains(str)) {
            setBehavior(properties.getString(str, null));
        }
    }

    public void hide() {
        clearPendingCommands();
        this.mHandler.post(this.mHideHandles);
    }

    public void showAndGo() {
        clearPendingCommands();
        this.mHandler.post(this.mShowAndGo);
    }

    /* access modifiers changed from: private */
    public void showAndGoInternal() {
        maybeShowHandles(false);
        long showAndGoDuration = getShowAndGoDuration();
        this.mShowAndGoEndsAt = SystemClock.elapsedRealtime() + showAndGoDuration;
        this.mHandler.postDelayed(this.mHideHandles, showAndGoDuration);
    }

    public void showAndGoDelayed(long j, boolean z) {
        clearPendingCommands();
        if (z) {
            this.mHandler.post(this.mHideHandles);
        }
        this.mHandler.postDelayed(this.mShowAndGo, j);
    }

    public void showAndStay() {
        clearPendingCommands();
        this.mHandler.post(new Runnable() {
            public final void run() {
                AssistHandleBehaviorController.this.lambda$showAndStay$1$AssistHandleBehaviorController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showAndStay$1 */
    public /* synthetic */ void lambda$showAndStay$1$AssistHandleBehaviorController() {
        maybeShowHandles(true);
    }

    public long getShowAndGoRemainingTimeMs() {
        return Long.max(this.mShowAndGoEndsAt - SystemClock.elapsedRealtime(), 0);
    }

    /* access modifiers changed from: 0000 */
    public boolean areHandlesShowing() {
        return this.mHandlesShowing;
    }

    /* access modifiers changed from: 0000 */
    public void onAssistantGesturePerformed() {
        ((BehaviorController) this.mBehaviorMap.get(this.mCurrentBehavior)).onAssistantGesturePerformed();
    }

    /* access modifiers changed from: 0000 */
    public void onAssistHandlesRequested() {
        if (this.mInGesturalMode) {
            ((BehaviorController) this.mBehaviorMap.get(this.mCurrentBehavior)).onAssistHandlesRequested();
        }
    }

    /* access modifiers changed from: 0000 */
    public void setBehavior(AssistHandleBehavior assistHandleBehavior) {
        if (this.mCurrentBehavior != assistHandleBehavior) {
            if (!this.mBehaviorMap.containsKey(assistHandleBehavior)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Unsupported behavior requested: ");
                sb.append(assistHandleBehavior.toString());
                Log.e("AssistHandleBehavior", sb.toString());
                return;
            }
            if (this.mInGesturalMode) {
                ((BehaviorController) this.mBehaviorMap.get(this.mCurrentBehavior)).onModeDeactivated();
                ((BehaviorController) this.mBehaviorMap.get(assistHandleBehavior)).onModeActivated(this.mContext, this);
            }
            this.mCurrentBehavior = assistHandleBehavior;
        }
    }

    private void setBehavior(String str) {
        try {
            setBehavior(AssistHandleBehavior.valueOf(str));
        } catch (IllegalArgumentException | NullPointerException unused) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid behavior: ");
            sb.append(str);
            Log.e("AssistHandleBehavior", sb.toString());
        }
    }

    private boolean handlesUnblocked(boolean z) {
        boolean z2 = z || SystemClock.elapsedRealtime() - this.mHandlesLastHiddenAt >= getShownFrequencyThreshold();
        ComponentName assistComponentForUser = this.mAssistUtils.getAssistComponentForUser(KeyguardUpdateMonitor.getCurrentUser());
        if (!z2 || assistComponentForUser == null) {
            return false;
        }
        return true;
    }

    private long getShownFrequencyThreshold() {
        return this.mDeviceConfigHelper.getLong("assist_handles_shown_frequency_threshold_ms", 0);
    }

    private long getShowAndGoDuration() {
        return this.mDeviceConfigHelper.getLong("assist_handles_show_and_go_duration_ms", DEFAULT_SHOW_AND_GO_DURATION_MS);
    }

    private String getBehaviorMode() {
        return this.mDeviceConfigHelper.getString("assist_handles_behavior_mode", DEFAULT_BEHAVIOR.toString());
    }

    private void maybeShowHandles(boolean z) {
        if (!this.mHandlesShowing && handlesUnblocked(z)) {
            this.mHandlesShowing = true;
            AssistHandleViewController assistHandleViewController = (AssistHandleViewController) this.mAssistHandleViewController.get();
            if (assistHandleViewController == null) {
                Log.w("AssistHandleBehavior", "Couldn't show handles, AssistHandleViewController unavailable");
            } else {
                assistHandleViewController.lambda$setAssistHintVisible$0(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void hideHandles() {
        if (this.mHandlesShowing) {
            this.mHandlesShowing = false;
            this.mHandlesLastHiddenAt = SystemClock.elapsedRealtime();
            AssistHandleViewController assistHandleViewController = (AssistHandleViewController) this.mAssistHandleViewController.get();
            if (assistHandleViewController == null) {
                Log.w("AssistHandleBehavior", "Couldn't show handles, AssistHandleViewController unavailable");
            } else {
                assistHandleViewController.lambda$setAssistHintVisible$0(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleNavigationModeChange(int i) {
        boolean isGesturalMode = QuickStepContract.isGesturalMode(i);
        if (this.mInGesturalMode != isGesturalMode) {
            this.mInGesturalMode = isGesturalMode;
            if (isGesturalMode) {
                ((BehaviorController) this.mBehaviorMap.get(this.mCurrentBehavior)).onModeActivated(this.mContext, this);
            } else {
                ((BehaviorController) this.mBehaviorMap.get(this.mCurrentBehavior)).onModeDeactivated();
                hide();
            }
        }
    }

    private void clearPendingCommands() {
        this.mHandler.removeCallbacks(this.mHideHandles);
        this.mHandler.removeCallbacks(this.mShowAndGo);
        this.mShowAndGoEndsAt = 0;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setInGesturalModeForTest(boolean z) {
        this.mInGesturalMode = z;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("Current AssistHandleBehaviorController State:");
        StringBuilder sb = new StringBuilder();
        sb.append("   mHandlesShowing=");
        sb.append(this.mHandlesShowing);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("   mHandlesLastHiddenAt=");
        sb2.append(this.mHandlesLastHiddenAt);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("   mInGesturalMode=");
        sb3.append(this.mInGesturalMode);
        printWriter.println(sb3.toString());
        printWriter.println("   Phenotype Flags:");
        StringBuilder sb4 = new StringBuilder();
        sb4.append("      assist_handles_show_and_go_duration_ms=");
        sb4.append(getShowAndGoDuration());
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("      assist_handles_shown_frequency_threshold_ms=");
        sb5.append(getShownFrequencyThreshold());
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("      assist_handles_behavior_mode=");
        sb6.append(getBehaviorMode());
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append("   mCurrentBehavior=");
        sb7.append(this.mCurrentBehavior.toString());
        printWriter.println(sb7.toString());
        ((BehaviorController) this.mBehaviorMap.get(this.mCurrentBehavior)).dump(printWriter, "   ");
    }
}
