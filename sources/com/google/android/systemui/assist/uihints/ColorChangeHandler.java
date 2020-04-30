package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfo;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfoListener;

public class ColorChangeHandler implements ConfigInfoListener {
    private final Context mContext;
    private boolean mIsDark;
    private PendingIntent mPendingIntent;

    ColorChangeHandler(Context context) {
        this.mContext = context;
    }

    /* access modifiers changed from: 0000 */
    public void onColorChange(boolean z) {
        this.mIsDark = z;
        sendColor();
    }

    public void onConfigInfo(ConfigInfo configInfo) {
        this.mPendingIntent = configInfo.onColorChanged;
        sendColor();
    }

    private void sendColor() {
        if (this.mPendingIntent != null) {
            Intent intent = new Intent();
            intent.putExtra("is_dark", this.mIsDark);
            try {
                this.mPendingIntent.send(this.mContext, 0, intent);
            } catch (CanceledException unused) {
                Log.w("ColorChangeHandler", "SysUI assist UI color changed PendingIntent canceled");
            }
        }
    }
}
