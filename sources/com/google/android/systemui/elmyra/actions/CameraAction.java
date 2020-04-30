package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import com.android.systemui.C2017R$string;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.ArrayList;
import java.util.List;

public class CameraAction extends ServiceAction {
    private final String mCameraPackageName;
    private final StatusBar mStatusBar;

    public static class Builder {
        private final Context mContext;
        List<FeedbackEffect> mFeedbackEffects = new ArrayList();
        private final StatusBar mStatusBar;

        public Builder(Context context, StatusBar statusBar) {
            this.mContext = context;
            this.mStatusBar = statusBar;
        }

        public Builder addFeedbackEffect(FeedbackEffect feedbackEffect) {
            this.mFeedbackEffects.add(feedbackEffect);
            return this;
        }

        public CameraAction build() {
            return new CameraAction(this.mContext, this.mStatusBar, this.mFeedbackEffects);
        }
    }

    private CameraAction(Context context, StatusBar statusBar, List<FeedbackEffect> list) {
        super(context, list);
        this.mCameraPackageName = context.getResources().getString(C2017R$string.google_camera_app_package_name);
        this.mStatusBar = statusBar;
    }

    /* access modifiers changed from: protected */
    public boolean checkSupportedCaller() {
        return checkSupportedCaller(this.mCameraPackageName);
    }

    public void onTrigger(DetectionProperties detectionProperties) {
        this.mStatusBar.collapseShade();
        super.onTrigger(detectionProperties);
    }
}
