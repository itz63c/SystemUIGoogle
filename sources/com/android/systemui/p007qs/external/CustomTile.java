package com.android.systemui.p007qs.external;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.Icon;
import android.metrics.LogMaker;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.quicksettings.IQSTileService;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import android.widget.Switch;
import com.android.systemui.Dependency;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.external.TileLifecycleManager.TileChangeListener;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.DrawableIcon;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.p006qs.QSTile;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.State;
import java.util.Objects;
import java.util.function.Supplier;

/* renamed from: com.android.systemui.qs.external.CustomTile */
public class CustomTile extends QSTileImpl<State> implements TileChangeListener {
    private final ComponentName mComponent;
    private Icon mDefaultIcon;
    private CharSequence mDefaultLabel;
    private boolean mIsShowingDialog;
    private boolean mIsTokenGranted;
    private boolean mListening;
    private final IQSTileService mService;
    private final TileServiceManager mServiceManager;
    private final Tile mTile;
    private final IBinder mToken = new Binder();
    private final int mUser;
    private final Context mUserContext;
    private final IWindowManager mWindowManager = WindowManagerGlobal.getWindowManagerService();

    public int getMetricsCategory() {
        return 268;
    }

    private CustomTile(QSHost qSHost, String str, Context context) {
        super(qSHost);
        this.mComponent = ComponentName.unflattenFromString(str);
        this.mTile = new Tile();
        this.mUserContext = context;
        this.mUser = context.getUserId();
        updateDefaultTileAndIcon();
        TileServiceManager tileWrapper = qSHost.getTileServices().getTileWrapper(this);
        this.mServiceManager = tileWrapper;
        if (tileWrapper.isToggleableTile()) {
            resetStates();
        }
        this.mService = this.mServiceManager.getTileService();
        this.mServiceManager.setTileChangeListener(this);
    }

