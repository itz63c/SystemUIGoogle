package com.android.systemui.p007qs.tileimpl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.Prefs;
import com.android.systemui.p007qs.PagedTileLayout.TilePage;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.logging.QSLogger;
import com.android.systemui.p007qs.tiles.QSSettingsControllerKt;
import com.android.systemui.p007qs.tiles.QSSettingsPanel;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.p006qs.DetailAdapter;
import com.android.systemui.plugins.p006qs.QSIconView;
import com.android.systemui.plugins.p006qs.QSTile;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.Callback;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.plugins.p006qs.QSTile.State;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/* renamed from: com.android.systemui.qs.tileimpl.QSTileImpl */
public abstract class QSTileImpl<TState extends State> implements QSTile, LifecycleOwner, Dumpable {
    protected static final Object ARG_SHOW_TRANSIENT_ENABLING = new Object();
    protected static final boolean DEBUG = Log.isLoggable("Tile", 3);
    protected final String TAG;
    private boolean mAnnounceNextStateChange;
    private final ArrayList<Callback> mCallbacks = new ArrayList<>();
    protected final Context mContext;
    /* access modifiers changed from: private */
    public EnforcedAdmin mEnforcedAdmin;
    /* access modifiers changed from: protected */
    public C1016H mHandler = new C1016H<>((Looper) Dependency.get(Dependency.BG_LOOPER));
    protected final QSHost mHost;
    private int mIsFullQs;
    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);
    private final ArraySet<Object> mListeners = new ArraySet<>();
    private final MetricsLogger mMetricsLogger = ((MetricsLogger) Dependency.get(MetricsLogger.class));
    private final QSLogger mQSLogger;
    protected final QSSettingsPanel mQSSettingsPanelOption;
    private boolean mShowingDetail;
    private final Object mStaleListener = new Object();
    protected TState mState;
    private final StatusBarStateController mStatusBarStateController = ((StatusBarStateController) Dependency.get(StatusBarStateController.class));
    private String mTileSpec;
    private TState mTmpState;
    protected final Handler mUiHandler = new Handler(Looper.getMainLooper());

    /* renamed from: com.android.systemui.qs.tileimpl.QSTileImpl$DrawableIcon */
    public static class DrawableIcon extends Icon {
        protected final Drawable mDrawable;
        protected final Drawable mInvisibleDrawable;

        public String toString() {
            return "DrawableIcon";
        }

        public DrawableIcon(Drawable drawable) {
            this.mDrawable = drawable;
            this.mInvisibleDrawable = drawable.getConstantState().newDrawable();
        }

        public Drawable getDrawable(Context context) {
            return this.mDrawable;
        }

        public Drawable getInvisibleDrawable(Context context) {
            return this.mInvisibleDrawable;
        }
    }

    /* renamed from: com.android.systemui.qs.tileimpl.QSTileImpl$H */
    protected final class C1016H extends Handler {
        @VisibleForTesting
        protected C1016H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            try {
                boolean z = true;
                if (message.what == 1) {
                    String str = "handleAddCallback";
                    QSTileImpl.this.handleAddCallback((Callback) message.obj);
                } else if (message.what == 11) {
                    String str2 = "handleRemoveCallbacks";
                    QSTileImpl.this.handleRemoveCallbacks();
                } else if (message.what == 12) {
                    String str3 = "handleRemoveCallback";
                    QSTileImpl.this.handleRemoveCallback((Callback) message.obj);
                } else if (message.what == 2) {
                    String str4 = "handleClick";
                    if (QSTileImpl.this.mState.disabledByPolicy) {
                        ((ActivityStarter) Dependency.get(ActivityStarter.class)).postStartActivityDismissingKeyguard(RestrictedLockUtils.getShowAdminSupportDetailsIntent(QSTileImpl.this.mContext, QSTileImpl.this.mEnforcedAdmin), 0);
                        return;
                    }
                    QSTileImpl.this.handleClick();
                } else if (message.what == 3) {
                    String str5 = "handleSecondaryClick";
                    QSTileImpl.this.handleSecondaryClick();
                } else if (message.what == 4) {
                    String str6 = "handleLongClick";
                    QSTileImpl.this.handleLongClick();
                } else if (message.what == 5) {
                    String str7 = "handleRefreshState";
                    QSTileImpl.this.handleRefreshState(message.obj);
                } else if (message.what == 6) {
                    String str8 = "handleShowDetail";
                    QSTileImpl qSTileImpl = QSTileImpl.this;
                    if (message.arg1 == 0) {
                        z = false;
                    }
                    qSTileImpl.handleShowDetail(z);
                } else if (message.what == 7) {
                    String str9 = "handleUserSwitch";
                    QSTileImpl.this.handleUserSwitch(message.arg1);
                } else if (message.what == 8) {
                    String str10 = "handleToggleStateChanged";
                    QSTileImpl qSTileImpl2 = QSTileImpl.this;
                    if (message.arg1 == 0) {
                        z = false;
                    }
                    qSTileImpl2.handleToggleStateChanged(z);
                } else if (message.what == 9) {
                    String str11 = "handleScanStateChanged";
                    QSTileImpl qSTileImpl3 = QSTileImpl.this;
                    if (message.arg1 == 0) {
                        z = false;
                    }
                    qSTileImpl3.handleScanStateChanged(z);
                } else if (message.what == 10) {
                    String str12 = "handleDestroy";
                    QSTileImpl.this.handleDestroy();
                } else if (message.what == 13) {
                    String str13 = "handleSetListeningInternal";
                    QSTileImpl qSTileImpl4 = QSTileImpl.this;
                    Object obj = message.obj;
                    if (message.arg1 == 0) {
                        z = false;
                    }
                    qSTileImpl4.handleSetListeningInternal(obj, z);
                } else if (message.what == 14) {
                    String str14 = "handleStale";
                    QSTileImpl.this.handleStale();
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unknown msg: ");
                    sb.append(message.what);
                    throw new IllegalArgumentException(sb.toString());
                }
            } catch (Throwable th) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Error in ");
                sb2.append(null);
                String sb3 = sb2.toString();
                Log.w(QSTileImpl.this.TAG, sb3, th);
                QSTileImpl.this.mHost.warn(sb3, th);
            }
        }
    }

    /* renamed from: com.android.systemui.qs.tileimpl.QSTileImpl$ResourceIcon */
    public static class ResourceIcon extends Icon {
        private static final SparseArray<Icon> ICONS = new SparseArray<>();
        protected final int mResId;

        private ResourceIcon(int i) {
            this.mResId = i;
        }

        public static synchronized Icon get(int i) {
            Icon icon;
            synchronized (ResourceIcon.class) {
                icon = (Icon) ICONS.get(i);
                if (icon == null) {
                    icon = new ResourceIcon(i);
                    ICONS.put(i, icon);
                }
            }
            return icon;
        }

        public Drawable getDrawable(Context context) {
            return context.getDrawable(this.mResId);
        }

        public Drawable getInvisibleDrawable(Context context) {
            return context.getDrawable(this.mResId);
        }

        public boolean equals(Object obj) {
            return (obj instanceof ResourceIcon) && ((ResourceIcon) obj).mResId == this.mResId;
        }

        public String toString() {
            return String.format("ResourceIcon[resId=0x%08x]", new Object[]{Integer.valueOf(this.mResId)});
        }
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        return null;
    }

    public DetailAdapter getDetailAdapter() {
        return null;
    }

    public abstract Intent getLongClickIntent();

    public abstract int getMetricsCategory();

    /* access modifiers changed from: protected */
    public long getStaleTimeout() {
        return 600000;
    }

    /* access modifiers changed from: protected */
    public abstract void handleClick();

    /* access modifiers changed from: protected */
    public abstract void handleUpdateState(TState tstate, Object obj);

    public boolean isAvailable() {
        return true;
    }

    public abstract TState newTileState();

    public void setDetailListening(boolean z) {
    }

    /* access modifiers changed from: protected */
    public boolean shouldAnnouncementBeDelayed() {
        return false;
    }

    protected QSTileImpl(QSHost qSHost) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tile.");
        sb.append(getClass().getSimpleName());
        this.TAG = sb.toString();
        this.mHost = qSHost;
        this.mContext = qSHost.getContext();
        this.mState = newTileState();
        this.mTmpState = newTileState();
        this.mQSSettingsPanelOption = QSSettingsControllerKt.getQSSettingsPanelOption();
        this.mQSLogger = qSHost.getQSLogger();
    }

    /* access modifiers changed from: protected */
    public final void resetStates() {
        this.mState = newTileState();
        this.mTmpState = newTileState();
    }

    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }

    public void setListening(Object obj, boolean z) {
        this.mHandler.obtainMessage(13, z ? 1 : 0, 0, obj).sendToTarget();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void handleStale() {
        setListening(this.mStaleListener, true);
    }

    public String getTileSpec() {
        return this.mTileSpec;
    }

    public void setTileSpec(String str) {
        this.mTileSpec = str;
    }

    public QSHost getHost() {
        return this.mHost;
    }

    public QSIconView createTileView(Context context) {
        return new QSIconViewImpl(context);
    }

    public void addCallback(Callback callback) {
        this.mHandler.obtainMessage(1, callback).sendToTarget();
    }

    public void removeCallback(Callback callback) {
        this.mHandler.obtainMessage(12, callback).sendToTarget();
    }

    public void removeCallbacks() {
        this.mHandler.sendEmptyMessage(11);
    }

    public void click() {
        this.mMetricsLogger.write(populate(new LogMaker(925).setType(4).addTaggedData(1592, Integer.valueOf(this.mStatusBarStateController.getState()))));
        this.mQSLogger.logTileClick(this.mTileSpec, this.mStatusBarStateController.getState(), this.mState.state);
        this.mHandler.sendEmptyMessage(2);
    }

    public void secondaryClick() {
        this.mMetricsLogger.write(populate(new LogMaker(926).setType(4).addTaggedData(1592, Integer.valueOf(this.mStatusBarStateController.getState()))));
        this.mQSLogger.logTileSecondaryClick(this.mTileSpec, this.mStatusBarStateController.getState(), this.mState.state);
        this.mHandler.sendEmptyMessage(3);
    }

    public void longClick() {
        this.mMetricsLogger.write(populate(new LogMaker(366).setType(4).addTaggedData(1592, Integer.valueOf(this.mStatusBarStateController.getState()))));
        this.mQSLogger.logTileLongClick(this.mTileSpec, this.mStatusBarStateController.getState(), this.mState.state);
        this.mHandler.sendEmptyMessage(4);
        Prefs.putInt(this.mContext, "QsLongPressTooltipShownCount", 2);
    }

    public LogMaker populate(LogMaker logMaker) {
        TState tstate = this.mState;
        if (tstate instanceof BooleanState) {
            logMaker.addTaggedData(928, Integer.valueOf(((BooleanState) tstate).value ? 1 : 0));
        }
        return logMaker.setSubtype(getMetricsCategory()).addTaggedData(1593, Integer.valueOf(this.mIsFullQs)).addTaggedData(927, Integer.valueOf(this.mHost.indexOf(this.mTileSpec)));
    }

    public void showDetail(boolean z) {
        this.mHandler.obtainMessage(6, z ? 1 : 0, 0).sendToTarget();
    }

    public void refreshState() {
        refreshState(null);
    }

    /* access modifiers changed from: protected */
    public final void refreshState(Object obj) {
        this.mHandler.obtainMessage(5, obj).sendToTarget();
    }

    public void userSwitch(int i) {
        this.mHandler.obtainMessage(7, i, 0).sendToTarget();
    }

    public void fireToggleStateChanged(boolean z) {
        this.mHandler.obtainMessage(8, z ? 1 : 0, 0).sendToTarget();
    }

    public void fireScanStateChanged(boolean z) {
        this.mHandler.obtainMessage(9, z ? 1 : 0, 0).sendToTarget();
    }

    public void destroy() {
        this.mHandler.sendEmptyMessage(10);
    }

    public TState getState() {
        return this.mState;
    }

    /* access modifiers changed from: private */
    public void handleAddCallback(Callback callback) {
        this.mCallbacks.add(callback);
        callback.onStateChanged(this.mState);
    }

    /* access modifiers changed from: private */
    public void handleRemoveCallback(Callback callback) {
        this.mCallbacks.remove(callback);
    }

    /* access modifiers changed from: private */
    public void handleRemoveCallbacks() {
        this.mCallbacks.clear();
    }

    /* access modifiers changed from: protected */
    public void handleSecondaryClick() {
        handleClick();
    }

    /* access modifiers changed from: protected */
    public void handleLongClick() {
        if (this.mQSSettingsPanelOption == QSSettingsPanel.USE_DETAIL) {
            showDetail(true);
        } else {
            ((ActivityStarter) Dependency.get(ActivityStarter.class)).postStartActivityDismissingKeyguard(getLongClickIntent(), 0);
        }
    }

    /* access modifiers changed from: protected */
    public void handleRefreshState(Object obj) {
        handleUpdateState(this.mTmpState, obj);
        if (this.mTmpState.copyTo(this.mState)) {
            this.mQSLogger.logTileUpdated(this.mTileSpec, this.mState);
            handleStateChanged();
        }
        this.mHandler.removeMessages(14);
        this.mHandler.sendEmptyMessageDelayed(14, getStaleTimeout());
        setListening(this.mStaleListener, false);
    }

    private void handleStateChanged() {
        boolean shouldAnnouncementBeDelayed = shouldAnnouncementBeDelayed();
        boolean z = false;
        if (this.mCallbacks.size() != 0) {
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                ((Callback) this.mCallbacks.get(i)).onStateChanged(this.mState);
            }
            if (this.mAnnounceNextStateChange && !shouldAnnouncementBeDelayed) {
                String composeChangeAnnouncement = composeChangeAnnouncement();
                if (composeChangeAnnouncement != null) {
                    ((Callback) this.mCallbacks.get(0)).onAnnouncementRequested(composeChangeAnnouncement);
                }
            }
        }
        if (this.mAnnounceNextStateChange && shouldAnnouncementBeDelayed) {
            z = true;
        }
        this.mAnnounceNextStateChange = z;
    }

    /* access modifiers changed from: private */
    public void handleShowDetail(boolean z) {
        this.mShowingDetail = z;
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            ((Callback) this.mCallbacks.get(i)).onShowDetail(z);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isShowingDetail() {
        return this.mShowingDetail;
    }

    /* access modifiers changed from: private */
    public void handleToggleStateChanged(boolean z) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            ((Callback) this.mCallbacks.get(i)).onToggleStateChanged(z);
        }
    }

    /* access modifiers changed from: private */
    public void handleScanStateChanged(boolean z) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            ((Callback) this.mCallbacks.get(i)).onScanStateChanged(z);
        }
    }

    /* access modifiers changed from: protected */
    public void handleUserSwitch(int i) {
        handleRefreshState(null);
    }

    /* access modifiers changed from: private */
    public void handleSetListeningInternal(Object obj, boolean z) {
        if (z) {
            if (this.mListeners.add(obj) && this.mListeners.size() == 1) {
                if (DEBUG) {
                    Log.d(this.TAG, "handleSetListening true");
                }
                this.mLifecycle.markState(Lifecycle.State.RESUMED);
                handleSetListening(z);
                refreshState();
            }
        } else if (this.mListeners.remove(obj) && this.mListeners.size() == 0) {
            if (DEBUG) {
                Log.d(this.TAG, "handleSetListening false");
            }
            this.mLifecycle.markState(Lifecycle.State.DESTROYED);
            handleSetListening(z);
        }
        updateIsFullQs();
    }

    private void updateIsFullQs() {
        Iterator it = this.mListeners.iterator();
        while (it.hasNext()) {
            if (TilePage.class.equals(it.next().getClass())) {
                this.mIsFullQs = 1;
                return;
            }
        }
        this.mIsFullQs = 0;
    }

    /* access modifiers changed from: protected */
    public void handleSetListening(boolean z) {
        String str = this.mTileSpec;
        if (str != null) {
            this.mQSLogger.logTileChangeListening(str, z);
        }
    }

    /* access modifiers changed from: protected */
    public void handleDestroy() {
        this.mQSLogger.logTileDestroyed(this.mTileSpec, "Handle destroy");
        if (this.mListeners.size() != 0) {
            handleSetListening(false);
        }
        this.mCallbacks.clear();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    /* access modifiers changed from: protected */
    public void checkIfRestrictionEnforcedByAdminOnly(State state, String str) {
        EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, str, ActivityManager.getCurrentUser());
        if (checkIfRestrictionEnforced == null || RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, str, ActivityManager.getCurrentUser())) {
            state.disabledByPolicy = false;
            this.mEnforcedAdmin = null;
            return;
        }
        state.disabledByPolicy = true;
        this.mEnforcedAdmin = checkIfRestrictionEnforced;
    }

    public static int getColorForState(Context context, int i) {
        if (i == 0) {
            return Utils.getDisabled(context, Utils.getColorAttrDefaultColor(context, 16842808));
        }
        if (i == 1) {
            return Utils.getColorAttrDefaultColor(context, 16842808);
        }
        if (i == 2) {
            return Utils.getColorAttrDefaultColor(context, 16843827);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid state ");
        sb.append(i);
        Log.e("QSTile", sb.toString());
        return 0;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(":");
        printWriter.println(sb.toString());
        printWriter.print("    ");
        printWriter.println(getState().toString());
    }
}
