package com.android.systemui.p007qs;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession.Token;
import android.metrics.LogMaker;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.media.InfoMediaManager;
import com.android.settingslib.media.LocalMediaManager;
import com.android.settingslib.media.LocalMediaManager.DeviceCallback;
import com.android.settingslib.media.MediaDevice;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.p007qs.PagedTileLayout.PageListener;
import com.android.systemui.p007qs.QSHost.Callback;
import com.android.systemui.p007qs.customize.QSCustomizer;
import com.android.systemui.p007qs.external.CustomTile;
import com.android.systemui.p007qs.logging.QSLogger;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.p006qs.DetailAdapter;
import com.android.systemui.plugins.p006qs.QSTile;
import com.android.systemui.plugins.p006qs.QSTile.State;
import com.android.systemui.plugins.p006qs.QSTileView;
import com.android.systemui.settings.BrightnessController;
import com.android.systemui.settings.ToggleSlider;
import com.android.systemui.settings.ToggleSliderView;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;
import com.android.systemui.statusbar.policy.BrightnessMirrorController.BrightnessMirrorListener;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/* renamed from: com.android.systemui.qs.QSPanel */
public class QSPanel extends LinearLayout implements Tunable, Callback, BrightnessMirrorListener, Dumpable {
    private final Executor mBackgroundExecutor;
    private BrightnessController mBrightnessController;
    private BrightnessMirrorController mBrightnessMirrorController;
    protected final View mBrightnessView;
    private String mCachedSpecs = "";
    private QSDetail.Callback mCallback;
    protected final Context mContext;
    /* access modifiers changed from: private */
    public QSCustomizer mCustomizePanel;
    /* access modifiers changed from: private */
    public Record mDetailRecord;
    /* access modifiers changed from: private */
    public MediaDevice mDevice;
    private final DeviceCallback mDeviceCallback = new DeviceCallback() {
        public void onDeviceListUpdate(List<MediaDevice> list) {
            MediaDevice currentConnectedDevice = QSPanel.this.mLocalMediaManager.getCurrentConnectedDevice();
            if (QSPanel.this.mDevice == null || !QSPanel.this.mDevice.equals(currentConnectedDevice)) {
                QSPanel.this.mDevice = currentConnectedDevice;
                Iterator it = QSPanel.this.mMediaPlayers.iterator();
                while (it.hasNext()) {
                    ((QSMediaPlayer) it.next()).updateDevice(QSPanel.this.mDevice);
                }
            }
        }

        public void onSelectedDeviceStateChanged(MediaDevice mediaDevice, int i) {
            if (QSPanel.this.mDevice == null || !QSPanel.this.mDevice.equals(mediaDevice)) {
                QSPanel.this.mDevice = mediaDevice;
                Iterator it = QSPanel.this.mMediaPlayers.iterator();
                while (it.hasNext()) {
                    ((QSMediaPlayer) it.next()).updateDevice(QSPanel.this.mDevice);
                }
            }
        }
    };
    private View mDivider;
    private final DumpManager mDumpManager;
    protected boolean mExpanded;
    protected QSSecurityFooter mFooter;
    private PageIndicator mFooterPageIndicator;
    private final Executor mForegroundExecutor;
    private boolean mGridContentVisible = true;
    /* access modifiers changed from: private */
    public final C0980H mHandler = new C0980H();
    protected QSTileHost mHost;
    protected boolean mListening;
    private final LocalBluetoothManager mLocalBluetoothManager;
    /* access modifiers changed from: private */
    public LocalMediaManager mLocalMediaManager;
    private final LinearLayout mMediaCarousel;
    /* access modifiers changed from: private */
    public final ArrayList<QSMediaPlayer> mMediaPlayers = new ArrayList<>();
    private final MetricsLogger mMetricsLogger = ((MetricsLogger) Dependency.get(MetricsLogger.class));
    private final NotificationMediaManager mNotificationMediaManager;
    private final QSLogger mQSLogger;
    private final QSTileRevealController mQsTileRevealController;
    protected final ArrayList<TileRecord> mRecords = new ArrayList<>();
    protected QSTileLayout mTileLayout;
    private boolean mUpdateCarousel = false;

