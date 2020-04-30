package androidx.remotecallback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri.Builder;

public class ProviderRelayReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if ("androidx.remotecallback.action.PROVIDER_RELAY".equals(intent.getAction())) {
            context.getContentResolver().call(new Builder().scheme("content").authority(intent.getStringExtra("androidx.remotecallback.extra.AUTHORITY")).build(), "androidx.remotecallback.method.PROVIDER_CALLBACK", null, intent.getExtras());
        }
    }
}
