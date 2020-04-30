package com.android.systemui.p007qs;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.service.quicksettings.Tile;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.p007qs.QSHost.Callback;
import com.android.systemui.p007qs.external.CustomTile;
import com.android.systemui.p007qs.external.TileLifecycleManager;
import com.android.systemui.p007qs.external.TileServices;
import com.android.systemui.p007qs.logging.QSLogger;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.p006qs.QSFactory;
import com.android.systemui.plugins.p006qs.QSTile;
import com.android.systemui.plugins.p006qs.QSTileView;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.phone.AutoTileManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.QSTileHost */
public class QSTileHost implements QSHost, Tunable, PluginListener<QSFactory>, Dumpable {
    private static final boolean DEBUG = Log.isLoggable("QSTileHost", 3);
    private AutoTileManager mAutoTiles;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final List<Callback> mCallbacks = new ArrayList();
    private final Context mContext;
    private int mCurrentUser;
    private final DumpManager mDumpManager;
    private final StatusBarIconController mIconController;
    private final QSLogger mQSLogger;
    private final ArrayList<QSFactory> mQsFactories = new ArrayList<>();
    private final TileServices mServices;
    private final Optional<StatusBar> mStatusBarOptional;
    protected final ArrayList<String> mTileSpecs = new ArrayList<>();
    private final LinkedHashMap<String, QSTile> mTiles = new LinkedHashMap<>();
    private final TunerService mTunerService;
    private Context mUserContext;

    public void warn(String str, Throwable th) {
    }