    /* renamed from: com.android.systemui.qs.QSPanel$H */
    private class C0980H extends Handler {
        private C0980H() {
        }

        public void handleMessage(Message message) {
            int i = message.what;
            boolean z = true;
            if (i == 1) {
                QSPanel qSPanel = QSPanel.this;
                Record record = (Record) message.obj;
                if (message.arg1 == 0) {
                    z = false;
                }
                qSPanel.handleShowDetail(record, z);
            } else if (i == 3) {
                QSPanel.this.announceForAccessibility((CharSequence) message.obj);
            }
        }
    }

    /* renamed from: com.android.systemui.qs.QSPanel$QSTileLayout */
    public interface QSTileLayout {
        void addTile(TileRecord tileRecord);

        int getNumVisibleTiles();

        int getOffsetTop(TileRecord tileRecord);

        void removeTile(TileRecord tileRecord);

        void restoreInstanceState(Bundle bundle) {
        }

        void saveInstanceState(Bundle bundle) {
        }

        void setExpansion(float f) {
        }

        void setListening(boolean z);

        boolean updateResources();
    }

    /* renamed from: com.android.systemui.qs.QSPanel$Record */
    protected static class Record {
        DetailAdapter detailAdapter;

        /* renamed from: x */
        int f65x;

        /* renamed from: y */
        int f66y;

        protected Record() {
        }
    }

    /* renamed from: com.android.systemui.qs.QSPanel$TileRecord */
    public static final class TileRecord extends Record {
        public QSTile.Callback callback;
        public boolean scanState;
        public QSTile tile;
        public QSTileView tileView;
    }

    /* access modifiers changed from: protected */
    public String getDumpableTag() {
        return "QSPanel";
    }

    public QSPanel(Context context, AttributeSet attributeSet, DumpManager dumpManager, BroadcastDispatcher broadcastDispatcher, QSLogger qSLogger, NotificationMediaManager notificationMediaManager, Executor executor, Executor executor2, LocalBluetoothManager localBluetoothManager) {
        super(context, attributeSet);
        this.mContext = context;
        this.mQSLogger = qSLogger;
        this.mDumpManager = dumpManager;
        this.mNotificationMediaManager = notificationMediaManager;
        this.mForegroundExecutor = executor;
        this.mBackgroundExecutor = executor2;
        this.mLocalBluetoothManager = localBluetoothManager;
        setOrientation(1);
        View inflate = LayoutInflater.from(this.mContext).inflate(C2013R$layout.quick_settings_brightness_dialog, this, false);
        this.mBrightnessView = inflate;
        addView(inflate);
        this.mTileLayout = (QSTileLayout) LayoutInflater.from(this.mContext).inflate(C2013R$layout.qs_paged_tile_layout, this, false);
        this.mQSLogger.logAllTilesChangeListening(this.mListening, getDumpableTag(), this.mCachedSpecs);
        this.mTileLayout.setListening(this.mListening);
        addView((View) this.mTileLayout);
        this.mQsTileRevealController = new QSTileRevealController(this.mContext, this, (PagedTileLayout) this.mTileLayout);
        addDivider();
        if (Utils.useQsMediaPlayer(context)) {
            HorizontalScrollView horizontalScrollView = (HorizontalScrollView) LayoutInflater.from(this.mContext).inflate(C2013R$layout.media_carousel, this, false);
            this.mMediaCarousel = (LinearLayout) horizontalScrollView.findViewById(C2011R$id.media_carousel);
            addView(horizontalScrollView, 0);
        } else {
            this.mMediaCarousel = null;
        }
        QSSecurityFooter qSSecurityFooter = new QSSecurityFooter(this, context);
        this.mFooter = qSSecurityFooter;
        addView(qSSecurityFooter.getView());
        updateResources();
        this.mBrightnessController = new BrightnessController(getContext(), (ToggleSlider) findViewById(C2011R$id.brightness_slider), broadcastDispatcher);
    }

