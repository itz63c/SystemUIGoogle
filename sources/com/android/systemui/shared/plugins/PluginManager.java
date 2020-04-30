package com.android.systemui.shared.plugins;

import android.text.TextUtils;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.annotations.ProvidesInterface;

public interface PluginManager {

    public static class Helper {
        public static <P> String getAction(Class<P> cls) {
            ProvidesInterface providesInterface = (ProvidesInterface) cls.getDeclaredAnnotation(ProvidesInterface.class);
            if (providesInterface == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(cls);
                sb.append(" doesn't provide an interface");
                throw new RuntimeException(sb.toString());
            } else if (!TextUtils.isEmpty(providesInterface.action())) {
                return providesInterface.action();
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(cls);
                sb2.append(" doesn't provide an action");
                throw new RuntimeException(sb2.toString());
            }
        }
    }

    <T extends Plugin> void addPluginListener(PluginListener<T> pluginListener, Class<?> cls);

    <T extends Plugin> void addPluginListener(PluginListener<T> pluginListener, Class<?> cls, boolean z);

    <T extends Plugin> void addPluginListener(String str, PluginListener<T> pluginListener, Class<?> cls);

    <T> boolean dependsOn(Plugin plugin, Class<T> cls);

    String[] getWhitelistedPlugins();

    void removePluginListener(PluginListener<?> pluginListener);
}
