package com.android.systemui.controls.management;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.UserHandle;
import android.util.Log;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsRequestReceiver.kt */
public final class ControlsRequestReceiver extends BroadcastReceiver {
    public static final Companion Companion = new Companion(null);

    /* compiled from: ControlsRequestReceiver.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final boolean isPackageInForeground(Context context, String str) {
            String str2 = "ControlsRequestReceiver";
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(str, "packageName");
            try {
                int packageUid = context.getPackageManager().getPackageUid(str, 0);
                ActivityManager activityManager = (ActivityManager) context.getSystemService(ActivityManager.class);
                if ((activityManager != null ? activityManager.getUidImportance(packageUid) : 1000) == 100) {
                    return true;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Uid ");
                sb.append(packageUid);
                sb.append(" not in foreground");
                Log.w(str2, sb.toString());
                return false;
            } catch (NameNotFoundException unused) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Package ");
                sb2.append(str);
                sb2.append(" not found");
                Log.w(str2, sb2.toString());
                return false;
            }
        }
    }

    public void onReceive(Context context, Intent intent) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        String str = "android.intent.extra.COMPONENT_NAME";
        ComponentName componentName = (ComponentName) intent.getParcelableExtra(str);
        String packageName = componentName != null ? componentName.getPackageName() : null;
        if (packageName != null && Companion.isPackageInForeground(context, packageName)) {
            Intent intent2 = new Intent(context, ControlsRequestDialog.class);
            intent2.putExtra(str, intent.getParcelableExtra(str));
            String str2 = "android.service.controls.extra.CONTROL";
            intent2.putExtra(str2, intent.getParcelableExtra(str2));
            intent2.putExtra("android.intent.extra.USER_ID", context.getUserId());
            context.startActivityAsUser(intent2, UserHandle.SYSTEM);
        }
    }
}
