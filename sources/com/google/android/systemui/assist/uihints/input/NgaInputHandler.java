package com.google.android.systemui.assist.uihints.input;

import android.graphics.Region;
import android.graphics.Region.Op;
import android.hardware.input.InputManager;
import android.os.Looper;
import android.util.Log;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.InputMonitor;
import android.view.MotionEvent;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.EdgeLightsInfoListener;
import com.google.android.systemui.assist.uihints.TouchInsideHandler;
import java.util.Set;
import java.util.function.Consumer;

public class NgaInputHandler implements EdgeLightsInfoListener {
    private InputEventReceiver mInputEventReceiver;
    private InputMonitor mInputMonitor;
    private final Set<TouchActionRegion> mTouchActionRegions;
    private final TouchInsideHandler mTouchInsideHandler;
    private final Set<TouchInsideRegion> mTouchInsideRegions;

    private class NgaInputEventReceiver extends InputEventReceiver {
        private NgaInputEventReceiver(InputChannel inputChannel) {
            super(inputChannel, Looper.getMainLooper());
        }

        public void onInputEvent(InputEvent inputEvent) {
            if (inputEvent instanceof MotionEvent) {
                NgaInputHandler.this.handleMotionEvent((MotionEvent) inputEvent);
            }
            finishInputEvent(inputEvent, false);
        }
    }

    NgaInputHandler(TouchInsideHandler touchInsideHandler, Set<TouchActionRegion> set, Set<TouchInsideRegion> set2) {
        this.mTouchInsideHandler = touchInsideHandler;
        this.mTouchActionRegions = set;
        this.mTouchInsideRegions = set2;
    }

    public void onEdgeLightsInfo(String str, boolean z) {
        if ("HALF_LISTENING".equals(str)) {
            startMonitoring();
        } else {
            stopMonitoring();
        }
    }

    private void startMonitoring() {
        String str = "NgaInputHandler";
        if (this.mInputEventReceiver == null && this.mInputMonitor == null) {
            this.mInputMonitor = InputManager.getInstance().monitorGestureInput(str, 0);
            this.mInputEventReceiver = new NgaInputEventReceiver(this.mInputMonitor.getInputChannel());
            return;
        }
        Log.w(str, "Already monitoring");
    }

    private void stopMonitoring() {
        InputEventReceiver inputEventReceiver = this.mInputEventReceiver;
        if (inputEventReceiver != null) {
            inputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        InputMonitor inputMonitor = this.mInputMonitor;
        if (inputMonitor != null) {
            inputMonitor.dispose();
            this.mInputMonitor = null;
        }
    }

    /* access modifiers changed from: private */
    public void handleMotionEvent(MotionEvent motionEvent) {
        int rawX = (int) motionEvent.getRawX();
        int rawY = (int) motionEvent.getRawY();
        Region region = new Region();
        for (TouchInsideRegion touchInsideRegion : this.mTouchInsideRegions) {
            touchInsideRegion.getTouchInsideRegion().ifPresent(new Consumer(region) {
                public final /* synthetic */ Region f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    this.f$0.op((Region) obj, Op.UNION);
                }
            });
        }
        for (TouchActionRegion touchActionRegion : this.mTouchActionRegions) {
            touchActionRegion.getTouchActionRegion().ifPresent(new Consumer(region) {
                public final /* synthetic */ Region f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    this.f$0.op((Region) obj, Op.DIFFERENCE);
                }
            });
        }
        if (region.contains(rawX, rawY)) {
            this.mTouchInsideHandler.onTouchInside();
        }
    }
}
