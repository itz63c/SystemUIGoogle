package com.google.android.systemui.elmyra.actions;

import android.app.KeyguardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import com.android.systemui.Dependency;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import com.google.android.systemui.assist.AssistManagerGoogle;
import com.google.android.systemui.assist.OpaEnabledListener;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LaunchOpa extends Action implements Tunable {
    private final AssistManager mAssistManager;
    /* access modifiers changed from: private */
    public boolean mEnableForAnyAssistant;
    private boolean mIsGestureEnabled;
    /* access modifiers changed from: private */
    public boolean mIsOpaEnabled;
    private final KeyguardManager mKeyguardManager;
    private final OpaEnabledListener mOpaEnabledListener;
    private final StatusBar mStatusBar;

    public static class Builder {
        private final Context mContext;
        List<FeedbackEffect> mFeedbackEffects = new ArrayList();
        private final StatusBar mStatusBar;

        public Builder(Context context, StatusBar statusBar) {
            this.mContext = context;
            this.mStatusBar = statusBar;
        }

        public Builder addFeedbackEffect(FeedbackEffect feedbackEffect) {
            this.mFeedbackEffects.add(feedbackEffect);
            return this;
        }

        public LaunchOpa build() {
            return new LaunchOpa(this.mContext, this.mStatusBar, this.mFeedbackEffects);
        }
    }

    private LaunchOpa(Context context, StatusBar statusBar, List<FeedbackEffect> list) {
        super(context, list);
        this.mOpaEnabledListener = new OpaEnabledListener() {
            public void onOpaEnabledReceived(Context context, boolean z, boolean z2, boolean z3) {
                boolean z4 = false;
                boolean z5 = z2 || LaunchOpa.this.mEnableForAnyAssistant;
                if (z && z5 && z3) {
                    z4 = true;
                }
                if (LaunchOpa.this.mIsOpaEnabled != z4) {
                    LaunchOpa.this.mIsOpaEnabled = z4;
                    LaunchOpa.this.notifyListener();
                }
            }
        };
        this.mStatusBar = statusBar;
        this.mAssistManager = (AssistManager) Dependency.get(AssistManager.class);
        this.mKeyguardManager = (KeyguardManager) getContext().getSystemService("keyguard");
        this.mIsGestureEnabled = isGestureEnabled();
        new UserContentObserver(getContext(), Secure.getUriFor("assist_gesture_enabled"), new Consumer() {
            public final void accept(Object obj) {
                LaunchOpa.this.lambda$new$0$LaunchOpa((Uri) obj);
            }
        });
        String str = "assist_gesture_any_assistant";
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, str);
        boolean z = false;
        if (Secure.getInt(getContext().getContentResolver(), str, 0) == 1) {
            z = true;
        }
        this.mEnableForAnyAssistant = z;
        ((AssistManagerGoogle) this.mAssistManager).addOpaEnabledListener(this.mOpaEnabledListener);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$LaunchOpa(Uri uri) {
        updateGestureEnabled();
    }

    private void updateGestureEnabled() {
        boolean isGestureEnabled = isGestureEnabled();
        if (this.mIsGestureEnabled != isGestureEnabled) {
            this.mIsGestureEnabled = isGestureEnabled;
            notifyListener();
        }
    }

    private boolean isGestureEnabled() {
        if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_enabled", 1, -2) != 0) {
            return true;
        }
        return false;
    }

    public void onTuningChanged(String str, String str2) {
        if ("assist_gesture_any_assistant".equals(str)) {
            this.mEnableForAnyAssistant = "1".equals(str2);
            ((AssistManagerGoogle) this.mAssistManager).dispatchOpaEnabledState();
        }
    }

    public boolean isAvailable() {
        return this.mIsGestureEnabled && this.mIsOpaEnabled;
    }

    public void onProgress(float f, int i) {
        updateFeedbackEffects(f, i);
    }

    public void launchOpa() {
        launchOpa(0);
    }

    public void launchOpa(long j) {
        Bundle bundle = new Bundle();
        bundle.putInt("triggered_by", this.mKeyguardManager.isKeyguardLocked() ? 14 : 13);
        bundle.putLong("latency_id", j);
        bundle.putInt("invocation_type", 2);
        this.mAssistManager.startAssist(bundle);
    }

    public void onTrigger(DetectionProperties detectionProperties) {
        this.mStatusBar.collapseShade();
        triggerFeedbackEffects(detectionProperties);
        launchOpa(detectionProperties != null ? detectionProperties.getActionId() : 0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mIsGestureEnabled -> ");
        sb.append(this.mIsGestureEnabled);
        sb.append("; mIsOpaEnabled -> ");
        sb.append(this.mIsOpaEnabled);
        sb.append("]");
        return sb.toString();
    }
}
