package com.android.systemui.plugins;

import android.util.ArrayMap;
import com.android.systemui.Dependency;
import com.android.systemui.shared.plugins.PluginManager;

public class PluginDependencyProvider extends DependencyProvider {
    private final ArrayMap<Class<?>, Object> mDependencies = new ArrayMap<>();
    private final PluginManager mManager;

    public PluginDependencyProvider(PluginManager pluginManager) {
        this.mManager = pluginManager;
        PluginDependency.sProvider = this;
    }

    public <T> void allowPluginDependency(Class<T> cls) {
        allowPluginDependency(cls, Dependency.get(cls));
    }

    public <T> void allowPluginDependency(Class<T> cls, T t) {
        synchronized (this.mDependencies) {
            this.mDependencies.put(cls, t);
        }
    }

    /* access modifiers changed from: 0000 */
    public <T> T get(Plugin plugin, Class<T> cls) {
        T t;
        if (this.mManager.dependsOn(plugin, cls)) {
            synchronized (this.mDependencies) {
                if (this.mDependencies.containsKey(cls)) {
                    t = this.mDependencies.get(cls);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unknown dependency ");
                    sb.append(cls);
                    throw new IllegalArgumentException(sb.toString());
                }
            }
            return t;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(plugin.getClass());
        sb2.append(" does not depend on ");
        sb2.append(cls);
        throw new IllegalArgumentException(sb2.toString());
    }
}
