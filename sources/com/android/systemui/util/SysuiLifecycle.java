package com.android.systemui.util;

import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public class SysuiLifecycle {

    private static class ViewLifecycle implements LifecycleOwner, OnAttachStateChangeListener {
        private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

        ViewLifecycle(View view) {
            view.addOnAttachStateChangeListener(this);
        }

        public Lifecycle getLifecycle() {
            return this.mLifecycle;
        }

        public void onViewAttachedToWindow(View view) {
            this.mLifecycle.markState(State.RESUMED);
        }

        public void onViewDetachedFromWindow(View view) {
            this.mLifecycle.markState(State.DESTROYED);
        }
    }

    public static LifecycleOwner viewAttachLifecycle(View view) {
        return new ViewLifecycle(view);
    }
}
