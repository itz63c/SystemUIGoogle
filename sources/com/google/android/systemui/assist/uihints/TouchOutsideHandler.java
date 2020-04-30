package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.util.Log;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfo;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfoListener;

class TouchOutsideHandler implements ConfigInfoListener {
    private PendingIntent mTouchOutside;

    TouchOutsideHandler() {
    }

    public void onConfigInfo(ConfigInfo configInfo) {
        this.mTouchOutside = configInfo.onTouchOutside;
    }

    /* access modifiers changed from: 0000 */
    public void onTouchOutside() {
        PendingIntent pendingIntent = this.mTouchOutside;
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
            } catch (CanceledException unused) {
                Log.w("TouchOutsideHandler", "Touch outside PendingIntent canceled");
            }
        }
    }
}
