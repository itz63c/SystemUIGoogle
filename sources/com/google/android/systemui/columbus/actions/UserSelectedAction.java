package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.os.Handler;
import android.provider.DeviceConfig.OnPropertiesChangedListener;
import android.util.Log;
import com.android.systemui.assist.DeviceConfigHelper;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import java.util.Map;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: UserSelectedAction.kt */
public final class UserSelectedAction extends Action {
    private final DeviceConfigHelper deviceConfigHelper;
    /* access modifiers changed from: private */
    public final Handler handler;
    private final LaunchOpa launchOpa;
    private final OnPropertiesChangedListener propertiesChangedListener = new UserSelectedAction$propertiesChangedListener$1(this);
    /* access modifiers changed from: private */
    public Action selectedAction = getSelectedAction$default(this, null, 1, null);
    private final Map<String, Action> userSelectedActions;

    public UserSelectedAction(Context context, DeviceConfigHelper deviceConfigHelper2, Map<String, Action> map, LaunchOpa launchOpa2, Handler handler2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(deviceConfigHelper2, "deviceConfigHelper");
        Intrinsics.checkParameterIsNotNull(map, "userSelectedActions");
        Intrinsics.checkParameterIsNotNull(launchOpa2, "launchOpa");
        Intrinsics.checkParameterIsNotNull(handler2, "handler");
        super(context, null);
        this.deviceConfigHelper = deviceConfigHelper2;
        this.userSelectedActions = map;
        this.launchOpa = launchOpa2;
        this.handler = handler2;
        StringBuilder sb = new StringBuilder();
        sb.append("User Action selected: ");
        sb.append(this.selectedAction);
        Log.i("Columbus/SelectedAction", sb.toString());
        this.deviceConfigHelper.addOnPropertiesChangedListener(new Executor(this) {
            final /* synthetic */ UserSelectedAction this$0;

            {
                this.this$0 = r1;
            }

            public final void execute(Runnable runnable) {
                this.this$0.handler.post(runnable);
            }
        }, this.propertiesChangedListener);
        UserSelectedAction$sublistener$1 userSelectedAction$sublistener$1 = new UserSelectedAction$sublistener$1(this);
        for (Action listener : this.userSelectedActions.values()) {
            listener.setListener(userSelectedAction$sublistener$1);
        }
    }

    public boolean isAvailable() {
        return this.selectedAction.isAvailable();
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        this.selectedAction.onProgress(i, detectionProperties);
    }

    public void updateFeedbackEffects(int i, DetectionProperties detectionProperties) {
        this.selectedAction.updateFeedbackEffects(i, detectionProperties);
    }

    public void onTrigger() {
        this.selectedAction.onTrigger();
    }

    public final boolean isAssistant() {
        return Intrinsics.areEqual((Object) this.selectedAction, (Object) this.launchOpa);
    }

    static /* synthetic */ Action getSelectedAction$default(UserSelectedAction userSelectedAction, String str, int i, Object obj) {
        if ((i & 1) != 0) {
            str = null;
        }
        return userSelectedAction.getSelectedAction(str);
    }

    /* access modifiers changed from: private */
    public final Action getSelectedAction(String str) {
        if (str == null) {
            str = this.deviceConfigHelper.getString("systemui_google_columbus_user_action", "assistant");
        }
        if (str == null) {
            str = "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Selected action name: ");
        sb.append(str);
        Log.i("Columbus/SelectedAction", sb.toString());
        return (Action) this.userSelectedActions.getOrDefault(str, this.launchOpa);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [selectedAction -> ");
        sb.append(this.selectedAction);
        sb.append("]");
        return sb.toString();
    }
}
