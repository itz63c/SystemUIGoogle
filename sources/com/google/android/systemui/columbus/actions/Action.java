package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Action.kt */
public abstract class Action {
    private final Context context;
    private final List<FeedbackEffect> feedbackEffects;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Listener listener;

    /* compiled from: Action.kt */
    public interface Listener {
        void onActionAvailabilityChanged(Action action);
    }

    public abstract boolean isAvailable();

    public void onProgress(int i, DetectionProperties detectionProperties) {
    }

    public void onTrigger() {
    }

    public Action(Context context2, List<? extends FeedbackEffect> list) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        this.context = context2;
        if (list == null) {
            list = CollectionsKt__CollectionsKt.emptyList();
        }
        this.feedbackEffects = CollectionsKt___CollectionsKt.toList(list);
    }

    /* access modifiers changed from: protected */
    public final Context getContext() {
        return this.context;
    }

    public Listener getListener() {
        return this.listener;
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }

    /* access modifiers changed from: protected */
    public final void notifyListener() {
        if (getListener() != null) {
            this.handler.post(new Action$notifyListener$1(this));
        }
        if (!isAvailable()) {
            this.handler.post(new Action$notifyListener$2(this));
        }
    }

    public void updateFeedbackEffects(int i, DetectionProperties detectionProperties) {
        for (FeedbackEffect onProgress : this.feedbackEffects) {
            onProgress.onProgress(i, detectionProperties);
        }
    }

    public String toString() {
        String simpleName = getClass().getSimpleName();
        Intrinsics.checkExpressionValueIsNotNull(simpleName, "javaClass.simpleName");
        return simpleName;
    }
}
