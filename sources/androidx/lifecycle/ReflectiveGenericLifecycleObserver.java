package androidx.lifecycle;

import androidx.lifecycle.Lifecycle.Event;

class ReflectiveGenericLifecycleObserver implements LifecycleEventObserver {
    private final CallbackInfo mInfo;
    private final Object mWrapped;

    ReflectiveGenericLifecycleObserver(Object obj) {
        this.mWrapped = obj;
        this.mInfo = ClassesInfoCache.sInstance.getInfo(obj.getClass());
    }

    public void onStateChanged(LifecycleOwner lifecycleOwner, Event event) {
        this.mInfo.invokeCallbacks(lifecycleOwner, event, this.mWrapped);
    }
}
