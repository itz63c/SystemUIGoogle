package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.p007qs.AutoAddTracker;
import com.android.systemui.p007qs.QSTileHost;
import com.android.systemui.p007qs.SecureSetting;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.CastController.Callback;
import com.android.systemui.statusbar.policy.CastController.CastDevice;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DataSaverController.Listener;
import com.android.systemui.statusbar.policy.HotspotController;
import java.util.Iterator;

public class AutoTileManager {
    /* access modifiers changed from: private */
    public final AutoAddTracker mAutoTracker;
    @VisibleForTesting
    final Callback mCastCallback = new Callback() {
        public void onCastDevicesChanged() {
            String str = "cast";
            if (!AutoTileManager.this.mAutoTracker.isAdded(str)) {
                boolean z = false;
                Iterator it = AutoTileManager.this.mCastController.getCastDevices().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    int i = ((CastDevice) it.next()).state;
                    if (i != 2) {
                        if (i == 1) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                z = true;
                if (z) {
                    AutoTileManager.this.mHost.addTile(str);
                    AutoTileManager.this.mAutoTracker.setTileAdded(str);
                    AutoTileManager.this.mHandler.post(new Runnable() {
                        public final void run() {
                            C14416.this.lambda$onCastDevicesChanged$0$AutoTileManager$6();
                        }
                    });
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCastDevicesChanged$0 */
        public /* synthetic */ void lambda$onCastDevicesChanged$0$AutoTileManager$6() {
            AutoTileManager.this.mCastController.removeCallback(AutoTileManager.this.mCastCallback);
        }
    };
    /* access modifiers changed from: private */
    public final CastController mCastController;
    /* access modifiers changed from: private */
    public SecureSetting mColorsSetting;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final DataSaverController mDataSaverController;
    /* access modifiers changed from: private */
    public final Listener mDataSaverListener = new Listener() {
        public void onDataSaverChanged(boolean z) {
            String str = "saver";
            if (!AutoTileManager.this.mAutoTracker.isAdded(str) && z) {
                AutoTileManager.this.mHost.addTile(str);
                AutoTileManager.this.mAutoTracker.setTileAdded(str);
                AutoTileManager.this.mHandler.post(new Runnable() {
                    public final void run() {
                        C14383.this.lambda$onDataSaverChanged$0$AutoTileManager$3();
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onDataSaverChanged$0 */
        public /* synthetic */ void lambda$onDataSaverChanged$0$AutoTileManager$3() {
            AutoTileManager.this.mDataSaverController.removeCallback(AutoTileManager.this.mDataSaverListener);
        }
    };
    /* access modifiers changed from: private */
    public final Handler mHandler;
    /* access modifiers changed from: private */
    public final QSTileHost mHost;
    /* access modifiers changed from: private */
    public final HotspotController.Callback mHotspotCallback = new HotspotController.Callback() {
        public void onHotspotChanged(boolean z, int i) {
            String str = "hotspot";
            if (!AutoTileManager.this.mAutoTracker.isAdded(str) && z) {
                AutoTileManager.this.mHost.addTile(str);
                AutoTileManager.this.mAutoTracker.setTileAdded(str);
                AutoTileManager.this.mHandler.post(new Runnable() {
                    public final void run() {
                        C14394.this.lambda$onHotspotChanged$0$AutoTileManager$4();
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onHotspotChanged$0 */
        public /* synthetic */ void lambda$onHotspotChanged$0$AutoTileManager$4() {
            AutoTileManager.this.mHotspotController.removeCallback(AutoTileManager.this.mHotspotCallback);
        }
    };
    /* access modifiers changed from: private */
    public final HotspotController mHotspotController;
    /* access modifiers changed from: private */
    public final ManagedProfileController mManagedProfileController;
    @VisibleForTesting
    final NightDisplayListener.Callback mNightDisplayCallback = new NightDisplayListener.Callback() {
        public void onActivated(boolean z) {
            if (z) {
                addNightTile();
            }
        }

        public void onAutoModeChanged(int i) {
            if (i == 1 || i == 2) {
                addNightTile();
            }
        }

        private void addNightTile() {
            String str = "night";
            if (!AutoTileManager.this.mAutoTracker.isAdded(str)) {
                AutoTileManager.this.mHost.addTile(str);
                AutoTileManager.this.mAutoTracker.setTileAdded(str);
                AutoTileManager.this.mHandler.post(new Runnable() {
                    public final void run() {
                        C14405.this.lambda$addNightTile$0$AutoTileManager$5();
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$addNightTile$0 */
        public /* synthetic */ void lambda$addNightTile$0$AutoTileManager$5() {
            AutoTileManager.this.mNightDisplayListener.setCallback(null);
        }
    };
    /* access modifiers changed from: private */
    public final NightDisplayListener mNightDisplayListener;
    private final ManagedProfileController.Callback mProfileCallback = new ManagedProfileController.Callback() {
        public void onManagedProfileRemoved() {
        }

        public void onManagedProfileChanged() {
            String str = "work";
            if (!AutoTileManager.this.mAutoTracker.isAdded(str) && AutoTileManager.this.mManagedProfileController.hasActiveProfile()) {
                AutoTileManager.this.mHost.addTile(str);
                AutoTileManager.this.mAutoTracker.setTileAdded(str);
            }
        }
    };

    public AutoTileManager(Context context, AutoAddTracker autoAddTracker, QSTileHost qSTileHost, Handler handler, HotspotController hotspotController, DataSaverController dataSaverController, ManagedProfileController managedProfileController, NightDisplayListener nightDisplayListener, CastController castController) {
        this.mAutoTracker = autoAddTracker;
        this.mContext = context;
        this.mHost = qSTileHost;
        this.mHandler = handler;
        this.mHotspotController = hotspotController;
        this.mDataSaverController = dataSaverController;
        this.mManagedProfileController = managedProfileController;
        this.mNightDisplayListener = nightDisplayListener;
        this.mCastController = castController;
        if (!autoAddTracker.isAdded("hotspot")) {
            hotspotController.addCallback(this.mHotspotCallback);
        }
        if (!this.mAutoTracker.isAdded("saver")) {
            dataSaverController.addCallback(this.mDataSaverListener);
        }
        if (!this.mAutoTracker.isAdded("inversion")) {
            C14361 r2 = new SecureSetting(this.mContext, this.mHandler, "accessibility_display_inversion_enabled") {
                /* access modifiers changed from: protected */
                public void handleValueChanged(int i, boolean z) {
                    String str = "inversion";
                    if (!AutoTileManager.this.mAutoTracker.isAdded(str) && i != 0) {
                        AutoTileManager.this.mHost.addTile(str);
                        AutoTileManager.this.mAutoTracker.setTileAdded(str);
                        AutoTileManager.this.mHandler.post(new Runnable() {
                            public final void run() {
                                C14361.this.lambda$handleValueChanged$0$AutoTileManager$1();
                            }
                        });
                    }
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$handleValueChanged$0 */
                public /* synthetic */ void lambda$handleValueChanged$0$AutoTileManager$1() {
                    AutoTileManager.this.mColorsSetting.setListening(false);
                }
            };
            this.mColorsSetting = r2;
            r2.setListening(true);
        }
        if (!this.mAutoTracker.isAdded("work")) {
            managedProfileController.addCallback(this.mProfileCallback);
        }
        if (!this.mAutoTracker.isAdded("night") && ColorDisplayManager.isNightDisplayAvailable(this.mContext)) {
            nightDisplayListener.setCallback(this.mNightDisplayCallback);
        }
        if (!this.mAutoTracker.isAdded("cast")) {
            castController.addCallback(this.mCastCallback);
        }
    }

    public void unmarkTileAsAutoAdded(String str) {
        this.mAutoTracker.setTileRemoved(str);
    }
}