    public void onVisibilityAggregated(boolean z) {
        super.onVisibilityAggregated(z);
        if (!z && this.mUpdateCarousel) {
            Iterator it = this.mMediaPlayers.iterator();
            while (it.hasNext()) {
                QSMediaPlayer qSMediaPlayer = (QSMediaPlayer) it.next();
                if (qSMediaPlayer.isPlaying()) {
                    LayoutParams layoutParams = (LayoutParams) qSMediaPlayer.getView().getLayoutParams();
                    this.mMediaCarousel.removeView(qSMediaPlayer.getView());
                    this.mMediaCarousel.addView(qSMediaPlayer.getView(), 0, layoutParams);
                    ((HorizontalScrollView) this.mMediaCarousel.getParent()).fullScroll(17);
                    this.mUpdateCarousel = false;
                    return;
                }
            }
        }
    }

    public void addMediaSession(Token token, Icon icon, int i, int i2, View view, StatusBarNotification statusBarNotification) {
        QSMediaPlayer qSMediaPlayer;
        QSMediaPlayer qSMediaPlayer2;
        Token token2 = token;
        String str = "QSPanel";
        if (!Utils.useQsMediaPlayer(this.mContext)) {
            Log.e(str, "Tried to add media session without player!");
        } else if (token2 == null) {
            Log.e(str, "Media session token was null!");
        } else {
            String packageName = statusBarNotification.getPackageName();
            Iterator it = this.mMediaPlayers.iterator();
            while (true) {
                if (!it.hasNext()) {
                    qSMediaPlayer = null;
                    break;
                }
                qSMediaPlayer = (QSMediaPlayer) it.next();
                if (!qSMediaPlayer.getMediaSessionToken().equals(token2)) {
                    if (packageName.equals(qSMediaPlayer.getMediaPlayerPackage())) {
                        Log.d(str, "found an old session for this app");
                        break;
                    }
                } else {
                    Log.d(str, "a player for this session already exists");
                    break;
                }
            }
            int dimension = (int) getResources().getDimension(C2009R$dimen.qs_media_padding);
            LayoutParams layoutParams = new LayoutParams((int) getResources().getDimension(C2009R$dimen.qs_media_width), -1);
            layoutParams.setMarginStart(dimension);
            layoutParams.setMarginEnd(dimension);
            if (qSMediaPlayer == null) {
                Log.d(str, "creating new player");
                qSMediaPlayer2 = new QSMediaPlayer(this.mContext, this, this.mNotificationMediaManager, this.mForegroundExecutor, this.mBackgroundExecutor);
                if (qSMediaPlayer2.isPlaying()) {
                    this.mMediaCarousel.addView(qSMediaPlayer2.getView(), 0, layoutParams);
                } else {
                    this.mMediaCarousel.addView(qSMediaPlayer2.getView(), layoutParams);
                }
                this.mMediaPlayers.add(qSMediaPlayer2);
            } else {
                if (qSMediaPlayer.isPlaying()) {
                    this.mUpdateCarousel = true;
                }
                qSMediaPlayer2 = qSMediaPlayer;
            }
            Log.d(str, "setting player session");
            qSMediaPlayer2.setMediaSession(token, icon, i, i2, view, statusBarNotification.getNotification(), this.mDevice);
            if (this.mMediaPlayers.size() > 0) {
                ((View) this.mMediaCarousel.getParent()).setVisibility(0);
                LocalMediaManager localMediaManager = new LocalMediaManager(this.mContext, this.mLocalBluetoothManager, new InfoMediaManager(this.mContext, null, null, this.mLocalBluetoothManager), null);
                this.mLocalMediaManager = localMediaManager;
                localMediaManager.startScan();
                this.mDevice = this.mLocalMediaManager.getCurrentConnectedDevice();
                this.mLocalMediaManager.registerCallback(this.mDeviceCallback);
            }
        }
    }

    /* access modifiers changed from: protected */
    public View getMediaPanel() {
        return this.mMediaCarousel;
    }

