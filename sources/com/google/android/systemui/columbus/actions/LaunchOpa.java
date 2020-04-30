package com.google.android.systemui.columbus.actions;

import android.app.KeyguardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import com.google.android.systemui.assist.AssistManagerGoogle;
import com.google.android.systemui.assist.OpaEnabledListener;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.ColumbusContentObserver.Factory;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: LaunchOpa.kt */
public class LaunchOpa extends Action implements Tunable {
    private final AssistManagerGoogle assistManager;
    /* access modifiers changed from: private */
    public boolean enableForAnyAssistant;
    private boolean isGestureEnabled;
    /* access modifiers changed from: private */
    public boolean isOpaEnabled;
    private final KeyguardManager keyguardManager;
    private final OpaEnabledListener opaEnabledListener;
    private final ColumbusContentObserver settingsObserver;
    private final StatusBar statusBar;

    public LaunchOpa(Context context, StatusBar statusBar2, Set<FeedbackEffect> set, AssistManager assistManager2, TunerService tunerService, Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(statusBar2, "statusBar");
        Intrinsics.checkParameterIsNotNull(set, "feedbackEffects");
        Intrinsics.checkParameterIsNotNull(assistManager2, "assistManager");
        Intrinsics.checkParameterIsNotNull(tunerService, "tunerService");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context, CollectionsKt___CollectionsKt.toList(set));
        this.statusBar = statusBar2;
        if (!(assistManager2 instanceof AssistManagerGoogle)) {
            assistManager2 = null;
        }
        this.assistManager = (AssistManagerGoogle) assistManager2;
        this.keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
        this.opaEnabledListener = new LaunchOpa$opaEnabledListener$1(this);
        Uri uriFor = Secure.getUriFor("assist_gesture_enabled");
        Intrinsics.checkExpressionValueIsNotNull(uriFor, "Settings.Secure.getUriFo…e.ASSIST_GESTURE_ENABLED)");
        this.settingsObserver = factory.create(uriFor, new LaunchOpa$settingsObserver$1(this));
        this.isGestureEnabled = fetchIsGestureEnabled();
        String str = "assist_gesture_any_assistant";
        boolean z = false;
        if (Secure.getInt(getContext().getContentResolver(), str, 0) == 1) {
            z = true;
        }
        this.enableForAnyAssistant = z;
        this.settingsObserver.activate();
        tunerService.addTunable(this, str);
        AssistManagerGoogle assistManagerGoogle = this.assistManager;
        if (assistManagerGoogle != null) {
            assistManagerGoogle.addOpaEnabledListener(this.opaEnabledListener);
        }
    }

    /* access modifiers changed from: private */
    public final void updateGestureEnabled() {
        boolean fetchIsGestureEnabled = fetchIsGestureEnabled();
        if (this.isGestureEnabled != fetchIsGestureEnabled) {
            this.isGestureEnabled = fetchIsGestureEnabled;
            notifyListener();
        }
    }

    private final boolean fetchIsGestureEnabled() {
        if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_enabled", 1, -2) != 0) {
            return true;
        }
        return false;
    }

    public void onTuningChanged(String str, String str2) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        if (Intrinsics.areEqual((Object) "assist_gesture_any_assistant", (Object) str)) {
            this.enableForAnyAssistant = Intrinsics.areEqual((Object) "1", (Object) str2);
            AssistManagerGoogle assistManagerGoogle = this.assistManager;
            if (assistManagerGoogle != null) {
                assistManagerGoogle.dispatchOpaEnabledState();
            }
        }
    }

    public boolean isAvailable() {
        return this.isGestureEnabled && this.isOpaEnabled;
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        updateFeedbackEffects(i, detectionProperties);
        if (i == 3) {
            this.statusBar.collapseShade();
            launchOpa(detectionProperties != null ? detectionProperties.getActionId() : 0);
        }
    }

    public void onTrigger() {
        launchOpa();
    }

    private final void launchOpa() {
        launchOpa(0);
    }

    private final void launchOpa(long j) {
        Bundle bundle = new Bundle();
        KeyguardManager keyguardManager2 = this.keyguardManager;
        bundle.putInt("triggered_by", (keyguardManager2 == null || !keyguardManager2.isKeyguardLocked()) ? 13 : 14);
        bundle.putLong("latency_id", j);
        bundle.putInt("invocation_type", 2);
        AssistManagerGoogle assistManagerGoogle = this.assistManager;
        if (assistManagerGoogle != null) {
            assistManagerGoogle.startAssist(bundle);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [isGestureEnabled -> ");
        sb.append(this.isGestureEnabled);
        sb.append("; isOpaEnabled -> ");
        sb.append(this.isOpaEnabled);
        sb.append("]");
        return sb.toString();
    }
}