    /* access modifiers changed from: protected */
    public long getStaleTimeout() {
        return (((long) this.mHost.indexOf(getTileSpec())) * 60000) + 3600000;
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0041 A[Catch:{ NameNotFoundException -> 0x007b }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x004c A[Catch:{ NameNotFoundException -> 0x007b }] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0051 A[Catch:{ NameNotFoundException -> 0x007b }] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0075 A[Catch:{ NameNotFoundException -> 0x007b }] */
    /* JADX WARNING: Removed duplicated region for block: B:33:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateDefaultTileAndIcon() {
        /*
            r8 = this;
            r0 = 0
            android.content.Context r1 = r8.mUserContext     // Catch:{ NameNotFoundException -> 0x007b }
            android.content.pm.PackageManager r1 = r1.getPackageManager()     // Catch:{ NameNotFoundException -> 0x007b }
            r2 = 786432(0xc0000, float:1.102026E-39)
            boolean r3 = r8.isSystemApp(r1)     // Catch:{ NameNotFoundException -> 0x007b }
            if (r3 == 0) goto L_0x0012
            r2 = 786944(0xc0200, float:1.102743E-39)
        L_0x0012:
            android.content.ComponentName r3 = r8.mComponent     // Catch:{ NameNotFoundException -> 0x007b }
            android.content.pm.ServiceInfo r2 = r1.getServiceInfo(r3, r2)     // Catch:{ NameNotFoundException -> 0x007b }
            int r3 = r2.icon     // Catch:{ NameNotFoundException -> 0x007b }
            if (r3 == 0) goto L_0x001f
            int r3 = r2.icon     // Catch:{ NameNotFoundException -> 0x007b }
            goto L_0x0023
        L_0x001f:
            android.content.pm.ApplicationInfo r3 = r2.applicationInfo     // Catch:{ NameNotFoundException -> 0x007b }
            int r3 = r3.icon     // Catch:{ NameNotFoundException -> 0x007b }
        L_0x0023:
            android.service.quicksettings.Tile r4 = r8.mTile     // Catch:{ NameNotFoundException -> 0x007b }
            android.graphics.drawable.Icon r4 = r4.getIcon()     // Catch:{ NameNotFoundException -> 0x007b }
            r5 = 0
            r6 = 1
            if (r4 == 0) goto L_0x003e
            android.service.quicksettings.Tile r4 = r8.mTile     // Catch:{ NameNotFoundException -> 0x007b }
            android.graphics.drawable.Icon r4 = r4.getIcon()     // Catch:{ NameNotFoundException -> 0x007b }
            android.graphics.drawable.Icon r7 = r8.mDefaultIcon     // Catch:{ NameNotFoundException -> 0x007b }
            boolean r4 = r8.iconEquals(r4, r7)     // Catch:{ NameNotFoundException -> 0x007b }
            if (r4 == 0) goto L_0x003c
            goto L_0x003e
        L_0x003c:
            r4 = r5
            goto L_0x003f
        L_0x003e:
            r4 = r6
        L_0x003f:
            if (r3 == 0) goto L_0x004c
            android.content.ComponentName r7 = r8.mComponent     // Catch:{ NameNotFoundException -> 0x007b }
            java.lang.String r7 = r7.getPackageName()     // Catch:{ NameNotFoundException -> 0x007b }
            android.graphics.drawable.Icon r3 = android.graphics.drawable.Icon.createWithResource(r7, r3)     // Catch:{ NameNotFoundException -> 0x007b }
            goto L_0x004d
        L_0x004c:
            r3 = r0
        L_0x004d:
            r8.mDefaultIcon = r3     // Catch:{ NameNotFoundException -> 0x007b }
            if (r4 == 0) goto L_0x0056
            android.service.quicksettings.Tile r4 = r8.mTile     // Catch:{ NameNotFoundException -> 0x007b }
            r4.setIcon(r3)     // Catch:{ NameNotFoundException -> 0x007b }
        L_0x0056:
            android.service.quicksettings.Tile r3 = r8.mTile     // Catch:{ NameNotFoundException -> 0x007b }
            java.lang.CharSequence r3 = r3.getLabel()     // Catch:{ NameNotFoundException -> 0x007b }
            if (r3 == 0) goto L_0x006c
            android.service.quicksettings.Tile r3 = r8.mTile     // Catch:{ NameNotFoundException -> 0x007b }
            java.lang.CharSequence r3 = r3.getLabel()     // Catch:{ NameNotFoundException -> 0x007b }
            java.lang.CharSequence r4 = r8.mDefaultLabel     // Catch:{ NameNotFoundException -> 0x007b }
            boolean r3 = android.text.TextUtils.equals(r3, r4)     // Catch:{ NameNotFoundException -> 0x007b }
            if (r3 == 0) goto L_0x006d
        L_0x006c:
            r5 = r6
        L_0x006d:
            java.lang.CharSequence r1 = r2.loadLabel(r1)     // Catch:{ NameNotFoundException -> 0x007b }
            r8.mDefaultLabel = r1     // Catch:{ NameNotFoundException -> 0x007b }
            if (r5 == 0) goto L_0x007f
            android.service.quicksettings.Tile r2 = r8.mTile     // Catch:{ NameNotFoundException -> 0x007b }
            r2.setLabel(r1)     // Catch:{ NameNotFoundException -> 0x007b }
            goto L_0x007f
        L_0x007b:
            r8.mDefaultIcon = r0
            r8.mDefaultLabel = r0
        L_0x007f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.external.CustomTile.updateDefaultTileAndIcon():void");
    }

    private boolean isSystemApp(PackageManager packageManager) throws NameNotFoundException {
        return packageManager.getApplicationInfo(this.mComponent.getPackageName(), 0).isSystemApp();
    }

    private boolean iconEquals(Icon icon, Icon icon2) {
        if (icon == icon2) {
            return true;
        }
        return icon != null && icon2 != null && icon.getType() == 2 && icon2.getType() == 2 && icon.getResId() == icon2.getResId() && Objects.equals(icon.getResPackage(), icon2.getResPackage());
    }

    public void onTileChanged(ComponentName componentName) {
        updateDefaultTileAndIcon();
    }

    public boolean isAvailable() {
        return this.mDefaultIcon != null;
    }

    public int getUser() {
        return this.mUser;
    }

    public ComponentName getComponent() {
        return this.mComponent;
    }

    public LogMaker populate(LogMaker logMaker) {
        return super.populate(logMaker).setComponentName(this.mComponent);
    }

    public Tile getQsTile() {
        updateDefaultTileAndIcon();
        return this.mTile;
    }

    public void updateState(Tile tile) {
        this.mTile.setIcon(tile.getIcon());
        this.mTile.setLabel(tile.getLabel());
        this.mTile.setSubtitle(tile.getSubtitle());
        this.mTile.setContentDescription(tile.getContentDescription());
        this.mTile.setStateDescription(tile.getStateDescription());
        this.mTile.setState(tile.getState());
    }

    public void onDialogShown() {
        this.mIsShowingDialog = true;
    }

    public void onDialogHidden() {
        this.mIsShowingDialog = false;
        try {
            this.mWindowManager.removeWindowToken(this.mToken, 0);
        } catch (RemoteException unused) {
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:13|14|15|16) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x003b */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleSetListening(boolean r3) {
        /*
            r2 = this;
            super.handleSetListening(r3)
            boolean r0 = r2.mListening
            if (r0 != r3) goto L_0x0008
            return
        L_0x0008:
            r2.mListening = r3
            if (r3 == 0) goto L_0x0026
            r2.updateDefaultTileAndIcon()     // Catch:{ RemoteException -> 0x0044 }
            r2.refreshState()     // Catch:{ RemoteException -> 0x0044 }
            com.android.systemui.qs.external.TileServiceManager r3 = r2.mServiceManager     // Catch:{ RemoteException -> 0x0044 }
            boolean r3 = r3.isActiveTile()     // Catch:{ RemoteException -> 0x0044 }
            if (r3 != 0) goto L_0x0044
            com.android.systemui.qs.external.TileServiceManager r3 = r2.mServiceManager     // Catch:{ RemoteException -> 0x0044 }
            r0 = 1
            r3.setBindRequested(r0)     // Catch:{ RemoteException -> 0x0044 }
            android.service.quicksettings.IQSTileService r2 = r2.mService     // Catch:{ RemoteException -> 0x0044 }
            r2.onStartListening()     // Catch:{ RemoteException -> 0x0044 }
            goto L_0x0044
        L_0x0026:
            android.service.quicksettings.IQSTileService r3 = r2.mService     // Catch:{ RemoteException -> 0x0044 }
            r3.onStopListening()     // Catch:{ RemoteException -> 0x0044 }
            boolean r3 = r2.mIsTokenGranted     // Catch:{ RemoteException -> 0x0044 }
            r0 = 0
            if (r3 == 0) goto L_0x003d
            boolean r3 = r2.mIsShowingDialog     // Catch:{ RemoteException -> 0x0044 }
            if (r3 != 0) goto L_0x003d
            android.view.IWindowManager r3 = r2.mWindowManager     // Catch:{ RemoteException -> 0x003b }
            android.os.IBinder r1 = r2.mToken     // Catch:{ RemoteException -> 0x003b }
            r3.removeWindowToken(r1, r0)     // Catch:{ RemoteException -> 0x003b }
        L_0x003b:
            r2.mIsTokenGranted = r0     // Catch:{ RemoteException -> 0x0044 }
        L_0x003d:
            r2.mIsShowingDialog = r0     // Catch:{ RemoteException -> 0x0044 }
            com.android.systemui.qs.external.TileServiceManager r2 = r2.mServiceManager     // Catch:{ RemoteException -> 0x0044 }
            r2.setBindRequested(r0)     // Catch:{ RemoteException -> 0x0044 }
        L_0x0044:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.external.CustomTile.handleSetListening(boolean):void");
    }

    /* access modifiers changed from: protected */
    public void handleDestroy() {
        super.handleDestroy();
        if (this.mIsTokenGranted) {
            try {
                this.mWindowManager.removeWindowToken(this.mToken, 0);
            } catch (RemoteException unused) {
            }
        }
        this.mHost.getTileServices().freeService(this, this.mServiceManager);
    }

    public State newTileState() {
        TileServiceManager tileServiceManager = this.mServiceManager;
        if (tileServiceManager == null || !tileServiceManager.isToggleableTile()) {
            return new State();
        }
        return new BooleanState();
    }

    public Intent getLongClickIntent() {
        Intent intent = new Intent("android.service.quicksettings.action.QS_TILE_PREFERENCES");
        intent.setPackage(this.mComponent.getPackageName());
        Intent resolveIntent = resolveIntent(intent);
        if (resolveIntent != null) {
            resolveIntent.putExtra("android.intent.extra.COMPONENT_NAME", this.mComponent);
            resolveIntent.putExtra("state", this.mTile.getState());
            return resolveIntent;
        }
        return new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", this.mComponent.getPackageName(), null));
    }

    private Intent resolveIntent(Intent intent) {
        ResolveInfo resolveActivityAsUser = this.mContext.getPackageManager().resolveActivityAsUser(intent, 0, ActivityManager.getCurrentUser());
        if (resolveActivityAsUser == null) {
            return null;
        }
        Intent intent2 = new Intent("android.service.quicksettings.action.QS_TILE_PREFERENCES");
        ActivityInfo activityInfo = resolveActivityAsUser.activityInfo;
        return intent2.setClassName(activityInfo.packageName, activityInfo.name);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't wrap try/catch for region: R(8:3|4|5|6|7|(1:9)|10|11) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0016 */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x001e A[Catch:{ RemoteException -> 0x002f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleClick() {
        /*
            r5 = this;
            android.service.quicksettings.Tile r0 = r5.mTile
            int r0 = r0.getState()
            if (r0 != 0) goto L_0x0009
            return
        L_0x0009:
            r0 = 1
            android.view.IWindowManager r1 = r5.mWindowManager     // Catch:{ RemoteException -> 0x0016 }
            android.os.IBinder r2 = r5.mToken     // Catch:{ RemoteException -> 0x0016 }
            r3 = 2035(0x7f3, float:2.852E-42)
            r4 = 0
            r1.addWindowToken(r2, r3, r4)     // Catch:{ RemoteException -> 0x0016 }
            r5.mIsTokenGranted = r0     // Catch:{ RemoteException -> 0x0016 }
        L_0x0016:
            com.android.systemui.qs.external.TileServiceManager r1 = r5.mServiceManager     // Catch:{ RemoteException -> 0x002f }
            boolean r1 = r1.isActiveTile()     // Catch:{ RemoteException -> 0x002f }
            if (r1 == 0) goto L_0x0028
            com.android.systemui.qs.external.TileServiceManager r1 = r5.mServiceManager     // Catch:{ RemoteException -> 0x002f }
            r1.setBindRequested(r0)     // Catch:{ RemoteException -> 0x002f }
            android.service.quicksettings.IQSTileService r0 = r5.mService     // Catch:{ RemoteException -> 0x002f }
            r0.onStartListening()     // Catch:{ RemoteException -> 0x002f }
        L_0x0028:
            android.service.quicksettings.IQSTileService r0 = r5.mService     // Catch:{ RemoteException -> 0x002f }
            android.os.IBinder r5 = r5.mToken     // Catch:{ RemoteException -> 0x002f }
            r0.onClick(r5)     // Catch:{ RemoteException -> 0x002f }
        L_0x002f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.external.CustomTile.handleClick():void");
    }

    public CharSequence getTileLabel() {
        return getState().label;
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(State state, Object obj) {
        Drawable drawable;
        int state2 = this.mTile.getState();
        boolean z = false;
        if (this.mServiceManager.hasPendingBind()) {
            state2 = 0;
        }
        state.state = state2;
        try {
            drawable = this.mTile.getIcon().loadDrawable(this.mUserContext);
        } catch (Exception unused) {
            Log.w(this.TAG, "Invalid icon, forcing into unavailable state");
            state.state = 0;
            drawable = this.mDefaultIcon.loadDrawable(this.mUserContext);
        }
        state.iconSupplier = new Supplier(drawable) {
            public final /* synthetic */ Drawable f$0;

            {
                this.f$0 = r1;
            }

            public final Object get() {
                return CustomTile.lambda$handleUpdateState$0(this.f$0);
            }
        };
        state.label = this.mTile.getLabel();
        CharSequence subtitle = this.mTile.getSubtitle();
        if (subtitle == null || subtitle.length() <= 0) {
            state.secondaryLabel = null;
        } else {
            state.secondaryLabel = subtitle;
        }
        if (this.mTile.getContentDescription() != null) {
            state.contentDescription = this.mTile.getContentDescription();
        } else {
            state.contentDescription = state.label;
        }
        if (this.mTile.getStateDescription() != null) {
            state.stateDescription = this.mTile.getStateDescription();
        } else {
            state.stateDescription = null;
        }
        if (state instanceof BooleanState) {
            state.expandedAccessibilityClassName = Switch.class.getName();
            BooleanState booleanState = (BooleanState) state;
            if (state.state == 2) {
                z = true;
            }
            booleanState.value = z;
        }
    }

    static /* synthetic */ QSTile.Icon lambda$handleUpdateState$0(Drawable drawable) {
        DrawableIcon drawableIcon = null;
        if (drawable == null) {
            return null;
        }
        ConstantState constantState = drawable.getConstantState();
        if (constantState != null) {
            drawableIcon = new DrawableIcon(constantState.newDrawable());
        }
        return drawableIcon;
    }

    public void startUnlockAndRun() {
        ((ActivityStarter) Dependency.get(ActivityStarter.class)).postQSRunnableDismissingKeyguard(new Runnable() {
            public final void run() {
                CustomTile.this.lambda$startUnlockAndRun$1$CustomTile();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startUnlockAndRun$1 */
    public /* synthetic */ void lambda$startUnlockAndRun$1$CustomTile() {
        try {
            this.mService.onUnlockComplete();
        } catch (RemoteException unused) {
        }
    }

    public static String toSpec(ComponentName componentName) {
        StringBuilder sb = new StringBuilder();
        sb.append("custom(");
        sb.append(componentName.flattenToShortString());
        sb.append(")");
        return sb.toString();
    }

    public static ComponentName getComponentFromSpec(String str) {
        String substring = str.substring(7, str.length() - 1);
        if (!substring.isEmpty()) {
            return ComponentName.unflattenFromString(substring);
        }
        throw new IllegalArgumentException("Empty custom tile spec action");
    }

    public static CustomTile create(QSHost qSHost, String str, Context context) {
        if (str == null || !str.startsWith("custom(") || !str.endsWith(")")) {
            StringBuilder sb = new StringBuilder();
            sb.append("Bad custom tile spec: ");
            sb.append(str);
            throw new IllegalArgumentException(sb.toString());
        }
        String substring = str.substring(7, str.length() - 1);
        if (!substring.isEmpty()) {
            return new CustomTile(qSHost, substring, context);
        }
        throw new IllegalArgumentException("Empty custom tile spec action");
    }
}
