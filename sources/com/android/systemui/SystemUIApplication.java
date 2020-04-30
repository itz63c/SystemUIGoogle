package com.android.systemui;

import android.app.ActivityThread;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Process;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import android.util.TimingsTraceLog;
import com.android.systemui.SystemUIAppComponentFactory.ContextAvailableCallback;
import com.android.systemui.SystemUIAppComponentFactory.ContextInitializer;
import com.android.systemui.dagger.ContextComponentHelper;
import com.android.systemui.dagger.SystemUIRootComponent;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.util.NotificationChannels;
import java.lang.reflect.InvocationTargetException;

public class SystemUIApplication extends Application implements ContextInitializer {
    /* access modifiers changed from: private */
    public BootCompleteCacheImpl mBootCompleteCache;
    private ContextComponentHelper mComponentHelper;
    private ContextAvailableCallback mContextAvailableCallback;
    private SystemUIRootComponent mRootComponent;
    /* access modifiers changed from: private */
    public SystemUI[] mServices;
    /* access modifiers changed from: private */
    public boolean mServicesStarted;

    public SystemUIApplication() {
        Log.v("SystemUIService", "SystemUIApplication constructed.");
    }

    public void onCreate() {
        super.onCreate();
        Log.v("SystemUIService", "SystemUIApplication created.");
        TimingsTraceLog timingsTraceLog = new TimingsTraceLog("SystemUIBootTiming", 4096);
        timingsTraceLog.traceBegin("DependencyInjection");
        this.mContextAvailableCallback.onContextAvailable(this);
        SystemUIRootComponent rootComponent = SystemUIFactory.getInstance().getRootComponent();
        this.mRootComponent = rootComponent;
        this.mComponentHelper = rootComponent.getContextComponentHelper();
        this.mBootCompleteCache = this.mRootComponent.provideBootCacheImpl();
        timingsTraceLog.traceEnd();
        setTheme(C2018R$style.Theme_SystemUI);
        if (Process.myUserHandle().equals(UserHandle.SYSTEM)) {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.BOOT_COMPLETED");
            intentFilter.setPriority(1000);
            registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if (!SystemUIApplication.this.mBootCompleteCache.isBootComplete()) {
                        SystemUIApplication.this.unregisterReceiver(this);
                        SystemUIApplication.this.mBootCompleteCache.setBootComplete();
                        if (SystemUIApplication.this.mServicesStarted) {
                            for (SystemUI onBootCompleted : SystemUIApplication.this.mServices) {
                                onBootCompleted.onBootCompleted();
                            }
                        }
                    }
                }
            }, intentFilter);
            registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if ("android.intent.action.LOCALE_CHANGED".equals(intent.getAction()) && SystemUIApplication.this.mBootCompleteCache.isBootComplete()) {
                        NotificationChannels.createAll(context);
                    }
                }
            }, new IntentFilter("android.intent.action.LOCALE_CHANGED"));
        } else {
            String currentProcessName = ActivityThread.currentProcessName();
            ApplicationInfo applicationInfo = getApplicationInfo();
            if (currentProcessName != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(applicationInfo.processName);
                sb.append(":");
                if (currentProcessName.startsWith(sb.toString())) {
                    return;
                }
            }
            startSecondaryUserServicesIfNeeded();
        }
    }

    public void startServicesIfNeeded() {
        startServicesIfNeeded("StartServices", getResources().getStringArray(C2005R$array.config_systemUIServiceComponents));
    }

    /* access modifiers changed from: 0000 */
    public void startSecondaryUserServicesIfNeeded() {
        startServicesIfNeeded("StartSecondaryServices", getResources().getStringArray(C2005R$array.config_systemUIServiceComponentsPerUser));
    }

    private void startServicesIfNeeded(String str, String[] strArr) {
        if (!this.mServicesStarted) {
            this.mServices = new SystemUI[strArr.length];
            if (!this.mBootCompleteCache.isBootComplete()) {
                if ("1".equals(SystemProperties.get("sys.boot_completed"))) {
                    this.mBootCompleteCache.setBootComplete();
                }
            }
            DumpManager createDumpManager = this.mRootComponent.createDumpManager();
            StringBuilder sb = new StringBuilder();
            sb.append("Starting SystemUI services for user ");
            sb.append(Process.myUserHandle().getIdentifier());
            sb.append(".");
            String str2 = "SystemUIService";
            Log.v(str2, sb.toString());
            TimingsTraceLog timingsTraceLog = new TimingsTraceLog("SystemUIBootTiming", 4096);
            timingsTraceLog.traceBegin(str);
            int length = strArr.length;
            int i = 0;
            while (i < length) {
                String str3 = strArr[i];
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append(str3);
                timingsTraceLog.traceBegin(sb2.toString());
                long currentTimeMillis = System.currentTimeMillis();
                try {
                    SystemUI resolveSystemUI = this.mComponentHelper.resolveSystemUI(str3);
                    if (resolveSystemUI == null) {
                        resolveSystemUI = (SystemUI) Class.forName(str3).getConstructor(new Class[]{Context.class}).newInstance(new Object[]{this});
                    }
                    this.mServices[i] = resolveSystemUI;
                    this.mServices[i].start();
                    timingsTraceLog.traceEnd();
                    long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
                    if (currentTimeMillis2 > 1000) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("Initialization of ");
                        sb3.append(str3);
                        sb3.append(" took ");
                        sb3.append(currentTimeMillis2);
                        sb3.append(" ms");
                        Log.w(str2, sb3.toString());
                    }
                    if (this.mBootCompleteCache.isBootComplete()) {
                        this.mServices[i].onBootCompleted();
                    }
                    createDumpManager.registerDumpable(this.mServices[i].getClass().getName(), this.mServices[i]);
                    i++;
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            this.mRootComponent.getInitController().executePostInitTasks();
            timingsTraceLog.traceEnd();
            this.mServicesStarted = true;
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (this.mServicesStarted) {
            this.mRootComponent.getConfigurationController().onConfigurationChanged(configuration);
            int length = this.mServices.length;
            for (int i = 0; i < length; i++) {
                SystemUI[] systemUIArr = this.mServices;
                if (systemUIArr[i] != null) {
                    systemUIArr[i].onConfigurationChanged(configuration);
                }
            }
        }
    }

    public void setContextAvailableCallback(ContextAvailableCallback contextAvailableCallback) {
        this.mContextAvailableCallback = contextAvailableCallback;
    }
}