    public QSTileHost(Context context, StatusBarIconController statusBarIconController, QSFactory qSFactory, Handler handler, Looper looper, PluginManager pluginManager, TunerService tunerService, Provider<AutoTileManager> provider, DumpManager dumpManager, BroadcastDispatcher broadcastDispatcher, Optional<StatusBar> optional, QSLogger qSLogger) {
        this.mIconController = statusBarIconController;
        this.mContext = context;
        this.mUserContext = context;
        this.mTunerService = tunerService;
        this.mDumpManager = dumpManager;
        this.mQSLogger = qSLogger;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mServices = new TileServices(this, looper, this.mBroadcastDispatcher);
        this.mStatusBarOptional = optional;
        this.mQsFactories.add(qSFactory);
        pluginManager.addPluginListener((PluginListener<T>) this, QSFactory.class, true);
        this.mDumpManager.registerDumpable("QSTileHost", this);
        handler.post(new Runnable(tunerService, provider) {
            public final /* synthetic */ TunerService f$1;
            public final /* synthetic */ Provider f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                QSTileHost.this.lambda$new$0$QSTileHost(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$QSTileHost(TunerService tunerService, Provider provider) {
        tunerService.addTunable(this, "sysui_qs_tiles");
        this.mAutoTiles = (AutoTileManager) provider.get();
    }

    public StatusBarIconController getIconController() {
        return this.mIconController;
    }

    public void onPluginConnected(QSFactory qSFactory, Context context) {
        this.mQsFactories.add(0, qSFactory);
        String str = "sysui_qs_tiles";
        String value = this.mTunerService.getValue(str);
        onTuningChanged(str, "");
        onTuningChanged(str, value);
    }

    public void onPluginDisconnected(QSFactory qSFactory) {
        this.mQsFactories.remove(qSFactory);
        String str = "sysui_qs_tiles";
        String value = this.mTunerService.getValue(str);
        onTuningChanged(str, "");
        onTuningChanged(str, value);
    }

    public QSLogger getQSLogger() {
        return this.mQSLogger;
    }

    public void addCallback(Callback callback) {
        this.mCallbacks.add(callback);
    }

    public void removeCallback(Callback callback) {
        this.mCallbacks.remove(callback);
    }

    public Collection<QSTile> getTiles() {
        return this.mTiles.values();
    }

    public void collapsePanels() {
        this.mStatusBarOptional.ifPresent($$Lambda$4RRpk2g2DG1jxcebU4uq2xyjwbI.INSTANCE);
    }

    public void forceCollapsePanels() {
        this.mStatusBarOptional.ifPresent($$Lambda$mg7HvLF2bK625f51dPBSLbws.INSTANCE);
    }

    public void openPanels() {
        this.mStatusBarOptional.ifPresent($$Lambda$dlfb7Xnz27iJwNxSQU2fGCzuI2E.INSTANCE);
    }

    public Context getContext() {
        return this.mContext;
    }

    public Context getUserContext() {
        return this.mUserContext;
    }

    public TileServices getTileServices() {
        return this.mServices;
    }

    public int indexOf(String str) {
        return this.mTileSpecs.indexOf(str);
    }

    public void onTuningChanged(String str, String str2) {
        if ("sysui_qs_tiles".equals(str)) {
            String str3 = "QSTileHost";
            Log.d(str3, "Recreating tiles");
            if (str2 == null && UserManager.isDeviceInDemoMode(this.mContext)) {
                str2 = this.mContext.getResources().getString(C2017R$string.quick_settings_tiles_retail_mode);
            }
            List<String> loadTileSpecs = loadTileSpecs(this.mContext, str2);
            int currentUser = ActivityManager.getCurrentUser();
            if (currentUser != this.mCurrentUser) {
                this.mUserContext = this.mContext.createContextAsUser(UserHandle.of(currentUser), 0);
            }
            if (!loadTileSpecs.equals(this.mTileSpecs) || currentUser != this.mCurrentUser) {
                this.mTiles.entrySet().stream().filter(new Predicate(loadTileSpecs) {
                    public final /* synthetic */ List f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final boolean test(Object obj) {
                        return QSTileHost.lambda$onTuningChanged$2(this.f$0, (Entry) obj);
                    }
                }).forEach(new Consumer() {
                    public final void accept(Object obj) {
                        QSTileHost.this.lambda$onTuningChanged$3$QSTileHost((Entry) obj);
                    }
                });
                LinkedHashMap linkedHashMap = new LinkedHashMap();
                for (String str4 : loadTileSpecs) {
                    QSTile qSTile = (QSTile) this.mTiles.get(str4);
                    String str5 = "Tile not available";
                    String str6 = "Destroying not available tile: ";
                    if (qSTile != null) {
                        boolean z = qSTile instanceof CustomTile;
                        if (!z || ((CustomTile) qSTile).getUser() == currentUser) {
                            if (qSTile.isAvailable()) {
                                if (DEBUG) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Adding ");
                                    sb.append(qSTile);
                                    Log.d(str3, sb.toString());
                                }
                                qSTile.removeCallbacks();
                                if (!z && this.mCurrentUser != currentUser) {
                                    qSTile.userSwitch(currentUser);
                                }
                                linkedHashMap.put(str4, qSTile);
                                this.mQSLogger.logTileAdded(str4);
                            } else {
                                qSTile.destroy();
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append(str6);
                                sb2.append(str4);
                                Log.d(str3, sb2.toString());
                                this.mQSLogger.logTileDestroyed(str4, str5);
                            }
                        }
                    }
                    if (qSTile != null) {
                        qSTile.destroy();
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("Destroying tile for wrong user: ");
                        sb3.append(str4);
                        Log.d(str3, sb3.toString());
                        this.mQSLogger.logTileDestroyed(str4, "Tile for wrong user");
                    }
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("Creating tile: ");
                    sb4.append(str4);
                    Log.d(str3, sb4.toString());
                    try {
                        QSTile createTile = createTile(str4);
                        if (createTile != null) {
                            createTile.setTileSpec(str4);
                            if (createTile.isAvailable()) {
                                linkedHashMap.put(str4, createTile);
                                this.mQSLogger.logTileAdded(str4);
                            } else {
                                createTile.destroy();
                                StringBuilder sb5 = new StringBuilder();
                                sb5.append(str6);
                                sb5.append(str4);
                                Log.d(str3, sb5.toString());
                                this.mQSLogger.logTileDestroyed(str4, str5);
                            }
                        }
                    } catch (Throwable th) {
                        StringBuilder sb6 = new StringBuilder();
                        sb6.append("Error creating tile for spec: ");
                        sb6.append(str4);
                        Log.w(str3, sb6.toString(), th);
                    }
                }
                this.mCurrentUser = currentUser;
                ArrayList arrayList = new ArrayList(this.mTileSpecs);
                this.mTileSpecs.clear();
                this.mTileSpecs.addAll(loadTileSpecs);
                this.mTiles.clear();
                this.mTiles.putAll(linkedHashMap);
                if (!linkedHashMap.isEmpty() || loadTileSpecs.isEmpty()) {
                    for (int i = 0; i < this.mCallbacks.size(); i++) {
                        ((Callback) this.mCallbacks.get(i)).onTilesChanged();
                    }
                } else {
                    Log.d(str3, "No valid tiles on tuning changed. Setting to default.");
                    changeTiles(arrayList, loadTileSpecs(this.mContext, ""));
                }
            }
        }
    }

    static /* synthetic */ boolean lambda$onTuningChanged$2(List list, Entry entry) {
        return !list.contains(entry.getKey());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onTuningChanged$3 */
    public /* synthetic */ void lambda$onTuningChanged$3$QSTileHost(Entry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("Destroying tile: ");
        sb.append((String) entry.getKey());
        Log.d("QSTileHost", sb.toString());
        this.mQSLogger.logTileDestroyed((String) entry.getKey(), "Tile removed");
        ((QSTile) entry.getValue()).destroy();
    }

    public void removeTile(String str) {
        changeTileSpecs(new Predicate(str) {
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ((List) obj).remove(this.f$0);
            }
        });
    }

    public void unmarkTileAsAutoAdded(String str) {
        AutoTileManager autoTileManager = this.mAutoTiles;
        if (autoTileManager != null) {
            autoTileManager.unmarkTileAsAutoAdded(str);
        }
    }

    static /* synthetic */ boolean lambda$addTile$5(String str, List list) {
        return !list.contains(str) && list.add(str);
    }

    public void addTile(String str) {
        changeTileSpecs(new Predicate(str) {
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return QSTileHost.lambda$addTile$5(this.f$0, (List) obj);
            }
        });
    }

    private void changeTileSpecs(Predicate<List<String>> predicate) {
        String str = "sysui_qs_tiles";
        List loadTileSpecs = loadTileSpecs(this.mContext, Secure.getStringForUser(this.mContext.getContentResolver(), str, ActivityManager.getCurrentUser()));
        if (predicate.test(loadTileSpecs)) {
            Secure.putStringForUser(this.mContext.getContentResolver(), str, TextUtils.join(",", loadTileSpecs), ActivityManager.getCurrentUser());
        }
    }

    public void addTile(ComponentName componentName) {
        String spec = CustomTile.toSpec(componentName);
        if (!this.mTileSpecs.contains(spec)) {
            ArrayList arrayList = new ArrayList(this.mTileSpecs);
            arrayList.add(0, spec);
            changeTiles(this.mTileSpecs, arrayList);
        }
    }

    public void removeTile(ComponentName componentName) {
        ArrayList arrayList = new ArrayList(this.mTileSpecs);
        arrayList.remove(CustomTile.toSpec(componentName));
        changeTiles(this.mTileSpecs, arrayList);
    }

    public void changeTiles(List<String> list, List<String> list2) {
        ArrayList arrayList = new ArrayList(list);
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            String str = (String) arrayList.get(i);
            if (str.startsWith("custom(") && !list2.contains(str)) {
                ComponentName componentFromSpec = CustomTile.getComponentFromSpec(str);
                TileLifecycleManager tileLifecycleManager = new TileLifecycleManager(new Handler(), this.mContext, this.mServices, new Tile(), new Intent().setComponent(componentFromSpec), new UserHandle(ActivityManager.getCurrentUser()), this.mBroadcastDispatcher);
                tileLifecycleManager.onStopListening();
                tileLifecycleManager.onTileRemoved();
                TileLifecycleManager.setTileAdded(this.mContext, componentFromSpec, false);
                tileLifecycleManager.flushMessagesAndUnbind();
            }
        }
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("saveCurrentTiles ");
            sb.append(list2);
            Log.d("QSTileHost", sb.toString());
        }
        Secure.putStringForUser(getContext().getContentResolver(), "sysui_qs_tiles", TextUtils.join(",", list2), ActivityManager.getCurrentUser());
    }

