package com.android.systemui.util;

import android.app.Fragment;
import android.os.Bundle;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public class LifecycleFragment extends Fragment implements LifecycleOwner {
    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }

    public void onCreate(Bundle bundle) {
        this.mLifecycle.handleLifecycleEvent(Event.ON_CREATE);
        super.onCreate(bundle);
    }

    public void onStart() {
        this.mLifecycle.handleLifecycleEvent(Event.ON_START);
        super.onStart();
    }

    public void onResume() {
        this.mLifecycle.handleLifecycleEvent(Event.ON_RESUME);
        super.onResume();
    }

    public void onPause() {
        this.mLifecycle.handleLifecycleEvent(Event.ON_PAUSE);
        super.onPause();
    }

    public void onStop() {
        this.mLifecycle.handleLifecycleEvent(Event.ON_STOP);
        super.onStop();
    }

    public void onDestroy() {
        this.mLifecycle.handleLifecycleEvent(Event.ON_DESTROY);
        super.onDestroy();
    }
}
