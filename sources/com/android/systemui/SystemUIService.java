package com.android.systemui;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.os.BinderInternal;
import com.android.internal.os.BinderInternal.BinderProxyLimitListener;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.dump.SystemUIAuxiliaryDumpService;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class SystemUIService extends Service {
    private final DumpManager mDumpManager;
    private final Handler mMainHandler;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public SystemUIService(Handler handler, DumpManager dumpManager) {
        this.mMainHandler = handler;
        this.mDumpManager = dumpManager;
    }

    public void onCreate() {
        super.onCreate();
        ((SystemUIApplication) getApplication()).startServicesIfNeeded();
        if (!Build.IS_DEBUGGABLE || !SystemProperties.getBoolean("debug.crash_sysui", false)) {
            if (Build.IS_DEBUGGABLE) {
                BinderInternal.nSetBinderProxyCountEnabled(true);
                BinderInternal.nSetBinderProxyCountWatermarks(1000, 900);
                BinderInternal.setBinderProxyCountCallback(new BinderProxyLimitListener(this) {
                    public void onLimitReached(int i) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("uid ");
                        sb.append(i);
                        sb.append(" sent too many Binder proxies to uid ");
                        sb.append(Process.myUid());
                        Slog.w("SystemUIService", sb.toString());
                    }
                }, this.mMainHandler);
            }
            startServiceAsUser(new Intent(getApplicationContext(), SystemUIAuxiliaryDumpService.class), UserHandle.SYSTEM);
            return;
        }
        throw new RuntimeException();
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (strArr.length == 0) {
            strArr = new String[]{"--dump-priority", "CRITICAL"};
        }
        this.mDumpManager.dump(fileDescriptor, printWriter, strArr);
    }
}