    public QSTile createTile(String str) {
        for (int i = 0; i < this.mQsFactories.size(); i++) {
            QSTile createTile = ((QSFactory) this.mQsFactories.get(i)).createTile(str);
            if (createTile != null) {
                return createTile;
            }
        }
        return null;
    }

    public QSTileView createTileView(QSTile qSTile, boolean z) {
        for (int i = 0; i < this.mQsFactories.size(); i++) {
            QSTileView createTileView = ((QSFactory) this.mQsFactories.get(i)).createTileView(qSTile, z);
            if (createTileView != null) {
                return createTileView;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Default factory didn't create view for ");
        sb.append(qSTile.getTileSpec());
        throw new RuntimeException(sb.toString());
    }

    protected static List<String> loadTileSpecs(Context context, String str) {
        Resources resources = context.getResources();
        String str2 = "QSTileHost";
        if (TextUtils.isEmpty(str)) {
            str = resources.getString(C2017R$string.quick_settings_tiles);
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Loaded tile specs from config: ");
                sb.append(str);
                Log.d(str2, sb.toString());
            }
        } else if (DEBUG) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Loaded tile specs from setting: ");
            sb2.append(str);
            Log.d(str2, sb2.toString());
        }
        ArrayList arrayList = new ArrayList();
        ArraySet arraySet = new ArraySet();
        boolean z = false;
        for (String trim : str.split(",")) {
            String trim2 = trim.trim();
            if (!trim2.isEmpty()) {
                if (trim2.equals("default")) {
                    if (!z) {
                        for (String str3 : getDefaultSpecs(context)) {
                            if (!arraySet.contains(str3)) {
                                arrayList.add(str3);
                                arraySet.add(str3);
                            }
                        }
                        z = true;
                    }
                } else if (!arraySet.contains(trim2)) {
                    arrayList.add(trim2);
                    arraySet.add(trim2);
                }
            }
        }
        return arrayList;
    }

    public static List<String> getDefaultSpecs(Context context) {
        ArrayList arrayList = new ArrayList();
        Resources resources = context.getResources();
        String string = resources.getString(C2017R$string.quick_settings_tiles_default);
        String string2 = resources.getString(17039760);
        String str = ",";
        arrayList.addAll(Arrays.asList(string.split(str)));
        arrayList.addAll(Arrays.asList(string2.split(str)));
        if (Build.IS_DEBUGGABLE) {
            arrayList.add("dbg:mem");
        }
        return arrayList;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("QSTileHost:");
        this.mTiles.values().stream().filter($$Lambda$QSTileHost$w0YHlhMwIm7qnoeEO7kRZCq47o8.INSTANCE).forEach(new Consumer(fileDescriptor, printWriter, strArr) {
            public final /* synthetic */ FileDescriptor f$0;
            public final /* synthetic */ PrintWriter f$1;
            public final /* synthetic */ String[] f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void accept(Object obj) {
                ((Dumpable) ((QSTile) obj)).dump(this.f$0, this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ boolean lambda$dump$6(QSTile qSTile) {
        return qSTile instanceof Dumpable;
    }
}