    /* access modifiers changed from: protected */
    public boolean removeMediaPlayer(QSMediaPlayer qSMediaPlayer) {
        if (!this.mMediaPlayers.remove(qSMediaPlayer)) {
            return false;
        }
        this.mMediaCarousel.removeView(qSMediaPlayer.getView());
        if (this.mMediaPlayers.size() == 0) {
            ((View) this.mMediaCarousel.getParent()).setVisibility(8);
            this.mLocalMediaManager.stopScan();
            this.mLocalMediaManager.unregisterCallback(this.mDeviceCallback);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void addDivider() {
        View inflate = LayoutInflater.from(this.mContext).inflate(C2013R$layout.qs_divider, this, false);
        this.mDivider = inflate;
        inflate.setBackgroundColor(com.android.settingslib.Utils.applyAlpha(inflate.getAlpha(), QSTileImpl.getColorForState(this.mContext, 2)));
        addView(this.mDivider);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int paddingBottom = getPaddingBottom() + getPaddingTop();
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != 8) {
                paddingBottom += childAt.getMeasuredHeight();
            }
        }
        setMeasuredDimension(getMeasuredWidth(), paddingBottom);
    }

    public View getDivider() {
        return this.mDivider;
    }

    public QSTileRevealController getQsTileRevealController() {
        return this.mQsTileRevealController;
    }

