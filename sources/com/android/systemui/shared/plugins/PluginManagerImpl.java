package com.android.systemui.shared.plugins;

import android.app.LoadedApk;
import android.app.Notification.Action;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.widget.Toast;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginManager.Helper;
import dalvik.system.PathClassLoader;
import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class PluginManagerImpl extends BroadcastReceiver implements PluginManager {
    private static final String TAG = PluginManagerImpl.class.getSimpleName();
    private final boolean isDebuggable;
    private final Map<String, ClassLoader> mClassLoaders;
    private final Context mContext;
    private final PluginInstanceManagerFactory mFactory;
    private boolean mHasOneShot;
    private boolean mListening;
    private Looper mLooper;
    private final ArraySet<String> mOneShotPackages;
    private ClassLoaderFilter mParentClassLoader;
    private final PluginEnabler mPluginEnabler;
    private final PluginInitializer mPluginInitializer;
    /* access modifiers changed from: private */
    public final ArrayMap<PluginListener<?>, PluginInstanceManager> mPluginMap;
    private final PluginPrefs mPluginPrefs;
    private final ArraySet<String> mWhitelistedPlugins;

    private static class ClassLoaderFilter extends ClassLoader {
        private final ClassLoader mBase;
        private final String mPackage;

        public ClassLoaderFilter(ClassLoader classLoader, String str) {
            super(ClassLoader.getSystemClassLoader());
            this.mBase = classLoader;
            this.mPackage = str;
        }

        /* access modifiers changed from: protected */
        public Class<?> loadClass(String str, boolean z) throws ClassNotFoundException {
            if (!str.startsWith(this.mPackage)) {
                super.loadClass(str, z);
            }
            return this.mBase.loadClass(str);
        }
    }

    public static class CrashWhilePluginActiveException extends RuntimeException {
        public CrashWhilePluginActiveException(Throwable th) {
            super(th);
        }
    }

    private class PluginExceptionHandler implements UncaughtExceptionHandler {
        private final UncaughtExceptionHandler mHandler;

        private PluginExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
            this.mHandler = uncaughtExceptionHandler;
        }

        public void uncaughtException(Thread thread, Throwable th) {
            if (SystemProperties.getBoolean("plugin.debugging", false)) {
                this.mHandler.uncaughtException(thread, th);
                return;
            }
            boolean checkStack = checkStack(th);
            if (!checkStack) {
                synchronized (this) {
                    for (PluginInstanceManager disableAll : PluginManagerImpl.this.mPluginMap.values()) {
                        checkStack |= disableAll.disableAll();
                    }
                }
            }
            if (checkStack) {
                th = new CrashWhilePluginActiveException(th);
            }
            this.mHandler.uncaughtException(thread, th);
        }

        private boolean checkStack(Throwable th) {
            StackTraceElement[] stackTrace;
            boolean z;
            if (th == null) {
                return false;
            }
            synchronized (this) {
                z = false;
                for (StackTraceElement stackTraceElement : th.getStackTrace()) {
                    for (PluginInstanceManager checkAndDisable : PluginManagerImpl.this.mPluginMap.values()) {
                        z |= checkAndDisable.checkAndDisable(stackTraceElement.getClassName());
                    }
                }
            }
            return checkStack(th.getCause()) | z;
        }
    }

    @VisibleForTesting
    public static class PluginInstanceManagerFactory {
        public <T extends Plugin> PluginInstanceManager createPluginInstanceManager(Context context, String str, PluginListener<T> pluginListener, boolean z, Looper looper, Class<?> cls, PluginManagerImpl pluginManagerImpl) {
            VersionInfo versionInfo = new VersionInfo();
            versionInfo.addClass(cls);
            PluginInstanceManager pluginInstanceManager = new PluginInstanceManager(context, str, pluginListener, z, looper, versionInfo, pluginManagerImpl);
            return pluginInstanceManager;
        }
    }

    public PluginManagerImpl(Context context, PluginInitializer pluginInitializer) {
        this(context, new PluginInstanceManagerFactory(), Build.IS_DEBUGGABLE, Thread.getUncaughtExceptionPreHandler(), pluginInitializer);
    }

    @VisibleForTesting
    PluginManagerImpl(Context context, PluginInstanceManagerFactory pluginInstanceManagerFactory, boolean z, UncaughtExceptionHandler uncaughtExceptionHandler, final PluginInitializer pluginInitializer) {
        this.mPluginMap = new ArrayMap<>();
        this.mClassLoaders = new ArrayMap();
        this.mOneShotPackages = new ArraySet<>();
        this.mWhitelistedPlugins = new ArraySet<>();
        this.mContext = context;
        this.mFactory = pluginInstanceManagerFactory;
        this.mLooper = pluginInitializer.getBgLooper();
        this.isDebuggable = z;
        this.mWhitelistedPlugins.addAll(Arrays.asList(pluginInitializer.getWhitelistedPlugins(this.mContext)));
        this.mPluginPrefs = new PluginPrefs(this.mContext);
        this.mPluginEnabler = pluginInitializer.getPluginEnabler(this.mContext);
        this.mPluginInitializer = pluginInitializer;
        Thread.setUncaughtExceptionPreHandler(new PluginExceptionHandler(uncaughtExceptionHandler));
        new Handler(this.mLooper).post(new Runnable(this) {
            public void run() {
                pluginInitializer.onPluginManagerInit();
            }
        });
    }

    public String[] getWhitelistedPlugins() {
        return (String[]) this.mWhitelistedPlugins.toArray(new String[0]);
    }

    public PluginEnabler getPluginEnabler() {
        return this.mPluginEnabler;
    }

    public <T extends Plugin> void addPluginListener(PluginListener<T> pluginListener, Class<?> cls) {
        addPluginListener(pluginListener, cls, false);
    }

    public <T extends Plugin> void addPluginListener(PluginListener<T> pluginListener, Class<?> cls, boolean z) {
        addPluginListener(Helper.getAction(cls), pluginListener, cls, z);
    }

    public <T extends Plugin> void addPluginListener(String str, PluginListener<T> pluginListener, Class<?> cls) {
        addPluginListener(str, pluginListener, cls, false);
    }

    public <T extends Plugin> void addPluginListener(String str, PluginListener<T> pluginListener, Class cls, boolean z) {
        this.mPluginPrefs.addAction(str);
        PluginInstanceManager createPluginInstanceManager = this.mFactory.createPluginInstanceManager(this.mContext, str, pluginListener, z, this.mLooper, cls, this);
        createPluginInstanceManager.loadAll();
        synchronized (this) {
            this.mPluginMap.put(pluginListener, createPluginInstanceManager);
        }
        startListening();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0022, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removePluginListener(com.android.systemui.plugins.PluginListener<?> r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            android.util.ArrayMap<com.android.systemui.plugins.PluginListener<?>, com.android.systemui.shared.plugins.PluginInstanceManager> r0 = r1.mPluginMap     // Catch:{ all -> 0x0023 }
            boolean r0 = r0.containsKey(r2)     // Catch:{ all -> 0x0023 }
            if (r0 != 0) goto L_0x000b
            monitor-exit(r1)     // Catch:{ all -> 0x0023 }
            return
        L_0x000b:
            android.util.ArrayMap<com.android.systemui.plugins.PluginListener<?>, com.android.systemui.shared.plugins.PluginInstanceManager> r0 = r1.mPluginMap     // Catch:{ all -> 0x0023 }
            java.lang.Object r2 = r0.remove(r2)     // Catch:{ all -> 0x0023 }
            com.android.systemui.shared.plugins.PluginInstanceManager r2 = (com.android.systemui.shared.plugins.PluginInstanceManager) r2     // Catch:{ all -> 0x0023 }
            r2.destroy()     // Catch:{ all -> 0x0023 }
            android.util.ArrayMap<com.android.systemui.plugins.PluginListener<?>, com.android.systemui.shared.plugins.PluginInstanceManager> r2 = r1.mPluginMap     // Catch:{ all -> 0x0023 }
            int r2 = r2.size()     // Catch:{ all -> 0x0023 }
            if (r2 != 0) goto L_0x0021
            r1.stopListening()     // Catch:{ all -> 0x0023 }
        L_0x0021:
            monitor-exit(r1)     // Catch:{ all -> 0x0023 }
            return
        L_0x0023:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0023 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.shared.plugins.PluginManagerImpl.removePluginListener(com.android.systemui.plugins.PluginListener):void");
    }

    private void startListening() {
        if (!this.mListening) {
            this.mListening = true;
            IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
            intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
            intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addAction("com.android.systemui.action.PLUGIN_CHANGED");
            intentFilter.addAction("com.android.systemui.action.DISABLE_PLUGIN");
            intentFilter.addDataScheme("package");
            this.mContext.registerReceiver(this, intentFilter);
            this.mContext.registerReceiver(this, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        }
    }

    private void stopListening() {
        if (this.mListening && !this.mHasOneShot) {
            this.mListening = false;
            this.mContext.unregisterReceiver(this);
        }
    }

    public void onReceive(Context context, Intent intent) {
        String str;
        String str2 = TAG;
        if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
            synchronized (this) {
                for (PluginInstanceManager loadAll : this.mPluginMap.values()) {
                    loadAll.loadAll();
                }
            }
        } else {
            if ("com.android.systemui.action.DISABLE_PLUGIN".equals(intent.getAction())) {
                ComponentName unflattenFromString = ComponentName.unflattenFromString(intent.getData().toString().substring(10));
                if (!isPluginWhitelisted(unflattenFromString)) {
                    getPluginEnabler().setDisabled(unflattenFromString, 1);
                    ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).cancel(unflattenFromString.getClassName(), 6);
                }
            } else {
                String encodedSchemeSpecificPart = intent.getData().getEncodedSchemeSpecificPart();
                ComponentName unflattenFromString2 = ComponentName.unflattenFromString(encodedSchemeSpecificPart);
                if (this.mOneShotPackages.contains(encodedSchemeSpecificPart)) {
                    int identifier = Resources.getSystem().getIdentifier("stat_sys_warning", "drawable", "android");
                    int identifier2 = Resources.getSystem().getIdentifier("system_notification_accent_color", "color", "android");
                    try {
                        PackageManager packageManager = this.mContext.getPackageManager();
                        str = packageManager.getApplicationInfo(encodedSchemeSpecificPart, 0).loadLabel(packageManager).toString();
                    } catch (NameNotFoundException unused) {
                        str = encodedSchemeSpecificPart;
                    }
                    Builder color = new Builder(this.mContext, "ALR").setSmallIcon(identifier).setWhen(0).setShowWhen(false).setPriority(2).setVisibility(1).setColor(this.mContext.getColor(identifier2));
                    StringBuilder sb = new StringBuilder();
                    sb.append("Plugin \"");
                    sb.append(str);
                    sb.append("\" has updated");
                    Builder contentText = color.setContentTitle(sb.toString()).setContentText("Restart SysUI for changes to take effect.");
                    Intent intent2 = new Intent("com.android.systemui.action.RESTART");
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("package://");
                    sb2.append(encodedSchemeSpecificPart);
                    String str3 = "Restart SysUI";
                    contentText.addAction(new Action.Builder(null, str3, PendingIntent.getBroadcast(this.mContext, 0, intent2.setData(Uri.parse(sb2.toString())), 0)).build());
                    ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).notify(6, contentText.build());
                }
                if (clearClassLoader(encodedSchemeSpecificPart)) {
                    if (Build.IS_ENG) {
                        Context context2 = this.mContext;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("Reloading ");
                        sb3.append(encodedSchemeSpecificPart);
                        Toast.makeText(context2, sb3.toString(), 1).show();
                    } else {
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("Reloading ");
                        sb4.append(encodedSchemeSpecificPart);
                        Log.v(str2, sb4.toString());
                    }
                }
                if ("android.intent.action.PACKAGE_REPLACED".equals(intent.getAction()) && unflattenFromString2 != null) {
                    int disableReason = getPluginEnabler().getDisableReason(unflattenFromString2);
                    if (disableReason == 2 || disableReason == 3 || disableReason == 1) {
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("Re-enabling previously disabled plugin that has been updated: ");
                        sb5.append(unflattenFromString2.flattenToShortString());
                        Log.i(str2, sb5.toString());
                        getPluginEnabler().setEnabled(unflattenFromString2);
                    }
                }
                synchronized (this) {
                    if (!"android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
                        for (PluginInstanceManager onPackageChange : this.mPluginMap.values()) {
                            onPackageChange.onPackageChange(encodedSchemeSpecificPart);
                        }
                    } else {
                        for (PluginInstanceManager onPackageRemoved : this.mPluginMap.values()) {
                            onPackageRemoved.onPackageRemoved(encodedSchemeSpecificPart);
                        }
                    }
                }
            }
        }
    }

    public ClassLoader getClassLoader(ApplicationInfo applicationInfo) {
        if (!this.isDebuggable && !isPluginPackageWhitelisted(applicationInfo.packageName)) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot get class loader for non-whitelisted plugin. Src:");
            sb.append(applicationInfo.sourceDir);
            sb.append(", pkg: ");
            sb.append(applicationInfo.packageName);
            Log.w(str, sb.toString());
            return null;
        } else if (this.mClassLoaders.containsKey(applicationInfo.packageName)) {
            return (ClassLoader) this.mClassLoaders.get(applicationInfo.packageName);
        } else {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            LoadedApk.makePaths(null, true, applicationInfo, arrayList, arrayList2);
            PathClassLoader pathClassLoader = new PathClassLoader(TextUtils.join(File.pathSeparator, arrayList), TextUtils.join(File.pathSeparator, arrayList2), getParentClassLoader());
            this.mClassLoaders.put(applicationInfo.packageName, pathClassLoader);
            return pathClassLoader;
        }
    }

    private boolean clearClassLoader(String str) {
        return this.mClassLoaders.remove(str) != null;
    }

    /* access modifiers changed from: 0000 */
    public ClassLoader getParentClassLoader() {
        if (this.mParentClassLoader == null) {
            this.mParentClassLoader = new ClassLoaderFilter(PluginManagerImpl.class.getClassLoader(), "com.android.systemui.plugin");
        }
        return this.mParentClassLoader;
    }

    public <T> boolean dependsOn(Plugin plugin, Class<T> cls) {
        synchronized (this) {
            for (int i = 0; i < this.mPluginMap.size(); i++) {
                if (((PluginInstanceManager) this.mPluginMap.valueAt(i)).dependsOn(plugin, cls)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void handleWtfs() {
        this.mPluginInitializer.handleWtfs();
    }

    private boolean isPluginPackageWhitelisted(String str) {
        Iterator it = this.mWhitelistedPlugins.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            ComponentName unflattenFromString = ComponentName.unflattenFromString(str2);
            if (unflattenFromString != null) {
                if (unflattenFromString.getPackageName().equals(str)) {
                    return true;
                }
            } else if (str2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPluginWhitelisted(ComponentName componentName) {
        Iterator it = this.mWhitelistedPlugins.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
            if (unflattenFromString != null) {
                if (unflattenFromString.equals(componentName)) {
                    return true;
                }
            } else if (str.equals(componentName.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
