package com.android.systemui.volume;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnTouchListener;

public class Interaction {

    public interface Callback {
        void onInteraction();
    }

    public static void register(View view, final Callback callback) {
        view.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Callback.this.onInteraction();
                return false;
            }
        });
        view.setOnGenericMotionListener(new OnGenericMotionListener() {
            public boolean onGenericMotion(View view, MotionEvent motionEvent) {
                Callback.this.onInteraction();
                return false;
            }
        });
    }
}
