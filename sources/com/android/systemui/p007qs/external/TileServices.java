package com.android.systemui.p007qs.external;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.UserHandle;
import android.service.quicksettings.IQSService.Stub;
import android.service.quicksettings.Tile;
import android.util.ArrayMap;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.Dependency;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p007qs.QSTileHost;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* renamed from: com.android.systemui.qs.external.TileServices */
public class TileServices extends Stub {
    private static final Comparator<TileServiceManager> SERVICE_SORT = new Comparator<TileServiceManager>() {
        public int compare(TileServiceManager tileServiceManager, TileServiceManager tileServiceManager2) {
            return -Integer.compare(tileServiceManager.getBindPriority(), tileServiceManager2.getBindPriority());
        }
    };
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Context mContext;
    private final Handler mHandler;
    /* access modifiers changed from: private */
    public final QSTileHost mHost;
    private final Handler mMainHandler;
    private int mMaxBound = 3;
    private final BroadcastReceiver mRequestListeningReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.service.quicksettings.action.REQUEST_LISTENING".equals(intent.getAction())) {
                TileServices.this.requestListening((ComponentName) intent.getParcelableExtra("android.intent.extra.COMPONENT_NAME"));
            }
        }
    };
    private final ArrayMap<CustomTile, TileServiceManager> mServices = new ArrayMap<>();
    private final ArrayMap<ComponentName, CustomTile> mTiles = new ArrayMap<>();
    private final ArrayMap<IBinder, CustomTile> mTokenMap = new ArrayMap<>();

    public TileServices(QSTileHost qSTileHost, Looper looper, BroadcastDispatcher broadcastDispatcher) {
        this.mHost = qSTileHost;
        this.mContext = qSTileHost.getContext();
        this.mBroadcastDispatcher = broadcastDispatcher;
        broadcastDispatcher.registerReceiver(this.mRequestListeningReceiver, new IntentFilter("android.service.quicksettings.action.REQUEST_LISTENING"));
        this.mHandler = new Handler(looper);
        this.mMainHandler = new Handler(Looper.getMainLooper());
    }

    public Context getContext() {
        return this.mContext;
    }

    public QSTileHost getHost() {
        return this.mHost;
    }

    public TileServiceManager getTileWrapper(CustomTile customTile) {
        ComponentName component = customTile.getComponent();
        TileServiceManager onCreateTileService = onCreateTileService(component, customTile.getQsTile(), this.mBroadcastDispatcher);
        synchronized (this.mServices) {
            this.mServices.put(customTile, onCreateTileService);
            this.mTiles.put(component, customTile);
            this.mTokenMap.put(onCreateTileService.getToken(), customTile);
        }
        onCreateTileService.startLifecycleManagerAndAddTile();
        return onCreateTileService;
    }

    /* access modifiers changed from: protected */
    public TileServiceManager onCreateTileService(ComponentName componentName, Tile tile, BroadcastDispatcher broadcastDispatcher) {
        TileServiceManager tileServiceManager = new TileServiceManager(this, this.mHandler, componentName, tile, broadcastDispatcher);
        return tileServiceManager;
    }

    public void freeService(CustomTile customTile, TileServiceManager tileServiceManager) {
        synchronized (this.mServices) {
            tileServiceManager.setBindAllowed(false);
            tileServiceManager.handleDestroy();
            this.mServices.remove(customTile);
            this.mTokenMap.remove(tileServiceManager.getToken());
            this.mTiles.remove(customTile.getComponent());
            this.mMainHandler.post(new Runnable(customTile.getComponent().getClassName()) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TileServices.this.lambda$freeService$0$TileServices(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$freeService$0 */
    public /* synthetic */ void lambda$freeService$0$TileServices(String str) {
        this.mHost.getIconController().removeAllIconsForSlot(str);
    }

    public void recalculateBindAllowance() {
        ArrayList arrayList;
        synchronized (this.mServices) {
            arrayList = new ArrayList(this.mServices.values());
        }
        int size = arrayList.size();
        if (size > this.mMaxBound) {
            long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < size; i++) {
                ((TileServiceManager) arrayList.get(i)).calculateBindPriority(currentTimeMillis);
            }
            Collections.sort(arrayList, SERVICE_SORT);
        }
        int i2 = 0;
        while (i2 < this.mMaxBound && i2 < size) {
            ((TileServiceManager) arrayList.get(i2)).setBindAllowed(true);
            i2++;
        }
        while (i2 < size) {
            ((TileServiceManager) arrayList.get(i2)).setBindAllowed(false);
            i2++;
        }
    }

    private void verifyCaller(CustomTile customTile) {
        try {
            if (Binder.getCallingUid() != this.mContext.getPackageManager().getPackageUidAsUser(customTile.getComponent().getPackageName(), Binder.getCallingUserHandle().getIdentifier())) {
                throw new SecurityException("Component outside caller's uid");
            }
        } catch (NameNotFoundException e) {
            throw new SecurityException(e);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:12|13|14|15|16|17) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x003c */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void requestListening(android.content.ComponentName r4) {
        /*
            r3 = this;
            android.util.ArrayMap<com.android.systemui.qs.external.CustomTile, com.android.systemui.qs.external.TileServiceManager> r0 = r3.mServices
            monitor-enter(r0)
            com.android.systemui.qs.external.CustomTile r1 = r3.getTileForComponent(r4)     // Catch:{ all -> 0x003e }
            if (r1 != 0) goto L_0x0021
            java.lang.String r3 = "TileServices"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x003e }
            r1.<init>()     // Catch:{ all -> 0x003e }
            java.lang.String r2 = "Couldn't find tile for "
            r1.append(r2)     // Catch:{ all -> 0x003e }
            r1.append(r4)     // Catch:{ all -> 0x003e }
            java.lang.String r4 = r1.toString()     // Catch:{ all -> 0x003e }
            android.util.Log.d(r3, r4)     // Catch:{ all -> 0x003e }
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            return
        L_0x0021:
            android.util.ArrayMap<com.android.systemui.qs.external.CustomTile, com.android.systemui.qs.external.TileServiceManager> r3 = r3.mServices     // Catch:{ all -> 0x003e }
            java.lang.Object r3 = r3.get(r1)     // Catch:{ all -> 0x003e }
            com.android.systemui.qs.external.TileServiceManager r3 = (com.android.systemui.p007qs.external.TileServiceManager) r3     // Catch:{ all -> 0x003e }
            boolean r4 = r3.isActiveTile()     // Catch:{ all -> 0x003e }
            if (r4 != 0) goto L_0x0031
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            return
        L_0x0031:
            r4 = 1
            r3.setBindRequested(r4)     // Catch:{ all -> 0x003e }
            android.service.quicksettings.IQSTileService r3 = r3.getTileService()     // Catch:{ RemoteException -> 0x003c }
            r3.onStartListening()     // Catch:{ RemoteException -> 0x003c }
        L_0x003c:
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            return
        L_0x003e:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.external.TileServices.requestListening(android.content.ComponentName):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0028, code lost:
        r5.updateState(r4);
        r5.refreshState();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateQsTile(android.service.quicksettings.Tile r4, android.os.IBinder r5) {
        /*
            r3 = this;
            com.android.systemui.qs.external.CustomTile r5 = r3.getTileForToken(r5)
            if (r5 == 0) goto L_0x0053
            r3.verifyCaller(r5)
            android.util.ArrayMap<com.android.systemui.qs.external.CustomTile, com.android.systemui.qs.external.TileServiceManager> r0 = r3.mServices
            monitor-enter(r0)
            android.util.ArrayMap<com.android.systemui.qs.external.CustomTile, com.android.systemui.qs.external.TileServiceManager> r3 = r3.mServices     // Catch:{ all -> 0x0050 }
            java.lang.Object r3 = r3.get(r5)     // Catch:{ all -> 0x0050 }
            com.android.systemui.qs.external.TileServiceManager r3 = (com.android.systemui.p007qs.external.TileServiceManager) r3     // Catch:{ all -> 0x0050 }
            if (r3 == 0) goto L_0x002f
            boolean r1 = r3.isLifecycleStarted()     // Catch:{ all -> 0x0050 }
            if (r1 != 0) goto L_0x001d
            goto L_0x002f
        L_0x001d:
            r3.clearPendingBind()     // Catch:{ all -> 0x0050 }
            long r1 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0050 }
            r3.setLastUpdate(r1)     // Catch:{ all -> 0x0050 }
            monitor-exit(r0)     // Catch:{ all -> 0x0050 }
            r5.updateState(r4)
            r5.refreshState()
            goto L_0x0053
        L_0x002f:
            java.lang.String r3 = "TileServices"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0050 }
            r4.<init>()     // Catch:{ all -> 0x0050 }
            java.lang.String r1 = "TileServiceManager not started for "
            r4.append(r1)     // Catch:{ all -> 0x0050 }
            android.content.ComponentName r5 = r5.getComponent()     // Catch:{ all -> 0x0050 }
            r4.append(r5)     // Catch:{ all -> 0x0050 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0050 }
            java.lang.IllegalStateException r5 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0050 }
            r5.<init>()     // Catch:{ all -> 0x0050 }
            android.util.Log.e(r3, r4, r5)     // Catch:{ all -> 0x0050 }
            monitor-exit(r0)     // Catch:{ all -> 0x0050 }
            return
        L_0x0050:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0050 }
            throw r3
        L_0x0053:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.external.TileServices.updateQsTile(android.service.quicksettings.Tile, android.os.IBinder):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0021, code lost:
        r4.refreshState();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onStartSuccessful(android.os.IBinder r4) {
        /*
            r3 = this;
            com.android.systemui.qs.external.CustomTile r4 = r3.getTileForToken(r4)
            if (r4 == 0) goto L_0x0049
            r3.verifyCaller(r4)
            android.util.ArrayMap<com.android.systemui.qs.external.CustomTile, com.android.systemui.qs.external.TileServiceManager> r0 = r3.mServices
            monitor-enter(r0)
            android.util.ArrayMap<com.android.systemui.qs.external.CustomTile, com.android.systemui.qs.external.TileServiceManager> r3 = r3.mServices     // Catch:{ all -> 0x0046 }
            java.lang.Object r3 = r3.get(r4)     // Catch:{ all -> 0x0046 }
            com.android.systemui.qs.external.TileServiceManager r3 = (com.android.systemui.p007qs.external.TileServiceManager) r3     // Catch:{ all -> 0x0046 }
            if (r3 == 0) goto L_0x0025
            boolean r1 = r3.isLifecycleStarted()     // Catch:{ all -> 0x0046 }
            if (r1 != 0) goto L_0x001d
            goto L_0x0025
        L_0x001d:
            r3.clearPendingBind()     // Catch:{ all -> 0x0046 }
            monitor-exit(r0)     // Catch:{ all -> 0x0046 }
            r4.refreshState()
            goto L_0x0049
        L_0x0025:
            java.lang.String r3 = "TileServices"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0046 }
            r1.<init>()     // Catch:{ all -> 0x0046 }
            java.lang.String r2 = "TileServiceManager not started for "
            r1.append(r2)     // Catch:{ all -> 0x0046 }
            android.content.ComponentName r4 = r4.getComponent()     // Catch:{ all -> 0x0046 }
            r1.append(r4)     // Catch:{ all -> 0x0046 }
            java.lang.String r4 = r1.toString()     // Catch:{ all -> 0x0046 }
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0046 }
            r1.<init>()     // Catch:{ all -> 0x0046 }
            android.util.Log.e(r3, r4, r1)     // Catch:{ all -> 0x0046 }
            monitor-exit(r0)     // Catch:{ all -> 0x0046 }
            return
        L_0x0046:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0046 }
            throw r3
        L_0x0049:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.external.TileServices.onStartSuccessful(android.os.IBinder):void");
    }

    public void onShowDialog(IBinder iBinder) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken != null) {
            verifyCaller(tileForToken);
            tileForToken.onDialogShown();
            this.mHost.forceCollapsePanels();
            ((TileServiceManager) this.mServices.get(tileForToken)).setShowingDialog(true);
        }
    }

    public void onDialogHidden(IBinder iBinder) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken != null) {
            verifyCaller(tileForToken);
            ((TileServiceManager) this.mServices.get(tileForToken)).setShowingDialog(false);
            tileForToken.onDialogHidden();
        }
    }

    public void onStartActivity(IBinder iBinder) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken != null) {
            verifyCaller(tileForToken);
            this.mHost.forceCollapsePanels();
        }
    }

    public void updateStatusIcon(IBinder iBinder, Icon icon, String str) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken != null) {
            verifyCaller(tileForToken);
            try {
                final ComponentName component = tileForToken.getComponent();
                String packageName = component.getPackageName();
                UserHandle callingUserHandle = Stub.getCallingUserHandle();
                if (this.mContext.getPackageManager().getPackageInfoAsUser(packageName, 0, callingUserHandle.getIdentifier()).applicationInfo.isSystemApp()) {
                    final StatusBarIcon statusBarIcon = icon != null ? new StatusBarIcon(callingUserHandle, packageName, icon, 0, 0, str) : null;
                    this.mMainHandler.post(new Runnable() {
                        public void run() {
                            StatusBarIconController iconController = TileServices.this.mHost.getIconController();
                            iconController.setIcon(component.getClassName(), statusBarIcon);
                            iconController.setExternalIcon(component.getClassName());
                        }
                    });
                }
            } catch (NameNotFoundException unused) {
            }
        }
    }

    public Tile getTile(IBinder iBinder) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken == null) {
            return null;
        }
        verifyCaller(tileForToken);
        return tileForToken.getQsTile();
    }

    public void startUnlockAndRun(IBinder iBinder) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken != null) {
            verifyCaller(tileForToken);
            tileForToken.startUnlockAndRun();
        }
    }

    public boolean isLocked() {
        return ((KeyguardStateController) Dependency.get(KeyguardStateController.class)).isShowing();
    }

    public boolean isSecure() {
        KeyguardStateController keyguardStateController = (KeyguardStateController) Dependency.get(KeyguardStateController.class);
        return keyguardStateController.isMethodSecure() && keyguardStateController.isShowing();
    }

    private CustomTile getTileForToken(IBinder iBinder) {
        CustomTile customTile;
        synchronized (this.mServices) {
            customTile = (CustomTile) this.mTokenMap.get(iBinder);
        }
        return customTile;
    }

    private CustomTile getTileForComponent(ComponentName componentName) {
        CustomTile customTile;
        synchronized (this.mServices) {
            customTile = (CustomTile) this.mTiles.get(componentName);
        }
        return customTile;
    }
}
