package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.view.KeyEvent;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ManageMedia.kt */
public final class ManageMedia extends Action {
    private final AudioManager audioManager;

    public ManageMedia(Context context, Handler handler) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        AudioManager audioManager2 = null;
        super(context, null);
        Object systemService = context.getSystemService("audio");
        if (systemService instanceof AudioManager) {
            audioManager2 = systemService;
        }
        this.audioManager = audioManager2;
    }

    public boolean isAvailable() {
        return this.audioManager != null;
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        if (i == 3) {
            onTrigger();
        }
    }

    public void onTrigger() {
        if (this.audioManager != null) {
            sendPlayPauseKeyEvent(0);
            sendPlayPauseKeyEvent(1);
        }
    }

    private final void sendPlayPauseKeyEvent(int i) {
        getContext().sendOrderedBroadcast(new Intent("android.intent.action.MEDIA_BUTTON").putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(i, 85)), null);
    }
}