    public boolean isShowingCustomize() {
        QSCustomizer qSCustomizer = this.mCustomizePanel;
        return qSCustomizer != null && qSCustomizer.isCustomizing();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, "qs_show_brightness");
        QSTileHost qSTileHost = this.mHost;
        if (qSTileHost != null) {
            setTiles(qSTileHost.getTiles());
        }
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.addCallback((BrightnessMirrorListener) this);
        }
        this.mDumpManager.registerDumpable(getDumpableTag(), this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
        QSTileHost qSTileHost = this.mHost;
        if (qSTileHost != null) {
            qSTileHost.removeCallback(this);
        }
        Iterator it = this.mRecords.iterator();
        while (it.hasNext()) {
            ((TileRecord) it.next()).tile.removeCallbacks();
        }
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.removeCallback((BrightnessMirrorListener) this);
        }
        this.mDumpManager.unregisterDumpable(getDumpableTag());
        LocalMediaManager localMediaManager = this.mLocalMediaManager;
        if (localMediaManager != null) {
            localMediaManager.stopScan();
            this.mLocalMediaManager.unregisterCallback(this.mDeviceCallback);
        }
        super.onDetachedFromWindow();
    }

    public void onTilesChanged() {
        setTiles(this.mHost.getTiles());
    }

    public void onTuningChanged(String str, String str2) {
        if ("qs_show_brightness".equals(str)) {
            updateViewVisibilityForTuningValue(this.mBrightnessView, str2);
        }
    }

    private void updateViewVisibilityForTuningValue(View view, String str) {
        view.setVisibility(TunerService.parseIntegerSwitch(str, true) ? 0 : 8);
    }

    public void openDetails(String str) {
        QSTile tile = getTile(str);
        if (tile != null) {
            showDetailAdapter(true, tile.getDetailAdapter(), new int[]{getWidth() / 2, 0});
        }
    }

    private QSTile getTile(String str) {
        for (int i = 0; i < this.mRecords.size(); i++) {
            if (str.equals(((TileRecord) this.mRecords.get(i)).tile.getTileSpec())) {
                return ((TileRecord) this.mRecords.get(i)).tile;
            }
        }
        return this.mHost.createTile(str);
    }

    public void setBrightnessMirror(BrightnessMirrorController brightnessMirrorController) {
        BrightnessMirrorController brightnessMirrorController2 = this.mBrightnessMirrorController;
        if (brightnessMirrorController2 != null) {
            brightnessMirrorController2.removeCallback((BrightnessMirrorListener) this);
        }
        this.mBrightnessMirrorController = brightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.addCallback((BrightnessMirrorListener) this);
        }
        updateBrightnessMirror();
    }

    public void onBrightnessMirrorReinflated(View view) {
        updateBrightnessMirror();
    }

    /* access modifiers changed from: 0000 */
    public View getBrightnessView() {
        return this.mBrightnessView;
    }

    public void setCallback(QSDetail.Callback callback) {
        this.mCallback = callback;
    }

    public void setHost(QSTileHost qSTileHost, QSCustomizer qSCustomizer) {
        this.mHost = qSTileHost;
        qSTileHost.addCallback(this);
        setTiles(this.mHost.getTiles());
        this.mFooter.setHostEnvironment(qSTileHost);
        this.mCustomizePanel = qSCustomizer;
        if (qSCustomizer != null) {
            qSCustomizer.setHost(this.mHost);
        }
    }

    public void setFooterPageIndicator(PageIndicator pageIndicator) {
        if (this.mTileLayout instanceof PagedTileLayout) {
            this.mFooterPageIndicator = pageIndicator;
            updatePageIndicator();
        }
    }

    private void updatePageIndicator() {
        if (this.mTileLayout instanceof PagedTileLayout) {
            PageIndicator pageIndicator = this.mFooterPageIndicator;
            if (pageIndicator != null) {
                pageIndicator.setVisibility(8);
                ((PagedTileLayout) this.mTileLayout).setPageIndicator(this.mFooterPageIndicator);
            }
        }
    }

    public QSTileHost getHost() {
        return this.mHost;
    }

    public void updateResources() {
        Resources resources = this.mContext.getResources();
        setPadding(0, resources.getDimensionPixelSize(C2009R$dimen.qs_panel_padding_top), 0, resources.getDimensionPixelSize(C2009R$dimen.qs_panel_padding_bottom));
        updatePageIndicator();
        if (this.mListening) {
            refreshAllTiles();
        }
        QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout != null) {
            qSTileLayout.updateResources();
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mFooter.onConfigurationChanged();
        updateResources();
        updateBrightnessMirror();
    }

    public void updateBrightnessMirror() {
        if (this.mBrightnessMirrorController != null) {
            ToggleSliderView toggleSliderView = (ToggleSliderView) findViewById(C2011R$id.brightness_slider);
            toggleSliderView.setMirror((ToggleSliderView) this.mBrightnessMirrorController.getMirror().findViewById(C2011R$id.brightness_slider));
            toggleSliderView.setMirrorController(this.mBrightnessMirrorController);
        }
    }

    public void setExpanded(boolean z) {
        if (this.mExpanded != z) {
            this.mQSLogger.logPanelExpanded(z, getDumpableTag());
            this.mExpanded = z;
            if (!z) {
                QSTileLayout qSTileLayout = this.mTileLayout;
                if (qSTileLayout instanceof PagedTileLayout) {
                    ((PagedTileLayout) qSTileLayout).setCurrentItem(0, false);
                }
            }
            this.mMetricsLogger.visibility(111, this.mExpanded);
            if (!this.mExpanded) {
                closeDetail();
            } else {
                logTiles();
            }
        }
    }

    public void setPageListener(PageListener pageListener) {
        QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout instanceof PagedTileLayout) {
            ((PagedTileLayout) qSTileLayout).setPageListener(pageListener);
        }
    }

    public boolean isExpanded() {
        return this.mExpanded;
    }

    public void setListening(boolean z) {
        if (this.mListening != z) {
            this.mListening = z;
            if (this.mTileLayout != null) {
                this.mQSLogger.logAllTilesChangeListening(z, getDumpableTag(), this.mCachedSpecs);
                this.mTileLayout.setListening(z);
            }
            if (this.mListening) {
                refreshAllTiles();
            }
        }
    }

    private String getTilesSpecs() {
        return (String) this.mRecords.stream().map($$Lambda$QSPanel$BhHZgebPgkrbRfUrB_Ik6CkLFO8.INSTANCE).collect(Collectors.joining(","));
    }

    public void setListening(boolean z, boolean z2) {
        setListening(z && z2);
        getFooter().setListening(z);
        setBrightnessListening(z);
    }

    public void setBrightnessListening(boolean z) {
        if (z) {
            this.mBrightnessController.registerCallbacks();
        } else {
            this.mBrightnessController.unregisterCallbacks();
        }
    }

    public void refreshAllTiles() {
        this.mBrightnessController.checkRestrictionAndSetEnabled();
        Iterator it = this.mRecords.iterator();
        while (it.hasNext()) {
            ((TileRecord) it.next()).tile.refreshState();
        }
        this.mFooter.refreshState();
    }

    public void showDetailAdapter(boolean z, DetailAdapter detailAdapter, int[] iArr) {
        int i = iArr[0];
        int i2 = iArr[1];
        ((View) getParent()).getLocationInWindow(iArr);
        Record record = new Record();
        record.detailAdapter = detailAdapter;
        record.f65x = i - iArr[0];
        record.f66y = i2 - iArr[1];
        iArr[0] = i;
        iArr[1] = i2;
        showDetail(z, record);
    }

    /* access modifiers changed from: protected */
    public void showDetail(boolean z, Record record) {
        this.mHandler.obtainMessage(1, z ? 1 : 0, 0, record).sendToTarget();
    }

    public void setTiles(Collection<QSTile> collection) {
        setTiles(collection, false);
    }

    public void setTiles(Collection<QSTile> collection, boolean z) {
        if (!z) {
            this.mQsTileRevealController.updateRevealedTiles(collection);
        }
        Iterator it = this.mRecords.iterator();
        while (it.hasNext()) {
            TileRecord tileRecord = (TileRecord) it.next();
            this.mTileLayout.removeTile(tileRecord);
            tileRecord.tile.removeCallback(tileRecord.callback);
        }
        this.mRecords.clear();
        this.mCachedSpecs = "";
        for (QSTile addTile : collection) {
            addTile(addTile, z);
        }
    }

    /* access modifiers changed from: protected */
    public void drawTile(TileRecord tileRecord, State state) {
        tileRecord.tileView.onStateChanged(state);
    }

    /* access modifiers changed from: protected */
    public QSTileView createTileView(QSTile qSTile, boolean z) {
        return this.mHost.createTileView(qSTile, z);
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowDetail() {
        return this.mExpanded;
    }

    /* access modifiers changed from: protected */
    public TileRecord addTile(QSTile qSTile, boolean z) {
        final TileRecord tileRecord = new TileRecord();
        tileRecord.tile = qSTile;
        tileRecord.tileView = createTileView(qSTile, z);
        C09782 r2 = new QSTile.Callback() {
            public void onStateChanged(State state) {
                QSPanel.this.drawTile(tileRecord, state);
            }

            public void onShowDetail(boolean z) {
                if (QSPanel.this.shouldShowDetail()) {
                    QSPanel.this.showDetail(z, tileRecord);
                }
            }

            public void onToggleStateChanged(boolean z) {
                if (QSPanel.this.mDetailRecord == tileRecord) {
                    QSPanel.this.fireToggleStateChanged(z);
                }
            }

            public void onScanStateChanged(boolean z) {
                tileRecord.scanState = z;
                Record access$400 = QSPanel.this.mDetailRecord;
                TileRecord tileRecord = tileRecord;
                if (access$400 == tileRecord) {
                    QSPanel.this.fireScanStateChanged(tileRecord.scanState);
                }
            }

            public void onAnnouncementRequested(CharSequence charSequence) {
                if (charSequence != null) {
                    QSPanel.this.mHandler.obtainMessage(3, charSequence).sendToTarget();
                }
            }
        };
        tileRecord.tile.addCallback(r2);
        tileRecord.callback = r2;
        tileRecord.tileView.init(tileRecord.tile);
        tileRecord.tile.refreshState();
        this.mRecords.add(tileRecord);
        this.mCachedSpecs = getTilesSpecs();
        QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout != null) {
            qSTileLayout.addTile(tileRecord);
        }
        return tileRecord;
    }

    public void showEdit(final View view) {
        view.post(new Runnable() {
            public void run() {
                if (QSPanel.this.mCustomizePanel != null && !QSPanel.this.mCustomizePanel.isCustomizing()) {
                    int[] locationOnScreen = view.getLocationOnScreen();
                    QSPanel.this.mCustomizePanel.show(locationOnScreen[0] + (view.getWidth() / 2), locationOnScreen[1] + (view.getHeight() / 2));
                }
            }
        });
    }

    public void closeDetail() {
        QSCustomizer qSCustomizer = this.mCustomizePanel;
        if (qSCustomizer == null || !qSCustomizer.isShown()) {
            showDetail(false, this.mDetailRecord);
        } else {
            this.mCustomizePanel.hide();
        }
    }

    /* access modifiers changed from: protected */
    public void handleShowDetail(Record record, boolean z) {
        int i;
        if (record instanceof TileRecord) {
            handleShowDetailTile((TileRecord) record, z);
            return;
        }
        int i2 = 0;
        if (record != null) {
            i2 = record.f65x;
            i = record.f66y;
        } else {
            i = 0;
        }
        handleShowDetailImpl(record, z, i2, i);
    }

    private void handleShowDetailTile(TileRecord tileRecord, boolean z) {
        if ((this.mDetailRecord != null) != z || this.mDetailRecord != tileRecord) {
            if (z) {
                DetailAdapter detailAdapter = tileRecord.tile.getDetailAdapter();
                tileRecord.detailAdapter = detailAdapter;
                if (detailAdapter == null) {
                    return;
                }
            }
            tileRecord.tile.setDetailListening(z);
            handleShowDetailImpl(tileRecord, z, tileRecord.tileView.getLeft() + (tileRecord.tileView.getWidth() / 2), tileRecord.tileView.getDetailY() + this.mTileLayout.getOffsetTop(tileRecord) + getTop());
        }
    }

    private void handleShowDetailImpl(Record record, boolean z, int i, int i2) {
        DetailAdapter detailAdapter = null;
        setDetailRecord(z ? record : null);
        if (z) {
            detailAdapter = record.detailAdapter;
        }
        fireShowingDetail(detailAdapter, i, i2);
    }

    /* access modifiers changed from: protected */
    public void setDetailRecord(Record record) {
        if (record != this.mDetailRecord) {
            this.mDetailRecord = record;
            fireScanStateChanged((record instanceof TileRecord) && ((TileRecord) record).scanState);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setGridContentVisibility(boolean z) {
        int i = z ? 0 : 4;
        setVisibility(i);
        if (this.mGridContentVisible != z) {
            this.mMetricsLogger.visibility(111, i);
        }
        this.mGridContentVisible = z;
    }

    private void logTiles() {
        for (int i = 0; i < this.mRecords.size(); i++) {
            QSTile qSTile = ((TileRecord) this.mRecords.get(i)).tile;
            this.mMetricsLogger.write(qSTile.populate(new LogMaker(qSTile.getMetricsCategory()).setType(1)));
        }
    }

    private void fireShowingDetail(DetailAdapter detailAdapter, int i, int i2) {
        QSDetail.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onShowingDetail(detailAdapter, i, i2);
        }
    }

    /* access modifiers changed from: private */
    public void fireToggleStateChanged(boolean z) {
        QSDetail.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onToggleStateChanged(z);
        }
    }

    /* access modifiers changed from: private */
    public void fireScanStateChanged(boolean z) {
        QSDetail.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onScanStateChanged(z);
        }
    }

    public void clickTile(ComponentName componentName) {
        String spec = CustomTile.toSpec(componentName);
        int size = this.mRecords.size();
        for (int i = 0; i < size; i++) {
            if (((TileRecord) this.mRecords.get(i)).tile.getTileSpec().equals(spec)) {
                ((TileRecord) this.mRecords.get(i)).tile.click();
                return;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public QSTileLayout getTileLayout() {
        return this.mTileLayout;
    }

    /* access modifiers changed from: 0000 */
    public QSTileView getTileView(QSTile qSTile) {
        Iterator it = this.mRecords.iterator();
        while (it.hasNext()) {
            TileRecord tileRecord = (TileRecord) it.next();
            if (tileRecord.tile == qSTile) {
                return tileRecord.tileView;
            }
        }
        return null;
    }

    public QSSecurityFooter getFooter() {
        return this.mFooter;
    }

    public void showDeviceMonitoringDialog() {
        this.mFooter.showDeviceMonitoringDialog();
    }

    public void setMargins(int i) {
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if (childAt != this.mTileLayout) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                layoutParams.leftMargin = i;
                layoutParams.rightMargin = i;
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(":");
        printWriter.println(sb.toString());
        printWriter.println("  Tile records:");
        Iterator it = this.mRecords.iterator();
        while (it.hasNext()) {
            TileRecord tileRecord = (TileRecord) it.next();
            if (tileRecord.tile instanceof Dumpable) {
                String str = "    ";
                printWriter.print(str);
                ((Dumpable) tileRecord.tile).dump(fileDescriptor, printWriter, strArr);
                printWriter.print(str);
                printWriter.println(tileRecord.tileView.toString());
            }
        }
    }
}
