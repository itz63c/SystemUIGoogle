package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.util.Log;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.Optional;
import java.util.function.Consumer;

public class UnpinNotifications extends Action {
    /* access modifiers changed from: private */
    public boolean mHasPinnedHeadsUp;
    private final OnHeadsUpChangedListener mHeadsUpChangedListener = new OnHeadsUpChangedListener() {
        public void onHeadsUpPinnedModeChanged(boolean z) {
            if (UnpinNotifications.this.mHasPinnedHeadsUp != z) {
                UnpinNotifications.this.mHasPinnedHeadsUp = z;
                UnpinNotifications.this.notifyListener();
            }
        }
    };
    private final Optional<HeadsUpManager> mHeadsUpManagerOptional;
    private boolean mSilenceSettingEnabled;

    public UnpinNotifications(Context context, Optional<HeadsUpManager> optional) {
        super(context, null);
        this.mHeadsUpManagerOptional = optional;
        if (optional.isPresent()) {
            updateHeadsUpListener();
            new UserContentObserver(getContext(), Secure.getUriFor("assist_gesture_silence_alerts_enabled"), new Consumer() {
                public final void accept(Object obj) {
                    UnpinNotifications.this.lambda$new$0$UnpinNotifications((Uri) obj);
                }
            });
            return;
        }
        Log.w("Elmyra/UnpinNotifications", "No HeadsUpManager");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$UnpinNotifications(Uri uri) {
        updateHeadsUpListener();
    }

    private void updateHeadsUpListener() {
        if (this.mHeadsUpManagerOptional.isPresent()) {
            boolean z = true;
            if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1, -2) == 0) {
                z = false;
            }
            if (this.mSilenceSettingEnabled != z) {
                this.mSilenceSettingEnabled = z;
                if (z) {
                    this.mHasPinnedHeadsUp = ((HeadsUpManager) this.mHeadsUpManagerOptional.get()).hasPinnedHeadsUp();
                    ((HeadsUpManager) this.mHeadsUpManagerOptional.get()).addListener(this.mHeadsUpChangedListener);
                } else {
                    this.mHasPinnedHeadsUp = false;
                    ((HeadsUpManager) this.mHeadsUpManagerOptional.get()).removeListener(this.mHeadsUpChangedListener);
                }
                notifyListener();
            }
        }
    }

    public boolean isAvailable() {
        if (this.mSilenceSettingEnabled) {
            return this.mHasPinnedHeadsUp;
        }
        return false;
    }

    public void onTrigger(DetectionProperties detectionProperties) {
        this.mHeadsUpManagerOptional.ifPresent($$Lambda$UnpinNotifications$2AIQLXUnga6EFVuQA5J5PzBLz_w.INSTANCE);
    }

    public String toString() {
        return super.toString();
    }
}
