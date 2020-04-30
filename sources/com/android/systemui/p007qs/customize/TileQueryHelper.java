package com.android.systemui.p007qs.customize;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.ArraySet;
import android.widget.Button;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.QSTileHost;
import com.android.systemui.p007qs.external.CustomTile;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.DrawableIcon;
import com.android.systemui.plugins.p006qs.QSTile;
import com.android.systemui.plugins.p006qs.QSTile.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

/* renamed from: com.android.systemui.qs.customize.TileQueryHelper */
public class TileQueryHelper {
    private final Executor mBgExecutor;
    private final Context mContext;
    private boolean mFinished;
    private TileStateListener mListener;
    private final Executor mMainExecutor;
    private final ArraySet<String> mSpecs = new ArraySet<>();
    private final ArrayList<TileInfo> mTiles = new ArrayList<>();

    /* renamed from: com.android.systemui.qs.customize.TileQueryHelper$TileInfo */
    public static class TileInfo {
        public boolean isSystem;
        public String spec;
        public State state;
    }

    /* renamed from: com.android.systemui.qs.customize.TileQueryHelper$TileStateListener */
    public interface TileStateListener {
        void onTilesChanged(List<TileInfo> list);
    }

    public TileQueryHelper(Context context, Executor executor, Executor executor2) {
        this.mContext = context;
        this.mMainExecutor = executor;
        this.mBgExecutor = executor2;
    }

    public void setListener(TileStateListener tileStateListener) {
        this.mListener = tileStateListener;
    }

    public void queryTiles(QSTileHost qSTileHost) {
        this.mTiles.clear();
        this.mSpecs.clear();
        this.mFinished = false;
        addCurrentAndStockTiles(qSTileHost);
        addPackageTiles(qSTileHost);
    }

    public boolean isFinished() {
        return this.mFinished;
    }

    private void addCurrentAndStockTiles(QSTileHost qSTileHost) {
        String[] split;
        String string = this.mContext.getString(C2017R$string.quick_settings_tiles_stock);
        String string2 = Secure.getString(this.mContext.getContentResolver(), "sysui_qs_tiles");
        ArrayList arrayList = new ArrayList();
        String str = ",";
        if (string2 != null) {
            arrayList.addAll(Arrays.asList(string2.split(str)));
        } else {
            string2 = "";
        }
        for (String str2 : string.split(str)) {
            if (!string2.contains(str2)) {
                arrayList.add(str2);
            }
        }
        if (Build.IS_DEBUGGABLE) {
            String str3 = "dbg:mem";
            if (!string2.contains(str3)) {
                arrayList.add(str3);
            }
        }
        ArrayList arrayList2 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            String str4 = (String) it.next();
            if (!str4.startsWith("custom(")) {
                QSTile createTile = qSTileHost.createTile(str4);
                if (createTile != null) {
                    if (!createTile.isAvailable()) {
                        createTile.destroy();
                    } else {
                        createTile.setListening(this, true);
                        createTile.refreshState();
                        createTile.setListening(this, false);
                        createTile.setTileSpec(str4);
                        arrayList2.add(createTile);
                    }
                }
            }
        }
        this.mBgExecutor.execute(new Runnable(arrayList2) {
            public final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TileQueryHelper.this.lambda$addCurrentAndStockTiles$0$TileQueryHelper(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addCurrentAndStockTiles$0 */
    public /* synthetic */ void lambda$addCurrentAndStockTiles$0$TileQueryHelper(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            QSTile qSTile = (QSTile) it.next();
            State copy = qSTile.getState().copy();
            copy.label = qSTile.getTileLabel();
            qSTile.destroy();
            addTile(qSTile.getTileSpec(), null, copy, true);
        }
        notifyTilesChanged(false);
    }

    private void addPackageTiles(QSTileHost qSTileHost) {
        this.mBgExecutor.execute(new Runnable(qSTileHost) {
            public final /* synthetic */ QSTileHost f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TileQueryHelper.this.lambda$addPackageTiles$1$TileQueryHelper(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addPackageTiles$1 */
    public /* synthetic */ void lambda$addPackageTiles$1$TileQueryHelper(QSTileHost qSTileHost) {
        Collection tiles = qSTileHost.getTiles();
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentServicesAsUser = packageManager.queryIntentServicesAsUser(new Intent("android.service.quicksettings.action.QS_TILE"), 0, ActivityManager.getCurrentUser());
        String string = this.mContext.getString(C2017R$string.quick_settings_tiles_stock);
        for (ResolveInfo resolveInfo : queryIntentServicesAsUser) {
            ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
            if (!string.contains(componentName.flattenToString())) {
                CharSequence loadLabel = resolveInfo.serviceInfo.applicationInfo.loadLabel(packageManager);
                String spec = CustomTile.toSpec(componentName);
                State state = getState(tiles, spec);
                if (state != null) {
                    addTile(spec, loadLabel, state, false);
                } else {
                    ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                    if (serviceInfo.icon != 0 || serviceInfo.applicationInfo.icon != 0) {
                        Drawable loadIcon = resolveInfo.serviceInfo.loadIcon(packageManager);
                        if ("android.permission.BIND_QUICK_SETTINGS_TILE".equals(resolveInfo.serviceInfo.permission) && loadIcon != null) {
                            loadIcon.mutate();
                            loadIcon.setTint(this.mContext.getColor(17170443));
                            CharSequence loadLabel2 = resolveInfo.serviceInfo.loadLabel(packageManager);
                            createStateAndAddTile(spec, loadIcon, loadLabel2 != null ? loadLabel2.toString() : "null", loadLabel);
                        }
                    }
                }
            }
        }
        notifyTilesChanged(true);
    }

    private void notifyTilesChanged(boolean z) {
        this.mMainExecutor.execute(new Runnable(new ArrayList(this.mTiles), z) {
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TileQueryHelper.this.lambda$notifyTilesChanged$2$TileQueryHelper(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$notifyTilesChanged$2 */
    public /* synthetic */ void lambda$notifyTilesChanged$2$TileQueryHelper(ArrayList arrayList, boolean z) {
        TileStateListener tileStateListener = this.mListener;
        if (tileStateListener != null) {
            tileStateListener.onTilesChanged(arrayList);
        }
        this.mFinished = z;
    }

    private State getState(Collection<QSTile> collection, String str) {
        for (QSTile qSTile : collection) {
            if (str.equals(qSTile.getTileSpec())) {
                return qSTile.getState().copy();
            }
        }
        return null;
    }

    private void addTile(String str, CharSequence charSequence, State state, boolean z) {
        if (!this.mSpecs.contains(str)) {
            TileInfo tileInfo = new TileInfo();
            tileInfo.state = state;
            state.dualTarget = false;
            state.expandedAccessibilityClassName = Button.class.getName();
            tileInfo.spec = str;
            State state2 = tileInfo.state;
            if (z || TextUtils.equals(state.label, charSequence)) {
                charSequence = null;
            }
            state2.secondaryLabel = charSequence;
            tileInfo.isSystem = z;
            this.mTiles.add(tileInfo);
            this.mSpecs.add(str);
        }
    }

    private void createStateAndAddTile(String str, Drawable drawable, CharSequence charSequence, CharSequence charSequence2) {
        State state = new State();
        state.state = 1;
        state.label = charSequence;
        state.contentDescription = charSequence;
        state.icon = new DrawableIcon(drawable);
        addTile(str, charSequence2, state, false);
    }
}
