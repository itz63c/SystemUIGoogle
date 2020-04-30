package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfo;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfoListener;
import java.util.ArrayList;

public class ConfigurationHandler implements ConfigInfoListener {
    private final Context mContext;

    public ConfigurationHandler(Context context) {
        this.mContext = context;
    }

    public void onConfigInfo(ConfigInfo configInfo) {
        if (configInfo.configurationCallback != null) {
            Intent intent = new Intent();
            ArrayList arrayList = new ArrayList();
            arrayList.add("go_back");
            arrayList.add("take_screenshot");
            arrayList.add("half_listening_full");
            arrayList.add("input_chips");
            arrayList.add("actions_without_ui");
            intent.putCharSequenceArrayListExtra("flags", arrayList);
            intent.putExtra("version", 3);
            try {
                configInfo.configurationCallback.send(this.mContext, 0, intent);
            } catch (CanceledException e) {
                Log.e("ConfigurationHandler", "Pending intent canceled", e);
            }
        }
    }
}
