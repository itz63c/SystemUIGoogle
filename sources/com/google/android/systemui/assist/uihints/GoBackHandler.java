package com.google.android.systemui.assist.uihints;

import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.view.KeyEvent;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.GoBackListener;

class GoBackHandler implements GoBackListener {
    GoBackHandler() {
    }

    public void onGoBack() {
        injectBackKeyEvent(0);
        injectBackKeyEvent(1);
    }

    private void injectBackKeyEvent(int i) {
        long uptimeMillis = SystemClock.uptimeMillis();
        InputManager instance = InputManager.getInstance();
        KeyEvent keyEvent = new KeyEvent(uptimeMillis, uptimeMillis, i, 4, 0, 0, -1, 0, 72, 257);
        instance.injectInputEvent(keyEvent, 0);
    }
}
