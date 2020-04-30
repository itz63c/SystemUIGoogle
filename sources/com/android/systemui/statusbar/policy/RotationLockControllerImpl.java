package com.android.systemui.statusbar.policy;

import android.content.Context;
import com.android.internal.view.RotationPolicy;
import com.android.internal.view.RotationPolicy.RotationPolicyListener;
import com.android.systemui.statusbar.policy.RotationLockController.RotationLockControllerCallback;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public final class RotationLockControllerImpl implements RotationLockController {
    private final CopyOnWriteArrayList<RotationLockControllerCallback> mCallbacks = new CopyOnWriteArrayList<>();
    private final Context mContext;
    private final RotationPolicyListener mRotationPolicyListener = new RotationPolicyListener() {
        public void onChange() {
            RotationLockControllerImpl.this.notifyChanged();
        }
    };

    public RotationLockControllerImpl(Context context) {
        this.mContext = context;
        setListening(true);
    }

    public void addCallback(RotationLockControllerCallback rotationLockControllerCallback) {
        this.mCallbacks.add(rotationLockControllerCallback);
        notifyChanged(rotationLockControllerCallback);
    }

    public void removeCallback(RotationLockControllerCallback rotationLockControllerCallback) {
        this.mCallbacks.remove(rotationLockControllerCallback);
    }

    public int getRotationLockOrientation() {
        return RotationPolicy.getRotationLockOrientation(this.mContext);
    }

    public boolean isRotationLocked() {
        return RotationPolicy.isRotationLocked(this.mContext);
    }

    public void setRotationLocked(boolean z) {
        RotationPolicy.setRotationLock(this.mContext, z);
    }

    public void setRotationLockedAtAngle(boolean z, int i) {
        RotationPolicy.setRotationLockAtAngle(this.mContext, z, i);
    }

    public void setListening(boolean z) {
        if (z) {
            RotationPolicy.registerRotationPolicyListener(this.mContext, this.mRotationPolicyListener, -1);
        } else {
            RotationPolicy.unregisterRotationPolicyListener(this.mContext, this.mRotationPolicyListener);
        }
    }

    /* access modifiers changed from: private */
    public void notifyChanged() {
        Iterator it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            notifyChanged((RotationLockControllerCallback) it.next());
        }
    }

    private void notifyChanged(RotationLockControllerCallback rotationLockControllerCallback) {
        rotationLockControllerCallback.onRotationLockStateChanged(RotationPolicy.isRotationLocked(this.mContext), RotationPolicy.isRotationLockToggleVisible(this.mContext));
    }
}
