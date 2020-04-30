package com.android.systemui.shared.system;

import android.content.Context;
import android.os.Bundle;
import android.util.Size;
import android.view.SurfaceControl;
import android.view.SurfaceControlViewHost;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.WindowlessWindowManager;

public class SurfaceViewRequestReceiver {
    private final int mOpacity;
    private SurfaceControlViewHost mSurfaceControlViewHost;

    public SurfaceViewRequestReceiver() {
        this(-2);
    }

    public SurfaceViewRequestReceiver(int i) {
        this.mOpacity = i;
    }

    public void onReceive(Context context, Bundle bundle, View view) {
        onReceive(context, bundle, view, null);
    }

    public void onReceive(Context context, Bundle bundle, View view, Size size) {
        SurfaceControlViewHost surfaceControlViewHost = this.mSurfaceControlViewHost;
        if (surfaceControlViewHost != null) {
            surfaceControlViewHost.die();
        }
        SurfaceControl surfaceControl = SurfaceViewRequestUtils.getSurfaceControl(bundle);
        if (surfaceControl != null) {
            if (size == null) {
                size = new Size(surfaceControl.getWidth(), surfaceControl.getHeight());
            }
            this.mSurfaceControlViewHost = new SurfaceControlViewHost(context, context.getDisplayNoVerify(), new WindowlessWindowManager(context.getResources().getConfiguration(), surfaceControl, SurfaceViewRequestUtils.getHostToken(bundle)));
            LayoutParams layoutParams = new LayoutParams(size.getWidth(), size.getHeight(), 2, 16777216, this.mOpacity);
            float min = Math.min(((float) surfaceControl.getWidth()) / ((float) size.getWidth()), ((float) surfaceControl.getHeight()) / ((float) size.getHeight()));
            view.setScaleX(min);
            view.setScaleY(min);
            view.setPivotX(0.0f);
            view.setPivotY(0.0f);
            view.setTranslationX((((float) surfaceControl.getWidth()) - (((float) size.getWidth()) * min)) / 2.0f);
            view.setTranslationY((((float) surfaceControl.getHeight()) - (min * ((float) size.getHeight()))) / 2.0f);
            this.mSurfaceControlViewHost.setView(view, layoutParams);
        }
    }
}
