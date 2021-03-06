package com.android.systemui.accessibility;

import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import com.android.systemui.SystemUI;

public class WindowMagnification extends SystemUI {
    private final Handler mHandler;
    private Configuration mLastConfiguration;
    private WindowMagnificationController mWindowMagnificationController;

    public WindowMagnification(Context context, Handler handler) {
        super(context);
        this.mHandler = handler;
        this.mLastConfiguration = new Configuration(context.getResources().getConfiguration());
    }

    public void onConfigurationChanged(Configuration configuration) {
        int diff = configuration.diff(this.mLastConfiguration);
        if ((diff & 4096) != 0) {
            this.mLastConfiguration.setTo(configuration);
            WindowMagnificationController windowMagnificationController = this.mWindowMagnificationController;
            if (windowMagnificationController != null) {
                windowMagnificationController.onConfigurationChanged(diff);
            }
        }
    }

    public void start() {
        this.mContext.getContentResolver().registerContentObserver(Secure.getUriFor("window_magnification"), true, new ContentObserver(this.mHandler) {
            public void onChange(boolean z) {
                WindowMagnification.this.updateWindowMagnification();
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateWindowMagnification() {
        try {
            if (Secure.getInt(this.mContext.getContentResolver(), "window_magnification") != 0) {
                enableMagnification();
            } else {
                disableMagnification();
            }
        } catch (SettingNotFoundException unused) {
            disableMagnification();
        }
    }

    private void enableMagnification() {
        if (this.mWindowMagnificationController == null) {
            this.mWindowMagnificationController = new WindowMagnificationController(this.mContext, null);
        }
        this.mWindowMagnificationController.createWindowMagnification();
    }

    private void disableMagnification() {
        WindowMagnificationController windowMagnificationController = this.mWindowMagnificationController;
        if (windowMagnificationController != null) {
            windowMagnificationController.deleteWindowMagnification();
        }
        this.mWindowMagnificationController = null;
    }
}
