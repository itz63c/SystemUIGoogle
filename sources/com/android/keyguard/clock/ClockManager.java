package com.android.keyguard.clock;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import androidx.lifecycle.Observer;
import com.android.keyguard.clock.ClockManager.ClockChangedListener;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.dock.DockManager.DockEventListener;
import com.android.systemui.plugins.ClockPlugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.settings.CurrentUserObservable;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.util.InjectionInflationController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ClockManager {
    private final List<Supplier<ClockPlugin>> mBuiltinClocks;
    private final ContentObserver mContentObserver;
    private final ContentResolver mContentResolver;
    /* access modifiers changed from: private */
    public final CurrentUserObservable mCurrentUserObservable;
    private final Observer<Integer> mCurrentUserObserver;
    private final DockEventListener mDockEventListener;
    private final DockManager mDockManager;
    /* access modifiers changed from: private */
    public final int mHeight;
    /* access modifiers changed from: private */
    public boolean mIsDocked;
    private final Map<ClockChangedListener, AvailableClocks> mListeners;
    private final Handler mMainHandler;
    private final PluginManager mPluginManager;
    private final AvailableClocks mPreviewClocks;
    /* access modifiers changed from: private */
    public final SettingsWrapper mSettingsWrapper;
    /* access modifiers changed from: private */
    public final int mWidth;

    private final class AvailableClocks implements PluginListener<ClockPlugin> {
        private final List<ClockInfo> mClockInfo;
        private final Map<String, ClockPlugin> mClocks;
        private ClockPlugin mCurrentClock;

        private AvailableClocks() {
            this.mClocks = new ArrayMap();
            this.mClockInfo = new ArrayList();
        }

        public void onPluginConnected(ClockPlugin clockPlugin, Context context) {
            addClockPlugin(clockPlugin);
            reloadIfNeeded(clockPlugin);
        }

        public void onPluginDisconnected(ClockPlugin clockPlugin) {
            removeClockPlugin(clockPlugin);
            reloadIfNeeded(clockPlugin);
        }

        /* access modifiers changed from: 0000 */
        public ClockPlugin getCurrentClock() {
            return this.mCurrentClock;
        }

        /* access modifiers changed from: 0000 */
        public List<ClockInfo> getInfo() {
            return this.mClockInfo;
        }

        /* access modifiers changed from: 0000 */
        public void addClockPlugin(ClockPlugin clockPlugin) {
            String name = clockPlugin.getClass().getName();
            this.mClocks.put(clockPlugin.getClass().getName(), clockPlugin);
            List<ClockInfo> list = this.mClockInfo;
            Builder builder = ClockInfo.builder();
            builder.setName(clockPlugin.getName());
            Objects.requireNonNull(clockPlugin);
            builder.setTitle(new Supplier() {
                public final Object get() {
                    return ClockPlugin.this.getTitle();
                }
            });
            builder.setId(name);
            Objects.requireNonNull(clockPlugin);
            builder.setThumbnail(new Supplier() {
                public final Object get() {
                    return ClockPlugin.this.getThumbnail();
                }
            });
            builder.setPreview(new Supplier(clockPlugin) {
                public final /* synthetic */ ClockPlugin f$1;

                {
                    this.f$1 = r2;
                }

                public final Object get() {
                    return AvailableClocks.this.lambda$addClockPlugin$0$ClockManager$AvailableClocks(this.f$1);
                }
            });
            list.add(builder.build());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$addClockPlugin$0 */
        public /* synthetic */ Bitmap lambda$addClockPlugin$0$ClockManager$AvailableClocks(ClockPlugin clockPlugin) {
            return clockPlugin.getPreview(ClockManager.this.mWidth, ClockManager.this.mHeight);
        }

        private void removeClockPlugin(ClockPlugin clockPlugin) {
            String name = clockPlugin.getClass().getName();
            this.mClocks.remove(name);
            for (int i = 0; i < this.mClockInfo.size(); i++) {
                if (name.equals(((ClockInfo) this.mClockInfo.get(i)).getId())) {
                    this.mClockInfo.remove(i);
                    return;
                }
            }
        }

        private void reloadIfNeeded(ClockPlugin clockPlugin) {
            boolean z = true;
            boolean z2 = clockPlugin == this.mCurrentClock;
            reloadCurrentClock();
            if (clockPlugin != this.mCurrentClock) {
                z = false;
            }
            if (z2 || z) {
                ClockManager.this.reload();
            }
        }

        /* access modifiers changed from: 0000 */
        public void reloadCurrentClock() {
            this.mCurrentClock = getClockPlugin();
        }

        /* JADX WARNING: Removed duplicated region for block: B:10:0x0054  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private com.android.systemui.plugins.ClockPlugin getClockPlugin() {
            /*
                r3 = this;
                com.android.keyguard.clock.ClockManager r0 = com.android.keyguard.clock.ClockManager.this
                boolean r0 = r0.isDocked()
                if (r0 == 0) goto L_0x0033
                com.android.keyguard.clock.ClockManager r0 = com.android.keyguard.clock.ClockManager.this
                com.android.keyguard.clock.SettingsWrapper r0 = r0.mSettingsWrapper
                com.android.keyguard.clock.ClockManager r1 = com.android.keyguard.clock.ClockManager.this
                com.android.systemui.settings.CurrentUserObservable r1 = r1.mCurrentUserObservable
                androidx.lifecycle.LiveData r1 = r1.getCurrentUser()
                java.lang.Object r1 = r1.getValue()
                java.lang.Integer r1 = (java.lang.Integer) r1
                int r1 = r1.intValue()
                java.lang.String r0 = r0.getDockedClockFace(r1)
                if (r0 == 0) goto L_0x0033
                java.util.Map<java.lang.String, com.android.systemui.plugins.ClockPlugin> r1 = r3.mClocks
                java.lang.Object r0 = r1.get(r0)
                com.android.systemui.plugins.ClockPlugin r0 = (com.android.systemui.plugins.ClockPlugin) r0
                if (r0 == 0) goto L_0x0034
                return r0
            L_0x0033:
                r0 = 0
            L_0x0034:
                com.android.keyguard.clock.ClockManager r1 = com.android.keyguard.clock.ClockManager.this
                com.android.keyguard.clock.SettingsWrapper r1 = r1.mSettingsWrapper
                com.android.keyguard.clock.ClockManager r2 = com.android.keyguard.clock.ClockManager.this
                com.android.systemui.settings.CurrentUserObservable r2 = r2.mCurrentUserObservable
                androidx.lifecycle.LiveData r2 = r2.getCurrentUser()
                java.lang.Object r2 = r2.getValue()
                java.lang.Integer r2 = (java.lang.Integer) r2
                int r2 = r2.intValue()
                java.lang.String r1 = r1.getLockScreenCustomClockFace(r2)
                if (r1 == 0) goto L_0x005d
                java.util.Map<java.lang.String, com.android.systemui.plugins.ClockPlugin> r3 = r3.mClocks
                java.lang.Object r3 = r3.get(r1)
                r0 = r3
                com.android.systemui.plugins.ClockPlugin r0 = (com.android.systemui.plugins.ClockPlugin) r0
            L_0x005d:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.clock.ClockManager.AvailableClocks.getClockPlugin():com.android.systemui.plugins.ClockPlugin");
        }
    }

    public interface ClockChangedListener {
        void onClockChanged(ClockPlugin clockPlugin);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ClockManager(Integer num) {
        reload();
    }

    public ClockManager(Context context, InjectionInflationController injectionInflationController, PluginManager pluginManager, SysuiColorExtractor sysuiColorExtractor, DockManager dockManager, BroadcastDispatcher broadcastDispatcher) {
        this(context, injectionInflationController, pluginManager, sysuiColorExtractor, context.getContentResolver(), new CurrentUserObservable(broadcastDispatcher), new SettingsWrapper(context.getContentResolver()), dockManager);
    }

    ClockManager(Context context, InjectionInflationController injectionInflationController, PluginManager pluginManager, SysuiColorExtractor sysuiColorExtractor, ContentResolver contentResolver, CurrentUserObservable currentUserObservable, SettingsWrapper settingsWrapper, DockManager dockManager) {
        this.mBuiltinClocks = new ArrayList();
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mContentObserver = new ContentObserver(this.mMainHandler) {
            public void onChange(boolean z, Collection<Uri> collection, int i, int i2) {
                if (Objects.equals(Integer.valueOf(i2), ClockManager.this.mCurrentUserObservable.getCurrentUser().getValue())) {
                    ClockManager.this.reload();
                }
            }
        };
        this.mCurrentUserObserver = new Observer() {
            public final void onChanged(Object obj) {
                ClockManager.this.lambda$new$0$ClockManager((Integer) obj);
            }
        };
        this.mDockEventListener = new DockEventListener() {
            public void onEvent(int i) {
                ClockManager clockManager = ClockManager.this;
                boolean z = true;
                if (!(i == 1 || i == 2)) {
                    z = false;
                }
                clockManager.mIsDocked = z;
                ClockManager.this.reload();
            }
        };
        this.mListeners = new ArrayMap();
        this.mPluginManager = pluginManager;
        this.mContentResolver = contentResolver;
        this.mSettingsWrapper = settingsWrapper;
        this.mCurrentUserObservable = currentUserObservable;
        this.mDockManager = dockManager;
        this.mPreviewClocks = new AvailableClocks();
        Resources resources = context.getResources();
        addBuiltinClock(new Supplier(resources, injectionInflationController.injectable(LayoutInflater.from(context)), sysuiColorExtractor) {
            public final /* synthetic */ Resources f$0;
            public final /* synthetic */ LayoutInflater f$1;
            public final /* synthetic */ SysuiColorExtractor f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final Object get() {
                return ClockManager.lambda$new$1(this.f$0, this.f$1, this.f$2);
            }
        });
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        this.mWidth = displayMetrics.widthPixels;
        this.mHeight = displayMetrics.heightPixels;
    }

    static /* synthetic */ ClockPlugin lambda$new$1(Resources resources, LayoutInflater layoutInflater, SysuiColorExtractor sysuiColorExtractor) {
        return new DefaultClockController(resources, layoutInflater, sysuiColorExtractor);
    }

    public void addOnClockChangedListener(ClockChangedListener clockChangedListener) {
        if (this.mListeners.isEmpty()) {
            register();
        }
        AvailableClocks availableClocks = new AvailableClocks();
        for (int i = 0; i < this.mBuiltinClocks.size(); i++) {
            availableClocks.addClockPlugin((ClockPlugin) ((Supplier) this.mBuiltinClocks.get(i)).get());
        }
        this.mListeners.put(clockChangedListener, availableClocks);
        this.mPluginManager.addPluginListener((PluginListener<T>) availableClocks, ClockPlugin.class, true);
        reload();
    }

    public void removeOnClockChangedListener(ClockChangedListener clockChangedListener) {
        this.mPluginManager.removePluginListener((AvailableClocks) this.mListeners.remove(clockChangedListener));
        if (this.mListeners.isEmpty()) {
            unregister();
        }
    }

    /* access modifiers changed from: 0000 */
    public List<ClockInfo> getClockInfos() {
        return this.mPreviewClocks.getInfo();
    }

    /* access modifiers changed from: 0000 */
    public boolean isDocked() {
        return this.mIsDocked;
    }

    /* access modifiers changed from: 0000 */
    public ContentObserver getContentObserver() {
        return this.mContentObserver;
    }

    /* access modifiers changed from: 0000 */
    public void addBuiltinClock(Supplier<ClockPlugin> supplier) {
        this.mPreviewClocks.addClockPlugin((ClockPlugin) supplier.get());
        this.mBuiltinClocks.add(supplier);
    }

    private void register() {
        this.mPluginManager.addPluginListener((PluginListener<T>) this.mPreviewClocks, ClockPlugin.class, true);
        this.mContentResolver.registerContentObserver(Secure.getUriFor("lock_screen_custom_clock_face"), false, this.mContentObserver, -1);
        this.mContentResolver.registerContentObserver(Secure.getUriFor("docked_clock_face"), false, this.mContentObserver, -1);
        this.mCurrentUserObservable.getCurrentUser().observeForever(this.mCurrentUserObserver);
        DockManager dockManager = this.mDockManager;
        if (dockManager != null) {
            dockManager.addListener(this.mDockEventListener);
        }
    }

    private void unregister() {
        this.mPluginManager.removePluginListener(this.mPreviewClocks);
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
        this.mCurrentUserObservable.getCurrentUser().removeObserver(this.mCurrentUserObserver);
        DockManager dockManager = this.mDockManager;
        if (dockManager != null) {
            dockManager.removeListener(this.mDockEventListener);
        }
    }

    /* access modifiers changed from: private */
    public void reload() {
        this.mPreviewClocks.reloadCurrentClock();
        this.mListeners.forEach(new BiConsumer() {
            public final void accept(Object obj, Object obj2) {
                ClockManager.this.lambda$reload$3$ClockManager((ClockChangedListener) obj, (AvailableClocks) obj2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$reload$3 */
    public /* synthetic */ void lambda$reload$3$ClockManager(ClockChangedListener clockChangedListener, AvailableClocks availableClocks) {
        availableClocks.reloadCurrentClock();
        ClockPlugin currentClock = availableClocks.getCurrentClock();
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (currentClock instanceof DefaultClockController) {
                currentClock = null;
            }
            clockChangedListener.onClockChanged(currentClock);
            return;
        }
        this.mMainHandler.post(new Runnable(currentClock) {
            public final /* synthetic */ ClockPlugin f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ClockManager.lambda$reload$2(ClockChangedListener.this, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$reload$2(ClockChangedListener clockChangedListener, ClockPlugin clockPlugin) {
        if (clockPlugin instanceof DefaultClockController) {
            clockPlugin = null;
        }
        clockChangedListener.onClockChanged(clockPlugin);
    }
}
