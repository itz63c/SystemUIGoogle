package com.android.systemui.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Window;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.broadcast.BroadcastDispatcher;

public class BrightnessDialog extends Activity {
    private BrightnessController mBrightnessController;
    private final BroadcastDispatcher mBroadcastDispatcher;

    public BrightnessDialog(BroadcastDispatcher broadcastDispatcher) {
        this.mBroadcastDispatcher = broadcastDispatcher;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        window.setGravity(48);
        window.clearFlags(2);
        window.requestFeature(1);
        setContentView(LayoutInflater.from(this).inflate(C2013R$layout.quick_settings_brightness_dialog, null));
        this.mBrightnessController = new BrightnessController(this, (ToggleSliderView) findViewById(C2011R$id.brightness_slider), this.mBroadcastDispatcher);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.mBrightnessController.registerCallbacks();
        MetricsLogger.visible(this, 220);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        MetricsLogger.hidden(this, 220);
        this.mBrightnessController.unregisterCallbacks();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 25 || i == 24 || i == 164) {
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
